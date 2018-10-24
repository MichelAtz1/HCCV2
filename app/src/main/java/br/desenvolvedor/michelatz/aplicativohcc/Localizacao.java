package br.desenvolvedor.michelatz.aplicativohcc;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import br.desenvolvedor.michelatz.aplicativohcc.Adapter.AdapterListViewGeral;
import br.desenvolvedor.michelatz.aplicativohcc.ClassesExtras.Helper;
import br.desenvolvedor.michelatz.aplicativohcc.Modelo.DadosGerais;

public class Localizacao extends AppCompatActivity{

    private static final int REQUEST_CAMERA_PERMISSION = 200;

    Uri outputUri;
    String caminhoImagem = "inicial";
    File fileFinal;
    private String imgString;
    SQLiteDatabase db;
    String BANCO = "banco.db";
    String TABELAFOTOSPOSTE = "fotosposte";
    private String idPoste;
    String valorPagina="0";

    private ListView listViewFotosPostes;
    private ArrayList<DadosGerais> itens;
    private AdapterListViewGeral adapterListViewFotosPoste;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_localizacao);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Aplicativo HCC");
        toolbar.setSubtitle("Imagem do poste");

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        SharedPreferences sharedpreferences = getSharedPreferences(Login.MyPREFERENCES, Context.MODE_PRIVATE);
        idPoste = sharedpreferences.getString("idPosteKey", null);

        listViewFotosPostes = (ListView) findViewById(R.id.listViewFotosPostes);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if (getIntent().getStringExtra("USERTELA") != null){
            if (getIntent().getStringExtra("USERTELA").equals("EDITAR")){
                valorPagina = "1";
            }
        }
        inflaListaFotosPostes();
    }

    private void inflaListaFotosPostes() {
        itens = new ArrayList<DadosGerais>();

        db = openOrCreateDatabase(BANCO, Context.MODE_PRIVATE, null);
        Cursor linhas = db.rawQuery("SELECT ID FROM " + TABELAFOTOSPOSTE +" WHERE IDPOSTE = "+idPoste+";",null);
        int contador=1;
        if(linhas.moveToFirst()){
            do{
                String idPoste = linhas.getString(0);
                String texto = "Foto "+contador;

                DadosGerais item1 = new DadosGerais(idPoste,texto);
                itens.add(item1);
                contador++;
            }
            while(linhas.moveToNext());
        }
        adapterListViewFotosPoste = new AdapterListViewGeral(this, itens);
        listViewFotosPostes.setAdapter(adapterListViewFotosPoste);
        listViewFotosPostes.setCacheColorHint(Color.TRANSPARENT);

        linhas.close();
        Helper.getListViewSize(listViewFotosPostes);
        db.close();
    }

    public void salvarLocalizacao(View v){

        Toast.makeText(getApplicationContext(), "Poste Inserido com sucesso!", Toast.LENGTH_SHORT).show();
        SharedPreferences sharedpreferences = getSharedPreferences(Login.MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.remove("idPosteKey");

        editor.commit();
        editor.clear();

        Intent it;
        it = new Intent(this, GerenciarLocacoes.class);
        startActivity(it);
        finish();

    }

    public void inseriImagem(View v){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Localizacao.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA_PERMISSION);
            return;
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            outputUri = getTempCameraUri();

            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
            startActivityForResult(intent, 1);
        }else{
            /*
            Intent it = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(it, 0);
            */
            Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (it.resolveActivity(getPackageManager()) != null) {
                File arquivoFoto = null;
                try {
                    arquivoFoto = criaArquivoImagem();
                } catch (IOException ex) {

                }
                if (arquivoFoto != null) {
                    it.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(arquivoFoto));
                    startActivityForResult(it, 0);
                }
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Toast.makeText(Localizacao.this, "Desculpe!!! você não pode usar este aplicativo sem conceder permissão!", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (resultCode == RESULT_OK) {
                Bitmap bitmap = null;
                if (outputUri == null) {
                    return;
                }
                if (outputUri != null) {
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), outputUri);
                        Bitmap tamanhoReduzidoImagem;
                        tamanhoReduzidoImagem = Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() * 0.3), (int) (bitmap.getHeight() * 0.3), true);
                        imgString = Base64.encodeToString(getBytesFromBitmap(tamanhoReduzidoImagem), Base64.NO_WRAP);
                        salvaFotoPosteBanco();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Você NÃO inseriu a foto!", Toast.LENGTH_SHORT).show();
            }
        }else{
            if (resultCode == RESULT_OK) {
                //Bitmap bp = (Bitmap) data.getExtras().get("data");
                //imgString = encodeToBase64(bp, Bitmap.CompressFormat.JPEG, 100);

                    File arquivoImagem = new File(caminhoImagem);
                    if (arquivoImagem.exists()) {
                        Bitmap imagemBitmap = BitmapFactory.decodeFile(
                                arquivoImagem.getAbsolutePath());

                        Bitmap tamanhoReduzidoImagem;
                        tamanhoReduzidoImagem = Bitmap.createScaledBitmap(imagemBitmap,(int)(imagemBitmap.getWidth()*0.5), (int)(imagemBitmap.getHeight()*0.5), true);
                        imgString = Base64.encodeToString(getBytesFromBitmap(tamanhoReduzidoImagem), Base64.NO_WRAP);
                        salvaFotoPosteBanco();
                    }
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Você NÃO inseriu a foto!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private File criaArquivoImagem() throws IOException {
        SharedPreferences sharedpreferences = getSharedPreferences(Login.MyPREFERENCES, Context.MODE_PRIVATE);
        String numeroNotaSelect = sharedpreferences.getString("numeroNotaKey", null);
        String horarioSistema = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String nomeArquivoImagem = "JPEG_" + numeroNotaSelect +"_"+horarioSistema + "_";

        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/HCC/";

        File dir = new File(path);
        if(!dir.exists())
            dir.mkdirs();

        String path2 = Environment.getExternalStorageDirectory().getAbsolutePath() + "/HCC/"+numeroNotaSelect;
        File dir2 = new File(path2);
        if(!dir2.exists())
            dir2.mkdirs();

        File imagem = File.createTempFile(nomeArquivoImagem,".jpg",dir2);
        caminhoImagem = imagem.getAbsolutePath();

        return imagem;
    }

    public static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality) {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }

    public byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private Uri getTempCameraUri() {
        try {
            SharedPreferences sharedpreferences = getSharedPreferences(Login.MyPREFERENCES, Context.MODE_PRIVATE);
            String numeroNotaSelect = sharedpreferences.getString("numeroNotaKey", null);
            String horarioSistema = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String nomeArquivoImagem = "JPEG_" + numeroNotaSelect +"_"+horarioSistema + "_";
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/HCC/";

            File dir = new File(path);
            if(!dir.exists())
                dir.mkdirs();

            String path2 = Environment.getExternalStorageDirectory().getAbsolutePath() + "/HCC/"+numeroNotaSelect;
            File dir2 = new File(path2);
            if(!dir2.exists())
                dir2.mkdirs();

            fileFinal = File.createTempFile(nomeArquivoImagem, ".jpg", dir2);
            Uri photoURI2 = FileProvider.getUriForFile(Localizacao.this,
                    BuildConfig.APPLICATION_ID + ".provider",
                    fileFinal);

            return photoURI2;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Tem certeza que deseja sair desta aba? Os dados ainda não foram salvos");

        alertDialogBuilder.setPositiveButton("Sim",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent intent = new Intent(Localizacao.this, CadastrarEquipamento.class);
                        intent.putExtra("USERTELA","EDITAR");
                        Localizacao.this.startActivity(intent);
                        finish();
                        Localizacao.super.onBackPressed();
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
        adapterListViewFotosPoste.removeItem((Integer) v.getTag());
        adapterListViewFotosPoste.notifyDataSetChanged();
        String idMensagem= adapterListViewFotosPoste.idSelecionado;
        confirmarDelete(idMensagem);
    }

    private void confirmarDelete(final String idMensagem){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Tem certeza que deseja deletar esta Foto?");

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
        db.execSQL("DELETE FROM "+TABELAFOTOSPOSTE+" WHERE ID = "+idExcluido+"");
        db.close();

        inflaListaFotosPostes();
    }

    private void salvaFotoPosteBanco(){
        db = openOrCreateDatabase(BANCO, Context.MODE_PRIVATE, null);
        db.execSQL("INSERT INTO " + TABELAFOTOSPOSTE + "(IMAGEM, IDPOSTE) VALUES ('" + imgString + "','" + idPoste + "')");
        db.close();
        inflaListaFotosPostes();
        Toast.makeText(getApplicationContext(), "Inserção realizada com sucesso!", Toast.LENGTH_SHORT).show();
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
                                Intent intent = new Intent(Localizacao.this, CadastrarEquipamento.class);
                                intent.putExtra("USERTELA","EDITAR");
                                Localizacao.this.startActivity(intent);
                                finish();
                                Localizacao.super.onBackPressed();
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
}