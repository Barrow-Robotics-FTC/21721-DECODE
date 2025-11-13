package org.firstinspires.ftc.teamcode.tests;

// FTC SDK
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

// helper files
import org.firstinspires.ftc.teamcode.utils.Intake;

/*
test OpMode for the intake using the intake helper file

the intake will turn on when you click the right bumper, and will turn off when you click the left bumper
*/

@TeleOp(name = "Intake Test", group = "Tests")
@SuppressWarnings("FieldCanBeLocal") // Suppress pointless Android Studio warnings
public class IntakeTest extends LinearOpMode {
    private Intake intake; // Custom intake class
    private boolean intakeRunning = false; // True when intake is running

    @Override
    public void runOpMode() {
        // Create instance of intake and initialize
        intake = new Intake(hardwareMap);

        // Log completed initialization
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        // Wait for START to be pressed
        waitForStart();

        while (opModeIsActive()) {

            if (gamepad2.right_trigger > 0) {
                intake.run();
            }
            if(!(gamepad1.right_trigger > 0)){
                intake.off();
            }

            telemetry.addData("Intake Running", intakeRunning);
            telemetry.update(); // Update Panels and Driver Station after logging
        }
    }
}

