package org.firstinspires.ftc.teamcode.utils;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Intake {
    static final double INTAKE_POWER = .75;

    static DcMotor intakeFront;

    public Intake(HardwareMap hardwareMap) {
        intakeFront = hardwareMap.get(DcMotor.class, "intakeFront");
    }

   public static void off() {
       intakeFront.setPower(0);
    }
    
    public static void run() {
        intakeFront.setPower(INTAKE_POWER);
    }

   
}
