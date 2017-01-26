package rkr.binatestation.pathrakkaran.modules.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import rkr.binatestation.pathrakkaran.R;
import rkr.binatestation.pathrakkaran.activities.SplashScreen;
import rkr.binatestation.pathrakkaran.modules.register.RegisterActivity;

import static rkr.binatestation.pathrakkaran.utils.GeneralUtils.alert;

/**
 * A activity_login screen that offers activity_login via email/phone number and password.
 */
public class LoginActivity extends AppCompatActivity implements LoginListeners.ViewListener, OnClickListener, TextWatcher {

    private static final String TAG = "LoginActivity";
    LoginListeners.PresenterListener mPresenterListener;
    // UI references.
    private TextInputEditText mFieldPhoneTextInputEditText;
    private TextInputEditText mFieldPasswordTextInputEditText;
    private TextInputLayout mFieldPhoneTextInputLayout, mFieldPasswordTextInputLayout;
    private ContentLoadingProgressBar mContentLoadingProgressBar;

    private boolean isPresenterLive() {
        return mPresenterListener != null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mPresenterListener = new LoginPresenter(this);
        // Set up the activity_login form.
        mContentLoadingProgressBar = (ContentLoadingProgressBar) findViewById(R.id.AL_progress_bar);
        mFieldPhoneTextInputEditText = (TextInputEditText) findViewById(R.id.AL_field_phone);
        mFieldPasswordTextInputEditText = (TextInputEditText) findViewById(R.id.AL_filed_password);
        mFieldPhoneTextInputLayout = (TextInputLayout) findViewById(R.id.AL_field_username_layout);
        mFieldPasswordTextInputLayout = (TextInputLayout) findViewById(R.id.AL_field_password_layout);

        mContentLoadingProgressBar.hide();
        mFieldPhoneTextInputEditText.addTextChangedListener(this);
        mFieldPasswordTextInputEditText.addTextChangedListener(this);

        mFieldPasswordTextInputEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.AL_action_login || id == EditorInfo.IME_NULL) {
                    Log.d(TAG, "onEditorAction() called with: textView = [" + textView + "], id = [" + id + "], keyEvent = [" + keyEvent + "]");
                    if (isPresenterLive()) {
                        mPresenterListener.attemptLogin(
                                mFieldPhoneTextInputEditText.getText().toString().trim(),
                                mFieldPasswordTextInputEditText.getText().toString().trim()
                        );
                    }
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void resetErrors() {
        // Reset errors.
        Log.d(TAG, "resetErrors() called");
        mContentLoadingProgressBar.show();
        if (mFieldPhoneTextInputEditText != null && mFieldPasswordTextInputEditText != null) {
            mFieldPhoneTextInputEditText.setError(null);
            mFieldPasswordTextInputEditText.setError(null);
        }
    }

    @Override
    public void passwordError() {
        mContentLoadingProgressBar.hide();
        if (mFieldPasswordTextInputLayout != null) {
            mFieldPasswordTextInputLayout.setError(getString(R.string.error_invalid_password));
        }
        if (mFieldPasswordTextInputEditText != null) {
            mFieldPasswordTextInputEditText.requestFocus();
        }
    }

    @Override
    public void usernameError() {
        mContentLoadingProgressBar.hide();
        if (mFieldPhoneTextInputLayout != null) {
            mFieldPhoneTextInputLayout.setError(getString(R.string.error_field_required));
        }
        if (mFieldPhoneTextInputEditText != null) {
            mFieldPhoneTextInputEditText.requestFocus();
        }
    }

    @Override
    public void onSuccessfulLogin(String message) {
        mContentLoadingProgressBar.hide();
        startActivity(new Intent(LoginActivity.this, SplashScreen.class));
        finish();
    }

    @Override
    public void onErrorLogin(String message) {
        mContentLoadingProgressBar.hide();
        alert(getContext(), "Error", message);
    }

    @Override
    public Context getContext() {
        return LoginActivity.this;
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick() called with: v = [" + v + "]");
        switch (v.getId()) {
            case R.id.AL_action_register:
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                finish();
                break;
            case R.id.AL_action_login:
                if (isPresenterLive()) {
                    mPresenterListener.attemptLogin(
                            mFieldPhoneTextInputEditText.getText().toString().trim(),
                            mFieldPasswordTextInputEditText.getText().toString().trim()
                    );
                }
                break;

        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        mFieldPhoneTextInputLayout.setErrorEnabled(false);
        mFieldPasswordTextInputLayout.setErrorEnabled(false);
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}

