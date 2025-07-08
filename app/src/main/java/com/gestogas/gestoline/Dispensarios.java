package com.gestogas.gestoline;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.gestogas.gestoline.adapter.adapterDispensarios;
import com.gestogas.gestoline.adapter.adapterExtintores;
import com.gestogas.gestoline.adapter.adapterRecepcionBitacora;
import com.gestogas.gestoline.controllers.AppController;
import com.gestogas.gestoline.data.dataDispensarios;
import com.gestogas.gestoline.data.dataExtintores;
import com.gestogas.gestoline.utils.Constantes;
import com.gestogas.gestoline.utils.DialogHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class Dispensarios extends BaseActivity {

    private RecyclerView recyclerView;
    private adapterDispensarios adapter;
    private List<dataDispensarios> itemList;
    private RequestQueue requestQueue;
    private int Idestacion;
    ImageView ImgResultado;
    TextView Mensaje;
    FloatingActionButton NuevoDispensario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dispensarios);

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
        NuevoDispensario = findViewById(R.id.NuevoDispensario);

        recyclerView = findViewById(R.id.Recyclerview);

        itemList = new ArrayList<>();
        adapter = new adapterDispensarios(this,itemList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        requestQueue = Volley.newRequestQueue(this);
        fetchDispensarios();

        NuevoDispensario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent home = new Intent (view.getContext(), DispensarioCrearEditar.class);
                home.putExtra("estado","Nuevo");
                home.putExtra("titulo","Crear Dispensario");
                home.putExtra("tituloboton","GUARDAR DISPENSARIO");
                startActivityForResult(home, 1);
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                // Oculta el FAB mientras se hace scroll hacia abajo
                if (dy > 0 && NuevoDispensario.isShown()) {
                    NuevoDispensario.hide();
                }
            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                // Si el usuario deja de hacer scroll, vuelve a mostrar el FAB
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    NuevoDispensario.show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);

        // Usa el SearchView de androidx.appcompat
        SearchView searchView = (SearchView) searchItem.getActionView();

        // Personalizar texto del SearchView
        EditText searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(Color.WHITE);            // Color del texto ingresado
        searchEditText.setHintTextColor(Color.LTGRAY);       // Color del hint

        // Personalizar ícono de lupa
        ImageView searchIcon = searchView.findViewById(androidx.appcompat.R.id.search_mag_icon);
        if (searchIcon != null) {
            searchIcon.setColorFilter(Color.WHITE);
        }

        // Personalizar ícono de cerrar (x)
        ImageView closeIcon = searchView.findViewById(androidx.appcompat.R.id.search_close_btn);
        if (closeIcon != null) {
            closeIcon.setColorFilter(Color.WHITE);
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText);
                return false;
            }
        });

        return true;
    }

    private void fetchDispensarios(){
        DialogHelper.showProgressDialog(this);
        String url = Constantes.URL_SERVIDOR + "Dispensario/lista-dispensario-estacion.php?idEstacion=" + Idestacion;

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

                                String id = jsonObject.getString("id");
                                String nodispensario = jsonObject.getString("nodispensario");
                                String marca = jsonObject.getString("marca");
                                String modelo = jsonObject.getString("modelo");
                                String serie = jsonObject.getString("serie");

                                String nomproducto1 = jsonObject.getString("nomproducto1");
                                String producto1 = jsonObject.getString("producto1");
                                String nomproducto2 = jsonObject.getString("nomproducto2");
                                String producto2 = jsonObject.getString("producto2");
                                String nomproducto3 = jsonObject.getString("nomproducto3");
                                String producto3 = jsonObject.getString("producto3");

                                dataDispensarios item = new dataDispensarios(id,nodispensario,marca,modelo,serie,
                                        nomproducto1,producto1,nomproducto2,producto2,nomproducto3,producto3);
                                itemList.add(item);

                            }

                            // Notifica al adaptador que los datos han cambiado
                            adapter.updateData(itemList);
                            recyclerView.smoothScrollToPosition(0);
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
            fetchDispensarios();
            recyclerView.scrollToPosition(0);
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