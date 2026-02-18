package org.firstinspires.ftc.teamcode.auto;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.telemetry.TelemetryManager;
import com.bylazar.telemetry.PanelsTelemetry;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
import org.firstinspires.ftc.teamcode.utils.LauncherV2;
import org.firstinspires.ftc.teamcode.utils.Intake;
import org.firstinspires.ftc.teamcode.utils.Ramp;

import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.follower.Follower;
import com.pedropathing.paths.PathChain;
import com.pedropathing.geometry.Pose;

@Autonomous(name = "BLUE - FAR: 8 ball", group = "Autonomous")
@Configurable // Panels
public class blueFar8ballAuto extends OpMode {
    private TelemetryManager panelsTelemetry; // Panels Telemetry instance
    public Follower follower; // Pedro Pathing follower instance
    private int pathState; // Current autonomous path state (state machine)
    private Paths paths; // Paths defined in the Paths class
    public LauncherV2 launcher;
    private Intake intake;
    int targetLaunches = 1;
    private Ramp ramp; // Renamed to lower case for consistency


    @Override
    public void init() {
        panelsTelemetry = PanelsTelemetry.INSTANCE.getTelemetry();

        // FIX: Removed 'Ramp', 'Intake', and 'LauncherV2' type prefixes to assign to class fields
        ramp = new Ramp(hardwareMap);
        intake = new Intake(hardwareMap);
        launcher = new LauncherV2(hardwareMap);

        follower = Constants.createFollower(hardwareMap);
        follower.setStartingPose(new Pose(56, 8, Math.toRadians(90)));

        paths = new Paths(follower); // Build paths

        panelsTelemetry.debug("Status", "Initialized");
        panelsTelemetry.update(telemetry);

        launcher.setTargetLaunches(targetLaunches);
        launcher.TARGET_RPM = launcher.FAR_FAR_TARGET_RPM;
    }

    @Override
    public void loop() {
        follower.update(); // Update Pedro Pathing

        pathState = autonomousPathUpdate(); // Update autonomous state machine

        // Log values to Panels and Driver Station
        panelsTelemetry.debug("Path State", pathState);
        panelsTelemetry.debug("X", follower.getPose().getX());
        panelsTelemetry.debug("Y", follower.getPose().getY());
        panelsTelemetry.debug("Heading", follower.getPose().getHeading());
        panelsTelemetry.update(telemetry);
    }


    public static class Paths {
        public static PathChain toScorePreload;
        public static PathChain toSpikeOne;
        public static PathChain collectSpikeOne;
        public static PathChain toScoreOne;
        public static PathChain toSpikeTwo;
        public static PathChain collectSpikeTwo;
        public static PathChain awayFromGate;
        public static PathChain toScore2;

        public Paths(Follower follower) {
            toScorePreload = follower.pathBuilder().addPath(
                            new BezierLine(
                                    new Pose(88.000, 8.000),

                                    new Pose(84.000, 12.000)
                            )
                    ).setLinearHeadingInterpolation(Math.toRadians(90), Math.toRadians(60))

                    .build();

            toSpikeOne = follower.pathBuilder().addPath(
                            new BezierCurve(
                                    new Pose(84.000, 12.000),
                                    new Pose(84.000, 36.000),
                                    new Pose(96.000, 36.000)
                            )
                    ).setLinearHeadingInterpolation(Math.toRadians(60), Math.toRadians(0))

                    .build();

            collectSpikeOne = follower.pathBuilder().addPath(
                            new BezierLine(
                                    new Pose(96.000, 36.000),

                                    new Pose(132.000, 36.000)
                            )
                    ).setConstantHeadingInterpolation(Math.toRadians(0))

                    .build();

            toScoreOne = follower.pathBuilder().addPath(
                            new BezierLine(
                                    new Pose(132.000, 36.000),

                                    new Pose(84.000, 12.000)
                            )
                    ).setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(60))

                    .build();

            toSpikeTwo = follower.pathBuilder().addPath(
                            new BezierLine(
                                    new Pose(84.000, 12.000),

                                    new Pose(96.000, 60.000)
                            )
                    ).setLinearHeadingInterpolation(Math.toRadians(60), Math.toRadians(0))

                    .build();

            collectSpikeTwo = follower.pathBuilder().addPath(
                            new BezierLine(
                                    new Pose(96.000, 60.000),

                                    new Pose(126.000, 60.000)
                            )
                    ).setTangentHeadingInterpolation()

                    .build();

            awayFromGate = follower.pathBuilder().addPath(
                            new BezierLine(
                                    new Pose(126.000, 60.000),

                                    new Pose(108.000, 60.000)
                            )
                    ).setConstantHeadingInterpolation(Math.toRadians(0))

                    .build();

            toScore2 = follower.pathBuilder().addPath(
                            new BezierLine(
                                    new Pose(108.000, 60.000),

                                    new Pose(84.000, 12.000)
                            )
                    ).setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(60))

                    .build();
        }
    }



    public int autonomousPathUpdate() {
        switch (pathState) {
            case 0:
                setPathState(1);
                break;

            case 1:
                ramp.setPosFar();
                launcher.launchV2();
                follower.followPath(redFar8ballAuto.Paths.toScorePreload,true );
                setPathState(2);
                break;

            case 2:
                if(!follower.isBusy()   &&    !launcher.isBusy()  ) {


                    follower.followPath(Paths.toSpikeOne,true );
                    setPathState(3);
                }
                break;

            case 3:
                if(!follower.isBusy()) {
                    //SLOW DOWN TO .2 POWER

                    intake.in();
                    follower.followPath(Paths.collectSpikeOne,.3, true);
                    setPathState(4);
                }
                break;

            case 4:
                if(!follower.isBusy()) {
                    intake.off();
                    follower.followPath(Paths.toScoreOne, true);
                    launcher.launchV2();
                    setPathState(5);
                }
                break;


            case 5:
                if(!follower.isBusy()   &&  !launcher.isBusy()  ) {
                    follower.followPath(Paths.toSpikeTwo, true);
                    setPathState(6);
                }
                break;

            case 6:
                if(!follower.isBusy()) {
                    Intake.in();

                    follower.followPath(Paths.collectSpikeTwo,.3, true);
                    setPathState(7);
                }
                break;

            case 7:
                if(!follower.isBusy()) {
                    Intake.off();
                    follower.followPath(Paths.awayFromGate, true);
                    setPathState(8);
                }
                break;

            case 8:
                if(!follower.isBusy()) {
                    follower.followPath(Paths.toScore2, true);
                    setPathState(9);
                }
                break;

            case 9:
                if(!follower.isBusy()) {

                    launcher.launchV2();
                    setPathState(10);
                }
                break;

            case 10:
                if(!launcher.isBusy()) {

                    setPathState(-1);
                }
                break;




        }
        return pathState;
    }
    public void setPathState(int pState) {
        pathState = pState;
    }
}
