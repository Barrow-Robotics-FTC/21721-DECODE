package org.firstinspires.ftc.teamcode.util;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Intake {
    final double INTAKE_POWER = .8;

    CRServo intakeL;
    CRServo intakeR;

    public Intake(HardwareMap hardwareMap) {
        intakeL = hardwareMap.get(CRServo.class, "intakeL");
        intakeR = hardwareMap.get(CRServo.class, "intakeR");
    }

   public void stop() {
        intakeL.setPower(0);
        intakeR.setPower(0);
    }  
    
    public void run() {
        intakeL.setPower(INTAKE_POWER);
        intakeR.setPower(-INTAKE_POWER);
    }

   
}
