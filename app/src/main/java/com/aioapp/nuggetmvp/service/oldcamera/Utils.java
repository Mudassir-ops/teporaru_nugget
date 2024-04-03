package com.aioapp.nuggetmvp.service.oldcamera;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;

public class Utils {
    public static boolean isFrontCameraPresent(Context context) {
        boolean result = false;
        if (context.getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA_ANY)) {

            int numOfCameras = Camera.getNumberOfCameras();
            for (int i = 0; i < numOfCameras; i++) {
                CameraInfo info = new CameraInfo();
                Camera.getCameraInfo(i, info);
                if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }
}