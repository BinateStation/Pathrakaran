package rkr.binatestation.pathrakkaran.modules.suppliers;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import rkr.binatestation.pathrakkaran.R;
import rkr.binatestation.pathrakkaran.utils.GeneralUtils;

/**
 * Dialog fragment use to add the supplier
 */
public class AddSupplierFragment extends DialogFragment implements TextWatcher, View.OnClickListener {

    private static final String TAG = "AddSupplierFragment";

    private TextInputLayout mFieldNameTextInputLayout;
    private TextInputLayout mFiledPhoneTextInputLayout;
    private TextInputLayout mFieldEmailTextInputLayout;
    private EditText mFieldNameEditText;
    private EditText mFieldPhoneNumberEditText;
    private EditText mFieldEmailEditText;
    private ContentLoadingProgressBar mContentLoadingProgressBar;
    private AddSupplierListener mAddSupplierListener;


    public AddSupplierFragment() {
        // Required empty public constructor
    }

    public static AddSupplierFragment newInstance(AddSupplierListener addSupplierListener) {
        Log.d(TAG, "newInstance() called");
        Bundle args = new Bundle();

        AddSupplierFragment fragment = new AddSupplierFragment();
        fragment.mAddSupplierListener = addSupplierListener;
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_supplier, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mFieldNameTextInputLayout = (TextInputLayout) view.findViewById(R.id.FAS_field_name_layout);
        mFiledPhoneTextInputLayout = (TextInputLayout) view.findViewById(R.id.FAS_field_phone_layout);
        mFieldEmailTextInputLayout = (TextInputLayout) view.findViewById(R.id.FAS_field_email_layout);

        mFieldNameEditText = (EditText) view.findViewById(R.id.FAS_field_name);
        mFieldPhoneNumberEditText = (EditText) view.findViewById(R.id.FAS_field_phone_number);
        mFieldEmailEditText = (EditText) view.findViewById(R.id.FAS_field_email);

        View submitFabView = view.findViewById(R.id.FAS_action_submit);
        View submitLineView = view.findViewById(R.id.FAS_action_submit_button_line);

        mContentLoadingProgressBar = (ContentLoadingProgressBar) view.findViewById(R.id.FAS_progress_bar);
        mContentLoadingProgressBar.hide();

        mFieldNameEditText.addTextChangedListener(this);
        mFieldPhoneNumberEditText.addTextChangedListener(this);
        mFieldEmailEditText.addTextChangedListener(this);
        submitFabView.setOnClickListener(this);
        submitLineView.setOnClickListener(this);

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        mFieldNameTextInputLayout.setErrorEnabled(false);
        mFiledPhoneTextInputLayout.setErrorEnabled(false);
        mFieldEmailTextInputLayout.setErrorEnabled(false);

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    public void nameFieldError(String errorMessage) {
        if (mFieldNameTextInputLayout != null) {
            mFieldNameTextInputLayout.setError(errorMessage);
        }
        if (mFieldNameEditText != null) {
            mFieldNameEditText.requestFocus();
        }
    }

    public void phoneFieldError(String errorMessage) {
        if (mFiledPhoneTextInputLayout != null) {
            mFiledPhoneTextInputLayout.setError(errorMessage);
        }
        if (mFieldPhoneNumberEditText != null) {
            mFieldPhoneNumberEditText.requestFocus();
        }
    }

    public void emailFieldError(String errorMessage) {
        if (mFieldEmailTextInputLayout != null) {
            mFieldEmailTextInputLayout.setError(errorMessage);
        }
        if (mFieldEmailEditText != null) {
            mFieldEmailEditText.requestFocus();
        }
    }

    public void showProgress() {
        if (mContentLoadingProgressBar != null) {
            mContentLoadingProgressBar.show();
        }
    }

    public void hideProgress() {
        if (mContentLoadingProgressBar != null) {
            mContentLoadingProgressBar.hide();
        }
    }

    @Override
    public void onClick(View v) {
        if (mAddSupplierListener != null && mFieldNameEditText != null && mFieldPhoneNumberEditText != null && mFieldEmailEditText != null) {
            String name = mFieldNameEditText.getText().toString().trim();
            String mobile = mFieldPhoneNumberEditText.getText().toString().trim();
            String email = mFieldEmailEditText.getText().toString().trim();
            if (TextUtils.isEmpty(name)) {
                nameFieldError(getString(R.string.name_error_message));
            } else if (TextUtils.isEmpty(mobile)) {
                phoneFieldError(getString(R.string.mobile_mandatory_alert_message));
            } else if ((!TextUtils.isDigitsOnly(mobile) || (TextUtils.getTrimmedLength(mobile) < 10))) {
                phoneFieldError(getString(R.string.mobile_invalid_alert_msg));
            } else if (!TextUtils.isEmpty(email) && !GeneralUtils.validateEmail(email)) {
                emailFieldError(getString(R.string.invalid_email_alert_msg));
            } else {
                showProgress();
                mAddSupplierListener.submit(name, mobile, email);
            }
        }
    }

    public interface AddSupplierListener {
        void submit(String name, String mobile, String email);
    }
}
