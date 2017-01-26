package rkr.binatestation.pathrakkaran.modules.profile;

import android.content.Context;

import rkr.binatestation.pathrakkaran.models.UserDetailsModel;

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

        void getUserDetails(Context context, long userId);

        void setUserData(UserDetailsModel userDetailsModel);

        void errorGettingUserDetails(String errorMessage);

        void validateInputs(Context context, long userId, String name, String email, String address, String postcode, String latitude, String longitude, String imagePath);
    }

    interface InterActorListener {

        void getUserDetails(Context context, long userId);

        void updateUserDetails(Context context, long userId, String name, String email, String address, String postcode, String latitude, String longitude, String imagePath);
    }
}
