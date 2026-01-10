package org.firstinspires.ftc.teamcode.auto;

import static com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior.BRAKE;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.utils.Launcher;
import org.firstinspires.ftc.teamcode.utils.Ramp;




/* hello, my name is Mikey and im head of software for team 21721 :D
 * If you are reading this it means that I gave you our sample autonomous, so here is a basic rundown:
 * At the start of the match, you earn 3 extra points for leaving a launch area at the beginning of autonomous. This doesnt seem like alot, but
 * it helps push our alliance to getting the movement ranked point, one of my team's first meet goals.
 */


@Autonomous(name="BLUE LAUNCH - LM4 auto", group="Autonomous")
public class launchLM3AutoBlue extends LinearOpMode {

    /* Declare OpMode members. */
    private DcMotor         backRightDrive   = null;
    private DcMotor         backLeftDrive  = null;
    private DcMotor         frontRightDrive  = null;
    private DcMotor         frontLeftDrive  = null;
    private DcMotor         intakeFront  = null;


    private Launcher launcher;

    double intakePower = -.8;
    int TARGET_RPM = 1600;
    final int RPM_TOLERANCE = 50;
    final int RPM_IN_RANGE_TIME = 250;
    private int AUTO_TARGET_RPM = 1000;
    private final ElapsedTime feedTimer = new ElapsedTime();




    @Override
    public void runOpMode() {


        launcher = new Launcher(hardwareMap);
        launcher.update(true);
        Ramp Ramp = new Ramp(hardwareMap);


        // change these to your motor names
        backRightDrive  = hardwareMap.get(DcMotor.class, "bRDrive");
        frontRightDrive = hardwareMap.get(DcMotor.class, "fRDrive");
        backLeftDrive  = hardwareMap.get(DcMotor.class, "bLDrive");
        frontLeftDrive  = hardwareMap.get(DcMotor.class, "fLDrive");
        intakeFront = hardwareMap.get(DcMotor.class, "intakeFront");


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



        sleep(50);
        Ramp.setPosAgainst();
        sleep(1500);
        // LAUNCH
        launcher.chipMotor.setVelocity(AUTO_TARGET_RPM);
        sleep(3000);
        intakeFront.setPower(intakePower);
        launcher.rServo.setPower(-.5);
        launcher.lServo.setPower(.5);
        sleep(4000);
        launcher.lServo.setPower(0);
        launcher.rServo.setPower(0);
        launcher.chipMotor.setVelocity(0);
        sleep(250);

        frontLeftDrive.setPower(-.2);
        frontRightDrive.setPower(-.2);
        backLeftDrive.setPower(-.2);
        backRightDrive.setPower(-.2);
        sleep(1000);
        frontLeftDrive.setPower(0);
        frontRightDrive.setPower(0);
        backLeftDrive.setPower(0);
        backRightDrive.setPower(-0);
        sleep(250);

        frontLeftDrive.setPower(.3);
        frontRightDrive.setPower(-.3);
        backLeftDrive.setPower(-.3);
        backRightDrive.setPower(.3);
        sleep(1500);
        frontLeftDrive.setPower(0);
        backLeftDrive.setPower(0);




    }
}