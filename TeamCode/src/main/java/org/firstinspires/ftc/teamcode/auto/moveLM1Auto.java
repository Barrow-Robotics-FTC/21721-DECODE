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


@Autonomous(name="MOVE - LM1 auto", group="Autonomous")
public class moveLM1Auto extends LinearOpMode {

    /* Declare OpMode members. */
    private DcMotor         backRightDrive   = null;
    private DcMotor         backLeftDrive  = null;
    private DcMotor         frontRightDrive  = null;
    private DcMotor         frontLeftDrive  = null;

    private Launcher launcher;

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
        sleep(1500);
        frontLeftDrive.setPower(0);
        frontRightDrive.setPower(0);


        while (opModeIsActive() && launcher.getState() != Launcher.State.IDLE) {
            launcher.launch();
            telemetry.addData("Launcher State", launcher.getState());
            telemetry.addData("Launches", launcher.getLaunches());
            telemetry.update();
        }
    }
}
