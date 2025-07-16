package com.gestogas.gestoline.mantenimiento;

import static android.view.View.VISIBLE;

import static com.gestogas.gestoline.utils.Constantes.URL_HOST;
import static com.gestogas.gestoline.utils.Constantes.URL_SERVIDOR;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.gestogas.gestoline.Evidencias;
import com.gestogas.gestoline.R;
import com.gestogas.gestoline.controllers.AppController;
import com.gestogas.gestoline.recepcion.RecepcionBitacoraCrearEditar;
import com.gestogas.gestoline.utils.DialogHelper;
import com.gestogas.gestoline.utils.DistanciaUtils;
import com.gestogas.gestoline.utils.ResultadoValida;
import com.gestogas.gestoline.utils.TecladoUtils;
import com.gestogas.gestoline.utils.ToastUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MantenimientoPreventivoExtintor extends AppCompatActivity {
    int idEstacion, IdUsuario, idPuesto, estado, Sigpagina, Antpagina, idextintor, iddetalle = 0,resultado= 0, totalextintores = 0, numeropagina = 0, distMax = 0;
    String idMantenimiento, NumeroEquipo, NombreEquipo, NumeroPagina, idSeleccionado = "0";
    private final Calendar calendar = Calendar.getInstance();
    TextView TxtTitulo;
    EditText Fecha,Manometro,BoquillaDescarga,Manguera,Funcionalidad,Observaciones;
    private AutoCompleteTextView PersonaRealizaInterno;
    private TextInputLayout PersonaRealizaInterno2;
    private ArrayAdapter<String> adapter;
    private final Map<String, String> mapaPersonal = new HashMap<>();
    private final List<String> listaNombres = new ArrayList<>();
    Button BtnGuardar,BtnSiguiente, BtnAnterior;
    double latitudeEstacion = 0, longitudeEstacion = 0, latitudeEquipo = 0, longitudeEquipo = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mantenimiento_preventivo_extintor);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        idEstacion = AppController.getInstance().GetIdestacion();
        IdUsuario = AppController.getInstance().GetidUsuario();
        idPuesto = AppController.getInstance().GetIdPuesto();
        latitudeEquipo = Double.parseDouble(AppController.getInstance().GetLatitudEquipo());
        longitudeEquipo = Double.parseDouble(AppController.getInstance().GetLongitudEquipo());

        distMax = Integer.parseInt(AppController.getInstance().GetDistMax());
        latitudeEstacion = Double.parseDouble(AppController.getInstance().GetLatitud());
        longitudeEstacion = Double.parseDouble(AppController.getInstance().GetLongitud());

        idMantenimiento = getIntent().getStringExtra("idMantenimiento");
        NumeroEquipo = getIntent().getStringExtra("NumeroEquipo");
        NombreEquipo = getIntent().getStringExtra("NombreEquipo");
        NumeroPagina = getIntent().getStringExtra("NumeroPagina");
        estado = getIntent().getIntExtra("estado", 0);

        Sigpagina = Integer.parseInt(NumeroPagina) + 1;
        Antpagina = Integer.parseInt(NumeroPagina) - 1;

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Mantenimiento Preventivo");
        }

        TxtTitulo = findViewById(R.id.TxtTitulo);
        Fecha = findViewById(R.id.Fecha);
        Manometro = findViewById(R.id.Manometro);
        BoquillaDescarga = findViewById(R.id.BoquillaDescarga);
        Manguera = findViewById(R.id.Manguera);
        Funcionalidad = findViewById(R.id.Funcionalidad);
        Observaciones = findViewById(R.id.Observaciones);
        BtnSiguiente = findViewById(R.id.BtnSiguiente);
        BtnAnterior = findViewById(R.id.BtnAnterior);
        PersonaRealizaInterno = findViewById(R.id.PersonaRealizaInterno);
        PersonaRealizaInterno2 = findViewById(R.id.PersonaRealizaInterno2);
        BtnGuardar = findViewById(R.id.BtnGuardar);

        TxtTitulo.setText(NombreEquipo);

        //------------------------------------------------------------------------------------------
        //------------------------------------------------------------------------------------------

        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                fechaCalendario();
            }
        };

        Fecha.setOnClickListener(v -> {
            new DatePickerDialog(this, dateSetListener,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        LinearLayout layoutFueraRango = findViewById(R.id.layoutFueraRango);

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

            // Colores
            int grisFondo = Color.parseColor("#F5F5F5");
            int grisTexto = Color.parseColor("#757575");

            // Configurar BtnGuardar
            MaterialButton botonGuardar = (MaterialButton) BtnGuardar;
            BtnGuardar.setTextColor(grisTexto);
            botonGuardar.setStrokeColor(ColorStateList.valueOf(grisFondo));
            BtnGuardar.setBackgroundTintList(ColorStateList.valueOf(grisFondo));

            // Configurar BtnSiguiente
            BtnSiguiente.setEnabled(false);
            MaterialButton botonSiguiente = (MaterialButton) BtnSiguiente;
            BtnSiguiente.setTextColor(grisTexto);
            botonSiguiente.setStrokeColor(ColorStateList.valueOf(grisFondo));
            BtnSiguiente.setBackgroundTintList(ColorStateList.valueOf(grisFondo));

            // Configurar BtnAnterior
            BtnAnterior.setEnabled(false);
            MaterialButton botonAnterior = (MaterialButton) BtnAnterior;
            BtnAnterior.setTextColor(grisTexto);
            botonAnterior.setStrokeColor(ColorStateList.valueOf(grisFondo));
            BtnAnterior.setBackgroundTintList(ColorStateList.valueOf(grisFondo));
        }


        BtnSiguiente.setOnClickListener(view -> {

            actualizarExtintor();

        });

        BtnAnterior.setOnClickListener(view -> {

            Intent mantenimiento = new Intent(getApplicationContext(), MantenimientoPreventivoExtintor.class);
            mantenimiento.putExtra("idMantenimiento",idMantenimiento);
            mantenimiento.putExtra("NumeroEquipo",NumeroEquipo);
            mantenimiento.putExtra("NombreEquipo",NombreEquipo);
            mantenimiento.putExtra("NumeroPagina",String.valueOf(Antpagina));
            startActivity(mantenimiento);
            finish();

        });

        BtnGuardar.setOnClickListener(view -> {
            guardarMantenimiento();
        });

        fechtExtintor();

    }

    private void fechaCalendario() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Fecha.setText(sdf.format(calendar.getTime()));
    }

    public void fechtExtintor() {

        DialogHelper.showProgressDialog(this);
        String url = URL_SERVIDOR + "Mantenimiento/detalle-extintor.php?idEstacion=" + idEstacion + "&idMantenimiento=" + idMantenimiento + "&NumeroPagina=" + NumeroPagina;
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onResponse(JSONArray response) {
                        String  noextintor = "",
                                ubicacion = "",
                                tipoextintor = "",
                                pesokg = "",
                                ultimarecarga = "",
                                manometro = "",
                                boquilladescarga = "",
                                manguera = "",
                                funcionalidad = "",
                                observaciones = "",
                                IDFPR = "0", PFPR = "";
                        int anterior = 0;

                        try {

                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jsonObject = response.getJSONObject(i);

                                anterior = jsonObject.getInt("anterior");
                                totalextintores = jsonObject.getInt("TotalExtintores");
                                numeropagina = jsonObject.getInt("NumeroPagina");
                                idextintor = jsonObject.getInt("idextintor");
                                noextintor = jsonObject.getString("noextintor");
                                ubicacion = jsonObject.getString("ubicacion");
                                tipoextintor = jsonObject.getString("tipoextintor");
                                pesokg = jsonObject.getString("pesokg");
                                iddetalle = jsonObject.getInt("id");
                                ultimarecarga = jsonObject.getString("ultimarecarga");
                                manometro = jsonObject.getString("manometro");
                                boquilladescarga = jsonObject.getString("boquilladescarga");
                                manguera = jsonObject.getString("manguera");
                                funcionalidad  = jsonObject.getString("funcionalidad");
                                observaciones  = jsonObject.getString("observaciones");
                                resultado = jsonObject.getInt("resultado");
                                IDFPR = jsonObject.getString("IDFPR");
                                PFPR = jsonObject.getString("PFPR");


                            }
                            TextView NuExtintor = findViewById(R.id.NuExtintor);
                            TextView TipoExtintor = findViewById(R.id.TipoExtintor);
                            TextView Ubicacion = findViewById(R.id.Ubicacion);
                            TextView PesoKg = findViewById(R.id.PesoKg);

                            NuExtintor.setText(noextintor);
                            TipoExtintor.setText(tipoextintor);
                            Ubicacion.setText(ubicacion);
                            PesoKg.setText(pesokg);

                            Fecha.setText(ultimarecarga);
                            Manometro.setText(manometro);
                            BoquillaDescarga.setText(boquilladescarga);
                            Manguera.setText(manguera);
                            Funcionalidad.setText(funcionalidad);
                            Observaciones.setText(observaciones);

                            idSeleccionado = IDFPR;
                            PersonaRealizaInterno.setText(PFPR);

                            if (anterior != 0){
                                BtnAnterior.setVisibility(View.VISIBLE);
                            }

                            if (totalextintores != numeropagina){
                                BtnSiguiente.setVisibility(View.VISIBLE);
                            }else {
                                BtnGuardar.setVisibility(View.VISIBLE);
                                PersonaRealizaInterno.setVisibility(View.VISIBLE);
                                PersonaRealizaInterno2.setVisibility(View.VISIBLE);

                                ListaPersonal();
                            }

                            DialogHelper.hideProgressDialog();
                        } catch (JSONException e) {
                            DialogHelper.hideProgressDialog();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        DialogHelper.hideProgressDialog();
                    }
                });

        requestQueue.add(jsonArrayRequest);
    }

    private void ListaPersonal() {
        DialogHelper.showProgressDialog(this);

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        String url = URL_SERVIDOR + "Autorizacion/personal-autorizado.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("Personal");

                            listaNombres.clear();
                            mapaPersonal.clear();

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject obj = jsonArray.getJSONObject(i);
                                String id = obj.getString("IdUsuario");
                                String nombre = obj.getString("NombreUsuario");

                                listaNombres.add(nombre);
                                mapaPersonal.put(nombre, id);
                            }

                            // Configura el click para abrir pop-up
                            PersonaRealizaInterno.setOnClickListener(v -> {
                                int selectedIndex = listaNombres.indexOf(PersonaRealizaInterno.getText().toString());

                                new MaterialAlertDialogBuilder(MantenimientoPreventivoExtintor.this)
                                        .setTitle("Selecciona el personal")
                                        .setSingleChoiceItems(listaNombres.toArray(new String[0]), selectedIndex, (dialog, which) -> {
                                            String seleccionado = listaNombres.get(which);
                                            PersonaRealizaInterno.setText(seleccionado);

                                            idSeleccionado = mapaPersonal.getOrDefault(seleccionado, "0");

                                            dialog.dismiss();
                                        })
                                        .show();
                            });

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } finally {
                            DialogHelper.hideProgressDialog();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        DialogHelper.hideProgressDialog();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("idEstacion", String.valueOf(idEstacion));
                params.put("Categoria", "RDP");
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }

    public void actualizarExtintor(){
        final String url = URL_SERVIDOR + "Mantenimiento/mantenimiento-extintor-actualizar.php";
        final String fecha = Fecha.getText().toString();
        final String manometro = Manometro.getText().toString().trim();
        final String boquilladescarga = BoquillaDescarga.getText().toString().trim();
        final String manguera = Manguera.getText().toString().trim();
        final String funcionalidad = Funcionalidad.getText().toString().trim();
        final String observaciones = Observaciones.getText().toString().trim();

        DialogHelper.showProgressDialog(this);

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {

                    ToastUtils.showAndThen(MantenimientoPreventivoExtintor.this, "Se actualizo correctamente la información", ToastUtils.SUCCESS, () -> {
                        Intent mantenimiento = new Intent(getApplicationContext(), MantenimientoPreventivoExtintor.class);
                        mantenimiento.putExtra("idMantenimiento",idMantenimiento);
                        mantenimiento.putExtra("NumeroEquipo",NumeroEquipo);
                        mantenimiento.putExtra("NombreEquipo",NombreEquipo);
                        mantenimiento.putExtra("NumeroPagina",String.valueOf(Sigpagina));
                        mantenimiento.putExtra("estado",estado);
                        startActivityForResult(mantenimiento,1);
                        setResult(RESULT_OK);
                        finish();
                    });

                },
                error -> {
                    DialogHelper.hideProgressDialog();
                    ToastUtils.show(this, "Error al enviar datos", ToastUtils.ERROR);
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("iddetalle", String.valueOf(iddetalle));
                params.put("resultado", String.valueOf(resultado));
                params.put("idMantenimiento", idMantenimiento);
                params.put("idextintor", String.valueOf(idextintor));
                params.put("mostrarfecha", fecha);
                params.put("manometro", manometro);
                params.put("boquilladescarga", boquilladescarga);
                params.put("manguera", manguera);
                params.put("funcionalidad", funcionalidad);
                params.put("observaciones", observaciones);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);

    }

    public void guardarMantenimiento(){

        final String url = URL_SERVIDOR + "Mantenimiento/mantenimiento-extintor-finalizar.php";
        final String mostrarfecha = Fecha.getText().toString().trim();
        final String manometro = Manometro.getText().toString().trim();
        final String boquilladescarga = BoquillaDescarga.getText().toString().trim();
        final String manguera = Manguera.getText().toString().trim();
        final String funcionalidad = Funcionalidad.getText().toString().trim();
        final String observaciones = Observaciones.getText().toString().trim();

        DialogHelper.showProgressDialog(this);

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {

                    ToastUtils.showAndThen(MantenimientoPreventivoExtintor.this, "Mantenimiento Finalizado", ToastUtils.SUCCESS, () -> {
                        Intent mantenimiento = new Intent(getApplicationContext(), Evidencias.class);
                        mantenimiento.putExtra("id", idMantenimiento);
                        mantenimiento.putExtra("categoria", "MantenimientoPreventivo");
                        mantenimiento.putExtra("carpeta", "Mantenimiento");
                        mantenimiento.putExtra("titulo", "Evidencia Mantenimiento Preventivo");
                        startActivityForResult(mantenimiento,1);
                        setResult(RESULT_OK);
                        finish();
                    });

                },
                error -> {
                    DialogHelper.hideProgressDialog();
                    ToastUtils.show(this, "Error al enviar datos", ToastUtils.ERROR);
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("iddetalle", String.valueOf(iddetalle));
                params.put("resultado", String.valueOf(resultado));
                params.put("idMantenimiento", idMantenimiento);
                params.put("idextintor", String.valueOf(idextintor));
                params.put("mostrarfecha", mostrarfecha);
                params.put("manometro", manometro);
                params.put("boquilladescarga", boquilladescarga);
                params.put("manguera", manguera);
                params.put("funcionalidad", funcionalidad);
                params.put("observaciones", observaciones);
                params.put("estado", String.valueOf(estado));

                params.put("PersonalSupervisa", String.valueOf(IdUsuario));
                params.put("PersonalRealiza", idSeleccionado);
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
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