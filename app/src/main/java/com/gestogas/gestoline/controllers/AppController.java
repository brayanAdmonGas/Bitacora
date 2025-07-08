package com.gestogas.gestoline.controllers;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.appcompat.app.AppCompatDelegate;

import com.gestogas.gestoline.utils.Constantes;

public class AppController extends Application {

    private static AppController mInstance;
    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate(){
        super.onCreate();
        mInstance = this;
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public SharedPreferences getSharedPreferences() {
        if (sharedPreferences == null)
            sharedPreferences = super.getSharedPreferences(Constantes.PREFERENCES_NOMBRE, Context.MODE_PRIVATE);
        return sharedPreferences;
    }

    public void LoginUsuario(int idusuario, String nombreusuario, int idestacion, int idpuesto, String idgrupo, String nombregrupo, String permisocre, String razonsocial,String direccion,
                             String productouno, String productodos, String productotres, String logo, String latitud, String longitud, String distmax, String ubicacion, int confiprofeco, int session, String token, int permisbitacora){

        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putInt(Constantes.ID_USUARIO, idusuario);
        editor.putString(Constantes.NOMBRE_USUARIO, nombreusuario);
        editor.putInt(Constantes.ID_ESTACION, idestacion);
        editor.putInt(Constantes.ID_PUESTO, idpuesto);
        editor.putString(Constantes.ID_GRUPO, idgrupo);
        editor.putString(Constantes.NOMBRE_GRUPO, nombregrupo);
        editor.putString(Constantes.PERMISO_CRE, permisocre);
        editor.putString(Constantes.RAZON_SOCIAL,razonsocial);
        editor.putString(Constantes.DIRECCION,direccion);
        editor.putString(Constantes.PRODUCTO_UNO,productouno);
        editor.putString(Constantes.PRODUCTO_DOS, productodos);
        editor.putString(Constantes.PRODUCTO_TRES, productotres);
        editor.putString(Constantes.LOGO_ESTACION, logo);
        editor.putString(Constantes.LATITUD, latitud);
        editor.putString(Constantes.LONGITUD, longitud);
        editor.putString(Constantes.DISTMAX, distmax);
        editor.putString(Constantes.UBICACION, ubicacion);
        editor.putInt(Constantes.CONFIPROFECO, confiprofeco);
        editor.putInt(Constantes.SESSION_USUARIO, session);
        editor.putString(Constantes.TOKEN, token);
        editor.putInt(Constantes.PERMISO_BITACORA, permisbitacora);
        editor.apply();

    }

    public void ActualizarEstacion(int idestacion, String permisocre, String razonsocial,String direccion,
                                   String productouno, String productodos, String productotres, String logo, String latitud, String longitud, String distmax){

        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putInt(Constantes.ID_ESTACION, idestacion);
        editor.putString(Constantes.PERMISO_CRE, permisocre);
        editor.putString(Constantes.RAZON_SOCIAL,razonsocial);
        editor.putString(Constantes.DIRECCION,direccion);
        editor.putString(Constantes.PRODUCTO_UNO,productouno);
        editor.putString(Constantes.PRODUCTO_DOS, productodos);
        editor.putString(Constantes.PRODUCTO_TRES, productotres);
        editor.putString(Constantes.LOGO_ESTACION, logo);
        editor.putString(Constantes.LATITUD, latitud);
        editor.putString(Constantes.LONGITUD, longitud);
        editor.putString(Constantes.DISTMAX, distmax);
        editor.apply();

    }

    public void ActualizarUsuario(int idusuario, String nombreusuario){

        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putInt(Constantes.ID_USUARIO, idusuario);
        editor.putString(Constantes.NOMBRE_USUARIO, nombreusuario);
        editor.apply();

    }

    public void LatitudEquipo(String latitud){
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(Constantes.LATITUD_EQUIPO, latitud);
        editor.apply();
    }

    public void LongitudEquipo(String latitud){
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(Constantes.LONGITUD_EQUIPO, latitud);
        editor.apply();
    }

    public void FirmaRecibe(String urlimagen){
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(Constantes.PERSONAL_RECIBE_FIRMA, urlimagen);
        editor.apply();
    }

    public void FirmaSupervisa(String urlimagen){
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(Constantes.PERSONAL_SUPERVISO_FIRMA, urlimagen);
        editor.apply();
    }


    public void PersonalARDP(int estado){
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putInt(Constantes.PERSONAL_AUTORIZADO_RECEPCION, estado);
        editor.apply();
    }

    public void PersonalAMPC(int estado){
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putInt(Constantes.PERSONAL_AUTORIZADO_MANTENIMIENTO, estado);
        editor.apply();
    }

    public void TanquesAC(int estado){
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putInt(Constantes.TANQUES_ALMACENAMIENTO, estado);
        editor.apply();
    }

    public void CerrarSession() {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.clear();
        editor.apply();
    }

    public void SetUbicacion(){
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(Constantes.UBICACION, "1");
        editor.apply();
    }

    public void SetLatitud(String latitud){
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(Constantes.LATITUD, latitud);
        editor.apply();
    }

    public void SetLongitud(String longitud){
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(Constantes.LONGITUD, longitud);
        editor.apply();
    }

    //-----------------------------------------------------------------------------------------------
    //-----------------------------------------------------------------------------------------------

    public int GetidUsuario() {
        return getSharedPreferences().getInt(Constantes.ID_USUARIO, 0);
    }

    public String GetNombreUsuario() {
        return getSharedPreferences().getString(Constantes.NOMBRE_USUARIO, null);
    }

    public int GetIdestacion() {
        return getSharedPreferences().getInt(Constantes.ID_ESTACION, 0);
    }

    public String GetPermisoCre() {
        return getSharedPreferences().getString(Constantes.PERMISO_CRE, null);
    }

    public String GetRazonSocial() {
        return getSharedPreferences().getString(Constantes.RAZON_SOCIAL, null);
    }

    public String GetDireccion() {
        return getSharedPreferences().getString(Constantes.DIRECCION, null);
    }

    public String GetProductoUno() {
        return getSharedPreferences().getString(Constantes.PRODUCTO_UNO, null);
    }

    public String GetProductoDos() {
        return getSharedPreferences().getString(Constantes.PRODUCTO_DOS, null);
    }

    public String GetProductoTres() {
        return getSharedPreferences().getString(Constantes.PRODUCTO_TRES, null);
    }

    public String GetLogo() {
        return getSharedPreferences().getString(Constantes.LOGO_ESTACION, null);
    }

    public int GetConfigProfeco() {
        return getSharedPreferences().getInt(Constantes.CONFIPROFECO, 0);
    }

    public int GetSession() {
        return getSharedPreferences().getInt(Constantes.SESSION_USUARIO, 0);
    }


    public int GetPersonalARDP() {
        return getSharedPreferences().getInt(Constantes.PERSONAL_AUTORIZADO_RECEPCION, 0);
    }

    public int GetTanquesAlmacenamiento() {
        return getSharedPreferences().getInt(Constantes.TANQUES_ALMACENAMIENTO, 0);
    }

    public int GetPersonalAMPC() {
        return getSharedPreferences().getInt(Constantes.PERSONAL_AUTORIZADO_MANTENIMIENTO, 0);
    }

    public String GetFirmaRecibe() {
        return getSharedPreferences().getString(Constantes.PERSONAL_RECIBE_FIRMA, null);
    }

    public String GetFirmaSupervisa() {
        return getSharedPreferences().getString(Constantes.PERSONAL_SUPERVISO_FIRMA, null);
    }

    public String GetLatitud() {
        return getSharedPreferences().getString(Constantes.LATITUD, "0");
    }

    public String GetLongitud() {
        return getSharedPreferences().getString(Constantes.LONGITUD, "0");
    }

    public String GetDistMax() {
        return getSharedPreferences().getString(Constantes.DISTMAX, "0");
    }

    public String GetUbicacion() {
        return getSharedPreferences().getString(Constantes.UBICACION, "0");
    }

    public String GetLatitudEquipo() {
        return getSharedPreferences().getString(Constantes.LATITUD_EQUIPO, "0");
    }

    public String GetLongitudEquipo() {
        return getSharedPreferences().getString(Constantes.LONGITUD_EQUIPO, "0");
    }

    public int GetIdPuesto() {
        return getSharedPreferences().getInt(Constantes.ID_PUESTO, 0);
    }

    public String GetIdGrupo() {
        return getSharedPreferences().getString(Constantes.ID_GRUPO, null);
    }

    public String GetNombreGrupo() {
        return getSharedPreferences().getString(Constantes.NOMBRE_GRUPO, null);
    }

    public String GetToken() {
        return getSharedPreferences().getString(Constantes.TOKEN, null);
    }

    public int GetPermisoBitacora() {
        return getSharedPreferences().getInt(Constantes.PERMISO_BITACORA, 0);
    }

}
