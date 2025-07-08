package com.gestogas.gestoline;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.gestogas.gestoline.adapter.adapterEstacion;
import com.gestogas.gestoline.controllers.AppController;
import com.gestogas.gestoline.data.dataEstacion;
import com.gestogas.gestoline.utils.Constantes;
import com.gestogas.gestoline.utils.DialogHelper;
import com.gestogas.gestoline.utils.ToastUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Estaciones extends BaseActivity {

    private RecyclerView recyclerView;
    private adapterEstacion adapter;
    private List<dataEstacion> itemList;
    private RequestQueue requestQueue;
    String idgrupo;
    TextView GrupoNombre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_estaciones);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        idgrupo = AppController.getInstance().GetIdGrupo();
        String NombreGrupo = AppController.getInstance().GetNombreGrupo();

        GrupoNombre = findViewById(R.id.GrupoNombre);
        GrupoNombre.setText(NombreGrupo);

        recyclerView = findViewById(R.id.Recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        itemList = new ArrayList<>();
        adapter = new adapterEstacion(this,itemList);
        recyclerView.setAdapter(adapter);

        requestQueue = Volley.newRequestQueue(this);

        DialogHelper.showProgressDialog(this);
        fetchEstacion();


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

    private void fetchEstacion(){
        String url = Constantes.URL_SERVIDOR + Constantes.FOLDER_ADMIN + "lista-estaciones.php?idGrupo=" + idgrupo;;

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
                                int idgas = jsonObject.getInt("IdGas");
                                String permisocre = jsonObject.getString("PermisoCre");
                                String razonsocial = jsonObject.getString("RazonSocial");
                                String direccioncompleta = jsonObject.getString("Direccion");
                                String productouno = jsonObject.getString("ProductoUno");
                                String productodos = jsonObject.getString("ProductoDos");
                                String productotres = jsonObject.getString("ProductoTres");
                                String logo = jsonObject.getString("Logo");
                                String latitud = jsonObject.getString("Latitud");
                                String longitud = jsonObject.getString("Longitud");
                                String distmax = jsonObject.getString("Distmax");

                                // Crea un objeto Item y lo añade a la lista
                                dataEstacion item = new dataEstacion(idgas, permisocre, razonsocial, direccioncompleta, productouno, productodos, productotres,
                                        logo, latitud, longitud, distmax);
                                itemList.add(item);
                            }

                            // Notifica al adaptador que los datos han cambiado
                            adapter.updateData(itemList);
                            DialogHelper.hideProgressDialog();
                        } catch (JSONException e) {
                            ToastUtils.show(Estaciones.this, "No se encontro informacion.", ToastUtils.INFO);
                            DialogHelper.hideProgressDialog();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        DialogHelper.hideProgressDialog();
                        ToastUtils.show(Estaciones.this, "No se encontro informacion.", ToastUtils.INFO);
                    }
                });

        requestQueue.add(jsonArrayRequest);

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