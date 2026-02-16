package org.firstinspires.ftc.teamcode.tests;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;

@TeleOp
public class ATautoAlignment extends OpMode {

    private Limelight3A limelight;
    private IMU imu;
    DcMotor frontLeftDrive = null;
    DcMotor backLeftDrive = null;
    DcMotor frontRightDrive = null;
    DcMotor backRightDrive = null;

    // --------------------------------- PD controller (Rotation) ---------------------
    double kP = .025; // Increased from .001 for better response
    double error = 0;
    double lastError = 0;
    double goalX = 0; // Target crosshair offset (0 = centered)
    double angleTolerance = 0.5;
    double kD = .001; 
    double lastTime = 0;

    // --------------------------------- PD controller (Distance) ---------------------
    double kP_dist = 0.04; // Increased from 0.02
    double distError = 0;
    double distGoal = 60.0; // Desired distance from tag
    double distTolerance = 1.5;

    // --------------------------------- driving setup ---------------------
    double forward, strafe, rotate;

    //--------------------------------- controller based PD tuning ----------------
    double[] stepSizes = {.1, .01, .001, .0001};
    int stepIndex = 2;

    public void init(){
        frontLeftDrive = hardwareMap.get(DcMotor.class, "fLDrive");
        backLeftDrive = hardwareMap.get(DcMotor.class, "bLDrive");
        frontRightDrive = hardwareMap.get(DcMotor.class, "fRDrive");
        backRightDrive = hardwareMap.get(DcMotor.class, "bRDrive");

        frontLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        backLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        frontRightDrive.setDirection(DcMotor.Direction.FORWARD);
        backRightDrive.setDirection(DcMotor.Direction.FORWARD);

        limelight = hardwareMap.get(Limelight3A.class,"limelight");
        limelight.pipelineSwitch(1); // Ensure pipeline 1 is configured for AprilTags in the Limelight UI
        
        imu = hardwareMap.get(IMU.class, "imu");
        RevHubOrientationOnRobot revHubOrientationOnRobot = new RevHubOrientationOnRobot(RevHubOrientationOnRobot.LogoFacingDirection.LEFT,
                RevHubOrientationOnRobot.UsbFacingDirection.UP);
        imu.initialize(new IMU.Parameters(revHubOrientationOnRobot));

        telemetry.addLine("Initialized. Ensure Limelight pipeline 1 is set to AprilTag.");
    }

    public void drive(double forward, double strafe, double rotate){
        double denominator = Math.max(Math.abs(forward) + Math.abs(strafe) + Math.abs(rotate), 1);
        double frontLeftPower = (forward + strafe + rotate) / denominator;
        double backLeftPower = (forward - strafe + rotate) / denominator;
        double frontRightPower = (forward - strafe - rotate) / denominator;
        double backRightPower = (forward + strafe - rotate) / denominator;

        frontLeftDrive.setPower(frontLeftPower);
        backLeftDrive.setPower(backLeftPower);
        frontRightDrive.setPower(frontRightPower);
        backRightDrive.setPower(backRightPower);
    }
    
    public void start(){
        lastTime = getRuntime();
        limelight.start();
    }

    public void loop(){
        // Default to stick values
        forward = -gamepad1.left_stick_y;
        strafe = gamepad1.left_stick_x;
        rotate = gamepad1.right_stick_x;

        YawPitchRollAngles orientation = imu.getRobotYawPitchRollAngles();
        limelight.updateRobotOrientation(orientation.getYaw());
        LLResult llResult = limelight.getLatestResult();

        boolean hasTarget = (llResult != null && llResult.isValid());

        if (gamepad1.left_trigger > 0.2){
            if (hasTarget){
                // --- Rotation PD Logic ---
                error = goalX - llResult.getTx();
                if (Math.abs(error) < angleTolerance){
                    rotate = 0;
                } else {
                    double curTime = getRuntime();
                    double deltaTime = curTime - lastTime;
                    double pTerm = error * kP;
                    double dTerm = (deltaTime > 0) ? ((error - lastError) / deltaTime) * kD : 0;
                    
                    rotate = Range.clip(pTerm + dTerm, -.5, .5);
                    
                    lastError = error;
                    lastTime = curTime;
                }

                // --- Distance PD Logic ---
                double currentDist = getDistanceFromAT(llResult.getTa());
                distError = currentDist - distGoal;
                
                if (Math.abs(distError) < distTolerance) {
                    forward = 0;
                } else {
                    forward = Range.clip(distError * kP_dist, -.5, .5);
                }
                
                telemetry.addData("Status", "Auto Aligning");
                telemetry.addData("Dist", "%.1f", currentDist);
                telemetry.addData("TX", "%.1f", llResult.getTx());
            } else {
                telemetry.addData("Status", "No Target Visible");
                lastTime = getRuntime();
                lastError = 0;
            }
        } else {
            telemetry.addData("Status", "Manual Control");
            lastError = 0;
            lastTime = getRuntime();
        }

        drive(forward, strafe, rotate);

        // Tuning and Telemetry
        if (gamepad1.bWasReleased()) stepIndex = (stepIndex + 1) % stepSizes.length;
        if (gamepad1.dpadLeftWasReleased()) kP -= stepSizes[stepIndex];
        if (gamepad1.dpadRightWasReleased()) kP += stepSizes[stepIndex];
        if (gamepad1.dpadUpWasReleased()) kD += stepSizes[stepIndex];
        if (gamepad1.dpadDownWasReleased()) kD -= stepSizes[stepIndex];

        telemetry.addData("kP", kP);
        telemetry.addData("kD", kD);
        telemetry.addData("Target Found", hasTarget);
        telemetry.addData("Outputs (Fwd/Rot)", "%.2f / %.2f", forward, rotate);
        telemetry.update();
    }

    public double getDistanceFromAT(double ta){
        if (ta <= 0) return 0;
        double scale = 326.0366;
        return (scale / ta);
    }
}
