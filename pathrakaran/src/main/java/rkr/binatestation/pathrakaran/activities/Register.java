package rkr.binatestation.pathrakaran.activities;

import android.app.ProgressDialog;
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
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import rkr.binatestation.pathrakaran.R;
import rkr.binatestation.pathrakaran.network.VolleySingleTon;

public class Register extends AppCompatActivity {

    TextInputLayout nameLayout, phoneLayout, usernameLayout, passwordLayout, confirmPasswordLayout;
    EditText name, phone, username, password, confirmPassword;
    private ProgressDialog mProgressDialog;

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
        mProgressDialog = new ProgressDialog(Register.this);

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

        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                nameLayout.setErrorEnabled(false);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                phoneLayout.setErrorEnabled(false);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                usernameLayout.setError(null);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                checkUsernameAvailability(s.toString(), "N");
            }
        });
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                passwordLayout.setErrorEnabled(false);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        confirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                confirmPasswordLayout.setErrorEnabled(false);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.R_toolbarLogin);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateInputs();
            }
        });
        FloatingActionButton login = (FloatingActionButton) findViewById(R.id.R_login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fab.performClick();
            }
        });
    }

    private void validateInputs() {
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

    public void showProgressDialog(Boolean aBoolean) {
        if (mProgressDialog != null) {
            if (aBoolean) {
                mProgressDialog.setMessage("Please wait ...");
                mProgressDialog.setCancelable(false);
                mProgressDialog.show();
            } else {
                if (mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
            }
        }
    }

    private void alert(String title, String message) {
        new AlertDialog.Builder(Register.this)
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
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                VolleySingleTon.getDomainUrl() + "profile/register", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(getLocalClassName(), "Response :- " + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    getSharedPreferences(getPackageName(), MODE_PRIVATE).edit()
                            .putString("KEY_USER_ID", jsonObject.getString("userid"))
                            .putString("KEY_USER_NAME", jsonObject.getString("name"))
                            .putString("KEY_USER_PHONE", jsonObject.getString("contact"))
                            .putBoolean("KEY_IS_LOGGED_IN", true).apply();
                    startActivity(new Intent(Register.this, SplashScreen.class));
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                    alert("Error", "Username not available, try another one");
                }
                showProgressDialog(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(getLocalClassName(), "Error :- " + error.toString());
                showProgressDialog(false);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("name", name);
                params.put("contact", phone);
                params.put("username", username);
                params.put("password", password);
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
        VolleySingleTon.getInstance(Register.this).addToRequestQueue(Register.this, stringRequest);
        showProgressDialog(true);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(Register.this, LoginActivity.class));
        finish();
    }

    private void checkUsernameAvailability(final String username, final String loginType) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
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
        VolleySingleTon.getInstance(Register.this).addToRequestQueue(Register.this, stringRequest);
    }

}
