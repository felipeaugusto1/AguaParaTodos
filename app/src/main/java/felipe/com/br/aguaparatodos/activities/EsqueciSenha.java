package felipe.com.br.aguaparatodos.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
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
import felipe.com.br.aguaparatodos.utils.ToastUtil;
import felipe.com.br.aguaparatodos.utils.ValidadorUtil;
import felipe.com.br.aguaparatodos.utils.WebService;

public class EsqueciSenha extends AppCompatActivity {

    private Drawer navigationDrawer;
    private Toolbar toolbar;

    private EditText edEmailRecuperacao;
    private RequestParams parametros;
    private static ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_esqueci_senha);

        criarReferenciasComponentes();

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
                //Intent intent = new Intent(DetalheOcorrencia.this, MainActivity.class);
                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                //startActivity(intent);
                onBackPressed();
            }
        });
    }

    private void criarReferenciasComponentes() {
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.edEmailRecuperacao = (EditText) findViewById(R.id.editTextEmailRecuperacao);
    }

    private boolean validarCampos() {
        this.edEmailRecuperacao.setError(null);

        ValidadorUtil.validarCampoEmBranco(this.edEmailRecuperacao, "Informe seu email");

        if (!ValidadorUtil.isNuloOuVazio(this.edEmailRecuperacao.getError()))
            return false;
        return true;
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnRecuperarSenha:
                if (validarCampos()) {
                    progressDialog = ProgressDialog.show(this, getResources()
                                    .getString(R.string.aguarde),
                            "Enviando email...");
                    parametros = new RequestParams();
                    parametros.put("email", this.edEmailRecuperacao.getText().toString());
                    enviarEmailVerificacao();
                }
                break;
        }
    }

    private void enviarEmailVerificacao() {
        AsyncHttpClient client = new AsyncHttpClient();

        client.get(WebService.ENDERECO_WS.concat(getResources().getString(R.string.usuario_esqueci_senha)), this.parametros, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String str = "";
                try {
                    str = new String(bytes, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                progressDialog.dismiss();

                if (str.equalsIgnoreCase(WebService.RESPOSTA_SUCESSO)) {
                    ToastUtil.criarToastLongo(EsqueciSenha.this, "Por favor, confira seu email e siga as instruções.");
                } else if (str.equalsIgnoreCase(WebService.RESPOSTA_ACESSO_NEGADO)) {
                    ToastUtil.criarToastLongo(EsqueciSenha.this, "Algo estranho ocorreu ao buscar seu email. Por favor, tente novamente.");
                } else if (str.equalsIgnoreCase(WebService.RESPOSTA_ERRO)) {
                    ToastUtil.criarToastLongo(EsqueciSenha.this, "Algo estranho ocorreu. Por favor, tente novamente.");
                }

                Intent intent = new Intent(EsqueciSenha.this, Login.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                ToastUtil.criarToastLongo(getApplicationContext(), getResources().getString(R.string.login_erro));
                progressDialog.dismiss();
            }

        });
    }

}
