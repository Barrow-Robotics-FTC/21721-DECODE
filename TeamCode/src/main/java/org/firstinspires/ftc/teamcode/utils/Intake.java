package org.firstinspires.ftc.teamcode.utils;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Intake {
    public static double intakePower = (-.7);
    public static double intakePowerOut = (.5);

    public static double lowServoPower = (.7);
    public static double lowServoOff = (0);




    public static DcMotor intakeFront;
    public static CRServo lServoLow;
    public static CRServo rServoLow;



    public Intake(HardwareMap hardwareMap) {
        intakeFront = hardwareMap.get(DcMotor.class, "intakeFront");
        intakeFront.setDirection(DcMotor.Direction.FORWARD);

        lServoLow = hardwareMap.get(CRServo.class, "lServoLow");
        rServoLow = hardwareMap.get(CRServo.class, "rServoLow");

    }

    public static void stop(){
        intakeFront.setPower(0);
    }

    public static void in(){
        intakeFront.setPower(intakePower);
    }

    public static void out(){
        intakeFront.setPower(intakePowerOut);
    }

    public static void servoIn(){
        lServoLow.setPower(lowServoPower);
        rServoLow.setPower(-lowServoPower);
    }

    public static void servoOut(){
        lServoLow.setPower(-lowServoPower);
        rServoLow.setPower(lowServoPower);
    }


}
