package felipe.com.br.aguaparatodos.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by felipe on 8/30/15.
 */
public class PreferenciasUtil {

    public static final String PREFERENCIAS_LOGIN = "USUARIO_LOGIN";
    public static final String PREFERENCIAS_NOTIFICACAO = "RECEBER_NOTIFICACAO";
    public static final String KEY_PREFERENCIAS_USUARIO_LOGADO_EMAIL = "email";
    public static final String KEY_PREFERENCIAS_USUARIO_LOGADO_NOME = "usuario";
    public static final String KEY_PREFERENCIAS_USUARIO_LOGADO_FOTO = "foto";
    public static final String KEY_PREFERENCIAS_NOTIFICACAO = "receber_notificacao";

    public static final String VALUE_PREFERENCIAS_NOTIFICACAO_TRUE = "true";
    public static final String VALUE_PREFERENCIAS_NOTIFICACAO_FALSE = "false";

    public static void salvarPreferenciasLogin(String key, String value, Context contexto) {
        SharedPreferences preferencias = contexto.getSharedPreferences(
                PREFERENCIAS_LOGIN, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferencias.edit();
        editor.putString(key, value.trim());
        editor.commit();
    }

    public static String getPreferenciasUsuarioLogado(String key, Context contexto) {
        SharedPreferences sharedPreferences = contexto.getSharedPreferences(
                PreferenciasUtil.PREFERENCIAS_LOGIN, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, "erro");
    }


}
