package org.firstinspires.ftc.teamcode.auto;

import static com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior.BRAKE;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;




/* hello, my name is Mikey and im head of software for team 21721 :D
 * If you are reading this it means that I gave you our sample autonomous, so here is a basic rundown:
 * At the start of the match, you earn 3 extra points for leaving a launch area at the beginning of autonomous. This doesnt seem like alot, but
 * it helps push our alliance to getting the movement ranked point, one of my team's first meet goals.
 */


@Autonomous(name="strafe right auto", group="Autonomous")
public class lM4MoveAuto extends LinearOpMode {

    /* Declare OpMode members. */
    private DcMotor         backRightDrive   = null;
    private DcMotor         backLeftDrive  = null;
    private DcMotor         frontRightDrive  = null;
    private DcMotor         frontLeftDrive  = null;







    @Override
    public void runOpMode() {





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



        sleep(250);

        frontLeftDrive.setPower(.3);
        frontRightDrive.setPower(-.3);
        backLeftDrive.setPower(-.3);
        backRightDrive.setPower(.3);
        sleep(500);
        frontLeftDrive.setPower(0);
        backLeftDrive.setPower(0);




    }
}