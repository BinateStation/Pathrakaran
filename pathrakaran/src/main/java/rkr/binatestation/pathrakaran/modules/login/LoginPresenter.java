package rkr.binatestation.pathrakaran.modules.login;

import android.content.Context;
import android.support.v4.app.LoaderManager;
import android.text.TextUtils;
import android.util.Log;

import java.util.List;

/**
 * Created by RKR on 20/11/2016.
 * LoginPresenter.
 */

class LoginPresenter implements LoginListeners.PresenterListener {

    private static final String TAG = "LoginPresenter";

    private LoginListeners.InterActorListener interActorListener;
    private LoginListeners.ViewListener viewListener;

    LoginPresenter(LoginListeners.ViewListener viewListener) {
        this.viewListener = viewListener;
        this.interActorListener = new LoginInterActor(this);
    }

    private boolean isInterActorLive() {
        return interActorListener != null;
    }

    private boolean isViewLive() {
        return viewListener != null;
    }

    @Override
    public void populateAutoComplete(LoaderManager loaderManager) {
        if (isInterActorLive()) {
            interActorListener.populateAutoComplete(loaderManager);
        }
    }

    @Override
    public void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        if (isViewLive()) {
            viewListener.addEmailsToAutoComplete(emailAddressCollection);
        }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     *
     * @param email    the username
     * @param password password entered
     */
    @Override
    public void attemptLogin(String email, String password) {
        Log.d(TAG, "attemptLogin() called");

        if (isViewLive()) {
            viewListener.resetErrors();
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            if (isViewLive()) {
                viewListener.usernameError();
            }
        }
        // Check for a valid password, if the user entered one.
        else if (TextUtils.isEmpty(password)) {
            if (isViewLive()) {
                viewListener.passwordError();
            }
        } else {
            if (isInterActorLive()) {
                interActorListener.login(email, password, "N");
            }
        }
    }

    @Override
    public void onSuccessfulLogin() {
        if (isViewLive()) {
            viewListener.onSuccessfulLogin();
        }
    }

    @Override
    public void onErrorLogin(String message) {
        if (isViewLive()) {
            viewListener.onErrorLogin(message);
        }
    }

    @Override
    public Context getContext() {
        if (isViewLive()) {
            return viewListener.getContext();
        } else {
            return null;
        }
    }
}
