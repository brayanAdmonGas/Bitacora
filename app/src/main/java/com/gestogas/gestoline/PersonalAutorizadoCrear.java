package com.gestogas.gestoline;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.material.button.MaterialButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.gestogas.gestoline.controllers.AppController;
import com.gestogas.gestoline.recepcion.RecepcionBitacoraCrearEditar;
import com.gestogas.gestoline.utils.Constantes;
import com.gestogas.gestoline.utils.DialogHelper;
import com.gestogas.gestoline.utils.DistanciaUtils;
import com.gestogas.gestoline.utils.TecladoUtils;
import com.gestogas.gestoline.utils.ToastUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PersonalAutorizadoCrear extends AppCompatActivity {

    String categoria, url;
    int idEstacion, idPuesto, distMax = 0;
    AutoCompleteTextView NombrePersonal;
    Button BtnGuardar;
    List<String> opcionesList;
    ArrayAdapter<String> adapter;
    private RequestQueue requestQueue;
    Map<String, String> nombreIdMap = new HashMap<>();
    SwitchCompat RecepcionDescarga, Mantenimiento;
    TextView txtCategoria;
    double latitudeEstacion = 0, longitudeEstacion = 0, latitudeEquipo = 0, longitudeEquipo = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_personal_autorizado_crear);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        idEstacion = AppController.getInstance().GetIdestacion();
        idPuesto = AppController.getInstance().GetIdPuesto();

        latitudeEquipo = Double.parseDouble(AppController.getInstance().GetLatitudEquipo());
        longitudeEquipo = Double.parseDouble(AppController.getInstance().GetLongitudEquipo());

        distMax = Integer.parseInt(AppController.getInstance().GetDistMax());
        latitudeEstacion = Double.parseDouble(AppController.getInstance().GetLatitud());
        longitudeEstacion = Double.parseDouble(AppController.getInstance().GetLongitud());

        categoria = getIntent().getStringExtra("categoria");

        NombrePersonal = findViewById(R.id.NombrePersonal);
        BtnGuardar = findViewById(R.id.BtnGuardar);
        RecepcionDescarga = findViewById(R.id.RecepcionDescarga);
        Mantenimiento = findViewById(R.id.Mantenimiento);
        txtCategoria = findViewById(R.id.txtCategoria);
        LinearLayout layoutFueraRango = findViewById(R.id.layoutFueraRango);

        opcionesList = new ArrayList<>();
        requestQueue = Volley.newRequestQueue(this);
        PersonalAutorizado();

        if (categoria.isEmpty()) {
            RecepcionDescarga.setVisibility(View.VISIBLE);
            Mantenimiento.setVisibility(View.VISIBLE);
        }else{
            txtCategoria.setVisibility(View.VISIBLE);
            if (categoria.equals("RDP")) {
                txtCategoria.setText("Recepción y Descarga");
            } else if (categoria.equals("MPC")) {
                txtCategoria.setText("Mantenimiento");
            }
        }

        BtnGuardar.setOnClickListener(view -> {
            validaPersonal();
        });

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

    }

    private void PersonalAutorizado() {
        DialogHelper.showProgressDialog(this);

        String url = Constantes.URL_SERVIDOR + "Autorizacion/autorizacion-recepcion.php?idEstacion=" + idEstacion + "&Categoria=" + categoria;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            opcionesList.clear(); // Asegúrate de limpiar la lista si ya tenía datos
                            nombreIdMap.clear();  // Limpia el mapa si aplica

                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jsonObject = response.getJSONObject(i);

                                String idUsuario = jsonObject.getString("IdUsuario");
                                String nombreUsuario = jsonObject.getString("NombreUsuario");

                                opcionesList.add(nombreUsuario);
                                nombreIdMap.put(nombreUsuario, idUsuario);
                            }

                            // Habilitar el click del AutoCompleteTextView para mostrar el diálogo
                            NombrePersonal.setOnClickListener(v -> {
                                int selectedIndex = opcionesList.indexOf(NombrePersonal.getText().toString());

                                new MaterialAlertDialogBuilder(PersonalAutorizadoCrear.this)
                                        .setTitle("Selecciona el personal")
                                        .setSingleChoiceItems(opcionesList.toArray(new String[0]), selectedIndex, (dialog, which) -> {
                                            NombrePersonal.setText(opcionesList.get(which));
                                            dialog.dismiss();
                                        })
                                        .show();
                            });

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


    private void validaPersonal() {
        if (NombrePersonal.getText().toString().isEmpty()) {
            NombrePersonal.setError("Falta agregar el personal autorizado");
            ToastUtils.show(this, "Falta agregar el personal autorizado", ToastUtils.INFO);
        } else {

            if (categoria.isEmpty()) {
                if (!RecepcionDescarga.isChecked() && !Mantenimiento.isChecked()) {
                    RecepcionDescarga.setError("Obligatorio");
                    Mantenimiento.setError("Obligatorio");
                    ToastUtils.show(this, "Debes seleccionar al menos una opción.", ToastUtils.INFO);
                    return;
                }
            }

            DialogHelper.showProgressDialog(this);
            guardarPersonal();

        }

    }

    private void guardarPersonal(){

        String nombreSeleccionado = NombrePersonal.getText().toString();
        String idUsuarioSeleccionado = nombreIdMap.get(nombreSeleccionado);
        String rydp = "", mpc = "";

        mpc = (Mantenimiento.isChecked()) ? "MPC" : "";
        rydp = (RecepcionDescarga.isChecked()) ? "RDP" : "";

        url =  "Autorizacion/agregar-autorizacion.php";

        String finalRecepcion = rydp;
        String finalMantenimiento = mpc;
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

                                ToastUtils.showAndThen(PersonalAutorizadoCrear.this, mensaje, ToastUtils.SUCCESS, () -> {
                                    setResult(RESULT_OK);
                                    finish();
                                });

                            }else{
                                DialogHelper.hideProgressDialog();
                                ToastUtils.show(PersonalAutorizadoCrear.this, mensaje, ToastUtils.ERROR);
                            }

                        }catch (Exception e){
                            ToastUtils.show(PersonalAutorizadoCrear.this, e.toString(), ToastUtils.INFO);
                            DialogHelper.hideProgressDialog();
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ToastUtils.show(PersonalAutorizadoCrear.this, error.toString(), ToastUtils.INFO);
                        DialogHelper.hideProgressDialog();
                    }
                }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("idEstacion", String.valueOf(idEstacion));
                params.put("idUsuario", idUsuarioSeleccionado);
                params.put("RyD", finalRecepcion);
                params.put("MPC", finalMantenimiento);
                params.put("categoria", categoria);
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