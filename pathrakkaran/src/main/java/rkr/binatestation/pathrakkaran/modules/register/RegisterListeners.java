package rkr.binatestation.pathrakkaran.modules.register;

import android.content.Context;

/**
 * Created by RKR on 10/12/2016.
 * RegisterListeners.
 */

interface RegisterListeners {
    interface ViewListener {
        void nameFieldError(String errorMessage);

        void phoneFieldError(String errorMessage);

        void emailFieldError(String errorMessage);

        void passwordFieldError(String errorMessage);

        void confirmPassword(String errorMessage);

        void finishRegistering();

        void showProgress();

        void hideProgress();

        void showAlertDialog(String errorMessage);
    }

    interface PresenterListener {
        void validateInputs(Context context, String name, String phoneNumber, String email, String password, String confirmPassword, int userType);

        void registerSuccessfully();

        void errorRegistering(String errorMessage);
    }

    interface InterActorListener {
        void register(Context context, String name, String phoneNumber, String email, String password, String userTypeValue, String loginType);
    }
}
