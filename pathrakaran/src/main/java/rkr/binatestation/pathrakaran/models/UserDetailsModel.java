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
    private long userId;
    private String name;
    private String address;
    private String postcode;
    private String email;
    private String mobile;
    private String image;
    private String userType;
    private double latitude;
    private double longitude;

    public UserDetailsModel(long userId, String name, String address, String postcode, String email, String mobile, String image, String userType, double latitude, double longitude) {
        this.userId = userId;
        this.name = name;
        this.address = address;
        this.postcode = postcode;
        this.email = email;
        this.mobile = mobile;
        this.image = image;
        this.userType = userType;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    private UserDetailsModel(Parcel in) {
        userId = in.readLong();
        name = in.readString();
        address = in.readString();
        postcode = in.readString();
        email = in.readString();
        mobile = in.readString();
        image = in.readString();
        userType = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(userId);
        dest.writeString(name);
        dest.writeString(address);
        dest.writeString(postcode);
        dest.writeString(email);
        dest.writeString(mobile);
        dest.writeString(image);
        dest.writeString(userType);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
    }
}
