package br.desenvolvedor.michelatz.aplicativohcc;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

public class CadastrarExtruturas extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private String vaiEditar = "0";
    private String numeroPlaca =" ",  idConsumidor;
    private String telefonia1, telefonia2, telefonia3, telefonia4, vidaUtil, estai, selectedIdAltura, selectedIdIluminacao;
    private String selectedIdAcesso, TipoPosteSelecionado, CapacidadeSelecionado, TipoSoloSelecionado;

    private TextView textDescricaoEstrutura;
    private RadioButton radioPrimaria, radioSecundaria;

    private String idPoste;
    private String selectedIdTipoEstrutura = " ";
    private String veioEdicao="0";

    SQLiteDatabase db;
    String BANCO = "banco.db";
    String TABELAESTRUTURA = "estrutura";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastra_extrutura);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Aplicativo HCC");
        toolbar.setSubtitle("Inserção de Estrutura");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        textDescricaoEstrutura = (EditText) findViewById(R.id.textDescricaoEstrutura);
        radioPrimaria = (RadioButton) findViewById(R.id.radioPrimaria);
        radioSecundaria = (RadioButton) findViewById(R.id.radioSecundaria);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (getIntent().getStringExtra("USERTELA") != null){
            numeroPlaca = bundle.getString("numeroPlaca");
            telefonia1 = bundle.getString("telefonia1");
            telefonia2 = bundle.getString("telefonia2");
            telefonia3 = bundle.getString("telefonia3");
            telefonia4 = bundle.getString("telefonia4");
            estai = bundle.getString("estai");
            selectedIdAltura = bundle.getString("selectedIdAltura");
            selectedIdIluminacao = bundle.getString("selectedIdIluminacao");
            selectedIdAcesso = bundle.getString("selectedIdAcesso");
            TipoPosteSelecionado = bundle.getString("TipoPosteSelecionado");
            CapacidadeSelecionado = bundle.getString("CapacidadeSelecionado");
            TipoSoloSelecionado = bundle.getString("TipoSoloSelecionado");
            vidaUtil = bundle.getString("vidautil");
            if (getIntent().getStringExtra("edit")!= null && getIntent().getStringExtra("edit").equals("1")){
                veioEdicao = "1";
            }
            if (getIntent().getStringExtra("USERTELA").equals("EDITAR")){
                vaiEditar = "1";
                idConsumidor = bundle.getString("id");
                preencheDadosEdicaoExtrutura(idConsumidor);
            }
        }
    }

    private void preencheDadosEdicaoExtrutura(String idConsumidor) {
        String tipo = null;
        String descricao = null;

        db = openOrCreateDatabase(BANCO, Context.MODE_PRIVATE, null);
        Cursor linhas = db.rawQuery("SELECT * FROM " + TABELAESTRUTURA + " WHERE ID = " + idConsumidor + ";", null);
        if (linhas.moveToFirst()) {
            do {
                tipo = linhas.getString(1);
                descricao = linhas.getString(2);
            }
            while (linhas.moveToNext());
            linhas.close();
        }
        db.close();
        if (tipo != null) {
            if (tipo.equals("Primaria")) {
                radioPrimaria.setChecked(true);
                selectedIdTipoEstrutura = "Primaria";
            } else if (tipo.equals("Secundaria")) {
                radioSecundaria.setChecked(true);
                selectedIdTipoEstrutura = "Secundaria";
            }
        }
        if (descricao != null) {
            textDescricaoEstrutura.setText(descricao);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setMessage("Tem certeza que deseja sair desta aba? Os dados ainda não foram salvos");

                alertDialogBuilder.setPositiveButton("Sim",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                if(veioEdicao.equals("1")){
                                    Intent intent = new Intent(CadastrarExtruturas.this, InseriPoste.class);
                                    intent.putExtra("USERTELA","5");
                                    intent.putExtra("numeroPlaca",numeroPlaca);
                                    intent.putExtra("telefonia1",telefonia1);
                                    intent.putExtra("telefonia2",telefonia2);
                                    intent.putExtra("telefonia3",telefonia3);
                                    intent.putExtra("telefonia4",telefonia4);
                                    intent.putExtra("estai",estai);
                                    intent.putExtra("selectedIdAltura",String.valueOf(selectedIdAltura));
                                    intent.putExtra("selectedIdIluminacao",String.valueOf(selectedIdIluminacao));
                                    intent.putExtra("selectedIdAcesso", selectedIdAcesso);
                                    intent.putExtra("TipoPosteSelecionado",TipoPosteSelecionado);
                                    intent.putExtra("CapacidadeSelecionado",CapacidadeSelecionado);
                                    intent.putExtra("TipoSoloSelecionado",TipoSoloSelecionado);
                                    intent.putExtra("vidautil",vidaUtil);
                                    intent.putExtra("edit","1");
                                    CadastrarExtruturas.this.startActivity(intent);
                                    finish();
                                }else{
                                    Intent intent = new Intent(CadastrarExtruturas.this, InseriPoste.class);
                                    intent.putExtra("USERTELA","5");
                                    intent.putExtra("numeroPlaca",numeroPlaca);
                                    intent.putExtra("telefonia1",telefonia1);
                                    intent.putExtra("telefonia2",telefonia2);
                                    intent.putExtra("telefonia3",telefonia3);
                                    intent.putExtra("telefonia4",telefonia4);
                                    intent.putExtra("estai",estai);
                                    intent.putExtra("selectedIdAltura",String.valueOf(selectedIdAltura));
                                    intent.putExtra("selectedIdIluminacao",String.valueOf(selectedIdIluminacao));
                                    intent.putExtra("selectedIdAcesso", selectedIdAcesso);
                                    intent.putExtra("TipoPosteSelecionado",TipoPosteSelecionado);
                                    intent.putExtra("CapacidadeSelecionado",CapacidadeSelecionado);
                                    intent.putExtra("TipoSoloSelecionado",TipoSoloSelecionado);
                                    intent.putExtra("vidautil",vidaUtil);

                                    CadastrarExtruturas.this.startActivity(intent);
                                    finish();
                                }
                            }
                        });

                alertDialogBuilder.setNegativeButton("Não",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {

                            }
                        });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                break;
            default:break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("Tem certeza que deseja sair desta aba? Os dados ainda não foram salvos");

            alertDialogBuilder.setPositiveButton("Sim",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            if(veioEdicao.equals("1")){
                                Intent intent = new Intent(CadastrarExtruturas.this, InseriPoste.class);
                                intent.putExtra("USERTELA","5");
                                intent.putExtra("numeroPlaca",numeroPlaca);
                                intent.putExtra("telefonia1",telefonia1);
                                intent.putExtra("telefonia2",telefonia2);
                                intent.putExtra("telefonia3",telefonia3);
                                intent.putExtra("telefonia4",telefonia4);
                                intent.putExtra("estai",estai);
                                intent.putExtra("selectedIdAltura",String.valueOf(selectedIdAltura));
                                intent.putExtra("selectedIdIluminacao",String.valueOf(selectedIdIluminacao));
                                intent.putExtra("selectedIdAcesso", selectedIdAcesso);
                                intent.putExtra("TipoPosteSelecionado",TipoPosteSelecionado);
                                intent.putExtra("CapacidadeSelecionado",CapacidadeSelecionado);
                                intent.putExtra("TipoSoloSelecionado",TipoSoloSelecionado);
                                intent.putExtra("vidautil",vidaUtil);
                                intent.putExtra("edit","1");
                                CadastrarExtruturas.this.startActivity(intent);
                                finish();
                            }else{
                                Intent intent = new Intent(CadastrarExtruturas.this, InseriPoste.class);
                                intent.putExtra("USERTELA","5");
                                intent.putExtra("numeroPlaca",numeroPlaca);
                                intent.putExtra("telefonia1",telefonia1);
                                intent.putExtra("telefonia2",telefonia2);
                                intent.putExtra("telefonia3",telefonia3);
                                intent.putExtra("telefonia4",telefonia4);
                                intent.putExtra("estai",estai);
                                intent.putExtra("selectedIdAltura",String.valueOf(selectedIdAltura));
                                intent.putExtra("selectedIdIluminacao",String.valueOf(selectedIdIluminacao));
                                intent.putExtra("selectedIdAcesso", selectedIdAcesso);
                                intent.putExtra("TipoPosteSelecionado",TipoPosteSelecionado);
                                intent.putExtra("CapacidadeSelecionado",CapacidadeSelecionado);
                                intent.putExtra("TipoSoloSelecionado",TipoSoloSelecionado);
                                intent.putExtra("vidautil",vidaUtil);

                                CadastrarExtruturas.this.startActivity(intent);
                                finish();
                            }
                        }
                    });

            alertDialogBuilder.setNegativeButton("Não",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {

                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_locacoes) {
            SharedPreferences sharedpreferences = getSharedPreferences(Login.MyPREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.remove("idPosteKey");
            editor.commit();
            editor.clear();

            Intent it;
            it = new Intent(this, GerenciarLocacoes.class);
            startActivity(it);
            finish();
        } else if (id == R.id.nav_locacoesfinalizadas) {
            SharedPreferences sharedpreferences = getSharedPreferences(Login.MyPREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.remove("idPosteKey");
            editor.commit();
            editor.clear();

            Intent it;
            it = new Intent(this, ListaLocacoesFinalizadas.class);
            startActivity(it);
            finish();
        } else if (id == R.id.nav_sair) {
            SharedPreferences sharedpreferences = getSharedPreferences(Login.MyPREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.remove("numeroNotaKey");
            editor.remove("idPosteKey");
            editor.remove("idLocacaoKey");
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

    public void salvarEstrutura(View v){

        salvarEstruturaBanco();
        if(veioEdicao.equals("1")){
            Intent intent = new Intent(CadastrarExtruturas.this, InseriPoste.class);
            intent.putExtra("USERTELA","5");
            intent.putExtra("numeroPlaca",numeroPlaca);
            intent.putExtra("telefonia1",telefonia1);
            intent.putExtra("telefonia2",telefonia2);
            intent.putExtra("telefonia3",telefonia3);
            intent.putExtra("telefonia4",telefonia4);
            intent.putExtra("estai",estai);
            intent.putExtra("selectedIdAltura",String.valueOf(selectedIdAltura));
            intent.putExtra("selectedIdIluminacao",String.valueOf(selectedIdIluminacao));
            intent.putExtra("selectedIdAcesso", selectedIdAcesso);
            intent.putExtra("TipoPosteSelecionado",TipoPosteSelecionado);
            intent.putExtra("CapacidadeSelecionado",CapacidadeSelecionado);
            intent.putExtra("TipoSoloSelecionado",TipoSoloSelecionado);
            intent.putExtra("vidautil",vidaUtil);
            intent.putExtra("edit","1");
            CadastrarExtruturas.this.startActivity(intent);
            finish();
        }else{
            Intent intent = new Intent(this, InseriPoste.class);
            intent.putExtra("USERTELA","5");
            intent.putExtra("numeroPlaca",numeroPlaca);
            intent.putExtra("telefonia1",telefonia1);
            intent.putExtra("telefonia2",telefonia1);
            intent.putExtra("telefonia3",telefonia3);
            intent.putExtra("telefonia4",telefonia4);
            intent.putExtra("estai",estai);
            intent.putExtra("selectedIdAltura",String.valueOf(selectedIdAltura));
            intent.putExtra("selectedIdIluminacao",String.valueOf(selectedIdIluminacao));
            intent.putExtra("selectedIdAcesso", selectedIdAcesso);
            intent.putExtra("TipoPosteSelecionado",TipoPosteSelecionado);
            intent.putExtra("CapacidadeSelecionado",CapacidadeSelecionado);
            intent.putExtra("TipoSoloSelecionado",TipoSoloSelecionado);
            intent.putExtra("vidautil",vidaUtil);

            this.startActivity(intent);
            finish();
        }
    }

    public void salvarEstruturaBanco(){

        SharedPreferences sharedpreferences = getSharedPreferences(Login.MyPREFERENCES, Context.MODE_PRIVATE);
        idPoste = sharedpreferences.getString("idPosteKey", null);

        String descricaoEstru = textDescricaoEstrutura.getText().toString();
        descricaoEstru = descricaoEstru.toUpperCase();

        if(vaiEditar.equals("1")){
            db = openOrCreateDatabase(BANCO, Context.MODE_PRIVATE, null);
            ContentValues values = new ContentValues();
            values.put("TIPO", selectedIdTipoEstrutura);
            values.put("DESCRICAO", descricaoEstru);
            db.update(TABELAESTRUTURA, values, "ID=" + idConsumidor, null);
            db.close();

        }else{
            if (selectedIdTipoEstrutura.equals("Primaria")) {
                db = openOrCreateDatabase(BANCO, Context.MODE_PRIVATE, null);
                ContentValues values = new ContentValues();
                values.put("TIPO", selectedIdTipoEstrutura);
                values.put("DESCRICAO", descricaoEstru);
                values.put("IDPOSTE", idPoste);

                db.insert(TABELAESTRUTURA, null, values);
                db.close();
            } else if (selectedIdTipoEstrutura.equals("Secundaria")) {
                db = openOrCreateDatabase(BANCO, Context.MODE_PRIVATE, null);
                ContentValues values = new ContentValues();
                values.put("TIPO", selectedIdTipoEstrutura);
                values.put("DESCRICAO", descricaoEstru);
                values.put("IDPOSTE", idPoste);

                db.insert(TABELAESTRUTURA, null, values);
                db.close();
            }
        }
    }

    public void clicouPrimaria(View v){
        selectedIdTipoEstrutura = "Primaria";
    }

    public void clicouSecundaria(View v){
        selectedIdTipoEstrutura = "Secundaria";
    }

}
