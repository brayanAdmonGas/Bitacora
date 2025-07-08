package com.gestogas.gestoline;

import android.app.DatePickerDialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.gestogas.gestoline.controllers.AppController;
import com.gestogas.gestoline.utils.Constantes;
import com.gestogas.gestoline.utils.DialogHelper;
import com.gestogas.gestoline.utils.DistanciaUtils;
import com.gestogas.gestoline.utils.TecladoUtils;
import com.gestogas.gestoline.utils.ToastUtils;
import com.google.android.material.button.MaterialButton;

import androidx.activity.EdgeToEdge;

import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ExtintorCrearEditar extends BaseActivity {
    int idEstacion, idPuesto;
    String estado,idextintor,noextintor,ubicacion,fecharecarga,tipoextintor,pesokg,url, titulo, tituloboton;
    private EditText NoExtintor, Ubicacion, FechaRecarga, TipoExtintor, PesoKg;
    private Button BtnGuardar;
    private final Calendar calendar = Calendar.getInstance();
    double latitudeEstacion = 0, longitudeEstacion = 0, latitudeEquipo = 0, longitudeEquipo = 0;
    int distMax = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_crear_extintor);

        idEstacion = AppController.getInstance().GetIdestacion();
        idPuesto = AppController.getInstance().GetIdPuesto();
        latitudeEquipo = Double.parseDouble(AppController.getInstance().GetLatitudEquipo());
        longitudeEquipo = Double.parseDouble(AppController.getInstance().GetLongitudEquipo());

        distMax = Integer.parseInt(AppController.getInstance().GetDistMax());
        latitudeEstacion = Double.parseDouble(AppController.getInstance().GetLatitud());
        longitudeEstacion = Double.parseDouble(AppController.getInstance().GetLongitud());

        estado = getIntent().getStringExtra("estado");
        idextintor = getIntent().getStringExtra("idextintor");
        noextintor = getIntent().getStringExtra("noextintor");
        ubicacion = getIntent().getStringExtra("ubicacion");
        fecharecarga = getIntent().getStringExtra("fecharecarga");
        tipoextintor = getIntent().getStringExtra("tipoextintor");
        pesokg = getIntent().getStringExtra("pesokg");
        titulo = getIntent().getStringExtra("titulo");
        tituloboton = getIntent().getStringExtra("tituloboton");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(titulo);
        }

        NoExtintor = findViewById(R.id.NoExtintor);
        Ubicacion = findViewById(R.id.Ubicacion);
        FechaRecarga = findViewById(R.id.FechaRecarga);
        TipoExtintor = findViewById(R.id.TipoExtintor);
        PesoKg = findViewById(R.id.PesoKg);
        BtnGuardar = findViewById(R.id.BtnGuardar);
        LinearLayout layoutFueraRango = findViewById(R.id.layoutFueraRango);

        NoExtintor.setText(noextintor);
        Ubicacion.setText(ubicacion);
        FechaRecarga.setText(fecharecarga);
        TipoExtintor.setText(tipoextintor);
        PesoKg.setText(pesokg);
        BtnGuardar.setText(tituloboton);

        boolean validarDistancia = DistanciaUtils.validarDistancia(
                getApplicationContext(),
                idPuesto,
                latitudeEquipo,
                longitudeEquipo,
                latitudeEstacion,
                longitudeEstacion,
                distMax
        );

        if (validarDistancia) {
            BtnGuardar.setEnabled(true);
            layoutFueraRango.setVisibility(View.GONE);
        } else {
            BtnGuardar.setEnabled(false);
            layoutFueraRango.setVisibility(View.VISIBLE);
            // Cambiar a gris (#757575)
            int gris = Color.parseColor("#F5F5F5");
            int gris2 = Color.parseColor("#757575");

            MaterialButton boton = (MaterialButton) BtnGuardar; // casteo explícito
            BtnGuardar.setTextColor(gris2);
            boton.setStrokeColor(ColorStateList.valueOf(gris)); // cambia borde
            BtnGuardar.setBackgroundTintList(ColorStateList.valueOf(gris)); // opcional si quieres también fondo gris

        }

        // Crear el listener para el DatePicker
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };

        // Mostrar el DatePicker al hacer clic en el EditText
        FechaRecarga.setOnClickListener(v -> {
            new DatePickerDialog(ExtintorCrearEditar.this, dateSetListener,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        BtnGuardar.setOnClickListener(view -> {
            validaExtintor();
        });

    }

    private void updateLabel() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        FechaRecarga.setText(sdf.format(calendar.getTime()));
    }

    private void validaExtintor() {

        if (NoExtintor.getText().toString().isEmpty()) {
            NoExtintor.setError("Falta agregar el numero de extintor");
            ToastUtils.show(this, "Falta agregar el numero de extintor", ToastUtils.INFO);
        } else {
            if (Ubicacion.getText().toString().isEmpty()) {
                Ubicacion.setError("Falta agregar la ubicación");
                ToastUtils.show(this, "Falta agregar la ubicación", ToastUtils.INFO);
            } else {
                if (FechaRecarga.getText().toString().isEmpty()) {
                    FechaRecarga.setError("Falta agregar la fecha de recarga");
                    ToastUtils.show(this, "Falta agregar la fecha de recarga", ToastUtils.INFO);
                } else {
                    if (TipoExtintor.getText().toString().isEmpty()) {
                        TipoExtintor.setError("Falta agregar el tipo de extintor");
                        ToastUtils.show(this, "Falta agregar el tipo de extintor", ToastUtils.INFO);
                    } else {
                        if (PesoKg.getText().toString().isEmpty()) {
                            PesoKg.setError("Falta agregar el peso del extintor");
                            ToastUtils.show(this, "Falta agregar el peso del extintor", ToastUtils.INFO);
                        } else {
                            DialogHelper.showProgressDialog(this);
                            guardarExtintor();
                        }
                    }
                }
            }
        }
    }

    private void guardarExtintor() {

        String noextintor = NoExtintor.getText().toString().trim();
        String ubicacion = Ubicacion.getText().toString().trim();
        String fecharecarga = FechaRecarga.getText().toString().trim();
        String tipoextintor = TipoExtintor.getText().toString().trim();
        String pesokg = PesoKg.getText().toString().trim();

        url = (estado.equals("Nuevo"))?  "Mantenimiento/agregar-extintor-estacion.php" :  "Mantenimiento/editar-extintor-estacion.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constantes.URL_SERVIDOR + url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {

                            JSONArray jsonarray = new JSONArray(response);
                            int estado = 0;
                            String mensaje = "";

                            if (jsonarray.length() > 0) {
                                JSONObject obj = jsonarray.getJSONObject(0);
                                estado = obj.getInt("estado");
                                mensaje = obj.getString("mensaje");
                            }

                            if (estado == 1){

                                ToastUtils.showAndThen(ExtintorCrearEditar.this, mensaje, ToastUtils.SUCCESS, () -> {
                                    setResult(RESULT_OK);
                                    finish();
                                });


                            }else{
                                DialogHelper.hideProgressDialog();
                                ToastUtils.show(ExtintorCrearEditar.this, mensaje, ToastUtils.INFO);
                            }

                        }catch (JSONException e){
                            ToastUtils.show(ExtintorCrearEditar.this, e.toString(), ToastUtils.INFO);
                            DialogHelper.hideProgressDialog();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ToastUtils.show(ExtintorCrearEditar.this, error.toString(), ToastUtils.INFO);
                        DialogHelper.hideProgressDialog();
                    }
                }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("ValEstacionid", String.valueOf(idEstacion));
                params.put("ValIdExtintor", idextintor);
                params.put("ValNoExtintor", noextintor);
                params.put("ValUbicacion", ubicacion);
                params.put("ValFechaRecarga", fecharecarga);
                params.put("ValTipoExtintor", tipoextintor);
                params.put("ValPesoKg", pesokg);

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
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
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }



}