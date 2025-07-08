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
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;

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
import com.gestogas.gestoline.utils.DistanciaUtils;
import com.gestogas.gestoline.utils.TecladoUtils;
import com.gestogas.gestoline.utils.ToastUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

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
    AutoCompleteTextView SpinnerDispensario, SpinnerLado, SpinnerResponsable;
    SwitchCompat Producto1, Producto2, Producto3;
    LinearLayout layoutFueraRango;
    List<dataDispensarios> listaDispensarios = new ArrayList<>();
    List<dataUsuario> listaResponsable = new ArrayList<>();
    ArrayList<String> lado = new ArrayList<>();
    String ProductoUno = "", ProductoDos = "", ProductoTres = "";
    String ValProducto1 = "", ValProducto2 = "", ValProducto3 = "";
    double latitudeEstacion = 0, longitudeEstacion = 0, latitudeEquipo = 0, longitudeEquipo = 0;
    int distMax = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profeco_crear_editar);

        idEstacion = AppController.getInstance().GetIdestacion();
        idPuesto = AppController.getInstance().GetIdPuesto();
        ProductoUno = String.valueOf(AppController.getInstance().GetProductoUno());
        ProductoDos = String.valueOf(AppController.getInstance().GetProductoDos());
        ProductoTres = String.valueOf(AppController.getInstance().GetProductoTres());
        latitudeEquipo = Double.parseDouble(AppController.getInstance().GetLatitudEquipo());
        longitudeEquipo = Double.parseDouble(AppController.getInstance().GetLongitudEquipo());
        distMax = Integer.parseInt(AppController.getInstance().GetDistMax());
        latitudeEstacion = Double.parseDouble(AppController.getInstance().GetLatitud());
        longitudeEstacion = Double.parseDouble(AppController.getInstance().GetLongitud());

        // Vistas
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getIntent().getStringExtra("titulo"));
        }

        Fecha = findViewById(R.id.Fecha);
        Hora = findViewById(R.id.Hora);
        SpinnerLado = findViewById(R.id.SpinnerLado);
        SpinnerDispensario = findViewById(R.id.SpinnerDispensario);
        SpinnerResponsable = findViewById(R.id.SpinnerResponsable);
        Motivo = findViewById(R.id.TxtMotivo);
        TxtObservaciones = findViewById(R.id.TxtObservaciones);
        Producto1 = findViewById(R.id.Producto1);
        Producto2 = findViewById(R.id.Producto2);
        Producto3 = findViewById(R.id.Producto3);
        layoutFueraRango = findViewById(R.id.layoutFueraRango);
        findViewById(R.id.BtnGuardar).setOnClickListener(v -> validaProfeco());

        // Lados
        lado.add("Seleccione");
        lado.add("A");
        lado.add("B");
        SpinnerLado.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, lado));
        // === DIÁLOGO DISPENSARIO ===
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

// === DIÁLOGO LADO ===
        SpinnerLado.setKeyListener(null);
        SpinnerLado.setOnClickListener(v -> {
            int selectedIndex = lado.indexOf(SpinnerLado.getText().toString());

            new MaterialAlertDialogBuilder(this)
                    .setTitle("Selecciona lado")
                    .setSingleChoiceItems(lado.toArray(new String[0]), selectedIndex, (dialog, which) -> {
                        SpinnerLado.setText(lado.get(which));
                        dialog.dismiss();
                    })
                    .show();
        });

// === DIÁLOGO RESPONSABLE ===
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

        // Productos visibles solo si están definidos
        Producto1.setVisibility(ProductoUno.isEmpty() ? View.GONE : View.VISIBLE);
        Producto2.setVisibility(ProductoDos.isEmpty() ? View.GONE : View.VISIBLE);
        Producto3.setVisibility(ProductoTres.isEmpty() ? View.GONE : View.VISIBLE);
        Producto1.setText(ProductoUno);
        Producto2.setText(ProductoDos);
        Producto3.setText(ProductoTres);

        // Calendario
        Fecha.setOnClickListener(v -> new DatePickerDialog(this, (view, year, month, day) -> {
            calendar.set(year, month, day);
            Fecha.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.getTime()));
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show());

        // Hora
        Hora.setOnClickListener(v -> {
            Calendar now = Calendar.getInstance();
            new TimePickerDialog(this, (view, hour, minute) -> {
                String amPm = hour < 12 ? "a.m." : "p.m.";
                int hourFormat = hour % 12 == 0 ? 12 : hour % 12;
                Hora.setText(String.format(Locale.getDefault(), "%02d:%02d %s", hourFormat, minute, amPm));
            }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), false).show();
        });

        // Validar distancia
        if (DistanciaUtils.validarDistancia(this, idPuesto, latitudeEquipo, longitudeEquipo, latitudeEstacion, longitudeEstacion, distMax)) {
            findViewById(R.id.BtnGuardar).setEnabled(true);
            layoutFueraRango.setVisibility(View.GONE);
        } else {
            findViewById(R.id.BtnGuardar).setEnabled(false);
            layoutFueraRango.setVisibility(View.VISIBLE);
            findViewById(R.id.BtnGuardar).setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.color_inactivo));
        }

        showProgressDialog(this);
        ListaDispensario();
        ListaResponsable();
    }

    private void ListaDispensario() {
        listaDispensarios.clear();
        listaDispensarios.add(new dataDispensarios("0", "", "", "", "", "", "", "", "", "", ""));
        String url = URL_SERVIDOR + "Dispensario/lista-dispensario-estacion.php?idEstacion=" + idEstacion;

        Volley.newRequestQueue(this).add(new StringRequest(Request.Method.GET, url, response -> {
            try {
                JSONArray array = new JSONArray(response);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    listaDispensarios.add(new dataDispensarios(obj.getString("id"), obj.getString("nodispensario"), obj.getString("marca"), "", "", "", "", "", "", "", ""));
                }
                SpinnerDispensario.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaDispensarios));
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                hideProgressDialog();
            }
        }, error -> {
            error.printStackTrace();
            hideProgressDialog();
        }));
    }

    private void ListaResponsable() {
        listaResponsable.clear();
        listaResponsable.add(new dataUsuario(0, ""));
        String url = URL_SERVIDOR + "Dispensario/lista-dispensario-responsable.php?idEstacion=" + idEstacion;

        Volley.newRequestQueue(this).add(new StringRequest(Request.Method.GET, url, response -> {
            try {
                JSONArray array = new JSONArray(response);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    listaResponsable.add(new dataUsuario(obj.getInt("id"), obj.getString("nombre")));
                }
                SpinnerResponsable.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaResponsable));
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                hideProgressDialog();
            }
        }, error -> {
            error.printStackTrace();
            hideProgressDialog();
        }));
    }

    private void validaProfeco() {
        if (Fecha.getText().toString().isEmpty()) {
            Fecha.setError("Seleccione una fecha");
            ToastUtils.show(this, "Seleccione una fecha", ToastUtils.INFO);
            return;
        }
        if (Hora.getText().toString().isEmpty()) {
            Hora.setError("Seleccione una hora");
            ToastUtils.show(this, "Seleccione una hora", ToastUtils.INFO);
            return;
        }
        dataDispensarios dispensario = (dataDispensarios) SpinnerDispensario.getAdapter().getItem(SpinnerDispensario.getListSelection());
        if (dispensario == null || dispensario.getId().equals("0")) {
            ToastUtils.show(this, "Seleccione un dispensario", ToastUtils.INFO);
            return;
        }
        if (SpinnerLado.getText().toString().equals("Seleccione")) {
            ToastUtils.show(this, "Seleccione un lado", ToastUtils.INFO);
            return;
        }
        dataUsuario responsable = (dataUsuario) SpinnerResponsable.getAdapter().getItem(SpinnerResponsable.getListSelection());
        if (responsable == null || responsable.getId() == 0) {
            ToastUtils.show(this, "Seleccione un responsable", ToastUtils.INFO);
            return;
        }
        AgregarProfeco(dispensario, responsable);
    }

    private void AgregarProfeco(dataDispensarios dispensario, dataUsuario responsable) {
        showProgressDialog(this);
        ValProducto1 = Producto1.isChecked() ? ProductoUno : "";
        ValProducto2 = Producto2.isChecked() ? ProductoDos : "";
        ValProducto3 = Producto3.isChecked() ? ProductoTres : "";

        StringRequest request = new StringRequest(Request.Method.POST,
                URL_SERVIDOR + "Dispensario/agregar-bitacora-dispensario.php",
                response -> {
                    try {
                        JSONObject obj = new JSONArray(response).getJSONObject(0);
                        int estado = obj.getInt("estado");
                        String mensaje = obj.getString("mensaje");

                        if (estado == 1) {
                            ToastUtils.showAndThen(this, mensaje, ToastUtils.SUCCESS, () -> {
                                setResult(RESULT_OK);
                                finish();
                            });
                        } else {
                            ToastUtils.show(this, mensaje, ToastUtils.ERROR);
                        }
                    } catch (Exception e) {
                        Log.e("ERROR", e.toString());
                        ToastUtils.show(this, "Error: " + e.getMessage(), ToastUtils.INFO);
                    } finally {
                        hideProgressDialog();
                    }
                },
                error -> {
                    ToastUtils.show(this, error.toString(), ToastUtils.ERROR);
                    hideProgressDialog();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("idEstacion", String.valueOf(idEstacion));
                params.put("fecha", Fecha.getText().toString());
                params.put("hora", Hora.getText().toString());
                params.put("dispensario", dispensario.getId());
                params.put("lado", SpinnerLado.getText().toString());
                params.put("producto1", ValProducto1);
                params.put("producto2", ValProducto2);
                params.put("producto3", ValProducto3);
                params.put("motivo", Motivo.getText().toString());
                params.put("responsable", String.valueOf(responsable.getId()));
                params.put("observaciones", TxtObservaciones.getText().toString());
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        TecladoUtils.handleTouchEvent(this, ev);
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) finish();
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
