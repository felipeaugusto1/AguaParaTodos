package felipe.com.br.aguaparatodos.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import felipe.com.br.aguaparatodos.R;


/**
 * Created by felipe on 9/4/15.
 */
public class DialogUtils {

    public static Dialog criarDialog(final Context contexto, String titulo, String mensagem) {

        return new AlertDialog.Builder(contexto)
                .setTitle(titulo)
                .setMessage(mensagem)
                .setPositiveButton(contexto.getResources().getString(R.string.msgSim), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ToastUtil.criarToastCurto(contexto, "Simmmm");
                    }
                })
                .setNegativeButton(contexto.getResources().getString(R.string.msgSim), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ToastUtil.criarToastCurto(contexto, "Naoooo");
                    }
                }).create();

    }

}
