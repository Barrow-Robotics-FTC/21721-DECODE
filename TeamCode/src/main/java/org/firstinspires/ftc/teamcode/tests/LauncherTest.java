package org.firstinspires.ftc.teamcode.tests;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.teamcode.utils.Launcher;

@TeleOp(name = "Launcher Test", group = "Opmode")
public class LauncherTest extends LinearOpMode {

    private Launcher launcher;

    @Override
    public void runOpMode() {
        launcher = new Launcher(hardwareMap);

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            launcher.update(true);

            if (gamepad1.right_bumper) {
                launcher.launch();
            }

            if (gamepad1.left_bumper) {
                launcher.stop();
            }

            telemetry.addData("Launcher State", launcher.getState());
            telemetry.addData("Chip RPM", launcher.getChipRPM());
            telemetry.addData("Target RPM", launcher.getTargetRPM());
            telemetry.addData("Launches", launcher.getLaunches());
            telemetry.update();
        }
    }
}
