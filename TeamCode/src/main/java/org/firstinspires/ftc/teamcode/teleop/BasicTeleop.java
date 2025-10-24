package org.firstinspires.ftc.teamcode.teleop;

// FTC SDK
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

// Panels
import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.telemetry.TelemetryManager;
import com.bylazar.telemetry.PanelsTelemetry;

// Pedro Pathing
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
import com.pedropathing.paths.HeadingInterpolator;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.follower.Follower;
import com.pedropathing.paths.PathChain;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.Path;

// Java
import java.util.function.Supplier;

// get helper files
import org.firstinspires.ftc.teamcode.utils.Launcher;
import org.firstinspires.ftc.teamcode.utils.AllianceSelector;


/*
*    ---------------------------------------------- Gamepad Map for TeleOp ------------------------------------------------------
*        Left Stick X: Strafe
*        Left Stick Y: Forward
*        Right Stick X: Turn
*        left bumper: Intake
*        Right bumper: launch 3 artifacts
*        A (press): Start auto drive
*        B (press): Stop auto drive mid-path
*    ----------------------------------------------------------------------------------------------------------------------------
*/


@TeleOp(name = "assisted TeleOp", group = "Opmode")
@Configurable // Use Panels
@SuppressWarnings("FieldCanBeLocal") // Stop Android Studio from bugging about variables being predefined
public class BasicTeleop extends LinearOpMode {
    private final double slowModeMultiplier = 0.5; // Multiplier for slow mode speed
    private final double nonSlowModeMultiplier = 8; // Multiplier for normal driving speed
    private final boolean brakeMode = true; // Whether the motors should break on stop (recommended)
    private final boolean robotCentric = true; // True for robot centric driving, false for field centric
    public static Pose startingPose = new Pose(); // Starting pose of the robot for TeleOp

    private final ElapsedTime runtime = new ElapsedTime();
    private Follower follower; // Pedro pathing follower
    private Pose currentPose; // Current pose of the robot
    private boolean automatedDrive; // Is Pedro Pathing driving?
    private TelemetryManager panelsTelemetry; // Panels telemetry
    private final boolean slowMode = false; // Slow down the robot

    private AllianceSelector.Alliance alliance; // Alliance of the robot
    private Launcher launcher;


    // Class to store poses (Poses.poseName)
    static class Poses {
        // Poses (red alliance)
        public static Pose scoreClose = new Pose(24, 120, Math.toRadians(140)); // pretty much up against the goal facing it
    }
    
    private PathChain getPathToPose(Pose pose) {
        // Flip the pose if the alliance is blue
        if (alliance == AllianceSelector.Alliance.BLUE) {
            pose = pose.mirror();
        }

        // Return PathChain
        return follower.pathBuilder()
                .addPath(new BezierLine(follower.getPose(), pose))
                .setLinearHeadingInterpolation(follower.getHeading(), pose.getHeading())
                .build();
    }

    // Custom logging function to support telemetry and Panels
    private void log(String caption, Object... text) {
        if (text.length == 1) {
            telemetry.addData(caption, text[0]);
            panelsTelemetry.debug(caption + ": " + text[0]);
        } else if (text.length >= 2) {
            StringBuilder message = new StringBuilder();
            for (int i = 0; i < text.length; i++) {
                message.append(text[i]);
                if (i < text.length - 1) message.append(" ");
            }
            telemetry.addData(caption, message.toString());
            panelsTelemetry.debug(caption + ": " + message);
        }
    }

    private void intakeArtifacts() {
        // Put your intake logic here
        return;
    }

    private void shootArtifacts() {
        launcher.update(true); // Put your shooting logic here
        return;
    }

    


    @Override
    public void runOpMode() {
        
        follower = Constants.createFollower(hardwareMap);
        follower.setStartingPose(startingPose);
        follower.update();

        //new instance of launcher
        Launcher launcher = new Launcher(hardwareMap);

        // Get alliance variable from Blackboard
        // alliance = (AllianceSelector.Alliance) blackboard.getOrDefault("alliance", AllianceSelector.Alliance.RED);

        // Initialize Panels telemetry
        panelsTelemetry = PanelsTelemetry.INSTANCE.getTelemetry();

        // Log completed initialization to Panels and driver station (custom log function)
        log("Status", "Initialized");
        telemetry.update(); // Update driver station after logging

        // Wait for the TeleOp period to start (driver presses START)
        waitForStart();
        runtime.reset();

        // Start with TeleOp (manual) drive
        follower.startTeleopDrive(brakeMode);

        while (opModeIsActive()) {
            // Update Pedro Pathing and Panels every iteration
            follower.update();
            panelsTelemetry.update();
            currentPose = follower.getPose();

            if (!automatedDrive) {
                // Send gamepad inputs to Pedro Pathing for driving
                // Make the last parameter false for field-centric
                follower.setTeleOpDrive(
                        -gamepad1.left_stick_y * (slowMode ? slowModeMultiplier : nonSlowModeMultiplier),
                        -gamepad1.left_stick_x * (slowMode ? slowModeMultiplier : nonSlowModeMultiplier),
                        -gamepad1.right_stick_x * (slowMode ? slowModeMultiplier : nonSlowModeMultiplier),
                        robotCentric
                );
            }

            // Use A to follow the path
            if (gamepad1.aWasPressed()) {
                follower.followPath(getPathToPose(Poses.scoreClose)); // "getPathToPose" just means to go from the current position to any pose - "Poses.scoreClose" gets the close scoring pose from the poses class
                automatedDrive = true;
            }

            // Stop automated following if the follower is done or the driver presses B
            if (automatedDrive && (gamepad1.bWasPressed() || !follower.isBusy())) {
                follower.startTeleopDrive(brakeMode); // Restart the manual TeleOp drive
                automatedDrive = false;
            }


            

            // Left bumper: intake artifacts
            if (gamepad1.right_bumper) {
                launcher.launch();
            }

            // Right bumper: shoot artifacts


            // Log to Panels and driver station (custom log function)
            log("X: ", currentPose.getX());
            log("Y: ", currentPose.getY());
            log("Heading: ", currentPose.getHeading());
            telemetry.update(); // Update the driver station after logging
        }
    }
}
