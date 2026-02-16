package org.firstinspires.ftc.teamcode.utils;

import static com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior.BRAKE;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import org.firstinspires.ftc.teamcode.utils.Intake;


public class LauncherV2 {
    // Launcher constants
    public int CLOSE_TARGET_RPM = 1050;
    public int FAR_TARGET_RPM = 1400;
    public int FAR_FAR_TARGET_RPM = 1680;
    public int TARGET_RPM = 1300;

    final int RPM_TOLERANCE = 50;
    final int RPM_IN_RANGE_TIME = 250;
    final int MIN_TIME_BETWEEN_LAUNCHES = 500;
    public final double feedPower = 1;
    public final double feedPowerSwapped = -1;
    public static double intakePower = (-1);

    final int FEED_TIME = 3000;

    public boolean launched = false;

    // Motors and servos
    public static DcMotorEx chipMotor;
    public static DcMotorEx intakeFront;

    public static CRServo lServo;
    public static CRServo rServo;

    // Other variables
    public State state = State.IDLE;
    private int launches;

    public int targetLaunches;
    public boolean isBusy;
    private Intake Intake;



    // timers
    private final ElapsedTime feedTimer = new ElapsedTime();
    private final ElapsedTime inToleranceTimer = new ElapsedTime();
    private final ElapsedTime recoveryTimer = new ElapsedTime();


    public LauncherV2(HardwareMap hardwareMap) {
        // initialize hardware (drivetrain is initialized by Pedro Pathing)
        chipMotor = hardwareMap.get(DcMotorEx.class, "chipper");
        lServo = hardwareMap.get(CRServo.class, "lServo");
        rServo = hardwareMap.get(CRServo.class, "rServo");
        intakeFront = hardwareMap.get(DcMotorEx.class, "intakeFront");


        // Set launcher motor characteristics
        chipMotor.setZeroPowerBehavior(BRAKE);
        chipMotor.setMode(com.qualcomm.robotcore.hardware.DcMotor.RunMode.RUN_USING_ENCODER);
        chipMotor.setPIDFCoefficients(DcMotorEx.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(300,0,0,10));
        chipMotor.setDirection(DcMotorEx.Direction.FORWARD);
        intakeFront.setDirection(DcMotor.Direction.FORWARD);

        // Make sure servos are stopped
    }

    public enum State {
        IDLE, //waiting to launch
        SPEED_UP,
        FEED,
        RECOVER
    }

    public void stop() {
        state = State.IDLE;
        chipMotor.setPower(0);
        lServo.setPower(0);
        rServo.setPower(0);
        launches = 0;
    }

    public boolean isBusy() {
        return state != State.IDLE;
    }



    public double getChipRPM() {
        return chipMotor.getVelocity();
    }

    public int getLaunches() {
        return launches;
    }

    public int getTargetRPM() {
        return TARGET_RPM;
    }

    public void setTargetLaunches(int launches) {
        this.targetLaunches = launches;
    }



    public void launchV2() {
        if (state == State.IDLE) {
            isBusy = true;
            state = State.SPEED_UP;
            launches = 0;
            inToleranceTimer.reset();
            inToleranceTimer.reset();
        }
    }



    public State getState() {
        return state;
    }

    public void update(boolean b) {
        switch (state) {
            case IDLE:
                chipMotor.setPower(0);
                lServo.setPower(0);
                rServo.setPower(0);
                intakeFront.setPower(0);
                break;

            case SPEED_UP:
                chipMotor.setVelocity(TARGET_RPM);
                boolean chipInTol = Math.abs(TARGET_RPM - getChipRPM()) <= RPM_TOLERANCE;

                if (chipInTol) {
                    if (inToleranceTimer.milliseconds() >= RPM_IN_RANGE_TIME) {
                        // Ready to feed
                        lServo.setPower(feedPower);
                        rServo.setPower(-feedPower);
                        intakeFront.setPower(intakePower);
                        state = State.FEED;
                        feedTimer.reset();

                    }
                } else {
                    inToleranceTimer.reset();
                }
                break;

            case FEED:

                if (feedTimer.milliseconds() >= FEED_TIME) {
                    lServo.setPower(0);
                    rServo.setPower(0);
                    launches++;
                    recoveryTimer.reset();
                    state = State.RECOVER;
                }
                break;

            case RECOVER:
                if (recoveryTimer.milliseconds() >= MIN_TIME_BETWEEN_LAUNCHES) {
                    if (launches >= targetLaunches) {
                        stop();
                    }
                    else {
                        state = State.SPEED_UP;
                        inToleranceTimer.reset();
                    }
                }
                isBusy = false;
                break;
        }
    }
}