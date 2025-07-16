package com.gestogas.gestoline;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.gestogas.gestoline.utils.Constantes;
import com.gestogas.gestoline.utils.ToastUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MiGBitacora extends AppCompatActivity {

    String serverUrl = Constantes.URL_SERVIDOR + "Configuracion/version.json";
    ProgressDialog progressDialog;
    Button btnDescargarActualizacion;
    String updateVersion;
    String apkUrl;

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

        btnDescargarActualizacion = findViewById(R.id.btnDescargarActualizacion);

        btnDescargarActualizacion.setOnClickListener(v -> {
            obtenerUrlYDescargar();
        });

    }

    private void obtenerUrlYDescargar() {
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, serverUrl, null,
                response -> {
                    try {
                        updateVersion = response.getString("version");
                        apkUrl = response.getString("apk_url");
                        descargarConDownloadManager(apkUrl);
                    } catch (Exception e) {
                        Log.e("DescargaUpdate", "Parse error: " + e.getMessage());
                        ToastUtils.show(this, "Error en la respuesta del servidor", ToastUtils.ERROR);
                    }
                },
                error -> {
                    Log.e("DescargaUpdate", "Volley error: " + error.getMessage());
                    ToastUtils.show(this, "Error al conectar con el servidor", ToastUtils.ERROR);
                });

        queue.add(request);
    }

    private void descargarConDownloadManager(String url) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setTitle("Descargando actualización");
        request.setDescription("Descargando APK...");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,  "GBitacora-" + updateVersion + ".apk");
        request.setAllowedOverMetered(true);
        request.setAllowedOverRoaming(true);

        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        downloadManager.enqueue(request);

        ToastUtils.show(this, "Descarga iniciada", ToastUtils.SUCCESS);
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