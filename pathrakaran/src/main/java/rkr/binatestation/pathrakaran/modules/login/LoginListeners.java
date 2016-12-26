package rkr.binatestation.pathrakaran.modules.login;

import android.content.Context;

/**
 * Created by RKR on 20/11/2016.
 * LoginListeners.
 */

interface LoginListeners {
    interface ViewListener {
        void resetErrors();

        void passwordError();

        void usernameError();

        void onSuccessfulLogin(String message);

        void onErrorLogin(String message);

        Context getContext();
    }

    interface PresenterListener {
        void attemptLogin(String s, String toString);

        void onSuccessfulLogin(String message);

        void onErrorLogin(String message);

        Context getContext();
    }

    interface InterActorListener {
        void login(final String username, final String password, final String loginType);
    }
}
