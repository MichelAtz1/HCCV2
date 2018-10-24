package br.desenvolvedor.michelatz.aplicativohcc.Utils;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class VerificaConexao {

    public  boolean verificaConexao(Context contexto) {
        boolean conectado;
        ConnectivityManager conectivtyManager = (ConnectivityManager) contexto.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conectivtyManager.getActiveNetworkInfo() != null
                && conectivtyManager.getActiveNetworkInfo().isAvailable()
                && conectivtyManager.getActiveNetworkInfo().isConnected()) {
            conectado = true;
        } else {
            conectado = false;
        }
        return conectado;
    }

    public boolean verificaAcessoInternet(Context context) {
        if (verificaConexao(context)) {
            try {
                HttpURLConnection urlc = (HttpURLConnection) (new URL("http://clients3.google.com/generate_204").openConnection());
                urlc.setRequestProperty("User-Agent", "Android");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(1500);
                urlc.connect();
                return (urlc.getResponseCode() == 204 &&
                        urlc.getContentLength() == 0);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public void naoConectouInternet(Context context){
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(context);
        alertDialogBuilder.setTitle("Sem acesso a Internet");
        alertDialogBuilder.setMessage("Para entrar na aba dos Mapas, seu celular precisa estar conectado a internet.\n\n Por Favor!\n Conecte-se a internet e tente novamente!");

        alertDialogBuilder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });
        android.app.AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void falhaAcessoServidor(Context context){
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(context);
        alertDialogBuilder.setTitle("Falha no Envio de Dados");
        alertDialogBuilder.setMessage("O envio de dados ao servidor falhou!\n\n Por Favor!\n Verifique seu pacotes de dados ou seu modem de distribuição Wi-Fi e tente novamente!");
        alertDialogBuilder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });
        android.app.AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

}
