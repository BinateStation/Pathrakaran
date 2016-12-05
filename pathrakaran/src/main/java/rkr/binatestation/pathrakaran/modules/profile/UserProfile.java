package rkr.binatestation.pathrakaran.modules.profile;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import rkr.binatestation.pathrakaran.R;
import rkr.binatestation.pathrakaran.activities.MapPicker;
import rkr.binatestation.pathrakaran.network.VolleySingleTon;
import rkr.binatestation.pathrakaran.utils.GeneralUtils;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static rkr.binatestation.pathrakaran.utils.Constants.REQUEST_LOCATION_PERMISSION;

public class UserProfile extends AppCompatActivity implements OnMapReadyCallback, TextWatcher {
    private static final String TAG = "UserProfile";

    MapView mapView;
    LatLng latLng;
    JSONObject jsonObject;
    EditText name;
    EditText phoneNumber;
    EditText email;
    EditText address;
    EditText postalCode;
    TextInputLayout nameLayout, phoneNumberLayout, emailLayout, addressLayout, postalCodeLayout;
    NetworkImageView profilePic;
    String selectedImagePath = "";
    Integer REQUEST_CAMERA = 1;
    Integer SELECT_FILE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        name = (EditText) findViewById(R.id.UP_name);
        email = (EditText) findViewById(R.id.UP_email);
        address = (EditText) findViewById(R.id.UP_address);
        phoneNumber = (EditText) findViewById(R.id.UP_phone_number);
        postalCode = (EditText) findViewById(R.id.UP_postal_code);

        nameLayout = (TextInputLayout) findViewById(R.id.UP_name_layout);
        phoneNumberLayout = (TextInputLayout) findViewById(R.id.UP_phone_layout);
        emailLayout = (TextInputLayout) findViewById(R.id.UP_email_layout);
        addressLayout = (TextInputLayout) findViewById(R.id.UP_address_layout);
        postalCodeLayout = (TextInputLayout) findViewById(R.id.UP_postal_code_layout);

        name.addTextChangedListener(this);
        phoneNumber.addTextChangedListener(this);
        address.addTextChangedListener(this);
        postalCode.addTextChangedListener(this);

        mapView = (MapView) findViewById(R.id.UP_map_view);
        mapView.onCreate(savedInstanceState);
        // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
        try {
            MapsInitializer.initialize(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        profilePic = (NetworkImageView) findViewById(R.id.UP_profilePic);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.isSelected()) {
                    validateInputs(view);
                } else {
                    setEditable(true);
                    view.setSelected(true);
                }
            }
        });

        setEditable(false);
        getUserDetails();
        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
    }

    private void selectImage() {
        selectedImagePath = "";
        final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(UserProfile.this);
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
            if (requestCode == 101) {
                LatLng latLng = new LatLng(data.getDoubleExtra("BPMPA_LATITUDE", 0.0), data.getDoubleExtra("BPMPA_LONGITUDE", 0.0));
                if (latLng.latitude != 0.0 && latLng.longitude != 0.0) {
                    this.latLng = latLng;
                    mapView.getMapAsync(this);
                }
            }
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
                    selectedImagePath = destination.getAbsolutePath();
                    profilePic.setImageBitmap(thumbnail);
                } else if (requestCode == SELECT_FILE) {
                    Uri selectedImageUri = data.getData();
                    String[] projection = {MediaStore.MediaColumns.DATA};
                    CursorLoader cursorLoader = new CursorLoader(UserProfile.this, selectedImageUri, projection, null, null, null);
                    Cursor cursor = cursorLoader.loadInBackground();
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                    cursor.moveToFirst();
                    selectedImagePath = cursor.getString(column_index);
                    if (!selectedImagePath.contains("http")) {
                        profilePic.setImageURI(selectedImageUri);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void validateInputs(View view) {
        if (TextUtils.isEmpty(name.getText().toString())) {
            nameLayout.setError("Please type your name here.");
        } else if (TextUtils.isEmpty(phoneNumber.getText().toString())) {
            phoneNumberLayout.setError("Please type your phone number here.");
        } else if (phoneNumber.getText().toString().trim().length() != 10) {
            phoneNumberLayout.setError("Phone number must contain 10 digits");
        } else if (TextUtils.isEmpty(address.getText().toString())) {
            addressLayout.setError("Please type your address here.!");
        } else if (TextUtils.isEmpty(postalCode.getText().toString())) {
            postalCodeLayout.setError("Please type your postal code here.!");
        } else {
            updateUserDetailsWithOutImage(view);
        }
    }

    private void setEditable(Boolean flag) {
        name.setFocusable(flag);
        name.setFocusableInTouchMode(flag);
        phoneNumber.setClickable(flag);
        phoneNumber.setFocusableInTouchMode(flag);
        email.setClickable(flag);
        email.setFocusableInTouchMode(flag);
        address.setClickable(flag);
        address.setFocusableInTouchMode(flag);
        postalCode.setClickable(flag);
        postalCode.setFocusableInTouchMode(flag);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (latLng != null) {
            googleMap.addMarker(new MarkerOptions().position(latLng).title(TAG));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
            googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    startActivity(new Intent(UserProfile.this, MapPicker.class)
                            .putExtra("UP_LATITUDE", UserProfile.this.latLng.latitude)
                            .putExtra("UP_LONGITUDE", UserProfile.this.latLng.longitude));
                }
            });
            if (mayRequestLocation()) {
                //noinspection MissingPermission
                googleMap.setMyLocationEnabled(true);
            }

        }
    }

    private boolean mayRequestLocation() {
        Log.d(TAG, "mayRequestLocation() called");
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
            Snackbar.make(name, R.string.location_permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION}, REQUEST_LOCATION_PERMISSION);
                        }
                    }).show();
        } else {
            requestPermissions(new String[]{ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        }
        return false;
    }

    private void alert(String title, String message) {
        new AlertDialog.Builder(UserProfile.this)
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

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    private void setView() {
        if (jsonObject != null) {
            name.setText(jsonObject.optString("name"));
            email.setText(jsonObject.optString("email"));
            phoneNumber.setText(jsonObject.optString("contact"));
            address.setText(jsonObject.optString("address"));
            postalCode.setText(jsonObject.optString(""));

            latLng = new LatLng(jsonObject.optDouble("latitude"), jsonObject.optDouble("longitude"));
            mapView.getMapAsync(this);
            profilePic.setImageUrl(jsonObject.optString("image"), VolleySingleTon.getInstance(UserProfile.this).getImageLoader());
        } else {
            alert("Alert", "Something Wrong Please Try Later..");
        }
    }


    private void getUserDetails() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                VolleySingleTon.getDomainUrl() + "profile/profile_details", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(getLocalClassName(), "Response :- " + response);
                try {
                    jsonObject = new JSONObject(response);
                    setView();
                } catch (JSONException e) {
                    e.printStackTrace();
                    alert("Error", "Username not available, try another one");
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
        VolleySingleTon.getInstance(UserProfile.this).addToRequestQueue(UserProfile.this, stringRequest);
    }

    private void updateUserDetailsWithOutImage(final View view) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                VolleySingleTon.getDomainUrl() + "profile/update_profile", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(getLocalClassName(), "Response :- " + response);
                if (response.equalsIgnoreCase("1")) {
                    alert("Alert", "Profile updated successfully");
                    setEditable(false);
                    view.setSelected(false);
                } else {
                    alert("Alert", "Something went wrong, please try again later.!");
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
                params.put("name", name.getText().toString().trim());
                params.put("address", address.getText().toString().trim());
                params.put("image", "");
                params.put("contact", phoneNumber.getText().toString().trim());
                params.put("email", email.getText().toString().trim());
                params.put("latitude", "" + latLng.latitude);
                params.put("longitude", "" + latLng.longitude);
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
        VolleySingleTon.getInstance(UserProfile.this).addToRequestQueue(UserProfile.this, stringRequest);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        nameLayout.setErrorEnabled(false);
        phoneNumberLayout.setErrorEnabled(false);
        addressLayout.setErrorEnabled(false);
        postalCodeLayout.setErrorEnabled(false);
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
