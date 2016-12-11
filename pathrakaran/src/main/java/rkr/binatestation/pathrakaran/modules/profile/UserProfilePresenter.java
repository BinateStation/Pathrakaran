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
        if (TextUtils.isEmpty(userId)) {
            return;
        }
        if ("0".equalsIgnoreCase(userId)) {
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
        }
    }

    @Override
    public void errorGettingUserDetails(String errorMessage) {
        if (isViewLive()) {
            mViewListener.showAlert(errorMessage);
        }
    }
}
