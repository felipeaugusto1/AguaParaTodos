package felipe.com.br.aguaparatodos.gcm;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import felipe.com.br.aguaparatodos.R;
import felipe.com.br.aguaparatodos.utils.WebService;

/**
 * Created by felipe on 9/7/15.
 */
public class AtualizarGCMUsuario {

    public static void atualizarGcm(RequestParams parametros, Context contexto) {
        AsyncHttpClient client = new AsyncHttpClient();

        client.get(WebService.ENDERECO_WS.concat(contexto.getResources().getString(R.string.usuario_atualizar_gcm)), parametros, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {

            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

            }

        });
    }

}
