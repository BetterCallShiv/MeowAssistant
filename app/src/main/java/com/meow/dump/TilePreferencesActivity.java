package com.meow.dump;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;

import com.meow.dump.tiles.DnsTile;

public class TilePreferencesActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent != null && "android.service.quicksettings.action.QS_TILE_PREFERENCES".equals(intent.getAction())) {
            ComponentName component = intent.getParcelableExtra(Intent.EXTRA_COMPONENT_NAME);
            String className = component != null ? component.getClassName() : DnsTile.class.getName();
            if (className.equals(DnsTile.class.getName())) {
                try {
                    Intent targetIntent = new Intent("android.settings.PRIVATE_DNS_SETTINGS");
                    targetIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(targetIntent);
                } catch (Exception e) {
                    Intent fallbackIntent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
                    fallbackIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    try {
                        startActivity(fallbackIntent);
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }
            }
        }
        finish();
    }
}
