package com.gestogas.gestoline.mantenimiento;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
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
import com.gestogas.gestoline.adapter.adapterMantenimeinto;
import com.gestogas.gestoline.controllers.AppController;
import com.gestogas.gestoline.data.dataMantenimiento;
import com.gestogas.gestoline.utils.Constantes;
import com.gestogas.gestoline.utils.DialogHelper;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MantenimientoPreventivo extends AppCompatActivity {
    private int Idestacion;

    private RecyclerView recyclerView;
    private adapterMantenimeinto adapter;
    private List<dataMantenimiento> itemList;
    private RequestQueue requestQueue;
    String estado;
    TextView Mensaje;
    ImageView ImgResultado;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mantenimiento_preventivo);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        Idestacion = AppController.getInstance().GetIdestacion();

        TextView RazonSocial = findViewById(R.id.RazonSocial);
        RazonSocial.setText(AppController.getInstance().GetRazonSocial());

        estado = getIntent().getStringExtra("estado");

        Mensaje = findViewById(R.id.Mensaje);
        ImgResultado = findViewById(R.id.ImgResultado);

        recyclerView = findViewById(R.id.Recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        itemList = new ArrayList<>();
        adapter = new adapterMantenimeinto(this,itemList);
        recyclerView.setAdapter(adapter);

        requestQueue = Volley.newRequestQueue(this);
        fetchMantenimiento();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar_search, menu);
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

    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == R.id.action_buscar){
            mostrarBottomSheetBuscar();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void mostrarBottomSheetBuscar() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_buscar_mantenimiento, null);
        bottomSheetDialog.setContentView(view);

        Button opcion1 = view.findViewById(R.id.opcion1);
        Button opcion2 = view.findViewById(R.id.opcion2);

        opcion1.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), MantenimientoPreventivo.class);
            intent.putExtra("estado", "0");
            startActivity(intent);
            finish();

        });

        opcion2.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), MantenimientoPreventivo.class);
            intent.putExtra("estado", "1");
            startActivity(intent);
            finish();

        });

        bottomSheetDialog.show();
    }

    private void fetchMantenimiento() {

        DialogHelper.showProgressDialog(this);
        String url = Constantes.URL_SERVIDOR + "Mantenimiento/lista-mantenimiento-preventivo-estacion.php?idEstacion=" + Idestacion + "&estado=" + estado;

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
                                String idequipo = jsonObject.getString("idequipo");
                                String descripcion = jsonObject.getString("nombreequipo");
                                String fecha = jsonObject.getString("fecha");
                                String hora = jsonObject.getString("hora");
                                String estado = jsonObject.getString("estado");
                                String numverificacion = jsonObject.getString("numverificacion");

                                // Crea un objeto Item y lo añade a la lista
                                dataMantenimiento item = new dataMantenimiento(id, folio,idequipo,descripcion,fecha,hora,estado,numverificacion);
                                itemList.add(item);
                            }

                            // Notifica al adaptador que los datos han cambiado
                            adapter.updateData(itemList);
                            ocultarError();
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
            fetchMantenimiento();
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