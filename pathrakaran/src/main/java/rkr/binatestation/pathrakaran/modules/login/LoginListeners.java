package rkr.binatestation.pathrakaran.modules.login;

import android.content.Context;
import android.support.v4.app.LoaderManager;

import java.util.List;

/**
 * Created by RKR on 20/11/2016.
 * LoginListeners.
 */

interface LoginListeners {
    interface ViewListener {
        void addEmailsToAutoComplete(List<String> emailAddressCollection);

        void resetErrors();

        void passwordError();

        void usernameError();

        void onSuccessfulLogin();

        Context getContext();
    }

    interface PresenterListener {
        void populateAutoComplete(LoaderManager loaderManager);

        void addEmailsToAutoComplete(List<String> emailAddressCollection);

        void attemptLogin(String s, String toString);

        void onSuccessfulLogin();

        Context getContext();
    }

    interface InterActorListener {
        void populateAutoComplete(LoaderManager loaderManager);

        void login(final String username, final String password, final String loginType);
    }
}
