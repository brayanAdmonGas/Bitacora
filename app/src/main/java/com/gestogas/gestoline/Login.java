package com.gestogas.gestoline;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.gestogas.gestoline.controllers.AppController;
import com.gestogas.gestoline.utils.Constantes;
import com.gestogas.gestoline.utils.DialogHelper;
import com.gestogas.gestoline.utils.TecladoUtils;
import com.gestogas.gestoline.utils.ToastUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.messaging.FirebaseMessaging;


import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Login extends BaseActivity {

    private TextInputEditText InputUsuario, InputPassword;
    private MaterialButton BtnEntrar;
    private String url, token_usuario;

    private static final String TAG = "LoginActivity";

    @SuppressLint("SuspiciousIndentation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        InputUsuario = findViewById(R.id.InputUsuario);
        InputPassword = findViewById(R.id.InputPassword);
        BtnEntrar = findViewById(R.id.BtnEntrar);

        SharedPreferences prefs = getSharedPreferences("mi_usuario", MODE_PRIVATE);
        String usuarioGuardado = prefs.getString("usuario", "");

        InputUsuario.setText(usuarioGuardado);

        url = Constantes.URL_SERVIDOR + Constantes.FOLDER_LOGIN + "login-app.php";

              FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        token_usuario  = task.getResult();

                    }
                });

        BtnEntrar.setOnClickListener(view -> {
            validaCredenciales();
        });

    }

    private void validaCredenciales(){

        if (Objects.requireNonNull(InputUsuario.getText()).toString().isEmpty()){
            InputUsuario.setError("Falta agregar el usuario");
            ToastUtils.show(this, "Falta agregar el usuario", ToastUtils.INFO);
        }else{

            if (Objects.requireNonNull(InputPassword.getText()).toString().isEmpty()){
                InputPassword.setError("Falta agregar la contraseña");
                ToastUtils.show(this, "Falta agregar la contraseña", ToastUtils.INFO);
            }else{
                InputUsuario.setEnabled(false);
                InputPassword.setEnabled(false);
                BtnEntrar.setEnabled(false);

                   DialogHelper.showProgressDialog(this);
                   LoginUsuario();

            }
        }

    }

    private void LoginUsuario() {

        final String nomusuario = Objects.requireNonNull(InputUsuario.getText()).toString().trim();
        final String password = Objects.requireNonNull(InputPassword.getText()).toString().trim();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String res) {
                        //Manejar la respuesta

                        String nombreusuario= "", idgrupo = "", nombregrupo = "", permisocre = "", razonsocial = "", direccion = "", productouno = "", productodos = "", productotres = "",
                                logo = "", latitud = "", longitud = "", distmax = "", ubicacion = "";
                        int idusuario = 0, idestacion = 0, idpuesto = 0, confiprofeco = 0, code = 0, permisbitacora = 0;

                       try {

                           JSONArray jsonArray = new JSONArray(res);
                           if (jsonArray.length() > 0) {
                               JSONObject response = jsonArray.getJSONObject(0);

                               idusuario = response.getInt("IdUsuario");
                               nombreusuario = response.getString("NombreUsuario");

                               idestacion = response.getInt("IdGas");

                               idpuesto = response.getInt("IdPuesto");
                               idgrupo = response.getString("IdGrupo");
                               nombregrupo = response.getString("NombreGrupo");

                               permisocre = response.getString("PermisoCre");
                               razonsocial = response.getString("RazonSocial");
                               direccion = response.getString("Direccion");
                               productouno = response.getString("ProductoUno");
                               productodos = response.getString("ProductoDos");
                               productotres = response.getString("ProductoTres");
                               logo = response.getString("Logo");
                               latitud = response.getString("Latitud");
                               longitud = response.getString("Longitud");
                               distmax = response.getString("Distmax");
                               ubicacion = response.getString("Ubicacion");
                               confiprofeco = response.getInt("ConfiProfeco");
                               permisbitacora = response.getInt("Permiso");
                               code = response.getInt("code");
                           }

                           if (code == 200){

                               AppController.getInstance().LoginUsuario(idusuario, nombreusuario, idestacion,idpuesto, idgrupo, nombregrupo, permisocre, razonsocial, direccion, productouno, productodos, productotres, logo, latitud, longitud, distmax, ubicacion, confiprofeco, 1,token_usuario, permisbitacora);

                               if (permisbitacora == 2){

                                   Intent intent = new Intent(getApplicationContext(), Estaciones.class);
                                   startActivity(intent);
                                   finish();

                               }else if (permisbitacora == 1){

                                   Intent intent = new Intent(getApplicationContext(), Home.class);
                                   startActivity(intent);
                                   finish();

                               }

                               SharedPreferences prefs = getSharedPreferences("mi_usuario", MODE_PRIVATE);
                               SharedPreferences.Editor editor = prefs.edit();
                               editor.putString("usuario", nomusuario);
                               editor.apply();

                               DialogHelper.hideProgressDialog();

                           }else{
                               InputUsuario.setEnabled(true);
                               InputPassword.setEnabled(true);
                               BtnEntrar.setEnabled(true);
                               DialogHelper.hideProgressDialog();
                               ToastUtils.show(Login.this, "Usuario o contraseña incorrectos", ToastUtils.INFO);
                           }

                       } catch (JSONException  e) {

                           InputUsuario.setEnabled(true);
                           InputPassword.setEnabled(true);
                           BtnEntrar.setEnabled(true);
                           DialogHelper.hideProgressDialog();
                           ToastUtils.show(Login.this, "El usuario o contraseña no tiene permisos de acceso.", ToastUtils.INFO);

                       }

                    }
                },
                new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error.networkResponse != null) {
                    // Obtener el código de estado HTTP
                    int statusCode = error.networkResponse.statusCode;

                    // Obtener el cuerpo del error (si está disponible)
                    String errorBody = new String(error.networkResponse.data);
                    ToastUtils.show(Login.this, errorBody, ToastUtils.INFO);

                } else {
                    // Error de red o tiempo de espera agotado
                    System.out.println();
                    ToastUtils.show(Login.this, "Se produjo un error, revise su conexión a internet", ToastUtils.INFO);
                }

                InputUsuario.setEnabled(true);
                InputPassword.setEnabled(true);
                BtnEntrar.setEnabled(true);
                DialogHelper.hideProgressDialog();

            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("nomusuario", nomusuario);
                params.put("password", password);
                params.put("token_usuario", token_usuario);
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DialogHelper.hideProgressDialog();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        TecladoUtils.handleTouchEvent(this, event);
        return super.dispatchTouchEvent(event);
    }

}