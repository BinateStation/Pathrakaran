package rkr.larc.mynewspaperagent.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.widget.SearchView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import rkr.larc.mynewspaperagent.R;

public class MapPicker extends FragmentActivity implements OnMapReadyCallback {
    LatLng latLng;
    Marker marker;
    SearchView mapSearch;
    SupportMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_picker);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.MP_map);
        mapSearch = (SearchView) findViewById(R.id.MP_mapSearch);
        latLng = new LatLng(getIntent().getDoubleExtra("UP_LATITUDE", 0.0), getIntent().getDoubleExtra("UP_LONGITUDE", 0.0));
        mapFragment.getMapAsync(this);
        mapSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
//                LatLng latLng = Util.getLocationFromAddress(MapPicker.this, query);

                if (latLng != null) {
                    MapPicker.this.latLng = latLng;
                    mapFragment.getMapAsync(MapPicker.this);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
//                LatLng latLng = Util.getLocationFromAddress(MapPicker.this, newText);
                if (latLng != null) {
                    MapPicker.this.latLng = latLng;
                    mapFragment.getMapAsync(MapPicker.this);
                }
                return true;

            }
        });

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        // Add a marker in Sydney and move the camera

        if (latLng != null) {
//           marker= googleMap.addMarker(new MarkerOptions().position(latLng).title(Util.getCompleteAddressString(MapPicker.this,latLng)));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                googleMap.setMyLocationEnabled(true);
                googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                googleMap.getUiSettings().setCompassEnabled(true);
                googleMap.getUiSettings().setTiltGesturesEnabled(true);
                googleMap.getUiSettings().setAllGesturesEnabled(true);
                googleMap.getUiSettings().setMapToolbarEnabled(true);
            }
            googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {

                    MapPicker.this.latLng = latLng;
                    marker.setPosition(latLng);
//                    marker.setTitle(Util.getCompleteAddressString(MapPicker.this, latLng));


                }
            });
        }

    }

    @Override
    public void onBackPressed() {
        setResult(101, new Intent().putExtra("BPMPA_LATITUDE", latLng.latitude).putExtra("BPMPA_LONGITUDE", latLng.longitude));
        finish();
    }
}
