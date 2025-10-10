package org.firstinspires.ftc.teamcode.examples;

// FTC SDK

import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.robotcore.external.hardware.camera.BuiltinCameraDirection;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;
import org.firstinspires.ftc.teamcode.utils.Launcher;
import org.firstinspires.ftc.teamcode.utils.AllianceSelector;
import org.firstinspires.ftc.teamcode.utils.AprilTag;
import java.util.List;
import java.util.Arrays;

@Autonomous(name = "skeleton for auto", group = "Autonomous")
@Configurable // Panels
@SuppressWarnings("FieldCanBeLocal") // Stop Android Studio from bugging about variables being predefined
public class skeleAuto extends LinearOpMode {
    // Initialize elapsed timer
    private final ElapsedTime runtime = new ElapsedTime();
    List<StateMachine.State> stateList = Arrays.asList( // Add autonomous states for the state machine here
            StateMachine.State.GRAB_ARTIFACT,
            StateMachine.State.RUN_OVER_ARTIFACT,
            StateMachine.State.SCORE_ARTIFACT,
    );


    // Other variables
    private AllianceSelector.Alliance alliance; // Alliance of the robot
    private StateMachine stateMachine; // Custom autonomous state machine
    private Launcher launcher; // Custom launcher class
    private AprilTag aprilTag; // Custom April Tag class
    private Pose currentPose; // Current pose of the robot
    public Follower follower; // Pedro Pathing follower
    private TelemetryManager panelsTelemetry; // Panels telemetry
    private StateMachine.State pathState; // Current state machine value
    private AprilTag.Pattern targetPattern; // Target pattern determined by obelisk April Tag


    
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

// a place to put your intake and shooting functions


    @Override
    public void runOpMode() {
        // Initialize Panels telemetry
        panelsTelemetry = PanelsTelemetry.INSTANCE.getTelemetry();

        // Initialize Pedro Pathing follower
        follower = Constants.createFollower(hardwareMap);
        follower.setStartingPose(Poses.home);

        // Create state machine and initialize
        stateMachine = new StateMachine();
        stateMachine.init(follower, stateList, launcher, HUMAN_PLAYER_WAIT_TIME);

        // Create instance of launcher and initialize
        launcher = new Launcher();
        launcher.init(hardwareMap);

        // Crate instance of April Tag and initialize
        aprilTag = new AprilTag();
        aprilTag.init(hardwareMap);

        // Prompt the driver to select an alliance
        alliance = AllianceSelector.run(gamepad1, panelsTelemetry, telemetry);

        // Log completed initialization to Panels and driver station
        panelsTelemetry.debug("Status", "Initialized");
        panelsTelemetry.update(telemetry); // Update Panels and driver station after logging

        // Wait for the game to start (driver presses START)
        waitForStart();

        // Reset runtime timer
        runtime.reset();

        /*
        The April tag obelisk is randomized after the OpMode is initialized, so right after we run the OpMode
        we'll need to immediately scan the April Tag and then initialize our paths and state machine.
        */
        targetPattern = aprilTag.detectPattern();
        Paths.build(follower, targetPattern);

        while (opModeIsActive()) {
            // Update Pedro Pathing and Panels every iteration
            follower.update();
            panelsTelemetry.update();
            currentPose = follower.getPose(); // Update the current pose



            // Log to Panels and driver station (custom log function)
            log("Elapsed", runtime.toString());
            log("X", currentPose.getX());
            log("Y", currentPose.getY());
            log("Heading", currentPose.getHeading());
            telemetry.update(); // Update the driver station after logging
        }
    }



    static class Poses {
        // Poses
        public static Pose startPose = new Pose(72, 8, Math.toRadians(90));
        private final Pose scorePose = new Pose(24, 120, Math.toRadians(130)); // Scoring Pose of our robot. It is facing the goal at a 130 degree angle.
        private final Pose PPGPose = new Pose(100, 83.5, Math.toRadians(0)); // Highest (First Set) of Artifacts from the Spike Mark.
        private final Pose PGPPose = new Pose(100, 59.5, Math.toRadians(0)); // Middle (Second Set) of Artifacts from the Spike Mark.
        private final Pose GPPPose = new Pose(100, 35.5, Math.toRadians(0)); // Lowest (Third Set) of Artifacts from the Spike Mark.
        private final Pose PPGrunOver = new Pose(130, 35.5, Math.toRadians(0)); // to the right of the highest (first Set) of Artifacts from the Spike Mark.
        private final Pose PGPrunOver = new Pose(130, 35.5, Math.toRadians(0)); // to the right of the middle (second Set) of Artifacts from the Spike Mark.
        private final Pose GPPrunOver = new Pose(130, 35.5, Math.toRadians(0)); // to the right of the lowest (third Set) of Artifacts from the Spike Mark.
        
        
    }

    static class Paths {
        private PathChain grabArtifact;
        private PathChain runOverArtifact;
        private PathChain scoreArtifact;

        public static void build(Follower follower, AprilTag.Pattern pattern) {
            // Select the correct intake pose based on pattern
            Pose patternIntakePose = Poses.PPGArtifacts;
            if (pattern == AprilTag.Pattern.PGP) {
                patternIntakePose = Poses.PGPPose;

            } else if (pattern == AprilTag.Pattern.GPP) {
                patternIntakePose = Poses.GPPPose;
            }

            
            Pose runOverPose = Poses.PPGArtifacts;
            if (pattern == AprilTag.Pattern.PGP) {
                patternRunOverPose = Poses.PGPRunOver;

            } else if (pattern == AprilTag.Pattern.GPP) {
                patternRunOverPose = Poses.GPPRunOver;
            }

            grabArtifact = follower.pathBuilder()
                    .addPath(new BezierLine(Poses.start, patternIntakePose))
                    .setLinearHeadingInterpolation(Poses.home.getHeading(), patternIntakePose.getHeading())
                    .build();
           
            runOverArtifact = follower.pathBuilder()
                    .addPath(new BezierLine(Poses.start, runOverPose))
                    .setLinearHeadingInterpolation(Poses.home.getHeading(), runOverPose.getHeading())
                    .build();

            scoreArtifact = follower.pathBuilder()
                    .addPath(new BezierLine(patternIntakePose, Poses.scorePose))
                    .setLinearHeadingInterpolation(patternIntakePose.getHeading(), Poses.scorePose.getHeading())
                    .build();

           
        }
    }

    //below is the state machine or each pattern




    public void updateStateMachine() {
        switch (pathStateGPP) {
            case 0:
                // Move to the scoring position from the start position
                follower.followPath(grabGPP);
                setpathStateGPP(1); // Call the setter method
                break;
            case 1:
                // Wait until we have passed all path constraints
                if (!follower.isBusy()) {

                    // Move to the first artifact pickup location from the scoring position
                    follower.followPath(scoreGPP);
                    setpathStateGPP(-1); //set it to -1 so it stops the state machine execution
                }
                break;
        }
    }

    // Setter methods for pathState variables placed at the class level
    void setpathState(int newPathState) {
        this.pathState = newPathState;


    /*
     Manually set the camera gain and exposure.
     This can only be called AFTER calling initAprilTag(), and only works for Webcams;
    */
  
}
