package rkr.larc.mynewspaperagent.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import rkr.larc.mynewspaperagent.R;
import rkr.larc.mynewspaperagent.fragments.SM_Home;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Aashique Cr");
            getSupportActionBar().setSubtitle("Something write here");
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        handleNavigationMenuItems(navigationView, "");
    }

    private void handleNavigationMenuItems(NavigationView navigationView, String type) {
        Menu menu = navigationView.getMenu();
        if (type.equalsIgnoreCase("agent_module")) {
            navigationView.setCheckedItem(R.id.nav_AM_home);
            menu.setGroupVisible(R.id.nav_subscriberModule, false);
            menu.setGroupVisible(R.id.nav_suppliersModule, false);
        } else if (type.equalsIgnoreCase("suppliers_module")) {
            navigationView.setCheckedItem(R.id.nav_SPM_home);
            menu.setGroupVisible(R.id.nav_subscriberModule, false);
            menu.setGroupVisible(R.id.nav_agentModule, false);
        } else {
            navigationView.setCheckedItem(R.id.nav_SM_home);
            menu.setGroupVisible(R.id.nav_agentModule, false);
            menu.setGroupVisible(R.id.nav_suppliersModule, false);
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                    .replace(R.id.ABH_contentLayout, new SM_Home())
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
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        Fragment fragment = null;
        switch (item.getItemId()) {
            case R.id.nav_SM_home:
                fragment = new SM_Home();
                break;
            case R.id.nav_SM_profile:
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
