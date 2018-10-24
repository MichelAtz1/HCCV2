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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class CadastrarConsumidores extends AppCompatActivity {

    private RadioGroup grupLado;
    private RadioButton radioDireita, radioEsquerda;

    private Spinner spnD10, spnQ10, spnW01, spnS03, spnQ25, spnW02, spnAF, spnQ35, spnW031, spnW032, spnW033;
    ArrayList<String> spn1 = new ArrayList<String>();

    SQLiteDatabase db;
    String BANCO = "banco.db";
    String TABELACONSUMIDOR = "consumidor";
    private String idPoste, vaiEditar = "0", veioEdicao="0";
    private String idLocacao;
    ArrayAdapter adapter;
    private String tipoEquipamento, numeroPlacaEquipamento, tensao, descricao, idConsumidor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adiciona_consumidores);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Aplicativo HCC");
        toolbar.setSubtitle("Dados do Consumidores");

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        grupLado = (RadioGroup) findViewById(R.id.grupLado);
        radioDireita = (RadioButton) findViewById(R.id.radioDireita);
        radioEsquerda = (RadioButton) findViewById(R.id.radioEsquerda);

        spnD10 = (Spinner) findViewById(R.id.spnD10);
        spnQ10 = (Spinner) findViewById(R.id.spnQ10);
        spnW01 = (Spinner) findViewById(R.id.spnW01);
        spnS03 = (Spinner) findViewById(R.id.spnS03);
        spnQ25 = (Spinner) findViewById(R.id.spnQ25);
        spnW02 = (Spinner) findViewById(R.id.spnW02);
        spnAF = (Spinner) findViewById(R.id.spnAF);
        spnQ35 = (Spinner) findViewById(R.id.spnQ35);
        spnW031 = (Spinner) findViewById(R.id.spnW031);
        spnW032 = (Spinner) findViewById(R.id.spnW032);
        spnW033 = (Spinner) findViewById(R.id.spnW033);

        spn1.add("0");
        spn1.add("1");
        spn1.add("2");
        spn1.add("3");
        spn1.add("4");
        spn1.add("5");
        spn1.add("6");
        spn1.add("7");
        spn1.add("8");
        spn1.add("9");
        spn1.add("10");

        adapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item,spn1);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spnD10.setAdapter(adapter);
        spnQ10.setAdapter(adapter);
        spnW01.setAdapter(adapter);
        spnS03.setAdapter(adapter);
        spnQ25.setAdapter(adapter);
        spnW02.setAdapter(adapter);
        spnAF.setAdapter(adapter);
        spnQ35.setAdapter(adapter);
        spnW031.setAdapter(adapter);
        spnW032.setAdapter(adapter);
        spnW033.setAdapter(adapter);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (getIntent().getStringExtra("USERTELA") != null){
            tipoEquipamento = bundle.getString("tipo");
            numeroPlacaEquipamento = bundle.getString("placa");
            tensao = bundle.getString("tensao");
            descricao = bundle.getString("descricao");
            if (getIntent().getStringExtra("USERTELA").equals("EDITAR")){
                vaiEditar = "1";
                idConsumidor = bundle.getString("id");
                preencheDadosEdicaoConsumidores(idConsumidor);
            }
            if (getIntent().getStringExtra("edit")!= null && getIntent().getStringExtra("edit").equals("1")){
                veioEdicao = "1";
            }
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
                                    Intent intent = new Intent(CadastrarConsumidores.this, CadastrarEquipamento.class);
                                    intent.putExtra("USERTELA","EDITAR");
                                    CadastrarConsumidores.this.startActivity(intent);
                                    finish();
                                }else{
                                    Intent intent = new Intent(CadastrarConsumidores.this, CadastrarEquipamento.class);
                                    intent.putExtra("USERTELA","10");
                                    intent.putExtra("tipo",tipoEquipamento);
                                    intent.putExtra("placa",numeroPlacaEquipamento);
                                    intent.putExtra("descricao",descricao);
                                    intent.putExtra("tensao",tensao);

                                    CadastrarConsumidores.this.startActivity(intent);
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

    public void preencheDadosEdicaoConsumidores(String idConsumidor){
        String lado = null;
        String ramal = null;

        db = openOrCreateDatabase(BANCO, Context.MODE_PRIVATE, null);
        Cursor linhas = db.rawQuery("SELECT * FROM " + TABELACONSUMIDOR +" WHERE ID = "+idConsumidor+";",null);
        if(linhas.moveToFirst()) {
            do{
                lado = linhas.getString(1);
                ramal = linhas.getString(4);
            }
            while(linhas.moveToNext());
            linhas.close();
        }
        db.close();
        if (lado != null) {
            if (lado.equals("E")) {
                grupLado.check(R.id.radioEsquerda);
            } else if (lado.equals("D")) {
                grupLado.check(R.id.radioDireita);
            }
        }

        if(ramal != null) {
            String[] textoSeparado1 = ramal.split(" ");

            for ( int i = 0; i <textoSeparado1.length; i++ ) {
                StringTokenizer st = new StringTokenizer(textoSeparado1[i]);
                String idLocoPrev = st.nextToken(":");
                String idNext = st.nextToken(":");

                if(idLocoPrev.equals("D10")){
                    int spinnerPosition = adapter.getPosition(idNext);
                    spnD10.setSelection(spinnerPosition);
                }
                if(idLocoPrev.equals("S03")){
                    int spinnerPosition = adapter.getPosition(idNext);
                    spnS03.setSelection(spinnerPosition);
                }
                if(idLocoPrev.equals("AF")){
                    int spinnerPosition = adapter.getPosition(idNext);
                    spnAF.setSelection(spinnerPosition);
                }
                if(idLocoPrev.equals("Q10")){
                    int spinnerPosition = adapter.getPosition(idNext);
                    spnQ10.setSelection(spinnerPosition);
                }
                if(idLocoPrev.equals("Q25")){
                    int spinnerPosition = adapter.getPosition(idNext);
                    spnQ25.setSelection(spinnerPosition);
                }
                if(idLocoPrev.equals("Q35")){
                    int spinnerPosition = adapter.getPosition(idNext);
                    spnQ35.setSelection(spinnerPosition);
                }
                if(idLocoPrev.equals("W01")){
                    int spinnerPosition = adapter.getPosition(idNext);
                    spnW01.setSelection(spinnerPosition);
                }
                if(idLocoPrev.equals("W02")){
                    int spinnerPosition = adapter.getPosition(idNext);
                    spnW02.setSelection(spinnerPosition);
                }
                if(idLocoPrev.equals("W03(10mm)")){
                    int spinnerPosition = adapter.getPosition(idNext);
                    spnW031.setSelection(spinnerPosition);
                }
                if(idLocoPrev.equals("W03(16mm)")){
                    int spinnerPosition = adapter.getPosition(idNext);
                    spnW032.setSelection(spinnerPosition);
                }
                if(idLocoPrev.equals("W03(25mm)")){
                    int spinnerPosition = adapter.getPosition(idNext);
                    spnW033.setSelection(spinnerPosition);
                }
            }
        }
    }

    public void salvaConsumidor(View v){
        salvaDadosConsumidor();
    }

    private void salvaDadosConsumidor() {
        SharedPreferences sharedpreferences = getSharedPreferences(Login.MyPREFERENCES, Context.MODE_PRIVATE);
        idPoste = sharedpreferences.getString("idPosteKey", null);
        idLocacao = sharedpreferences.getString("idLocacaoKey", null);

        String lado=" ";
        String RamalCompleto="";

        if(!spnD10.getSelectedItem().toString().equals("0")){
            RamalCompleto = RamalCompleto+"D10:"+spnD10.getSelectedItem().toString()+" ";
        }
        if(!spnS03.getSelectedItem().toString().equals("0")){
            RamalCompleto = RamalCompleto+"S03:"+spnS03.getSelectedItem().toString()+" ";
        }
        if(!spnAF.getSelectedItem().toString().equals("0")){
            RamalCompleto = RamalCompleto+"AF:"+spnAF.getSelectedItem().toString()+" ";
        }
        if(!spnQ10.getSelectedItem().toString().equals("0")){
            RamalCompleto = RamalCompleto+"Q10:"+spnQ10.getSelectedItem().toString()+" ";
        }
        if(!spnQ25.getSelectedItem().toString().equals("0")){
            RamalCompleto = RamalCompleto+"Q25:"+spnQ25.getSelectedItem().toString()+" ";
        }
        if(!spnQ35.getSelectedItem().toString().equals("0")){
            RamalCompleto = RamalCompleto+"Q35:"+spnQ35.getSelectedItem().toString()+" ";
        }
        if(!spnW01.getSelectedItem().toString().equals("0")){
            RamalCompleto = RamalCompleto+"W01:"+spnW01.getSelectedItem().toString()+" ";
        }
        if(!spnW02.getSelectedItem().toString().equals("0")){
            RamalCompleto = RamalCompleto+"W02:"+spnW02.getSelectedItem().toString()+" ";
        }
        if(!spnW031.getSelectedItem().toString().equals("0")){
            RamalCompleto = RamalCompleto+"W03(10mm):"+spnW031.getSelectedItem().toString()+" ";
        }
        if(!spnW032.getSelectedItem().toString().equals("0")){
            RamalCompleto = RamalCompleto+"W03(16mm):"+spnW032.getSelectedItem().toString()+" ";
        }
        if(!spnW033.getSelectedItem().toString().equals("0")){
            RamalCompleto = RamalCompleto+"W03(25mm):"+spnW033.getSelectedItem().toString()+" ";
        }

        int selectedIdLado = grupLado.getCheckedRadioButtonId();
        if (selectedIdLado == radioDireita.getId()) {
            lado = "D";
        } else if (selectedIdLado == radioEsquerda.getId()) {
            lado = "E";
        }

        if(lado.equals(" ")||RamalCompleto.equals("")){
            Toast.makeText(getApplicationContext(), "Por Favor! Insira os dados obrigatórios!!", Toast.LENGTH_SHORT).show();
        }else {
            if(vaiEditar.equals("1")){
                db = openOrCreateDatabase(BANCO, Context.MODE_PRIVATE, null);
                ContentValues values = new ContentValues();
                values.put("LADO", lado);
                values.put("RAMAL", RamalCompleto);
                db.update(TABELACONSUMIDOR, values, "ID=" + idConsumidor, null);
                db.close();
            }else{
                db = openOrCreateDatabase(BANCO, Context.MODE_PRIVATE, null);
                ContentValues values = new ContentValues();
                values.put("LADO", lado);
                values.put("RAMAL", RamalCompleto);
                values.put("IDPOSTE", idPoste);

                db.insert(TABELACONSUMIDOR, null, values);
                db.close();

            }
            if(veioEdicao.equals("1")){
                Intent intent = new Intent(this, CadastrarEquipamento.class);
                intent.putExtra("USERTELA","EDITAR");
                this.startActivity(intent);
                finish();
            }else{
                Intent intent = new Intent(this, CadastrarEquipamento.class);
                intent.putExtra("USERTELA","10");
                intent.putExtra("tipo",tipoEquipamento);
                intent.putExtra("placa",numeroPlacaEquipamento);
                intent.putExtra("descricao",descricao);
                intent.putExtra("tensao",tensao);

                this.startActivity(intent);
                finish();
            }
        }
    }

    @Override
    public void onBackPressed(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Tem certeza que deseja sair desta aba? Os dados ainda não foram salvos");

        alertDialogBuilder.setPositiveButton("Sim",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        if(veioEdicao.equals("1")){
                            Intent intent = new Intent(CadastrarConsumidores.this, CadastrarEquipamento.class);
                            intent.putExtra("USERTELA","EDITAR");
                            CadastrarConsumidores.this.startActivity(intent);
                            finish();
                        }else{
                            Intent intent = new Intent(CadastrarConsumidores.this, CadastrarEquipamento.class);
                            intent.putExtra("USERTELA","10");
                            intent.putExtra("tipo",tipoEquipamento);
                            intent.putExtra("placa",numeroPlacaEquipamento);
                            intent.putExtra("descricao",descricao);
                            intent.putExtra("tensao",tensao);

                            CadastrarConsumidores.this.startActivity(intent);
                            finish();
                        }
                        CadastrarConsumidores.super.onBackPressed();
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
