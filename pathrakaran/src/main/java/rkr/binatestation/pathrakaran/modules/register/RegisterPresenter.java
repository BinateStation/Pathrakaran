package rkr.binatestation.pathrakaran.modules.register;

import android.content.Context;
import android.text.TextUtils;

import static rkr.binatestation.pathrakaran.utils.Constants.USER_TYPE_AGENT;
import static rkr.binatestation.pathrakaran.utils.Constants.USER_TYPE_SUBSCRIBER;
import static rkr.binatestation.pathrakaran.utils.Constants.USER_TYPE_SUPPLIER;

/**
 * Created by RKR on 10/12/2016.
 * RegisterPresenter.
 */

class RegisterPresenter implements RegisterListeners.PresenterListener {
    private RegisterListeners.ViewListener mViewListener;
    private RegisterListeners.InterActorListener mInterActorListener;

    RegisterPresenter(RegisterListeners.ViewListener viewListener) {
        this.mViewListener = viewListener;
        this.mInterActorListener = new RegisterInterActor(this);
    }

    private boolean isViewLive() {
        return mViewListener != null;
    }

    private boolean isInterActorLive() {
        return mInterActorListener != null;
    }

    @Override
    public void validateInputs(Context context, String name, String phoneNumber, String email, String password, String confirmPassword, int userType) {
        if (isViewLive()) {
            mViewListener.showProgress();
        }
        if (TextUtils.isEmpty(name)) {
            if (isViewLive()) {
                mViewListener.nameFieldError("Name is Mandatory.!");
                mViewListener.hideProgress();
            }
        } else if (TextUtils.isEmpty(phoneNumber)) {
            if (isViewLive()) {
                mViewListener.phoneFieldError("Phone number is mandatory.!");
                mViewListener.hideProgress();
            }
        } else if ((!TextUtils.isDigitsOnly(phoneNumber) || (TextUtils.getTrimmedLength(phoneNumber) < 10))) {
            if (isViewLive()) {
                mViewListener.phoneFieldError("Invalid mFieldPhoneNumberEditText number.!");
                mViewListener.hideProgress();
            }
        } else if (TextUtils.isEmpty(email)) {
            if (isViewLive()) {
                mViewListener.emailFieldError("Please use a valid email address.!");
                mViewListener.hideProgress();
            }
        } else if (TextUtils.isEmpty(password)) {
            if (isViewLive()) {
                mViewListener.passwordFieldError("Please type your password here.!");
                mViewListener.hideProgress();
            }
        } else if (TextUtils.isEmpty(confirmPassword)) {
            if (isViewLive()) {
                mViewListener.confirmPassword("Please confirm your password.!");
                mViewListener.hideProgress();
            }
        } else if (!password.equalsIgnoreCase(confirmPassword)) {
            if (isViewLive()) {
                mViewListener.confirmPassword("Password mismatch.!");
                mViewListener.hideProgress();
            }
        } else {
            String userTypeValue = USER_TYPE_SUBSCRIBER;
            switch (userType) {
                case 0:
                    userTypeValue = USER_TYPE_SUBSCRIBER;
                    break;
                case 1:
                    userTypeValue = USER_TYPE_AGENT;
                    break;
                case 2:
                    userTypeValue = USER_TYPE_SUPPLIER;
                    break;
            }
            if (isInterActorLive()) {
                mInterActorListener.register(context, name, phoneNumber, email, password, userTypeValue, "N");
            }
        }
    }

    @Override
    public void registerSuccessfully() {
        if (isViewLive()) {
            mViewListener.hideProgress();
            mViewListener.finishRegistering();
        }
    }

    @Override
    public void errorRegistering(String errorMessage) {
        if (isViewLive()) {
            mViewListener.hideProgress();
            mViewListener.showAlertDialog(errorMessage);
        }
    }
}
