package org.firstinspires.ftc.teamcode.utils;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Intake {
    public static double intakePower = (-1);
    public static double intakePowerOut = (1);





    public static DcMotor intakeFront;
    public static CRServo lServoLow;
    public static CRServo rServoLow;



    public Intake(HardwareMap hardwareMap) {
        intakeFront = hardwareMap.get(DcMotor.class, "intakeFront");
        intakeFront.setDirection(DcMotor.Direction.FORWARD);

    }



    public static void in(){
        intakeFront.setPower(intakePower);
    }

    public static void out(){

        intakeFront.setPower(intakePowerOut);
    }

    public static void off(){
        intakeFront.setPower(0);
    }




}
