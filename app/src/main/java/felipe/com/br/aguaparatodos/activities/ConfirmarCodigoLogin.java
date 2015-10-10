package felipe.com.br.aguaparatodos.activities;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.facebook.login.LoginManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import felipe.com.br.aguaparatodos.R;
import felipe.com.br.aguaparatodos.dominio.Usuario;
import felipe.com.br.aguaparatodos.singleton.UsuarioSingleton;
import felipe.com.br.aguaparatodos.utils.BuscarEnderecoGoogle;
import felipe.com.br.aguaparatodos.utils.ConexoesWS;
import felipe.com.br.aguaparatodos.utils.PreferenciasUtil;
import felipe.com.br.aguaparatodos.utils.ToastUtil;
import felipe.com.br.aguaparatodos.utils.ValidadorUtil;
import felipe.com.br.aguaparatodos.utils.WebService;

public class ConfirmarCodigoLogin extends Activity {

    private RequestParams parametros;
    private Usuario usuarioLogado;
    private EditText edCodigo;


    private String cidade = "", estado = "";
    private Map<String, String> valores;
    private List<Double> coordenadas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmar_codigo_login);

        this.edCodigo = (EditText) findViewById(R.id.edCodigoConfirmacao);

        String gcm = getIntent().getStringExtra("gcm");
        if (!ValidadorUtil.isNuloOuVazio(gcm)) {
            ConexoesWS.prepararCamposAtualizarGcm(ConfirmarCodigoLogin.this, UsuarioSingleton.getInstancia().getUsuario().getId(), gcm);
        }
    }

    private boolean validarCampos() {
        this.edCodigo.setError("");

        ValidadorUtil.validarCampoEmBranco(this.edCodigo, "Por favor informe o código enviado ao seu email");

        if (!ValidadorUtil.isNuloOuVazio(this.edCodigo.getError()))
            return false;

        return true;
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnConfirmarCodigoConfirmacao:
                if (validarCampos()) {
                    ToastUtil.criarToastLongo(ConfirmarCodigoLogin.this, "Entrando...");

                    this.valores = BuscarEnderecoGoogle.buscarEnderecoByNome(UsuarioSingleton.getInstancia().getUsuario().getEndereco().getCidade(), getApplicationContext());
                    this.coordenadas = BuscarEnderecoGoogle.buscarCoordenadasPorEndereco(getApplicationContext(), UsuarioSingleton.getInstancia().getUsuario().getEndereco().getCidade());
                    this.cidade = this.valores.get("CIDADE");
                    this.estado = this.valores.get("ESTADO");

                    this.parametros = new RequestParams();
                    this.parametros.put("email", UsuarioSingleton.getInstancia().getUsuario().getEmail());
                    this.parametros.put("codigo", this.edCodigo.getText().toString());
                    this.parametros.put("cidade", this.cidade);
                    this.parametros.put("estado", this.estado);
                    this.parametros.put("lat", String.valueOf(this.coordenadas.get(0)));
                    this.parametros.put("lon", String.valueOf(this.coordenadas.get(1)));
                    verificarCodigoWs();
                }
                break;
        }
    }

    private void verificarCodigoWs() {
        AsyncHttpClient client = new AsyncHttpClient();

        client.get(WebService.ENDERECO_WS.concat(getResources().getString(R.string.usuario_verificar_codigo)), this.parametros, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String str = "";
                try {
                    str = new String(bytes, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                Gson gson = new GsonBuilder().setDateFormat(
                        "yyyy-MM-dd'T'HH:mm:ss").create();

                usuarioLogado = gson.fromJson(str, Usuario.class);

                if (usuarioLogado.getId() != 0) {
                    UsuarioSingleton.getInstancia().setUsuario(usuarioLogado);
                    UsuarioSingleton.getInstancia().getUsuario().setPreferenciaVisualizacao(Usuario.PREFERENCIA_VISUALIZACAO_CIDADE);

                    PreferenciasUtil.salvarPreferenciasLogin(PreferenciasUtil.KEY_PREFERENCIAS_USUARIO_LOGADO_NOME, usuarioLogado.getNomeCompleto(), getApplicationContext());
                    PreferenciasUtil.salvarPreferenciasLogin(PreferenciasUtil.KEY_PREFERENCIAS_USUARIO_LOGADO_EMAIL, usuarioLogado.getEmail(), getApplicationContext());

                    startActivity(new Intent(ConfirmarCodigoLogin.this, MainActivity.class));
                } else
                    ToastUtil.criarToastLongo(ConfirmarCodigoLogin.this, "Usuário ou senha inválidos.");
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                LoginManager.getInstance().logOut();
                ToastUtil.criarToastLongo(getApplicationContext(), getResources().getString(R.string.login_erro));
            }

        });
    }

}
