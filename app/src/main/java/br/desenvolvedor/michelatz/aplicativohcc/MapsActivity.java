package br.desenvolvedor.michelatz.aplicativohcc;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import br.desenvolvedor.michelatz.aplicativohcc.conexaoWeb.Config;
import br.desenvolvedor.michelatz.aplicativohcc.conexaoWeb.RequestHandler;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String JSON_STRING;
    private Marker marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Aplicativo HCC");
        toolbar.setSubtitle("Mapa das Locações");

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-12.551647, -53.1445545), (float) 3.4));
        getJSON();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(MapsActivity.this, TelaPrincipal.class);
                MapsActivity.this.startActivity(intent);
                finish();
                MapsActivity.super.onBackPressed();

                break;
            default:break;
        }
        return true;
    }

    private void getJSON(){
        class GetJSON extends AsyncTask<Void,Void,String> {

            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(MapsActivity.this,"Buscando Lista de Locações","Aguarde...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                JSON_STRING = s;
                showEmployee();
            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();
                String s = rh.sendGetRequest(Config.URL_GET_PONTOS_LOCACAO);
                return s;
            }
        }
        GetJSON gj = new GetJSON();
        gj.execute();
    }

    private void showEmployee(){
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(JSON_STRING);
            JSONArray result = jsonObject.getJSONArray(Config.TAG_JSON_ARRAY);

            for(int i = 0; i<result.length(); i++){
                JSONObject jo = result.getJSONObject(i);

                String longitude = jo.getString(Config.TAG_LONGITUDE);
                Double longitudeDoub = Double.parseDouble(longitude);
                String latitude = jo.getString(Config.TAG_LATITUDE);
                Double latitudeDoub = Double.parseDouble(latitude);
                String numeroNota = jo.getString(Config.TAG_NUMERO_NOTA);
                marcadores(new LatLng(latitudeDoub, longitudeDoub), "Nota: "+numeroNota);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void marcadores(LatLng latlong, String titulo) {

        MarkerOptions opcoes = new MarkerOptions();
        opcoes.position(latlong).title(titulo).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        marker = mMap.addMarker(opcoes);
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(MapsActivity.this, TelaPrincipal.class);
        MapsActivity.this.startActivity(intent);
        finish();
    }
}
