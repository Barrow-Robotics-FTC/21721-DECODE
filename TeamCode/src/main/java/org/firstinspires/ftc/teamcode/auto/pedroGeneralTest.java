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

@Autonomous(name = "pedro general test", group = "Autonomous")
@Configurable // Panels
public class pedroGeneralTest extends OpMode {

    private TelemetryManager panelsTelemetry; // Panels Telemetry instance
    public Follower follower; // Pedro Pathing follower instance
    private int pathState; // Current autonomous path state (state machine)

    @Override
    public void init() {
        panelsTelemetry = PanelsTelemetry.INSTANCE.getTelemetry();

        follower = Constants.createFollower(hardwareMap);
        follower.setStartingPose(new Pose(72, 8, Math.toRadians(90)));
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
        public static PathChain startToScorePre;
        public static PathChain scoreToTop;

        public Paths(Follower follower) {
            startToScorePre = follower.pathBuilder().addPath(
                            new BezierCurve(
                                    new Pose(56.000, 8.000),
                                    new Pose(34.000, 56.000),
                                    new Pose(71.000, 82.000)
                            )
                    ).setLinearHeadingInterpolation(Math.toRadians(90), Math.toRadians(140))

                    .build();

            scoreToTop = follower.pathBuilder().addPath(
                            new BezierLine(
                                    new Pose(71.000, 82.000),

                                    new Pose(45.000, 84.000)
                            )
                    ).setLinearHeadingInterpolation(Math.toRadians(140), Math.toRadians(180))

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
                follower.followPath(Paths.startToScorePre);
                break;
            case 2:

                if(!follower.isBusy()) {

                    // INTAKE RUN LOGIC HERE --------------------------------------------
                    follower.followPath(Paths.scoreToTop,true);
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