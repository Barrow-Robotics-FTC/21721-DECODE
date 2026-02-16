package org.firstinspires.ftc.teamcode.tests;

import androidx.annotation.NonNull;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;
@TeleOp
public class aprilTagLimeLightTest extends OpMode {
    private Limelight3A limelight;
    private IMU imu;
    private double distance;

    @Override
    public void init(){
        limelight = hardwareMap.get(Limelight3A.class,"limelight");
        limelight.pipelineSwitch(1); //blue goal pipeline
        imu = hardwareMap.get(IMU.class, "imu");
        RevHubOrientationOnRobot revHubOrientationOnRobot = new RevHubOrientationOnRobot(RevHubOrientationOnRobot.LogoFacingDirection.LEFT,
                RevHubOrientationOnRobot.UsbFacingDirection.UP);
        imu.initialize(new IMU.Parameters(revHubOrientationOnRobot));
    }
    @Override
    public void start(){
        limelight.start();

    }

    @Override
    public void loop(){
        YawPitchRollAngles orientation = imu.getRobotYawPitchRollAngles();
        limelight.updateRobotOrientation(orientation.getYaw());
        LLResult llResult = limelight.getLatestResult();
        if(llResult != null && llResult.isValid()) {
            distance = getDistanceFromAT(llResult.getTa());
            telemetry.addData("distance", distance);
            Pose3D botPose = llResult.getBotpose_MT2();
            telemetry.addData("Target x", llResult.getTx());
            telemetry.addData("Target y", llResult.getTy());
            telemetry.addData("Target a", llResult.getTa());

        }

    }

    public double getDistanceFromAT(double ta){
        double scale = 326.0366;
        double distance = (scale / ta);
        return distance;
    }

}
