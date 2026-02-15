package org.firstinspires.ftc.teamcode.pedroPathing;
import com.pedropathing.control.FilteredPIDFCoefficients;
import com.pedropathing.control.PIDFCoefficients;
import com.pedropathing.control.PredictiveBrakingCoefficients;
import com.pedropathing.follower.Follower;
import com.pedropathing.follower.FollowerConstants;
import com.pedropathing.ftc.FollowerBuilder;
import com.pedropathing.ftc.drivetrains.MecanumConstants;
import com.pedropathing.ftc.localization.constants.PinpointConstants;
import com.pedropathing.paths.PathConstraints;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class Constants {
    public static FollowerConstants followerConstants = new FollowerConstants()
        .mass(18)
        .forwardZeroPowerAcceleration(-60.23597214)
        .lateralZeroPowerAcceleration(-79.99681021)
        .centripetalScaling(0.0001)


        .headingPIDFCoefficients(new PIDFCoefficients(
                    1.1,
                    0,
                    0.03,
                    0.01
            ))
        .predictiveBrakingCoefficients(new PredictiveBrakingCoefficients(
                .1,
                .06013193345655383,
                .001747533340838866
        ));




    public static PathConstraints pathConstraints = new PathConstraints(
            0.99,
            100,
            .9,
            1);

    public static MecanumConstants driveConstants = new MecanumConstants()
            .maxPower(1)
            .rightFrontMotorName("fRDrive")
            .rightRearMotorName("bRDrive")
            .leftRearMotorName("bLDrive")
            .leftFrontMotorName("fLDrive")
            .leftFrontMotorDirection(DcMotorSimple.Direction.REVERSE)
            .leftRearMotorDirection(DcMotorSimple.Direction.REVERSE)
            .rightFrontMotorDirection(DcMotorSimple.Direction.FORWARD)
            .rightRearMotorDirection(DcMotorSimple.Direction.FORWARD)
            .xVelocity(72.3456329586)
            .yVelocity(54.4307736374);





    public static Follower createFollower(HardwareMap hardwareMap) {
        return new FollowerBuilder(followerConstants, hardwareMap)
                .pathConstraints(pathConstraints)
                .mecanumDrivetrain(driveConstants)
                .pinpointLocalizer(localizerConstants)
                .build();
    }

    public static PinpointConstants localizerConstants = new PinpointConstants()
            .forwardPodY(-1)
            .strafePodX(-7.5)
            .distanceUnit(DistanceUnit.INCH)
            .hardwareMapName("pinpoint")
            .encoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD)
            .forwardEncoderDirection(GoBildaPinpointDriver.EncoderDirection.FORWARD)
            .strafeEncoderDirection(GoBildaPinpointDriver.EncoderDirection.REVERSED);
}