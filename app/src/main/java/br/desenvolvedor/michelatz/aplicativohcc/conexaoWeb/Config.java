package br.desenvolvedor.michelatz.aplicativohcc.conexaoWeb;

/**
 * Created by Michel Atz on 18/10/2016.
 */

public class Config {

    //public static final String URL_ADD_IMAGEM ="http://idealizaweb.com.br/dados/inserifoto.php";
    //public static final String URL_ADD_TXT ="http://idealizaweb.com.br/dados/inseritxt.php";

    //Servidor real
    //public static final String URL_ADD_IMAGEM ="http://idealizaweb.com.br/dados/inseriFotoServidor.php";
    //public static final String URL_ADD_TXT ="http://idealizaweb.com.br/dados/inseriTxtServidor.php";

    //Servidor teste
    public static final String URL_ADD_IMAGEM ="http://projetosw.esy.es/dados/inseriFotoServidor.php";
    public static final String URL_ADD_TXT ="http://projetosw.esy.es/dados/inseriTxtServidor.php";

    public static final String URL_GET_PONTOS_LOCACAO = "http://idealizaweb.com.br/dados/listaPontos.php";


    //Chaves que seram usadas nos scripts PHPs
    //Chaves das Imagens
    public static final String KEY_ADD_NUMERO_NOTA = "nota";
    public static final String KEY_ADD_TIPO_IMAGEM = "tipoimagem";
    public static final String KEY_ADD_IMAGEM= "imagem";

    //Chaves do TXT
    public static final String KEY_ADD_NOME_ARQUIVO = "nomearquivo";
    public static final String KEY_ADD_BASE64_ARQUIVO= "arquivo";

    //Tags JSON
    public static final String TAG_JSON_ARRAY="result";
    public static final String TAG_NUMERO_NOTA = "nota";
    public static final String TAG_LONGITUDE = "longitude";
    public static final String TAG_LATITUDE = "latitude";

    //Id da Locaçao que sera passada por intent
    public static final String EMP_ID = "emp_id";
}
