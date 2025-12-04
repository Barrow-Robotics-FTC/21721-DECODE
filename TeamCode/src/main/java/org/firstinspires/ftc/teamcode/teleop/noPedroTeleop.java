package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.utils.Intake;
import org.firstinspires.ftc.teamcode.utils.Launcher;
import org.firstinspires.ftc.teamcode.utils.Ramp;

import static com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior.BRAKE;
import static org.firstinspires.ftc.teamcode.utils.Intake.intakeFront;
import static org.firstinspires.ftc.teamcode.utils.Intake.lowServoOff;
import static org.firstinspires.ftc.teamcode.utils.Intake.lowServoPower;


@TeleOp(name="teleOP without PP", group="opmode")
public class noPedroTeleop extends LinearOpMode {

    // Declare OpMode members for each of the 4 motors.
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor frontLeftDrive = null;
    private DcMotor backLeftDrive = null;
    private DcMotor frontRightDrive = null;
    private DcMotor backRightDrive = null;
    private DcMotor intakeFront = null;

    private Launcher launcher;
    private Intake Intake;
    private Ramp Ramp;

    public int targetLaunches;

    private float driveSpeed = .6F;




    @Override
    public void runOpMode() {
        Launcher launcher = new Launcher(hardwareMap);
        Ramp = new Ramp(hardwareMap);

        frontLeftDrive = hardwareMap.get(DcMotor.class, "fLDrive");
        backLeftDrive = hardwareMap.get(DcMotor.class, "bLDrive");
        frontRightDrive = hardwareMap.get(DcMotor.class, "fRDrive");
        backRightDrive = hardwareMap.get(DcMotor.class, "bRDrive");
        intakeFront = hardwareMap.get(DcMotor.class, "intakeFront");

        CRServo lServoLow = hardwareMap.get(CRServo.class, "lServoLow");
        CRServo rServoLow = hardwareMap.get(CRServo.class, "rServoLow");


        frontLeftDrive.setZeroPowerBehavior(BRAKE);
        frontRightDrive.setZeroPowerBehavior(BRAKE);
        backLeftDrive.setZeroPowerBehavior(BRAKE);
        backRightDrive.setZeroPowerBehavior(BRAKE);

        frontLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        backLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        frontRightDrive.setDirection(DcMotor.Direction.FORWARD);
        backRightDrive.setDirection(DcMotor.Direction.FORWARD);

        // Wait for the game to start (driver presses START)
        telemetry.addData("Status", "Initialized");
        telemetry.update();




        waitForStart();
        runtime.reset();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            launcher.update(true);

            if (gamepad2.yWasReleased()) {
                targetLaunches = 1;
                launcher.setTargetLaunches(targetLaunches);
                launcher.launch();
            }

            if (gamepad2.bWasReleased()) {
                Ramp.setPosFar();
                launcher.TARGET_RPM = launcher.FAR_TARGET_RPM;

            }

            if (gamepad2.aWasReleased()) {
                Ramp.setPosAgainst();
                launcher.TARGET_RPM = launcher.CLOSE_TARGET_RPM;

            }

            if (gamepad2.left_bumper) {
                launcher.lServo.setPower(launcher.feedPowerSwapped);
                launcher.rServo.setPower(launcher.feedPower);
                intakeFront.setPower(.3);
            }

            if (gamepad1.left_bumper) {
                driveSpeed = .2F;
            }

            if (gamepad1.right_bumper) {
                driveSpeed = .6F;
            }

            if (gamepad2.right_trigger > 0) {
                intakeFront.setPower(-.7);

            }

            if (gamepad2.left_trigger > 0) {
                intakeFront.setPower(.5);

            }

            if (gamepad2.right_bumper){
                Launcher.lServo.setPower(-.7);
                Launcher.rServo.setPower(.7);

            }

            if (gamepad2.dpadUpWasReleased()){
                intakeFront.setPower(0);
                lServoLow.setPower(0);
                rServoLow.setPower(0);
                launcher.stop();

            }




            double max;
            double axial   = (-gamepad1.left_stick_x * driveSpeed);  // Note: pushing stick forward gives negative value
            double lateral =  (gamepad1.left_stick_y * driveSpeed);
            double yaw     =  (gamepad1.right_stick_x * driveSpeed);

            double frontLeftPower  = axial + lateral + yaw;
            double frontRightPower = axial - lateral - yaw;
            double backLeftPower   = axial - lateral + yaw;
            double backRightPower  = axial + lateral - yaw;


            // Normalize the values so no wheel power exceeds 100%
            // This ensures that the robot maintains the desired motion.
            max = Math.max(Math.abs(frontLeftPower), Math.abs(frontRightPower));
            max = Math.max(max, Math.abs(backLeftPower));
            max = Math.max(max, Math.abs(backRightPower));

            if (max > 1.0) {
                frontLeftPower  /= max;
                frontRightPower /= max;
                backLeftPower   /= max;
                backRightPower  /= max;
            }


            // Send calculated power to wheels
            frontLeftDrive.setPower(frontLeftPower);
            frontRightDrive.setPower(frontRightPower);
            backLeftDrive.setPower(backLeftPower);
            backRightDrive.setPower(backRightPower);




            // Show the elapsed game time and wheel power.
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.addData("Front left/Right", "%4.2f, %4.2f", frontLeftPower, frontRightPower);
            telemetry.addData("Back  left/Right", "%4.2f, %4.2f", backLeftPower, backRightPower);
            telemetry.addData("target launches", targetLaunches);
            telemetry.addData("frontLeftPower", frontLeftPower);
            telemetry.addData("frontRightPower", frontRightPower);
            telemetry.addData("backLeftPower", backLeftPower);
            telemetry.addData("backRightPower", backRightPower);
            telemetry.update();

        }
    }}
