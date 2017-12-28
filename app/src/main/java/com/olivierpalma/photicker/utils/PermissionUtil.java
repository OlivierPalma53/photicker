package com.olivierpalma.photicker.utils;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;

import com.olivierpalma.photicker.R;
import com.olivierpalma.photicker.views.MainActivity;

/**
 * Created by olivierpalma on 28/12/2017.
 */

public class PermissionUtil {
    public static final int CAMERA_PERMISSION = 0;

    public static boolean hasCameraPermission(Context context) {
        if(needToAskPermission()){
            return ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    private static boolean needToAskPermission() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    public static void askCameraPermission(final MainActivity mainActivity) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(mainActivity, Manifest.permission.CAMERA)) {
            new AlertDialog.Builder(mainActivity)
            .setMessage(mainActivity.getString(R.string.permission_camera_explanation))
            .setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    ActivityCompat.requestPermissions(mainActivity, new String[] {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PermissionUtil.CAMERA_PERMISSION);
                }
            }).show();

        } else {
            ActivityCompat.requestPermissions(mainActivity, new String[] {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PermissionUtil.CAMERA_PERMISSION);
        }
    }
}
