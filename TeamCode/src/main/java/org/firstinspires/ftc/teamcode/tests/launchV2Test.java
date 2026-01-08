package org.firstinspires.ftc.teamcode.tests;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.teamcode.utils.LauncherV2;

@TeleOp(name = "Launcher V2 Test", group = "Opmode")
public class launchV2Test extends LinearOpMode {

    private LauncherV2 launcher;

    public static int targetLaunches;


    @Override
    public void runOpMode() {
        launcher = new LauncherV2(hardwareMap);

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            launcher.update(true);

            if (gamepad1.rightBumperWasReleased()) {
                targetLaunches = 1;
                launcher.launchV2();
            }

            if (gamepad1.leftBumperWasReleased()) {
                targetLaunches = 3;
                launcher.launchV2();
            }


            telemetry.addData("Launcher State", launcher.getState());
            telemetry.addData("Chip RPM", launcher.getChipRPM());
            telemetry.addData("Target RPM", launcher.getTargetRPM());
            telemetry.addData("Launches", launcher.getLaunches());
            telemetry.addData("controls", "left bumper for three, right for one");
            telemetry.update();
        }
    }
}
