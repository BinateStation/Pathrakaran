package rkr.binatestation.pathrakaran.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by RKR on 11/12/2016.
 * UserDetailsModel.
 */

public class UserDetailsModel implements Parcelable {
    public static final Parcelable.Creator<UserDetailsModel> CREATOR = new Parcelable.Creator<UserDetailsModel>() {
        @Override
        public UserDetailsModel createFromParcel(Parcel in) {
            return new UserDetailsModel(in);
        }

        @Override
        public UserDetailsModel[] newArray(int size) {
            return new UserDetailsModel[size];
        }
    };
    private String mUserId;
    private String mName;
    private String mEmail;
    private String mMobile;
    private String mImage;
    private String mUserType;
    private double mLatitude;
    private double mLongitude;

    public UserDetailsModel(String userId, String name, String email, String mobile, String image, String userType, double latitude, double longitude) {
        this.mUserId = userId;
        this.mName = name;
        this.mEmail = email;
        this.mMobile = mobile;
        this.mImage = image;
        this.mUserType = userType;
        this.mLatitude = latitude;
        this.mLongitude = longitude;
    }

    private UserDetailsModel(Parcel in) {
        mUserId = in.readString();
        mName = in.readString();
        mEmail = in.readString();
        mMobile = in.readString();
        mImage = in.readString();
        mUserType = in.readString();
        mLatitude = in.readDouble();
        mLongitude = in.readDouble();
    }

    public String getUserId() {
        return mUserId;
    }

    public void setUserId(String userId) {
        this.mUserId = userId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        this.mEmail = email;
    }

    public String getMobile() {
        return mMobile;
    }

    public void setMobile(String mobile) {
        this.mMobile = mobile;
    }

    public String getImage() {
        return mImage;
    }

    public void setImage(String image) {
        this.mImage = image;
    }

    public String getUserType() {
        return mUserType;
    }

    public void setUserType(String userType) {
        this.mUserType = userType;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public void setLatitude(double latitude) {
        this.mLatitude = latitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public void setLongitude(double longitude) {
        this.mLongitude = longitude;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mUserId);
        dest.writeString(mName);
        dest.writeString(mEmail);
        dest.writeString(mMobile);
        dest.writeString(mImage);
        dest.writeString(mUserType);
        dest.writeDouble(mLatitude);
        dest.writeDouble(mLongitude);
    }
}
