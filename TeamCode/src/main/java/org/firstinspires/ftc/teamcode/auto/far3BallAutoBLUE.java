package org.firstinspires.ftc.teamcode.auto;

import static com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior.BRAKE;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.IMU;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;

import org.firstinspires.ftc.teamcode.utils.LauncherV2;
import org.firstinspires.ftc.teamcode.utils.Ramp;





/* hello, my name is Mikey and im head of software for team 21721 :D
 * If you are reading this it means that I gave you our sample autonomous, so here is a basic rundown:
 * At the start of the match, you earn 3 extra points for leaving a launch area at the beginning of autonomous. This doesnt seem like alot, but
 * it helps push our alliance to getting the movement ranked point, one of my team's first meet goals.
 */


@Autonomous(name="BLUE - far 3 ball", group="Autonomous")
public class far3BallAutoBLUE extends LinearOpMode {

    /* Declare OpMode members. */
    private DcMotor         backRightDrive   = null;
    private DcMotor         backLeftDrive  = null;
    private DcMotor         frontRightDrive  = null;
    private DcMotor         frontLeftDrive  = null;
    private DcMotor         intakeFront  = null;
    private IMU             imu         = null;      // Control/Expansion Hub IMU



    private LauncherV2 launcher;
    private Ramp ramp;

    private double targetHeading = 0;
    private double headingError  = 0;

    static final double     HEADING_THRESHOLD       = 1.0 ;    // How close must the heading get to the target before moving to next step.
    static final double     P_DRIVE_GAIN           = 0.03;     // Larger is more responsive, but also less stable.
    static final double     P_TURN_GAIN            = 0.02;     // Larger is more responsive, but also less stable.
    static final double     turnSpeed              = 0.2;     // Max turn speed to limit turn rate.



    double intakePower = -.8;
    double drivePower;
    int driveTime;







    public void forward(double dp, int dt) {
        driveTime = dt;
        drivePower = dp;

        frontLeftDrive.setPower(dp);
        frontRightDrive.setPower(dp);
        backLeftDrive.setPower(dp);
        backRightDrive.setPower(dp);
        sleep(driveTime);
        frontLeftDrive.setPower(0);
        frontRightDrive.setPower(0);
        backLeftDrive.setPower(0);
        backRightDrive.setPower(0);

    }

    public void backward(double dp, int dt) {
        driveTime = dt;
        drivePower = dp;

        frontLeftDrive.setPower(-dp);
        frontRightDrive.setPower(-dp);
        backLeftDrive.setPower(-dp);
        backRightDrive.setPower(-dp);
        sleep(driveTime);
        frontLeftDrive.setPower(0);
        frontRightDrive.setPower(0);
        backLeftDrive.setPower(0);
        backRightDrive.setPower(0);


    }

    public double getHeading() {
        YawPitchRollAngles orientation = imu.getRobotYawPitchRollAngles();
        return orientation.getYaw(AngleUnit.DEGREES);
    }
    public double getSteeringCorrection(double desiredHeading, double proportionalGain) {
        targetHeading = desiredHeading;  // Save for telemetry

        // Determine the heading current error
        headingError = targetHeading - getHeading();

        // Normalize the error to be within +/- 180 degrees
        while (headingError > 180)  headingError -= 360;
        while (headingError <= -180) headingError += 360;

        // Multiply the error by the gain to determine the required steering correction/  Limit the result to +/- 1.0
        return Range.clip(headingError * proportionalGain, -1, 1);
    }

    public void turnLeft(double dp, int th) {
        targetHeading = th;
        drivePower = dp;
        getSteeringCorrection(targetHeading, P_DRIVE_GAIN);


        while (opModeIsActive() && (Math.abs(headingError) > HEADING_THRESHOLD)) {
            frontLeftDrive.setPower(-dp);
            frontRightDrive.setPower(dp);
            backRightDrive.setPower(dp);
            backLeftDrive.setPower(-dp);


            // Display drive status for the driver.

        }

        if (!(Math.abs(headingError) > HEADING_THRESHOLD)){
            frontLeftDrive.setPower(0);
            frontRightDrive.setPower(0);
            backRightDrive.setPower(0);
            backLeftDrive.setPower(0);
        }


    }





    @Override
    public void runOpMode() {


        launcher = new LauncherV2(hardwareMap);

        ramp = new Ramp(hardwareMap);


        // change these to your motor names
        backRightDrive  = hardwareMap.get(DcMotor.class, "bRDrive");
        frontRightDrive = hardwareMap.get(DcMotor.class, "fRDrive");
        backLeftDrive  = hardwareMap.get(DcMotor.class, "bLDrive");
        frontLeftDrive  = hardwareMap.get(DcMotor.class, "fLDrive");

        RevHubOrientationOnRobot.LogoFacingDirection logoDirection = RevHubOrientationOnRobot.LogoFacingDirection.LEFT;
        RevHubOrientationOnRobot.UsbFacingDirection  usbDirection  = RevHubOrientationOnRobot.UsbFacingDirection.UP;
        RevHubOrientationOnRobot orientationOnRobot = new RevHubOrientationOnRobot(logoDirection, usbDirection);
        imu = hardwareMap.get(IMU.class, "imu");
        imu.initialize(new IMU.Parameters(orientationOnRobot));
        imu.resetYaw();

        // Now initialize the IMU with this mounting orientation
        // This sample expects the IMU to be in a REV Hub and named "imu".



        //make these directions match the way your robot is built
        frontLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        backLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        frontRightDrive.setDirection(DcMotor.Direction.FORWARD);
        backRightDrive.setDirection(DcMotor.Direction.FORWARD);

        frontLeftDrive.setZeroPowerBehavior(BRAKE);
        frontRightDrive.setZeroPowerBehavior(BRAKE);
        backLeftDrive.setZeroPowerBehavior(BRAKE);
        backRightDrive.setZeroPowerBehavior(BRAKE);

        // Send telemetry message to signify robot waiting;
        telemetry.addData("Status", "Ready to run");    //
        telemetry.update();

        // Wait for the game to start (driver presses START)
        waitForStart();

        //        VALUES TESTED AT 12.5ish VOLTS
        // Value to run the robot forward one full tile of the field. Drive power (.3) and drive time (1000 miliseconds)
        // Value to run the robot backward one full tile of the field. Drive power (.3) and drive time (1000 miliseconds)



        ramp.setPosFar();
        launcher.TARGET_RPM = 1650;


        turnLeft(.2, 24);
        launcher.launchV2();

        while (opModeIsActive() && launcher.getState() != LauncherV2.State.IDLE) {
            launcher.update(true);
            telemetry.addData("Launcher State", launcher.getState());
            telemetry.update();
        }
        forward(.3, 1000);









    }
}
