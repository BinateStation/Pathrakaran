package rkr.binatestation.pathrakkaran.modules.login;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

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

    /**
     * Attempts to sign in or register the account specified by the activity_login form.
     * If there are form errors (invalid phone, missing fields, etc.), the
     * errors are presented and no actual activity_login attempt is made.
     *
     * @param phone    the username
     * @param password password entered
     */
    @Override
    public void attemptLogin(String phone, String password) {
        Log.d(TAG, "attemptLogin() called");

        if (isViewLive()) {
            viewListener.resetErrors();
        }

        // Check for a valid phone address.
        if (TextUtils.isEmpty(phone)) {
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
                interActorListener.login(phone, password, "N");
            }
        }
    }

    @Override
    public void onSuccessfulLogin(String message) {
        if (isViewLive()) {
            viewListener.onSuccessfulLogin(message);
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
