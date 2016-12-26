package rkr.binatestation.pathrakaran.modules.profile;

import android.content.Context;
import android.text.TextUtils;

import rkr.binatestation.pathrakaran.models.UserDetailsModel;

/**
 * Created by RKR on 11/12/2016.
 * UserProfilePresenter.
 */

class UserProfilePresenter implements UserProfileListeners.PresenterListener {
    private UserProfileListeners.ViewListener mViewListener;
    private UserProfileListeners.InterActorListener mInterActorListener;

    UserProfilePresenter(UserProfileListeners.ViewListener viewListener) {
        mViewListener = viewListener;
        mInterActorListener = new UserProfileInterActor(this);
    }

    private boolean isViewLive() {
        return mViewListener != null;
    }

    private boolean isInterActorLive() {
        return mInterActorListener != null;
    }

    @Override
    public void getUserDetails(Context context, String userId) {
        if (isViewLive()) {
            mViewListener.showProgressView();
        }
        if (TextUtils.isEmpty(userId)) {
            if (isViewLive()) {
                mViewListener.hideProgressView();
            }
            return;
        }
        if ("0".equalsIgnoreCase(userId)) {
            if (isViewLive()) {
                mViewListener.hideProgressView();
            }
            return;
        }
        if (isInterActorLive()) {
            mInterActorListener.getUserDetails(context, userId);
        }
    }

    @Override
    public void setUserData(UserDetailsModel userDetailsModel) {
        if (isViewLive()) {
            mViewListener.setView(userDetailsModel);
            mViewListener.hideProgressView();
        }
    }

    @Override
    public void errorGettingUserDetails(String errorMessage) {
        if (isViewLive()) {
            mViewListener.showAlert(errorMessage);
            mViewListener.hideProgressView();
        }
    }

    @Override
    public void validateInputs(Context context, String userId, String name, String address, String postcode, String latitude, String longitude, String imagePath) {
        if (isViewLive()) {
            mViewListener.showProgressView();
        }
        if (TextUtils.isEmpty(userId)) {
            if (isViewLive()) {
                mViewListener.hideProgressView();
            }
            return;
        }
        if ("0".equalsIgnoreCase(userId)) {
            if (isViewLive()) {
                mViewListener.hideProgressView();
            }
            return;
        }
        if (TextUtils.isEmpty(name)) {
            if (isViewLive()) {
                mViewListener.nameFieldError("Please type your name here.");
                mViewListener.hideProgressView();
            }
        } else {
            if (isInterActorLive()) {
                mInterActorListener.updateUserDetails(context, userId, name, address, postcode, latitude, longitude, imagePath);
            }
        }
    }
}
