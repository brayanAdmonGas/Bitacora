package com.gestogas.gestoline;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.gestogas.gestoline.utils.AppUpdater;
import com.gestogas.gestoline.utils.UpdateChecker;

import java.io.File;

public class MiGBitacora extends AppCompatActivity {

    private Button btnActualizar;
    private static final String VERSION_URL = "https://demo.gestogas.com/bitacora-api-app/app/Configuracion/version.json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mi_gbitacora);

        TextView versionText = findViewById(R.id.appVersion);

        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            versionText.setText("Versión " + version);
        } catch (PackageManager.NameNotFoundException e) {
            versionText.setText("Versión desconocida");
        }

        btnActualizar = findViewById(R.id.btnActualizar);
        btnActualizar.setVisibility(Button.GONE);

        AppUpdater.cleanupOldApk(this);

        String installedVersion = getInstalledVersion();
        String downloadedVersion = UpdateChecker.getLastDownloadedVersion(this);

        if (downloadedVersion != null && UpdateChecker.isNewerVersion(downloadedVersion, installedVersion)) {
            btnActualizar.setText("Instalar versión " + downloadedVersion);
            btnActualizar.setVisibility(Button.VISIBLE);
            btnActualizar.setOnClickListener(v -> installDownloadedApk());
        } else {
            checkForNewUpdate();
        }

    }

    private void checkForNewUpdate() {
        UpdateChecker.check(this, VERSION_URL, new UpdateChecker.UpdateListener() {
            @Override
            public void onUpdateAvailable(String version, String apkUrl) {
                btnActualizar.setText("Actualizar a versión " + version);
                btnActualizar.setVisibility(Button.VISIBLE);
                btnActualizar.setOnClickListener(v -> {
                    new AppUpdater(MiGBitacora.this).startDownload(apkUrl, version);
                });
            }

            @Override
            public void onUpToDate() {
                // No hacer nada
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void installDownloadedApk() {
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS), "miapp_update.apk");
        if (file.exists()) {
            Uri apkUri = FileProvider.getUriForFile(this, getPackageName() + ".provider", file);
            Intent installIntent = new Intent(Intent.ACTION_VIEW);
            installIntent.setDataAndType(apkUri, "application/vnd.android.package-archive");
            installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(installIntent);
        }
    }

    private String getInstalledVersion() {
        try {
            return getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (Exception e) {
            return "0.0.0";
        }
    }

    private void volverAHome() {
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            volverAHome();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onSupportNavigateUp() {
        volverAHome();
        return true;
    }
}