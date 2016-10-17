package rkr.larc.mynewspaperagent.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import java.io.File;

import rkr.larc.mynewspaperagent.R;

/**
 * Created by RKR on 27-01-2016.
 * Util.
 */
public class Util {

    private static final String TAG = "Util";
    private static final String captureImagePath = Environment.getExternalStorageDirectory().toString() +
            File.separator + "Pathrakaran" + File.separator + "Images" + File.separator;

    public static boolean isValidEmail(CharSequence target) {
        return target != null && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public static String getCaptureImagePath() {
        File file = new File(captureImagePath);
        if (file.exists()) {
            return captureImagePath;
        } else {
            if (file.mkdirs()) {
                return captureImagePath;
            } else {
                return Environment.getExternalStorageDirectory().toString() + File.separator;
            }
        }
    }

    public static void alert(Context context, String title, String message) {
        Log.d(TAG, "alert() called with: title = [" + title + "], message = [" + message + "]");
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }


}
