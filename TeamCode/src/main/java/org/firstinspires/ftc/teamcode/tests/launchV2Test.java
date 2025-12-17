package org.firstinspires.ftc.teamcode.tests;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.teamcode.utils.Launcher;

@TeleOp(name = "Launcher V2 Test", group = "Opmode")
public class launchV2Test extends LinearOpMode {

    private Launcher launcher;

    public static int targetLaunches;


    @Override
    public void runOpMode() {
        launcher = new Launcher(hardwareMap);

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            launcher.update(true);

            if (gamepad1.rightBumperWasReleased()) {
                targetLaunches = 1;
                launcher.launch();
            }

            if (gamepad1.rightBumperWasReleased()) {
                targetLaunches = 3;
                launcher.launch();
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
