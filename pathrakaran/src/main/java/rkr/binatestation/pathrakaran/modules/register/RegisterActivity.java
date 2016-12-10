package rkr.binatestation.pathrakaran.modules.register;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import rkr.binatestation.pathrakaran.R;
import rkr.binatestation.pathrakaran.activities.SplashScreen;
import rkr.binatestation.pathrakaran.modules.login.LoginActivity;
import rkr.binatestation.pathrakaran.utils.GeneralUtils;

public class RegisterActivity extends AppCompatActivity implements TextWatcher, View.OnClickListener, RegisterListeners.ViewListener {

    private static final String TAG = "RegisterActivity";

    TextInputLayout mFieldNameTextInputLayout;
    TextInputLayout mFiledPhoneTextInputLayout;
    TextInputLayout mFieldEmailTextInputLayout;
    TextInputLayout mFieldPasswordTextInputLayout;
    TextInputLayout mFieldConfirmPasswordTextInputLayout;
    EditText mFieldNameEditText;
    EditText mFieldPhoneNumberEditText;
    EditText mFieldEmailEditText;
    EditText mFieldPasswordEditText;
    EditText mFieldConfirmPasswordEditText;
    Spinner mFieldUserTypeSpinner;
    ContentLoadingProgressBar mContentLoadingProgressBar;

    RegisterListeners.PresenterListener mPresenterListener;

    private boolean isPresenterLive() {
        return mPresenterListener != null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mPresenterListener = new RegisterPresenter(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.welcome);
            getSupportActionBar().setSubtitle(getString(R.string.you_are_going_to_simplify_your_life));
        }

        mFieldNameTextInputLayout = (TextInputLayout) findViewById(R.id.AR_field_name_layout);
        mFiledPhoneTextInputLayout = (TextInputLayout) findViewById(R.id.AR_field_phone_layout);
        mFieldEmailTextInputLayout = (TextInputLayout) findViewById(R.id.AR_field_email_layout);
        mFieldPasswordTextInputLayout = (TextInputLayout) findViewById(R.id.AR_field_password_layout);
        mFieldConfirmPasswordTextInputLayout = (TextInputLayout) findViewById(R.id.AR_field_confirm_password_layout);

        mFieldNameEditText = (EditText) findViewById(R.id.AR_field_name);
        mFieldPhoneNumberEditText = (EditText) findViewById(R.id.AR_field_phone_number);
        mFieldEmailEditText = (EditText) findViewById(R.id.AR_field_email);
        mFieldPasswordEditText = (EditText) findViewById(R.id.AR_field_password);
        mFieldConfirmPasswordEditText = (EditText) findViewById(R.id.AR_field_confirm_password);
        mFieldUserTypeSpinner = (Spinner) findViewById(R.id.AR_field_user_type);
        mContentLoadingProgressBar = (ContentLoadingProgressBar) findViewById(R.id.AR_progress_bar);
        mContentLoadingProgressBar.hide();

        mFieldNameEditText.addTextChangedListener(this);
        mFieldPhoneNumberEditText.addTextChangedListener(this);
        mFieldEmailEditText.addTextChangedListener(this);
        mFieldPasswordEditText.addTextChangedListener(this);
        mFieldConfirmPasswordEditText.addTextChangedListener(this);

    }

    private void getInputs(Context context) {
        Log.d(TAG, "getInputs() called");
        if (isPresenterLive()) {
            mPresenterListener.validateInputs(
                    context,
                    mFieldNameEditText.getText().toString().trim(),
                    mFieldPhoneNumberEditText.getText().toString().trim(),
                    mFieldEmailEditText.getText().toString().trim(),
                    mFieldPasswordEditText.getText().toString().trim(),
                    mFieldConfirmPasswordEditText.getText().toString().trim(),
                    mFieldUserTypeSpinner.getSelectedItemPosition()
            );
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        finish();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        mFieldNameTextInputLayout.setErrorEnabled(false);
        mFiledPhoneTextInputLayout.setErrorEnabled(false);
        mFieldEmailTextInputLayout.setErrorEnabled(false);
        mFieldPasswordTextInputLayout.setErrorEnabled(false);
        mFieldConfirmPasswordTextInputLayout.setErrorEnabled(false);

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.AR_action_login:
            case R.id.AR_action_login_button_line:
                getInputs(v.getContext());
                break;
        }
    }

    @Override
    public void nameFieldError(String errorMessage) {
        if (mFieldNameTextInputLayout != null) {
            mFieldNameTextInputLayout.setError(errorMessage);
        }
        if (mFieldNameEditText != null) {
            mFieldNameEditText.requestFocus();
        }
    }

    @Override
    public void phoneFieldError(String errorMessage) {
        if (mFiledPhoneTextInputLayout != null) {
            mFiledPhoneTextInputLayout.setError(errorMessage);
        }
        if (mFieldPhoneNumberEditText != null) {
            mFieldPhoneNumberEditText.requestFocus();
        }
    }

    @Override
    public void emailFieldError(String errorMessage) {
        if (mFieldEmailTextInputLayout != null) {
            mFieldEmailTextInputLayout.setError(errorMessage);
        }
        if (mFieldEmailEditText != null) {
            mFieldEmailEditText.requestFocus();
        }
    }

    @Override
    public void passwordFieldError(String errorMessage) {
        if (mFieldPasswordTextInputLayout != null) {
            mFieldPasswordTextInputLayout.setError(errorMessage);
        }
        if (mFieldPasswordEditText != null) {
            mFieldPasswordEditText.requestFocus();
        }
    }

    @Override
    public void confirmPassword(String errorMessage) {
        if (mFieldConfirmPasswordTextInputLayout != null) {
            mFieldConfirmPasswordTextInputLayout.setError(errorMessage);
        }
        if (mFieldConfirmPasswordEditText != null) {
            mFieldConfirmPasswordEditText.requestFocus();
        }
    }

    @Override
    public void finishRegistering() {
        startActivity(new Intent(RegisterActivity.this, SplashScreen.class));
        finish();
    }

    @Override
    public void showProgress() {
        if (mContentLoadingProgressBar != null) {
            mContentLoadingProgressBar.show();
        }
    }

    @Override
    public void hideProgress() {
        if (mContentLoadingProgressBar != null) {
            mContentLoadingProgressBar.hide();
        }
    }

    @Override
    public void showAlertDialog(String errorMessage) {
        GeneralUtils.alert(RegisterActivity.this, "Error", errorMessage);
    }
}
