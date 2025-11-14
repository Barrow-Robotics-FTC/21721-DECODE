package org.firstinspires.ftc.teamcode.utils;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Intake {
    public static double intakePower = (-.7);
    public static double intakePowerOut = (.5);


    public static DcMotor intakeFront;

    public Intake(HardwareMap hardwareMap) {
        intakeFront = hardwareMap.get(DcMotor.class, "intakeFront");
        intakeFront.setDirection(DcMotor.Direction.FORWARD);

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


}
