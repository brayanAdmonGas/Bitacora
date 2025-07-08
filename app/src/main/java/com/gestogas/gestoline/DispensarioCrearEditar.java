package com.gestogas.gestoline;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

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
import com.gestogas.gestoline.utils.Constantes;
import com.gestogas.gestoline.utils.DialogHelper;
import com.gestogas.gestoline.utils.DistanciaUtils;
import com.gestogas.gestoline.utils.TecladoUtils;
import com.gestogas.gestoline.utils.ToastUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.gestogas.gestoline.databinding.ActivityDispensarioCrearEditarBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DispensarioCrearEditar extends BaseActivity {

    int idEstacion, idPuesto;
    String estado, url, titulo,pd1, pd2, pd3;
    EditText NoDispensario, Marca, Modelo, Serie, Producto1, Producto2, Producto3;
    Button BtnGuardar;
    double latitudeEstacion = 0, longitudeEstacion = 0, latitudeEquipo = 0, longitudeEquipo = 0;
    int distMax = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dispensario_crear_editar);

        idEstacion = AppController.getInstance().GetIdestacion();
        idPuesto = AppController.getInstance().GetIdPuesto();
        latitudeEquipo = Double.parseDouble(AppController.getInstance().GetLatitudEquipo());
        longitudeEquipo = Double.parseDouble(AppController.getInstance().GetLongitudEquipo());

        pd1 = AppController.getInstance().GetProductoUno();
        pd2 = AppController.getInstance().GetProductoDos();
        pd3 = AppController.getInstance().GetProductoTres();

        distMax = Integer.parseInt(AppController.getInstance().GetDistMax());
        latitudeEstacion = Double.parseDouble(AppController.getInstance().GetLatitud());
        longitudeEstacion = Double.parseDouble(AppController.getInstance().GetLongitud());

        estado = getIntent().getStringExtra("estado");
        titulo = getIntent().getStringExtra("titulo");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(titulo);
        }

        NoDispensario = findViewById(R.id.NoDispensario);
        Marca = findViewById(R.id.Marca);
        Modelo = findViewById(R.id.Modelo);
        Serie = findViewById(R.id.Serie);
        Producto1 = findViewById(R.id.Producto1);
        Producto2 = findViewById(R.id.Producto2);
        Producto3 = findViewById(R.id.Producto3);
        BtnGuardar = findViewById(R.id.BtnGuardar);
        LinearLayout layoutFueraRango = findViewById(R.id.layoutFueraRango);

        Producto1.setHint("Mangueras " + pd1);
        Producto2.setHint("Mangueras " + pd2);

        if (pd3.isEmpty()){
            Producto3.setVisibility(VISIBLE);
            Producto3.setHint("Mangueras " + pd3);
        }

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



        BtnGuardar.setOnClickListener(view -> {
            validarDispensario();
        });

    }

    public void validarDispensario(){

        if (NoDispensario.getText().toString().isEmpty()) {
            NoDispensario.setError("Falta agregar el numero de dispensario");
            ToastUtils.show(this, "Falta agregar el numero de dispensario", ToastUtils.INFO);
        }else if (Marca.getText().toString().isEmpty()) {
            Marca.setError("Falta agregar la marca");
            ToastUtils.show(this, "Falta agregar la marca", ToastUtils.INFO);
        }else if (Modelo.getText().toString().isEmpty()) {
            Modelo.setError("Falta agregar el modelo");
            ToastUtils.show(this, "Falta agregar el modelo", ToastUtils.INFO);
        }else if (Serie.getText().toString().isEmpty()) {
            Serie.setError("Falta agregar la serie");
            ToastUtils.show(this, "Falta agregar la serie", ToastUtils.INFO);
        }else if (Producto1.getText().toString().isEmpty()) {
            Producto1.setError("Falta agregar el producto 1");
            ToastUtils.show(this, "Falta agregar el producto 1", ToastUtils.INFO);
        }else{
            AgregarDispensario();
        }
    }

    public void AgregarDispensario(){

        final String nodispensario = NoDispensario.getText().toString();
        final String marca = Marca.getText().toString();
        final String modelo = Modelo.getText().toString();
        final String serie = Serie.getText().toString();
        final String producto1 = Producto1.getText().toString();
        final String producto2 = Producto2.getText().toString();
        final String producto3 = Producto3.getText().toString();

        url = (estado.equals("Nuevo"))?  "Dispensario/agregar-dispensario-estacion.php" :  "";

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

                                ToastUtils.showAndThen(DispensarioCrearEditar.this, mensaje, ToastUtils.SUCCESS, () -> {
                                    setResult(RESULT_OK);
                                    finish();
                                });


                            }else{
                                DialogHelper.hideProgressDialog();
                                ToastUtils.show(DispensarioCrearEditar.this, mensaje, ToastUtils.INFO);
                            }

                        }catch (JSONException e){
                            ToastUtils.show(DispensarioCrearEditar.this, e.toString(), ToastUtils.INFO);
                            DialogHelper.hideProgressDialog();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ToastUtils.show(DispensarioCrearEditar.this, error.toString(), ToastUtils.INFO);
                        DialogHelper.hideProgressDialog();
                    }
                }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("ValEstacionid", String.valueOf(idEstacion));
                params.put("ValNoDispensario", nodispensario);
                params.put("ValMarca", marca);
                params.put("ValModelo", modelo);
                params.put("ValSerie", serie);
                params.put("ValProducto1", producto1);
                params.put("ValProducto2", producto2);
                params.put("ValProducto3", producto3);

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