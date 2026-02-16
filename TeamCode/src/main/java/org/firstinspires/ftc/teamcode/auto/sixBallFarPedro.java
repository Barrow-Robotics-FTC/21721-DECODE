package org.firstinspires.ftc.teamcode.auto;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.telemetry.TelemetryManager;
import com.bylazar.telemetry.PanelsTelemetry;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
import org.firstinspires.ftc.teamcode.utils.LauncherV2;
import org.firstinspires.ftc.teamcode.utils.Intake;

import com.pedropathing.geometry.BezierLine;
import com.pedropathing.follower.Follower;
import com.pedropathing.paths.PathChain;
import com.pedropathing.geometry.Pose;

@Autonomous(name = "six Ball Far Pedro", group = "Autonomous")
@Configurable // Panels
public class sixBallFarPedro extends OpMode {
    private TelemetryManager panelsTelemetry; // Panels Telemetry instance
    public Follower follower; // Pedro Pathing follower instance
    private int pathState; // Current autonomous path state (state machine)
    private Paths paths; // Paths defined in the Paths class
    private LauncherV2 launcher; // Paths defined in the Paths class
    private Intake intake; // Paths defined in the Paths class

    @Override
    public void init() {
        panelsTelemetry = PanelsTelemetry.INSTANCE.getTelemetry();

        Intake intake = new Intake(hardwareMap);
        LauncherV2 launcher = new LauncherV2(hardwareMap);
        launcher.update(true);

        follower = Constants.createFollower(hardwareMap);
        follower.setStartingPose(new Pose(72, 8, Math.toRadians(90)));

        paths = new Paths(follower); // Build paths

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
        public static PathChain toPreload;
        public static PathChain toSpike;
        public static PathChain collect;
        public static PathChain toScore;

        public Paths(Follower follower) {
            toPreload = follower.pathBuilder().addPath(
                            new BezierLine(
                                    new Pose(56.000, 8.000),

                                    new Pose(60.000, 12.000)
                            )
                    ).setLinearHeadingInterpolation(Math.toRadians(90), Math.toRadians(120))

                    .build();

            toSpike = follower.pathBuilder().addPath(
                            new BezierLine(
                                    new Pose(60.000, 12.000),

                                    new Pose(48.000, 36.000)
                            )
                    ).setLinearHeadingInterpolation(Math.toRadians(120), Math.toRadians(180))

                    .build();

            collect = follower.pathBuilder().addPath(
                            new BezierLine(
                                    new Pose(48.000, 36.000),

                                    new Pose(12.000, 36.000)
                            )
                    ).setConstantHeadingInterpolation(Math.toRadians(180))

                    .build();

            toScore = follower.pathBuilder().addPath(
                            new BezierLine(
                                    new Pose(12.000, 36.000),

                                    new Pose(60.000, 12.000)
                            )
                    ).setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(120))

                    .build();
        }
    }


    public int autonomousPathUpdate() {
        switch (pathState) {
            case 0:

                setPathState(1);
                break;

            case 1:
                follower.followPath(sixBallFarPedro.Paths.toPreload);
                if(!follower.isBusy()) {
                    launcher.launchV2();

                    if(!launcher.isBusy()) {
                        follower.followPath(sixBallFarPedro.Paths.toSpike);
                        setPathState(2);

                    }

                }
                break;

            case 2:

                if(!follower.isBusy()) {
                    Intake.in();
                    follower.followPath(sixBallFarPedro.Paths.collect);
                    setPathState(3);

                }
                break;
            case 3:

                if(!follower.isBusy()) {
                    Intake.off();
                    follower.followPath(sixBallFarPedro.Paths.toScore);
                    setPathState(4);

                }
                break;

            case 4:

                if(!follower.isBusy()) {
                    launcher.launchV2();

                    if(!launcher.isBusy()) {

                        setPathState(-1);

                    }

                }





        }
        return pathState;
    }
    public void setPathState(int pState) {
        pathState = pState;

    }
}