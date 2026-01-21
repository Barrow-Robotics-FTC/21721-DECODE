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
    GoBildaPinpointDriver pinpoint;


    private ElapsedTime     runtime = new ElapsedTime();
    static final double     DRIVE_SPEED             = 0.8;
    static final double     TURN_SPEED              = 0.3;

    public class Poses {

            Pose2D startPose = new Pose2D(DistanceUnit.INCH, 0, 0, AngleUnit.DEGREES, 0);


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
        telemetry.addData("Starting at", DRIVE_SPEED);

        telemetry.update();

        // Wait for the game to start (driver presses START)
        waitForStart();

        // Step through each leg of the path,
        // Note: Reverse movement is obtained by setting a negative distance (not speed)
        encoderDrive(DRIVE_SPEED,  48,  48, 5.0);  // S1: Forward 47 Inches with 5 Sec timeout
        encoderDrive(TURN_SPEED,   12, -12, 4.0);  // S2: Turn Right 12 Inches with 4 Sec timeout
        encoderDrive(DRIVE_SPEED, -24, -24, 4.0);  // S3: Reverse 24 Inches with 4 Sec timeout

        telemetry.addData("Path", "Complete");
        telemetry.update();
        sleep(1000);  // pause to display final telemetry message.
    }

    /*
     *  Method to perform a relative move, based on encoder counts.
     *  Encoders are not reset as the move is based on the current position.
     *  Move will stop if any of three conditions occur:
     *  1) Move gets to the desired position
     *  2) Move runs out of time
     *  3) Driver stops the OpMode running.
     */
    public void encoderDrive(double speed, double leftInches, double rightInches, double timeoutS) {
        int newLeftTarget;
        int newRightTarget;

        // Ensure that the OpMode is still active
        if (opModeIsActive()) {

            // Determine new target position, and pass to motor controller



            // Turn On RUN_TO_POSITION
            fLDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            fRDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            // reset the timeout time and start motion.
            runtime.reset();
            fLDrive.setPower(Math.abs(speed));
            fRDrive.setPower(Math.abs(speed));

            // keep looping while we are still active, and there is time left, and both motors are running.
            // Note: We use (isBusy() && isBusy()) in the loop test, which means that when EITHER motor hits
            // its target position, the motion will stop.  This is "safer" in the event that the robot will
            // always end the motion as soon as possible.
            // However, if you require that BOTH motors have finished their moves before the robot continues
            // onto the next step, use (isBusy() || isBusy()) in the loop test.
            while (opModeIsActive() &&
                    (runtime.seconds() < timeoutS) &&
                    (fLDrive.isBusy() && fRDrive.isBusy())) {

                // Display it for the driver.
                telemetry.addData("Running to",  " %7d :%7d", newLeftTarget,  newRightTarget);
                telemetry.addData("Currently at",  " at %7d :%7d",
                        fLDrive.getCurrentPosition(), fRDrive.getCurrentPosition());
                telemetry.update();
            }

            // Stop all motion;
            fLDrive.setPower(0);
            fRDrive.setPower(0);

            // Turn off RUN_TO_POSITION
            fLDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            fRDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

            sleep(250);   // optional pause after each move.
        }
    }
}
