package org.firstinspires.ftc.teamcode.utils;

import static com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior.BRAKE;

import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

public class Launcher {
    // Launcher constants
    int TARGET_RPM = 1500;
    final int RPM_TOLERANCE = 50;
    final int RPM_IN_RANGE_TIME = 250;
    final int MIN_TIME_BETWEEN_LAUNCHES = 750;
    final double feedPower = .6;
    final int FEED_TIME = 1;

    public boolean launched = false;

    // Motors and servos
    public DcMotorEx chipMotor;
    public CRServo lServo;
    public CRServo rServo;

    // Other variables
    public State state = State.IDLE;
    private int launches;

    public int targetLaunches;

    // timers
    private final ElapsedTime feedTimer = new ElapsedTime();
    private final ElapsedTime inToleranceTimer = new ElapsedTime();
    private final ElapsedTime recoveryTimer = new ElapsedTime();


    public Launcher(HardwareMap hardwareMap) {
        // initialize hardware (drivetrain is initialized by Pedro Pathing)
        chipMotor = hardwareMap.get(DcMotorEx.class, "chipper");
        lServo = hardwareMap.get(CRServo.class, "lServo");
        rServo = hardwareMap.get(CRServo.class, "rServo");

        // Set launcher motor characteristics
        chipMotor.setZeroPowerBehavior(BRAKE);
        chipMotor.setMode(com.qualcomm.robotcore.hardware.DcMotor.RunMode.RUN_USING_ENCODER);
        chipMotor.setPIDFCoefficients(DcMotorEx.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(300,0,0,10));
        chipMotor.setDirection(DcMotorEx.Direction.FORWARD);

        // Make sure servos are stopped
        lServo.setPower(0);
        rServo.setPower(0);
    }

    public enum State {
        IDLE,
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



    public void launch() {
        if (state == State.IDLE) {
            state = State.SPEED_UP;
            launches = 0;
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
                break;

            case SPEED_UP:
                chipMotor.setVelocity(TARGET_RPM);
                boolean chipInTol = Math.abs(TARGET_RPM - getChipRPM()) <= RPM_TOLERANCE;

                if (chipInTol) {
                    if (inToleranceTimer.milliseconds() >= RPM_IN_RANGE_TIME) {
                        // Ready to feed
                        lServo.setPower(feedPower);
                        rServo.setPower(-feedPower);
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
                        launched = true;
                    } else {
                        state = State.SPEED_UP;
                        inToleranceTimer.reset();
                    }
                }
                break;
        }
    }
}