package com.meow.dump.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

public class ScriptExecutor {
    
    public static final String SCRIPT_KILL_APP = "/data/adb/modules/QuiteKill/QuiteKill.sh";
    public static final String SCRIPT_PIF = "/data/adb/modules/playintegrityfix/action.sh";
    public static final String SCRIPT_KILL_GMS = "/data/adb/modules/playintegrityfix/webroot/common_scripts/gms.sh";
    public static final String SCRIPT_KEYBOX = "/data/adb/modules/playintegrityfix/webroot/common_scripts/key.sh";
    public static final String SCRIPT_REFRESH_TARGET = "/data/adb/modules/playintegrityfix/webroot/common_scripts/target.sh";
    public static final String SCRIPT_IMPORT_HMA = "/data/adb/modules/playintegrityfix/webroot/common_scripts/hma.sh";
    public static final String SCRIPT_HIDE_LINEAGE = "/data/adb/modules/playintegrityfix/webroot/common_scripts/override_lineage.sh";
    public static final String SCRIPT_OPEN_WEBUI = "/data/adb/modules/playintegrityfix/webroot/common_scripts/webui.sh";
    
    public static final String URL_QUITEKILL = "https://github.com/MeowDump/QuietKill/releases";
    public static final String URL_PIF = "https://github.com/MeowDump/Integrity-Box/releases";
    
    public interface ExecutionCallback {
        void onSuccess(String output);
        void onError(String error);
        void onModuleMissing(String moduleUrl);
    }
    
    public static void executeScript(final Context context, final String scriptPath, final String moduleUrl, final ExecutionCallback callback) {
        if (!RootUtils.hasRootAccess()) {
            callback.onError("Please grant Root access");
            return;
        }
        
        if (!RootUtils.fileExists(scriptPath)) {
            callback.onModuleMissing(moduleUrl);
            return;
        }
        
        if (!RootUtils.isExecutable(scriptPath)) {
            RootUtils.makeExecutable(scriptPath);
        }
        
        new Thread(new Runnable() {
            public void run() {
                final RootUtils.CommandResult result = RootUtils.runCommand("sh " + scriptPath);
                
                if (context instanceof Activity) {
                    final Activity activity = (Activity) context;
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            if (result.isSuccess()) {
                                callback.onSuccess(result.getOutput());
                            } else {
                                callback.onError(result.getError());
                            }
                        }
                    });
                }
            }
        }).start();
    }
    
    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
    
    public static void openUrl(Context context, String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        context.startActivity(intent);
    }
}
