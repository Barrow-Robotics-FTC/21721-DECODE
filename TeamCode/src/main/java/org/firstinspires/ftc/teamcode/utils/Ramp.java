package org.firstinspires.ftc.teamcode.utils;

import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Ramp {
    public static double lAgainstPos = (1);
    public static double rAgainstPos = (0);



    public static double midPos = (.7);


    public static double lFarPos = (.25);
    public static double rFarPos = (1);



    public static Servo lRampServo;
    public static Servo rRampServo;



    public Ramp(HardwareMap hardwareMap) {
        lRampServo = hardwareMap.get(Servo.class, "lRamp");
        rRampServo = hardwareMap.get(Servo.class, "rRamp");

    }

    public static void setPosAgainst(){
        lRampServo.setPosition(lAgainstPos);
        rRampServo.setPosition(rAgainstPos);

    }

    public static void setPosMid(){
        lRampServo.setPosition(midPos);
        rRampServo.setPosition(midPos);

    }
    public static void setPosFar(){
        lRampServo.setPosition(lFarPos);
        rRampServo.setPosition(rFarPos);

    }












}
