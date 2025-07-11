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
import com.gestogas.gestoline.utils.DialogHelper;
import com.gestogas.gestoline.utils.DistanciaUtils;
import com.gestogas.gestoline.utils.ImageNameGenerator;
import com.gestogas.gestoline.utils.ResultadoValida;
import com.gestogas.gestoline.utils.SignatureView;
import com.gestogas.gestoline.utils.TecladoUtils;
import com.gestogas.gestoline.utils.ToastUtils;

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

public class MantenimientoPreventivoDetectorHumo extends AppCompatActivity {

    int idEstacion, IdUsuario, idPuesto, distMax = 0, Sigpagina, Antpagina, iddetector;
    String idMantenimiento, NumeroEquipo, NombreEquipo, estado, valExterno = "", NumeroPagina;
    TextView NuDetector, Ubicacion, TxtVerificar1, TxtVerificar2, TxtVerificar3, TxtVerificar4, TxtPersonal;
    String Verificar1, Verificar2, Verificar3, Verificar4, ResultadoCheck1 = "", ResultadoCheck2 = "", ResultadoCheck3 = "", ResultadoCheck4 = "", firmaBase64, nombreImagen = "";
    CheckBox Interno,Externo;
    TextView TxtPersonalExterno;
    ImageView ImageFirma;
    LinearLayout LinearRealizaInterno, LinearRealizaExterno, LinearGuardar;
    Button BtbFirma, BtnGuardar,BtnSiguiente, BtnAnterior;
    String idSeleccionado = "0";
    private AutoCompleteTextView PersonaRealizaInterno;
    CheckBox Si_1, No_1,
            Si_2, No_2,
            Si_3, No_3,
            Si_4, No_4;
    private EditText Observaciones;
    private ArrayAdapter<String> adapter;
    private final Map<String, String> mapaPersonal = new HashMap<>();
    private final List<String> listaNombres = new ArrayList<>();
    double latitudeEstacion = 0, longitudeEstacion = 0, latitudeEquipo = 0, longitudeEquipo = 0;
    List<ResultadoValida> resultados = new ArrayList<>();

    ConstraintLayout LayoutRealiza;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mantenimiento_preventivo_detector_humo);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

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
        NumeroPagina = getIntent().getStringExtra("NumeroPagina");

        Sigpagina = Integer.parseInt(NumeroPagina) + 1;
        Antpagina = Integer.parseInt(NumeroPagina) - 1;

        NuDetector = findViewById(R.id.NuDetector);
        Ubicacion = findViewById(R.id.Ubicacion);

        TxtPersonal = findViewById(R.id.TxtPersonal);
        TxtVerificar1 = findViewById(R.id.TxtVerificar1);
        TxtVerificar2 = findViewById(R.id.TxtVerificar2);
        TxtVerificar3 = findViewById(R.id.TxtVerificar3);
        TxtVerificar4 = findViewById(R.id.TxtVerificar4);
        Si_1 = findViewById(R.id.Si_1);
        No_1 = findViewById(R.id.No_1);
        Si_2 = findViewById(R.id.Si_2);
        No_2 = findViewById(R.id.No_2);
        Si_3 = findViewById(R.id.Si_3);
        No_3 = findViewById(R.id.No_3);
        Si_4 = findViewById(R.id.Si_4);
        No_4 = findViewById(R.id.No_4);

        Observaciones = findViewById(R.id.Observaciones);
        LayoutRealiza = findViewById(R.id.LayoutRealiza);

        BtnAnterior = findViewById(R.id.BtnAnterior);
        BtnSiguiente = findViewById(R.id.BtnSiguiente);

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

        Verificar1 = "Revisión auditiva (Emisión de sonido)";
        Verificar2 = "Revisión visual (Emisión de luz indicadora)";
        Verificar3 = "¿El funcionamiento del detector es el óptimo?";
        Verificar4 = "¿Requiere cambio de batería?";
        TxtVerificar1.setText(Verificar1);
        TxtVerificar2.setText(Verificar2);
        TxtVerificar3.setText(Verificar3);
        TxtVerificar4.setText(Verificar4);

        BtbFirma.setOnClickListener(v -> {
            DialogoFirma();
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

        BtnAnterior.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent mantenimiento = new Intent(getApplicationContext(), MantenimientoPreventivoDetectorHumo.class);
                mantenimiento.putExtra("idMantenimiento",idMantenimiento);
                mantenimiento.putExtra("NumeroEquipo",NumeroEquipo);
                mantenimiento.putExtra("NombreEquipo",NombreEquipo);
                mantenimiento.putExtra("NumeroPagina",String.valueOf(Antpagina));
                mantenimiento.putExtra("estado",estado);
                startActivity(mantenimiento);
                finish();


            }
        });

        BtnSiguiente.setOnClickListener(v->{
            ValidaForma1();
        });

        BtnGuardar.setOnClickListener(v->{
            ValidaForma2();
        });


        if (validarDistancia) {
            BtnGuardar.setEnabled(true);
            layoutFueraRango.setVisibility(View.GONE);
        } else {
            BtnGuardar.setEnabled(false);
            layoutFueraRango.setVisibility(VISIBLE);
            BtnGuardar.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.color_inactivo));
        }

           fechtMantenimiento();

    }

    public void fechtMantenimiento() {

        DialogHelper.showProgressDialog(this);
        String url = URL_SERVIDOR + "Mantenimiento/detalle-detector-humo.php?idEstacion=" + idEstacion + "&idMantenimiento=" + idMantenimiento + "&NumeroPagina=" + NumeroPagina;
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        JsonArrayRequest JsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onResponse(JSONArray response) {

                        Log.d("TAG", "onResponse: " + response.toString());

                        String  nodetector = "",
                                ubicacion = "",
                                resultado1 = "",
                                resultado2 = "",
                                resultado3 = "",
                                resultado4 = "",
                                IDFPR = "",
                                IDFPS = "",
                                FPR = "",
                                FPS = "",
                                PFPR = "",
                                PFPS = "";
                        int anterior = 0, totaldetector = 0;

                        try {

                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jsonObject = response.getJSONObject(i);
                                iddetector = jsonObject.getInt("iddetector");
                                nodetector = jsonObject.getString("nodetector");
                                ubicacion = jsonObject.getString("ubicacion");
                                resultado1 = jsonObject.getString("resultado1");
                                resultado2 = jsonObject.getString("resultado2");
                                resultado3 = jsonObject.getString("resultado3");
                                resultado4 = jsonObject.getString("resultado4");
                                anterior = jsonObject.getInt("anterior");
                                totaldetector = jsonObject.getInt("totaldetector");

                                IDFPR = jsonObject.getString("IDFPR");
                                IDFPS = jsonObject.getString("IDFPS");
                                FPR = jsonObject.getString("FPR");
                                FPS = jsonObject.getString("FPS");
                                PFPR = jsonObject.getString("PFPR");
                                PFPS = jsonObject.getString("PFPS");
                            }

                            NuDetector.setText(nodetector);
                            Ubicacion.setText(ubicacion);

                            if (resultado1.isEmpty()) {
                                Si_1.setChecked(false);
                                No_1.setChecked(false);
                            }else{
                                if (resultado1.equals("Si")) {
                                    Si_1.setChecked(true);
                                    No_1.setEnabled(false);
                                }else {
                                    Si_1.setEnabled(false);
                                    No_1.setChecked(true);
                                }
                            }

                            if (resultado2.isEmpty()) {
                                Si_2.setChecked(false);
                                No_2.setChecked(false);
                            }else{
                                if (resultado2.equals("Si")) {
                                    Si_2.setChecked(true);
                                    No_2.setEnabled(false);
                                }else{
                                    Si_2.setEnabled(false);
                                    No_2.setChecked(true);
                                }
                            }

                            if (resultado3.isEmpty()) {
                                Si_3.setChecked(false);
                                No_3.setChecked(false);
                            }else{
                                if (resultado3.equals("Si")) {
                                    Si_3.setChecked(true);
                                    No_3.setEnabled(false);
                                }else{
                                    Si_3.setEnabled(false);
                                    No_3.setChecked(true);
                                }
                            }

                            if (resultado4.isEmpty()) {
                                Si_4.setChecked(false);
                                No_4.setChecked(false);
                            }else{
                                if (resultado4.equals("Si")) {
                                    Si_4.setChecked(true);
                                    No_4.setEnabled(false);
                                }else{
                                    Si_4.setEnabled(false);
                                    No_4.setChecked(true);
                                }
                            }

                            if (anterior != 0){
                                BtnAnterior.setVisibility(View.VISIBLE);
                            }

                            if (totaldetector != Integer.parseInt(NumeroPagina)){
                                BtnSiguiente.setVisibility(View.VISIBLE);
                            }else if(totaldetector == Integer.parseInt(NumeroPagina)){

                                BtnSiguiente.setVisibility(View.GONE);
                                Observaciones.setVisibility(View.VISIBLE);
                                TxtPersonal.setVisibility(View.VISIBLE);
                                LayoutRealiza.setVisibility(View.VISIBLE);

                                if(IDFPR.equals("0")){
                                    Externo.setChecked(true);
                                    Interno.setEnabled(false);
                                    LinearRealizaExterno.setVisibility(VISIBLE);
                                    LinearGuardar.setVisibility(VISIBLE);
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
                                    }else{
                                        Interno.setChecked(true);
                                        Externo.setEnabled(false);

                                        LinearRealizaInterno.setVisibility(VISIBLE);
                                        LinearGuardar.setVisibility(VISIBLE);
                                        ListaPersonal();
                                    }

                                    PersonaRealizaInterno.setText(PFPR);
                                    idSeleccionado = IDFPR;
                                }
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

        requestQueue.add(JsonArrayRequest);
    }


    public void CheckboxClicked(View view) {
        boolean checked = ((CheckBox) view).isChecked();
        int viewId = view.getId(); // Get the ID once
        if(viewId == R.id.Si_1){
            No_1.setEnabled(!checked);
        }else if(viewId == R.id.No_1){
            Si_1.setEnabled(!checked);
        }else if(viewId == R.id.Si_2){
            No_2.setEnabled(!checked);
        }else if(viewId == R.id.No_2){
            Si_2.setEnabled(!checked);
        }else if(viewId == R.id.Si_3){
            No_3.setEnabled(!checked);
        }else if(viewId == R.id.No_3){
            Si_3.setEnabled(!checked);
        }else if(viewId == R.id.Si_4){
            No_4.setEnabled(!checked);
        }else if(viewId == R.id.No_4){
            Si_4.setEnabled(!checked);
        }else if (viewId == R.id.Interno) {
            if (checked) {
                Externo.setEnabled(false);
                LinearRealizaInterno.setVisibility(VISIBLE);
                LinearGuardar.setVisibility(VISIBLE);
                ListaPersonal();
            }else{
                Externo.setEnabled(true);
                LinearRealizaInterno.setVisibility(View.GONE);
                LinearGuardar.setVisibility(View.GONE);
                limpiarAutoComplete();
            }
        } else if (viewId == R.id.Externo) {
            if (checked) {
                Interno.setEnabled(false);
                LinearRealizaExterno.setVisibility(VISIBLE);
                LinearGuardar.setVisibility(VISIBLE);
                limpiarAutoComplete();
            }else{
                Interno.setEnabled(true);
                LinearRealizaExterno.setVisibility(View.GONE);
                LinearGuardar.setVisibility(View.GONE);
                limpiarAutoComplete();
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

                            adapter = new ArrayAdapter<>(MantenimientoPreventivoDetectorHumo.this,
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



    public void ValidaForma1(){

        if (Si_1.isChecked() && !No_1.isChecked()) {
            ResultadoCheck1 = "Si";
        } else if (!Si_1.isChecked() && No_1.isChecked()) {
            ResultadoCheck1 = "No";
        }

        if (Si_2.isChecked() && !No_2.isChecked()) {
            ResultadoCheck2 = "Si";
        } else if (!Si_2.isChecked() && No_2.isChecked()) {
            ResultadoCheck2 = "No";
        }

        if (Si_3.isChecked() && !No_3.isChecked()) {
            ResultadoCheck3 = "Si";
        } else if (!Si_3.isChecked() && No_3.isChecked()) {
            ResultadoCheck3 = "No";
        }

        if (Si_4.isChecked() && !No_4.isChecked()) {
            ResultadoCheck4 = "Si";
        } else if (!Si_4.isChecked() && No_4.isChecked()) {
            ResultadoCheck4 = "No";
        }

        DialogHelper.showProgressDialog(this);
        AgregarInformacion();


    }

    public void ValidaForma2(){

        if (Si_1.isChecked() && !No_1.isChecked()) {
            ResultadoCheck1 = "Si";
        } else if (!Si_1.isChecked() && No_1.isChecked()) {
            ResultadoCheck1 = "No";
        }

        if (Si_2.isChecked() && !No_2.isChecked()) {
            ResultadoCheck2 = "Si";
        } else if (!Si_2.isChecked() && No_2.isChecked()) {
            ResultadoCheck2 = "No";
        }

        if (Si_3.isChecked() && !No_3.isChecked()) {
            ResultadoCheck3 = "Si";
        } else if (!Si_3.isChecked() && No_3.isChecked()) {
            ResultadoCheck3 = "No";
        }

        if (Si_4.isChecked() && !No_4.isChecked()) {
            ResultadoCheck4 = "Si";
        } else if (!Si_4.isChecked() && No_4.isChecked()) {
            ResultadoCheck4 = "No";
        }

        DialogHelper.showProgressDialog(this);
        Finalizarmantenimiento();

    }

    public void AgregarInformacion(){

        final String url = URL_SERVIDOR + "Mantenimiento/mantenimiento-detector-humo-actualizar.php";
        DialogHelper.showProgressDialog(this);

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {

                    Intent mantenimiento = new Intent(getApplicationContext(), MantenimientoPreventivoDetectorHumo.class);
                    mantenimiento.putExtra("idMantenimiento",idMantenimiento);
                    mantenimiento.putExtra("NumeroEquipo",NumeroEquipo);
                    mantenimiento.putExtra("NombreEquipo",NombreEquipo);
                    mantenimiento.putExtra("NumeroPagina",String.valueOf(Sigpagina));
                    mantenimiento.putExtra("estado",estado);
                    startActivityForResult(mantenimiento,1);
                    setResult(RESULT_OK);
                    finish();

                },
                error -> {
                    DialogHelper.hideProgressDialog();
                    ToastUtils.show(this, "Error al enviar datos", ToastUtils.ERROR);
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("iddetector", String.valueOf(iddetector));
                params.put("idMantenimiento", idMantenimiento);
                params.put("Verificar1", Verificar1);
                params.put("ResultadoCheck1", ResultadoCheck1);
                params.put("Verificar2", Verificar2);
                params.put("ResultadoCheck2", ResultadoCheck2);
                params.put("Verificar3", Verificar3);
                params.put("ResultadoCheck3", ResultadoCheck3);
                params.put("Verificar4", Verificar4);
                params.put("ResultadoCheck4", ResultadoCheck4);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);

    }

    public void Finalizarmantenimiento(){

        final String observaciones = Observaciones.getText().toString().trim();
        String personal = "";

        final String url = URL_SERVIDOR + "Mantenimiento/mantenimiento-detector-humo-finalizar.php";

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

                    ToastUtils.showAndThen(MantenimientoPreventivoDetectorHumo.this, "Mantenimiento Finalizado", ToastUtils.SUCCESS, () -> {
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
                params.put("iddetector", String.valueOf(iddetector));
                params.put("idMantenimiento", idMantenimiento);
                params.put("Verificar1", Verificar1);
                params.put("ResultadoCheck1", ResultadoCheck1);
                params.put("Verificar2", Verificar2);
                params.put("ResultadoCheck2", ResultadoCheck2);
                params.put("Verificar3", Verificar3);
                params.put("ResultadoCheck3", ResultadoCheck3);
                params.put("Verificar4", Verificar4);
                params.put("ResultadoCheck4", ResultadoCheck4);

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