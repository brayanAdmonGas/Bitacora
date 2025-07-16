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
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.gestogas.gestoline.R;
import com.gestogas.gestoline.adapter.adapterImagen;
import com.gestogas.gestoline.adapter.adapterMantenimientoExtintor;
import com.gestogas.gestoline.controllers.AppController;
import com.gestogas.gestoline.data.dataExtintores;
import com.gestogas.gestoline.data.dataImagen;
import com.gestogas.gestoline.utils.DialogHelper;
import com.gestogas.gestoline.utils.TecladoUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MantenimientoPreventivoExtintorDetalle extends AppCompatActivity {
    String idMantenimiento, NumeroEquipo, NombreEquipo, numVerificacion;
    private RecyclerView recyclerView;
    private adapterMantenimientoExtintor adapter;
    private List<dataExtintores> itemList;
    private RequestQueue requestQueue;
    private int Idestacion;
    ImageView ImgResultado;
    TextView Mensaje;

    FloatingActionButton Editar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mantenimiento_preventivo_extintor_detalle);

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

        Editar = findViewById(R.id.Editar);
        Mensaje = findViewById(R.id.Mensaje);
        ImgResultado = findViewById(R.id.ImgResultado);

        recyclerView = findViewById(R.id.Recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        itemList = new ArrayList<>();
        adapter = new adapterMantenimientoExtintor(this,itemList);
        recyclerView.setAdapter(adapter);

        requestQueue = Volley.newRequestQueue(this);

        Editar.setOnClickListener(v -> {

            Intent didactic = new Intent(this, MantenimientoPreventivoExtintor.class);
            didactic.putExtra("idMantenimiento", idMantenimiento);
            didactic.putExtra("NumeroEquipo", NumeroEquipo);
            didactic.putExtra("NombreEquipo", NombreEquipo);
            didactic.putExtra("numVerificacion", numVerificacion);
            didactic.putExtra("NumeroPagina","1");
            didactic.putExtra("estado", "1");
            startActivityForResult(didactic, 1);

        });

        fetchExtintores();
    }

    private void fetchExtintores(){
        String rutalista = URL_SERVIDOR + "Mantenimiento/lista-extintores-mantenimiento.php?idMantenimiento=" + idMantenimiento;
        DialogHelper.showProgressDialog(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, rutalista, null,
                new Response.Listener<JSONObject>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            itemList.clear(); // Limpia lista actual

                            // Obtiene las firmas generales si las necesitas


                            String folio = response.getString("folio");
                            String fechacreacion = response.getString("fechacreacion");
                            String hora = response.getString("hora");

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

                            TextView Fecha = findViewById(R.id.Fecha);
                            ImageView ImagePR = findViewById(R.id.ImagePR);
                            ImageView ImagePS = findViewById(R.id.ImagePS);

                            TextView NomPR = findViewById(R.id.NomPR);
                            TextView NomPS = findViewById(R.id.NomPS);

                            Glide.with(getApplicationContext()).load(URL_HOST + "imgs/firma-personal/" + FPR).into(ImagePR);
                            Glide.with(getApplicationContext()).load(URL_HOST + "imgs/firma-personal/" + FPS).into(ImagePS);

                            NomPR.setText(PFPR);
                            NomPS.setText(PFPS);

                            // Puedes guardarlas en variables globales o en un modelo, según tu diseño
                            // this.firmaRealiza = FPR; // ejemplo

                            JSONArray extintoresArray = response.getJSONArray("extintores");

                            for (int i = 0; i < extintoresArray.length(); i++) {
                                JSONObject jsonObject = extintoresArray.getJSONObject(i);

                                String idextintor = jsonObject.getString("idextintor");
                                String noextintor = jsonObject.getString("noextintor");
                                String ubicacion = jsonObject.getString("ubicacion");
                                String tipoextintor = jsonObject.getString("tipoextintor");
                                String pesokg = jsonObject.getString("pesokg");
                                String iddetalle = jsonObject.getString("id");
                                String ultimarecarga = jsonObject.getString("ultimarecarga");
                                String manometro = jsonObject.getString("manometro");
                                String boquilladescarga = jsonObject.getString("boquilladescarga");
                                String manguera = jsonObject.getString("manguera");
                                String funcionalidad  = jsonObject.getString("funcionalidad");
                                String observaciones  = jsonObject.getString("observaciones");

                                // Crea el objeto dataExtintores
                                dataExtintores item = new dataExtintores(
                                        idextintor,
                                        noextintor,
                                        ubicacion,
                                        null,
                                        null,
                                        tipoextintor,
                                        pesokg,
                                        iddetalle,
                                        ultimarecarga,
                                        manometro,
                                        boquilladescarga,
                                        manguera,
                                        funcionalidad,
                                        observaciones,
                                        FPR,
                                        FPS,
                                        PFPR,
                                        PFPS
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
            fetchExtintores();
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