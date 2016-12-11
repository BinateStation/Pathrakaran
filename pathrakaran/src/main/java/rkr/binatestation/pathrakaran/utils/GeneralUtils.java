package rkr.binatestation.pathrakaran.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;

import java.io.File;

import rkr.binatestation.pathrakaran.R;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static rkr.binatestation.pathrakaran.utils.Constants.REQUEST_EXTERNAL_STORAGE;
import static rkr.binatestation.pathrakaran.utils.Constants.REQUEST_LOCATION_PERMISSION;
import static rkr.binatestation.pathrakaran.utils.Constants.REQUEST_READ_CONTACTS;


/**
 * Created by RKR on 27-01-2016.
 * GeneralUtils.
 */
public class GeneralUtils {

    private static final String TAG = "GeneralUtils";
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

    public static boolean mayRequestLocation(final Activity activity, View view) {
        Log.d(TAG, "mayRequestLocation() called");
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (ActivityCompat.checkSelfPermission(activity, ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(activity, ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (activity.shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
            Snackbar.make(view, R.string.location_permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            activity.requestPermissions(new String[]{ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION}, REQUEST_LOCATION_PERMISSION);
                        }
                    }).show();
        } else {
            activity.requestPermissions(new String[]{ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        }
        return false;
    }

    public static boolean mayRequestExternalMemory(final Activity activity, View view) {
        Log.d(TAG, "mayRequestLocation() called");
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (ActivityCompat.checkSelfPermission(activity, WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (activity.shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)) {
            Snackbar.make(view, R.string.external_storage_permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            activity.requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STORAGE);
                        }
                    }).show();
        } else {
            activity.requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STORAGE);
        }
        return false;
    }

    public static boolean mayRequestContacts(final Activity activity, View view) {
        Log.d(TAG, "mayRequestContacts() called");
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (activity.checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (activity.shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(view, R.string.contacts_permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            activity.requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    }).show();
        } else {
            activity.requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }


}
