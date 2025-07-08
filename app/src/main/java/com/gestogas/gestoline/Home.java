package com.gestogas.gestoline;

import static com.gestogas.gestoline.utils.Constantes.URL_SERVIDOR;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gestogas.gestoline.controllers.AppController;
import com.gestogas.gestoline.mantenimiento.MantenimientoCorrectivo;
import com.gestogas.gestoline.mantenimiento.MantenimientoPreventivo;
import com.gestogas.gestoline.profeco.Profeco;
import com.gestogas.gestoline.recepcion.RecepcionBitacora;
import com.gestogas.gestoline.utils.DialogHelper;
import com.gestogas.gestoline.utils.LocationHelper;
import com.gestogas.gestoline.utils.ToastUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.cardview.widget.CardView;
import android.widget.GridLayout;

import com.gestogas.gestoline.databinding.ActivityHomeBinding;

public class Home extends BaseActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityHomeBinding binding;
    private NavigationView navigationView;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private LocationHelper locationHelper;

    private LinearLayout rootLayout;
    private static final String VERSION_URL = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        locationHelper = new LocationHelper(this);
        rootLayout = findViewById(R.id.root_home);

        String razon_social = AppController.getInstance().GetRazonSocial();
        String permiso_cre = AppController.getInstance().GetPermisoCre();
        String direccion = AppController.getInstance().GetDireccion();
        String usuario = AppController.getInstance().GetNombreUsuario();
        int permisoBitacora = AppController.getInstance().GetPermisoBitacora();
        int confiUbicacion = Integer.parseInt(AppController.getInstance().GetUbicacion());

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarHome.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        if(permisoBitacora == 1){
            MenuItem navEstaciones = navigationView.getMenu().findItem(R.id.nav_estaciones);
            navEstaciones.setVisible(false);
        }

        if (confiUbicacion == 0){
            AlertaUbicacion();
        }else{
            validacionUbicacion();
        }

        View headerView = navigationView.getHeaderView(0);

        ImageView ImgLogo = findViewById(R.id.ImgLogo);
        TextView navRazonSocial = headerView.findViewById(R.id.navRazonSocial);
        TextView navNomUsuario = headerView.findViewById(R.id.navNomUsuario);
        TextView RazonSocial = findViewById(R.id.RazonSocial);
        TextView PermisoCre = findViewById(R.id.PermisoCre);
        TextView Direccion = findViewById(R.id.Direccion);

        Glide.with(this)
                .load(URL_SERVIDOR + "Logo/" + AppController.getInstance().GetLogo())
                .placeholder(R.drawable.logo_gbitacora)
                .error(R.drawable.logo_gbitacora)
                .into(ImgLogo);

        navNomUsuario.setText(usuario);
        navRazonSocial.setText(razon_social);
        RazonSocial.setText(razon_social);
        PermisoCre.setText(permiso_cre);
        Direccion.setText(direccion);

        if (AppController.getInstance().GetConfigProfeco() == 1){
            CardView Profeco = findViewById(R.id.Profeco);
            Profeco.setVisibility(View.VISIBLE);
        }

        GridLayout gridLayout = findViewById(R.id.gridCards);
        int orientation = getResources().getConfiguration().orientation;
        int screenLayout = getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;

        boolean isTablet = screenLayout == Configuration.SCREENLAYOUT_SIZE_LARGE
                || screenLayout == Configuration.SCREENLAYOUT_SIZE_XLARGE;

        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            gridLayout.setColumnCount(isTablet ? 4 : 2); // Tablet: 4 columnas, Celular: 2 columnas
        } else {
            gridLayout.setColumnCount(isTablet ? 2 : 1); // Tablet: 2 columnas, Celular: 1 columna
        }


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_tanques) {

                    Intent intent = new Intent(getApplicationContext(), Tanques.class);
                    startActivity(intent);
                    finish();

                    return true;
                }else if (id == R.id.nav_extintores) {

                    Intent intent = new Intent(getApplicationContext(), Extintores.class);
                    startActivity(intent);
                    finish();

                    return true;
                }else if (id == R.id.nav_dispensarios) {

                    Intent intent = new Intent(getApplicationContext(), Dispensarios.class);
                    startActivity(intent);
                    finish();

                    return true;
                }else if (id == R.id.nav_estaciones) {

                    Intent intent = new Intent(getApplicationContext(), Estaciones.class);
                    startActivity(intent);
                    finish();

                    return true;
                }else if (id == R.id.nav_personal_autorizado) {

                    Intent intent = new Intent(getApplicationContext(), PersonalAutorizado.class);
                    intent.putExtra("categoria", "");
                    startActivity(intent);
                    finish();

                    return true;
                }else if (id == R.id.nav_migbitacora) {

                    Intent intent = new Intent(getApplicationContext(), MiGBitacora.class);
                    startActivity(intent);
                    finish();

                    return true;
                }else if (id == R.id.nav_salir) {

                    DialogHelper.showProgressDialog(Home.this);
                    View rootView = findViewById(android.R.id.content);
                    rootView.postDelayed(this::SalirAplicacion, 2000);

                    return true;
                }

                return false;
            }
            private void SalirAplicacion() {
                AppController.getInstance().CerrarSession();
                int SessionUsuario = AppController.getInstance().GetSession();
                if (SessionUsuario == 0){

                    Intent intent = new Intent (getApplicationContext(), Login.class);
                    startActivity(intent);
                    finish();

                }
            }

        });

    }

    private void AlertaUbicacion(){
        AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_confi_ubicacion,null);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        Button Configuracion = view.findViewById(R.id.btnConfiguracion);
        Configuracion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                Intent intent = new Intent (getApplicationContext(), ConfigurarUbicacion.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
    public void RecepcionBitacora(View view){

        Intent intent = new Intent (getApplicationContext(), RecepcionBitacora.class);
        startActivity(intent);
        finish();

    }
    public void MantenimientoPreventivo(View view){
        Intent intent = new Intent(getApplicationContext(), MantenimientoPreventivo.class);
        intent.putExtra("estado", "0");
        startActivity(intent);
        finish();
    }
    public void MantenimientoCorrectivo(View view){
        Intent intent = new Intent (getApplicationContext(), MantenimientoCorrectivo.class);
        startActivity(intent);
        finish();
    }
    public void ProfecoBitacora(View view){
        Intent intent = new Intent (getApplicationContext(), Profeco.class);
        startActivity(intent);
        finish();
    }

    private void validacionUbicacion(){

        if (locationHelper.checkLocationPermissions()) {
            if (LocationHelper.isLocationEnabled(this)) {
                startLocationUpdates();
            } else {
                checkLocationSettings();
            }
        } else {
            locationHelper.requestLocationPermissions(this, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void startLocationUpdates() {
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();

                AppController.getInstance().LatitudEquipo(String.valueOf(latitude));
                AppController.getInstance().LongitudEquipo(String.valueOf(longitude));

            } else {
                // Si no hay ubicación reciente, inicia actualizaciones en tiempo real
                requestLiveLocation();
            }
        }).addOnFailureListener(e -> {
            // Falló al obtener última ubicación: iniciar actualizaciones
            requestLiveLocation();
        });
    }

    private void requestLiveLocation() {
        locationHelper.startLocationUpdates(new LocationHelper.LocationListener() {
            @Override
            public void onLocationReceived(double latitude, double longitude) {

                AppController.getInstance().LatitudEquipo(String.valueOf(latitude));
                AppController.getInstance().LongitudEquipo(String.valueOf(longitude));

            }
        });
    }

    private void checkLocationSettings() {
        new MaterialAlertDialogBuilder(this) // Usa MaterialAlertDialogBuilder para un diseño moderno
                .setTitle("Localización desactivada") // Título desde strings.xml
                .setMessage("Su ubicación está desactivada.\\nPor favor active su ubicación en la configuración del dispositivo.") // Mensaje desde strings.xml
                .setPositiveButton("Configuración de ubicación", (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                })
                .setNegativeButton("Cancelar", (dialog, which) -> {

                    dialog.dismiss();

                })
                .setCancelable(false) // Evita que el usuario cierre el diálogo tocando fuera de él
                .show(); // Muestra el diálogo
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
            } else {
                ToastUtils.show(this, "Permisos de ubicación denegados", ToastUtils.SUCCESS);
            }
        }
    }


}