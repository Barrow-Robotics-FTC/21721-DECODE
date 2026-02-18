package org.firstinspires.ftc.teamcode.tests;

import static org.firstinspires.ftc.teamcode.utils.Intake.intakeFront;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.utils.LauncherV2;
import org.firstinspires.ftc.teamcode.utils.Ramp;
import org.firstinspires.ftc.teamcode.utils.Intake;



@TeleOp(name = "Multi Test", group = "Opmode")
public class MultiTest extends LinearOpMode {


    int CLOSE_TARGET_RPM = 1200;
    int FAR_TARGET_RPM = 1300;

    private LauncherV2 launcher;
    private Intake Intake;

    private Ramp Ramp;



    @Override
    public void runOpMode() {
        Ramp = new Ramp(hardwareMap);
        Intake = new Intake(hardwareMap);
        launcher = new LauncherV2(hardwareMap);

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





            if (gamepad1.bWasReleased()) {
                intakeFront.setPower(-.5);

            }

            if (gamepad1.xWasReleased()) {
                intakeFront.setPower(0);
            }




            if (gamepad1.yWasReleased()) {
                LauncherV2.chipMotor.setVelocity(FAR_TARGET_RPM);
            }

            if (gamepad1.aWasReleased()) {
                LauncherV2.chipMotor.setVelocity(CLOSE_TARGET_RPM);

            }


            if (gamepad1.dpadUpWasReleased()) {
                intakeFront.setPower(0);
                launcher.lServo.setPower(0);
                launcher.rServo.setPower(0);
                launcher.stop();
            }

            if (gamepad1.dpadRightWasReleased()) {
                launcher.lServo.setPower(launcher.feedPower);
                launcher.rServo.setPower(launcher.feedPowerSwapped);
            }

            if (gamepad1.dpadLeftWasReleased()) {
                launcher.lServo.setPower(0);
                launcher.rServo.setPower(0);
            }
            if (gamepad1.dpadRightWasReleased()) {
                launcher.launchV2();
            }
            if (gamepad1.dpadDownWasReleased()) {
                launcher.lServo.setPower(launcher.feedPowerSwapped);
                launcher.rServo.setPower(launcher.feedPower);
                intakeFront.setPower(.2);
            }





        }
    }
}