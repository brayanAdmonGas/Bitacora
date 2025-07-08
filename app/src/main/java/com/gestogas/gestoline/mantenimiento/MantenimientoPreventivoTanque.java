package com.gestogas.gestoline.mantenimiento;

import static android.view.View.GONE;
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
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MantenimientoPreventivoTanque extends AppCompatActivity {
    int idEstacion, IdUsuario, idPuesto, distMax = 0;
    String idMantenimiento, NumeroEquipo, NombreEquipo, NumeroPagina, estado, idSeleccionado = "0", valExterno = "";
    String firmaBase64, nombreImagen = "";
    double latitudeEstacion = 0, longitudeEstacion = 0, latitudeEquipo = 0, longitudeEquipo = 0;
    TextView TxtTitulo, TxtPersonalExterno;
    ImageView ImageFirma;
    FrameLayout FLTipoBase, FLTipoSi, FLTipoNo;
    CheckBox Si_1, No_1,
            Si_2, No_2,
            Si_3, No_3,
            Si_4, No_4,
            Si_5, No_5,

            Si_7, No_7,
            Si_8, No_8,
            Si_9, No_9,
            Si_10, No_10,
            Si_11, No_11,
            Si_12, No_12,
            Si_13, No_13,
            Si_14, No_14,
            Si_15, No_15,
            Si_16, No_16,
            Si_17, No_17,
            Si_18, No_18,
            Si_19, No_19;

    EditText EquipoULIT6;

    Button BtbFirma, BtnFinalizar;
    ImageView RealizaImage, ResponsableTImage;
    LinearLayout LinearTecnico, LinearFinalizar;

    EditText Observaciones;

    String  ResultadoCheck1 = "",
            ResultadoCheck2 = "",
            ResultadoCheck3 = "",
            ResultadoCheck4 = "",
            ResultadoCheck5 = "",

            ResultadoCheck7 = "",
            ResultadoCheck8 = "",
            ResultadoCheck9 = "",
            ResultadoCheck10 = "",
            ResultadoCheck11 = "",
            ResultadoCheck12 = "",
            ResultadoCheck13 = "",
            ResultadoCheck14 = "",
            ResultadoCheck15 = "",
            ResultadoCheck16 = "",
            ResultadoCheck17 = "",
            ResultadoCheck18 = "",
            ResultadoCheck19 = "",
            ResultadoEquipo = "";

    private AutoCompleteTextView PersonaRealizaInterno;
    private ArrayAdapter<String> adapter;
    private final Map<String, String> mapaPersonal = new HashMap<>();
    private final List<String> listaNombres = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mantenimiento_preventivo_tanque);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Mantenimiento Preventivo");
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
        NumeroPagina = getIntent().getStringExtra("NumeroPagina");
        estado = getIntent().getStringExtra("estado");

        TxtTitulo = findViewById(R.id.TxtTitulo);
        FLTipoBase = findViewById(R.id.FLTipoBase);
        FLTipoSi = findViewById(R.id.FLTipoSi);
        FLTipoNo = findViewById(R.id.FLTipoNo);

        Si_1 = findViewById(R.id.Si_1);
        No_1 = findViewById(R.id.No_1);
        Si_2 = findViewById(R.id.Si_2);
        No_2 = findViewById(R.id.No_2);
        Si_3 = findViewById(R.id.Si_3);
        No_3 = findViewById(R.id.No_3);
        Si_4 = findViewById(R.id.Si_4);
        No_4 = findViewById(R.id.No_4);
        Si_5 = findViewById(R.id.Si_5);
        No_5 = findViewById(R.id.No_5);
        Si_7 = findViewById(R.id.Si_7);
        No_7 = findViewById(R.id.No_7);
        Si_8 = findViewById(R.id.Si_8);
        No_8 = findViewById(R.id.No_8);
        Si_9 = findViewById(R.id.Si_9);
        No_9 = findViewById(R.id.No_9);
        Si_10 = findViewById(R.id.Si_10);
        No_10 = findViewById(R.id.No_10);
        Si_11 = findViewById(R.id.Si_11);
        No_11 = findViewById(R.id.No_11);
        Si_12 = findViewById(R.id.Si_12);
        No_12 = findViewById(R.id.No_12);
        Si_13 = findViewById(R.id.Si_13);
        No_13 = findViewById(R.id.No_13);
        Si_14 = findViewById(R.id.Si_14);
        No_14 = findViewById(R.id.No_14);
        Si_15 = findViewById(R.id.Si_15);
        No_15 = findViewById(R.id.No_15);
        Si_16 = findViewById(R.id.Si_16);
        No_16 = findViewById(R.id.No_16);
        Si_17 = findViewById(R.id.Si_17);
        No_17 = findViewById(R.id.No_17);
        Si_18 = findViewById(R.id.Si_18);
        No_18 = findViewById(R.id.No_18);
        Si_19 = findViewById(R.id.Si_19);
        No_19 = findViewById(R.id.No_19);

        EquipoULIT6 = findViewById(R.id.EquipoULIT6);
        PersonaRealizaInterno = findViewById(R.id.PersonaRealizaInterno);

        LinearTecnico = findViewById(R.id.LinearTecnico);
        LinearFinalizar = findViewById(R.id.LinearFinalizar);
        TxtPersonalExterno = findViewById(R.id.TxtPersonalExterno);
        ImageFirma = findViewById(R.id.ImageFirma);
        BtbFirma = findViewById(R.id.BtbFirma);

        Observaciones = findViewById(R.id.Observaciones);

        BtnFinalizar = findViewById(R.id.BtnFinalizar);

        TxtTitulo.setText(NombreEquipo);

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
            BtnFinalizar.setEnabled(true);
            layoutFueraRango.setVisibility(GONE);
        } else {
            BtnFinalizar.setEnabled(false);
            layoutFueraRango.setVisibility(VISIBLE);
            BtnFinalizar.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.color_inactivo));
        }

        BtnFinalizar.setOnClickListener(v-> {
            if (Si_1.isChecked()) {
                ResultadoCheck1 = "Si";
                    ValidaSi();
            }else if (No_1.isChecked()) {
                ResultadoCheck1 = "No";
                    ValidaNo();
            }
        });

        BtbFirma.setOnClickListener(v -> {
            DialogoFirma();
        });

        fectTanques();


    }

    private void fectTanques() {

        DialogHelper.showProgressDialog(this);
        String url = URL_SERVIDOR + "Mantenimiento/mantenimiento-verificar-tanque-detalle.php?idMantenimiento=" + idMantenimiento;
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {

                    try {
                        JSONArray detallesArray = response.getJSONArray("detalles");

                        String IDFPR = response.getString("IDFPR");
                        String IDFPS = response.getString("IDFPS");
                        String FPR = response.getString("FPR");
                        String FPS = response.getString("FPS");
                        String PFPR = response.getString("PFPR");
                        String PFPS = response.getString("PFPS");

                        for (int i = 0; i < detallesArray.length(); i++) {
                            JSONObject detalle = detallesArray.getJSONObject(i);

                            int numList = detalle.getInt("numlista");
                            String resultado = detalle.getString("resultado");

                            int txtId = getResources().getIdentifier("TxtVerificar" + numList, "id", getPackageName());
                            int siId = getResources().getIdentifier("Si_" + numList, "id", getPackageName());
                            int noId = getResources().getIdentifier("No_" + numList, "id", getPackageName());

                            TextView txtVerificar = findViewById(txtId);
                            CheckBox siCheck = findViewById(siId);
                            CheckBox noCheck = findViewById(noId);

                            if (txtVerificar != null && siCheck != null && noCheck != null) {
                                txtVerificar.setText(detalle.getString("detalle"));

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

                                if (numList == 1) {
                                    if (siCheck.isChecked()) {
                                        actualizarVistaNum1(true);
                                    } else if (noCheck.isChecked()) {
                                        actualizarVistaNum1(false);
                                    } else {
                                        reiniciarVistaNum1();
                                    }
                                }

                                if (numList == 2) {
                                    if (siCheck.isChecked()) {
                                        actualizarVistaNum2(true);

                                        PersonaRealizaInterno.setText(PFPR);
                                        idSeleccionado = IDFPR;

                                    } else if (noCheck.isChecked()) {
                                        actualizarVistaNum2(false);

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

                                    } else {
                                        reiniciarVistaNum2();
                                    }
                                }

                            }

                            if (numList == 6) {
                                EquipoULIT6.setText(resultado);
                            }
                        }

                        Observaciones.setText(response.getString("observaciones"));

                        DialogHelper.hideProgressDialog();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        DialogHelper.hideProgressDialog();
                    }

                }, error -> {
            error.printStackTrace();
            DialogHelper.hideProgressDialog();
        });

        requestQueue.add(jsonObjectRequest);
    }


    private void actualizarVistaNum1(boolean isSiChecked) {
        if (isSiChecked) {
            if (No_1 != null) No_1.setEnabled(false);
            if (FLTipoBase != null) FLTipoBase.setVisibility(View.VISIBLE);
            if (FLTipoSi != null) FLTipoSi.setVisibility(View.VISIBLE);
            if (Observaciones != null) Observaciones.setVisibility(View.VISIBLE);
        } else {
            if (Si_1 != null) Si_1.setEnabled(false);
            if (FLTipoBase != null) FLTipoBase.setVisibility(View.VISIBLE);
            if (FLTipoNo != null) FLTipoNo.setVisibility(View.VISIBLE);
            if (Observaciones != null) Observaciones.setVisibility(View.VISIBLE);

            ListaPersonal();
        }
    }

    private void reiniciarVistaNum1() {
        if (No_1 != null) No_1.setEnabled(true);
        if (Si_1 != null) Si_1.setEnabled(true);
        if (FLTipoBase != null) FLTipoBase.setVisibility(View.GONE);
        if (FLTipoSi != null) FLTipoSi.setVisibility(View.GONE);
        if (FLTipoNo != null) FLTipoNo.setVisibility(View.GONE);
        if (Observaciones != null) Observaciones.setVisibility(View.GONE);

        limpiarAutoComplete();
    }

    private void actualizarVistaNum2(boolean isSiChecked) {
        if (isSiChecked) {
            if (No_2 != null) No_2.setEnabled(false);
            if (PersonaRealizaInterno != null) PersonaRealizaInterno.setVisibility(View.VISIBLE);
            if (LinearFinalizar != null) LinearFinalizar.setVisibility(View.VISIBLE);
            ListaPersonal();
        } else {
            if (Si_2 != null) Si_2.setEnabled(false);
            if (LinearTecnico != null) LinearTecnico.setVisibility(View.VISIBLE);
            if (LinearFinalizar != null) LinearFinalizar.setVisibility(View.VISIBLE);
            limpiarAutoComplete();
        }
    }

    private void reiniciarVistaNum2() {
        if (No_2 != null) No_2.setEnabled(true);
        if (Si_2 != null) Si_2.setEnabled(true);
        if (PersonaRealizaInterno != null) PersonaRealizaInterno.setVisibility(View.GONE);
        if (LinearTecnico != null) LinearTecnico.setVisibility(View.GONE);
        if (LinearFinalizar != null) LinearFinalizar.setVisibility(View.GONE);
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

                            adapter = new ArrayAdapter<>(MantenimientoPreventivoTanque.this,
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

    private void limpiarAutoComplete() {
        idSeleccionado = "0";
        PersonaRealizaInterno.setText("");
        PersonaRealizaInterno.clearFocus();
        PersonaRealizaInterno.dismissDropDown();
    }

    public void CheckboxClicked(View view) {
        boolean checked = ((CheckBox) view).isChecked();
        if (view.getId() == R.id.Si_1) {
            if (checked) {
                No_1.setEnabled(false);
                FLTipoBase.setVisibility(VISIBLE);
                FLTipoSi.setVisibility(VISIBLE);
                Observaciones.setVisibility(VISIBLE);

            } else {
                No_1.setEnabled(true);
                FLTipoBase.setVisibility(GONE);
                FLTipoSi.setVisibility(GONE);
                Observaciones.setVisibility(GONE);
            }
        }else if(view.getId() == R.id.No_1) {

            if (checked) {
                Si_1.setEnabled(false);
                FLTipoBase.setVisibility(VISIBLE);
                FLTipoNo.setVisibility(VISIBLE);
                Observaciones.setVisibility(VISIBLE);


            } else {
                Si_1.setEnabled(true);
                FLTipoBase.setVisibility(GONE);
                FLTipoNo.setVisibility(GONE);
                Observaciones.setVisibility(GONE);

            }

        }else if(view.getId() == R.id.Si_2) {
            if (checked){

                No_2.setEnabled(false);
                PersonaRealizaInterno.setVisibility(View.VISIBLE);
                LinearFinalizar.setVisibility(View.VISIBLE);
                ListaPersonal();

                }else{
                No_2.setEnabled(true);
                PersonaRealizaInterno.setVisibility(View.GONE);
                LinearFinalizar.setVisibility(View.GONE);
                limpiarAutoComplete();

                }
        }else if(view.getId() == R.id.No_2) {
            if (checked){
                Si_2.setEnabled(false);
                LinearTecnico.setVisibility(View.VISIBLE);
                LinearFinalizar.setVisibility(View.VISIBLE);
                ListaPersonal();
                }else{
                Si_2.setEnabled(true);
                LinearTecnico.setVisibility(View.GONE);
                LinearFinalizar.setVisibility(View.GONE);
                limpiarAutoComplete();
                }
        }else if(view.getId() == R.id.Si_3) {
            No_3.setEnabled(!checked);
        }else if(view.getId() == R.id.No_3) {
            Si_3.setEnabled(!checked);
        }else if(view.getId() == R.id.Si_4) {
            No_4.setEnabled(!checked);
        }else if(view.getId() == R.id.No_4) {
            Si_4.setEnabled(!checked);
        }else if(view.getId() == R.id.Si_5) {
            No_5.setEnabled(!checked);
        }else if(view.getId() == R.id.No_5) {
            Si_5.setEnabled(!checked);
        }else if(view.getId() == R.id.Si_7) {
            No_7.setEnabled(!checked);
        }else if(view.getId() == R.id.No_7) {
            Si_7.setEnabled(!checked);
        }else if(view.getId() == R.id.Si_8) {
            No_8.setEnabled(!checked);
        }else if(view.getId() == R.id.No_8) {
            Si_8.setEnabled(!checked);
        }else if (view.getId() == R.id.Si_9) {
            No_9.setEnabled(!checked);
        }else if (view.getId() == R.id.No_9) {
            Si_9.setEnabled(!checked);
        }else if (view.getId() == R.id.Si_10) {
            No_10.setEnabled(!checked);
        }else if (view.getId() == R.id.No_10) {
            Si_10.setEnabled(!checked);
        }else if (view.getId() == R.id.Si_11) {
            No_11.setEnabled(!checked);
        }else if (view.getId() == R.id.No_11) {
            Si_11.setEnabled(!checked);
        }else if (view.getId() == R.id.Si_12) {
            No_12.setEnabled(!checked);
        }else if (view.getId() == R.id.No_12) {
            Si_12.setEnabled(!checked);
        }else if (view.getId() == R.id.Si_13) {
            No_13.setEnabled(!checked);
        }else if (view.getId() == R.id.No_13) {
            Si_13.setEnabled(!checked);
        }else if (view.getId() == R.id.Si_14) {
            No_14.setEnabled(!checked);
        }else if (view.getId() == R.id.No_14) {
            Si_14.setEnabled(!checked);
        }else if (view.getId() == R.id.Si_15) {
            No_15.setEnabled(!checked);
        }else if (view.getId() == R.id.No_15) {
            Si_15.setEnabled(!checked);
        }else if (view.getId() == R.id.Si_16) {
            No_16.setEnabled(!checked);
        }else if (view.getId() == R.id.No_16) {
            Si_16.setEnabled(!checked);
        }else if (view.getId() == R.id.Si_17) {
            No_17.setEnabled(!checked);
        }else if (view.getId() == R.id.No_17) {
            Si_17.setEnabled(!checked);
        }else if (view.getId() == R.id.Si_18) {
            No_18.setEnabled(!checked);
        }else if (view.getId() == R.id.No_18) {
            Si_18.setEnabled(!checked);
        }else if (view.getId() == R.id.Si_19) {
            No_19.setEnabled(!checked);
        }else if (view.getId() == R.id.No_19) {
            Si_19.setEnabled(!checked);
        }
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

    public void ValidaSi(){

        if (Si_2.isChecked() && !No_2.isChecked()){
            ResultadoCheck2 = "Si";
        }else if (!Si_2.isChecked() && No_2.isChecked()){
            ResultadoCheck2 = "No";
        }

        if (Si_3.isChecked() && !No_3.isChecked()){
            ResultadoCheck3 = "Si";
        }else if (!Si_3.isChecked() && No_3.isChecked()){
            ResultadoCheck3 = "No";
        }

        if (Si_4.isChecked() && !No_4.isChecked()){
            ResultadoCheck4 = "Si";
        }else if (!Si_4.isChecked() && No_4.isChecked()){
            ResultadoCheck4 = "No";
        }

        if (Si_5.isChecked() && !No_5.isChecked()){
            ResultadoCheck5 = "Si";
        }else if (!Si_5.isChecked() && No_5.isChecked()){
            ResultadoCheck5 = "No";
        }

        ResultadoEquipo = EquipoULIT6.getText().toString();

        Finalizarmantenimiento();

    }

    public void ValidaNo(){

        if (Si_2.isChecked() && !No_2.isChecked()){
            ResultadoCheck2 = "Si";
        }else if (!Si_2.isChecked() && No_2.isChecked()){
            ResultadoCheck2 = "No";
        }

        if (Si_3.isChecked() && !No_3.isChecked()){
            ResultadoCheck3 = "Si";
        }else if (!Si_3.isChecked() && No_3.isChecked()){
            ResultadoCheck3 = "No";
        }

        if (Si_7.isChecked() && !No_7.isChecked()){
            ResultadoCheck7 = "Si";
        }else if (!Si_7.isChecked() && No_7.isChecked()){
            ResultadoCheck7 = "No";
        }

        if (Si_8.isChecked() && !No_8.isChecked()){
            ResultadoCheck8 = "Si";
        }else if (!Si_8.isChecked() && No_8.isChecked()){
            ResultadoCheck8 = "No";
        }

        if (Si_9.isChecked() && !No_9.isChecked()){
            ResultadoCheck9 = "Si";
        }else if (!Si_9.isChecked() && No_9.isChecked()){
            ResultadoCheck9 = "No";
        }

        if (Si_10.isChecked() && !No_10.isChecked()){
            ResultadoCheck10 = "Si";
        }else if (!Si_10.isChecked() && No_10.isChecked()){
            ResultadoCheck10 = "No";
        }

        if (Si_11.isChecked() && !No_11.isChecked()){
            ResultadoCheck11 = "Si";
        }else if (!Si_11.isChecked() && No_11.isChecked()){
            ResultadoCheck11 = "No";
        }

        if (Si_12.isChecked() && !No_12.isChecked()){
            ResultadoCheck12 = "Si";
        }else if (!Si_12.isChecked() && No_12.isChecked()){
            ResultadoCheck12 = "No";
        }

        if (Si_13.isChecked() && !No_13.isChecked()){
            ResultadoCheck13 = "Si";
        }else if (!Si_13.isChecked() && No_13.isChecked()){
            ResultadoCheck13 = "No";
        }

        if (Si_14.isChecked() && !No_14.isChecked()){
            ResultadoCheck14 = "Si";
        }else if (!Si_14.isChecked() && No_14.isChecked()){
            ResultadoCheck14 = "No";
        }

        if (Si_15.isChecked() && !No_15.isChecked()){
            ResultadoCheck15 = "Si";
        }else if (!Si_15.isChecked() && No_15.isChecked()){
            ResultadoCheck15 = "No";
        }

        if (Si_16.isChecked() && !No_16.isChecked()){
            ResultadoCheck16 = "Si";
        }else if (!Si_16.isChecked() && No_16.isChecked()){
            ResultadoCheck16 = "No";
        }

        if (Si_17.isChecked() && !No_17.isChecked()){
            ResultadoCheck17 = "Si";
        }else if (!Si_17.isChecked() && No_17.isChecked()){
            ResultadoCheck17 = "No";
        }

        if (Si_18.isChecked() && !No_18.isChecked()){
            ResultadoCheck18 = "Si";
        }else if (!Si_18.isChecked() && No_18.isChecked()){
            ResultadoCheck18 = "No";
        }

        if (Si_19.isChecked() && !No_19.isChecked()){
            ResultadoCheck19 = "Si";
        }else if (!Si_19.isChecked() && No_19.isChecked()){
            ResultadoCheck19 = "No";
        }

        Finalizarmantenimiento();

    }

    public void Finalizarmantenimiento(){

        final String observaciones = Observaciones.getText().toString().trim();
        Toast.makeText(this, estado, Toast.LENGTH_SHORT).show();
        String url = URL_SERVIDOR + "Mantenimiento/mantenimiento-verificar-tanque.php";
        String personal = "";

        if(Si_2.isChecked()){
            personal = PersonaRealizaInterno.getText().toString();
        }else if(No_2.isChecked()){

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


                    ToastUtils.showAndThen(MantenimientoPreventivoTanque.this, "Mantenimiento Finalizado", ToastUtils.SUCCESS, () -> {
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
                    Log.d("TAG", "onResponse: " + error.toString());
                    ToastUtils.show(this, "Error al enviar datos", ToastUtils.ERROR);
                    DialogHelper.hideProgressDialog();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                params.put("ResultadoCheck1", ResultadoCheck1);

                if (Si_1.isChecked()) {
                    params.put("ResultadoCheck2", ResultadoCheck2);
                    params.put("ResultadoCheck3", ResultadoCheck3);
                    params.put("ResultadoCheck4", ResultadoCheck4);
                    params.put("ResultadoCheck5", ResultadoCheck5);
                    params.put("EquipoULIT", EquipoULIT6.getText().toString());

                }else {

                    params.put("ResultadoCheck2", ResultadoCheck2);
                    params.put("ResultadoCheck3", ResultadoCheck3);
                    params.put("ResultadoCheck7", ResultadoCheck7);
                    params.put("ResultadoCheck8", ResultadoCheck8);
                    params.put("ResultadoCheck9", ResultadoCheck9);
                    params.put("ResultadoCheck10", ResultadoCheck10);
                    params.put("ResultadoCheck11", ResultadoCheck11);
                    params.put("ResultadoCheck12", ResultadoCheck12);
                    params.put("ResultadoCheck13", ResultadoCheck13);
                    params.put("ResultadoCheck14", ResultadoCheck14);
                    params.put("ResultadoCheck15", ResultadoCheck15);
                    params.put("ResultadoCheck16", ResultadoCheck16);
                    params.put("ResultadoCheck17", ResultadoCheck17);
                    params.put("ResultadoCheck18", ResultadoCheck18);
                    params.put("ResultadoCheck19", ResultadoCheck19);
                }
                params.put("idPersonalSupervisa", String.valueOf(IdUsuario));
                params.put("idMantenimiento", idMantenimiento);
                params.put("NumeroEquipo", NumeroEquipo);
                params.put("Observaciones", observaciones);

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