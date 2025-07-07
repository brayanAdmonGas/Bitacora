package com.gestogas.gestoline.profeco;

import static com.gestogas.gestoline.utils.Constantes.URL_SERVIDOR;
import static com.gestogas.gestoline.utils.DialogHelper.hideProgressDialog;
import static com.gestogas.gestoline.utils.DialogHelper.showProgressDialog;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.AutoCompleteTextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;

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
    private String ProductoUno, ProductoDos, ProductoTres, ValProducto1 = "", ValProducto2 = "", ValProducto3 = "";
    SwitchCompat Producto1, Producto2, Producto3;
    Button BtnGuardar;
    double latitudeEstacion = 0, longitudeEstacion = 0, latitudeEquipo = 0, longitudeEquipo = 0;
    int distMax = 0;
    AutoCompleteTextView SpinnerLado, SpinnerDispensario, SpinnerResponsable;

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

        // Cargar lista dispensarios y responsables
        showProgressDialog(this);
        ListaDispensario();
        ListaResponsable();

        // Configurar campos para que muestren diálogo modal con radio buttons
        configurarDialogosSeleccion();

        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            fechaCalendario();
        };

        Fecha.setOnClickListener(v -> new DatePickerDialog(this, dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show());

        Hora.setOnClickListener(v -> ObtenerHora());

        if (ProductoUno.isEmpty()) Producto1.setVisibility(View.GONE);
        if (ProductoDos.isEmpty()) Producto2.setVisibility(View.GONE);
        if (ProductoTres.isEmpty()) Producto3.setVisibility(View.GONE);

        Producto1.setText(ProductoUno);
        Producto2.setText(ProductoDos);
        Producto3.setText(ProductoTres);

        boolean validarDistancia = DistanciaUtils.validarDistancia(
                getApplicationContext(), idPuesto,
                latitudeEquipo, longitudeEquipo,
                latitudeEstacion, longitudeEstacion, distMax
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

    private void configurarDialogosSeleccion() {
        // Dispensario
        SpinnerDispensario.setKeyListener(null);
        SpinnerDispensario.setOnClickListener(v -> {
            List<String> dispensariosNombres = new ArrayList<>();
            for (dataDispensarios d : listaDispensarios) {
                dispensariosNombres.add(d.toString());
            }
            int selectedIndex = dispensariosNombres.indexOf(SpinnerDispensario.getText().toString());

            new MaterialAlertDialogBuilder(this)
                    .setTitle("Selecciona dispensario")
                    .setSingleChoiceItems(dispensariosNombres.toArray(new String[0]), selectedIndex, (dialog, which) -> {
                        SpinnerDispensario.setText(dispensariosNombres.get(which));
                        dialog.dismiss();
                    })
                    .show();
        });

        // Lado
        SpinnerLado.setKeyListener(null);
        SpinnerLado.setOnClickListener(v -> {
            String[] ladosArray = lado.toArray(new String[0]);
            int selectedIndex = lado.indexOf(SpinnerLado.getText().toString());

            new MaterialAlertDialogBuilder(this)
                    .setTitle("Selecciona lado")
                    .setSingleChoiceItems(ladosArray, selectedIndex, (dialog, which) -> {
                        SpinnerLado.setText(ladosArray[which]);
                        dialog.dismiss();
                    })
                    .show();
        });

        // Responsable
        SpinnerResponsable.setKeyListener(null);
        SpinnerResponsable.setOnClickListener(v -> {
            List<String> responsablesNombres = new ArrayList<>();
            for (dataUsuario u : listaResponsable) {
                responsablesNombres.add(u.toString());
            }
            int selectedIndex = responsablesNombres.indexOf(SpinnerResponsable.getText().toString());

            new MaterialAlertDialogBuilder(this)
                    .setTitle("Selecciona responsable")
                    .setSingleChoiceItems(responsablesNombres.toArray(new String[0]), selectedIndex, (dialog, which) -> {
                        SpinnerResponsable.setText(responsablesNombres.get(which));
                        dialog.dismiss();
                    })
                    .show();
        });
    }

    private void fechaCalendario() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Fecha.setText(sdf.format(calendar.getTime()));
    }

    private void ObtenerHora() {
        Calendar calendar = Calendar.getInstance();
        int hora = calendar.get(Calendar.HOUR_OF_DAY);
        int minuto = calendar.get(Calendar.MINUTE);

        new TimePickerDialog(this, (view, hourOfDay, minuteSelected) -> {
            String horaFormateada = String.format("%02d", hourOfDay % 12 == 0 ? 12 : hourOfDay % 12);
            String minutoFormateado = String.format("%02d", minuteSelected);
            String amPm = (hourOfDay < 12) ? "a.m." : "p.m.";
            Hora.setText(horaFormateada + ":" + minutoFormateado + " " + amPm);
        }, hora, minuto, false).show();
    }

    private void ListaDispensario() {
        listaDispensarios.clear();
        listaDispensarios.add(new dataDispensarios("0", "Seleccione", "", "", "", "", "", "", "", "", ""));
        String url = URL_SERVIDOR + "Dispensario/lista-dispensario-estacion.php?idEstacion=" + idEstacion;

        Volley.newRequestQueue(this).add(new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            listaDispensarios.add(new dataDispensarios(
                                    obj.getString("id"),
                                    obj.getString("nodispensario"),
                                    obj.getString("marca"),
                                    "", "", "", "", "", "", "", ""));
                        }
                        // Actualizar adapter para el campo dispensario
                        ArrayAdapter<dataDispensarios> adapter = new ArrayAdapter<>(
                                this, android.R.layout.simple_dropdown_item_1line, listaDispensarios);
                        SpinnerDispensario.setAdapter(adapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    hideProgressDialog();
                },
                error -> hideProgressDialog()));
    }

    private void ListaResponsable() {
        listaResponsable.clear();
        listaResponsable.add(new dataUsuario(0, "Seleccione"));
        String url = URL_SERVIDOR + "Dispensario/lista-dispensario-responsable.php?idEstacion=" + idEstacion;

        Volley.newRequestQueue(this).add(new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            listaResponsable.add(new dataUsuario(
                                    Integer.parseInt(obj.getString("id")),
                                    obj.getString("nombre")));
                        }
                        // Actualizar adapter para el campo responsable
                        ArrayAdapter<dataUsuario> adapter = new ArrayAdapter<>(
                                this, android.R.layout.simple_dropdown_item_1line, listaResponsable);
                        SpinnerResponsable.setAdapter(adapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    hideProgressDialog();
                },
                error -> hideProgressDialog()));
    }

    private void validaProfeco() {
        if (Fecha.getText().toString().isEmpty()) {
            Fecha.setError("Seleccione una fecha");
            return;
        }
        if (Hora.getText().toString().isEmpty()) {
            Hora.setError("Seleccione una hora");
            return;
        }

        String dispensarioStr = SpinnerDispensario.getText().toString();
        dataDispensarios dispensarioSeleccionado = null;
        for (dataDispensarios item : listaDispensarios) {
            if (item.toString().equals(dispensarioStr)) {
                dispensarioSeleccionado = item;
                break;
            }
        }
        if (dispensarioSeleccionado == null || dispensarioSeleccionado.getId().equals("0")) {
            ToastUtils.show(this, "Seleccione un dispensario válido", ToastUtils.INFO);
            return;
        }

        String ladoStr = SpinnerLado.getText().toString();
        if (ladoStr.equals("Seleccione")) {
            ToastUtils.show(this, "Seleccione un lado válido", ToastUtils.INFO);
            return;
        }

        String responsableStr = SpinnerResponsable.getText().toString();
        dataUsuario responsableSeleccionado = null;
        for (dataUsuario item : listaResponsable) {
            if (item.toString().equals(responsableStr)) {
                responsableSeleccionado = item;
                break;
            }
        }
        if (responsableSeleccionado == null || responsableSeleccionado.getId() == 0) {
            ToastUtils.show(this, "Seleccione un responsable válido", ToastUtils.INFO);
            return;
        }

        AgregarProfeco(dispensarioSeleccionado, ladoStr, responsableSeleccionado);
    }

    private void AgregarProfeco(dataDispensarios dispensario, String lado, dataUsuario responsable) {
        showProgressDialog(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                URL_SERVIDOR + "Dispensario/agregar-bitacora-dispensario.php",
                response -> {
                    try {
                        JSONArray jsonarray = new JSONArray(response);
                        JSONObject obj = jsonarray.getJSONObject(0);
                        int estado = obj.getInt("estado");
                        String mensaje = obj.getString("mensaje");

                        if (estado == 1) {
                            ToastUtils.showAndThen(this, mensaje, ToastUtils.SUCCESS, () -> {
                                setResult(RESULT_OK);
                                finish();
                            });
                        } else {
                            ToastUtils.show(this, mensaje, ToastUtils.ERROR);
                            hideProgressDialog();
                        }

                    } catch (Exception e) {
                        ToastUtils.show(this, e.toString(), ToastUtils.INFO);
                        hideProgressDialog();
                    }
                },
                error -> {
                    ToastUtils.show(this, error.toString(), ToastUtils.INFO);
                    hideProgressDialog();
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("idEstacion", String.valueOf(idEstacion));
                params.put("fecha", Fecha.getText().toString());
                params.put("hora", Hora.getText().toString());
                params.put("dispensario", dispensario.getId());
                params.put("lado", lado);
                params.put("producto1", Producto1.isChecked() ? ProductoUno : "");
                params.put("producto2", Producto2.isChecked() ? ProductoDos : "");
                params.put("producto3", Producto3.isChecked() ? ProductoTres : "");
                params.put("motivo", Motivo.getText().toString());
                params.put("responsable", String.valueOf(responsable.getId()));
                params.put("observaciones", TxtObservaciones.getText().toString());
                return params;
            }
        };
        Volley.newRequestQueue(this).add(stringRequest);
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
