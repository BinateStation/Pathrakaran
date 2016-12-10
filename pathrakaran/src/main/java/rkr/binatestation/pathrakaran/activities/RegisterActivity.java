package rkr.binatestation.pathrakaran.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import rkr.binatestation.pathrakaran.R;
import rkr.binatestation.pathrakaran.modules.login.LoginActivity;
import rkr.binatestation.pathrakaran.network.VolleySingleTon;

import static com.android.volley.Request.Method.POST;
import static rkr.binatestation.pathrakaran.utils.Constants.KEY_JSON_CONTACT;
import static rkr.binatestation.pathrakaran.utils.Constants.KEY_JSON_NAME;
import static rkr.binatestation.pathrakaran.utils.Constants.KEY_JSON_USER_ID;
import static rkr.binatestation.pathrakaran.utils.Constants.KEY_POST_CONTACT;
import static rkr.binatestation.pathrakaran.utils.Constants.KEY_POST_LOGIN_TYPE;
import static rkr.binatestation.pathrakaran.utils.Constants.KEY_POST_NAME;
import static rkr.binatestation.pathrakaran.utils.Constants.KEY_POST_PASSWORD;
import static rkr.binatestation.pathrakaran.utils.Constants.KEY_POST_USER;
import static rkr.binatestation.pathrakaran.utils.Constants.KEY_SP_IS_LOGGED_IN;
import static rkr.binatestation.pathrakaran.utils.Constants.KEY_SP_USER_ID;
import static rkr.binatestation.pathrakaran.utils.Constants.KEY_SP_USER_NAME;
import static rkr.binatestation.pathrakaran.utils.Constants.KEY_SP_USER_PHONE;
import static rkr.binatestation.pathrakaran.utils.Constants.USER_REGISTER;

public class RegisterActivity extends AppCompatActivity implements TextWatcher {

    private static final String TAG = "RegisterActivity";

    TextInputLayout nameLayout, phoneLayout, usernameLayout, passwordLayout, confirmPasswordLayout;
    EditText name, phone, username, password, confirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Welcome");
            getSupportActionBar().setSubtitle("You're going to simplify your life");
        }

        nameLayout = (TextInputLayout) findViewById(R.id.R_nameLayout);
        phoneLayout = (TextInputLayout) findViewById(R.id.R_phoneLayout);
        usernameLayout = (TextInputLayout) findViewById(R.id.R_usernameLayout);
        passwordLayout = (TextInputLayout) findViewById(R.id.R_passwordLayout);
        confirmPasswordLayout = (TextInputLayout) findViewById(R.id.R_confirmPasswordLayout);

        name = (EditText) findViewById(R.id.R_name);
        phone = (EditText) findViewById(R.id.R_phone);
        username = (EditText) findViewById(R.id.R_username);
        password = (EditText) findViewById(R.id.R_password);
        confirmPassword = (EditText) findViewById(R.id.R_confirmPassword);

        name.addTextChangedListener(this);
        phone.addTextChangedListener(this);
        username.addTextChangedListener(this);
        password.addTextChangedListener(this);
        confirmPassword.addTextChangedListener(this);

        FloatingActionButton login = (FloatingActionButton) findViewById(R.id.R_login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateInputs();
            }
        });
    }

    private void validateInputs() {
        Log.d(TAG, "validateInputs() called");
        if (TextUtils.isEmpty(name.getText().toString().trim())) {
            nameLayout.setError("Name is Mandatory.!");
        } else if (TextUtils.isEmpty(phone.getText().toString().trim())) {
            phoneLayout.setError("Phone number is mandatory.!");
        } else if ((!TextUtils.isDigitsOnly(phone.getText().toString().trim()) || (TextUtils.getTrimmedLength(phone.getText().toString()) < 10))) {
            phoneLayout.setError("Invalid phone number.!");
        } else if (TextUtils.isEmpty(username.getText().toString()) || usernameLayout.getError() != null) {
            usernameLayout.setError("Please use a valid username.!");
        } else if (TextUtils.isEmpty(password.getText().toString().trim())) {
            passwordLayout.setError("Please type your password here.!");
        } else if (TextUtils.isEmpty(confirmPassword.getText().toString().trim())) {
            confirmPasswordLayout.setError("Please confirm your password.!");
        } else if (!password.getText().toString().equalsIgnoreCase(confirmPassword.getText().toString())) {
            confirmPasswordLayout.setError("Password mismatch.!");
        } else {
            register(
                    name.getText().toString().trim(),
                    phone.getText().toString().trim(),
                    username.getText().toString().trim(),
                    password.getText().toString().trim(),
                    "N"
            );
        }
    }

    private void alert(String title, String message) {
        new AlertDialog.Builder(RegisterActivity.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void register(final String name, final String phone, final String username, final String password, final String loginType) {
        Log.d(TAG, "register() called with: name = [" + name + "], phone = [" + phone + "], username = [" + username + "], password = [" + password + "], loginType = [" + loginType + "]");
        StringRequest stringRequest = new StringRequest(
                POST,
                VolleySingleTon.getDomainUrl() + USER_REGISTER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(getLocalClassName(), "Response :- " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            getSharedPreferences(getPackageName(), MODE_PRIVATE).edit()
                                    .putString(KEY_SP_USER_ID, jsonObject.getString(KEY_JSON_USER_ID))
                                    .putString(KEY_SP_USER_NAME, jsonObject.getString(KEY_JSON_NAME))
                                    .putString(KEY_SP_USER_PHONE, jsonObject.getString(KEY_JSON_CONTACT))
                                    .putBoolean(KEY_SP_IS_LOGGED_IN, true).apply();
                            startActivity(new Intent(RegisterActivity.this, SplashScreen.class));
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            alert("Error", "Username not available, try another one");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(getLocalClassName(), "Error :- " + error.toString());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(KEY_POST_NAME, name);
                params.put(KEY_POST_CONTACT, phone);
                params.put(KEY_POST_USER, username);
                params.put(KEY_POST_PASSWORD, password);
                params.put(KEY_POST_LOGIN_TYPE, loginType);

                Log.d(TAG, "getParams() returned: " + params);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };
        VolleySingleTon.getInstance(RegisterActivity.this).addToRequestQueue(RegisterActivity.this, stringRequest);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        finish();
    }

    private void checkUsernameAvailability(final String username, final String loginType) {
        StringRequest stringRequest = new StringRequest(POST,
                VolleySingleTon.getDomainUrl() + "profile/check_username_available", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(getLocalClassName(), "Response :- " + response);
                if (response.equals("0")) {
                    usernameLayout.setError("Username already exists.!");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(getLocalClassName(), "Error :- " + error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("login_type", loginType);
                Log.i(getLocalClassName(), "Request :- " + params.toString());
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };
        VolleySingleTon.getInstance(RegisterActivity.this).addToRequestQueue(RegisterActivity.this, stringRequest);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        nameLayout.setErrorEnabled(false);
        phoneLayout.setErrorEnabled(false);
        usernameLayout.setErrorEnabled(false);
        passwordLayout.setErrorEnabled(false);
        confirmPasswordLayout.setErrorEnabled(false);

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
