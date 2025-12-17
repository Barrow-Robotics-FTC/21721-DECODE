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

@Autonomous(name = "BLUE - LM4 pedro test", group = "Autonomous")
@Configurable // Panels
public class BLUE_PedroLM4Test extends OpMode {

    private TelemetryManager panelsTelemetry; // Panels Telemetry instance
    public Follower follower; // Pedro Pathing follower instance
    private int pathState; // Current autonomous path state (state machine)

    @Override
    public void init() {
        panelsTelemetry = PanelsTelemetry.INSTANCE.getTelemetry();

        follower = Constants.createFollower(hardwareMap);
        follower.setStartingPose(new Pose(72, 8, Math.toRadians(90)));

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

        public static PathChain starttocollect1;
        public static PathChain collect1;
        public static PathChain score1;
        public static PathChain score1tocollect2;
        public static PathChain collect2;
        public static PathChain score2;
        public static PathChain score2tocollect3;
        public static PathChain collect3;
        public static PathChain score3;

        public Paths(Follower follower) {
            starttocollect1 = follower
                    .pathBuilder()
                    .addPath(
                            new BezierCurve(
                                    new Pose(22.000, 125.000),
                                    new Pose(45.000, 103.500),
                                    new Pose(48.000, 84.000)
                            )
                    )
                    .setLinearHeadingInterpolation(Math.toRadians(145), Math.toRadians(180))
                    .build();

            collect1 = follower
                    .pathBuilder()
                    .addPath(
                            new BezierLine(new Pose(48.000, 84.000), new Pose(16.000, 84.000))
                    )
                    .setTangentHeadingInterpolation()
                    .build();

            score1 = follower
                    .pathBuilder()
                    .addPath(
                            new BezierCurve(
                                    new Pose(16.000, 84.000),
                                    new Pose(62.000, 87.000),
                                    new Pose(22.000, 125.000)
                            )
                    )
                    .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(145))
                    .build();

            score1tocollect2 = follower
                    .pathBuilder()
                    .addPath(
                            new BezierLine(new Pose(22.000, 125.000), new Pose(48.000, 59.000))
                    )
                    .setLinearHeadingInterpolation(Math.toRadians(145), Math.toRadians(180))
                    .build();

            collect2 = follower
                    .pathBuilder()
                    .addPath(
                            new BezierLine(new Pose(48.000, 59.000), new Pose(16.000, 59.000))
                    )
                    .setTangentHeadingInterpolation()
                    .build();

            score2 = follower
                    .pathBuilder()
                    .addPath(
                            new BezierCurve(
                                    new Pose(16.000, 59.000),
                                    new Pose(77.000, 70.000),
                                    new Pose(22.000, 125.000)
                            )
                    )
                    .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(145))
                    .build();

            score2tocollect3 = follower
                    .pathBuilder()
                    .addPath(
                            new BezierLine(new Pose(22.000, 125.000), new Pose(48.000, 36.000))
                    )
                    .setLinearHeadingInterpolation(Math.toRadians(145), Math.toRadians(180))
                    .build();

            collect3 = follower
                    .pathBuilder()
                    .addPath(
                            new BezierLine(new Pose(48.000, 36.000), new Pose(16.000, 36.000))
                    )
                    .setTangentHeadingInterpolation()
                    .build();

            score3 = follower
                    .pathBuilder()
                    .addPath(
                            new BezierCurve(
                                    new Pose(16.000, 36.000),
                                    new Pose(64.000, 68.000),
                                    new Pose(22.000, 125.000)
                            )
                    )
                    .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(145))
                    .build();
        }
    }

    public int autonomousPathUpdate() {
        switch (pathState) {
            case 0:
                // score preloaded ---------------------
                setPathState(1);
                break;
            case 1:
                follower.followPath(Paths.starttocollect1);
                break;
            case 2:

                if(!follower.isBusy()) {

                    // INTAKE RUN LOGIC HERE --------------------------------------------
                    follower.followPath(Paths.collect1,true);
                    setPathState(3);

                }
                break;
            case 3:
                if(!follower.isBusy()) {
                    // INTAKE OFF LOGIC HERE --------------------------------------------
                    follower.followPath(Paths.score1,true);
                    setPathState(4);
                }
                break;
            case 4:

                if(!follower.isBusy()) {
                    // LAUNCHER LOGIC HERE-------------------------------------
                    // make some sort of launcher.isBusy function to wait until launcher is done launching------------------------------------

                    follower.followPath(Paths.score1tocollect2,true);
                    setPathState(5);
                }
                break;
            case 5:

                if(!follower.isBusy()) {
                    //INTAKE RUN LOGIC ----------------------------------------
                    follower.followPath(Paths.collect2,true);
                    setPathState(6);
                }
                break;
            case 6:

                if(!follower.isBusy()) {
                    //INTAKE STOP LOGIC -----------------------------------
                    follower.followPath(Paths.score2, true);
                    setPathState(7);
                }
                break;
            case 7:

                if(!follower.isBusy()) {
                    // LAUNCHER LOGIC HERE-------------------------------------
                    // make some sort of launcher.isBusy function to wait until launcher is done launching------------------------------------
                    follower.followPath(Paths.score2tocollect3, true);
                    setPathState(8);
                }
                break;

            case 8:
                if(!follower.isBusy()) {
                    //INTAKE RUN LOGIC------------------------------------------------------------------
                    follower.followPath(Paths.collect3, true);
                    setPathState(9);
                }
                break;

            case 9:
                if(!follower.isBusy()) {
                    //INTAKE OFF LOGIC ------------------------------------------------------
                    follower.followPath(Paths.score3, true);
                    setPathState(10);
                }
                break;

            case 10:
                if(!follower.isBusy()) {
                    // LAUNCHER LOGIC HERE-------------------------------------
                    // make some sort of launcher.isBusy function to wait until launcher is done launching------------------------------------
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