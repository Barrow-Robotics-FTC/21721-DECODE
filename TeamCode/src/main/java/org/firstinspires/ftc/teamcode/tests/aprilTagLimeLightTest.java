package org.firstinspires.ftc.teamcode.tests;

import androidx.annotation.NonNull;

import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

public class aprilTagLimeLightTest extends OpMode {
    private Limelight3A limelight;

    public void init(){
        limelight = hardwareMap.get(Limelight3A.class,"limelight");
        limelight.pipelineSwitch(1);

    }

    public void start(){


    }

    public void loop(){


    }

}
