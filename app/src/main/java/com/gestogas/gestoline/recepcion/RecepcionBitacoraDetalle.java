package com.gestogas.gestoline.recepcion;

import static com.gestogas.gestoline.utils.Constantes.URL_SERVIDOR;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.gestogas.gestoline.Evidencias;
import com.gestogas.gestoline.R;
import com.gestogas.gestoline.adapter.adapterImagen;
import com.gestogas.gestoline.controllers.AppController;
import com.gestogas.gestoline.data.dataImagen;
import com.gestogas.gestoline.utils.Constantes;
import com.gestogas.gestoline.utils.DialogHelper;
import com.gestogas.gestoline.utils.DistanciaUtils;
import com.gestogas.gestoline.utils.TecladoUtils;
import com.gestogas.gestoline.utils.ToastUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RecepcionBitacoraDetalle extends AppCompatActivity implements adapterImagen.OnEvidenciaEliminadaListener{
    int idPuesto, distMax = 0;
    String idRecepcion;
    TextView NoFactura, TxtFolio, TxtProducto, TxtLitrosCompra, TxtFechaHora,  TxtObservaciones,
            Cancelada,LineaTransporte,NoRemolque,NoPlaca,Operador,Manometro,Temperatura,
            Verificar1,Resultado1,Verificar2,Resultado2,Verificar3,Resultado3,
            NoTanque1,InventarioI1,InventarioF1,
            NoTanque2,InventarioI2,InventarioF2,
            NoTanque3,InventarioI3,InventarioF3,
            NoTanque4,InventarioI4,InventarioF4,
            Merma;

    ImageView ImagePR, ImagePS;
    FrameLayout FrameLayoutC, FrameLayoutO, FrameLayoutFPR, FrameLayoutFPS;
    FloatingActionButton Editar, Cancelar;
    Button  Evidencia;
    double latitudeEstacion = 0, longitudeEstacion = 0, latitudeEquipo = 0, longitudeEquipo = 0;

    private RecyclerView recyclerView;
    private adapterImagen adapter;
    private List<dataImagen> itemList;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recepcion_bitacora_detalle);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        idPuesto = AppController.getInstance().GetIdPuesto();
        latitudeEquipo = Double.parseDouble(AppController.getInstance().GetLatitudEquipo());
        longitudeEquipo = Double.parseDouble(AppController.getInstance().GetLongitudEquipo());
        distMax = Integer.parseInt(AppController.getInstance().GetDistMax());
        latitudeEstacion = Double.parseDouble(AppController.getInstance().GetLatitud());
        longitudeEstacion = Double.parseDouble(AppController.getInstance().GetLongitud());

        idRecepcion = getIntent().getStringExtra("idRecepcion");

        NoFactura = findViewById(R.id.NoFactura);
        TxtFolio = findViewById(R.id.TxtFolio);
        TxtProducto = findViewById(R.id.TxtProducto);
        TxtFechaHora = findViewById(R.id.TxtFechaHora);
        TxtLitrosCompra = findViewById(R.id.TxtLitrosCompra);

        LineaTransporte = findViewById(R.id.LineaTransporte);
        NoRemolque = findViewById(R.id.NoRemolque);
        NoPlaca = findViewById(R.id.NoPlaca);
        Operador = findViewById(R.id.Operador);
        Manometro = findViewById(R.id.Manometro);
        Temperatura = findViewById(R.id.Temperatura);

        Verificar1 = findViewById(R.id.Verificar1);
        Resultado1 = findViewById(R.id.Resultado1);
        Verificar2 = findViewById(R.id.Verificar2);
        Resultado2 = findViewById(R.id.Resultado2);
        Verificar3 = findViewById(R.id.Verificar3);
        Resultado3 = findViewById(R.id.Resultado3);

        NoTanque1 = findViewById(R.id.NoTanque1);
        InventarioI1 = findViewById(R.id.InventarioI1);
        InventarioF1 = findViewById(R.id.InventarioF1);

        NoTanque2 = findViewById(R.id.NoTanque2);
        InventarioI2 = findViewById(R.id.InventarioI2);
        InventarioF2 = findViewById(R.id.InventarioF2);

        NoTanque3 = findViewById(R.id.NoTanque3);
        InventarioI3 = findViewById(R.id.InventarioI3);
        InventarioF3 = findViewById(R.id.InventarioF3);

        NoTanque4 = findViewById(R.id.NoTanque4);
        InventarioI4 = findViewById(R.id.InventarioI4);
        InventarioF4 = findViewById(R.id.InventarioF4);
        Merma = findViewById(R.id.Merma);

        TxtObservaciones = findViewById(R.id.TxtObservaciones);
        FrameLayoutFPR = findViewById(R.id.FrameLayoutFPR);
        FrameLayoutFPS = findViewById(R.id.FrameLayoutFPS);
        ImagePR = findViewById(R.id.ImagePR);
        ImagePS = findViewById(R.id.ImagePS);

        Editar = findViewById(R.id.Editar);
        Cancelar = findViewById(R.id.Cancelar);
        Evidencia = findViewById(R.id.Evidencia);

        recyclerView = findViewById(R.id.Recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        String rutadelete = URL_SERVIDOR + "Recepcion/eliminar-evidencia.php";
        itemList = new ArrayList<>();
        adapter = new adapterImagen(this,itemList,rutadelete,this);
        recyclerView.setAdapter(adapter);

        requestQueue = Volley.newRequestQueue(this);

        fetchRecepcionBitacora();
        fetchEvidencia();

        Editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent home = new Intent(view.getContext(), RecepcionBitacoraCrearEditar.class);
                home.putExtra("idRecepcion", idRecepcion);
                home.putExtra("titulo", "Editar Recepción");
                home.putExtra("tituloboton", "EDITAR RECEPCIÓN");
                startActivityForResult(home, 1);

            }
        });

        Cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mensaje = "Esta acción cancelará la recepción y descarga del producto asociada a la bitácora seleccionada. ¿Desea continuar?";
                AlertWarning(mensaje);
            }

        });

        Evidencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent home = new Intent(view.getContext(), Evidencias.class);
                home.putExtra("id", idRecepcion);
                home.putExtra("categoria", "Recepcion");
                home.putExtra("carpeta", "Recepcion");
                home.putExtra("titulo", "Evidencia Recepción");
                startActivityForResult(home, 1);
            }
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

        if (!validarDistancia) {

            Editar.setEnabled(false);
            Editar.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.color_inactivo));

            Cancelar.setEnabled(false);
            Cancelar.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.color_inactivo));
        }

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
                                resultado2 = "", verificar3 = "", resultado3 = "", notanque1 = "", inventarioinicial1 = "", inventariofinal1 = "",
                                notanque2 = "", inventarioinicial2 = "", inventariofinal2 = "", notanque3 = "", inventarioinicial3 = "", inventariofinal3 = "",
                                notanque4 = "", inventarioinicial4 = "", inventariofinal4 = "", totalmerma = "";
                        int totalevidencia = 0;

                        try {

                            // Recorre el JSONArray
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jsonObject = response.getJSONObject(i);

                                // Extrae los datos del JSON
                                folio = jsonObject.getString("folio");
                                fecha = jsonObject.getString("fecha");
                                horallegada = jsonObject.getString("horallegada");
                                horasalida = jsonObject.getString("horasalida");
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

                                notanque1 = jsonObject.getString("notanque1");
                                inventarioinicial1 = jsonObject.getString("inventarioinicial1");
                                inventariofinal1 = jsonObject.getString("inventariofinal1");
                                notanque2 = jsonObject.getString("notanque2");
                                inventarioinicial2 = jsonObject.getString("inventarioinicial2");
                                inventariofinal2 = jsonObject.getString("inventariofinal2");
                                notanque3 = jsonObject.getString("notanque3");
                                inventarioinicial3 = jsonObject.getString("inventarioinicial3");
                                inventariofinal3 = jsonObject.getString("inventariofinal3");
                                notanque4 = jsonObject.getString("notanque4");
                                inventarioinicial4 = jsonObject.getString("inventarioinicial4");
                                inventariofinal4 = jsonObject.getString("inventariofinal4");

                                totalmerma = jsonObject.getString("Merma");
                                totalevidencia = jsonObject.getInt("totalevidencia");


                            }

                            NumberFormat formato = NumberFormat.getNumberInstance(Locale.US);
                            formato.setMinimumFractionDigits(2);
                            formato.setMaximumFractionDigits(2);

                            TxtFolio.setText(folio);
                            NoFactura.setText(nofactura);

                            TxtProducto.setText(producto);
                            TxtLitrosCompra.setText(litroscompra);

                            String colorHex = productoColores.getOrDefault(producto, "#000000");
                            TxtProducto.setTextColor(Color.parseColor(colorHex));

                            TextView textViewdrum = findViewById(R.id.textViewdrum);
                            TextView NoDrum = findViewById(R.id.NoDrum);

                            if (nodrum.isEmpty()){
                                textViewdrum.setVisibility(View.GONE);
                                NoDrum.setVisibility(View.GONE);
                            }else{
                                textViewdrum.setVisibility(View.VISIBLE);
                                NoDrum.setVisibility(View.VISIBLE);
                                NoDrum.setText(nodrum);

                            }

                            LineaTransporte.setText(lineatransporte);
                            NoRemolque.setText(noremolque);
                            NoPlaca.setText(placa);
                            Operador.setText(operador);
                            Manometro.setText(manometro);
                            Temperatura.setText(temperatura);

                            TxtFechaHora.setText(fecha + ", " + horallegada + " a " + horasalida);

                            Verificar1.setText(verificar1);
                            Resultado1.setText(resultado1);
                            Verificar2.setText(verificar2);
                            Resultado2.setText(resultado2);
                            Verificar3.setText(verificar3);
                            Resultado3.setText(resultado3);

                            if (notanque1.equals("0") && inventarioinicial1.isEmpty() && inventariofinal1.isEmpty()) {
                                NoTanque1.setVisibility(View.GONE);
                                InventarioI1.setVisibility(View.GONE);
                                InventarioF1.setVisibility(View.GONE);
                            } else {
                                NoTanque1.setVisibility(View.VISIBLE);
                                InventarioI1.setVisibility(View.VISIBLE);
                                InventarioF1.setVisibility(View.VISIBLE);

                                NoTanque1.setText(notanque1);
                                InventarioI1.setText(inventarioinicial1);
                                InventarioF1.setText(inventariofinal1);
                            }

                            if (notanque2.equals("0") && inventarioinicial2.isEmpty() && inventariofinal2.isEmpty()){

                                NoTanque2.setVisibility(View.GONE);
                                InventarioI2.setVisibility(View.GONE);
                                InventarioF2.setVisibility(View.GONE);

                            }else{
                                NoTanque2.setVisibility(View.VISIBLE);
                                InventarioI2.setVisibility(View.VISIBLE);
                                InventarioF2.setVisibility(View.VISIBLE);

                                NoTanque2.setText(notanque2);
                                InventarioI2.setText(inventarioinicial2);
                                InventarioF2.setText(inventariofinal2);
                            }

                            if (notanque3.equals("0") && inventarioinicial3.isEmpty() && inventariofinal3.isEmpty()) {

                                NoTanque3.setVisibility(View.GONE);
                                InventarioI3.setVisibility(View.GONE);
                                InventarioF3.setVisibility(View.GONE);

                            }else{

                                NoTanque3.setVisibility(View.VISIBLE);
                                InventarioI3.setVisibility(View.VISIBLE);
                                InventarioF3.setVisibility(View.VISIBLE);

                                NoTanque3.setText(notanque3);
                                InventarioI3.setText(inventarioinicial3);
                                InventarioF3.setText(inventariofinal3);
                            }

                            if (notanque4.equals("0") && inventarioinicial4.isEmpty() && inventariofinal4.isEmpty()) {

                                NoTanque4.setVisibility(View.GONE);
                                InventarioI4.setVisibility(View.GONE);
                                InventarioF4.setVisibility(View.GONE);

                            }else{

                                NoTanque4.setVisibility(View.VISIBLE);
                                InventarioI4.setVisibility(View.VISIBLE);
                                InventarioF4.setVisibility(View.VISIBLE);

                                NoTanque4.setText(notanque4);
                                InventarioI4.setText(inventarioinicial4);
                                InventarioF4.setText(inventariofinal4);
                            }

                            if (observaciones.isEmpty()){
                                TxtObservaciones.setText("No se encontró alguna observación ");
                            }else{
                                TxtObservaciones.setText(observaciones);
                            }

                            Merma.setText(totalmerma);

                            Glide.with(getApplicationContext()).load(Constantes.URL_HOST + "imgs/firma-personal/" + FPR).into(ImagePR);
                            Glide.with(getApplicationContext()).load(Constantes.URL_HOST + "imgs/firma-personal/" + FPS).into(ImagePS);


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

    @Override
    public void onEvidenciaEliminada() {
        fetchEvidencia();
    }

    private void fetchEvidencia(){

        DialogHelper.showProgressDialog(this);
        String ruta = URL_SERVIDOR + "Recepcion/lista-evidencia.php?id=" + idRecepcion;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, ruta, null,
                new Response.Listener<JSONArray>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onResponse(JSONArray response) {

                        try {
                            // Limpia la lista actual
                            itemList.clear();

                            // Recorre el JSONArray
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jsonObject = response.getJSONObject(i);

                                int id = jsonObject.getInt("id");
                                String ruta = jsonObject.getString("ruta");
                                String nombre = jsonObject.getString("nombre");

                                // Crea un objeto Item y lo añade a la lista
                                dataImagen item = new dataImagen(id, ruta, nombre);
                                itemList.add(item);
                            }

                            // Notifica al adaptador que los datos han cambiado
                            adapter.notifyDataSetChanged();
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

    private void AlertWarning(String mensaje){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.alert_warning,null);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.show();
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);

        Button alertCancelar = view.findViewById(R.id.alertCancelar);
        Button alertAceptar = view.findViewById(R.id.alertAceptar);
        TextView Mensaje = view.findViewById(R.id.txtMensaje);

        Mensaje.setText(mensaje);

        alertCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });

        alertAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                DialogHelper.showProgressDialog(RecepcionBitacoraDetalle.this);
                cancelarRecepcion();

            }
        });
    }

    private void cancelarRecepcion() {
        String url = "Recepcion/cancelar-recepcion-descarga.php";

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

                                ToastUtils.showAndThen(RecepcionBitacoraDetalle.this, mensaje, ToastUtils.SUCCESS, () -> {
                                    setResult(RESULT_OK);
                                    finish();
                                });

                            }else{
                                DialogHelper.hideProgressDialog();
                                ToastUtils.show(RecepcionBitacoraDetalle.this, mensaje, ToastUtils.ERROR);
                            }

                        }catch (Exception e){
                            ToastUtils.show(RecepcionBitacoraDetalle.this, e.toString(), ToastUtils.INFO);
                            DialogHelper.hideProgressDialog();
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ToastUtils.show(RecepcionBitacoraDetalle.this, error.toString(), ToastUtils.INFO);
                        DialogHelper.hideProgressDialog();
                    }
                }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("idRecepcion", String.valueOf(idRecepcion));
                return params;

            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private static final Map<String, String> productoColores = new HashMap<String, String>() {{
        put("BP REGULAR", "#4cd387");
        put("BP PREMIUM", "#f64c0f");
        put("EFITEC 87", "#4a8147");
        put("EFITEC 92", "#b94128");
        put("MAGNA", "#16BB43");
        put("PEMEX MAGNA", "#16BB43");
        put("PREMIUM", "#BB1616");
        put("PEMEX PREMIUM", "#BB1616");
        put("Shell Súper Regular", "#f7cc04");
        put("V Power Premiun", "#e32f18");
        put("G SUPER", "#77bd1e");
        put("G PREMIUM", "#e11682");
        put("G DIESEL", "#5d0e8b");
        put("DIESEL", "#000000");
        put("PEMEX DIESEL", "#000000");
        put("Diésel", "#000000");
    }};

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            DialogHelper.showProgressDialog(this);
            fetchRecepcionBitacora();
            fetchEvidencia();
        }
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