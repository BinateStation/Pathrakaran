package rkr.binatestation.pathrakkaran.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import rkr.binatestation.pathrakkaran.modules.home.HomeActivity;
import rkr.binatestation.pathrakkaran.modules.login.LoginActivity;

import static rkr.binatestation.pathrakkaran.utils.Constants.KEY_SP_IS_LOGGED_IN;


/**
 * An activity which shows the app brand while initializing the application
 */
public class SplashScreen extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Start your app HomeActivity activity
        if (getSharedPreferences(getPackageName(), MODE_PRIVATE).getBoolean(KEY_SP_IS_LOGGED_IN, false)) {
            startActivity(new Intent(SplashScreen.this, HomeActivity.class));
        } else {
            startActivity(new Intent(SplashScreen.this, LoginActivity.class));
        }
        // close this activity
        finish();
    }
}
