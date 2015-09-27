package felipe.com.br.aguaparatodos.crashlistener;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

import org.acra.ReportField;
import org.acra.collector.CrashReportData;
import org.acra.sender.ReportSender;
import org.acra.sender.ReportSenderException;
import org.apache.http.Header;

import felipe.com.br.aguaparatodos.utils.WebService;

public class CrashSender implements ReportSender {

    @Override
    public void send(CrashReportData report) throws ReportSenderException {
        enviarStackTrace(report.getProperty(ReportField.STACK_TRACE));
        enviarVersao(report.getProperty(ReportField.APP_VERSION_NAME));
        enviarCelular("Cel model: " + report.getProperty(ReportField.PHONE_MODEL) + ". Android version: " + android.os.Build.VERSION.RELEASE);
    }

    private void enviarVersao(String descricao) {
        RequestParams parametros = new RequestParams();
        parametros.put("descricao", descricao);

        SyncHttpClient client = new SyncHttpClient();
        client.get(WebService.ENDERECO_WS + "erro/versao", parametros,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          byte[] response) {
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          byte[] errorResponse, Throwable e) {
                    }
                });
    }

    private void enviarCelular(String descricao) {
        RequestParams parametros = new RequestParams();
        parametros.put("descricao", descricao);

        SyncHttpClient client = new SyncHttpClient();
        client.get(WebService.ENDERECO_WS + "erro/modelPhone", parametros,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          byte[] response) {
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          byte[] errorResponse, Throwable e) {
                    }
                });
    }

    private void enviarStackTrace(String descricao) {
        RequestParams parametros = new RequestParams();
        parametros.put("descricao", descricao);

        SyncHttpClient client = new SyncHttpClient();
        client.get(WebService.ENDERECO_WS + "erro/stackTrace", parametros,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          byte[] response) {
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          byte[] errorResponse, Throwable e) {
                    }
                });
    }

}
