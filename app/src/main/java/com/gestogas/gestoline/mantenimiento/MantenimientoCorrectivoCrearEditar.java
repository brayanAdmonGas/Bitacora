package com.gestogas.gestoline.mantenimiento;

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

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
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
import com.gestogas.gestoline.utils.SignatureView;
import com.gestogas.gestoline.utils.TecladoUtils;
import com.gestogas.gestoline.utils.ToastUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MantenimientoCorrectivoCrearEditar extends BaseActivity {
    int idEstacion, IdUsuario, idPuesto, distMax = 0;
    String idMantenimiento, estado, url, firmaBase64 = "", nombreImagen = "", PFPR = "", valExterno = "";
    EditText NombreEquipo, DescripcionMantenimiento, DescripcionActividad,Herramienta, PersonaRealizaExterno;
    CheckBox Interno,Externo;
    TextView TxtPersonalExterno;
    ImageView ImageFirma;
    LinearLayout LinearRealizaInterno, LinearRealizaExterno, LinearGuardar;
    Button BtbFirma, BtnGuardar;

    String idSeleccionado = "0";
    private AutoCompleteTextView PersonaRealizaInterno;
    private ArrayAdapter<String> adapter;
    private final Map<String, String> mapaPersonal = new HashMap<>();
    private final List<String> listaNombres = new ArrayList<>();

    double latitudeEstacion = 0, longitudeEstacion = 0, latitudeEquipo = 0, longitudeEquipo = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mantenimiento_correctivo_crear_editar);

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
        String titulo = getIntent().getStringExtra("titulo");
        String tituloboton = getIntent().getStringExtra("tituloboton");
        estado = getIntent().getStringExtra("estado");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(titulo);
        }

        NombreEquipo = findViewById(R.id.NombreEquipo);
        DescripcionMantenimiento = findViewById(R.id.DescripcionMantenimiento);
        DescripcionActividad = findViewById(R.id.DescripcionActividad);
        Herramienta = findViewById(R.id.Herramienta);
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
        BtnGuardar.setText(tituloboton);

        BtbFirma.setOnClickListener(v -> {
            DialogoFirma();
        });

        BtnGuardar.setOnClickListener(v -> {
            validarCampos();
        });

        if(estado.equals("Editar")){
            fetchMantenimientoCorrectivo();
        }

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
            BtnGuardar.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.color_inactivo));
        }

    }

    private void fetchMantenimientoCorrectivo() {

        DialogHelper.showProgressDialog(this);
        String url = URL_SERVIDOR + "Mantenimiento/detalle-mantenimiento-correctivo.php?idMantenimiento=" + idMantenimiento;
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onResponse(JSONArray response) {
                        String folio = "", equipo = "", dhallazgo = "", dactividad = "", herramienta = "", fecha = "",
                                hora = "", FPR = "", FPS = "", PFPS = "",
                                IDFPR = "", IDFPS = "";

                        try {

                            // Recorre el JSONArray
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jsonObject = response.getJSONObject(i);
                                folio = jsonObject.getString("folio");
                                equipo = jsonObject.getString("equipo");
                                dhallazgo = jsonObject.getString("dhallazgo");
                                dactividad = jsonObject.getString("dactividad");
                                herramienta = jsonObject.getString("herramienta");
                                fecha = jsonObject.getString("fecha");
                                hora = jsonObject.getString("hora");
                                IDFPR = jsonObject.getString("IDFPR");
                                IDFPS = jsonObject.getString("IDFPS");
                                FPR = jsonObject.getString("FPR");
                                FPS = jsonObject.getString("FPS");
                                PFPR = jsonObject.getString("PFPR");
                                PFPS = jsonObject.getString("PFPS");

                            }

                            NombreEquipo.setText(equipo);
                            DescripcionMantenimiento.setText(dhallazgo);
                            DescripcionActividad.setText(dactividad);
                            Herramienta.setText(herramienta);

                            if(IDFPR.equals("0")){

                                Externo.setChecked(true);
                                Interno.setEnabled(false);
                                LinearRealizaExterno.setVisibility(View.VISIBLE);
                                LinearGuardar.setVisibility(View.VISIBLE);
                                limpiarAutoComplete();

                                BtbFirma.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#82F5A5")));
                                ImageFirma.setVisibility(View.VISIBLE);
                                if(IDFPR.equals("0")){
                                    Glide.with(getApplicationContext()).load(URL_SERVIDOR + "Mantenimiento/ImagenFirma/" + FPR).into(ImageFirma);
                                }else{
                                    Glide.with(getApplicationContext()).load(URL_HOST + "imgs/firma-personal/" + FPR).into(ImageFirma);
                                }

                                TxtPersonalExterno.setVisibility(View.VISIBLE);
                                TxtPersonalExterno.setText(PFPR);

                                valExterno = PFPR;
                                idSeleccionado = IDFPR;
                                nombreImagen = FPR;

                            }else{
                                Interno.setChecked(true);
                                Externo.setEnabled(false);
                                LinearRealizaInterno.setVisibility(View.VISIBLE);
                                LinearGuardar.setVisibility(View.VISIBLE);
                                ListaPersonal();

                                PersonaRealizaInterno.setText(PFPR);
                                idSeleccionado = IDFPR;
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

    private void DialogoFirma() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_firma, null);
        builder.setView(dialogView);

        PersonaRealizaExterno = dialogView.findViewById(R.id.et_nombre);
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
                    TxtPersonalExterno.setVisibility(View.VISIBLE);
                    valExterno = PersonaRealizaExterno.getText().toString();
                    TxtPersonalExterno.setText(PersonaRealizaExterno.getText().toString());
                    ImageFirma.setVisibility(View.VISIBLE);
                    ImageFirma.setImageBitmap(firmaBitmap);

                    ToastUtils.show(this, "Firma agregada", ToastUtils.SUCCESS);
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

    public void validarCampos(){

       String nombreEquipo = NombreEquipo.getText().toString();
       String descripcionMantenimiento = DescripcionMantenimiento.getText().toString();
       String descripcionActividad = DescripcionActividad.getText().toString();
       String herramienta = Herramienta.getText().toString();
       String personal = "";

        if(estado.equals("Nuevo")){

            if(Interno.isChecked()){
                personal = PersonaRealizaInterno.getText().toString();
            }else if(Externo.isChecked()){

                if (valExterno == null || valExterno.isEmpty()) {
                    ToastUtils.show(this, "Falta agregar la firma.", ToastUtils.INFO);
                    return;
                }

                personal = valExterno;
            }

        }else if(estado.equals("Editar")){
            personal =  valExterno;
        }

        url = (estado.equals("Nuevo"))?  "Mantenimiento/agregar-mantenimiento-correctivo.php" :  "Mantenimiento/editar-mantenimiento-correctivo.php";
        String finalPersonal = personal;

        DialogHelper.showProgressDialog(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_SERVIDOR + url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {

                            JSONArray jsonarray = new JSONArray(response);
                            int estado = 0;
                            String id, mensaje = "";

                            if (jsonarray.length() > 0) {
                                JSONObject obj = jsonarray.getJSONObject(0);
                                id = obj.getString("IdReporte");
                                estado = obj.getInt("estado");
                                mensaje = obj.getString("mensaje");
                            } else {
                                id = "";
                            }

                            if (estado == 1){
                                DialogHelper.hideProgressDialog();
                                ToastUtils.showAndThen(MantenimientoCorrectivoCrearEditar.this, mensaje, ToastUtils.SUCCESS, () -> {
                                    Intent home = new Intent(getApplicationContext(), Evidencias.class);
                                    home.putExtra("id", id);
                                    home.putExtra("categoria", "MantenimientoCorrectivo");
                                    home.putExtra("carpeta", "Mantenimiento");
                                    home.putExtra("titulo", "Evidencia Mantenimiento Correctivo");
                                    startActivityForResult(home, 1);
                                    setResult(RESULT_OK);
                                    finish();
                                });

                            }else{
                                ToastUtils.show(MantenimientoCorrectivoCrearEditar.this, mensaje, ToastUtils.ERROR);
                                DialogHelper.hideProgressDialog();
                            }


                        }catch (Exception e){
                            ToastUtils.show(MantenimientoCorrectivoCrearEditar.this, "Error: " + e.toString(), ToastUtils.INFO);
                            DialogHelper.hideProgressDialog();
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ToastUtils.show(MantenimientoCorrectivoCrearEditar.this, "Error: " + error.toString(), ToastUtils.INFO);
                        DialogHelper.hideProgressDialog();
                    }
                }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("idMantenimiento", idMantenimiento);
                params.put("idEstacion", String.valueOf(idEstacion));
                params.put("IdUsuario", String.valueOf(IdUsuario));
                params.put("hallazgo", descripcionMantenimiento);
                params.put("nombreequipo", nombreEquipo);
                params.put("actividad", descripcionActividad);
                params.put("herramientaut", herramienta);
                params.put("idPersonalRealiza", idSeleccionado);
                params.put("PersonalRealiza", finalPersonal);
                params.put("imagenFirma", nombreImagen);
                return params;

            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);


    }

    public void CheckboxClicked(View view) {
        boolean checked = ((CheckBox) view).isChecked();
        int viewId = view.getId(); // Get the ID once

        if (viewId == R.id.Interno) {
            if (checked) {
                Externo.setEnabled(false);
                LinearRealizaInterno.setVisibility(View.VISIBLE);
                LinearGuardar.setVisibility(View.VISIBLE);
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
                LinearRealizaExterno.setVisibility(View.VISIBLE);
                LinearGuardar.setVisibility(View.VISIBLE);
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

                            adapter = new ArrayAdapter<>(MantenimientoCorrectivoCrearEditar.this,
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