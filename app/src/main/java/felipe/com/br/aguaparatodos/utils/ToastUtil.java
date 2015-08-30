package felipe.com.br.aguaparatodos.utils;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

/**
 * Created by felipe on 8/29/15.
 */
public class ToastUtil {

    public static void criarToastCurto(Context context, String texto) {
        Toast.makeText(context, texto, Toast.LENGTH_SHORT).show();
    }

    public static void criarToastLongo(Context context, String texto) {
        Toast.makeText(context, texto, Toast.LENGTH_LONG).show();
    }
}
