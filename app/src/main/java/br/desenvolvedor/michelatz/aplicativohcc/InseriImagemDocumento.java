package br.desenvolvedor.michelatz.aplicativohcc;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class InseriImagemDocumento extends AppCompatActivity {
    private static final int REQUEST_CAMERA_PERMISSION = 200;

    private Spinner spnNomeDocumentos;
    ArrayList<String> tipoDocumento = new ArrayList<String>();

    String nomeDocumento;
    SQLiteDatabase db;
    String BANCO = "banco.db";
    String TABELADOCUMENTO = "documento";
    Uri outputUri;
    File fileFinal;
    private String imgString;
    String caminhoImagem = "inicial";
    static final int CODIGO_REQUISICAO = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inseri_imagem_documento);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        spnNomeDocumentos = (Spinner) findViewById(R.id.spnNomeDocumentos);

        tipoDocumento.add("Selecione um Documento");
        tipoDocumento.add("Autorização de Passagem");
        tipoDocumento.add("Cadastro de Cliente");
        tipoDocumento.add("Carta de Apresentação do Projeto");
        tipoDocumento.add("Croqui");
        tipoDocumento.add("Formulário de Acompanhamento");
        tipoDocumento.add("Licenciamento Ambiental");
        tipoDocumento.add("Termo de Garantia");

        ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item,tipoDocumento);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnNomeDocumentos.setAdapter(adapter);

        spnNomeDocumentos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                nomeDocumento = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Another interface callback
            }
        });
    }

    public void foto(View v){
        if(nomeDocumento.equals("Selecione um Documento")){
            Toast.makeText(InseriImagemDocumento.this,"Por Favor! Selecione o nome do Arquivo!", Toast.LENGTH_SHORT).show();
        }else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(InseriImagemDocumento.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA_PERMISSION);
                return;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                outputUri = getTempCameraUri();
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
                startActivityForResult(intent, 1);
            }else{
                //Intent it = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                //startActivityForResult(it, 0);

/*
                File file = new File(Environment.getExternalStorageDirectory() + "/arquivo.jpg");
                Uri outputFileUri = Uri.fromFile(file);
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);

                File outputUri2 = null;
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                try {
                    outputUri2 = criaArquivoImagem();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri2);
                startActivityForResult(intent, 0);

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
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(InseriImagemDocumento.this, "Desculpe!!! você não pode usar este aplicativo sem conceder permissão!", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        ImageView iv = (ImageView) findViewById(R.id.foto);
        Bitmap bitmap = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (outputUri == null){
                return;
            }
            if(outputUri != null){
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), outputUri);
                    Bitmap tamanhoReduzidoImagem;
                    tamanhoReduzidoImagem = Bitmap.createScaledBitmap(bitmap,(int)(bitmap.getWidth()*0.3), (int)(bitmap.getHeight()*0.3), true);
                    imgString = Base64.encodeToString(getBytesFromBitmap(tamanhoReduzidoImagem), Base64.NO_WRAP);
                    iv.setImageBitmap(bitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }else{
            if (resultCode == RESULT_OK) {
                if (requestCode == CODIGO_REQUISICAO) {

                    File arquivoImagem = new File(caminhoImagem);
                    if (arquivoImagem.exists()) {
                        Bitmap imagemBitmap = BitmapFactory.decodeFile(
                                arquivoImagem.getAbsolutePath());

                        Bitmap tamanhoReduzidoImagem, imagemVirada;
                        imagemVirada = RotateBitmap(imagemBitmap,90);
                        tamanhoReduzidoImagem = Bitmap.createScaledBitmap(imagemVirada,(int)(imagemVirada.getWidth()*0.5), (int)(imagemVirada.getHeight()*0.5), true);
                        imgString = Base64.encodeToString(getBytesFromBitmap(tamanhoReduzidoImagem), Base64.NO_WRAP);
                        iv.setImageBitmap(RotateBitmap(imagemBitmap,90));
                    }
                }
                /*
                Bitmap bp = (Bitmap) data.getExtras().get("data");
                imgString = encodeToBase64(bp, Bitmap.CompressFormat.JPEG, 100);
                iv.setImageBitmap(bp);

                if (data != null) {
                    Log.d("resposta"," Entrou no data: "+data);

                    Uri selectedImageUri = data.getData();
                    Bitmap srcBmp = null;
                    try {
                        srcBmp = BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImageUri), null, null);
                        Bitmap tamanhoReduzidoImagem, imagemVirada;
                        imagemVirada = RotateBitmap(srcBmp,90);
                        tamanhoReduzidoImagem = Bitmap.createScaledBitmap(imagemVirada,(int)(imagemVirada.getWidth()*0.5), (int)(imagemVirada.getHeight()*0.5), true);
                        imgString = Base64.encodeToString(getBytesFromBitmap(tamanhoReduzidoImagem), Base64.NO_WRAP);

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    iv.setImageBitmap(RotateBitmap(srcBmp,90));
                }else{
                    Log.d("resposta"," Não entrou no data");
                }
                */
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Você NÃO inseriu a foto!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static Bitmap RotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
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
            String nomeArquivoImagem = "JPEG_" + numeroNotaSelect +"_"+nomeDocumento+"_"+horarioSistema + "_";

            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/HCC/";

            File dir = new File(path);
            if(!dir.exists())
                dir.mkdirs();

            String path2 = Environment.getExternalStorageDirectory().getAbsolutePath() + "/HCC/"+numeroNotaSelect;
            File dir2 = new File(path2);
            if(!dir2.exists())
                dir2.mkdirs();

            fileFinal = File.createTempFile(nomeArquivoImagem, ".jpg", dir2);
            Uri photoURI2 = FileProvider.getUriForFile(InseriImagemDocumento.this,
                    BuildConfig.APPLICATION_ID + ".provider",
                    fileFinal);

            return photoURI2;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    private File criaArquivoImagem() throws IOException {
        SharedPreferences sharedpreferences = getSharedPreferences(Login.MyPREFERENCES, Context.MODE_PRIVATE);
        String numeroNotaSelect = sharedpreferences.getString("numeroNotaKey", null);

        String horarioSistema = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String nomeArquivoImagem = "JPEG_" + numeroNotaSelect +"_"+nomeDocumento+"_"+horarioSistema + "_";

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

    public void salvarImagemDocumento(View v){
            if(imgString == null){
                Toast.makeText(InseriImagemDocumento.this, "A Foto eh Obrigatória ", Toast.LENGTH_SHORT).show();
            }else{
                addImagemDocBanco();
            }
    }

    private void addImagemDocBanco(){
        SharedPreferences sharedpreferences = getSharedPreferences(Login.MyPREFERENCES, Context.MODE_PRIVATE);
        String idLocacaoSelecionada = sharedpreferences.getString("idLocacaoKey", null);
        //String numeroNotaSelect = sharedpreferences.getString("numeroNotaKey", null);

        if(nomeDocumento.equals("Selecione um Documento")){
            Toast.makeText(InseriImagemDocumento.this,"Por Favor! Selecione o nome do Arquivo!", Toast.LENGTH_SHORT).show();
        }else {
            db = openOrCreateDatabase(BANCO, Context.MODE_PRIVATE, null);
            //Log.d("Resposta", "Nome Documento: "+nomeDocumento);
            //Log.d("Resposta", "Imagem: "+imgString);
            db.execSQL("INSERT INTO " + TABELADOCUMENTO + "(NOMEDOCUMENTO, IMAGEM, IDLOCACAO) VALUES ('" + nomeDocumento + "','" + imgString + "','" + idLocacaoSelecionada + "')");
            db.close();
            Toast.makeText(getApplicationContext(), "Inserção realizada com sucesso!", Toast.LENGTH_SHORT).show();

            Intent it;
            it = new Intent(this, GerenciarLocacoes.class);
            startActivity(it);
            finish();
        }
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(this, GerenciarLocacoes.class);
        this.startActivity(intent);
        finish();
        super.onBackPressed();
    }
}
