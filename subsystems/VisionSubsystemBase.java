package frc.team670.mustanglib.subsystems;

import java.util.Optional;
import org.photonvision.EstimatedRobotPose;
import org.photonvision.PhotonCamera;
import org.photonvision.PhotonPoseEstimator;
import org.photonvision.PhotonPoseEstimator.PoseStrategy;
import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.math.Pair;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.wpilibj.PowerDistribution;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


/**
 * Stores values off of NetworkTables for easy retrieval and gives them Listeners to update the
 * stored values as they are changed.
 */
public abstract class VisionSubsystemBase extends MustangSubsystemBase {

    protected PhotonCameraWrapper[] cameras;
    private PowerDistribution pd;
    // protected double visionCapTime;
    // private boolean hasTarget;

    private boolean ledsTurnedOn;

    private boolean overriden;


    public VisionSubsystemBase(PowerDistribution pd, AprilTagFieldLayout visionFieldLayout,
            PhotonCamera[] cameras, Transform3d[] cameraOffsets) {
        this.pd = pd;
        PhotonCameraWrapper[] cams = new PhotonCameraWrapper[cameras.length];

        for (int i = 0; i < cameras.length; i++) {
            cams[i] = new PhotonCameraWrapper(cameras[i], cameraOffsets[i], visionFieldLayout);
        }
        this.cameras = cams;
    }

    public boolean hasTarget() {
        for (PhotonCameraWrapper pcw : cameras) {
            var targets = pcw.getCamera().getLatestResult().targets;
            if (targets.isEmpty())
                return false;
        }
        return true;
    }

    /**
     * @param estimatedRobotPose The current best guess at robot pose
     * @return an EstimatedRobotPose with an estimated pose, the timestamp, and targets used to
     *         create the estimate
     */
    public EstimatedRobotPose[] getEstimatedGlobalPose(Pose2d prevEstimatedRobotPose) {
        EstimatedRobotPose[] poses = new EstimatedRobotPose[cameras.length];
        for (int i = 0; i < poses.length; i++)
            poses[i] = cameras[i].getEstimatedGlobalPose(prevEstimatedRobotPose).orElse(null);

        return poses;
    }
    // public Pair<Pose2d, Double> getEstimatedGlobalPose(Pose2d prevEstimatedRobotPose) {
    // double avgX, avgY, avgDeg, avgTime;
    // avgX = avgY = avgDeg = avgTime = 0;
    // for (int i = 0; i < cameras.length; i++) {
    // EstimatedRobotPose p =
    // cameras[i].getEstimatedGlobalPose(prevEstimatedRobotPose).orElse(null);
    // if (p == null) return null;

    // avgX += p.estimatedPose.toPose2d().getX();
    // avgY += p.estimatedPose.toPose2d().getX();
    // avgDeg += p.estimatedPose.toPose2d().getRotation().getDegrees();
    // avgTime += p.timestampSeconds;
    // }
    // avgX /= cameras.length;
    // avgY /= cameras.length;
    // avgDeg /= cameras.length;
    // avgTime /= cameras.length;

    // return new Pair<>(new Pose2d(avgX, avgY, new Rotation2d(avgDeg)), avgTime);
    // }

    public void switchLEDS(boolean on, boolean override) {
        pd.setSwitchableChannel(on);
        ledsTurnedOn = on;
        overriden = override;
    }

    public void switchLEDS(boolean on) {
        switchLEDS(on, false);
    }

    public boolean LEDSOverriden() {
        return overriden;
    }

    public boolean LEDsTurnedOn() {
        return ledsTurnedOn;
    }

    public void testLEDS() {
        pd.setSwitchableChannel(SmartDashboard.getBoolean("LEDs on", true));
    }

    @Override
    public HealthState checkHealth() {
        return HealthState.GREEN;
    }

    public PhotonCamera[] getCameras() {
        PhotonCamera[] cameras = new PhotonCamera[this.cameras.length];
        for (int i = 0; i < cameras.length; i++) {
            cameras[i] = this.cameras[i].getCamera();
        }
        return cameras;
    }

    private class PhotonCameraWrapper {
        private PhotonCamera photonCamera;
        private PhotonPoseEstimator photonPoseEstimator;

        public PhotonCameraWrapper(PhotonCamera photonCamera, Transform3d robotToCam,
                AprilTagFieldLayout fieldLayout) {
            this.photonCamera = photonCamera;

            photonPoseEstimator = new PhotonPoseEstimator(fieldLayout,
                    PoseStrategy.LOWEST_AMBIGUITY, photonCamera, robotToCam);

            // @eric TODO: Comment out above code and uncomment code below to test multi tag pnp 
            // photonPoseEstimator = new PhotonPoseEstimator(fieldLayout,
            // PoseStrategy.MULTI_TAG_PNP,
            // photonCamera, robotToCam);
            // photonPoseEstimator.setMultiTagFallbackStrategy(PoseStrategy.LOWEST_AMBIGUITY);

        }

        /**
         * @param estimatedRobotPose The current best guess at robot pose
         * @return an EstimatedRobotPose with an estimated pose, the timestamp, and targets used to
         *         create the estimate
         */
        public Optional<EstimatedRobotPose> getEstimatedGlobalPose(Pose2d prevEstimatedRobotPose) {
            if (photonPoseEstimator == null) {
                // The field layout failed to load, so we cannot estimate poses.
                return Optional.empty();
            }
            photonPoseEstimator.setReferencePose(prevEstimatedRobotPose);
            return photonPoseEstimator.update();
        }

        public PhotonCamera getCamera() {
            return photonCamera;
        }
    }

}
