package rkr.binatestation.pathrakaran.modules.profile;

import android.app.Activity;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
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
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import rkr.binatestation.pathrakaran.R;
import rkr.binatestation.pathrakaran.models.UserDetailsModel;
import rkr.binatestation.pathrakaran.network.VolleySingleTon;
import rkr.binatestation.pathrakaran.utils.GeneralUtils;

import static rkr.binatestation.pathrakaran.utils.Constants.KEY_SP_USER_ID;
import static rkr.binatestation.pathrakaran.utils.Constants.REQUEST_EXTERNAL_STORAGE;
import static rkr.binatestation.pathrakaran.utils.Constants.REQUEST_LOCATION_PERMISSION;
import static rkr.binatestation.pathrakaran.utils.GeneralUtils.alert;
import static rkr.binatestation.pathrakaran.utils.GeneralUtils.mayRequestExternalMemory;

public class UserProfileActivity extends AppCompatActivity implements OnMapReadyCallback, TextWatcher, View.OnClickListener, UserProfileListeners.ViewListener {
    private static final String TAG = "UserProfileActivity";
    private static final int REQUEST_CAMERA = 1;
    private static final int SELECT_FILE = 2;
    private UserProfileListeners.PresenterListener mPresenterListener;
    private NetworkImageView mProfilePictureNetworkImageView;
    private TextInputLayout mNameTextInputLayout;
    private EditText mNameEditText;
    private TextInputLayout mPhoneNumberTextInputLayout;
    private EditText mPhoneNumberEditText;
    private TextInputLayout mEmailTextInputLayout;
    private EditText mEmailEditText;
    private TextInputLayout mAddressTextInputLayout;
    private EditText mAddressEditText;
    private TextInputLayout mPostcodeTextInputLayout;
    private EditText mPostcodeEditText;
    private MapView mUserLocationMapView;
    private LatLng mLatLng;
    private String mSelectedImagePath = "";

    private boolean isPresenterLive() {
        return mPresenterListener != null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        mPresenterListener = new UserProfilePresenter(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.AUP_toolbar);
        setSupportActionBar(toolbar);

        mNameEditText = (EditText) findViewById(R.id.AUP_field_name);
        mEmailEditText = (EditText) findViewById(R.id.AUP_field_email);
        mAddressEditText = (EditText) findViewById(R.id.AUP_field_address);
        mPhoneNumberEditText = (EditText) findViewById(R.id.AUP_field_phone_number);
        mPostcodeEditText = (EditText) findViewById(R.id.AUP_field_poscode);

        mNameTextInputLayout = (TextInputLayout) findViewById(R.id.AUP_field_name_layout);
        mPhoneNumberTextInputLayout = (TextInputLayout) findViewById(R.id.AUP_field_phone_layout);
        mEmailTextInputLayout = (TextInputLayout) findViewById(R.id.AUP_field_email_layout);
        mAddressTextInputLayout = (TextInputLayout) findViewById(R.id.AUP_field_address_layout);
        mPostcodeTextInputLayout = (TextInputLayout) findViewById(R.id.AUP_field_postcode_layout);

        mNameEditText.addTextChangedListener(this);
        mPhoneNumberEditText.addTextChangedListener(this);
        mAddressEditText.addTextChangedListener(this);
        mPostcodeEditText.addTextChangedListener(this);

        mUserLocationMapView = (MapView) findViewById(R.id.AUP_field_map_view);
        mUserLocationMapView.onCreate(savedInstanceState);
        // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
        try {
            MapsInitializer.initialize(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mProfilePictureNetworkImageView = (NetworkImageView) findViewById(R.id.AUP_profile_picture);
        mProfilePictureNetworkImageView.setOnClickListener(this);
        setEditable(false);
        if (isPresenterLive()) {
            mPresenterListener.getUserDetails(this, getSharedPreferences(getPackageName(), MODE_PRIVATE).getString(KEY_SP_USER_ID, "0"));
        }
    }

    private void selectImage() {
        mSelectedImagePath = "";
        final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(UserProfileActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CAMERA);
                } else if (items[item].equals("Choose from Library")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == REQUEST_CAMERA) {
                    Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    assert thumbnail != null;
                    thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
                    File destination = new File(GeneralUtils.getCaptureImagePath(),
                            "IMG_" + System.currentTimeMillis() + ".jpg");
                    FileOutputStream fo;
                    try {
                        if (destination.createNewFile()) {
                            fo = new FileOutputStream(destination);
                            fo.write(bytes.toByteArray());
                            fo.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mSelectedImagePath = destination.getAbsolutePath();
                    mProfilePictureNetworkImageView.setImageBitmap(thumbnail);
                } else if (requestCode == SELECT_FILE) {
                    Uri selectedImageUri = data.getData();
                    String[] projection = {MediaStore.MediaColumns.DATA};
                    CursorLoader cursorLoader = new CursorLoader(UserProfileActivity.this, selectedImageUri, projection, null, null, null);
                    Cursor cursor = cursorLoader.loadInBackground();
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                    cursor.moveToFirst();
                    mSelectedImagePath = cursor.getString(column_index);
                    if (!mSelectedImagePath.contains("http")) {
                        mProfilePictureNetworkImageView.setImageURI(selectedImageUri);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void validateInputs() {
        if (TextUtils.isEmpty(mNameEditText.getText().toString())) {
            mNameTextInputLayout.setError("Please type your name here.");
        } else if (TextUtils.isEmpty(mPhoneNumberEditText.getText().toString())) {
            mPhoneNumberTextInputLayout.setError("Please type your phone number here.");
        } else if (mPhoneNumberEditText.getText().toString().trim().length() != 10) {
            mPhoneNumberTextInputLayout.setError("Phone number must contain 10 digits");
        } else if (TextUtils.isEmpty(mAddressEditText.getText().toString())) {
            mAddressTextInputLayout.setError("Please type your address here.!");
        } else if (TextUtils.isEmpty(mPostcodeEditText.getText().toString())) {
            mPostcodeTextInputLayout.setError("Please type your postcode here.!");
        } else {
//            updateUserDetailsWithOutImage();
        }
    }

    private void setEditable(Boolean flag) {
        mNameEditText.setFocusable(flag);
        mNameEditText.setFocusableInTouchMode(flag);
        mPhoneNumberEditText.setClickable(false);
        mPhoneNumberEditText.setFocusableInTouchMode(false);
        mEmailEditText.setClickable(false);
        mEmailEditText.setFocusableInTouchMode(false);
        mAddressEditText.setClickable(flag);
        mAddressEditText.setFocusableInTouchMode(flag);
        mPostcodeEditText.setClickable(flag);
        mPostcodeEditText.setFocusableInTouchMode(flag);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (mLatLng != null) {
            googleMap.addMarker(new MarkerOptions().position(mLatLng).title(TAG));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLatLng, 10));
            googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                }
            });
            if (GeneralUtils.mayRequestLocation(UserProfileActivity.this, mNameEditText)) {
                //noinspection MissingPermission
                googleMap.setMyLocationEnabled(true);
            }

        }
    }

    @Override
    public void onResume() {
        mUserLocationMapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mUserLocationMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mUserLocationMapView.onLowMemory();
    }

    @Override
    public void setView(UserDetailsModel userDetailsModel) {
        if (userDetailsModel != null) {
            mNameEditText.setText(userDetailsModel.getName());
            mEmailEditText.setText(userDetailsModel.getEmail());
            mPhoneNumberEditText.setText(userDetailsModel.getMobile());
            mAddressEditText.setText("");
            mPostcodeEditText.setText("");

            mLatLng = new LatLng(userDetailsModel.getLatitude(), userDetailsModel.getLongitude());
            mUserLocationMapView.getMapAsync(this);
            mProfilePictureNetworkImageView.setImageUrl(
                    userDetailsModel.getImage(),
                    VolleySingleTon.getInstance(UserProfileActivity.this).getImageLoader()
            );
        } else {
            alert(UserProfileActivity.this, "Alert", "Something Wrong Please Try Later..");
        }
    }

    @Override
    public void showAlert(String errorMessage) {
        alert(UserProfileActivity.this, "Alert", errorMessage);
    }


    private void updateUserDetailsWithOutImage(final View view) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                VolleySingleTon.getDomainUrl() + "profile/update_profile", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(getLocalClassName(), "Response :- " + response);
                if (response.equalsIgnoreCase("1")) {
                    alert(UserProfileActivity.this, "Alert", "Profile updated successfully");
                    setEditable(false);
                    view.setSelected(false);
                } else {
                    alert(UserProfileActivity.this, "Alert", "Something went wrong, please try again later.!");
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
                params.put("u_id", getSharedPreferences(getPackageName(), MODE_PRIVATE).getString("KEY_SP_USER_ID", "0"));
                params.put("mNameEditText", mNameEditText.getText().toString().trim());
                params.put("mAddressEditText", mAddressEditText.getText().toString().trim());
                params.put("image", "");
                params.put("contact", mPhoneNumberEditText.getText().toString().trim());
                params.put("mEmailEditText", mEmailEditText.getText().toString().trim());
                params.put("latitude", "" + mLatLng.latitude);
                params.put("longitude", "" + mLatLng.longitude);
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
        VolleySingleTon.getInstance(UserProfileActivity.this).addToRequestQueue(UserProfileActivity.this, stringRequest);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        mNameTextInputLayout.setErrorEnabled(false);
        mPhoneNumberTextInputLayout.setErrorEnabled(false);
        mAddressTextInputLayout.setErrorEnabled(false);
        mPostcodeTextInputLayout.setErrorEnabled(false);
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.AUP_action_edit_save:
                if (view.isSelected()) {
                    validateInputs();
                } else {
                    setEditable(true);
                    view.setSelected(true);
                }
                break;
            case R.id.AUP_profile_picture:
                if (mNameEditText != null && mNameEditText.isFocusable() && mayRequestExternalMemory(this, mNameEditText)) {
                    selectImage();
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && mUserLocationMapView != null) {
            mUserLocationMapView.getMapAsync(this);
        }
        if (requestCode == REQUEST_EXTERNAL_STORAGE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && mProfilePictureNetworkImageView != null) {
            selectImage();
        }
    }
}
