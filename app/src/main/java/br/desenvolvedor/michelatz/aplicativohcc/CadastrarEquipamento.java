package br.desenvolvedor.michelatz.aplicativohcc;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import br.desenvolvedor.michelatz.aplicativohcc.Adapter.AdapterListViewConsumidores;
import br.desenvolvedor.michelatz.aplicativohcc.ClassesExtras.Helper;
import br.desenvolvedor.michelatz.aplicativohcc.Modelo.DadosGerais;

public class CadastrarEquipamento extends AppCompatActivity {
    private Spinner spnTiposEquipamento;
    ArrayList<String> tipoEquipamento = new ArrayList<String>();

    SQLiteDatabase db;
    String BANCO = "banco.db";
    String TABELAEQUIPAMENTO = "equipamento";
    String TABELACONSUMIDOR = "consumidor";
    String idPoste;
    String primeiraInsercao = "0";

    private AdapterListViewConsumidores adapterListViewConsumidores;
    private TextView textNumeroPlacaEquipamento;
    private ListView listViewConsumidor;

    ArrayAdapter adapter;
    String valorPagina = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consumidor_equipamento);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Aplicativo HCC");
        toolbar.setSubtitle("Dados do Equipamento");

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        listViewConsumidor = (ListView) findViewById(R.id.listViewConsumidor);

        tipoEquipamento.add("Selecione um Tipo");
        tipoEquipamento.add("Capacitor");
        tipoEquipamento.add("Para-Raios");
        tipoEquipamento.add("Chave a Óleo");
        tipoEquipamento.add("Chave - Faca");
        tipoEquipamento.add("Chave - Fusível");
        tipoEquipamento.add("Chave - Fusível - Repetidora");
        tipoEquipamento.add("Regulador de tensão");
        tipoEquipamento.add("Religador");
        tipoEquipamento.add("Transformador");

        SharedPreferences sharedpreferences = getSharedPreferences(Login.MyPREFERENCES, Context.MODE_PRIVATE);
        idPoste = sharedpreferences.getString("idPosteKey", null);

        spnTiposEquipamento = (Spinner) findViewById(R.id.spnTiposEquipamento);
        textNumeroPlacaEquipamento = (TextView) findViewById(R.id.textNumeroPlacaEquipamento);

        adapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item,tipoEquipamento);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnTiposEquipamento.setAdapter(adapter);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (getIntent().getStringExtra("USERTELA") != null){
            if (getIntent().getStringExtra("USERTELA").equals("10")){
                if (getIntent().getStringExtra("placa") != null) {
                    textNumeroPlacaEquipamento.setText(bundle.getString("placa"));
                }
                if (getIntent().getStringExtra("tipo") != null) {
                    int spinnerPosition = adapter.getPosition(bundle.getString("tipo"));
                    spnTiposEquipamento.setSelection(spinnerPosition);
                }
            }
            if (getIntent().getStringExtra("USERTELA").equals("EDITAR")){
                valorPagina = "1";
                preencheEquipamentoEdicao();
            }
        }
        inflaListaConsumidores();
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
                                Intent intent = new Intent(CadastrarEquipamento.this, InseriPoste.class);
                                intent.putExtra("USERTELA","EDITAR");
                                CadastrarEquipamento.this.startActivity(intent);
                                finish();
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

    private void inflaListaConsumidores(){
        ArrayList<DadosGerais> itensDoc = new ArrayList<DadosGerais>();

        db = openOrCreateDatabase(BANCO, Context.MODE_PRIVATE, null);
        Cursor linhas = db.rawQuery("SELECT * FROM " + TABELACONSUMIDOR +" WHERE IDPOSTE = "+idPoste+";",null);
        String texto="";
        if(linhas.moveToFirst()){
            do{
                String idPoste = linhas.getString(0);
                String lado = linhas.getString(1);
                if(lado != null){
                    if(lado.equals("E")){
                        texto="Esquerda";
                    }else if(lado.equals("D")){
                        texto="Direita";
                    }
                }
                DadosGerais itemDoc = new DadosGerais(idPoste,texto);
                itensDoc.add(itemDoc);
            }
            while(linhas.moveToNext());
        }
        adapterListViewConsumidores = new AdapterListViewConsumidores(this, itensDoc);
        listViewConsumidor.setAdapter(adapterListViewConsumidores);
        listViewConsumidor.setCacheColorHint(Color.TRANSPARENT);

        linhas.close();
        Helper.getListViewSize(listViewConsumidor);
        db.close();
    }

    public void irProximoRetirarFoto(View v){
        Intent intent = new Intent(this, Localizacao.class);
        if( valorPagina.equals("1")) {
            updateEquipamento();
            intent.putExtra("USERTELA","EDITAR");
        }else {
            inseriEquipamento();
        }
        this.startActivity(intent);
        finish();
    }

    private void inseriEquipamento() {
        String numeroPlacaEquipamento = textNumeroPlacaEquipamento.getText().toString();
        String TipoPosteSelecionado = spnTiposEquipamento.getSelectedItem().toString();

        if(TipoPosteSelecionado.equals("Selecione um Tipo")){
            TipoPosteSelecionado = "";
        }
        db = openOrCreateDatabase(BANCO, Context.MODE_PRIVATE, null);
        ContentValues values = new ContentValues();
        values.put("TIPO", TipoPosteSelecionado);
        values.put("PLACA", numeroPlacaEquipamento);
        values.put("IDPOSTE", idPoste);
        db.insert(TABELAEQUIPAMENTO, null, values);
        db.close();
    }

    private void updateEquipamento() {
        if(primeiraInsercao.equals("1")){
            inseriEquipamento();
        }else{
            String numeroPlacaEquipamento = textNumeroPlacaEquipamento.getText().toString();
            String TipoPosteSelecionado = spnTiposEquipamento.getSelectedItem().toString();
            if(TipoPosteSelecionado.equals("Selecione um Tipo")){
                TipoPosteSelecionado = "";
            }
            db = openOrCreateDatabase(BANCO, Context.MODE_PRIVATE, null);
            ContentValues values = new ContentValues();
            values.put("TIPO", TipoPosteSelecionado);
            values.put("PLACA", numeroPlacaEquipamento);
            db.update(TABELAEQUIPAMENTO, values, "IDPOSTE=" + idPoste, null);
            db.close();
        }
    }

    public void adicionarConsumidor(View v){
        String numeroPlacaEquipamento = textNumeroPlacaEquipamento.getText().toString();
        String TipoPosteSelecionado = spnTiposEquipamento.getSelectedItem().toString();

        Intent intent = new Intent(this, CadastrarConsumidores.class);
        intent.putExtra("USERTELA","10");
        intent.putExtra("tipo",TipoPosteSelecionado);
        intent.putExtra("placa",numeroPlacaEquipamento);
        if(valorPagina != null && valorPagina.equals("1")){
            intent.putExtra("edit","1");
        }
        this.startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Tem certeza que deseja sair desta aba? Os dados ainda não foram salvos");

        alertDialogBuilder.setPositiveButton("Sim",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent intent = new Intent(CadastrarEquipamento.this, InseriPoste.class);
                        intent.putExtra("USERTELA","EDITAR");
                        CadastrarEquipamento.this.startActivity(intent);
                        finish();
                        CadastrarEquipamento.super.onBackPressed();
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

    public void deletaItem(View v) {
        adapterListViewConsumidores.removeItem((Integer) v.getTag());
        String idMensagem= adapterListViewConsumidores.idSelecionado;
        confirmarDelete(idMensagem);
    }

    public void editaItem(View v) {
        adapterListViewConsumidores.editaItem((Integer) v.getTag());
        String idMensagem= adapterListViewConsumidores.idSelecionado;
        String numeroPlacaEquipamento = textNumeroPlacaEquipamento.getText().toString();
        String TipoPosteSelecionado = spnTiposEquipamento.getSelectedItem().toString();

        Intent intent = new Intent(this, CadastrarConsumidores.class);
        intent.putExtra("USERTELA","EDITAR");
        intent.putExtra("tipo",TipoPosteSelecionado);
        intent.putExtra("placa",numeroPlacaEquipamento);
        intent.putExtra("id",idMensagem);
        if(valorPagina != null && valorPagina.equals("1")){
            intent.putExtra("edit","1");
        }
        this.startActivity(intent);
        finish();
    }

    private void confirmarDelete(final String idMensagem){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Tem certeza que deseja deletar este Consumidor?");

        alertDialogBuilder.setPositiveButton("Sim",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        deletarMensagem(idMensagem);
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

    private void deletarMensagem(final String idMens){
        int idExcluido = Integer.parseInt(idMens.toString());
        db = openOrCreateDatabase(BANCO, Context.MODE_PRIVATE, null);
        db.execSQL("DELETE FROM "+TABELACONSUMIDOR+" WHERE ID = "+idExcluido+"");
        db.close();
        inflaListaConsumidores();
    }

    public void preencheEquipamentoEdicao(){
        String tipoEquipamento = null;
        String numeroPlaca = null;
        db = openOrCreateDatabase(BANCO, Context.MODE_PRIVATE, null);
        Cursor linhas = db.rawQuery("SELECT * FROM " + TABELAEQUIPAMENTO +" WHERE IDPOSTE = "+idPoste+";",null);
        int contador = 0;

        if(linhas.moveToFirst()) {
            do{
                tipoEquipamento = linhas.getString(1);
                numeroPlaca = linhas.getString(2);
                contador++;
            }
            while(linhas.moveToNext());
            linhas.close();
        }
        db.close();
        if(contador==0){
            primeiraInsercao = "1";
        }
        if (numeroPlaca != null) {
            textNumeroPlacaEquipamento.setText(numeroPlaca);
        }
        if (tipoEquipamento != null) {
            int spinnerPosition2 = adapter.getPosition(tipoEquipamento);
            spnTiposEquipamento.setSelection(spinnerPosition2);
        }
    }
}
