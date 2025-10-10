package org.firstinspires.ftc.teamcode.auto;

// FTC SDK


// FTC SDK
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.util.ElapsedTime;

// Panels
import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.telemetry.TelemetryManager;
import com.bylazar.telemetry.PanelsTelemetry;

// Pedro Pathing
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.follower.Follower;
import com.pedropathing.paths.PathChain;
import com.pedropathing.geometry.Pose;

// Local helper files
import org.firstinspires.ftc.teamcode.util.Launcher;
import org.firstinspires.ftc.teamcode.utils.AllianceSelector;
import org.firstinspires.ftc.teamcode.utils.AprilTag;

// Java
import java.util.Arrays;
import java.util.List;

@Autonomous(name = "skeleton for auto", group = "Autonomous")
@Configurable // Panels
@SuppressWarnings("FieldCanBeLocal") // Stop Android Studio from bugging about variables being predefined
public class skeleAuto extends LinearOpMode {
    // Initialize elapsed timer
    private final ElapsedTime runtime = new ElapsedTime();
    List<Paths.StateMachine.State> stateList = Arrays.asList( // Add autonomous states for the state machine here
            Paths.StateMachine.State.GRAB_ARTIFACT,
            Paths.StateMachine.State.RUN_OVER_ARTIFACT,
            Paths.StateMachine.State.SCORE_ARTIFACT
    );


    // Other variables
    private AllianceSelector.Alliance alliance; // Alliance of the robot
    private Paths.StateMachine stateMachine; // Custom autonomous state machine
    private Launcher launcher; // Custom launcher class
    private AprilTag aprilTag; // Custom April Tag class
    private Pose currentPose; // Current pose of the robot
    public Follower follower; // Pedro Pathing follower
    private TelemetryManager panelsTelemetry; // Panels telemetry
    private Paths.StateMachine.State pathState; // Current state machine value
    private AprilTag.Pattern targetPattern; // Target pattern determined by obelisk April Tag


    
// a place to put your intake and shooting functions


    @Override
    public void runOpMode() {
        // Initialize Panels telemetry
        panelsTelemetry = PanelsTelemetry.INSTANCE.getTelemetry();

        // Initialize Pedro Pathing follower
        follower = Constants.createFollower(hardwareMap);
        follower.setStartingPose(Poses.startPose);

        // Create state machine and initialize
        stateMachine = new Paths.StateMachine();
        stateMachine.init(follower, stateList, launcher);

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
            currentPose = follower.getPose(); // Update the current pose
            
            // Run the state machine update loop
            pathState = stateMachine.update();


            // Log to Panels and driver station (custom log function)
            panelsTelemetry.debug("Elapsed", runtime.toString());
            panelsTelemetry.debug("Path State", pathState);
            panelsTelemetry.debug("X", currentPose.getX());
            panelsTelemetry.debug("Y", currentPose.getY());
            panelsTelemetry.debug("Heading", currentPose.getHeading());
            panelsTelemetry.update(telemetry); // Update Panels and driver station after logging
        }
    }



    static class Poses {
        // Poses
        public static Pose startPose = new Pose(72, 8, Math.toRadians(90));
        public static Object PGPRunOver;
        private static final Pose scorePose = new Pose(24, 120, Math.toRadians(130)); // Scoring Pose of our robot. It is facing the goal at a 130 degree angle.
        private static final Pose PPGPose = new Pose(100, 83.5, Math.toRadians(0)); // Highest (First Set) of Artifacts from the Spike Mark.
        private static final Pose PGPPose = new Pose(100, 59.5, Math.toRadians(0)); // Middle (Second Set) of Artifacts from the Spike Mark.
        private static final Pose GPPPose = new Pose(100, 35.5, Math.toRadians(0)); // Lowest (Third Set) of Artifacts from the Spike Mark.

        private static final Pose GPPRunOver = new Pose(130, 35.5, Math.toRadians(0)); // to the right of the lowest (third Set) of Artifacts from the Spike Mark.
        
        
    }

    static class Paths {
        private static PathChain grabArtifact;
        private static PathChain runOverArtifact;
        private static PathChain scoreArtifact;


        public static void build(Follower follower, AprilTag.Pattern pattern) {
            // Select the correct intake pose based on pattern
            Pose patternIntakePose = Poses.PPGPose;
            if (pattern == AprilTag.Pattern.PGP) {
                patternIntakePose = Poses.PGPPose;

            } else if (pattern == AprilTag.Pattern.GPP) {
                patternIntakePose = Poses.GPPPose;
            }

            
            Pose runOverPose = Poses.PPGPose;
            Object patternRunOverPose;
            if (pattern == AprilTag.Pattern.PGP) {
                patternRunOverPose = Poses.PGPRunOver;

            } else if (pattern == AprilTag.Pattern.GPP) {
                patternRunOverPose = Poses.GPPRunOver;
            }

            grabArtifact = follower.pathBuilder()
                    .addPath(new BezierLine(Poses.startPose, patternIntakePose))
                    .setLinearHeadingInterpolation(Poses.startPose.getHeading(), patternIntakePose.getHeading())
                    .build();
           
            runOverArtifact = follower.pathBuilder()
                    .addPath(new BezierLine(Poses.startPose, runOverPose))
                    .setLinearHeadingInterpolation(Poses.startPose.getHeading(), runOverPose.getHeading())
                    .build();

            scoreArtifact = follower.pathBuilder()
                    .addPath(new BezierLine(patternIntakePose, Poses.scorePose))
                    .setLinearHeadingInterpolation(patternIntakePose.getHeading(), Poses.scorePose.getHeading())
                    .build();

           
        }

        static class StateMachine {
        private Follower follower; // Pedro Pathing follower (passed in init)
        private List<State> states; // List of states provided in init
        private Launcher launcher;
        private int statesIndex; // Current index in states
        private State currentState; // current state (only used in update for cleaner code)

        public enum State {
            INTAKE,
            LAUNCH,
            GRAB_ARTIFACT,
            SCORE_ARTIFACT,
            RUN_OVER_ARTIFACT
        }

        private void nextState() {
            statesIndex += 1;
        }

        public void init(Follower pedro_follower, List<State> state_list, Launcher launcher_instance) {
            follower = pedro_follower;
            launcher = launcher_instance;
            states = state_list;
            statesIndex = 0;
        }

        public State update() {
            currentState = states.get(statesIndex);
            if (!follower.isBusy()) { // If the follower is running, don't run the state machine
                switch (currentState) {
                    case INTAKE:
                        // Put intake logic here
                        nextState();
                        break;
                    case LAUNCH:
                        /*
                        launcher.update() will run the launcher state machine to launch 3 artifacts.
                        The state will become IDLE when all 3 artifacts are launched.
                         */
                        if (launcher.update() == Launcher.State.IDLE) {
                            nextState();
                        }
                        break;
                
                    case GRAB_ARTIFACT:
                        follower.followPath(Paths.grabArtifact);
                        nextState(); // Calling this after follower.followPath will wait until the follower is completed to run the next state
                        break;
                    case RUN_OVER_ARTIFACT:
                        follower.followPath(Paths.runOverArtifact);
                        nextState();
                        break;
                    case SCORE_ARTIFACT:
                        follower.followPath(Paths.scoreArtifact);
                        nextState();
                        break;
                    
                }
            }
            return currentState;
        }
    }
    }

    //below is the state machine or each pattern




    
  
}
