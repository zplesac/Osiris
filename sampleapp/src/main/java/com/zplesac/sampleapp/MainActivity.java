package com.zplesac.sampleapp;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.zplesac.osiris.Osiris;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Osiris.getInstance().track();

        if (Osiris.getInstance().shouldShowRequest()) {
            Toast.makeText(this, "Show app rate dialog", Toast.LENGTH_SHORT).show();
        }
    }
}
