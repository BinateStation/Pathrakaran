package rkr.binatestation.pathrakkaran.network.models;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

import rkr.binatestation.pathrakkaran.R;


/**
 * Created by RKR on 04-04-2017.
 * ErrorModel
 */

public class ErrorModel implements Parcelable {
    public static final Creator<ErrorModel> CREATOR = new Creator<ErrorModel>() {
        public ErrorModel createFromParcel(Parcel in) {
            return new ErrorModel(in);
        }

        public ErrorModel[] newArray(int size) {
            return new ErrorModel[size];
        }
    };

    private static final String KEY_ERROR_TITLE = "error_title";
    private static final String KEY_ERROR_MESSAGE = "error_message";

    private String errorTitle;
    private String errorMessage;

    public ErrorModel(String errorTitle, String errorMessage) {
        this.errorTitle = errorTitle;
        this.errorMessage = errorMessage;
    }

    public ErrorModel(JSONObject jsonObject) {
        this.errorTitle = jsonObject.optString(KEY_ERROR_TITLE);
        this.errorMessage = jsonObject.optString(KEY_ERROR_MESSAGE);
    }

    private ErrorModel(Parcel in) {
        this.errorTitle = in.readString();
        this.errorMessage = in.readString();
    }

    public static ErrorModel getNoConnectionError(Context context) {
        return new ErrorModel(
                context.getString(R.string.default_no_networks_error_title),
                context.getString(R.string.default_no_networks_error_message)
        );
    }

    public static ErrorModel getTimeoutError(Context context) {
        return new ErrorModel(
                context.getString(R.string.default_unknown_error_title),
                context.getString(R.string.default_unknown_error_message)
        );
    }

    public static ErrorModel getNetworkError(Context context) {
        return new ErrorModel(
                context.getString(R.string.default_unknown_error_title),
                context.getString(R.string.default_unknown_error_message)
        );
    }

    public static ErrorModel getUnKnownError(Context context) {
        return new ErrorModel(
                context.getString(R.string.default_unknown_error_title),
                context.getString(R.string.default_unknown_error_message)
        );
    }

    public static ErrorModel newInstance(Context context, String message) {
        return new ErrorModel(
                context.getString(android.R.string.dialog_alert_title),
                message
        );
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(errorTitle);
        out.writeString(errorMessage);
    }

    public String getErrorTitle() {
        return errorTitle;
    }

    public void setErrorTitle(String errorTitle) {
        this.errorTitle = errorTitle;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
