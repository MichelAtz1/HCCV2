package br.desenvolvedor.michelatz.aplicativohcc;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends AppCompatActivity{


    private EditText edtEmailUsuario;
    private EditText edtSenhaUsuario;

    SQLiteDatabase db;
    String BANCO = "banco.db";
    String TABELAUSUARIO = "usuario";

    public static final String MyPREFERENCES = "MinhasPreferencias" ;
    public static final String idUsuarioPref = "idKey";
    public static final String nomeUsuarioPref = "nomeKey";
    public static final String emailUsuarioPref = "emailKey";
    public static final String idLocacaoPref = "idLocacaoKey";
    public static final String idPostePref = "idPosteKey";
    public static final String numeroNotaPref = "numeroNotaKey";

    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtEmailUsuario = (EditText) findViewById(R.id.edtEmailUsuario);
        edtSenhaUsuario = (EditText) findViewById(R.id.edtSenhaUsuario);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
    }

    public void botaoCadastrarUsuario(View v){
            Intent it;
            it = new Intent(this, CadastrarUsuario.class);
            startActivity(it);
            finish();
    }

    public void entrarAplicativo(View v){
        final String email = edtEmailUsuario.getText().toString();
        final String senha = edtSenhaUsuario.getText().toString();

        if(email.equals("") || senha.equals("")){
            Toast.makeText(getApplicationContext(), "Por favor, informe e-mail e senha para login!", Toast.LENGTH_SHORT).show();
        }else{
            buscarSQL(email, senha);
        }
    }

    public void buscarSQL(String email, String senha){
        edtEmailUsuario.setText("");
        edtSenhaUsuario.setText("");

        db = openOrCreateDatabase(BANCO, Context.MODE_PRIVATE, null);
        Cursor linhas = db.rawQuery("SELECT * FROM " + TABELAUSUARIO + " WHERE email = '"+email+"' AND senha ='"+senha+"' ",null);
        if(linhas.moveToFirst()){ //retorna false se nao ha linhas na tabela

            SharedPreferences.Editor editor = sharedpreferences.edit();

            int id = linhas.getInt(0);
            String nomeUsuario = linhas.getString(1);
            String emailUsuario = linhas.getString(2);

            editor.putString(idUsuarioPref, String.valueOf(id));
            editor.putString(nomeUsuarioPref, nomeUsuario);
            editor.putString(emailUsuarioPref, emailUsuario);
            editor.commit();

            Intent it;
            it = new Intent(this, TelaPrincipal.class);
            startActivity(it);
            finish();
        }
        else
            Toast.makeText(getApplicationContext(), "Email e/ou Senha Incorretos!", Toast.LENGTH_SHORT).show();
        db.close();
    }
}

