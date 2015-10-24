package felipe.com.br.aguaparatodos.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;

import org.apache.http.Header;

import java.io.UnsupportedEncodingException;

import felipe.com.br.aguaparatodos.R;
import felipe.com.br.aguaparatodos.dominio.Usuario;
import felipe.com.br.aguaparatodos.singleton.UsuarioSingleton;
import felipe.com.br.aguaparatodos.utils.PreferenciasUtil;
import felipe.com.br.aguaparatodos.utils.ToastUtil;
import felipe.com.br.aguaparatodos.utils.ValidadorUtil;
import felipe.com.br.aguaparatodos.utils.WebService;

public class NovaSenha extends AppCompatActivity {

    private EditText edNovaSenha, edNovaSenhaConfirmacao;
    private Drawer navigationDrawer;
    private Toolbar toolbar;

    private RequestParams parametros;
    private static ProgressDialog progressDialog;

    private Usuario usuarioLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nova_senha);

        criarReferenciasComponentes();

        setSupportActionBar(this.toolbar);

        this.navigationDrawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(this.toolbar)
                .build();

        this.navigationDrawer.getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        this.toolbar.setNavigationOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NovaSenha.this, Login.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                UsuarioSingleton.getInstancia().setUsuario(new Usuario());
                PreferenciasUtil.salvarPreferenciasLogin(PreferenciasUtil.KEY_PREFERENCIAS_USUARIO_LOGADO_EMAIL, PreferenciasUtil.VALOR_INVALIDO, getApplicationContext());
                PreferenciasUtil.salvarPreferenciasLogin(PreferenciasUtil.KEY_PREFERENCIAS_USUARIO_LOGADO_FOTO, PreferenciasUtil.VALOR_INVALIDO, getApplicationContext());
                PreferenciasUtil.salvarPreferenciasLogin(PreferenciasUtil.KEY_PREFERENCIAS_USUARIO_LOGADO_NOME, PreferenciasUtil.VALOR_INVALIDO, getApplicationContext());

                startActivity(intent);
            }
        });

    }

    private void criarReferenciasComponentes() {
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.edNovaSenha = (EditText) findViewById(R.id.editTextSenhaNova);
        this.edNovaSenhaConfirmacao = (EditText) findViewById(R.id.editTextSenhaConfirmacaoNova);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAtualizarSenha:
                if (validarCampos()) {
                    progressDialog = ProgressDialog.show(NovaSenha.this, getResources().getString(R.string.aguarde), "Atualizando senha...");
                    prepararParametros();
                    alterarSenha();
                }
                break;
        }
    }

    private void prepararParametros() {
        parametros = new RequestParams();
        parametros.put("email", PreferenciasUtil.getPreferenciasUsuarioLogado(PreferenciasUtil.KEY_PREFERENCIAS_USUARIO_LOGADO_EMAIL, NovaSenha.this));
        parametros.put("senha", this.edNovaSenha.getText().toString());
    }

    private boolean validarCampos() {
        this.edNovaSenha.setError(null);
        this.edNovaSenhaConfirmacao.setError(null);

        ValidadorUtil.validarCampoEmBranco(this.edNovaSenha, "Informe sua senha");
        ValidadorUtil.validarCampoEmBranco(this.edNovaSenhaConfirmacao, "Confirme sua senha");

        if (!this.edNovaSenha.getText().toString().equals(this.edNovaSenhaConfirmacao.getText().toString())) {
            this.edNovaSenhaConfirmacao.setError("Senhas não coincidem");
        }

        if (!ValidadorUtil.isNuloOuVazio(this.edNovaSenha.getError()) || !ValidadorUtil.isNuloOuVazio(this.edNovaSenhaConfirmacao.getError()))
            return false;

        return true;
    }

    private void alterarSenha() {
        AsyncHttpClient client = new AsyncHttpClient();

        client.get(WebService.ENDERECO_WS.concat(getResources().getString(R.string.usuario_alterar_senha)), this.parametros, new AsyncHttpResponseHandler() {

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

                progressDialog.dismiss();

                if (usuarioLogado.getId() != 0) {
                    ToastUtil.criarToastLongo(NovaSenha.this, "Senha atualizada com sucesso!");

                    UsuarioSingleton.getInstancia().setUsuario(usuarioLogado);
                    UsuarioSingleton.getInstancia().getUsuario().setPreferenciaVisualizacao(Usuario.PREFERENCIA_VISUALIZACAO_CIDADE);

                    PreferenciasUtil.salvarPreferenciasLogin(PreferenciasUtil.KEY_PREFERENCIAS_USUARIO_LOGADO_NOME, usuarioLogado.getNomeCompleto(), getApplicationContext());
                    PreferenciasUtil.salvarPreferenciasLogin(PreferenciasUtil.KEY_PREFERENCIAS_USUARIO_LOGADO_EMAIL, usuarioLogado.getEmail(), getApplicationContext());

                    startActivity(new Intent(NovaSenha.this, MainActivity.class));
                }

            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                ToastUtil.criarToastLongo(getApplicationContext(), getResources().getString(R.string.login_erro));
                progressDialog.dismiss();
            }

        });
    }

}
