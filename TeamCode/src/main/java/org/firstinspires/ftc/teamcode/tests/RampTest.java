package org.firstinspires.ftc.teamcode.tests;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.teamcode.utils.Ramp;

@TeleOp(name = "Ramp Test", group = "Opmode")
public class RampTest extends LinearOpMode {
    

    @Override
    public void runOpMode() {
        Ramp Ramp = new Ramp(hardwareMap);

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            
            if (gamepad1.rightBumperWasReleased()) {
                Ramp.setPosAgainst();
            }

            
            if (gamepad1.leftBumperWasReleased()) {
                Ramp.setPosFar();
            }
            
        }
    }
}
