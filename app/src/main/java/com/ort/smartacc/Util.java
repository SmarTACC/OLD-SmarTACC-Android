package com.ort.smartacc;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Esta clase está pensaba para meter funciones que pueden ser utiles para distintas ocaciones.
 * La idea es evitar escribirlas en más de una clase.
 */
public class Util {
    /**
     * Dirección básica del servidor.
     */
    //TODO: cambiar esto por la url de ORT
    static String SERVER_URL = "http://santiaranguri.com/";
    /**
     * Función que verifica el poder conectarse a internet
     * @param ctx El contexto usado para conseguir el servicio para checkear el estado de la red.
     * @return boolean, true en caso de poder conectarse una red, false en el caso contrario.
     */
    public static boolean canConnect(Context ctx){
        ConnectivityManager cm =
                (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}
