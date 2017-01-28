package rkr.binatestation.pathrakkaran.modules.profile;

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
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.alexbbb.uploadservice.AbstractUploadServiceReceiver;
import com.android.volley.toolbox.NetworkImageView;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

import rkr.binatestation.pathrakkaran.R;
import rkr.binatestation.pathrakkaran.models.UserDetailsModel;
import rkr.binatestation.pathrakkaran.network.VolleySingleTon;
import rkr.binatestation.pathrakkaran.utils.Constants;
import rkr.binatestation.pathrakkaran.utils.GeneralUtils;

import static rkr.binatestation.pathrakkaran.utils.Constants.KEY_ADDRESS;
import static rkr.binatestation.pathrakkaran.utils.Constants.KEY_DATA;
import static rkr.binatestation.pathrakkaran.utils.Constants.KEY_EMAIL;
import static rkr.binatestation.pathrakkaran.utils.Constants.KEY_IMAGE;
import static rkr.binatestation.pathrakkaran.utils.Constants.KEY_LATITUDE;
import static rkr.binatestation.pathrakkaran.utils.Constants.KEY_LONGITUDE;
import static rkr.binatestation.pathrakkaran.utils.Constants.KEY_MESSAGE;
import static rkr.binatestation.pathrakkaran.utils.Constants.KEY_MOBILE;
import static rkr.binatestation.pathrakkaran.utils.Constants.KEY_NAME;
import static rkr.binatestation.pathrakkaran.utils.Constants.KEY_POSTCODE;
import static rkr.binatestation.pathrakkaran.utils.Constants.KEY_USER;
import static rkr.binatestation.pathrakkaran.utils.Constants.KEY_USER_ID;
import static rkr.binatestation.pathrakkaran.utils.Constants.KEY_USER_TYPE;
import static rkr.binatestation.pathrakkaran.utils.Constants.REQUEST_EXTERNAL_STORAGE;
import static rkr.binatestation.pathrakkaran.utils.Constants.REQUEST_LOCATION_PERMISSION;
import static rkr.binatestation.pathrakkaran.utils.GeneralUtils.alert;
import static rkr.binatestation.pathrakkaran.utils.GeneralUtils.mayRequestExternalMemory;

public class UserProfileActivity extends AppCompatActivity implements OnMapReadyCallback, TextWatcher, View.OnClickListener, UserProfileListeners.ViewListener {

    private static final String TAG = "UserProfileActivity";

    private static final int REQUEST_CAMERA = 1;
    private static final int SELECT_FILE = 2;
    private static final int PLACE_PICKER_REQUEST = 3;
    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
    private UserProfileListeners.PresenterListener mPresenterListener;
    private NetworkImageView mProfilePictureNetworkImageView;
    private TextInputLayout mNameTextInputLayout;
    private EditText mNameEditText;
    private EditText mPhoneNumberEditText;
    private EditText mEmailEditText;
    private EditText mAddressEditText;
    private EditText mPostcodeEditText;
    private View mActionSubmitView;
    private MapView mUserLocationMapView;
    private ContentLoadingProgressBar mProgressBar;
    private final AbstractUploadServiceReceiver uploadReceiver = new AbstractUploadServiceReceiver() {

        @Override
        public void onProgress(String uploadId, int progress) {
            Log.i(TAG, "The progress of the upload with ID " + uploadId + " is: " + progress);
            try {
                showProgressView();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onProgress(final String uploadId, final long uploadedBytes, final long totalBytes) {
            Log.i(TAG, "Upload with ID " + uploadId + " uploaded bytes: " + uploadedBytes
                    + ", total: " + totalBytes);
        }

        @Override
        public void onError(String uploadId, Exception exception) {
            try {
                if (isPresenterLive()) {
                    mPresenterListener.errorGettingUserDetails("Something went wrong, please try again later.!");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.e(TAG, "Error in upload with ID: " + uploadId + ". "
                    + exception.getLocalizedMessage(), exception);
        }

        @Override
        public void onCompleted(String uploadId, int serverResponseCode, String serverResponseMessage) {
            Log.i(TAG, "Upload with ID " + uploadId
                    + " has been completed with HTTP " + serverResponseCode
                    + ". Response from server: " + serverResponseMessage);
            try {
                JSONObject jsonObject = new JSONObject(serverResponseMessage);
                if (200 == jsonObject.optInt(Constants.KEY_STATUS)) {
                    JSONObject dataJsonObject = jsonObject.optJSONObject(KEY_DATA);
                    if (dataJsonObject != null) {
                        if (isPresenterLive()) {
                            mPresenterListener.setUserData(new UserDetailsModel(
                                    dataJsonObject.optLong(KEY_USER_ID),
                                    dataJsonObject.optString(KEY_NAME),
                                    dataJsonObject.optString(KEY_ADDRESS),
                                    dataJsonObject.optString(KEY_POSTCODE),
                                    dataJsonObject.optString(KEY_EMAIL),
                                    dataJsonObject.optString(KEY_MOBILE),
                                    dataJsonObject.optString(KEY_IMAGE),
                                    dataJsonObject.optInt(KEY_USER_TYPE),
                                    dataJsonObject.optDouble(KEY_LATITUDE),
                                    dataJsonObject.optDouble(KEY_LONGITUDE),
                                    UserDetailsModel.SAVE_STATUS_SAVED)
                            );
                        }
                        showAlert("Successfully updated..!");
                    } else {
                        if (isPresenterLive()) {
                            mPresenterListener.errorGettingUserDetails(
                                    jsonObject.has(KEY_MESSAGE) ?
                                            jsonObject.optString(KEY_MESSAGE) :
                                            "Something went wrong, please try again later.!"
                            );
                        }
                    }
                } else {
                    if (isPresenterLive()) {
                        mPresenterListener.errorGettingUserDetails(
                                jsonObject.has(KEY_MESSAGE) ?
                                        jsonObject.optString(KEY_MESSAGE) :
                                        "Something went wrong, please try again later.!"
                        );
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    private LatLng mLatLng;
    private String mPlaceName = "I am here";
    private String mSelectedImagePath = "";
    private Marker mMarker;

    private UserDetailsModel mUserDetailsModel;

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

        mProgressBar = (ContentLoadingProgressBar) findViewById(R.id.AUP_progress_bar);
        mNameEditText = (EditText) findViewById(R.id.AUP_field_name);
        mEmailEditText = (EditText) findViewById(R.id.AUP_field_email);
        mAddressEditText = (EditText) findViewById(R.id.AUP_field_address);
        mPhoneNumberEditText = (EditText) findViewById(R.id.AUP_field_phone_number);
        mPostcodeEditText = (EditText) findViewById(R.id.AUP_field_postcode);
        mActionSubmitView = findViewById(R.id.AUP_action_edit_save);
        mActionSubmitView.setOnClickListener(this);

        mNameTextInputLayout = (TextInputLayout) findViewById(R.id.AUP_field_name_layout);

        mProgressBar.hide();
        mNameEditText.addTextChangedListener(this);

        mUserLocationMapView = (MapView) findViewById(R.id.AUP_field_map_view);
        mUserLocationMapView.onCreate(savedInstanceState);
        mUserLocationMapView.setOnClickListener(this);
        // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
        try {
            MapsInitializer.initialize(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mProfilePictureNetworkImageView = (NetworkImageView) findViewById(R.id.AUP_profile_picture);
        mProfilePictureNetworkImageView.setOnClickListener(this);

        mUserDetailsModel = getIntent().getParcelableExtra(KEY_USER);
        if (mUserDetailsModel == null) {
            if (isPresenterLive()) {
                mPresenterListener.getUserDetails(this, getSharedPreferences(getPackageName(), MODE_PRIVATE).getLong(KEY_USER_ID, 0));
            }
        } else {
            setView(mUserDetailsModel);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        uploadReceiver.register(this);
    }

    private void selectImage() {
        Log.d(TAG, "selectImage() called");
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
        Log.d(TAG, "onActivityResult() called with: requestCode = [" + requestCode + "], resultCode = [" + resultCode + "], data = [" + data + "]");
        try {
            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == REQUEST_CAMERA) {
                    Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    assert thumbnail != null;
                    thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
                    File destination = new File(GeneralUtils.getCaptureImagePath(), "IMG_" + System.currentTimeMillis() + ".jpg");
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
                if (requestCode == PLACE_PICKER_REQUEST) {
                    Place place = PlacePicker.getPlace(this, data);
                    mLatLng = place.getLatLng();
                    mPlaceName = place.getName().toString();
                    mUserLocationMapView.getMapAsync(this);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setEditable(Boolean flag) {
        Log.d(TAG, "setEditable() called with: flag = [" + flag + "]");
        mNameEditText.setFocusable(flag);
        mNameEditText.setFocusableInTouchMode(flag);
        mPhoneNumberEditText.setFocusable(false);
        mPhoneNumberEditText.setFocusableInTouchMode(false);
        mEmailEditText.setFocusable(flag);
        mEmailEditText.setFocusableInTouchMode(flag);
        mAddressEditText.setFocusable(flag);
        mAddressEditText.setFocusableInTouchMode(flag);
        mPostcodeEditText.setFocusable(flag);
        mPostcodeEditText.setFocusableInTouchMode(flag);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady() called with: googleMap = [" + googleMap + "]");
        if (mLatLng != null) {
            if (mMarker == null) {
                mMarker = googleMap.addMarker(new MarkerOptions().position(mLatLng).title(mPlaceName));
            } else {
                mMarker.setTitle(mPlaceName);
                mMarker.setPosition(mLatLng);
            }
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLatLng, 20));
            googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    if (mActionSubmitView.isSelected()) {
                        startPlacePicker();
                    }
                }
            });
        }
    }

    private void startPlacePicker() {
        try {
            if (builder != null) {
                startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
            }
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
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
        uploadReceiver.unregister(this);

    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mUserLocationMapView.onLowMemory();
    }

    @Override
    public void setView(UserDetailsModel userDetailsModel) {
        Log.d(TAG, "setView() called with: mUserDetailsModel = [" + userDetailsModel + "]");
        setEditable(false);
        mActionSubmitView.setSelected(false);
        if (userDetailsModel != null) {
            int userType = getSharedPreferences(getPackageName(), MODE_PRIVATE).getInt(KEY_USER_TYPE, 0);
            if (userType == userDetailsModel.getUserType()) {
                mActionSubmitView.setVisibility(View.VISIBLE);
            } else {
                mActionSubmitView.setVisibility(View.INVISIBLE);
            }

            mNameEditText.setText(userDetailsModel.getName());
            mEmailEditText.setText(userDetailsModel.getEmail());
            mPhoneNumberEditText.setText(userDetailsModel.getMobile());
            mAddressEditText.setText(userDetailsModel.getAddress());
            mPostcodeEditText.setText(userDetailsModel.getPostcode());

            mLatLng = new LatLng(userDetailsModel.getLatitude(), userDetailsModel.getLongitude());
            mUserLocationMapView.getMapAsync(this);
            mProfilePictureNetworkImageView.setImageUrl(
                    VolleySingleTon.getDomainUrlForImage() + userDetailsModel.getImage(),
                    VolleySingleTon.getInstance(UserProfileActivity.this).getImageLoader()
            );
            mSelectedImagePath = userDetailsModel.getImage();
        } else {
            alert(UserProfileActivity.this, "Alert", "Something Wrong Please Try Later..");
        }
    }

    @Override
    public void showAlert(String errorMessage) {
        Log.d(TAG, "showAlert() called with: errorMessage = [" + errorMessage + "]");
        alert(UserProfileActivity.this, "Alert", errorMessage);
    }

    @Override
    public void nameFieldError(String errorMessage) {
        Log.d(TAG, "nameFieldError() called with: errorMessage = [" + errorMessage + "]");
        if (mNameTextInputLayout != null) {
            mNameTextInputLayout.setError(errorMessage);
        }
        if (mNameEditText != null) {
            mNameEditText.requestFocus();
        }
    }

    @Override
    public void showProgressView() {
        Log.d(TAG, "showProgressView() called");
        if (mProgressBar != null) {
            mProgressBar.show();
        }
    }

    @Override
    public void hideProgressView() {
        Log.d(TAG, "hideProgressView() called");
        if (mProgressBar != null) {
            mProgressBar.hide();
        }
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        Log.d(TAG, "beforeTextChanged() called with: s = [" + s + "], start = [" + start + "], count = [" + count + "], after = [" + after + "]");
        mNameTextInputLayout.setErrorEnabled(false);
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onClick(View view) {
        Log.d(TAG, "onClick() called with: view = [" + view + "]");
        switch (view.getId()) {
            case R.id.AUP_action_edit_save:
                if (view.isSelected() && isPresenterLive()) {
                    mPresenterListener.validateInputs(
                            this,
                            (mUserDetailsModel == null) ? getSharedPreferences(getPackageName(), MODE_PRIVATE).getLong(KEY_USER_ID, 0) : mUserDetailsModel.getUserId(),
                            mNameEditText.getText().toString().trim(),
                            mEmailEditText.getText().toString().trim(),
                            mAddressEditText.getText().toString().trim(),
                            mPostcodeEditText.getText().toString().trim(),
                            (mLatLng != null) ? mLatLng.latitude + "" : "",
                            (mLatLng != null) ? mLatLng.longitude + "" : "",
                            mSelectedImagePath
                    );
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
            case R.id.AUP_field_map_view:
                if (mActionSubmitView.isSelected()) {
                    startPlacePicker();
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult() called with: requestCode = [" + requestCode + "], permissions = [" + Arrays.toString(permissions) + "], grantResults = [" + Arrays.toString(grantResults) + "]");
        if (requestCode == REQUEST_LOCATION_PERMISSION && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && mUserLocationMapView != null) {
            mUserLocationMapView.getMapAsync(this);
        }
        if (requestCode == REQUEST_EXTERNAL_STORAGE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && mProfilePictureNetworkImageView != null) {
            selectImage();
        }
    }

}
