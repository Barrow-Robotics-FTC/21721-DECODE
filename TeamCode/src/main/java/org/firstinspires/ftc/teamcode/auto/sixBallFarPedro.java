package org.firstinspires.ftc.teamcode.auto;
import static java.lang.Thread.sleep;

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
        launcher.update(true);

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

                                    new Pose(60, 12)
                            )
                    ).setLinearHeadingInterpolation(Math.toRadians(90), Math.toRadians(120))

                    .build();

            toSpike = follower.pathBuilder().addPath(
                            new BezierLine(
                                    new Pose(60, 12),

                                    new Pose(48.000, 26)
                            )
                    ).setLinearHeadingInterpolation(Math.toRadians(120), Math.toRadians(180))

                    .build();

            collect = follower.pathBuilder().addPath(
                            new BezierLine(
                                    new Pose(48.000, 26),

                                    new Pose(15.000, 26)
                            )
                    ).setConstantHeadingInterpolation(Math.toRadians(180))

                    .build();

            toScore = follower.pathBuilder().addPath(
                            new BezierLine(
                                    new Pose(15, 26),

                                    new Pose(60, 12)
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
                ramp.setPosFar();
                launcher.launchV2();
                follower.followPath(sixBallFarPedro.Paths.toPreload,true );
                setPathState(2);
                break;

            case 2:
                if(!follower.isBusy()   &&    !launcher.isBusy()  ) {


                    follower.followPath(sixBallFarPedro.Paths.toSpike,true );
                    setPathState(3);
                }
                break;

            case 3:
                if(!follower.isBusy()) {
                    //SLOW DOWN TO .2 POWER

                    intake.in();
                    follower.followPath(sixBallFarPedro.Paths.collect, true);
                    setPathState(4);
                }
                break;

            case 4:
                if(!follower.isBusy()) {
                    intake.off();
                    follower.followPath(sixBallFarPedro.Paths.toScore, true);
                    setPathState(5);
                }
                break;


            case 5:
                if(!follower.isBusy()) {
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
