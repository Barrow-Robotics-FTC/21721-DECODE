package org.firstinspires.ftc.teamcode.auto;

import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

@Autonomous(name = "BLUE - no strafe pedro", group = "Autonomous")
@Configurable // Panels
public class frankensteinPedroAuto extends OpMode {

    private TelemetryManager panelsTelemetry; // Panels Telemetry instance
    public Follower follower; // Pedro Pathing follower instance
    private int pathState; // Current autonomous path state (state machine)

    @Override
    public void init() {
        panelsTelemetry = PanelsTelemetry.INSTANCE.getTelemetry();

        follower = Constants.createFollower(hardwareMap);
        follower.setStartingPose(new Pose(22, 143, Math.toRadians(90)));
        new Paths(follower);

        panelsTelemetry.debug("Status", "Initialized");
        panelsTelemetry.update(telemetry);
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
        public static PathChain startToAway;
        public static PathChain AwayToSpike;
        public static PathChain rotateForSpike;
        public static PathChain spikeCollect;
        public static PathChain collectToScore;
        public static PathChain rotateForLever;
        public static PathChain pushLever;
        public static PathChain awayLever;

        public Paths(Follower follower) {
            startToAway = follower.pathBuilder().addPath(
                            new BezierLine(
                                    new Pose(22.000, 126.000),

                                    new Pose(45.000, 126.000)
                            )
                    ).setLinearHeadingInterpolation(Math.toRadians(143), Math.toRadians(270))

                    .build();

            AwayToSpike = follower.pathBuilder().addPath(
                            new BezierLine(
                                    new Pose(45.000, 126.000),

                                    new Pose(45.000, 85.000)
                            )
                    ).setConstantHeadingInterpolation(Math.toRadians(270))

                    .build();

            rotateForSpike = follower.pathBuilder().addPath(
                            new BezierLine(
                                    new Pose(45.000, 85.000),

                                    new Pose(45.000, 83.000)
                            )
                    ).setLinearHeadingInterpolation(Math.toRadians(270), Math.toRadians(180))

                    .build();

            spikeCollect = follower.pathBuilder().addPath(
                            new BezierLine(
                                    new Pose(45.000, 83.000),

                                    new Pose(16.000, 83.000)
                            )
                    ).setConstantHeadingInterpolation(Math.toRadians(180))

                    .build();

            collectToScore = follower.pathBuilder().addPath(
                            new BezierLine(
                                    new Pose(16.000, 83.000),

                                    new Pose(60.000, 83.000)
                            )
                    ).setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(139))

                    .build();

            rotateForLever = follower.pathBuilder().addPath(
                            new BezierLine(
                                    new Pose(60.000, 83.000),

                                    new Pose(60.000, 71.000)
                            )
                    ).setLinearHeadingInterpolation(Math.toRadians(139), Math.toRadians(0))

                    .build();

            pushLever = follower.pathBuilder().addPath(
                            new BezierLine(
                                    new Pose(60.000, 71.000),

                                    new Pose(16.000, 71.000)
                            )
                    ).setConstantHeadingInterpolation(Math.toRadians(0))

                    .build();
            awayLever = follower.pathBuilder().addPath(
                            new BezierLine(
                                    new Pose(16, 71.000),

                                    new Pose(40.000, 71.000)
                            )
                    ).setConstantHeadingInterpolation(Math.toRadians(0))

                    .build();
        }
    }


    public int autonomousPathUpdate() {
        switch (pathState) {
            case 0:
                // make some sort of launcher.isBusy function to wait until launcher is done launching------------------------------------
                setPathState(1);
                break;
            case 1:
                follower.followPath(Paths.startToAway);
                break;
            case 2:

                if(!follower.isBusy()) {


                    follower.followPath(Paths.AwayToSpike,true);
                    setPathState(3);

                }
                break;
            case 3:
                if(!follower.isBusy()) {

                    follower.followPath(Paths.rotateForSpike,true);

                    setPathState(4);
                }
                break;
            case 4:

                if(!follower.isBusy()) {
                    // INTAKE ON LOGIC HERE --------------------------------------------

                    follower.followPath(Paths.spikeCollect,true);
                    setPathState(5);
                }
                break;
            case 5:

                if(!follower.isBusy()) {
                    // INTAKE OFF LOGIC HERE --------------------------------------------
                    // make some sort of launcher.isBusy function to wait until launcher is done launching------------------------------------
                    follower.followPath(Paths.collectToScore,true);
                    setPathState(6);
                }
                break;
            case 6:

                if(!follower.isBusy()) {

                    follower.followPath(Paths.rotateForLever, true);
                    setPathState(7);
                }
                break;
            case 7:

                if(!follower.isBusy()) {

                    follower.followPath(Paths.pushLever, true);
                    setPathState(8);
                }
                break;
            case 8:

                if(!follower.isBusy()) {

                    follower.followPath(Paths.awayLever, true);
                    setPathState(9);
                }
                break;
            case 9:

                if(!follower.isBusy()) {

                    setPathState(-1);
                }
                break;


        }
        return pathState;
    }
    /** These change the states of the paths and actions. It will also reset the timers of the individual switches **/
    public void setPathState(int pState) {
        pathState = pState;
        // pathTimer.resetTimer();
    }
}