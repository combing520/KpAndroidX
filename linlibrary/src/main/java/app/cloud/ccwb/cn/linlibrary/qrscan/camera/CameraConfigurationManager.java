package app.cloud.ccwb.cn.linlibrary.qrscan.camera;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.hardware.Camera;
import android.preference.PreferenceManager;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

import app.cloud.ccwb.cn.linlibrary.qrscan.Preferences;
import app.cloud.ccwb.cn.linlibrary.qrscan.camera.open.CameraFacing;
import app.cloud.ccwb.cn.linlibrary.qrscan.camera.open.OpenCamera;


/**
 * A class which deals with reading, parsing, and setting the camera parameters which are used to
 * configure the camera hardware.
 */
final class CameraConfigurationManager {

    private static final String TAG = "CameraConfiguration";

    private final Context context;
    private int cwNeededRotation;
    private int cwRotationFromDisplayToCamera;
    private Point screenResolution;
    private Point cameraResolution;
    private Point bestPreviewSize;
    private Point previewSizeOnScreen;

    CameraConfigurationManager(Context context) {
        this.context = context;
    }

    /**
     * Reads, one time, values from the camera that are needed by the app.
     */
    void initFromCameraParameters(OpenCamera camera) {
        Camera.Parameters parameters = camera.getCamera().getParameters();
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        int displayRotation = display.getRotation();
        int cwRotationFromNaturalToDisplay;
        if (displayRotation == Surface.ROTATION_0) {
            cwRotationFromNaturalToDisplay = 0;
        } else if (displayRotation == Surface.ROTATION_90) {
            cwRotationFromNaturalToDisplay = 90;
        } else if (displayRotation == Surface.ROTATION_180) {
            cwRotationFromNaturalToDisplay = 180;
        } else if (displayRotation == Surface.ROTATION_270) {
            cwRotationFromNaturalToDisplay = 270;
        } else {// Have seen this return incorrect values like -90
            if (displayRotation % 90 == 0) {
                cwRotationFromNaturalToDisplay = (360 + displayRotation) % 360;
            } else {
                throw new IllegalArgumentException("Bad rotation: " + displayRotation);
            }
        }
        int cwRotationFromNaturalToCamera = camera.getOrientation();
        // Still not 100% sure about this. But acts like we need to flip this:
        if (camera.getFacing() == CameraFacing.FRONT) {
            cwRotationFromNaturalToCamera = (360 - cwRotationFromNaturalToCamera) % 360;
        }

        cwRotationFromDisplayToCamera = (360 + cwRotationFromNaturalToCamera - cwRotationFromNaturalToDisplay) % 360;
        if (camera.getFacing() == CameraFacing.FRONT) {
            cwNeededRotation = (360 - cwRotationFromDisplayToCamera) % 360;
        } else {
            cwNeededRotation = cwRotationFromDisplayToCamera;
        }
        Point theScreenResolution = new Point();
        display.getSize(theScreenResolution);
        screenResolution = theScreenResolution;
        cameraResolution = CameraConfigurationUtils.findBestPreviewSizeValue(parameters, screenResolution);
        bestPreviewSize = CameraConfigurationUtils.findBestPreviewSizeValue(parameters, screenResolution);
        boolean isScreenPortrait = screenResolution.x < screenResolution.y;
        boolean isPreviewSizePortrait = bestPreviewSize.x < bestPreviewSize.y;

        if (isScreenPortrait == isPreviewSizePortrait) {
            previewSizeOnScreen = bestPreviewSize;
        } else {
            previewSizeOnScreen = new Point(bestPreviewSize.y, bestPreviewSize.x);
        }
    }

    void setDesiredCameraParameters(OpenCamera camera, boolean safeMode) {
        Camera theCamera = camera.getCamera();
        Camera.Parameters parameters = theCamera.getParameters();
        if (parameters == null) {
            return;
        }
        if (safeMode) {
        }
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        if (parameters.isZoomSupported()) {
            parameters.setZoom(parameters.getMaxZoom() / 10);
        }
        initializeTorch(parameters, prefs, safeMode);
        CameraConfigurationUtils.setFocus(
                parameters,
                prefs.getBoolean(Preferences.KEY_AUTO_FOCUS, true),
                prefs.getBoolean(Preferences.KEY_DISABLE_CONTINUOUS_FOCUS, true),
                safeMode);

        if (!safeMode) {
            if (prefs.getBoolean(Preferences.KEY_INVERT_SCAN, false)) {
                CameraConfigurationUtils.setInvertColor(parameters);
            }

            if (!prefs.getBoolean(Preferences.KEY_DISABLE_BARCODE_SCENE_MODE, true)) {
                CameraConfigurationUtils.setBarcodeSceneMode(parameters);
            }

            if (!prefs.getBoolean(Preferences.KEY_DISABLE_METERING, true)) {
                CameraConfigurationUtils.setVideoStabilization(parameters);
                CameraConfigurationUtils.setFocusArea(parameters);
                CameraConfigurationUtils.setMetering(parameters);
            }

            //SetRecordingHint to true also a workaround for low framerate on Nexus 4
            //https://stackoverflow.com/questions/14131900/extreme-camera-lag-on-nexus-4
            parameters.setRecordingHint(true);

        }

        parameters.setPreviewSize(bestPreviewSize.x, bestPreviewSize.y);

        theCamera.setParameters(parameters);

        theCamera.setDisplayOrientation(cwRotationFromDisplayToCamera);

        Camera.Parameters afterParameters = theCamera.getParameters();
        Camera.Size afterSize = afterParameters.getPreviewSize();
        if (afterSize != null && (bestPreviewSize.x != afterSize.width || bestPreviewSize.y != afterSize.height)) {
            bestPreviewSize.x = afterSize.width;
            bestPreviewSize.y = afterSize.height;
        }
    }

    Point getBestPreviewSize() {
        return bestPreviewSize;
    }

    Point getPreviewSizeOnScreen() {
        return previewSizeOnScreen;
    }

    Point getCameraResolution() {
        return cameraResolution;
    }

    Point getScreenResolution() {
        return screenResolution;
    }

    int getCWNeededRotation() {
        return cwNeededRotation;
    }

    boolean getTorchState(Camera camera) {
        if (camera != null) {
            Camera.Parameters parameters = camera.getParameters();
            if (parameters != null) {
                String flashMode = parameters.getFlashMode();
                return
                        Camera.Parameters.FLASH_MODE_ON.equals(flashMode) ||
                                Camera.Parameters.FLASH_MODE_TORCH.equals(flashMode);
            }
        }
        return false;
    }

    void setTorch(Camera camera, boolean newSetting) {
        Camera.Parameters parameters = camera.getParameters();
        doSetTorch(parameters, newSetting, false);
        camera.setParameters(parameters);
    }

    private void initializeTorch(Camera.Parameters parameters, SharedPreferences prefs, boolean safeMode) {
        boolean currentSetting = FrontLightMode.readPref(prefs) == FrontLightMode.ON;
        doSetTorch(parameters, currentSetting, safeMode);
    }

    private void doSetTorch(Camera.Parameters parameters, boolean newSetting, boolean safeMode) {
        CameraConfigurationUtils.setTorch(parameters, newSetting);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        if (!safeMode && !prefs.getBoolean(Preferences.KEY_DISABLE_EXPOSURE, true)) {
            CameraConfigurationUtils.setBestExposure(parameters, newSetting);
        }
    }

}