package com.gestogas.gestoline;

import static com.gestogas.gestoline.utils.Constantes.URL_SERVIDOR;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.gestogas.gestoline.adapter.adapterImagen;
import com.gestogas.gestoline.controllers.AppController;
import com.gestogas.gestoline.data.dataImagen;
import com.gestogas.gestoline.utils.DialogHelper;
import com.gestogas.gestoline.utils.TecladoUtils;
import com.gestogas.gestoline.utils.ToastUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.os.Environment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Evidencias extends AppCompatActivity implements adapterImagen.OnEvidenciaEliminadaListener{

    private String id, categoria, carpeta, rutalista;
    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private static final int REQUEST_IMAGE = 100;
    private static final int REQUEST_CAMERA = 101;
    private Uri cameraImageUri;
    private String ruta, rutadelete;
    ImageView ImgResultado;
    TextView Mensaje;

    FloatingActionButton btnSelectImage;
    private RecyclerView recyclerView;
    private adapterImagen adapter;
    private List<dataImagen> itemList;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_evidencias);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        id = getIntent().getStringExtra("id");
        categoria = getIntent().getStringExtra("categoria");
        carpeta = getIntent().getStringExtra("carpeta");
        String titulo = getIntent().getStringExtra("titulo");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(titulo);
        }

        TextView RazonSocial = findViewById(R.id.RazonSocial);
        RazonSocial.setText(AppController.getInstance().GetRazonSocial());

        Mensaje = findViewById(R.id.Mensaje);
        ImgResultado = findViewById(R.id.ImgResultado);
        btnSelectImage = findViewById(R.id.btnSelectImage);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        if(categoria.equals("Recepcion")){
            rutalista = URL_SERVIDOR + carpeta + "/lista-evidencia.php?id=" + id;
            ruta = URL_SERVIDOR + carpeta + "/agregar-evidencia.php";
            rutadelete = URL_SERVIDOR + carpeta + "/eliminar-evidencia.php";
        }else if(categoria.equals("MantenimientoCorrectivo")){
            rutalista = URL_SERVIDOR + carpeta + "/lista-evidencia-correctivo.php?id=" + id;
            ruta = URL_SERVIDOR + carpeta + "/agregar-evidencia-correctivo.php";
            rutadelete = URL_SERVIDOR + carpeta + "/eliminar-evidencia-correctivo.php";

        }else if(categoria.equals("MantenimientoPreventivo")){
            rutalista = URL_SERVIDOR + carpeta + "/lista-evidencia-preventivo.php?id=" + id;
            ruta = URL_SERVIDOR + carpeta + "/agregar-evidencia-preventivo.php";
            rutadelete = URL_SERVIDOR + carpeta + "/eliminar-evidencia-preventivo.php";

        }

        itemList = new ArrayList<>();
        adapter = new adapterImagen(this,itemList,rutadelete,this);
        recyclerView.setAdapter(adapter);

        requestQueue = Volley.newRequestQueue(this);
        fetchEvidencia();

        btnSelectImage.setOnClickListener(v -> showImagePickerDialog());

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                // Oculta el FAB mientras se hace scroll hacia abajo
                if (dy > 0 && btnSelectImage.isShown()) {
                    btnSelectImage.hide();
                }
            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                // Si el usuario deja de hacer scroll, vuelve a mostrar el FAB
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    btnSelectImage.show();
                }
            }
        });

    }

    @Override
    public void onEvidenciaEliminada() {
        fetchEvidencia();
    }

    private void fetchEvidencia(){

        DialogHelper.showProgressDialog(this);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, rutalista, null,
                new Response.Listener<JSONArray>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onResponse(JSONArray response) {

                        try {
                            // Limpia la lista actual
                            itemList.clear();

                            // Recorre el JSONArray
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jsonObject = response.getJSONObject(i);

                                int id = jsonObject.getInt("id");
                                String ruta = jsonObject.getString("ruta");
                                String nombre = jsonObject.getString("nombre");

                                // Crea un objeto Item y lo añade a la lista
                                dataImagen item = new dataImagen(id, ruta, nombre);
                                itemList.add(item);
                            }

                            // Notifica al adaptador que los datos han cambiado
                            adapter.notifyDataSetChanged();
                            DialogHelper.hideProgressDialog();
                            ocultarError();

                        } catch (JSONException e) {
                            DialogHelper.hideProgressDialog();
                            mostrarError();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mostrarError();
                        DialogHelper.hideProgressDialog();
                    }
                });

        requestQueue.add(jsonArrayRequest);

    }

    private void mostrarError() {
        Mensaje.setVisibility(View.VISIBLE);
        Mensaje.setText("No se encontró información para mostrar");
        ImgResultado.setImageResource(R.drawable.icon_sin_informacion);
        ImgResultado.setVisibility(View.VISIBLE);
    }

    private void ocultarError() {
        Mensaje.setVisibility(View.GONE);
        ImgResultado.setVisibility(View.GONE);
    }

    private void checkCameraPermissionAndOpenCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // No tiene permiso, solicítalo
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.CAMERA,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    },
                    REQUEST_CAMERA_PERMISSION);
        } else {
            // Ya tiene permiso, abre la cámara
            openCamera();
        }
    }

    private void showImagePickerDialog() {
        String[] options = {"Cámara", "Galería"};
        new AlertDialog.Builder(this)
                .setTitle("Seleccionar imagen desde")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        checkCameraPermissionAndOpenCamera(); // Cámara
                    } else {
                        openGallery(); // Galería
                    }
                })
                .show();
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "camera_image_" + System.currentTimeMillis() + ".jpg");
        cameraImageUri = FileProvider.getUriForFile(
                this,
                getPackageName() + ".provider",
                photoFile
        );
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            boolean granted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    granted = false;
                    break;
                }
            }

            if (granted) {
                openCamera();
            } else {
                ToastUtils.show(this, "Permisos denegados", ToastUtils.INFO);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Bitmap bitmap = null;

            if (requestCode == REQUEST_IMAGE && data != null) {
                Uri imageUri = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (requestCode == REQUEST_CAMERA && cameraImageUri != null) {
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), cameraImageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (bitmap != null) {
                uploadImage(bitmap);
            }
        }
    }

    private void uploadImage(Bitmap bitmap) {
        DialogHelper.showProgressDialog(this);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos);
        String imageBase64 = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);

        StringRequest request = new StringRequest(Request.Method.POST, ruta, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                fetchEvidencia();
                DialogHelper.hideProgressDialog();
                ToastUtils.show(Evidencias.this, "Imagen subida con éxito", ToastUtils.SUCCESS);

            }
            }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                ToastUtils.show(Evidencias.this, "Error al subir la imagen", ToastUtils.INFO);
                DialogHelper.hideProgressDialog();

            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", id);
                params.put("ruta", URL_SERVIDOR + carpeta);
                params.put("image", imageBase64);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        TecladoUtils.handleTouchEvent(this, event);
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            setResult(RESULT_OK);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onSupportNavigateUp() {
        setResult(RESULT_OK);
        finish();
        return true;
    }



}