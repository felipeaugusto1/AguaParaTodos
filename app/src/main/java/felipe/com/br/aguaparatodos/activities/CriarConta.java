package felipe.com.br.aguaparatodos.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AutoCompleteTextView;
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

import felipe.com.br.aguaparatodos.BuildConfig;
import felipe.com.br.aguaparatodos.R;
import felipe.com.br.aguaparatodos.dominio.Usuario;
import felipe.com.br.aguaparatodos.extras.PlacesAutoCompleteAdapter;
import felipe.com.br.aguaparatodos.utils.ToastUtil;
import felipe.com.br.aguaparatodos.utils.ValidadorUtil;
import felipe.com.br.aguaparatodos.utils.WebService;

public class CriarConta extends AppCompatActivity {

    private Drawer navigationDrawer;
    private Toolbar toolbar;
    private Usuario usuarioLogado;
    private RequestParams parametros;

    private EditText edNome, edEmail, edSenha, edSenhaConfirmacao;
    private AutoCompleteTextView edCidade;

    private static ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criar_conta);

        criarReferenciasComponentes();

        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
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
                Intent intent = new Intent(CriarConta.this, Login.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }

    private void criarReferenciasComponentes() {
        this.edNome = (EditText) findViewById(R.id.editTextNomeUsuario);
        this.edEmail = (EditText) findViewById(R.id.editTextEmailUsuario);
        this.edSenha = (EditText) findViewById(R.id.editTextSenha);
        this.edSenhaConfirmacao = (EditText) findViewById(R.id.editTextSenhaConfirmacao);

        this.edCidade = (AutoCompleteTextView) findViewById(R.id.editTextCidadeUsuario);

        this.edCidade.setAdapter(new PlacesAutoCompleteAdapter(getApplicationContext(), R.layout.item_lista_busca_endereco));
    }

    private void limparCampos() {
        this.edNome.setText("");
        this.edEmail.setText("");
        this.edCidade.setText("");
        this.edSenha.setText("");
        this.edSenhaConfirmacao.setText("");
    }

    private boolean validarCampos() {
        this.edNome.setError(null);
        this.edEmail.setError(null);
        this.edCidade.setError(null);
        this.edSenha.setError(null);
        this.edSenhaConfirmacao.setError(null);

        ValidadorUtil.validarCampoEmBranco(this.edNome, "Informe seu nome");
        ValidadorUtil.validarCampoEmBranco(this.edEmail, "Informe seu email");
        ValidadorUtil.validarCampoEmBranco(this.edCidade, "Informe sua cidade");
        ValidadorUtil.validarCampoEmBranco(this.edSenha, "Informe sua senha");
        ValidadorUtil.validarCampoEmBranco(this.edSenhaConfirmacao, "Confirme sua senha");

        if (!this.edSenha.getText().toString().equals(this.edSenhaConfirmacao.getText().toString())) {
            this.edSenhaConfirmacao.setError("Senhas não coincidem");
        }

        if (!ValidadorUtil.isNuloOuVazio(this.edNome.getError()) || !ValidadorUtil.isNuloOuVazio(this.edEmail.getError()) || !ValidadorUtil.isNuloOuVazio(this.edSenha.getError()) || !ValidadorUtil.isNuloOuVazio(this.edSenhaConfirmacao.getError()) || !ValidadorUtil.isNuloOuVazio(this.edCidade.getError())) {
            return false;
        }

        return true;
    }

    private void prepararParametros(String nome, String email, String senha, boolean usuarioFacebook, boolean usuarioTwitter, boolean usuarioGooglePlus, boolean usuarioNativo, boolean recebeNotificacao, String endereco) {
        parametros = new RequestParams();
        parametros.put("nome", nome);
        parametros.put("email", email);
        parametros.put("senha", senha);
        parametros.put("user_f", String.valueOf(usuarioFacebook));
        parametros.put("user_t", String.valueOf(usuarioTwitter));
        parametros.put("user_g", String.valueOf(usuarioGooglePlus));
        parametros.put("user_n", String.valueOf(usuarioNativo));
        parametros.put("recebe_notificacao", String.valueOf(recebeNotificacao));
        parametros.put("versao_app", String.valueOf(BuildConfig.VERSION_CODE));
        parametros.put("endereco", endereco);
    }

    private void verificarEmail() {
        AsyncHttpClient client = new AsyncHttpClient();

        client.get(WebService.ENDERECO_WS.concat(getResources().getString(R.string.usuario_login)), this.parametros, new AsyncHttpResponseHandler() {

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
                    ToastUtil.criarToastLongo(CriarConta.this, "Foi enviado um email com o código de verificação. Utilize-o para após o login.");
                    startActivity(new Intent(CriarConta.this, Login.class));
                }

            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                ToastUtil.criarToastLongo(getApplicationContext(), getResources().getString(R.string.login_erro));
                progressDialog.dismiss();
            }

        });
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnCadastrarUsuario:
                if (validarCampos()) {
                    progressDialog = ProgressDialog.show(this, getResources()
                                    .getString(R.string.aguarde),
                            "Criando sua conta...");
                    progressDialog.setCanceledOnTouchOutside(false);

                    prepararParametros(this.edNome.getText().toString(), this.edEmail.getText().toString(), this.edSenha.getText().toString(), false, false, false, true, true, this.edCidade.getText().toString());
                    verificarEmail();
                }

                break;
            case R.id.txtLimparCamposOcorrencia:
                limparCampos();
                break;
        }
    }

}
