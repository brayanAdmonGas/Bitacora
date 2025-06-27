package com.gestogas.gestoline.utils;

import android.content.Context;
import android.content.pm.PackageManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class UpdateChecker {

    public interface UpdateListener {
        void onUpdateAvailable(String version, String apkUrl);
        void onUpToDate();
        void onError(Exception e);
    }

    public static void check(Context context, String versionUrl, UpdateListener listener) {
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest request = new StringRequest(Request.Method.GET, versionUrl,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        String serverVersion = json.getString("version");
                        String apkUrl = json.getString("url");

                        String currentVersion = context.getPackageManager()
                                .getPackageInfo(context.getPackageName(), 0).versionName;

                        if (isNewerVersion(serverVersion, currentVersion)) {
                            listener.onUpdateAvailable(serverVersion, apkUrl);
                        } else {
                            listener.onUpToDate();
                        }

                    } catch (Exception e) {
                        listener.onError(e);
                    }
                },
                error -> listener.onError(error));
        queue.add(request);
    }

    public static boolean isNewerVersion(String server, String local) {
        String[] s = server.split("\\.");
        String[] l = local.split("\\.");
        int len = Math.max(s.length, l.length);
        for (int i = 0; i < len; i++) {
            int si = i < s.length ? Integer.parseInt(s[i]) : 0;
            int li = i < l.length ? Integer.parseInt(l[i]) : 0;
            if (si > li) return true;
            if (si < li) return false;
        }
        return false;
    }

    public static String getLastDownloadedVersion(Context context) {
        return context.getSharedPreferences("app_update", Context.MODE_PRIVATE)
                .getString("downloaded_version", null);
    }

    public static void saveDownloadedVersion(Context context, String version) {
        context.getSharedPreferences("app_update", Context.MODE_PRIVATE)
                .edit().putString("downloaded_version", version).apply();
    }

    public static void clearDownloadedVersion(Context context) {
        context.getSharedPreferences("app_update", Context.MODE_PRIVATE)
                .edit().remove("downloaded_version").apply();
    }
}
