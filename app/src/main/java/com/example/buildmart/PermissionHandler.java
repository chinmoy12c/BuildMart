package com.example.buildmart;

import android.app.Activity;
import android.content.Context;

import androidx.core.app.ActivityCompat;

public class PermissionHandler {

    private static final int REQUEST_CODE = 552;
    private Context context;
    private String[] permissions;

    PermissionHandler(Context context,String[] permissions) {
        this.context = context;
        this.permissions = permissions;
    }

    public void requestPermissions() {
        ActivityCompat.requestPermissions((Activity) context, permissions, REQUEST_CODE);
    }
}
