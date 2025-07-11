package com.gestogas.gestoline.mantenimiento;

import static com.gestogas.gestoline.utils.Constantes.URL_HOST;
import static com.gestogas.gestoline.utils.Constantes.URL_SERVIDOR;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.gestogas.gestoline.R;
import com.gestogas.gestoline.adapter.adapterDetectoresHumo;
import com.gestogas.gestoline.adapter.adapterMantenimientoExtintor;
import com.gestogas.gestoline.controllers.AppController;
import com.gestogas.gestoline.data.dataDetectoresHumo;
import com.gestogas.gestoline.data.dataExtintores;
import com.gestogas.gestoline.utils.DialogHelper;
import com.gestogas.gestoline.utils.TecladoUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MantenimientoPreventivoDetectorHumoDetalle extends AppCompatActivity {

    String idMantenimiento, NumeroEquipo, NombreEquipo, numVerificacion;
    private RecyclerView recyclerView;
    private adapterDetectoresHumo adapter;
    private List<dataDetectoresHumo> itemList;
    private RequestQueue requestQueue;
    private int Idestacion;
    ImageView ImgResultado;
    TextView Mensaje;

    FloatingActionButton Editar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mantenimiento_preventivo_detector_humo_detalle);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        Idestacion = AppController.getInstance().GetIdestacion();
        idMantenimiento = getIntent().getStringExtra("idMantenimiento");
        NumeroEquipo = getIntent().getStringExtra("NumeroEquipo");
        NombreEquipo = getIntent().getStringExtra("NombreEquipo");

        TextView RazonSocial = findViewById(R.id.RazonSocial);
        RazonSocial.setText(AppController.getInstance().GetRazonSocial());

        Editar = findViewById(R.id.Editar);
        Mensaje = findViewById(R.id.Mensaje);
        ImgResultado = findViewById(R.id.ImgResultado);

        recyclerView = findViewById(R.id.Recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        itemList = new ArrayList<>();
        adapter = new adapterDetectoresHumo(this,itemList);
        recyclerView.setAdapter(adapter);

        requestQueue = Volley.newRequestQueue(this);

        Editar.setOnClickListener(v -> {

            Intent didactic = new Intent(this, MantenimientoPreventivoDetectorHumo.class);
            didactic.putExtra("idMantenimiento", idMantenimiento);
            didactic.putExtra("NumeroEquipo", NumeroEquipo);
            didactic.putExtra("NombreEquipo", NombreEquipo);
            didactic.putExtra("numVerificacion", numVerificacion);
            didactic.putExtra("NumeroPagina","1");
            didactic.putExtra("estado", "1");
            startActivityForResult(didactic, 1);

        });

        fetchDetectorHumo();
    }

    private void fetchDetectorHumo(){

        String rutalista = URL_SERVIDOR + "Mantenimiento/lista-detectores-humo-mantenimiento.php?idMantenimiento=" + idMantenimiento;
        DialogHelper.showProgressDialog(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, rutalista, null,
                new Response.Listener<JSONObject>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            itemList.clear(); // Limpia lista actual

                            String folio = response.getString("folio");
                            String fechacreacion = response.getString("fechacreacion");
                            String hora = response.getString("hora");

                            String IDFPR = response.getString("IDFPR");
                            String IDFPS = response.getString("IDFPS");
                            String FPR = response.getString("FPR");
                            String FPS = response.getString("FPS");
                            String PFPR = response.getString("PFPR");
                            String PFPS = response.getString("PFPS");

                            TextView TxtTitulo = findViewById(R.id.TxtTitulo);
                            TxtTitulo.setText(NombreEquipo);

                            TextView TxtFolio = findViewById(R.id.TxtFolio);
                            TxtFolio.setText(folio);

                            TextView TxtFechaHora = findViewById(R.id.TxtFechaHora);
                            TxtFechaHora.setText(fechacreacion + " " + hora);

                            ImageView ImagePR = findViewById(R.id.ImagePR);
                            ImageView ImagePS = findViewById(R.id.ImagePS);

                            TextView Observaciones = findViewById(R.id.Observaciones);
                            Observaciones.setText(response.getString("observaciones"));

                            TextView NomPR = findViewById(R.id.NomPR);
                            TextView NomPS = findViewById(R.id.NomPS);

                            if(IDFPR.equals("0")){
                                Glide.with(getApplicationContext()).load(URL_SERVIDOR + "Mantenimiento/ImagenFirma/" + FPR).into(ImagePR);
                            }else{
                                Glide.with(getApplicationContext()).load(URL_HOST + "imgs/firma-personal/" + FPR).into(ImagePR);
                            }

                            if(IDFPS.equals("0")){
                                Glide.with(getApplicationContext()).load(URL_SERVIDOR + "Mantenimiento/ImagenFirma/" + FPS).into(ImagePS);
                            }else{
                                Glide.with(getApplicationContext()).load(URL_HOST + "imgs/firma-personal/" + FPS).into(ImagePS);
                            }

                            NomPR.setText(PFPR);
                            NomPS.setText(PFPS);

                            JSONArray extintoresArray = response.getJSONArray("detectores");

                            for (int i = 0; i < extintoresArray.length(); i++) {
                                JSONObject jsonObject = extintoresArray.getJSONObject(i);

                                String id = jsonObject.getString("iddetector");
                                String nodetector = jsonObject.getString("nodetector");
                                String ubicacion = jsonObject.getString("ubicacion");
                                String revision1 = jsonObject.getString("revision1");
                                String resultado1 = jsonObject.getString("resultado1");

                                String revision2 = jsonObject.getString("revision2");
                                String resultado2 = jsonObject.getString("resultado2");

                                String revision3 = jsonObject.getString("revision3");
                                String resultado3 = jsonObject.getString("resultado3");

                                String revision4 = jsonObject.getString("revision4");
                                String resultado4 = jsonObject.getString("resultado4");

                                dataDetectoresHumo item = new dataDetectoresHumo(id, nodetector, ubicacion,
                                        revision1, resultado1,
                                        revision2, resultado2,
                                        revision3, resultado3,
                                        revision4, resultado4
                                );

                                itemList.add(item);
                            }

                            adapter.notifyDataSetChanged();
                            DialogHelper.hideProgressDialog();
                            ocultarError();

                        } catch (JSONException e) {
                            e.printStackTrace();
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

        requestQueue.add(jsonObjectRequest);

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            fetchDetectorHumo();
        }

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