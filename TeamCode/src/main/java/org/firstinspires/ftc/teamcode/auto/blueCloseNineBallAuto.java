package org.firstinspires.ftc.teamcode.auto;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.telemetry.TelemetryManager;
import com.bylazar.telemetry.PanelsTelemetry;

import org.firstinspires.ftc.teamcode.auto.blueFarNineBallAuto;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
import org.firstinspires.ftc.teamcode.utils.Intake;
import org.firstinspires.ftc.teamcode.utils.LauncherV2;
import org.firstinspires.ftc.teamcode.utils.Ramp;

import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.follower.Follower;
import com.pedropathing.paths.PathChain;
import com.pedropathing.geometry.Pose;


@Autonomous(name = "BLUE - CLOSE: 9 ball", group = "Autonomous")
@Configurable // Panels
public class blueCloseNineBallAuto extends OpMode {
    private TelemetryManager panelsTelemetry; // Panels Telemetry instance
    public Follower follower; // Pedro Pathing follower instance
    private int pathState; // Current autonomous path state (state machine)
    private Paths paths; // Paths defined in the Paths class
    public LauncherV2 launcher;
    int targetLaunches = 1;

    private Intake intake;
    private Ramp ramp;

    @Override
    public void init() {
        panelsTelemetry = PanelsTelemetry.INSTANCE.getTelemetry();

        ramp = new Ramp(hardwareMap);
        intake = new Intake(hardwareMap);
        launcher = new LauncherV2(hardwareMap);

        follower = Constants.createFollower(hardwareMap);
        follower.setStartingPose(new Pose(23, 126, Math.toRadians(139)));

        paths = new Paths(follower); // Build paths

        launcher.TARGET_RPM = launcher.AUTO_FAR_TARGET_RPM;


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

        launcher.update(true);
    }


    public static class Paths {
        public static PathChain toScorePreload;
        public static PathChain toSpikeOne;
        public static PathChain collectSpikeOne;
        public static PathChain toScoreOne;
        public static PathChain toSpikeTwo;
        public static PathChain collectSpikeTwo;
        public static PathChain awayFromGate;
        public static PathChain toScoreTwo;
        public static PathChain toEnd;

        public Paths(Follower follower) {
            toScorePreload = follower.pathBuilder().addPath(
                            new BezierLine(
                                    new Pose(23.000, 126.000),

                                    new Pose(61.000, 83.000)
                            )
                    ).setConstantHeadingInterpolation(Math.toRadians(139))
                    .build();

            toSpikeOne = follower.pathBuilder().addPath(
                            new BezierLine(
                                    new Pose(62, 83.000),

                                    new Pose(48.000, 83.000)
                            )
                    ).setLinearHeadingInterpolation(Math.toRadians(139), Math.toRadians(180))

                    .build();

            collectSpikeOne = follower.pathBuilder().addPath(
                            new BezierLine(
                                    new Pose(48.000, 83.000),

                                    new Pose(16.000, 83.000)
                            )
                    ).setConstantHeadingInterpolation(Math.toRadians(180))

                    .build();

            toScoreOne = follower.pathBuilder().addPath(
                            new BezierLine(
                                    new Pose(16.000, 83.000),

                                    new Pose(62, 83)
                            )
                    ).setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(139))

                    .build();

            toSpikeTwo = follower.pathBuilder().addPath(
                            new BezierCurve(
                                    new Pose(60.000, 84.000),
                                    new Pose(60.000, 66.000),
                                    new Pose(48.000, 57.000)
                            )
                    ).setLinearHeadingInterpolation(Math.toRadians(139), Math.toRadians(180))

                    .build();

            collectSpikeTwo = follower.pathBuilder().addPath(
                            new BezierLine(
                                    new Pose(48.000, 57.000),

                                    new Pose(12.000, 57.000)
                            )
                    ).setTangentHeadingInterpolation()

                    .build();

            awayFromGate = follower.pathBuilder().addPath(
                            new BezierLine(
                                    new Pose(12.000, 57.000),

                                    new Pose(24.000, 57.000)
                            )
                    ).setConstantHeadingInterpolation(Math.toRadians(180))

                    .build();

            toScoreTwo = follower.pathBuilder().addPath(
                            new BezierCurve(
                                    new Pose(24.000, 57.000),
                                    new Pose(48.000, 66.000),
                                    new Pose(62, 82)
                            )
                    ).setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(138))

                    .build();

            toEnd = follower.pathBuilder().addPath(
                            new BezierLine(
                                    new Pose(60.000, 84.000),

                                    new Pose(60.000, 108.000)
                            )
                    ).setLinearHeadingInterpolation(Math.toRadians(138), Math.toRadians(180))

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
                follower.followPath(Paths.toScorePreload,true );
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
                    intake.in();

                    follower.followPath(Paths.collectSpikeTwo,.3, true);
                    setPathState(7);
                }
                break;

            case 7:
                if(!follower.isBusy()) {
                    intake.off();
                    follower.followPath(Paths.awayFromGate, true);
                    setPathState(8);
                }
                break;

            case 8:
                if(!follower.isBusy()) {
                    follower.followPath(Paths.toScoreTwo, true);
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
                    follower.followPath(Paths.toEnd, true);
                    setPathState(11);
                }
                break;
            case 11:
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
    