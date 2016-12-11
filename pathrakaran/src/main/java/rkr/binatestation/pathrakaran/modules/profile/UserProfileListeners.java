package rkr.binatestation.pathrakaran.modules.profile;

import android.content.Context;

import rkr.binatestation.pathrakaran.models.UserDetailsModel;

/**
 * Created by RKR on 11/12/2016.
 * UserProfileListeners.
 */

interface UserProfileListeners {
    interface ViewListener {

        void setView(UserDetailsModel userDetailsModel);

        void showAlert(String errorMessage);

        void nameFieldError(String errorMessage);

        void showProgressView();

        void hideProgressView();
    }

    interface PresenterListener {

        void getUserDetails(Context context, String userId);

        void setUserData(UserDetailsModel userDetailsModel);

        void errorGettingUserDetails(String errorMessage);

        void validateInputs(Context context, String userId, String name, String address, String postcode, String latitude, String longitude);
    }

    interface InterActorListener {

        void getUserDetails(Context context, String userId);

        void updateUserDetails(Context context, String userId, String name, String address, String postcode, String latitude, String longitude);
    }
}
