package com.gestogas.gestoline.recepcion;

import static com.gestogas.gestoline.utils.Constantes.URL_SERVIDOR;
import static com.gestogas.gestoline.utils.DialogHelper.hideProgressDialog;
import static com.gestogas.gestoline.utils.DialogHelper.showProgressDialog;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.gestogas.gestoline.BaseActivity;
import com.gestogas.gestoline.Evidencias;
import com.gestogas.gestoline.R;
import com.gestogas.gestoline.controllers.AppController;
import com.gestogas.gestoline.data.dataTanques;
import com.gestogas.gestoline.utils.Constantes;
import com.gestogas.gestoline.utils.DialogHelper;
import com.gestogas.gestoline.utils.DistanciaUtils;
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
import java.util.Objects;
import java.util.function.Consumer;

public class RecepcionBitacoraCrearEditar extends BaseActivity {

    private int idEstacion, idUsuario, idPuesto, distMax = 0;
    private String idRecepcion, urlServidor, ProductoUno, ProductoDos, ProductoTres, idSeleccionado = "0", Verificar1, Verificar2, Verificar3, ResultadoProducto,
            ResultadoCheck1, ResultadoCheck2, ResultadoCheck3, tanqueSeleccionado1, tanqueSeleccionado2, tanqueSeleccionado3, tanqueSeleccionado4;
    private final Calendar calendar = Calendar.getInstance();
    EditText FechaRecepcion, HoraLlegada, HoraSalida, LineaTransporte, NoRemolque, Placas, Operador, NoDrum, NoFactura, LitrosCompra,
            InventarioI1, InventarioI2, InventarioI3, InventarioI4, InventarioF1, InventarioF2, InventarioF3, InventarioF4, Manometro, Temperatura, Observaciones;
    TextInputLayout InputLayout;
    SwitchCompat Producto1, Producto2, Producto3;
    private Spinner spinnerTanque1, spinnerTanque2, spinnerTanque3, spinnerTanque4;
    ArrayList<String> tanque;
    CheckBox Si_1, Si_2, Si_3, No_1, No_2, No_3;
    private AutoCompleteTextView personaRecibe;
    private ArrayAdapter<String> adapter;
    private final Map<String, String> mapaPersonal = new HashMap<>();
    private final List<String> listaNombres = new ArrayList<>();
    private final List<dataTanques> listaTanques = new ArrayList<>();
    Button BtnGuardar;
    double latitudeEstacion = 0, longitudeEstacion = 0, latitudeEquipo = 0, longitudeEquipo = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recepcion_bitacora_crear_editar);

        idEstacion = AppController.getInstance().GetIdestacion();
        idUsuario = AppController.getInstance().GetidUsuario();
        ProductoUno = AppController.getInstance().GetProductoUno();
        ProductoDos = AppController.getInstance().GetProductoDos();
        ProductoTres = AppController.getInstance().GetProductoTres();
        idPuesto = AppController.getInstance().GetIdPuesto();
        latitudeEquipo = Double.parseDouble(AppController.getInstance().GetLatitudEquipo());
        longitudeEquipo = Double.parseDouble(AppController.getInstance().GetLongitudEquipo());

        distMax = Integer.parseInt(AppController.getInstance().GetDistMax());
        latitudeEstacion = Double.parseDouble(AppController.getInstance().GetLatitud());
        longitudeEstacion = Double.parseDouble(AppController.getInstance().GetLongitud());

        idRecepcion = getIntent().getStringExtra("idRecepcion");
        String titulo = getIntent().getStringExtra("titulo");
        String tituloboton = getIntent().getStringExtra("tituloboton");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(titulo);
        }

        FechaRecepcion = findViewById(R.id.FechaRecepcion);
        HoraLlegada = findViewById(R.id.HoraLlegada);
        HoraSalida = findViewById(R.id.HoraSalida);
        LineaTransporte = findViewById(R.id.LineaTransporte);
        NoRemolque = findViewById(R.id.NoRemolque);
        Placas = findViewById(R.id.Placas);
        Operador = findViewById(R.id.Operador);
        NoDrum = findViewById(R.id.NoDrum);
        NoFactura = findViewById(R.id.NoFactura);
        LitrosCompra = findViewById(R.id.LitrosCompra);

        Producto1 = findViewById(R.id.Producto1);
        Producto2 = findViewById(R.id.Producto2);
        Producto3 = findViewById(R.id.Producto3);

        tanque = new ArrayList<>();
        spinnerTanque1 = findViewById(R.id.spinnerTanque1);
        spinnerTanque2 = findViewById(R.id.spinnerTanque2);
        spinnerTanque3 = findViewById(R.id.spinnerTanque3);
        spinnerTanque4 = findViewById(R.id.spinnerTanque4);

        InventarioI1 = findViewById(R.id.InventarioI1);
        InventarioI2 = findViewById(R.id.InventarioI2);
        InventarioI3 = findViewById(R.id.InventarioI3);
        InventarioI4 = findViewById(R.id.InventarioI4);
        InventarioF1 = findViewById(R.id.InventarioF1);
        InventarioF2 = findViewById(R.id.InventarioF2);
        InventarioF3 = findViewById(R.id.InventarioF3);
        InventarioF4 = findViewById(R.id.InventarioF4);

        Si_1 = findViewById(R.id.Si_1);
        No_1 = findViewById(R.id.No_1);
        Si_2 = findViewById(R.id.Si_2);
        No_2 = findViewById(R.id.No_2);
        Si_3 = findViewById(R.id.Si_3);
        No_3 = findViewById(R.id.No_3);

        Manometro = findViewById(R.id.Manometro);
        Temperatura = findViewById(R.id.Temperatura);
        Observaciones = findViewById(R.id.Observaciones);
        InputLayout = findViewById(R.id.InputLayout);
        personaRecibe = findViewById(R.id.PersonaRecibe);
        BtnGuardar = findViewById(R.id.BtnGuardar);

        LinearLayout layoutFueraRango = findViewById(R.id.layoutFueraRango);

        if (idRecepcion == null || idRecepcion.trim().isEmpty()) {

            FechaRecepcion.setText(calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DAY_OF_MONTH));
            urlServidor = "Recepcion/agregar-recepcion-descarga.php";
            limpiarSpinner();

        } else {

            DialogHelper.showProgressDialog(this);
            fetchRecepcionBitacora();
            urlServidor = "Recepcion/editar-recepcion-descarga.php";
        }

        BtnGuardar.setText(tituloboton);

        if (ProductoUno.isEmpty()) {
            Producto1.setVisibility(View.GONE);
        }

        if (ProductoDos.isEmpty()) {
            Producto2.setVisibility(View.GONE);
        }

        if (ProductoTres.isEmpty()) {
            Producto3.setVisibility(View.GONE);
        }

        Verificar1 = "Se encuentran en buen estado";
        Verificar2 = "Sin rastro de sustancias aceitosas";
        Verificar3 = "Nivel del producto está a más de 1.5 cm (+/-0.3 cm)";

        if (idEstacion == 54 || idEstacion == 55) {
            InputLayout.setVisibility(View.VISIBLE);
        }

        ListaPersonal();

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

        FechaRecepcion.setOnClickListener(v -> {
            new DatePickerDialog(this, dateSetListener,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        HoraLlegada.setOnClickListener(v -> {
            ObtenerHora(TipoHora.LLEGADA);
        });

        HoraSalida.setOnClickListener(v -> {
            ObtenerHora(TipoHora.SALIDA);
        });

        Producto1.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                showProgressDialog(this);
                Producto2.setEnabled(false);
                Producto3.setEnabled(false);
                tanque.clear();
                ListaTanques(ProductoDos);
            } else {
                Producto2.setEnabled(true);
                Producto3.setEnabled(true);

                limpiarSpinner();

            }

        });

        Producto2.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (isChecked) {
                showProgressDialog(this);
                Producto1.setEnabled(false);
                Producto3.setEnabled(false);
                tanque.clear();
                ListaTanques(ProductoUno);
            } else {
                Producto1.setEnabled(true);
                Producto3.setEnabled(true);

                limpiarSpinner();


            }

        });

        Producto3.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                showProgressDialog(this);
                Producto1.setEnabled(false);
                Producto2.setEnabled(false);
                tanque.clear();
                ListaTanques(ProductoTres);
            } else {
                Producto1.setEnabled(true);
                Producto2.setEnabled(true);


                limpiarSpinner();
            }
        });

        BtnGuardar.setOnClickListener(v -> {
            validarCampos();
        });

    }

    public enum TipoHora {
        LLEGADA,
        SALIDA
    }

    private void ObtenerHora(final TipoHora tipo) {
        Calendar calendar = Calendar.getInstance();
        int hora = calendar.get(Calendar.HOUR_OF_DAY);
        int minuto = calendar.get(Calendar.MINUTE);

        TimePickerDialog picker = new TimePickerDialog(this, (view, hourOfDay, minuteSelected) -> {
            String horaFormateada = String.format("%02d", hourOfDay % 12 == 0 ? 12 : hourOfDay % 12);
            String minutoFormateado = String.format("%02d", minuteSelected);
            String amPm = (hourOfDay < 12) ? "a.m." : "p.m.";
            String horaFinal = horaFormateada + ":" + minutoFormateado + " " + amPm;

            if (tipo == TipoHora.LLEGADA) {
                HoraLlegada.setText(horaFinal);
            } else if (tipo == TipoHora.SALIDA) {
                HoraSalida.setText(horaFinal);
            }
        }, hora, minuto, false);

        picker.show();
    }

    private void fechaCalendario() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        FechaRecepcion.setText(sdf.format(calendar.getTime()));
    }

    private void fetchRecepcionBitacora() {
        DialogHelper.showProgressDialog(this);
        String url = Constantes.URL_SERVIDOR + "Recepcion/detalle-recepcion-descarga.php?idRecepcion=" + idRecepcion;
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onResponse(JSONArray response) {
                        String folio = "", fecha = "", horallegada = "", horasalida = "", nofactura = "", producto = "", litroscompra = "",
                                lineatransporte = "", noremolque = "", placa = "", operador = "", manometro = "", temperatura = "",
                                observaciones = "", FPR = "", FPS = "", estado = "", nodrum = "", verificar1 = "", resultado1 = "", verificar2 = "",
                                resultado2 = "", verificar3 = "", resultado3 = "", idtanque1 = "", inventarioinicial1 = "", inventariofinal1 = "",
                                idtanque2 = "", inventarioinicial2 = "", inventariofinal2 = "", idtanque3 = "", inventarioinicial3 = "", inventariofinal3 = "",
                                idtanque4 = "", inventarioinicial4 = "", inventariofinal4 = "", totalmerma = "",
                                FPRid = "", FPRnombre = "";

                        try {

                            // Recorre el JSONArray
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jsonObject = response.getJSONObject(i);

                                // Extrae los datos del JSON
                                folio = jsonObject.getString("folio");
                                fecha = jsonObject.getString("fecha_recepcion");
                                horallegada = jsonObject.getString("horallegada_recepcion");
                                horasalida = jsonObject.getString("horasalida_recepcion");
                                lineatransporte = jsonObject.getString("lineatransporte");
                                noremolque = jsonObject.getString("noremolque");
                                placa = jsonObject.getString("placa");
                                operador = jsonObject.getString("operador");
                                nofactura = jsonObject.getString("nofactura");
                                litroscompra = jsonObject.getString("litroscompra");
                                producto = jsonObject.getString("producto");
                                manometro = jsonObject.getString("manometro");
                                temperatura = jsonObject.getString("temperatura");
                                observaciones = jsonObject.getString("observaciones");

                                FPRid = jsonObject.getString("FPRid");
                                FPRnombre = jsonObject.getString("FPRnombre");
                                FPR = jsonObject.getString("FPR");

                                FPS = jsonObject.getString("FPS");

                                estado = jsonObject.getString("estado");
                                nodrum = jsonObject.getString("nodrum");

                                verificar1 = jsonObject.getString("verificar1");
                                resultado1 = jsonObject.getString("resultado1");
                                verificar2 = jsonObject.getString("verificar2");
                                resultado2 = jsonObject.getString("resultado2");
                                verificar3 = jsonObject.getString("verificar3");
                                resultado3 = jsonObject.getString("resultado3");

                                idtanque1 = jsonObject.getString("idtanque1");
                                inventarioinicial1 = jsonObject.getString("inventarioinicial1");
                                inventariofinal1 = jsonObject.getString("inventariofinal1");
                                idtanque2 = jsonObject.getString("idtanque2");
                                inventarioinicial2 = jsonObject.getString("inventarioinicial2");
                                inventariofinal2 = jsonObject.getString("inventariofinal2");
                                idtanque3 = jsonObject.getString("idtanque3");
                                inventarioinicial3 = jsonObject.getString("inventarioinicial3");
                                inventariofinal3 = jsonObject.getString("inventariofinal3");
                                idtanque4 = jsonObject.getString("idtanque4");
                                inventarioinicial4 = jsonObject.getString("inventarioinicial4");
                                inventariofinal4 = jsonObject.getString("inventariofinal4");

                            }

                            FechaRecepcion.setText(fecha);
                            HoraLlegada.setText(horallegada);
                            HoraSalida.setText(horasalida);
                            LineaTransporte.setText(lineatransporte);
                            NoRemolque.setText(noremolque);
                            Placas.setText(placa);
                            Operador.setText(operador);
                            NoFactura.setText(nofactura);
                            LitrosCompra.setText(quitarComas(litroscompra));

                            NoDrum.setText(nodrum);

                            if(Objects.equals(ProductoDos, producto)){
                                Producto1.setChecked(true);
                                ListaTanques(ProductoDos);
                            }else if(Objects.equals(ProductoUno, producto)){
                                Producto2.setChecked(true);
                                ListaTanques(ProductoUno);
                            }else if(Objects.equals(ProductoTres, producto)){
                                Producto3.setChecked(true);
                                ListaTanques(ProductoTres);
                            }

                            InventarioI1.setText(quitarComas(inventarioinicial1));
                            InventarioI2.setText(quitarComas(inventarioinicial2));
                            InventarioI3.setText(quitarComas(inventarioinicial3));
                            InventarioI4.setText(quitarComas(inventarioinicial4));
                            InventarioF1.setText(quitarComas(inventariofinal1));
                            InventarioF2.setText(quitarComas(inventariofinal2));
                            InventarioF3.setText(quitarComas(inventariofinal3));
                            InventarioF4.setText(quitarComas(inventariofinal4));

                            tanqueSeleccionado1 = idtanque1;
                            tanqueSeleccionado2 = idtanque2;
                            tanqueSeleccionado3 = idtanque3;
                            tanqueSeleccionado4 = idtanque4;

                            if(resultado1.equals("Si")){
                                Si_1.setChecked(true);
                                No_1.setEnabled(false);
                            }else{
                                No_1.setChecked(true);
                                Si_1.setEnabled(false);
                            }

                            if(resultado2.equals("Si")){
                                Si_2.setChecked(true);
                                No_2.setEnabled(false);
                            }else{
                                No_2.setChecked(true);
                                Si_2.setEnabled(false);
                            }

                            if(resultado3.equals("Si")){
                                Si_3.setChecked(true);
                                No_3.setEnabled(false);
                            }else{
                                No_3.setChecked(true);
                                Si_3.setEnabled(false);
                            }

                            Manometro.setText(manometro);
                            Temperatura.setText(temperatura);
                            Observaciones.setText(observaciones);

                            personaRecibe.setText(FPRnombre);
                            idSeleccionado = FPRid;


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

    public String quitarComas(String numero) {
        if (numero != null) {
            return numero.replace(",", "");
        }
        return "";
    }

    private void ListaTanques(String producto) {
        String url = URL_SERVIDOR + "TanqueAlmacenamiento/lista-tanque-almacenamiento.php?idEstacion=" + idEstacion + "&Producto=" + producto;

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        listaTanques.clear(); // limpiar anteriores
                        listaTanques.add(new dataTanques(0, "", "", ""));
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("Tanque");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            int idTanque = obj.getInt("IdTanque");
                            String noTanque = obj.getString("NoTanque");
                            String capacidad = obj.getString("Capacidad");

                            dataTanques item = new dataTanques(idTanque, noTanque, capacidad, producto);
                            listaTanques.add(item);
                        }

                        ArrayAdapter<dataTanques> adapter = new ArrayAdapter<>(
                                RecepcionBitacoraCrearEditar.this,
                                android.R.layout.simple_spinner_dropdown_item,
                                listaTanques
                        );

                        spinnerTanque1.setAdapter(adapter);
                        spinnerTanque2.setAdapter(adapter);
                        spinnerTanque3.setAdapter(adapter);
                        spinnerTanque4.setAdapter(adapter);

                        seleccionarTanquePorId(spinnerTanque1, Integer.parseInt(tanqueSeleccionado1));
                        seleccionarTanquePorId(spinnerTanque2, Integer.parseInt(tanqueSeleccionado2));
                        seleccionarTanquePorId(spinnerTanque3, Integer.parseInt(tanqueSeleccionado3));
                        seleccionarTanquePorId(spinnerTanque4, Integer.parseInt(tanqueSeleccionado4));

                        setOnSpinnerSelected(spinnerTanque1, id -> tanqueSeleccionado1 = String.valueOf(id));
                        setOnSpinnerSelected(spinnerTanque2, id -> tanqueSeleccionado2 = String.valueOf(id));
                        setOnSpinnerSelected(spinnerTanque3, id -> tanqueSeleccionado3 = String.valueOf(id));
                        setOnSpinnerSelected(spinnerTanque4, id -> tanqueSeleccionado4 = String.valueOf(id));

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
    private void seleccionarTanquePorId(Spinner spinner, int idBuscado) {

        ArrayAdapter<dataTanques> adapter = (ArrayAdapter<dataTanques>) spinner.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            dataTanques tanque = adapter.getItem(i);
            if (tanque != null && tanque.getId() == idBuscado) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    private void setOnSpinnerSelected(Spinner spinner, Consumer<Integer> onSelectedId) {

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                dataTanques seleccionado = (dataTanques) parent.getItemAtPosition(position);
                if (seleccionado != null) {
                    onSelectedId.accept(seleccionado.getId());
                } else {
                    onSelectedId.accept(0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                onSelectedId.accept(0);
            }
        });
    }

    private void limpiarSpinner() {
        listaTanques.clear();
        listaTanques.add(new dataTanques(0, "", "", ""));
        ArrayAdapter<dataTanques> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                listaTanques
        );

        spinnerTanque1.setAdapter(adapter);
        spinnerTanque2.setAdapter(adapter);
        spinnerTanque3.setAdapter(adapter);
        spinnerTanque4.setAdapter(adapter);

        setOnSpinnerSelected(spinnerTanque1, id -> tanqueSeleccionado1 = "0");
        setOnSpinnerSelected(spinnerTanque2, id -> tanqueSeleccionado2 = "0");
        setOnSpinnerSelected(spinnerTanque3, id -> tanqueSeleccionado3 = "0");
        setOnSpinnerSelected(spinnerTanque4, id -> tanqueSeleccionado4 = "0");

    }

    public void CheckboxClicked(View view) {
        boolean checked = ((CheckBox) view).isChecked();
        String viewIdName = getResources().getResourceEntryName(view.getId());

        // Determina el prefijo opuesto: "Si" ↔ "No"
        String prefix = viewIdName.startsWith("Si") ? "No" : "Si";

        // Extrae el número de índice del ID (por ejemplo, "1" de "Si_1")
        String index = viewIdName.split("_")[1];

        // Construye el ID del CheckBox opuesto
        int otherCheckBoxId = getResources().getIdentifier(prefix + "_" + index, "id", getPackageName());
        CheckBox otherCheckBox = findViewById(otherCheckBoxId);

        if (otherCheckBox != null) {
            otherCheckBox.setEnabled(!checked);
        }
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
                            personaRecibe.setOnClickListener(v -> {
                                int selectedIndex = listaNombres.indexOf(personaRecibe.getText().toString());

                                new MaterialAlertDialogBuilder(RecepcionBitacoraCrearEditar.this)
                                        .setTitle("Selecciona el personal")
                                        .setSingleChoiceItems(listaNombres.toArray(new String[0]), selectedIndex, (dialog, which) -> {
                                            String seleccionado = listaNombres.get(which);
                                            personaRecibe.setText(seleccionado);

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


    public void validarCampos() {

        final String fecharecepcion = FechaRecepcion.getText().toString().trim();
        final String horadellegada = HoraLlegada.getText().toString().trim();
        final String horadesalida = HoraSalida.getText().toString().trim();
        final String lineatransporte = LineaTransporte.getText().toString().trim();
        final String noremolque = NoRemolque.getText().toString().trim();
        final String placas = Placas.getText().toString().trim();
        final String operador = Operador.getText().toString().trim();
        final String Factura = NoFactura.getText().toString().trim();
        final String LitrosC = LitrosCompra.getText().toString().trim();

        final String tanque1 = tanqueSeleccionado1;
        final String inventarioi1 = InventarioI1.getText().toString().trim();
        final String inventariof1 = InventarioF1.getText().toString().trim();
        final String tanque2 = tanqueSeleccionado2;
        final String inventarioi2 = InventarioI2.getText().toString().trim();
        final String inventariof2 = InventarioF2.getText().toString().trim();
        final String tanque3 = tanqueSeleccionado3;
        final String inventarioi3 = InventarioI3.getText().toString().trim();
        final String inventariof3 = InventarioF3.getText().toString().trim();
        final String tanque4 = tanqueSeleccionado4;
        final String inventarioi4 = InventarioI4.getText().toString().trim();
        final String inventariof4 = InventarioF4.getText().toString().trim();

        final String manometro = Manometro.getText().toString().trim();
        final String temperatura = Temperatura.getText().toString().trim();
        final String observaciones = Observaciones.getText().toString().trim();
        final String numerodrum = NoDrum.getText().toString().trim();

        if (Producto1.isChecked()) {
            ResultadoProducto = ProductoDos;
        } else if (Producto2.isChecked()) {
            ResultadoProducto = ProductoUno;
        } else if (Producto3.isChecked()) {
            ResultadoProducto = ProductoTres;
        } else {
            ResultadoProducto = "";
        }

        ResultadoCheck1 = Si_1.isChecked() ? "Si" : (No_1.isChecked() ? "No" : "");
        ResultadoCheck2 = Si_2.isChecked() ? "Si" : (No_2.isChecked() ? "No" : "");
        ResultadoCheck3 = Si_3.isChecked() ? "Si" : (No_3.isChecked() ? "No" : "");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_SERVIDOR + urlServidor,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {

                            JSONArray jsonarray = new JSONArray(response);
                            int estado = 0;
                            String id,mensaje = "";

                            if (jsonarray.length() > 0) {
                                JSONObject obj = jsonarray.getJSONObject(0);
                                id = obj.getString("idrecepcion");
                                estado = obj.getInt("estado");
                                mensaje = obj.getString("mensaje");
                            } else {
                                id = "";
                            }

                            if (estado == 1) {

                                ToastUtils.showAndThen(RecepcionBitacoraCrearEditar.this, mensaje, ToastUtils.SUCCESS, () -> {
                                    Intent home = new Intent(getApplicationContext(), Evidencias.class);
                                    home.putExtra("id", id);
                                    home.putExtra("categoria", "Recepcion");
                                    home.putExtra("carpeta", "Recepcion");
                                    home.putExtra("titulo", "Evidencia Recepción");
                                    startActivityForResult(home, 1);
                                    setResult(RESULT_OK);
                                    finish();
                                });

                            } else {
                                hideProgressDialog();
                                ToastUtils.show(RecepcionBitacoraCrearEditar.this, mensaje, ToastUtils.ERROR);
                            }

                        } catch (Exception e) {
                            ToastUtils.show(RecepcionBitacoraCrearEditar.this, e.toString(), ToastUtils.INFO);
                            hideProgressDialog();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ToastUtils.show(RecepcionBitacoraCrearEditar.this, error.toString(), ToastUtils.INFO);
                        hideProgressDialog();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("idRecepcion", idRecepcion);
                params.put("idEstacion", String.valueOf(idEstacion));
                params.put("Fecha", fecharecepcion);
                params.put("HoraLlegada", horadellegada);
                params.put("HoraSalida", horadesalida);
                params.put("LineaTransporte", lineatransporte);
                params.put("NoRemolque", noremolque);
                params.put("Placa", placas);
                params.put("Operador", operador);
                params.put("Factura", Factura);
                params.put("LitrosCompra", LitrosC);
                params.put("ResultadoProducto", ResultadoProducto);

                params.put("Tanque1", tanque1);
                params.put("Inventarioi1", inventarioi1);
                params.put("Inventariof1", inventariof1);

                params.put("Tanque2", tanque2);
                params.put("Inventarioi2", inventarioi2);
                params.put("Inventariof2", inventariof2);

                params.put("Tanque3", tanque3);
                params.put("Inventarioi3", inventarioi3);
                params.put("Inventariof3", inventariof3);

                params.put("Tanque4", tanque4);
                params.put("Inventarioi4", inventarioi4);
                params.put("Inventariof4", inventariof4);

                params.put("Verificar1", Verificar1);
                params.put("ResultadoCheck1", ResultadoCheck1);
                params.put("Verificar2", Verificar2);
                params.put("ResultadoCheck2", ResultadoCheck2);

                params.put("Verificar3", Verificar3);
                params.put("ResultadoCheck3", ResultadoCheck3);

                params.put("Manometro", manometro);
                params.put("Temperatura", temperatura);
                params.put("Observaciones", observaciones);
                params.put("NoDrum", numerodrum);

                params.put("PersonalRevisa", idSeleccionado != null ? idSeleccionado : "0");
                params.put("PersonalSupervisa", String.valueOf(idUsuario));

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