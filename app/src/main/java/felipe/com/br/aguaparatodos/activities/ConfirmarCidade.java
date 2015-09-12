package felipe.com.br.aguaparatodos.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import felipe.com.br.aguaparatodos.R;
import felipe.com.br.aguaparatodos.dominio.Usuario;
import felipe.com.br.aguaparatodos.extras.PlacesAutoCompleteAdapter;
import felipe.com.br.aguaparatodos.gcm.AtualizarGCMUsuario;
import felipe.com.br.aguaparatodos.utils.BuscarEnderecoGoogle;
import felipe.com.br.aguaparatodos.utils.StringUtil;
import felipe.com.br.aguaparatodos.utils.ToastUtil;
import felipe.com.br.aguaparatodos.utils.UsuarioSingleton;
import felipe.com.br.aguaparatodos.utils.ValidadorUtil;
import felipe.com.br.aguaparatodos.utils.WebService;

/**
 * Created by felipe on 8/29/15.
 */
public class ConfirmarCidade extends AppCompatActivity {

    private AutoCompleteTextView cidadeAutoComplete;
    private RequestParams parametros;

    private String cidade = "", estado = "";
    private Map<String, String> valores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirmar_cidade);

        this.criarReferenciasComponentes();

        String gcm = getIntent().getStringExtra("gcm");
        if (!ValidadorUtil.isNuloOuVazio(gcm)) {

            this.parametros = new RequestParams();
            this.parametros.put("id", UsuarioSingleton.getInstancia().getUsuario().getId());
            this.parametros.put("gcm", gcm);
            AtualizarGCMUsuario.atualizarGcm(this.parametros, ConfirmarCidade.this);
        }

    }

    private void criarReferenciasComponentes() {
        this.cidadeAutoComplete = (AutoCompleteTextView) findViewById(R.id.editTextCidadeUsuario);

        this.cidadeAutoComplete.setAdapter(new PlacesAutoCompleteAdapter(getApplicationContext(), R.layout.item_lista_busca_endereco));
    }

    public void acaoBotoes(View view) {
        switch (view.getId()) {
            case R.id.btnContinuarCidade:
                this.cidadeAutoComplete.setError(null);

                if (ValidadorUtil.isNuloOuVazio(this.cidadeAutoComplete.getText().toString()) && ValidadorUtil.isNuloOuVazio(this.cidade))
                    this.cidadeAutoComplete.setError(getResources().getString(R.string.msgErroInformarCidade));
                else {
                    try {
                        this.valores = BuscarEnderecoGoogle.buscarEnderecoByNome(this.cidadeAutoComplete.getText().toString(), getApplicationContext());
                        this.cidade = this.valores.get("CIDADE");
                        this.estado = this.valores.get("ESTADO");

                        criarDialog(ConfirmarCidade.this, "Cidade", "Você mora em ".concat(this.cidade).concat("/").concat(this.estado).concat("?"));
                    } catch (Exception e) {
                        ToastUtil.criarToastLongo(getApplicationContext(), getResources().getString(R.string.erroBuscarCidade));
                    }
                }
        }
    }

    private void atualizarDados() {
        this.parametros = new RequestParams();
        this.parametros.put("id", UsuarioSingleton.getInstancia().getUsuario().getId());


        this.parametros.put("cidade", this.valores.get("CIDADE"));
        this.parametros.put("estado", this.valores.get("ESTADO"));
        atualizarCidadeUsuario();
    }

    public void criarDialog(final Context contexto, String titulo, String mensagem) {

        AlertDialog dialog =  new AlertDialog.Builder(contexto)
                .setTitle(titulo)
                .setMessage(mensagem)
                .setPositiveButton(contexto.getResources().getString(R.string.msgSim), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        atualizarDados();
                    }
                })
                .setNegativeButton(contexto.getResources().getString(R.string.msgNao), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();

        dialog.show();
    }

    private void atualizarCidadeUsuario() {
        AsyncHttpClient client = new AsyncHttpClient();

        client.get(WebService.ENDERECO_WS.concat(getResources().getString(R.string.usuario_atualizar_cidade)), this.parametros, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String str = "";
                try {
                    str = new String(bytes, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                if (str.equalsIgnoreCase(WebService.RESPOSTA_SUCESSO)) {
                    ToastUtil.criarToastLongo(getApplicationContext(), "Informação atualizada com sucesso!");
                    startActivity(new Intent(ConfirmarCidade.this, MainActivity.class));
                } else if (str.equalsIgnoreCase(WebService.RESPOSTA_ERRO)) {

                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                LoginManager.getInstance().logOut();
                ToastUtil.criarToastLongo(getApplicationContext(), getResources().getString(R.string.msgErroWS));
            }

        });
    }


}