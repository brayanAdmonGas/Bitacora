package com.gestogas.gestoline;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.gestogas.gestoline.adapter.adapterGerente;
import com.gestogas.gestoline.data.dataUsuario;
import com.gestogas.gestoline.utils.Constantes;
import com.gestogas.gestoline.utils.DialogHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class EstacionGerentes extends BaseActivity {

    private RecyclerView recyclerView;
    private adapterGerente adapter;
    private List<dataUsuario> itemList;
    private RequestQueue requestQueue;
    String idgas;
    ImageView ImgResultado;
    TextView Mensaje;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_gerentes);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        idgas = getIntent().getStringExtra("idgas");
        String razonsocial = getIntent().getStringExtra("razonsocial");

        TextView Estacion = findViewById(R.id.Estacion);
        Estacion.setText(razonsocial);

        Mensaje = findViewById(R.id.Mensaje);
        ImgResultado = findViewById(R.id.ImgResultado);

        recyclerView = findViewById(R.id.Recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        itemList = new ArrayList<>();
        adapter = new adapterGerente(this,itemList);
        recyclerView.setAdapter(adapter);

        requestQueue = Volley.newRequestQueue(this);

        DialogHelper.showProgressDialog(this);
        fetchGerentes();

    }

    private void fetchGerentes(){
        String url = Constantes.URL_SERVIDOR + Constantes.FOLDER_ADMIN + "lista-gerente.php?idEstacion=" + idgas;;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
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

                                // Extrae los datos del JSON
                                int idusuario = jsonObject.getInt("IdUsuario");
                                String nombre = jsonObject.getString("NombreUsuario");

                                // Crea un objeto Item y lo añade a la lista
                                dataUsuario item = new dataUsuario(idusuario, nombre);
                                itemList.add(item);
                            }

                            // Notifica al adaptador que los datos han cambiado
                            adapter.notifyDataSetChanged();
                            DialogHelper.hideProgressDialog();
                        } catch (JSONException e) {
                            Mensaje.setVisibility(View.VISIBLE);
                            Mensaje.setText("No se encontró información para mostrar");
                            DialogHelper.hideProgressDialog();
                            ImgResultado.setImageResource(R.drawable.informacion);
                            ImgResultado.setVisibility(View.VISIBLE);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        DialogHelper.hideProgressDialog();
                        Mensaje.setVisibility(View.VISIBLE);
                        Mensaje.setText("No se encontró información para mostrar");
                        ImgResultado.setImageResource(R.drawable.informacion);
                        ImgResultado.setVisibility(View.VISIBLE);
                    }
                });

        requestQueue.add(jsonArrayRequest);

    }

    private void volverAEstaciones() {
        Intent intent = new Intent(this, Estaciones.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            volverAEstaciones();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onSupportNavigateUp() {
        volverAEstaciones();
        return true;
    }

}
