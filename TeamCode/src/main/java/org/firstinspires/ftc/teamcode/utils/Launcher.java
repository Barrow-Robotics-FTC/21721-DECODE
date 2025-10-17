package org.firstinspires.ftc.teamcode.utils;

import static android.os.SystemClock.sleep;
import static com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior.BRAKE;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

public class Launcher {
    // Launcher constants
    int TARGET_RPM = 1500; // Target RPM for chip motor
    final int RPM_TOLERANCE = 50; // Tolerance of RPM required for launch
    final int RPM_IN_RANGE_TIME = 250; // How long the launcher must be within the target RPM tolerance to launch (milliseconds)
    final int MIN_TIME_BETWEEN_LAUNCHES = 500; // Minimum time between launches (milliseconds)
    final double feedPower = .5; //power to send to the feeding servos when launching
    final int FEED_TIME = 250; // Time to wait for the tapper to reach the pushed position (milliseconds)



    // Motors and servos
    private DcMotorEx chipMotor; // flywheel motor
    private CRServo lServo; // Left servo (looking into the feeder)
    private CRServo rServo; // Right servo (looking into the feeder)
    public double lServoPower;
    public double rServoPower;



    // Other variables
    private State state = State.IDLE;
    private int launches;
    private boolean feeding = false; // Whether the tapper has been commanded to the push position






    // timers
    private final ElapsedTime feedTimer = new ElapsedTime(); //timer for feeding into shooter
    private final ElapsedTime inToleranceTimer = new ElapsedTime();
    private final ElapsedTime timeSinceLastLaunch = new ElapsedTime();

    private final double motorTicksPerRev; // How many encoder ticks per revolution the motors have


    private boolean ballFed = false; // have the servos pushed the ball in

    public Launcher(HardwareMap hardwareMap) {
        // initialize hardware (drivetrain is initialized by Pedro Pathing)
        chipMotor = hardwareMap.get(DcMotorEx.class, "launcher_left");
        lServo = hardwareMap.get(CRServo.class, "lServo");
        rServo = hardwareMap.get(CRServo.class, "rServo");
        rServoPower = rServo.getPower();
        lServoPower = lServo.getPower();


        // Set ticks per revolution variable
        motorTicksPerRev = chipMotor.getMotorType().getTicksPerRev();

        // Set launcher motor characteristics with a list and a for loop to reduce redundant code
        java.util.List<DcMotorEx> motors = java.util.Arrays.asList(chipMotor);
        for (DcMotorEx motor : motors) {
            motor.setZeroPowerBehavior(BRAKE);
            motor.setMode(com.qualcomm.robotcore.hardware.DcMotorEx.RunMode.RUN_USING_ENCODER);
            motor.setPIDFCoefficients(DcMotorEx.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(300,0,0,10));
        }
        chipMotor.setDirection(DcMotorEx.Direction.REVERSE);

        // Make sure tapper is in the starting position
        lServo.setPower(0);
        rServo.setPower(0);

    }



    public enum State {
        IDLE,
        SPEED_UP,
        LAUNCH
    }

    public void init(HardwareMap hardwareMap) {
        // initialize hardware (drivetrain is initialized by Pedro Pathing)
        chipMotor = hardwareMap.get(DcMotorEx.class, "chipper");
        lServo = hardwareMap.get(CRServo.class, "feederL");
        rServo = hardwareMap.get(CRServo.class, "feederR");


        // Set launcher motor to brake
        chipMotor.setZeroPowerBehavior(BRAKE);

    }

    public void stop() {
        // When the state is set to idle, whatever ran this state machine will know that artifacts have been launched
        // With the state being idle, the next time update is called, the launch cycle will start over again.
        state = State.IDLE;

        // Stop the shooter motors
        chipMotor.setPower(0);

        // Reset launch count
        launches = 0;


    }


    // Unit converters
    private double rpmToTps(double rpm) {
        return rpm * motorTicksPerRev / 60.0;
    }

    private double tpsToRpm(double tps) {
        return tps * 60.0 / motorTicksPerRev;
    }

    public double getChipRPM() {
        return tpsToRpm(chipMotor.getVelocity());
    }



    // get + set
    public int getLaunches() {
        return launches;
    }

    public int getTargetRPM() {
        return TARGET_RPM;
    }

    public void setTargetRPM(int rpm) {
        TARGET_RPM = rpm;
    }

    double chipRPM = getChipRPM();

    public void feed() {
        lServo.setPower(-feedPower);
        rServo.setPower(-feedPower);
        sleep(250); //wait .25 seconds
        lServo.setPower(0);
        rServo.setPower(0);
    }

    public State update(boolean launchIfReady) {
        switch(state) {
            case IDLE:
                // If this runs, we are starting a new launch cycle, so we'll move to the speed up state
                state = State.SPEED_UP;
                launches = 0; // Resets the launch amount
                inToleranceTimer.reset(); // Reset in tolerance timer
                chipMotor.setPower(0); // Start with 0 power, will be adjusted in SPEED_UP state
                lServo.setPower(0);
                rServo.setPower(0);

                break;


            case SPEED_UP:

                chipMotor.setVelocity(rpmToTps(TARGET_RPM));

                // Create variables to check if each motor is within the RPM tolerance
                boolean chipInTol = Math.abs(TARGET_RPM - getChipRPM()) <= RPM_TOLERANCE;


                // Check if we are within the tolerance
                if (chipInTol) {
                    // Check if we have been within tolerance for the required amount of time (eliminates inconsistency due to oscillation)
                    if (inToleranceTimer.milliseconds() >= RPM_IN_RANGE_TIME) {
                        // We have reached all prerequisites for launch
                        if (!launchIfReady) {
                            // If we are not supposed to launch yet, stay in this state
                            break;
                        }
                        state = State.LAUNCH; // Move to launch state
                        feedTimer.reset(); // Reset tapper raised timer
                    }
                } else {
                    inToleranceTimer.reset();
                }

                break;

            case LAUNCH:
                // Push the ball into the shooter wheel

                if (launches != 0) { // If we aren't on the first launch
                    if (timeSinceLastLaunch.milliseconds() < MIN_TIME_BETWEEN_LAUNCHES) {
                        break; // Wait until the minimum time between launches has passed
                    }
                }


                // Detect shooter flywheel RPM drop to know when ball shoots
                if (!ballFed) { // If the ball isn't already fed
                    if (!feeding) { // If the balls arent feeding
                        feed(); // feed ball
                        feeding = true; // Mark that the servos has been commanded
                        feedTimer.reset(); // Reset the timer to measure how long since the feeder was commanded
                    }

                    // Wait for the feeder to stop
                    if (lServoPower == 0 && rServoPower == 0) {
                        break; // Wait until the feeder is fully stopped
                    }

                    ballFed = true; // Mark that the tapper is now in the pushed position
                }
                    

                    // Check if we've launched 3 artifacts
                    launches += 1;
                    if (launches >= 3) {
                        // Stop the launcher after all 3 artifacts have been launched
                        stop();
                    } else {
                        // Recover from launch
                        state = State.SPEED_UP;
                        inToleranceTimer.reset(); // Reset in tolerance timer
                    }

                timeSinceLastLaunch.reset(); // Reset the time since last launch timer


                break;
        }
        return state;
    }
}
