package com.omni.omniguidersdkdemo;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

/**
 * Created by wiliiamwang on 14/12/2017.
 */

public class OGEmergencyActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);

        findViewById(R.id.activity_emergency_btn_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OGMapsActivity.emergencyNaviTo(OGEmergencyActivity.this, "1", "Stair");
            }
        });
    }
}
