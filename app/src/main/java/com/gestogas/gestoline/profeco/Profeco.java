package com.gestogas.gestoline.profeco;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import com.gestogas.gestoline.Home;
import com.gestogas.gestoline.R;
import com.gestogas.gestoline.adapter.adapterProfeco;
import com.gestogas.gestoline.controllers.AppController;
import com.gestogas.gestoline.data.dataProfeco;
import com.gestogas.gestoline.utils.Constantes;
import com.gestogas.gestoline.utils.DialogHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Profeco extends AppCompatActivity {

    private RecyclerView recyclerView;
    private adapterProfeco adapter;
    private List<dataProfeco> itemList;
    private RequestQueue requestQueue;
    private int Idestacion;
    ImageView ImgResultado;
    TextView Mensaje;
    FloatingActionButton NuevoRegistro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profeco);

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
        NuevoRegistro = findViewById(R.id.NuevoRegistro);

        recyclerView = findViewById(R.id.Recyclerview);

        itemList = new ArrayList<>();
        adapter = new adapterProfeco(this,itemList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        requestQueue = Volley.newRequestQueue(this);
        fetchDispensarios();

        NuevoRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent home = new Intent (view.getContext(), ProfecoCrearEditar.class);
                home.putExtra("estado","Nuevo");
                home.putExtra("titulo","Crear Bitácora PROFECO");
                home.putExtra("tituloboton","GUARDAR BITACORA PROFECO");
                startActivityForResult(home, 1);


            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                // Oculta el FAB mientras se hace scroll hacia abajo
                if (dy > 0 && NuevoRegistro.isShown()) {
                    NuevoRegistro.hide();
                }
            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                // Si el usuario deja de hacer scroll, vuelve a mostrar el FAB
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    NuevoRegistro.show();
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
        String url = Constantes.URL_SERVIDOR + "Dispensario/lista-bitacora-dispensario.php?idEstacion=" + Idestacion;

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
                                    String folio = jsonObject.getString("folio");
                                    String fecha = jsonObject.getString("fecha");

                                    String hora = jsonObject.getString("hora");
                                    String nodispensario = jsonObject.getString("nodispensario");
                                    String productobitacora = jsonObject.getString("producto");
                                    String lado = jsonObject.getString("lado");
                                    String motivo = jsonObject.getString("motivo");
                                    String nombre = jsonObject.getString("nombre");
                                    String observaciones = jsonObject.getString("observaciones");
                                    int estado = jsonObject.getInt("estado");

                                    dataProfeco item = new dataProfeco(id, nodispensario, folio, fecha, hora, productobitacora, lado, motivo, nombre, observaciones, estado);
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
                        itemList.clear();
                        adapter.clearData();
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