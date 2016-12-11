package rkr.binatestation.pathrakaran.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import rkr.binatestation.pathrakaran.R;
import rkr.binatestation.pathrakaran.fragments.SMHome;
import rkr.binatestation.pathrakaran.modules.profile.UserProfile;
import rkr.binatestation.pathrakaran.utils.Constants;

import static rkr.binatestation.pathrakaran.utils.Constants.KEY_SP_USER_NAME;
import static rkr.binatestation.pathrakaran.utils.Constants.KEY_SP_USER_PHONE;
import static rkr.binatestation.pathrakaran.utils.Constants.KEY_SP_USER_TYPE;


public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "HomeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getSharedPreferences(getPackageName(), MODE_PRIVATE).getString(KEY_SP_USER_NAME, getString(R.string.app_name)));
            getSupportActionBar().setSubtitle(getSharedPreferences(getPackageName(), MODE_PRIVATE).getString(KEY_SP_USER_PHONE, getString(R.string.app_name)));
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        handleNavigationMenuItems(navigationView, getSharedPreferences(getPackageName(), MODE_PRIVATE).getString(KEY_SP_USER_TYPE, getString(R.string.app_name)));
    }

    private void handleNavigationMenuItems(NavigationView navigationView, String type) {
        Log.d(TAG, "handleNavigationMenuItems() called with: navigationView = [" + navigationView + "], type = [" + type + "]");
        Menu menu = navigationView.getMenu();
        if (Constants.USER_TYPE_AGENT.equalsIgnoreCase(type)) {
            navigationView.setCheckedItem(R.id.nav_AM_home);
            menu.setGroupVisible(R.id.nav_subscriberModule, false);
            menu.setGroupVisible(R.id.nav_suppliersModule, false);
        } else if (Constants.USER_TYPE_SUPPLIER.equalsIgnoreCase(type)) {
            navigationView.setCheckedItem(R.id.nav_SPM_home);
            menu.setGroupVisible(R.id.nav_subscriberModule, false);
            menu.setGroupVisible(R.id.nav_agentModule, false);
        } else {
            navigationView.setCheckedItem(R.id.nav_SM_home);
            menu.setGroupVisible(R.id.nav_agentModule, false);
            menu.setGroupVisible(R.id.nav_suppliersModule, false);
            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                    .replace(R.id.ABH_contentLayout, SMHome.newInstance())
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.global, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.G_logout) {
            logout();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        getSharedPreferences(getPackageName(), MODE_PRIVATE).edit().clear().apply();
        startActivity(new Intent(HomeActivity.this, SplashScreen.class));
        finish();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        Fragment fragment = null;
        switch (item.getItemId()) {
            case R.id.nav_SM_home:
                fragment = SMHome.newInstance();
                break;
            case R.id.nav_SM_profile:
            case R.id.nav_AM_profile:
            case R.id.nav_SPM_profile:
                startActivity(new Intent(HomeActivity.this, UserProfile.class));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        }

        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                    .replace(R.id.ABH_contentLayout, fragment)
                    .addToBackStack("")
                    .commit();
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
