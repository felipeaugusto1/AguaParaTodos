package felipe.com.br.aguaparatodos.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import com.facebook.login.LoginManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.*;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import org.apache.http.Header;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import felipe.com.br.aguaparatodos.BuildConfig;
import felipe.com.br.aguaparatodos.R;
import felipe.com.br.aguaparatodos.dominio.Usuario;
import felipe.com.br.aguaparatodos.utils.PreferenciasUtil;
import felipe.com.br.aguaparatodos.utils.ToastUtil;
import felipe.com.br.aguaparatodos.utils.UsuarioSingleton;
import felipe.com.br.aguaparatodos.utils.ValidadorUtil;
import felipe.com.br.aguaparatodos.utils.WebService;

/**
 * Created by felipe on 8/29/15.
 */
public class Login extends FragmentActivity {

    private CallbackManager callbackManager;
    private LoginButton btnLoginFacebook;
    private static ProgressDialog progressDialog;
    private Usuario usuarioLogado;
    private RequestParams parametros;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.login);

        if (!PreferenciasUtil.getPreferenciasUsuarioLogado(PreferenciasUtil.KEY_PREFERENCIAS_USUARIO_LOGADO_EMAIL, getApplicationContext()).equalsIgnoreCase("erro")) {
            this.parametros = new RequestParams();
            this.parametros.put("email", PreferenciasUtil.getPreferenciasUsuarioLogado(PreferenciasUtil.KEY_PREFERENCIAS_USUARIO_LOGADO_EMAIL, getApplicationContext()));
            this.verificarEmail(this.parametros);
        } else {
            this.btnLoginFacebook = (LoginButton) findViewById(R.id.btnLoginFacebook);
            this.btnLoginFacebook.setReadPermissions("public_profile");
            this.btnLoginFacebook.setReadPermissions("email");
            this.btnLoginFacebook.setReadPermissions("user_location");

            this.btnLoginFacebook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    ToastUtil.criarToastCurto(getApplicationContext(), getResources().getString(R.string.aguarde));
                    recuperarInformacoesUsuarioFacebook();
                }

                @Override
                public void onCancel() {
                    ToastUtil.criarToastCurto(getApplicationContext(), getResources().getString(R.string.login_facebook_cancelado));
                }

                @Override
                public void onError(FacebookException e) {
                    LoginManager.getInstance().logOut();
                    ToastUtil.criarToastCurto(getApplicationContext(), getResources().getString(R.string.login_facebook_erro));
                }
            });
        }
    }

     public void recuperarInformacoesUsuarioFacebook() {
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object,GraphResponse response) {

                JSONObject json = response.getJSONObject();
                try {
                    if(!ValidadorUtil.isNuloOuVazio(json)){
                        //String text = "<b>Name :</b> "+json.getString("name")+"<br><br><b>Email :</b> "+json.getString("email")+"<br><br><b>Profile link :</b> "+json.getString("link");
                        //ToastUtil.criarToastCurto(getApplicationContext(), "Email: " + Html.fromHtml(text)+"");
                        //ToastUtil.criarToastCurto(getApplicationContext(), "ID: " + json.getString("id")+"");
                        //Log.d("nome", json.getString("name"));
                        //Log.d("email", json.getString("email"));
                        //Log.d("profile", json.getString("link"));
                        //Log.d("ID", json.getString("id"));
                        //details_txt.setText(Html.fromHtml(text));
                        //profile.setProfileId(json.getString("id"));
                        Log.d("cidade", json.getJSONObject("location").getString("name"));
                        parametros = new RequestParams();
                        parametros.put("nome", json.getString("name"));
                        parametros.put("email", json.getString("email"));
                        parametros.put("user_f", String.valueOf(true));
                        parametros.put("user_t", String.valueOf(false));
                        parametros.put("user_g", String.valueOf(false));
                        parametros.put("recebe_notificacao", String.valueOf(true));
                        parametros.put("versao_app", String.valueOf(BuildConfig.VERSION_CODE));
                        parametros.put("endereco", String.valueOf(json.getJSONObject("location").getString("name")));

                        PreferenciasUtil.salvarPreferenciasLogin(PreferenciasUtil.KEY_PREFERENCIAS_USUARIO_LOGADO_NOME, json.getString("name"), getApplicationContext());
                        PreferenciasUtil.salvarPreferenciasLogin(PreferenciasUtil.KEY_PREFERENCIAS_USUARIO_LOGADO_EMAIL, json.getString("email"), getApplicationContext());
                        PreferenciasUtil.salvarPreferenciasLogin(PreferenciasUtil.KEY_PREFERENCIAS_USUARIO_LOGADO_FOTO, Profile.getCurrentProfile().getProfilePictureUri(50, 50).toString(), getApplicationContext());

                        verificarEmail(parametros);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id, name, link, email, picture, location");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private boolean verificarEmail(RequestParams parametros) {
        AsyncHttpClient client = new AsyncHttpClient();

        client.get(WebService.ENDERECO_WS.concat(getResources().getString(R.string.usuario_login)), parametros, new AsyncHttpResponseHandler() {

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

                UsuarioSingleton.getInstancia().setUsuario(usuarioLogado);

                Intent telaPosLogin = new Intent(Login.this, MainActivity.class);
                startActivity(telaPosLogin);
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                LoginManager.getInstance().logOut();
                ToastUtil.criarToastLongo(getApplicationContext(), getResources().getString(R.string.login_erro));
            }

        });

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    /* private void validarLogin() {
        progressDialog = ProgressDialog.show(this, "Autenticando...",
				"Autenticando...", false, true);

        startActivity(new Intent(Login.this, MainActivity.class));
    } */

}