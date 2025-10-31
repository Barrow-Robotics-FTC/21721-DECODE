package org.firstinspires.ftc.teamcode.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import org.firstinspires.ftc.teamcode.utils.Launcher;

@Autonomous(name = "LM1 RED AUTO", group = "Autonomous")
public class LM1RedApril extends LinearOpMode {

    // 1. DEFINE THE STATES FOR THE STATE MACHINE
    private enum State {
        SCANNING_FOR_TAG,
        MOVING_TO_TAG,
        LAUNCHING,
        STRAFING,
        DONE
    }

    // The current state of the robot, starting with SCANNING
    private State currentState = State.SCANNING_FOR_TAG;

    // 2. DEFINE CONSTANTS
    private static final int RED_GOAL_TAG_ID = 4; // IMPORTANT: Set this to your Red Goal Tag ID (e.g., 4, 10)
    private static final double DESIRED_DISTANCE = 12.0; // How close to get to the tag (inches)
    private static final double STRAFE_DURATION = 5.0; // How long to strafe (seconds)

    // Drive constants for approaching the tag
    private static final double SPEED_GAIN = 0.02;
    private static final double STRAFE_GAIN = 0.015;
    private static final double TURN_GAIN = 0.01;
    private static final double MAX_AUTO_SPEED = 0.5;
    private static final double MAX_AUTO_STRAFE = 0.5;
    private static final double MAX_AUTO_TURN = 0.3;

    // 3. DECLARE HARDWARE & UTILITY OBJECTS
    // Hardware
    private DcMotor frontLeft, frontRight, backLeft, backRight;
    private Launcher launcher;

    // AprilTag Vision
    private AprilTagProcessor aprilTag;
    private VisionPortal visionPortal;
    private AprilTagDetection desiredTag = null; // Used to hold the data for the detected tag

    // Timers
    private final ElapsedTime stateTimer = new ElapsedTime();

    @Override
    public void runOpMode() {
        // --- INITIALIZATION (runs once when you press INIT) ---

        // 4. MAP HARDWARE
        frontLeft = hardwareMap.get(DcMotor.class, "frontLeft");
        frontRight = hardwareMap.get(DcMotor.class, "frontRight");
        backLeft = hardwareMap.get(DcMotor.class, "backLeft");
        backRight = hardwareMap.get(DcMotor.class, "backRight");

        // Reverse motors if necessary (depends on your robot build)
        frontRight.setDirection(DcMotor.Direction.REVERSE);
        backRight.setDirection(DcMotor.Direction.REVERSE);

        // Initialize helper classes
        launcher = new Launcher(hardwareMap);

        // Initialize AprilTag vision
        initAprilTag();

        telemetry.addData("Status", "Initialization Complete");
        telemetry.update();

        waitForStart();

        // --- AUTONOMOUS LOOP (runs after you press PLAY) ---
        while (opModeIsActive() && currentState != State.DONE) {

            // The core of the state machine
            switch (currentState) {
                case SCANNING_FOR_TAG:
                    telemetry.addData("State", "Scanning for Tag ID: " + RED_GOAL_TAG_ID);
                    scanForAprilTags(); // Look for the tag

                    // If we found the tag, switch to the next state
                    if (desiredTag != null) {
                        telemetry.addLine("Tag Found! Switching to MOVING_TO_TAG.");
                        currentState = State.MOVING_TO_TAG;
                    }
                    break;

                case MOVING_TO_TAG:
                    telemetry.addData("State", "Moving to Tag");
                    scanForAprilTags(); // Keep looking at the tag to update our position

                    if (desiredTag != null) {
                        // Calculate movement errors based on tag's position
                        double rangeError = (desiredTag.ftcPose.range - DESIRED_DISTANCE);
                        double headingError = desiredTag.ftcPose.bearing;
                        double yawError = desiredTag.ftcPose.yaw;

                        // Calculate motor powers based on errors
                        double drive = Range.clip(rangeError * SPEED_GAIN, -MAX_AUTO_SPEED, MAX_AUTO_SPEED);
                        double turn = Range.clip(-headingError * TURN_GAIN, -MAX_AUTO_TURN, MAX_AUTO_TURN);
                        double strafe = Range.clip(-yawError * STRAFE_GAIN, -MAX_AUTO_STRAFE, MAX_AUTO_STRAFE);

                        // Move the robot using these calculated powers
                        moveRobot(drive, strafe, turn);

                        // If we are close enough, stop and move to the next state
                        if (Math.abs(rangeError) < 0.2) { // Within 0.5 inches
                            stopRobot();
                            currentState = State.LAUNCHING;
                        }
                    } else {
                        // We lost sight of the tag, so stop and go back to scanning
                        stopRobot();
                        currentState = State.SCANNING_FOR_TAG;
                    }
                    break;

                case LAUNCHING:
                    telemetry.addData("State", "Launching");
                    launcher.launch(); // Call your launcher's method

                    // Wait a moment for the launch to complete (optional, but good practice)
                    sleep(500); // Wait 0.5 seconds

                    // Reset the timer and switch to the next state
                    stateTimer.reset();
                    currentState = State.STRAFING;
                    break;

                case STRAFING:
                    telemetry.addData("State", "Strafing Right");
                    telemetry.addData("Timer", "%.1f / %.1f sec", stateTimer.seconds(), STRAFE_DURATION);

                    if (stateTimer.seconds() < STRAFE_DURATION) {
                        // Strafe left at 50% power
                        moveRobot(0, -0.5, 0);
                    } else {
                        // Time's up, stop and finish the OpMode
                        stopRobot();
                        currentState = State.DONE;
                    }
                    break;

                case DONE:
                    telemetry.addData("State", "Done");
                    stopRobot();
                    break;
            }
            telemetry.update();
        }
    }

    // --- HELPER METHODS ---

    /**
     * Initializes the AprilTag processor and Vision Portal.
     */
    private void initAprilTag() {
        aprilTag = new AprilTagProcessor.Builder()
                .setDrawAxes(true)
                .setDrawCubeProjection(false)
                .setDrawTagID(true)
                .setDrawTagOutline(true)
                .build();

        visionPortal = new VisionPortal.Builder()
                .addProcessor(aprilTag)
                .setCamera(hardwareMap.get(WebcamName.class, "webcam"))
                .setCameraResolution(new android.util.Size(640, 480))
                .build();
    }

    /**
     * Scans for AprilTags and updates the 'desiredTag' variable if the target ID is found.
     */
    private void scanForAprilTags() {
        desiredTag = null; // Reset before each scan
        for (AprilTagDetection detection : aprilTag.getDetections()) {
            if (detection.metadata != null && detection.id == RED_GOAL_TAG_ID) {
                desiredTag = detection; // We found our specific tag
                telemetry.addLine("Found Target Tag!");
                telemetry.addData("  Range", "%.2f in", detection.ftcPose.range);
                telemetry.addData("  Bearing", "%.2f deg", detection.ftcPose.bearing);
                telemetry.addData("  Yaw", "%.2f deg", detection.ftcPose.yaw);
                return; // Exit the loop early
            }
        }
    }

    /**
     * Sets power to all four drive motors.
     * Positive values: forward, strafe right, turn right.
     */
    public void moveRobot(double drive, double strafe, double turn) {
        // Mecanum drive calculations
        double frontLeftPower = drive + strafe + turn;
        double frontRightPower = drive - strafe - turn;
        double backLeftPower = drive - strafe + turn;
        double backRightPower = drive + strafe - turn;

        // Normalize wheel powers to be within [-1.0, 1.0]
        double max = Math.max(1.0, Math.abs(frontLeftPower));
        max = Math.max(max, Math.abs(frontRightPower));
        max = Math.max(max, Math.abs(backLeftPower));
        max = Math.max(max, Math.abs(backRightPower));

        frontLeft.setPower(frontLeftPower / max);
        frontRight.setPower(frontRightPower / max);
        backLeft.setPower(backLeftPower / max);
        backRight.setPower(backRightPower / max);
    }

    /**
     * Stops all drive motors.
     */
    public void stopRobot() {
        moveRobot(0, 0, 0);
    }
}