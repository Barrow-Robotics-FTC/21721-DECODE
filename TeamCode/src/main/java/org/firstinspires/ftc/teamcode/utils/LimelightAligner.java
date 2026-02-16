package org.firstinspires.ftc.teamcode.utils;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.robotcore.util.Range;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * Utility class to handle AprilTag alignment logic using PD control.
 */
public class LimelightAligner {
    // Default Gains - tune these as needed
    private double kP_rot = 0.025;
    private double kD_rot = 0.001;
    private double kP_dist = 0.04;
    
    private double lastErrorRot = 0;
    private ElapsedTime timer = new ElapsedTime();
    private double lastTime = 0;

    /**
     * Container for the calculated drive powers.
     */
    public static class AlignmentResult {
        public double forward;
        public double rotate;
        public boolean targetFound;
        
        public AlignmentResult(double f, double r, boolean found) {
            this.forward = f;
            this.rotate = r;
            this.targetFound = found;
        }
    }

    /**
     * Calculates the required drive powers to align with an AprilTag.
     * @param result Latest LLResult from the limelight
     * @param targetX Desired TX (0 is centered)
     * @param targetDist Desired distance from the tag
     * @return AlignmentResult containing powers and visibility status
     */
    public AlignmentResult calculateAlignment(LLResult result, double targetX, double targetDist) {
        if (result == null || !result.isValid()) {
            reset();
            return new AlignmentResult(0, 0, false);
        }

        double currentTime = timer.seconds();
        double deltaTime = currentTime - lastTime;
        
        // --- Rotation PD ---
        double errorRot = targetX - result.getTx();
        double dTerm = (deltaTime > 0) ? ((errorRot - lastErrorRot) / deltaTime) * kD_rot : 0;
        double rotatePower = Range.clip((errorRot * kP_rot) + dTerm, -0.5, 0.5);
        
        // --- Distance P ---
        double currentDist = getDistanceFromAT(result.getTa());
        double distError = currentDist - targetDist;
        double forwardPower = Range.clip(distError * kP_dist, -0.5, 0.5);

        // Update state for D-term
        lastErrorRot = errorRot;
        lastTime = currentTime;

        return new AlignmentResult(forwardPower, rotatePower, true);
    }

    /**
     * Resets the internal PD state. Call this when stopping alignment.
     */
    public void reset() {
        lastErrorRot = 0;
        lastTime = timer.seconds();
    }

    /**
     * Estimates distance based on the area (ta) of the target.
     */
    public double getDistanceFromAT(double ta) {
        if (ta <= 0) return 0;
        // Formula derived from aprilTagLimeLightTest
        double scale = 326.0366;
        return (scale / ta);
    }

    // Getters/Setters for tuning
    public void setGains(double pRot, double dRot, double pDist) {
        this.kP_rot = pRot;
        this.kD_rot = dRot;
        this.kP_dist = pDist;
    }
}
