package org.firstinspires.ftc.teamcode.util;

import static android.os.SystemClock.sleep;
import static com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior.BRAKE;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.util.ElapsedTime;

public class Launcher {
    // Launcher constants
    final int TARGET_RPM = 1500; // Target RPM for chip motor
    final int RPM_TOLERANCE = 50; // Tolerance of RPM required for launch
    final int RPM_IN_RANGE_TIME = 250; // How long the launcher must be within the target RPM tolerance to launch (milliseconds)
    final int ARTIFACT_LAUNCHED_RPM_TOLERANCE = TARGET_RPM - 100; // Launcher motor RPM must be below this for an artifact to be considered launched
    final int feedPower = .5; //power to send to the feeding servos when launching

    // Motors and servos
    private DcMotorEx chipMotor; // flywheel motor
    private CRServo lServo; // Left servo
    private CRServo rServo; // Right servo


    // Other variables
    private final ElapsedTime inToleranceTimer = new ElapsedTime();
    private State state = State.IDLE;
    private int launches;

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


        // Set launche motor to brake
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


    public double getChipRPM() {
        return chipMotor.getVelocity();
    }

    public void feed() {
        lServo.setPower(-feedPower);
        rServo.setPower(-feedPower);
        sleep(250); //wait .25 seconds
        lServo.setPower(0);
        rServo.setPower(0);
    }

    public State update() {
        switch(state) {
            case IDLE:
                // If this runs, we are starting a new launch cycle, so we'll move to the speed up state
                state = State.SPEED_UP;
                launches = 0; // Resets the launch amount
                inToleranceTimer.reset(); // Reset in tolerance timer

                break;
            case SPEED_UP:
                double currentChipRPM = chipMotor.getVelocity();
                

                // Check if we are within the tolerance
                if (Math.abs(TARGET_RPM - currentChipRPM) <= RPM_TOLERANCE) {
                    // Check if we have been within tolerance for the required amount of time
                    if (inToleranceTimer.milliseconds() >= RPM_IN_RANGE_TIME) {
                        // We have reached all requirements for launch
                        state = State.LAUNCH;
                    }
                } else {
                    inToleranceTimer.reset();
                }
                break;
            case LAUNCH:
                // Push the ball into the shooter wheels
                feed();

                // Detect shooter flywheel RPM drop to know when ball shoots
                if (chipMotor.getVelocity() <= ARTIFACT_LAUNCHED_RPM_TOLERANCE) {
                    
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
                }
                break;
        }
        return state;
    }
}
