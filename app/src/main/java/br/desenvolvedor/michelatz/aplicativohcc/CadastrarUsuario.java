package br.desenvolvedor.michelatz.aplicativohcc;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class CadastrarUsuario extends AppCompatActivity {

    private EditText edtNome, edtEmail, edtSenha;
    SQLiteDatabase db;
    String BANCO = "banco.db";
    String TABELAUSUARIO = "usuario";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_usuario);

        edtNome = (EditText) findViewById(R.id.edtNome);
        edtEmail = (EditText) findViewById(R.id.edtEmail);
        edtSenha = (EditText) findViewById(R.id.edtSenha);
    }

    public void salvar(View v){
        final String nome = edtNome.getText().toString().trim();
        final String email = edtEmail.getText().toString().trim();
        final String senha = edtSenha.getText().toString().trim();

        if((nome.equals("") || email.equals("") || senha.equals(""))){
            Toast.makeText(CadastrarUsuario.this, "Todos os campos são Obrigatórios", Toast.LENGTH_SHORT).show();
        }else{
            boolean emailValido = validaEmail(email);
            if(emailValido == true) {
                adicionarUsuario(nome, email, senha);
            }else{
                Toast.makeText(CadastrarUsuario.this, "Por Favor! Insira um E-mail valido!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void adicionarUsuario(final String nome, final String email, final String senha){
        db = openOrCreateDatabase(BANCO, Context.MODE_PRIVATE, null);
        db.execSQL("INSERT INTO "+ TABELAUSUARIO + "(NOME, EMAIL, SENHA) VALUES ('"+nome+"','"+email+"','"+senha+"')");
        db.close();
        Toast.makeText(getApplicationContext(), "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show();

        Intent it;
        it = new Intent(this, Login.class);
        startActivity(it);
        finish();
    }

    private static boolean validaEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    @Override
    public void onBackPressed(){
        Intent it;
        it = new Intent(this, Login.class);
        startActivity(it);
        finish();
        super.onBackPressed();
    }

}
