package com.tc.tech.cru.techcrunch.techcru;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

//import com.anythink.core.api.ATSDK;
//import com.facebook.ads.AudienceNetworkAds;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        // AudienceNetwork init
//        AudienceNetworkAds.initialize(this);
//        // ATSDK init
//        ATSDK.init(this, "a6656f0a726cfd", "a3399d82e8974e32e0da564a62d971450");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(0); // This ensures white text on the status bar
        }

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(getApplicationContext(), Activity1.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        }, 1200);

    }

}