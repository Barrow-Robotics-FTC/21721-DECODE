package org.firstinspires.ftc.teamcode.tests;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;

import org.firstinspires.ftc.teamcode.utils.LauncherV2;

/*
Launcher Tuning OpMode
Tune the PIDF controllers for the launcher subsystem
Use the Panels graph to visualize the RPM response to changes in the constants

Controls:
Right Bumper: Toggle launcher speed up

Constants:
kP: Gain
kI: Ignore
kD: Damping
kF: Static feedforward (unit conversion from velocity to power)

Tuning:
- Increase kF until the flywheel reaches target speed
- Tune kP to make it accelerate to the target speed quickly with minimal overshoot
- Increase kD if there is overshoot or oscillation
*/
@TeleOp(name = "Launcher Tuner", group = "Tests")
@SuppressWarnings("FieldCanBeLocal") // Suppress pointless Android Studio warnings
@Configurable
public class LauncherTuner extends LinearOpMode {
    private LauncherV2 launcher;
    public static double p;
    public static double i;
    public static double d;
    public static double f;

    @Override
    public void runOpMode() {
        TelemetryManager telemetryM = PanelsTelemetry.INSTANCE.getTelemetry();

        // Create instance of launcher and initialize
        launcher = new LauncherV2(hardwareMap);
        launcher.TARGET_RPM = 1250; // Set a default target RPM for tuning
        launcher.RPM_TOLERANCE = 0; // This stops the launcher from leaving the speed up state while tuning

        // Set initial coefficients from the launcher
        p = launcher.getCoefficients().p;
        i = launcher.getCoefficients().i;
        d = launcher.getCoefficients().d;
        f = launcher.getCoefficients().f;

        // Waiting for the start command
        telemetry.addData("Status", "Initialized");
        telemetry.update();
        waitForStart();

        while (opModeIsActive()) {
            // Update the controller coefficients and run the launcher loop
            PIDFCoefficients newCoeffs = new PIDFCoefficients(p, i, d, f);
            launcher.setCoefficients(newCoeffs);
            launcher.update(true);

            // Right Bumper: toggle launcher speed up
            if (gamepad1.rightBumperWasPressed()) {
                if (launcher.isBusy()) { // If launcher isn't idle
                    launcher.stop(); // Stop the launcher
                } else {
                    launcher.launchV2(); // Speed up the launcher (this won't ever enter the launch state since we're setting the tolerance to 0, so it will just hold the speed)
                }
            }

            // Panels telemetry for a graph
            telemetryM.addData("target", launcher.getTargetRPM());
            telemetryM.addData("rpm", launcher.getChipRPM());
            telemetryM.update();

            // Log status
            telemetry.addData("Launcher State", launcher.getState());
            telemetry.addData("Target RPM", launcher.getTargetRPM());
            telemetry.addData("Actual RPM", launcher.getChipRPM());
            telemetry.addData("P", newCoeffs.p);
            telemetry.addData("I", newCoeffs.i);
            telemetry.addData("D", newCoeffs.d);
            telemetry.addData("F", newCoeffs.f);
            telemetry.update();
        }
    }
}