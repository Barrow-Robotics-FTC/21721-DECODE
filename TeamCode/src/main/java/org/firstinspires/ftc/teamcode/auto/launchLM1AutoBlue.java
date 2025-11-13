package org.firstinspires.ftc.teamcode.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.utils.Launcher;


/* hello, my name is Mikey and im head of software for team 21721 :D
* If you are reading this it means that I gave you our sample autonomous, so here is a basic rundown:
* At the start of the match, you earn 3 extra points for leaving a launch area at the beginning of autonomous. This doesnt seem like alot, but
* it helps push our alliance to getting the movement ranked point, one of my team's first meet goals.
*/


@Autonomous(name="BLUE LAUNCH - LM1 auto", group="Autonomous")
public class launchLM1AutoBlue extends LinearOpMode {

    /* Declare OpMode members. */
    private DcMotor         backRightDrive   = null;
    private DcMotor         backLeftDrive  = null;
    private DcMotor         frontRightDrive  = null;
    private DcMotor         frontLeftDrive  = null;

    private Launcher launcher;

    int TARGET_RPM = 1600;
    final int RPM_TOLERANCE = 50;
    final int RPM_IN_RANGE_TIME = 250;


    @Override
    public void runOpMode() {


        launcher = new Launcher(hardwareMap);
        launcher.update(true);

        // change these to your motor names
        frontLeftDrive  = hardwareMap.get(DcMotor.class, "fLDrive");
        frontRightDrive = hardwareMap.get(DcMotor.class, "fRDrive");
        backLeftDrive  = hardwareMap.get(DcMotor.class, "bLDrive");
        backRightDrive  = hardwareMap.get(DcMotor.class, "bRDrive");

        //make these directions match the way your robot is built
        frontLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        backLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        frontRightDrive.setDirection(DcMotor.Direction.FORWARD);
        backRightDrive.setDirection(DcMotor.Direction.FORWARD);

        // Send telemetry message to signify robot waiting;
        telemetry.addData("Status", "Ready to run");    //
        telemetry.update();

        // Wait for the game to start (driver presses START)
        waitForStart();


        // Drive forward for 1 seconds at half motor power
        frontLeftDrive.setPower(-.5);
        frontRightDrive.setPower(-.5);
        sleep(600);
        frontLeftDrive.setPower(0);
        frontRightDrive.setPower(0);
        sleep(500);
        // LAUNCH
        launcher.chipMotor.setVelocity(TARGET_RPM);

        launcher.lServo.setPower(.1);
        launcher.rServo.setPower(-.1);
        sleep(8000);
        launcher.lServo.setPower(0);
        launcher.rServo.setPower(0);
        launcher.chipMotor.setVelocity(0);
        sleep(100);

        frontRightDrive.setPower(.6);
        backRightDrive.setPower(-.6);
        sleep(2000);
        frontRightDrive.setPower(0);
        backRightDrive.setPower(0);




    }
}
