package rkr.binatestation.pathrakaran.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import rkr.binatestation.pathrakaran.R;
import rkr.binatestation.pathrakaran.utils.Constants;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SplashScreen extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.splash_screen);

        // Start your app HomeActivity activity
        if (getSharedPreferences(getPackageName(), MODE_PRIVATE).getBoolean(Constants.KEY_IS_LOGGED_IN, false)) {
            startActivity(new Intent(SplashScreen.this, HomeActivity.class));
        } else {
            startActivity(new Intent(SplashScreen.this, LoginActivity.class));
        }
        // close this activity
        finish();
    }
}
