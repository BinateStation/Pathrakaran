package rkr.binatestation.pathrakkaran.modules.register;

import android.content.Context;
import android.text.TextUtils;

import rkr.binatestation.pathrakkaran.R;
import rkr.binatestation.pathrakkaran.utils.GeneralUtils;

import static rkr.binatestation.pathrakkaran.models.UserDetailsModel.USER_TYPE_AGENT;
import static rkr.binatestation.pathrakkaran.models.UserDetailsModel.USER_TYPE_SUBSCRIBER;
import static rkr.binatestation.pathrakkaran.models.UserDetailsModel.USER_TYPE_SUPPLIER;


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
                mViewListener.nameFieldError(context.getString(R.string.name_error_message));
                mViewListener.hideProgress();
            }
        } else if (TextUtils.isEmpty(phoneNumber)) {
            if (isViewLive()) {
                mViewListener.phoneFieldError(context.getString(R.string.mobile_mandatory_alert_message));
                mViewListener.hideProgress();
            }
        } else if ((!TextUtils.isDigitsOnly(phoneNumber) || (TextUtils.getTrimmedLength(phoneNumber) < 10))) {
            if (isViewLive()) {
                mViewListener.phoneFieldError(context.getString(R.string.mobile_invalid_alert_msg));
                mViewListener.hideProgress();
            }
        } else if (!TextUtils.isEmpty(email) && !GeneralUtils.validateEmail(email)) {
            if (isViewLive()) {
                mViewListener.emailFieldError(context.getString(R.string.invalid_email_alert_msg));
                mViewListener.hideProgress();
            }
        } else if (TextUtils.isEmpty(password)) {
            if (isViewLive()) {
                mViewListener.passwordFieldError(context.getString(R.string.empty_password_alert_msg));
                mViewListener.hideProgress();
            }
        } else if (TextUtils.isEmpty(confirmPassword)) {
            if (isViewLive()) {
                mViewListener.confirmPassword(context.getString(R.string.confirm_password_empty_msg));
                mViewListener.hideProgress();
            }
        } else if (!password.equalsIgnoreCase(confirmPassword)) {
            if (isViewLive()) {
                mViewListener.confirmPassword(context.getString(R.string.password_mismatch_alert_msg));
                mViewListener.hideProgress();
            }
        } else {
            int userTypeValue = USER_TYPE_SUBSCRIBER;
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
