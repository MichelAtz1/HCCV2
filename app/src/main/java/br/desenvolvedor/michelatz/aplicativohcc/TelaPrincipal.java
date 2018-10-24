package br.desenvolvedor.michelatz.aplicativohcc;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import br.desenvolvedor.michelatz.aplicativohcc.Utils.VerificaConexao;

public class TelaPrincipal extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    SQLiteDatabase db;
    String BANCO = "banco.db";
    String TABELAUSUARIO = "usuario";
    String TABELACOORDENADAS = "coordenadas";
    String TABELALOCACAO = "locacao";
    String TABELADOCUMENTO = "documento";
    String TABELAPOSTE = "poste";
    String TABELACONSUMIDOR = "consumidor";
    String TABELAEQUIPAMENTO = "equipamento";
    String TABELAESTRUTURA = "estrutura";
    String TABELAFOTOSPOSTE = "fotosposte";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        criarBanco();

        SharedPreferences sharedpreferences = getSharedPreferences(Login.MyPREFERENCES, Context.MODE_PRIVATE);
        String idUsuario = sharedpreferences.getString("idKey", null);

        if(idUsuario == null){
            Intent it = new Intent(this, Login.class);
            startActivity(it);
            finish();
        }

        toolbar.setTitle("Aplicativo da HCC");
        toolbar.setSubtitle("Inicio");
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            Intent it;
            it = new Intent(this, TelaPrincipal.class);
            startActivity(it);
            finish();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        VerificaConexao verConct = new VerificaConexao();
        int id = item.getItemId();
        if (id == R.id.nav_locacoes) {
            Intent it;
            it = new Intent(this, GerenciarLocacoes.class);
            startActivity(it);
            finish();
        } else if (id == R.id.nav_locacoesfinalizadas) {
            Intent it;
            it = new Intent(this, ListaLocacoesFinalizadas.class);
            startActivity(it);
            finish();
        } else if (id == R.id.nav_maps) {
            if (verConct.verificaConexao(this)) {
                Intent it;
                it = new Intent(this, MapsActivity.class);
                startActivity(it);
                finish();
            } else {
                verConct.naoConectouInternet(this);
            }
        }else if (id == R.id.nav_sair) {
            SharedPreferences sharedpreferences = getSharedPreferences(Login.MyPREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.remove("idKey");
            editor.remove("nomeKey");
            editor.remove("emailKey");

            editor.commit();
            editor.clear();

            Intent it;
            it = new Intent(this, Login.class);
            startActivity(it);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void criarBanco(){

        db = openOrCreateDatabase(BANCO, Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABELAUSUARIO +" (" + "ID INTEGER PRIMARY KEY, " +
                "NOME TEXT, " +
                "EMAIL TEXT, " +
                "SENHA TEXT);");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABELACOORDENADAS +" (" + "ID INTEGER PRIMARY KEY, " +
                "LONGITUDE TEXT, " +
                "LATITUDE TEXT, " +
                "IDPOSTE INTEGER);");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABELALOCACAO +" (" + "ID INTEGER PRIMARY KEY, " +
                "NOTA TEXT, " +
                "STATUS TEXT, " +
                "NOME TEXT, " +
                "DATA TEXT);");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABELADOCUMENTO +" (" + "ID INTEGER PRIMARY KEY, " +
                "NOMEDOCUMENTO TEXT, " +
                "IMAGEM BLOB, " +
                "IDLOCACAO INTEGER);");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABELAPOSTE   +" (" + "ID INTEGER PRIMARY KEY, " +
                "TIPOPOSTE TEXT, " +
                "ALTURA TEXT, " +
                "CAPACIDADE TEXT, " +
                "NUMPLACA TEXT, " +
                "ILUMICACAOPUBLICA TEXT, " +
                "TELEFONIA TEXT, " +
                "QUANTIDADE TEXT, " +
                "ACESSO TEXT, " +
                "TIPOSOLO TEXT, " +
                "FINALVIDAUTIL TEXT, " +
                "IDLOCACAO INTEGER);");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABELACONSUMIDOR   +" (" + "ID INTEGER PRIMARY KEY, " +
                "LADO TEXT, " +
                "TIPO TEXT, " +
                "CLASSE TEXT, " +
                "RAMAL TEXT, " +
                "FASE TEXT, " +
                "IDPOSTE INTEGER);");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABELAEQUIPAMENTO  +" (" + "ID INTEGER PRIMARY KEY, " +
                "TIPO TEXT, " +
                "PLACA TEXT, " +
                "TENSAO TEXT, " +
                "DESCRICAO TEXT, " +
                "IDPOSTE INTEGER);");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABELAESTRUTURA  +" (" + "ID INTEGER PRIMARY KEY, " +
                "TIPO TEXT, " +
                "DESCRICAO TEXT, " +
                "CLASSE TEXT, " +
                "NIVEL TEXT, " +
                "CRUZETA TEXT, " +
                "IDPOSTE INTEGER);");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABELAFOTOSPOSTE  +" (" + "ID INTEGER PRIMARY KEY, " +
                "IMAGEM BLOB, " +
                "IDPOSTE INTEGER);");
        db.close();
    }
}
