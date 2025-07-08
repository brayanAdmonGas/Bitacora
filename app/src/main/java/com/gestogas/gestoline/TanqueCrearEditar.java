package com.gestogas.gestoline;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.gestogas.gestoline.controllers.AppController;
import com.gestogas.gestoline.recepcion.RecepcionBitacoraCrearEditar;
import com.gestogas.gestoline.utils.Constantes;
import com.gestogas.gestoline.utils.DialogHelper;
import com.gestogas.gestoline.utils.DistanciaUtils;
import com.gestogas.gestoline.utils.TecladoUtils;
import com.gestogas.gestoline.utils.ToastUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import androidx.activity.EdgeToEdge;

import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TanqueCrearEditar extends BaseActivity {

    private int idEstacion,idPuesto;
    private String idtanque, productouno, productodos, productotres, titulo, tituloboton, estado, notanque, capacidad, tanqueproducto, url;
    private EditText NoTanque, Capacidad;
    private Button BtnGuardar;
    private AutoCompleteTextView producto;
    double latitudeEstacion = 0, longitudeEstacion = 0, latitudeEquipo = 0, longitudeEquipo = 0;
    int distMax = 0;

     @Override
    protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         EdgeToEdge.enable(this);
         setContentView(R.layout.activity_tanque_crear_editar);

         idEstacion = AppController.getInstance().GetIdestacion();
         idPuesto = AppController.getInstance().GetIdPuesto();

         latitudeEquipo = Double.parseDouble(AppController.getInstance().GetLatitudEquipo());
         longitudeEquipo = Double.parseDouble(AppController.getInstance().GetLongitudEquipo());

         distMax = Integer.parseInt(AppController.getInstance().GetDistMax());
         latitudeEstacion = Double.parseDouble(AppController.getInstance().GetLatitud());
         longitudeEstacion = Double.parseDouble(AppController.getInstance().GetLongitud());

         productouno =  AppController.getInstance().GetProductoUno();
         productodos =  AppController.getInstance().GetProductoDos();
         productotres =  AppController.getInstance().GetProductoTres();

         estado = getIntent().getStringExtra("estado");
         idtanque = getIntent().getStringExtra("idtanque");
         notanque = getIntent().getStringExtra("notanque");
         capacidad = getIntent().getStringExtra("capacidad");
         tanqueproducto = getIntent().getStringExtra("producto");
         titulo = getIntent().getStringExtra("titulo");
         tituloboton = getIntent().getStringExtra("tituloboton");

         Toolbar toolbar = findViewById(R.id.toolbar);
         setSupportActionBar(toolbar);

         if (getSupportActionBar() != null) {
             getSupportActionBar().setTitle(titulo);
         }

         NoTanque = findViewById(R.id.NoTanque);
         Capacidad = findViewById(R.id.Capacidad);
         producto = findViewById(R.id.Producto);
         BtnGuardar = findViewById(R.id.BtnGuardar);
         LinearLayout layoutFueraRango = findViewById(R.id.layoutFueraRango);

         NoTanque.setText(notanque);
         Capacidad.setText(capacidad);
         producto.setText(tanqueproducto);
         BtnGuardar.setText(tituloboton);

         // Lista de opciones
         List<String> opcionesList = new ArrayList<>();

         opcionesList.add(productouno);
         opcionesList.add(productodos);
         if (!TextUtils.isEmpty(productotres)) {
             opcionesList.add(productotres);
         }

         String[] opciones = opcionesList.toArray(new String[0]);

         producto.setOnClickListener(v -> {
             int selectedIndex = opcionesList.indexOf(producto.getText().toString());

             new MaterialAlertDialogBuilder(TanqueCrearEditar.this)
                     .setTitle("Selecciona el producto")
                     .setSingleChoiceItems(opcionesList.toArray(new String[0]), selectedIndex, (dialog, which) -> {
                         producto.setText(opcionesList.get(which));
                         dialog.dismiss();
                     })
                     .show();
         });


         BtnGuardar.setOnClickListener(view -> {
             validaTanque();
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
             BtnGuardar.setBackgroundTintList(ColorStateList.valueOf(gris));

         }


    }

    private void validaTanque() {

     if (NoTanque.getText().toString().isEmpty()) {
         NoTanque.setError("Falta agregar el numero de tanque");
         ToastUtils.show(this, "Falta agregar el numero de tanque", ToastUtils.INFO);
     } else {
         if (Capacidad.getText().toString().isEmpty()) {
             Capacidad.setError("Falta agregar la capacidad del tanque");
             ToastUtils.show(this, "Falta agregar la capacidad del tanque", ToastUtils.INFO);
         } else {
             if (producto.getText().toString().isEmpty()) {
                 producto.setError("Falta agregar el producto");
                 ToastUtils.show(this, "Falta agregar el producto", ToastUtils.INFO);
             } else {
                 DialogHelper.showProgressDialog(this);
                 guardarTanque();
             }
         }
     }
    }

    private void guardarTanque() {

         String notanque = NoTanque.getText().toString().trim();
         String capacidad = Capacidad.getText().toString().trim();
         String productotanque = producto.getText().toString().trim();

        url = (estado.equals("Nuevo"))?  "TanqueAlmacenamiento/agregar-tanque-almacenamiento.php" :  "TanqueAlmacenamiento/editar-tanque-almacenamiento.php";

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

                                ToastUtils.showAndThen(TanqueCrearEditar.this, mensaje, ToastUtils.SUCCESS, () -> {
                                    setResult(RESULT_OK);
                                    finish();
                                });

                            }else{
                                DialogHelper.hideProgressDialog();
                                ToastUtils.show(TanqueCrearEditar.this, mensaje, ToastUtils.ERROR);
                            }


                        }catch (Exception e){
                            ToastUtils.show(TanqueCrearEditar.this, "Se produjo un error, revise su conexión a internet", ToastUtils.INFO);
                            DialogHelper.hideProgressDialog();
                        }
                    }
                },
                    new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ToastUtils.show(TanqueCrearEditar.this, "Se produjo un error, revise su conexión a internet", ToastUtils.INFO);
                        DialogHelper.hideProgressDialog();
                    }
                }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", String.valueOf(idEstacion));
                params.put("idtanque", idtanque);
                params.put("nodetanque", notanque);
                params.put("capacidad", capacidad);
                params.put("nombreproducto", productotanque);
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