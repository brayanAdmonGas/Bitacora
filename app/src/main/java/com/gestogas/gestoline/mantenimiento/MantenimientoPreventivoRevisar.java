package com.gestogas.gestoline.mantenimiento;

import static android.view.View.VISIBLE;
import static com.gestogas.gestoline.utils.Constantes.URL_HOST;
import static com.gestogas.gestoline.utils.Constantes.URL_SERVIDOR;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.gestogas.gestoline.BaseActivity;
import com.gestogas.gestoline.Evidencias;
import com.gestogas.gestoline.R;
import com.gestogas.gestoline.controllers.AppController;
import com.gestogas.gestoline.utils.DialogHelper;
import com.gestogas.gestoline.utils.DistanciaUtils;
import com.gestogas.gestoline.utils.ImageNameGenerator;
import com.gestogas.gestoline.utils.ResultadoValida;
import com.gestogas.gestoline.utils.SignatureView;
import com.gestogas.gestoline.utils.TecladoUtils;
import com.gestogas.gestoline.utils.ToastUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MantenimientoPreventivoRevisar extends BaseActivity {
    int idEstacion, IdUsuario, idPuesto, distMax = 0;
    String idMantenimiento, NumeroEquipo, NombreEquipo, estado, valExterno = "";
    String firmaBase64, nombreImagen = "";
    TextView TxtSublista1,TxtSublista2,TxtSublista3,TxtSublista4,TxtSublista5;
    CheckBox Interno,Externo;
    TextView TxtPersonalExterno;
    ImageView ImageFirma;
    LinearLayout LinearRealizaInterno, LinearRealizaExterno, LinearGuardar;
    MaterialCardView CardInterno, CardExterno;

    Button BtbFirma, BtnGuardar;
    String idSeleccionado = "0";
    private AutoCompleteTextView PersonaRealizaInterno;
    private EditText Observaciones;
    private ArrayAdapter<String> adapter;
    private final Map<String, String> mapaPersonal = new HashMap<>();
    private final List<String> listaNombres = new ArrayList<>();
    double latitudeEstacion = 0, longitudeEstacion = 0, latitudeEquipo = 0, longitudeEquipo = 0;
    List<ResultadoValida> resultados = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mantenimiento_preventivo_revisar);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        CardInterno = findViewById(R.id.CardInterno);
        CardExterno = findViewById(R.id.CardExterno);

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
        estado = getIntent().getStringExtra("estado");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Mantenimiento Preventivo");
        }

        Observaciones = findViewById(R.id.Observaciones);
        Interno = findViewById(R.id.Interno);
        Externo = findViewById(R.id.Externo);

        LinearRealizaInterno = findViewById(R.id.LinearRealizaInterno);
        PersonaRealizaInterno = findViewById(R.id.PersonaRealizaInterno);

        LinearRealizaExterno = findViewById(R.id.LinearRealizaExterno);
        TxtPersonalExterno = findViewById(R.id.TxtPersonalExterno);
        ImageFirma = findViewById(R.id.ImageFirma);

        BtbFirma = findViewById(R.id.BtbFirma);
        LinearGuardar = findViewById(R.id.LinearGuardar);
        BtnGuardar = findViewById(R.id.BtnGuardar);
        TxtSublista1 = findViewById(R.id.TxtSublista1);
        TxtSublista2 = findViewById(R.id.TxtSublista2);
        TxtSublista3 = findViewById(R.id.TxtSublista3);
        TxtSublista4 = findViewById(R.id.TxtSublista4);
        TxtSublista5 = findViewById(R.id.TxtSublista5);

        switch (NumeroEquipo) {
            case "2":
                TxtSublista1.setVisibility(VISIBLE);
                TxtSublista1.setText("Registros");
                TxtSublista2.setVisibility(VISIBLE);
                TxtSublista2.setText("Boquillas");
                break;
            case "33":
                TxtSublista1.setVisibility(VISIBLE);
                TxtSublista1.setText("Anuncio estructural y faldón");
                TxtSublista3.setVisibility(VISIBLE);
                TxtSublista3.setText("Avisos verticales");
                TxtSublista4.setVisibility(VISIBLE);
                TxtSublista4.setText("Marcaje horizontal");
                break;
            case "42":
                TxtSublista1.setVisibility(VISIBLE);
                TxtSublista1.setText("Tinacos");
                TxtSublista5.setVisibility(VISIBLE);
                TxtSublista5.setText("Cisterna");
                break;
        }

        BtbFirma.setOnClickListener(v -> {
            DialogoFirma();
        });

        BtnGuardar.setOnClickListener(v -> {
            guardarMantenimiento(resultados);
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
            // Cambiar a gris (#757575)
            int gris = Color.parseColor("#F5F5F5");
            int gris2 = Color.parseColor("#757575");

            MaterialButton boton = (MaterialButton) BtnGuardar; // casteo explícito
            BtnGuardar.setTextColor(gris2);
            boton.setStrokeColor(ColorStateList.valueOf(gris)); // cambia borde
            BtnGuardar.setBackgroundTintList(ColorStateList.valueOf(gris)); // opcional si quieres también fondo gris

        }
        fechtMantenimiento();

    }

    public void fechtMantenimiento() {

        DialogHelper.showProgressDialog(this);
        String url = URL_SERVIDOR + "Mantenimiento/mantenimiento-preventivo-detalle.php?idMantenimiento=" + idMantenimiento;
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Datos generales del mantenimiento
                            int idmantenimiento = response.getInt("idmantenimiento");
                            int idEstacion = response.getInt("id_estacion");
                            int idEquipo = response.getInt("id_equipo");
                            String nomMantenimiento = response.getString("nom_mantenimiento");
                            String fechaCreacion = response.getString("fechacreacion");
                            String observaciones = response.getString("observaciones");

                            String IDFPR = response.getString("IDFPR");
                            String IDFPS = response.getString("IDFPS");
                            String FPR = response.getString("FPR");
                            String FPS = response.getString("FPS");
                            String PFPR = response.getString("PFPR");
                            String PFPS = response.getString("PFPS");

                            TextView TxtTitulo = findViewById(R.id.TxtTitulo);
                            TxtTitulo.setText(nomMantenimiento);

                            EditText EditObservaciones = findViewById(R.id.Observaciones);
                            EditObservaciones.setText(observaciones);

                            // Obtener el array de detalles
                            JSONArray detallesArray = response.getJSONArray("detalles");

                            // Declara el mapa al inicio de tu método o como atributo si es general
                            Map<Integer, Set<Integer>> editTextConfig = new HashMap<>();

                            editTextConfig.put(28, new HashSet<>(Arrays.asList(1,9,10,11,12,13)));
                            editTextConfig.put(29, new HashSet<>(Collections.singletonList(1)));
                            editTextConfig.put(30, new HashSet<>(Collections.singletonList(2)));
                            editTextConfig.put(32, new HashSet<>(Collections.singletonList(1)));
                            editTextConfig.put(33, new HashSet<>(Collections.singletonList(1)));
                            editTextConfig.put(34, new HashSet<>(Collections.singletonList(1)));
                            editTextConfig.put(35, new HashSet<>(Collections.singletonList(1)));
                            editTextConfig.put(36, new HashSet<>(Collections.singletonList(2)));
                            editTextConfig.put(38, new HashSet<>(Collections.singletonList(1)));
                            editTextConfig.put(39, new HashSet<>(Collections.singletonList(1)));
                            editTextConfig.put(45, new HashSet<>(Arrays.asList(1, 2, 4)));

                            for (int i = 0; i < detallesArray.length(); i++) {
                                JSONObject detalle = detallesArray.getJSONObject(i);

                                int idDetalle = detalle.getInt("iddetalle");
                                int numList = detalle.getInt("num_list");
                                String nomActividad = detalle.getString("nom_actividad");
                                String resultado = detalle.getString("resultado");

                                int Constraint = getResources().getIdentifier("Constraint" + numList, "id", getPackageName());
                                int txtId = getResources().getIdentifier("TxtVerificar" + numList, "id", getPackageName());
                                int siId = getResources().getIdentifier("Si_" + numList, "id", getPackageName());
                                int noId = getResources().getIdentifier("No_" + numList, "id", getPackageName());
                                int editId = getResources().getIdentifier("EditText" + numList, "id", getPackageName());
                                int textoInputId = getResources().getIdentifier("EditTextInfo" + numList, "id", getPackageName());

                                ConstraintLayout constraintLayout = findViewById(Constraint);
                                TextView txtVerificar = findViewById(txtId);
                                CheckBox siCheck = findViewById(siId);
                                CheckBox noCheck = findViewById(noId);
                                EditText editText = findViewById(editId);
                                TextInputLayout inputText = findViewById(textoInputId);

                                // Verifica si este idEquipo requiere EditText en este numList
                                boolean usaEditText = editTextConfig.containsKey(idEquipo) && editTextConfig.get(idEquipo).contains(numList);

                                if (usaEditText) {
                                    // Mostrar EditText
                                    constraintLayout.setVisibility(View.VISIBLE);
                                    txtVerificar.setVisibility(View.VISIBLE);
                                    editText.setVisibility(View.VISIBLE);
                                    inputText.setVisibility(View.VISIBLE);

                                    if (siCheck != null) siCheck.setVisibility(View.GONE);
                                    if (noCheck != null) noCheck.setVisibility(View.GONE);

                                    txtVerificar.setText(nomActividad);
                                    editText.setText(resultado);

                                    editText.addTextChangedListener(new TextWatcher() {
                                        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                                        @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
                                        @Override public void afterTextChanged(Editable s) {
                                            String textoIngresado = s.toString().trim();
                                            actualizarResultado(resultados, idDetalle, textoIngresado);
                                        }
                                    });
                                } else {
                                    // Mostrar CheckBox (lógica original)
                                    if (nomActividad.trim().isEmpty()) {
                                        constraintLayout.setVisibility(View.GONE);
                                    } else {
                                        constraintLayout.setVisibility(View.VISIBLE);
                                        txtVerificar.setVisibility(View.VISIBLE);
                                        txtVerificar.setText(nomActividad);

                                        if (siCheck != null && noCheck != null) {
                                            siCheck.setVisibility(View.VISIBLE);
                                            noCheck.setVisibility(View.VISIBLE);
                                        }

                                        if (editText != null) {
                                            editText.setVisibility(View.GONE);
                                            inputText.setVisibility(View.GONE);

                                        }

                                        if (resultado.isEmpty()) {
                                            siCheck.setChecked(false);
                                            noCheck.setChecked(false);
                                        } else {
                                            if (resultado.equals("Si")) {
                                                siCheck.setChecked(true);
                                                noCheck.setEnabled(false);
                                            } else {
                                                noCheck.setChecked(true);
                                                siCheck.setEnabled(false);
                                            }
                                        }

                                        siCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
                                            if (isChecked) {
                                                noCheck.setChecked(false);
                                                noCheck.setEnabled(false);
                                                actualizarResultado(resultados, idDetalle, "Si");
                                            } else {
                                                noCheck.setEnabled(true);
                                            }
                                        });

                                        noCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
                                            if (isChecked) {
                                                siCheck.setChecked(false);
                                                siCheck.setEnabled(false);
                                                actualizarResultado(resultados, idDetalle, "No");
                                            } else {
                                                siCheck.setEnabled(true);
                                            }
                                        });
                                    }
                                }
                            }

                            if(IDFPR.equals("0")){
                                Externo.setChecked(true);
                                Interno.setEnabled(false);

                                CardInterno.setVisibility(View.GONE);     // Mostrar tarjeta completa
                                CardExterno.setVisibility(View.VISIBLE);        // Ocultar si estaba activa

                                LinearRealizaExterno.setVisibility(VISIBLE);

                                ((ViewGroup) LinearGuardar.getParent()).removeView(LinearGuardar);
                                LinearRealizaExterno.addView(LinearGuardar);
                                LinearGuardar.setVisibility(View.VISIBLE);
                                limpiarAutoComplete();

                                BtbFirma.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#82F5A5")));

                                ImageFirma.setVisibility(VISIBLE);
                                if(IDFPR.equals("0")){
                                    Glide.with(getApplicationContext()).load(URL_SERVIDOR + "Mantenimiento/ImagenFirma/" + FPR).into(ImageFirma);
                                }else{
                                    Glide.with(getApplicationContext()).load(URL_HOST + "imgs/firma-personal/" + FPR).into(ImageFirma);
                                }

                                TxtPersonalExterno.setVisibility(VISIBLE);
                                TxtPersonalExterno.setText(PFPR);

                                idSeleccionado = IDFPR;
                                valExterno = PFPR;
                                nombreImagen = FPR;

                            }else{

                                if(IDFPR.isEmpty()){
                                    Interno.setChecked(false);
                                    Externo.setChecked(false);

                                    CardInterno.setVisibility(View.GONE);     // Mostrar tarjeta completa
                                    CardExterno.setVisibility(View.GONE);        // Ocultar si estaba activa

                                }else{
                                    Interno.setChecked(true);
                                    Externo.setEnabled(false);

                                    CardInterno.setVisibility(View.VISIBLE);     // Mostrar tarjeta completa
                                    CardExterno.setVisibility(View.GONE);        // Ocultar si estaba activa

                                    ((ViewGroup) LinearGuardar.getParent()).removeView(LinearGuardar);
                                    LinearRealizaInterno.addView(LinearGuardar);
                                    LinearGuardar.setVisibility(View.VISIBLE);

                                    ListaPersonal();
                                }

                                PersonaRealizaInterno.setText(PFPR);
                                idSeleccionado = IDFPR;
                            }

                            DialogHelper.hideProgressDialog();

                        } catch (JSONException e) {
                            DialogHelper.hideProgressDialog();
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        DialogHelper.hideProgressDialog();
                        error.printStackTrace();
                    }
                });

        requestQueue.add(jsonObjectRequest);
    }

    private void actualizarResultado(List<ResultadoValida> lista, int iddetalle, String resultado) {
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getIddetalle() == iddetalle) {
                lista.set(i, new ResultadoValida(iddetalle, resultado));
                return;
            }
        }
        lista.add(new ResultadoValida(iddetalle, resultado));
    }

    public void CheckboxClicked(View view) {
        boolean checked = ((CheckBox) view).isChecked();
        int viewId = view.getId();

        if (viewId == R.id.Interno) {
            if (checked) {
                Externo.setEnabled(false);

                CardInterno.setVisibility(View.VISIBLE);     // Mostrar tarjeta Interno
                CardExterno.setVisibility(View.GONE);        // Ocultar tarjeta Externo

                LinearRealizaInterno.setVisibility(View.VISIBLE);
                LinearRealizaExterno.setVisibility(View.GONE);
                ListaPersonal();

                // Mueve el botón al contenedor Interno
                ((ViewGroup) LinearGuardar.getParent()).removeView(LinearGuardar);
                LinearRealizaInterno.addView(LinearGuardar);
                LinearGuardar.setVisibility(View.VISIBLE);


            } else {
                Externo.setEnabled(true);
                CardInterno.setVisibility(View.GONE);
                CardExterno.setVisibility(View.GONE);    // Asegura ocultar Externo
                LinearRealizaInterno.setVisibility(View.GONE);
                LinearRealizaExterno.setVisibility(View.GONE);
                LinearGuardar.setVisibility(View.GONE);
            }
        } else if (viewId == R.id.Externo) {
            if (checked) {
                Interno.setEnabled(false);

                CardExterno.setVisibility(View.VISIBLE);     // Mostrar tarjeta Externo
                CardInterno.setVisibility(View.GONE);        // Ocultar tarjeta Interno

                LinearRealizaExterno.setVisibility(View.VISIBLE);
                LinearRealizaInterno.setVisibility(View.GONE);
                limpiarAutoComplete();

                // Mueve el botón al contenedor Externo
                ((ViewGroup) LinearGuardar.getParent()).removeView(LinearGuardar);
                LinearRealizaExterno.addView(LinearGuardar);
                LinearGuardar.setVisibility(View.VISIBLE);
            } else {
                Interno.setEnabled(true);
                CardExterno.setVisibility(View.GONE);
                CardInterno.setVisibility(View.GONE);    // Asegura ocultar Interno
                LinearRealizaInterno.setVisibility(View.GONE);
                LinearRealizaExterno.setVisibility(View.GONE);
                LinearGuardar.setVisibility(View.GONE);
            }
        }
    }

    private void limpiarAutoComplete() {
        idSeleccionado = "0";
        PersonaRealizaInterno.setText("");
        PersonaRealizaInterno.clearFocus();
        PersonaRealizaInterno.dismissDropDown();
    }

    private void ListaPersonal() {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                URL_SERVIDOR + "Autorizacion/personal-autorizado.php",
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

                            adapter = new ArrayAdapter<>(MantenimientoPreventivoRevisar.this,
                                    android.R.layout.simple_dropdown_item_1line, listaNombres);
                            PersonaRealizaInterno.setAdapter(adapter);
                            PersonaRealizaInterno.setThreshold(1); // muestra sugerencias desde 1 letra

                            PersonaRealizaInterno.setOnItemClickListener((parent, view, position, id) -> {
                                String nombreSeleccionado = adapter.getItem(position);
                                idSeleccionado = mapaPersonal.getOrDefault(nombreSeleccionado, "0");

                            });

                            // Si se borra el texto o se edita manualmente, restablecer idSeleccionado a "0"
                            PersonaRealizaInterno.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {
                                    // Verifica si el texto ingresado está en la lista
                                    String textoActual = s.toString();
                                    if (!mapaPersonal.containsKey(textoActual)) {
                                        idSeleccionado = "0";
                                    }
                                }

                                @Override
                                public void afterTextChanged(Editable s) {}
                            });


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, error -> {
            error.printStackTrace();
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("idEstacion", String.valueOf(idEstacion));
                params.put("Categoria", "MPC");
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }

    private void DialogoFirma() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_firma, null);
        builder.setView(dialogView);

        EditText PersonaRealizaExterno = dialogView.findViewById(R.id.et_nombre);
        SignatureView signatureView = dialogView.findViewById(R.id.signature_view);
        Button btnLimpiar = dialogView.findViewById(R.id.btn_limpiar);
        Button btnGuardar = dialogView.findViewById(R.id.btn_guardar);
        ImageButton btnSalir = dialogView.findViewById(R.id.BtnSalir);

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();

        btnSalir.setOnClickListener(v -> dialog.dismiss());

        btnLimpiar.setOnClickListener(v -> signatureView.clear());

        btnGuardar.setOnClickListener(v -> {
            String nombre = PersonaRealizaExterno.getText().toString().trim();

            if (nombre.isEmpty()) {
                PersonaRealizaExterno.setError("Ingresa el nombre del trabajador");
                ToastUtils.show(this, "Ingresa el nombre del trabajador", ToastUtils.INFO);
                return;
            }

            if (signatureView.isEmpty()) {
                ToastUtils.show(this, "Dibuja una firma antes de guardar", ToastUtils.INFO);
                return;
            }

            DialogHelper.showProgressDialog(this);
            valExterno = PersonaRealizaExterno.getText().toString();
            Bitmap firmaBitmap = signatureView.getSignatureBitmap();
            idSeleccionado = "0";
            // Envía al servidor
            enviarFirma(firmaBitmap);

            dialog.dismiss();
        });
    }

    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap = Bitmap.createScaledBitmap(bitmap, 400, 250, true);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] imagenBytes = baos.toByteArray();
        return Base64.encodeToString(imagenBytes, Base64.DEFAULT);
    }

    private void enviarFirma(Bitmap firmaBitmap) {

        firmaBase64 = bitmapToBase64(firmaBitmap);
        String url = URL_SERVIDOR + "Mantenimiento/agregar-imagen-descarga.php";
        nombreImagen = ImageNameGenerator.generarNombreConExtension(".png");

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {

                    BtbFirma.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#82F5A5")));
                    TxtPersonalExterno.setVisibility(VISIBLE);
                    TxtPersonalExterno.setText(valExterno);

                    ImageFirma.setVisibility(VISIBLE);
                    ImageFirma.setImageBitmap(firmaBitmap);

                    Log.d("TAG", "onResponse: " + firmaBitmap);

                    ToastUtils.show(this, "Firma agregada", ToastUtils.SUCCESS);
                    DialogHelper.hideProgressDialog();
                },
                error -> ToastUtils.show(this, "Error al enviar firma", ToastUtils.ERROR)) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("firma", firmaBase64);
                params.put("nombre", nombreImagen);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    private void guardarMantenimiento(List<ResultadoValida> resultados){

        String observaciones = Observaciones.getText().toString().trim();

        String url = URL_SERVIDOR + "Mantenimiento/mantenimiento-verificar-dos.php";
        String personal = "";

        if (Observaciones.getText().toString().trim().isEmpty()) {
            Observaciones.setError("Ingresa las observaciones");
            ToastUtils.show(this, "Ingresa las observaciones", ToastUtils.INFO);
            return;
        }

        if(Interno.isChecked()){
            personal = PersonaRealizaInterno.getText().toString();
        }else if(Externo.isChecked()){

            if (valExterno == null || valExterno.isEmpty()) {
                ToastUtils.show(this, "Falta agregar la firma.", ToastUtils.INFO);
                return;
            }

            personal = valExterno;
        }

        String finalPersonal = personal;
        DialogHelper.showProgressDialog(this);

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {


                    ToastUtils.showAndThen(MantenimientoPreventivoRevisar.this, "Mantenimiento Finalizado", ToastUtils.SUCCESS, () -> {
                        Intent home = new Intent(getApplicationContext(), Evidencias.class);
                        home.putExtra("id", idMantenimiento);
                        home.putExtra("categoria", "MantenimientoPreventivo");
                        home.putExtra("carpeta", "Mantenimiento");
                        home.putExtra("titulo", "Evidencia Mantenimiento Preventivo");
                        startActivityForResult(home, 1);
                        setResult(RESULT_OK);
                        finish();
                    });

                },
                error -> {
                    ToastUtils.show(this, "Error al enviar datos", ToastUtils.ERROR);
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                for (int i = 0; i < resultados.size(); i++) {
                    ResultadoValida r = resultados.get(i);
                    params.put("iddetalle_" + i, String.valueOf(r.getIddetalle()));
                    params.put("resultado_" + i, r.getResultado());
                }

                params.put("total", String.valueOf(resultados.size()));
                params.put("idMantenimiento", idMantenimiento);
                params.put("Observaciones", observaciones);
                params.put("idPersonalSupervisa", String.valueOf(IdUsuario));

                params.put("idPersonalRealiza", idSeleccionado);
                params.put("PersonalRealiza", finalPersonal);
                params.put("imagenFirma", nombreImagen);
                params.put("estado", estado);


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