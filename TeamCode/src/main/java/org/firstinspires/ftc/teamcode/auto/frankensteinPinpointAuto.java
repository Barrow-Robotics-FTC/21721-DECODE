package org.firstinspires.ftc.teamcode.auto;

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import static com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior.BRAKE;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;




@Autonomous(name="frankenstein pinpoint auto", group="Autonomous")
public class frankensteinPinpointAuto extends LinearOpMode {

    /* Declare OpMode members. */
    private DcMotor         fLDrive   = null;
    private DcMotor         fRDrive  = null;
    private DcMotor         bLDrive   = null;
    private DcMotor         bRDrive  = null;
    private Boolean robotIsBusy;

    GoBildaPinpointDriver pinpoint;


    private ElapsedTime     runtime = new ElapsedTime();
    static final double     driveSpeed             = 0.8;
    static final double     turnSpeed              = 0.2;
    static final double     posTolerance              = 2.0;


    public class Poses {

            Pose2D startPose = new Pose2D(DistanceUnit.INCH, 0, 0, AngleUnit.DEGREES, 0);
            Pose2D nextPosExample = new Pose2D(DistanceUnit.INCH, 10, 0, AngleUnit.DEGREES, 0);


    }

    public void configurePinpoint(){

        // basic pinpoint-constants stuff

        pinpoint.setOffsets(-7.5, -1, DistanceUnit.INCH);

        pinpoint.setEncoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);

        pinpoint.setEncoderDirections(GoBildaPinpointDriver.EncoderDirection.FORWARD, GoBildaPinpointDriver.EncoderDirection.REVERSED);

        pinpoint.resetPosAndIMU();
    }

    @Override
    public void runOpMode() {

        // Initialize the drive system variables.
        fLDrive  = hardwareMap.get(DcMotor.class, "fLDrive");
        fRDrive = hardwareMap.get(DcMotor.class, "fRDrive");
        bLDrive  = hardwareMap.get(DcMotor.class, "bLDrive");
        bRDrive = hardwareMap.get(DcMotor.class, "bRDrive");
        pinpoint = hardwareMap.get(GoBildaPinpointDriver.class, "pinpoint");



        // To drive forward, most robots need the motor on one side to be reversed, because the axles point in opposite directions.
        // When run, this OpMode should start both motors driving forward. So adjust these two lines based on your first test drive.
        // Note: The settings here assume direct drive on left and right wheels.  Gear Reduction or 90 Deg drives may require direction flips
        fLDrive.setDirection(DcMotor.Direction.REVERSE);
        fRDrive.setDirection(DcMotor.Direction.FORWARD);
        bLDrive.setDirection(DcMotor.Direction.REVERSE);
        bRDrive.setDirection(DcMotor.Direction.FORWARD);

        fLDrive.setZeroPowerBehavior(BRAKE);
        fRDrive.setZeroPowerBehavior(BRAKE);
        bLDrive.setZeroPowerBehavior(BRAKE);
        bRDrive.setZeroPowerBehavior(BRAKE);

        configurePinpoint();



        // Send telemetry message to indicate successful Encoder reset

        telemetry.update();

        // Wait for the game to start (driver presses START)
        waitForStart();

        // Step through each leg of the path,
        // Note: Reverse movement is obtained by setting a negative distance (not speed)


        telemetry.addData("Path", "Complete");
        telemetry.update();
        sleep(1000);  // pause to display final telemetry message.
    }


    public void Drive(double driveSpeed, Pose2D target) {
        robotIsBusy = true;
        double targetPosX = target.getX(DistanceUnit.INCH);
        double targetPosY = target.getY(DistanceUnit.INCH);


        fLDrive.setPower(driveSpeed);
        fRDrive.setPower(driveSpeed);
        bRDrive.setPower(driveSpeed);
        bRDrive.setPower(driveSpeed);

        if ((pinpoint.getPosition()) > ) {

        }






    }
}
