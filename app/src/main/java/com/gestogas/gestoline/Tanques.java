package com.gestogas.gestoline;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.gestogas.gestoline.adapter.adapterTanques;
import com.gestogas.gestoline.controllers.AppController;
import com.gestogas.gestoline.data.dataTanques;
import com.gestogas.gestoline.utils.Constantes;
import com.gestogas.gestoline.utils.DialogHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Tanques extends BaseActivity {

    private RecyclerView recyclerView;
    private adapterTanques adapter;
    private List<dataTanques> itemList;
    private RequestQueue requestQueue;
    private int Idestacion;
    ImageView ImgResultado;
    TextView Mensaje;
    FloatingActionButton NuevoTanque;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tanques);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        Idestacion = AppController.getInstance().GetIdestacion();

        TextView RazonSocial = findViewById(R.id.RazonSocial);
        RazonSocial.setText(AppController.getInstance().GetRazonSocial());

        Mensaje = findViewById(R.id.Mensaje);
        ImgResultado = findViewById(R.id.ImgResultado);

        NuevoTanque = findViewById(R.id.NuevoTanque);

        recyclerView = findViewById(R.id.Recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        itemList = new ArrayList<>();
        adapter = new adapterTanques(this,itemList);
        recyclerView.setAdapter(adapter);

        requestQueue = Volley.newRequestQueue(this);
        fetchTanques();

        NuevoTanque.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent home = new Intent (view.getContext(), TanqueCrearEditar.class);
                home.putExtra("estado","Nuevo");
                home.putExtra("idtanque","");
                home.putExtra("notanque","");
                home.putExtra("capacidad","");
                home.putExtra("producto","");
                home.putExtra("titulo","Agregar Tanque");
                home.putExtra("tituloboton","GUARDAR TANQUE");
                startActivityForResult(home, 1);
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                // Oculta el FAB mientras se hace scroll hacia abajo
                if (dy > 0 && NuevoTanque.isShown()) {
                    NuevoTanque.hide();
                }
            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                // Si el usuario deja de hacer scroll, vuelve a mostrar el FAB
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    NuevoTanque.show();
                }
            }
        });

    }

    private void fetchTanques() {
        DialogHelper.showProgressDialog(this);
        String url = Constantes.URL_SERVIDOR + "TanqueAlmacenamiento/lista-tanques-almacenamiento.php?idEstacion=" + Idestacion;

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
                                int idextintor = jsonObject.getInt("id");
                                String notanque = jsonObject.getString("notanque");
                                String capacidad = jsonObject.getString("capacidad");
                                String producto = jsonObject.getString("producto");

                                // Crea un objeto Item y lo añade a la lista
                                dataTanques item = new dataTanques(idextintor,notanque,capacidad,producto);
                                itemList.add(item);
                            }

                            // Notifica al adaptador que los datos han cambiado
                            adapter.notifyDataSetChanged();
                            DialogHelper.hideProgressDialog();
                            ocultarError();

                        } catch (JSONException e) {
                            DialogHelper.hideProgressDialog();
                            mostrarError();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        DialogHelper.hideProgressDialog();
                        mostrarError();
                    }
                });

        requestQueue.add(jsonArrayRequest);
    }

    private void mostrarError() {
        Mensaje.setVisibility(View.VISIBLE);
        Mensaje.setText("No se encontró información para mostrar");
        ImgResultado.setImageResource(R.drawable.informacion);
        ImgResultado.setVisibility(View.VISIBLE);
    }

    private void ocultarError() {
        Mensaje.setVisibility(View.GONE);
        ImgResultado.setVisibility(View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            fetchTanques();
        }
    }

    private void volverAHome() {
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            volverAHome();

        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onSupportNavigateUp() {
        volverAHome();
        return true;
    }

}