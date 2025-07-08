package com.gestogas.gestoline;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.gestogas.gestoline.adapter.adapterPersonal;
import com.gestogas.gestoline.controllers.AppController;
import com.gestogas.gestoline.data.dataPersonal;
import com.gestogas.gestoline.utils.Constantes;
import com.gestogas.gestoline.utils.DialogHelper;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PersonalAutorizado extends BaseActivity {
    String categoria;
    private RecyclerView recyclerView;
    private adapterPersonal adapter;
    private List<dataPersonal> itemList;
    private RequestQueue requestQueue;
    private int Idestacion;
    ImageView ImgResultado;
    TextView Mensaje;
    private FloatingActionButton NuevoPersonal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_personal_autorizado);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        Idestacion = AppController.getInstance().GetIdestacion();

        categoria = getIntent().getStringExtra("categoria");

        TextView RazonSocial = findViewById(R.id.RazonSocial);
        RazonSocial.setText(AppController.getInstance().GetRazonSocial());

        Mensaje = findViewById(R.id.Mensaje);
        ImgResultado = findViewById(R.id.ImgResultado);
        NuevoPersonal=findViewById(R.id.NuevoPersonal);

        recyclerView = findViewById(R.id.Recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        itemList = new ArrayList<>();
        adapter = new adapterPersonal(this,itemList);
        recyclerView.setAdapter(adapter);

        requestQueue = Volley.newRequestQueue(this);
        fetchPersonalAutorizado();

        NuevoPersonal.setOnClickListener(view -> {
            Intent intent = new Intent(this, PersonalAutorizadoCrear.class);
            intent.putExtra("categoria", categoria);
            startActivityForResult(intent, 1);
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                // Oculta el FAB mientras se hace scroll hacia abajo
                if (dy > 0 && NuevoPersonal.isShown()) {
                    NuevoPersonal.hide();
                }
            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                // Si el usuario deja de hacer scroll, vuelve a mostrar el FAB
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    NuevoPersonal.show();
                }
            }
        });

    }

    private void fetchPersonalAutorizado() {
        DialogHelper.showProgressDialog(this);

        String url = Constantes.URL_SERVIDOR + "Autorizacion/lista-autorizacion.php?idEstacion=" + Idestacion + "&Categoria=" + categoria;

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

                                int idFirma = jsonObject.getInt("idFirma");
                                String nombreusuario = jsonObject.getString("nombreusuario");
                                String categoria = jsonObject.getString("categoria");

                                // Crea un objeto Item y lo añade a la lista
                                dataPersonal item = new dataPersonal(idFirma,nombreusuario,categoria);
                                itemList.add(item);
                            }

                            // Notifica al adaptador que los datos han cambiado
                            adapter.updateData(itemList);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            fetchPersonalAutorizado();
        }
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


        // Listener de búsqueda
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
        View view = getLayoutInflater().inflate(R.layout.dialog_buscar_personal_autorizado, null);
        bottomSheetDialog.setContentView(view);

        // Mostrar primero el BottomSheetDialog
        bottomSheetDialog.show();

        // Luego de mostrar, forzar el ancho completo
        View bottomSheet = bottomSheetDialog.getDelegate().findViewById(com.google.android.material.R.id.design_bottom_sheet);
        if (bottomSheet != null) {
            bottomSheet.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
            bottomSheet.setLayoutParams(bottomSheet.getLayoutParams()); // 👈 importante para aplicar el cambio
        }

        // Configura acciones de botones
        Button opcion1 = view.findViewById(R.id.opcion1);
        Button opcion2 = view.findViewById(R.id.opcion2);

        opcion1.setOnClickListener(v -> irPersonalAutorizado("RDP"));
        opcion2.setOnClickListener(v -> irPersonalAutorizado("MPC"));
    }




    private void irPersonalAutorizado(String categoria){

        Intent intent = new Intent(getApplicationContext(), PersonalAutorizado.class);
        intent.putExtra("categoria", categoria);
        startActivity(intent);
        finish();

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