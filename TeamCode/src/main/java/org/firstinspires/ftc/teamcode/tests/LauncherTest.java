package org.firstinspires.ftc.teamcode.tests;

// FTC SDK
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

// helper files
import org.firstinspires.ftc.teamcode.utils.Launcher;

/*
test OpMode for the intake using the intake helper file

the intake will turn on when you click the right bumper, and will turn off when you click the left bumper
*/

@TeleOp(name = "Launcher Test", group = "Opmode")
@SuppressWarnings("FieldCanBeLocal") // Suppress pointless Android Studio warnings
public class LauncherTest extends LinearOpMode {

    private Launcher launcher;
    private boolean launcherRunning = false; // True when intake is running


    private void shootArtifacts() {
        launcher.update(true); // Put your shooting logic here
        return;
    }

    @Override
    public void runOpMode() {
        // Create instance of intake and initialize
        launcher.init(hardwareMap);

        // Log completed initialization
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        // Wait for START to be pressed
        waitForStart();

        while (opModeIsActive()) {
            if (gamepad1.rightBumperWasPressed()) { // When right bumper is pressed
                shootArtifacts();
            }

            if (gamepad1.leftBumperWasPressed()) { // When left bumper is pressed
                feedArtifacts();
            }

        }

        telemetry.addData("Intake Running", launcherRunning);
        telemetry.update(); // Update Panels and Driver Station after logging
    }
}
