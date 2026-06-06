package com.meow.dump;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.meow.dump.utils.RootUtils;
import com.meow.dump.utils.ScriptExecutor;

public class MainActivity extends Activity {
    
    private TextView rootStatusText;
    private ImageView rootStatusIcon;
    private float density;
    private boolean isDarkTheme;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        density = getResources().getDisplayMetrics().density;
        isDarkTheme = (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
        
        handlePopupIntent(getIntent());
        
        ScrollView scrollView = new ScrollView(this);
        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setPadding(dp(16), dp(16), dp(16), dp(16));
        
        final int bgColorStart = isDarkTheme ? 0xFF1C1B1F : 0xFFF3EDF7;
        final int bgColorEnd = isDarkTheme ? 0xFF313033 : 0xFFE0D8E6;
        final int cardColor = isDarkTheme ? 0xFF2B2930 : 0xFFF3EDF7;
        final int textColor = isDarkTheme ? 0xFFE6E1E5 : 0xFF1C1B1F;
        final int subTextColor = isDarkTheme ? 0xFFCAC4D0 : 0xFF49454F;
        final int titleColor = isDarkTheme ? 0xFFD0BCFF : 0xFF6750A4;
        
        GradientDrawable bg = new GradientDrawable(
            GradientDrawable.Orientation.TL_BR,
            new int[]{bgColorStart, bgColorEnd}
        );
        mainLayout.setBackgroundDrawable(bg);
        
        TextView title = new TextView(this);
        title.setText("Meow Assistant");
        title.setTextSize(20);
        title.setTextColor(titleColor);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, dp(16), 0, dp(16));
        mainLayout.addView(title);
        
        LinearLayout headerCard = createCard(cardColor);
        headerCard.setGravity(Gravity.CENTER);
        headerCard.setPadding(dp(24), dp(24), dp(24), dp(24));
        
        ImageView icon = new ImageView(this);
        icon.setImageResource(R.drawable.ic_launcher);
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(dp(64), dp(64));
        iconParams.setMargins(0, 0, 0, dp(16));
        headerCard.addView(icon, iconParams);
        
        TextView appTitle = new TextView(this);
        appTitle.setText("Meow Assistant");
        appTitle.setTextSize(24);
        appTitle.setTextColor(titleColor);
        appTitle.setGravity(Gravity.CENTER);
        headerCard.addView(appTitle);
        
        TextView subtitle = new TextView(this);
        subtitle.setText("Quick Settings Toolkit");
        subtitle.setTextSize(14);
        subtitle.setTextColor(subTextColor);
        subtitle.setGravity(Gravity.CENTER);
        headerCard.addView(subtitle);
        
        mainLayout.addView(headerCard);
        
        LinearLayout statusCard = createCard(cardColor);
        statusCard.setOrientation(LinearLayout.HORIZONTAL);
        statusCard.setGravity(Gravity.CENTER_VERTICAL);
        statusCard.setPadding(dp(20), dp(20), dp(20), dp(20));
        
        rootStatusIcon = new ImageView(this);
        rootStatusIcon.setImageResource(R.drawable.ic_root);
        GradientDrawable rootIconBg = new GradientDrawable();
        rootIconBg.setShape(GradientDrawable.OVAL);
        rootIconBg.setColor(isDarkTheme ? 0xFF4F378B : 0xFFEADDFF);
        rootStatusIcon.setBackgroundDrawable(rootIconBg);
        rootStatusIcon.setPadding(dp(12), dp(12), dp(12), dp(12));
        statusCard.addView(rootStatusIcon, dp(48), dp(48));
        
        LinearLayout statusTextLayout = new LinearLayout(this);
        statusTextLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        textParams.setMargins(dp(16), 0, 0, 0);
        
        TextView statusLabel = new TextView(this);
        statusLabel.setText("Root Status");
        statusLabel.setTextSize(12);
        statusLabel.setTextColor(subTextColor);
        statusTextLayout.addView(statusLabel);
        
        rootStatusText = new TextView(this);
        rootStatusText.setText("Checking...");
        rootStatusText.setTextSize(16);
        rootStatusText.setTextColor(textColor);
        rootStatusText.setTypeface(null, android.graphics.Typeface.BOLD);
        statusTextLayout.addView(rootStatusText);
        
        statusCard.addView(statusTextLayout, textParams);
        mainLayout.addView(statusCard);
        
        TextView tilesLabel = new TextView(this);
        tilesLabel.setText("About Me");
        tilesLabel.setTextSize(16);
        tilesLabel.setTextColor(textColor);
        tilesLabel.setTypeface(null, android.graphics.Typeface.BOLD);
        tilesLabel.setPadding(0, dp(16), 0, dp(12));
        tilesLabel.setGravity(android.view.Gravity.CENTER);
        mainLayout.addView(tilesLabel);
        
        TextView tilesDesc = new TextView(this);
        tilesDesc.setText("Meow puts useful shortcuts right in your Quick Settings panel. No need to dig through apps or menus, just swipe down and tap.\n\nSome tiles work out of the box, others need root access, and a few are built specifically for Meow's modules.");
        tilesDesc.setTextSize(14);
        tilesDesc.setTextColor(subTextColor);
        tilesDesc.setPadding(dp(16), 0, dp(16), dp(16));
        tilesDesc.setGravity(android.view.Gravity.CENTER);
        mainLayout.addView(tilesDesc);
        
        LinearLayout row1 = new LinearLayout(this);
        row1.setOrientation(LinearLayout.HORIZONTAL);
        
        LinearLayout tileKill = createTile("Kill App", "QuiteKill", Color.parseColor("#B3261E"), R.drawable.ic_kill, cardColor, textColor);
        tileKill.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                testScript(ScriptExecutor.SCRIPT_KILL_APP, ScriptExecutor.URL_QUITEKILL, "QuiteKill");
            }
        });
        row1.addView(tileKill, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        
        LinearLayout tilePif = createTile("Play Integrity", "PIF", Color.parseColor("#6750A4"), R.drawable.ic_shield, cardColor, textColor);
        tilePif.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                testScript(ScriptExecutor.SCRIPT_PIF, ScriptExecutor.URL_PIF, "Play Integrity Fix");
            }
        });
        LinearLayout.LayoutParams pifParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        pifParams.setMargins(dp(8), 0, 0, 0);
        row1.addView(tilePif, pifParams);
        
        mainLayout.addView(row1);
        
        LinearLayout row2 = new LinearLayout(this);
        row2.setOrientation(LinearLayout.HORIZONTAL);
        row2.setPadding(0, dp(8), 0, 0);
        
        LinearLayout tileGms = createTile("Kill GMS", "Kill GMS", Color.parseColor("#7D5260"), R.drawable.ic_block, cardColor, textColor);
        tileGms.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                testScript(ScriptExecutor.SCRIPT_KILL_GMS, ScriptExecutor.URL_PIF, "Kill GMS");
            }
        });
        row2.addView(tileGms, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        
        LinearLayout tileKey = createTile("Update Keybox", "Keybox", Color.parseColor("#625B71"), R.drawable.ic_key, cardColor, textColor);
        tileKey.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                testScript(ScriptExecutor.SCRIPT_KEYBOX, ScriptExecutor.URL_PIF, "Update Keybox");
            }
        });
        LinearLayout.LayoutParams keyParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        keyParams.setMargins(dp(8), 0, 0, 0);
        row2.addView(tileKey, keyParams);
        
        mainLayout.addView(row2);
        
        LinearLayout row3 = new LinearLayout(this);
        row3.setOrientation(LinearLayout.HORIZONTAL);
        row3.setPadding(0, dp(8), 0, 0);
        
        LinearLayout tileTarget = createTile("Refresh Target", "Target", Color.parseColor("#006C4C"), R.drawable.ic_refresh, cardColor, textColor);
        tileTarget.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                testScript(ScriptExecutor.SCRIPT_REFRESH_TARGET, ScriptExecutor.URL_PIF, "Refresh Target");
            }
        });
        row3.addView(tileTarget, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        
        LinearLayout tileHma = createTile("Import HMA", "HMA", Color.parseColor("#00639B"), R.drawable.ic_import, cardColor, textColor);
        tileHma.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                testScript(ScriptExecutor.SCRIPT_IMPORT_HMA, ScriptExecutor.URL_PIF, "Import HMA");
            }
        });
        LinearLayout.LayoutParams hmaParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        hmaParams.setMargins(dp(8), 0, 0, 0);
        row3.addView(tileHma, hmaParams);
        
        mainLayout.addView(row3);
        
        LinearLayout row4 = new LinearLayout(this);
        row4.setOrientation(LinearLayout.HORIZONTAL);
        row4.setPadding(0, dp(8), 0, 0);
        
        LinearLayout tileHide = createTile("Hide Lineage", "Lineage", Color.parseColor("#984061"), R.drawable.ic_hide, cardColor, textColor);
        tileHide.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                testScript(ScriptExecutor.SCRIPT_HIDE_LINEAGE, ScriptExecutor.URL_PIF, "Hide Lineage");
            }
        });
        row4.addView(tileHide, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        
        LinearLayout tileWebui = createTile("Open WebUI", "WebUI", Color.parseColor("#7D5260"), R.drawable.ic_web, cardColor, textColor);
        tileWebui.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                testScript(ScriptExecutor.SCRIPT_OPEN_WEBUI, ScriptExecutor.URL_PIF, "Open WebUI");
            }
        });
        LinearLayout.LayoutParams webuiParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        webuiParams.setMargins(dp(8), 0, 0, 0);
        row4.addView(tileWebui, webuiParams);
        
        mainLayout.addView(row4);
        
        LinearLayout row5 = new LinearLayout(this);
        row5.setOrientation(LinearLayout.HORIZONTAL);
        row5.setPadding(0, dp(8), 0, 0);
        
        LinearLayout tileLock = createTile("Lock Device", "Screen", Color.parseColor("#1C1B1F"), R.drawable.ic_lock, cardColor, textColor);
        tileLock.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Use QS tile for lock", Toast.LENGTH_SHORT).show();
            }
        });
        row5.addView(tileLock, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        
        LinearLayout tileCaffeine = createTile("Caffeine", "Timeout", Color.parseColor("#8B6914"), R.drawable.ic_coffee, cardColor, textColor);
        tileCaffeine.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Use QS tile for caffeine", Toast.LENGTH_SHORT).show();
            }
        });
        LinearLayout.LayoutParams caffeineParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        caffeineParams.setMargins(dp(8), 0, 0, 0);
        row5.addView(tileCaffeine, caffeineParams);
        
        mainLayout.addView(row5);
        
        LinearLayout row6 = new LinearLayout(this);
        row6.setOrientation(LinearLayout.HORIZONTAL);
        row6.setPadding(0, dp(8), 0, 0);
        
        LinearLayout tileScreenshot = createTile("Screenshot", "Capture", Color.parseColor("#4285F4"), R.drawable.ic_screenshot, cardColor, textColor);
        tileScreenshot.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Use QS tile for screenshot", Toast.LENGTH_SHORT).show();
            }
        });
        row6.addView(tileScreenshot, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        
        LinearLayout tileMobileData = createTile("Mobile Data", "Toggle", Color.parseColor("#34A853"), R.drawable.ic_mobile_data, cardColor, textColor);
        tileMobileData.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Use QS tile for mobile data", Toast.LENGTH_SHORT).show();
            }
        });
        LinearLayout.LayoutParams mobileParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        mobileParams.setMargins(dp(8), 0, 0, 0);
        row6.addView(tileMobileData, mobileParams);
        
        mainLayout.addView(row6);
        
        LinearLayout row7 = new LinearLayout(this);
        row7.setOrientation(LinearLayout.HORIZONTAL);
        row7.setPadding(0, dp(8), 0, dp(8));
        
        LinearLayout tileWiFi = createTile("WiFi", "Toggle", Color.parseColor("#006C4C"), R.drawable.ic_wifi, cardColor, textColor);
        tileWiFi.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Use QS tile for WiFi", Toast.LENGTH_SHORT).show();
            }
        });
        row7.addView(tileWiFi, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        
        LinearLayout tileSupport = createTile("Support", "Donate", Color.parseColor(isDarkTheme ? "#FF8C69" : "#FF6B6B"), R.drawable.ic_heart, cardColor, textColor);
        tileSupport.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openUrl("https://meowdump.github.io");
            }
        });
        LinearLayout.LayoutParams supportParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        supportParams.setMargins(dp(8), 0, 0, 0);
        row7.addView(tileSupport, supportParams);
        
        mainLayout.addView(row7);
        
        LinearLayout linkRow = new LinearLayout(this);
        linkRow.setOrientation(LinearLayout.HORIZONTAL);
        linkRow.setPadding(0, dp(8), 0, 0);
        
        Button btnGithub = createStyledButton("Source Code", Color.parseColor("#24292E"));
        btnGithub.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openUrl("https://github.com/MeowDump/MeowAssistant");
            }
        });
        linkRow.addView(btnGithub, new LinearLayout.LayoutParams(0, dp(48), 1));
        
        Button btnTelegram = createStyledButton("Feedback/Bug", Color.parseColor("#0088CC"));
        btnTelegram.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openUrl("https://t.me/MeowDump");
            }
        });
        LinearLayout.LayoutParams teleParams = new LinearLayout.LayoutParams(0, dp(48), 1);
        teleParams.setMargins(dp(8), 0, 0, 0);
        linkRow.addView(btnTelegram, teleParams);
        
        mainLayout.addView(linkRow);
        
        scrollView.addView(mainLayout);
        setContentView(scrollView);
        
        checkRootStatus();
    }
    
    private void handlePopupIntent(Intent intent) {
        if (intent != null && "android.intent.action.MAIN".equals(intent.getAction())) {
            String message = intent.getStringExtra("meowna");
            if (message != null && !message.isEmpty()) {
                showCustomPopup(message);
            }
        }
    }
    
    private void showCustomPopup(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("MeowDump");
        builder.setMessage(message);
        
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        
        builder.setCancelable(true);
        
        final AlertDialog dialog = builder.create();
        
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            public void onShow(DialogInterface d) {
                final Button positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                if (positive != null) {
                    positive.setBackgroundColor(0xFF6750A4);
                    positive.setTextColor(0xFFFFFFFF);
                    positive.setAllCaps(false);
                }
            }
        });
        
        dialog.show();
    }
    
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handlePopupIntent(intent);
    }
    
    private LinearLayout createCard(int cardColor) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(cardColor);
        bg.setCornerRadius(dp(16));
        card.setBackgroundDrawable(bg);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, dp(16));
        card.setLayoutParams(params);
        return card;
    }
    
    private LinearLayout createTile(String title, String subtitle, int iconBgColor, int iconRes, int cardColor, int textColor) {
        LinearLayout tile = new LinearLayout(this);
        tile.setOrientation(LinearLayout.VERTICAL);
        tile.setGravity(Gravity.CENTER);
        tile.setPadding(dp(16), dp(16), dp(16), dp(16));
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(isDarkTheme ? 0xFF3D3B40 : 0xFFFFFFFF);
        bg.setCornerRadius(dp(16));
        tile.setBackgroundDrawable(bg);
        
        ImageView icon = new ImageView(this);
        icon.setImageResource(iconRes);
        GradientDrawable iconBg = new GradientDrawable();
        iconBg.setColor(iconBgColor);
        iconBg.setCornerRadius(dp(12));
        icon.setBackgroundDrawable(iconBg);
        icon.setPadding(dp(8), dp(8), dp(8), dp(8));
        tile.addView(icon, dp(40), dp(40));
        
        TextView tvTitle = new TextView(this);
        tvTitle.setText(title);
        tvTitle.setTextSize(14);
        tvTitle.setTextColor(textColor);
        tvTitle.setTypeface(null, android.graphics.Typeface.BOLD);
        tvTitle.setPadding(0, dp(8), 0, 0);
        tvTitle.setGravity(Gravity.CENTER);
        tile.addView(tvTitle);
        
        TextView tvSub = new TextView(this);
        tvSub.setText(subtitle);
        tvSub.setTextSize(12);
        tvSub.setTextColor(isDarkTheme ? 0xFFCAC4D0 : 0xFF49454F);
        tvSub.setGravity(Gravity.CENTER);
        tile.addView(tvSub);
        
        return tile;
    }
    
    private Button createStyledButton(String text, int color) {
        Button btn = new Button(this);
        btn.setText(text);
        btn.setTextColor(Color.WHITE);
        btn.setAllCaps(false);
        
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(color);
        bg.setCornerRadius(dp(12));
        btn.setBackgroundDrawable(bg);
        
        return btn;
    }
    
    private int dp(int px) {
        return (int) (px * density);
    }
    
    private void checkRootStatus() {
        if (RootUtils.hasRootAccess()) {
            rootStatusText.setText("Rooted Access Granted");
            rootStatusIcon.setImageResource(R.drawable.ic_root_ok);
        } else {
            rootStatusText.setText("Root Access Denied");
            rootStatusIcon.setImageResource(R.drawable.ic_root_error);
            Toast.makeText(this, "Root access not granted. This app requires root to function.", Toast.LENGTH_LONG).show();
        }
    }
    
    private void testScript(final String scriptPath, final String moduleUrl, final String moduleName) {
        final MainActivity activity = this;
        ScriptExecutor.executeScript(this, scriptPath, moduleUrl, new ScriptExecutor.ExecutionCallback() {
            public void onSuccess(String output) {
                Toast.makeText(activity, moduleName + ": Success", Toast.LENGTH_SHORT).show();
            }
            
            public void onError(String error) {
                Toast.makeText(activity, moduleName + ": Failed", Toast.LENGTH_SHORT).show();
            }
            
            public void onModuleMissing(String url) {
                showModuleMissingDialog(moduleName, url);
            }
        });
    }
    
    private void showModuleMissingDialog(final String moduleName, final String url) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(moduleName + " Not Installed");
        builder.setMessage("The required module is not installed. Download it?");
        
        builder.setPositiveButton("Download", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                openUrl(url);
            }
        });
        
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        
        final AlertDialog dialog = builder.create();
        
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            public void onShow(DialogInterface d) {
                final Button positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                final Button negative = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                
                if (positive != null) {
                    positive.setBackgroundColor(0xFF6750A4);
                    positive.setTextColor(0xFFFFFFFF);
                    positive.setAllCaps(false);
                }
                if (negative != null) {
                    negative.setBackgroundColor(0x00FFFFFF);
                    negative.setTextColor(0xFF6750A4);
                    negative.setAllCaps(false);
                }
            }
        });
        
        dialog.show();
    }
    
    private void openUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }
}
