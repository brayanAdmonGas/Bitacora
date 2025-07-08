package com.gestogas.gestoline.recepcion;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.appcompat.widget.SearchView;

import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import com.gestogas.gestoline.adapter.adapterRecepcionBitacora;
import com.gestogas.gestoline.controllers.AppController;
import com.gestogas.gestoline.data.dataRecepcionBitacora;
import com.gestogas.gestoline.utils.Constantes;
import com.gestogas.gestoline.utils.DialogHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RecepcionBitacora extends AppCompatActivity {

    private RecyclerView recyclerView;
    private adapterRecepcionBitacora adapter;
    private List<dataRecepcionBitacora> itemList;
    private RequestQueue requestQueue;
    private int Idestacion;
    ImageView ImgResultado;
    TextView Mensaje;
    FloatingActionButton NuevaRecepcion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recepcion_bitacora);

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
        NuevaRecepcion = findViewById(R.id.NuevaRecepcion);

        recyclerView = findViewById(R.id.Recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        itemList = new ArrayList<>();
        adapter = new adapterRecepcionBitacora(this,itemList);
        recyclerView.setAdapter(adapter);

        requestQueue = Volley.newRequestQueue(this);
        fetchRecepcionBitacora();


        NuevaRecepcion.setOnClickListener(view -> {
            Intent home = new Intent(view.getContext(), RecepcionBitacoraCrearEditar.class);
            home.putExtra("idRecepcion", "");
            home.putExtra("titulo", "Crear Recepción");
            home.putExtra("tituloboton", "GUARDAR RECEPCIÓN");
            startActivityForResult(home, 1);
        });


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                // Oculta el FAB mientras se hace scroll hacia abajo
                if (dy > 0 && NuevaRecepcion.isShown()) {
                    NuevaRecepcion.hide();
                }
            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                // Si el usuario deja de hacer scroll, vuelve a mostrar el FAB
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    NuevaRecepcion.show();
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


    private void fetchRecepcionBitacora() {
        DialogHelper.showProgressDialog(this);

        String url = Constantes.URL_SERVIDOR + "Recepcion/lista-recepcion-descarga.php?idEstacion=" + Idestacion;

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
                                int id = jsonObject.getInt("id");
                                String folio = jsonObject.getString("folio");
                                String fecha = jsonObject.getString("fecha");
                                String horallegada = jsonObject.getString("horallegada");
                                String horasalida = jsonObject.getString("horasalida");
                                String nofactura = jsonObject.getString("nofactura");
                                String producto = jsonObject.getString("producto");
                                String litroscompra = jsonObject.getString("litroscompra");
                                int estado = jsonObject.getInt("estado");


                                // Crea un objeto Item y lo añade a la lista

                                dataRecepcionBitacora item = new dataRecepcionBitacora(id,folio,fecha,horallegada,horasalida,
                                        nofactura,litroscompra,producto,estado);

                                itemList.add(item);
                            }

                            // Notifica al adaptador que los datos han cambiado
                            adapter.updateData(itemList);
                            ocultarError();
                            recyclerView.smoothScrollToPosition(0);
                            DialogHelper.hideProgressDialog();

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
            fetchRecepcionBitacora();
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