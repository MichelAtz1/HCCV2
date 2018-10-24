package br.desenvolvedor.michelatz.aplicativohcc;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class CadastrarLocacao extends AppCompatActivity {

    private EditText textNota, textNome;
    SQLiteDatabase db;
    String BANCO = "banco.db";
    String TABELALOCACAO = "locacao";
    private String tipo;

    private String idLocacao;
    private String buscaNota;
    private String buscaCliente ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cadastro_locacao);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        tipo = bundle.getString("tipo");
        textNota = (EditText) findViewById(R.id.textNota);
        textNome = (EditText) findViewById(R.id.textNome);

        if(tipo.equals("2")) {
            buscaLocacao();
        }
    }

    public void salvarNovaLocacao(View v){
        final String numeroNota = textNota.getText().toString().trim();
        final String nomeCliente = textNome.getText().toString().trim();

        if((numeroNota.equals("") || nomeCliente.equals(""))){
            Toast.makeText(CadastrarLocacao.this, "Todos os campos são Obrigatórios", Toast.LENGTH_SHORT).show();
        }else{
            if(tipo.equals("1")) {
                adicionarLocacao(numeroNota, nomeCliente);
            }else if(tipo.equals("2")) {
                editarLocacao(numeroNota, nomeCliente);
            }
        }
    }

    private void adicionarLocacao(final String numeroNota, final String nomeCliente){
        db = openOrCreateDatabase(BANCO, Context.MODE_PRIVATE, null);
        ContentValues values = new ContentValues();
        values.put("NOTA", numeroNota);
        values.put("STATUS", "0");
        values.put("DATA", "00/00/0000");
        values.put("NOME", nomeCliente);

        long ultimoId = db.insert(TABELALOCACAO, null, values);
        db.close();

        String retorno = String.valueOf(ultimoId);
        Toast.makeText(getApplicationContext(), "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show();
        SharedPreferences sharedpreferences = getSharedPreferences(Login.MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("idLocacaoKey",retorno);
        editor.putString("numeroNotaKey", numeroNota);
        editor.commit();

        Intent it;
        it = new Intent(this, GerenciarLocacoes.class);
        startActivity(it);
        finish();
    }

    private void buscaLocacao(){
        SharedPreferences sharedpreferences = getSharedPreferences(Login.MyPREFERENCES, Context.MODE_PRIVATE);
        idLocacao = sharedpreferences.getString("idLocacaoKey", null);

        db = openOrCreateDatabase(BANCO, Context.MODE_PRIVATE, null);
        Cursor linhas = db.rawQuery("SELECT * FROM " + TABELALOCACAO + " WHERE ID = '"+idLocacao+"'",null);

        if(linhas.moveToFirst()){
            do{
                buscaNota = linhas.getString(1);
                buscaCliente = linhas.getString(3);
            }
            while(linhas.moveToNext());
        }
        linhas.close();
        db.close();

        textNota.setText(buscaNota);
        textNome.setText(buscaCliente);
    }

    private void editarLocacao(final String numeroNota, final String nomeCliente){
        db = openOrCreateDatabase(BANCO, Context.MODE_PRIVATE, null);
        db.execSQL("UPDATE "+ TABELALOCACAO + " SET NOTA = '"+numeroNota+"'," + "NOME = '"+nomeCliente+"' WHERE ID = "+"'"+idLocacao+"'");
        db.close();
        Toast.makeText(getApplicationContext(), "Edição realizada com sucesso!", Toast.LENGTH_SHORT).show();
        SharedPreferences sharedpreferences = getSharedPreferences(Login.MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("numeroNotaKey", numeroNota);
        editor.commit();

        Intent it;
        it = new Intent(this, GerenciarLocacoes.class);
        startActivity(it);
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(CadastrarLocacao.this, GerenciarLocacoes.class);
        CadastrarLocacao.this.startActivity(intent);
        finish();
    }

}
