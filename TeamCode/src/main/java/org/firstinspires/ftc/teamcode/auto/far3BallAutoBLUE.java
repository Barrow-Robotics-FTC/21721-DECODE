package org.firstinspires.ftc.teamcode.auto;

import static com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior.BRAKE;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

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


    private LauncherV2 launcher;
    private Ramp ramp;


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

    public void turnLeft(double dp, int dt) {
        driveTime = dt;
        drivePower = dp;

        frontLeftDrive.setPower(-dp);
        frontRightDrive.setPower(dp);
        backRightDrive.setPower(dp);
        sleep(driveTime);
        frontLeftDrive.setPower(0);
        frontRightDrive.setPower(0);
        backRightDrive.setPower(0);



    }

    public enum State {
        rampChange,   // waiting to launch
        turnLeft,
        launchArtifacts,
        moveForward

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


        turnLeft(.2, 1100);
        launcher.launchV2();

        while (opModeIsActive() && launcher.getState() != LauncherV2.State.IDLE) {
            launcher.update(true);
            telemetry.addData("Launcher State", launcher.getState());
            telemetry.update();
        }
        forward(.3, 1000);









    }
}
