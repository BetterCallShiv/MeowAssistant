package com.meow.dump.tiles;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.widget.Toast;

import com.meow.dump.utils.RootUtils;

public class DnsTile extends TileService {
    
    private static final String DEFAULT_LABEL = "DNS";
    
    @Override
    public void onClick() {
        super.onClick();
        
        if (!RootUtils.hasRootAccess()) {
            Toast.makeText(this, "Root Required", Toast.LENGTH_LONG).show();
            return;
        }
        
        toggleDns();
    }
    
    
    @TargetApi(Build.VERSION_CODES.Q)
    @Override
    public void onLongClick() {
        Intent intent = new Intent("android.settings.PRIVATE_DNS_SETTINGS");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
    
    private void toggleDns() {
        final boolean isEnabled = isDnsEnabled();
        final DnsTile context = this;
        
        RootUtils.collapseStatusBar();
        
        new Thread(new Runnable() {
            public void run() {
                boolean success = false;
                try {
                    if (isEnabled) {
                        RootUtils.runCommand("settings put global private_dns_mode off");
                    } else {
                        String specifier = getDnsSpecifier();
                        if (specifier != null && !specifier.isEmpty() && !specifier.equals("null")) {
                            RootUtils.runCommand("settings put global private_dns_mode hostname");
                        } else {
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                public void run() {
                                    Toast.makeText(context, "No DNS provider configured", Toast.LENGTH_LONG).show();
                                }
                            });
                            return;
                        }
                    }
                    success = true;
                } catch (Exception e) {
                    success = false;
                }

                final boolean newState = !isEnabled;
                final boolean finalSuccess = success;
                
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    public void run() {
                        if (finalSuccess) {
                            updateTileState(newState);
                            Toast.makeText(context, "DNS " + (newState ? "ON" : "OFF"), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "DNS toggle failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).start();
    }
    
    private boolean isDnsEnabled() {
        try {
            RootUtils.CommandResult result = RootUtils.runCommand("settings get global private_dns_mode");
            String mode = result.getOutput().trim();
            return "hostname".equals(mode);
        } catch (Exception e) {
            return false;
        }
    }
    
    private String getDnsSpecifier() {
        try {
            RootUtils.CommandResult result = RootUtils.runCommand("settings get global private_dns_specifier");
            return result.getOutput().trim();
        } catch (Exception e) {
            return null;
        }
    }
    
    private void updateTileState(boolean enabled) {
        Tile tile = getQsTile();
        if (tile != null) {
            tile.setState(enabled ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
            tile.setLabel(enabled ? "DNS ON" : DEFAULT_LABEL);
            tile.updateTile();
        }
    }
    
    @Override
    public void onStartListening() {
        super.onStartListening();
        updateTileState(isDnsEnabled());
    }
}
