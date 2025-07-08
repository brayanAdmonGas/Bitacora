package com.gestogas.gestoline.profeco;

import static com.gestogas.gestoline.utils.Constantes.URL_SERVIDOR;
import static com.gestogas.gestoline.utils.DialogHelper.hideProgressDialog;
import static com.gestogas.gestoline.utils.DialogHelper.showProgressDialog;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.gestogas.gestoline.BaseActivity;
import com.gestogas.gestoline.R;
import com.gestogas.gestoline.controllers.AppController;
import com.gestogas.gestoline.data.dataDispensarios;
import com.gestogas.gestoline.data.dataUsuario;
import com.gestogas.gestoline.utils.DialogHelper;
import com.gestogas.gestoline.utils.DistanciaUtils;
import com.gestogas.gestoline.utils.TecladoUtils;
import com.gestogas.gestoline.utils.ToastUtils;

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

public class ProfecoCrearEditar extends BaseActivity {
    int idEstacion, idPuesto;
    private final Calendar calendar = Calendar.getInstance();
    EditText Fecha, Hora, Motivo, TxtObservaciones;
    ArrayList<String> lado;
    List<dataDispensarios> listaDispensarios = new ArrayList<>();
    List<dataUsuario> listaResponsable = new ArrayList<>();
    Spinner SpinnerDispensario, SpinnerLado, SpinnerResponsable;
    private String ProductoUno, ProductoDos, ProductoTres, ValProducto1 = "", ValProducto2 = "", ValProducto3 = "";
    SwitchCompat Producto1, Producto2, Producto3;
    Button BtnGuardar;
    double latitudeEstacion = 0, longitudeEstacion = 0, latitudeEquipo = 0, longitudeEquipo = 0;
    int distMax = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profeco_crear_editar);

        idEstacion = AppController.getInstance().GetIdestacion();
        idPuesto = AppController.getInstance().GetIdPuesto();

        ProductoUno = AppController.getInstance().GetProductoUno();
        ProductoDos = AppController.getInstance().GetProductoDos();
        ProductoTres = AppController.getInstance().GetProductoTres();

        // Evitar NullPointerException:
        if (ProductoUno == null) ProductoUno = "";
        if (ProductoDos == null) ProductoDos = "";
        if (ProductoTres == null) ProductoTres = "";

        latitudeEquipo = Double.parseDouble(AppController.getInstance().GetLatitudEquipo());
        longitudeEquipo = Double.parseDouble(AppController.getInstance().GetLongitudEquipo());
        distMax = Integer.parseInt(AppController.getInstance().GetDistMax());
        latitudeEstacion = Double.parseDouble(AppController.getInstance().GetLatitud());
        longitudeEstacion = Double.parseDouble(AppController.getInstance().GetLongitud());

        String titulo = getIntent().getStringExtra("titulo");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(titulo);
        }

        Fecha = findViewById(R.id.Fecha);
        Hora = findViewById(R.id.Hora);
        SpinnerLado = findViewById(R.id.SpinnerLado);
        SpinnerDispensario = findViewById(R.id.SpinnerDispensario);
        Motivo = findViewById(R.id.TxtMotivo);
        TxtObservaciones = findViewById(R.id.TxtObservaciones);
        SpinnerResponsable = findViewById(R.id.SpinnerResponsable);
        Producto1 = findViewById(R.id.Producto1);
        Producto2 = findViewById(R.id.Producto2);
        Producto3 = findViewById(R.id.Producto3);
        BtnGuardar = findViewById(R.id.BtnGuardar);
        LinearLayout layoutFueraRango = findViewById(R.id.layoutFueraRango);

        lado = new ArrayList<>();
        lado.add("Seleccione");
        lado.add("A");
        lado.add("B");
        SpinnerLado.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, lado));

        if (ProductoUno.isEmpty()) {
            Producto1.setVisibility(View.GONE);
        }

        if (ProductoDos.isEmpty()) {
            Producto2.setVisibility(View.GONE);
        }

        if (ProductoTres.isEmpty()) {
            Producto3.setVisibility(View.GONE);
        }

        Producto1.setText(ProductoUno);
        Producto2.setText(ProductoDos);
        Producto3.setText(ProductoTres);

        showProgressDialog(this);
        ListaDispensario();
        ListaResponsable();

        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                fechaCalendario();
            }
        };

        Fecha.setOnClickListener(v -> new DatePickerDialog(this, dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show());

        Hora.setOnClickListener(v -> ObtenerHora());

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
            BtnGuardar.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.color_inactivo));
        }

        BtnGuardar.setOnClickListener(v -> validaProfeco());
    }

    private void fechaCalendario() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Fecha.setText(sdf.format(calendar.getTime()));
    }

    private void ObtenerHora() {
        Calendar calendar = Calendar.getInstance();
        int hora = calendar.get(Calendar.HOUR_OF_DAY);
        int minuto = calendar.get(Calendar.MINUTE);

        TimePickerDialog picker = new TimePickerDialog(this, (view, hourOfDay, minuteSelected) -> {
            String horaFormateada = String.format("%02d", hourOfDay % 12 == 0 ? 12 : hourOfDay % 12);
            String minutoFormateado = String.format("%02d", minuteSelected);
            String amPm = (hourOfDay < 12) ? "a.m." : "p.m.";
            String horaFinal = horaFormateada + ":" + minutoFormateado + " " + amPm;

            Hora.setText(horaFinal);
        }, hora, minuto, false);

        picker.show();
    }

    private void ListaDispensario() {
        listaDispensarios.clear();
        listaDispensarios.add(new dataDispensarios("0", "", "", "", "", "", "", "", "", "", ""));
        String url = URL_SERVIDOR + "Dispensario/lista-dispensario-estacion.php?idEstacion=" + idEstacion;

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONArray jsonArray = new JSONArray(response);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            String id = obj.getString("id");
                            String nodispensario = obj.getString("nodispensario");
                            String marca = obj.getString("marca");
                            // Ignorando otros campos no usados
                            listaDispensarios.add(new dataDispensarios(id, nodispensario, marca, "", "", "", "", "", "", "", ""));
                        }

                        ArrayAdapter<dataDispensarios> adapter = new ArrayAdapter<>(
                                ProfecoCrearEditar.this,
                                android.R.layout.simple_spinner_dropdown_item,
                                listaDispensarios
                        );

                        SpinnerDispensario.setAdapter(adapter);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        hideProgressDialog();
                    }
                },
                error -> {
                    error.printStackTrace();
                    hideProgressDialog();
                }
        );

        requestQueue.add(stringRequest);
    }

    private void ListaResponsable() {
        listaResponsable.clear();
        listaResponsable.add(new dataUsuario(0, ""));
        String url = URL_SERVIDOR + "Dispensario/lista-dispensario-responsable.php?idEstacion=" + idEstacion;

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONArray jsonArray = new JSONArray(response);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            String id = obj.getString("id");
                            String nombre = obj.getString("nombre");

                            listaResponsable.add(new dataUsuario(Integer.parseInt(id), nombre));
                        }

                        ArrayAdapter<dataUsuario> adapter = new ArrayAdapter<>(
                                ProfecoCrearEditar.this,
                                android.R.layout.simple_spinner_dropdown_item,
                                listaResponsable
                        );

                        SpinnerResponsable.setAdapter(adapter);

                        hideProgressDialog();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        hideProgressDialog();
                    }
                },
                error -> {
                    error.printStackTrace();
                    hideProgressDialog();
                }
        );

        requestQueue.add(stringRequest);
    }

    private void validaProfeco() {
        if (Fecha.getText().toString().isEmpty()) {
            Fecha.setError("Seleccione una fecha");
            ToastUtils.show(this, "Seleccione una fecha", ToastUtils.INFO);
        } else if (Hora.getText().toString().isEmpty()) {
            Hora.setError("Seleccione una hora");
            ToastUtils.show(this, "Seleccione una hora", ToastUtils.INFO);
        } else {

            dataDispensarios dispensarioSeleccionado = (dataDispensarios) SpinnerDispensario.getSelectedItem();
            if (dispensarioSeleccionado.getId().equals("0")) {
                ToastUtils.show(this, "Seleccione un dispensario", ToastUtils.INFO);
                return;
            }

            String seleccion = SpinnerLado.getSelectedItem().toString();
            if (seleccion.equals("Seleccione")) {
                ToastUtils.show(this, "Seleccione un lado", ToastUtils.INFO);
                return;
            }

            dataUsuario responsableSeleccionado = (dataUsuario) SpinnerResponsable.getSelectedItem();
            if (responsableSeleccionado.getId() == 0) {
                ToastUtils.show(this, "Seleccione un responsable", ToastUtils.INFO);
                return;
            }

            AgregarProfeco();
        }
    }

    private void AgregarProfeco() {

        showProgressDialog(this);
        String fecha = Fecha.getText().toString();
        String hora = Hora.getText().toString();
        String lado = SpinnerLado.getSelectedItem().toString();

        dataDispensarios dispensarioSeleccionado = (dataDispensarios) SpinnerDispensario.getSelectedItem();
        String idDispensario = dispensarioSeleccionado.getId();

        String motivo = Motivo.getText().toString();
        String observaciones = TxtObservaciones.getText().toString();

        dataUsuario responsableSeleccionado = (dataUsuario) SpinnerResponsable.getSelectedItem();
        String idResponsable = String.valueOf(responsableSeleccionado.getId());

        // Reiniciar para evitar que valores queden de operaciones anteriores
        ValProducto1 = "";
        ValProducto2 = "";
        ValProducto3 = "";

        if (Producto1.isChecked()) {
            ValProducto1 = ProductoUno;
        }
        if (Producto2.isChecked()) {
            ValProducto2 = ProductoDos;
        }
        if (Producto3.isChecked()) {
            ValProducto3 = ProductoTres;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_SERVIDOR + "Dispensario/agregar-bitacora-dispensario.php",
                response -> {
                    try {

                        JSONArray jsonarray = new JSONArray(response);
                        int estado = 0;
                        String mensaje = "";

                        if (jsonarray.length() > 0) {
                            JSONObject obj = jsonarray.getJSONObject(0);
                            estado = obj.getInt("estado");
                            mensaje = obj.getString("mensaje");
                        }

                        if (estado == 1) {
                            ToastUtils.showAndThen(ProfecoCrearEditar.this, mensaje, ToastUtils.SUCCESS, () -> {
                                setResult(RESULT_OK);
                                finish();
                            });

                        } else {
                            hideProgressDialog();
                            ToastUtils.show(ProfecoCrearEditar.this, mensaje, ToastUtils.ERROR);
                        }

                    } catch (Exception e) {
                        Log.d("respuesta", e.toString());
                        ToastUtils.show(ProfecoCrearEditar.this, e.toString(), ToastUtils.INFO);
                        hideProgressDialog();
                    }
                },
                error -> {
                    ToastUtils.show(ProfecoCrearEditar.this, error.toString(), ToastUtils.INFO);
                    hideProgressDialog();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("idEstacion", String.valueOf(idEstacion));
                params.put("fecha", fecha);
                params.put("hora", hora);
                params.put("dispensario", idDispensario);
                params.put("lado", lado);
                params.put("producto1", ValProducto1);
                params.put("producto2", ValProducto2);
                params.put("producto3", ValProducto3);
                params.put("motivo", motivo);
                params.put("responsable", idResponsable);
                params.put("observaciones", observaciones);

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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
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
