package felipe.com.br.aguaparatodos.activities;

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
    private TextView cidadeRecuperada, textViewCidade, textViewInterrogacao, textViewCidadeInformativo;
    private RequestParams parametros;

    private String cidade = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirmar_cidade);

        this.criarReferenciasComponentes();

        Bundle b = getIntent().getExtras();

        if (b != null) {
            this.cidade = b.getString("cidade");
            if (!ValidadorUtil.isNuloOuVazio(cidade))
                this.cidadeRecuperada.setText(StringUtil.converterPrimeiraLetraMaiuscula(cidade));
            else
                this.msgErroCidadeInvalida();
        }
    }

    private void msgErroCidadeInvalida() {
        this.cidadeRecuperada.setText("");
        this.textViewInterrogacao.setText("");
        this.textViewCidadeInformativo.setText("");
        this.textViewCidade.setText("Ocorreu um erro ao recuperar sua cidade de residência. Por favor informe no campo abaixo.");
    }

    private void criarReferenciasComponentes() {
        this.cidadeRecuperada = (TextView) findViewById(R.id.textViewCidadeInformada);
        this.textViewCidade = (TextView) findViewById(R.id.textViewCidade);
        this.textViewInterrogacao = (TextView) findViewById(R.id.textViewInterrogacao);
        this.textViewCidadeInformativo = (TextView) findViewById(R.id.textViewCidadeInformativo);
        this.cidadeAutoComplete = (AutoCompleteTextView) findViewById(R.id.editTextCidadeUsuario);

        this.cidadeAutoComplete.setAdapter(new PlacesAutoCompleteAdapter(getApplicationContext(), R.layout.item_lista_busca_endereco));
    }

    public void acaoBotoes(View view) {
        switch (view.getId()) {
            case R.id.btnContinuarCidade:
                this.cidadeAutoComplete.setError(null);

                if (ValidadorUtil.isNuloOuVazio(this.cidadeAutoComplete.getText().toString()) && ValidadorUtil.isNuloOuVazio(this.cidade))
                    this.cidadeAutoComplete.setError("Por favor informe a cidade que deseja acompanhar as ocorrências.");
                else {
                    try {
                        this.parametros = new RequestParams();
                        this.parametros.put("id", UsuarioSingleton.getInstancia().getUsuario().getId());

                        if (ValidadorUtil.isNuloOuVazio(this.cidade)) {
                            Map<String, String> valores = BuscarEnderecoGoogle.buscarEnderecoByNome(this.cidadeAutoComplete.getText().toString(), getApplicationContext());
                            this.parametros.put("cidade", valores.get("CIDADE"));
                        } else
                            this.parametros.put("cidade", this.cidade);

                        atualizarCidadeUsuario();
                    } catch (Exception e) {
                        ToastUtil.criarToastLongo(getApplicationContext(), getResources().getString(R.string.erroBuscarEndereco));
                    }

                }
        }
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