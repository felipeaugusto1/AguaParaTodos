package felipe.com.br.aguaparatodos.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

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

        this.btnLoginFacebook = (LoginButton) findViewById(R.id.btnLoginFacebook);
        this.btnLoginFacebook.setReadPermissions("public_profile");
        this.btnLoginFacebook.setReadPermissions("email");

        this.btnLoginFacebook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                ToastUtil.criarToastCurto(getApplicationContext(), getResources().getString(R.string.login_facebook_aguarde));
                recuperarInformacoesUsuarioFacebook();
            }

            @Override
            public void onCancel() {
                ToastUtil.criarToastCurto(getApplicationContext(), getResources().getString(R.string.login_facebook_cancelado));
            }

            @Override
            public void onError(FacebookException e) {
                ToastUtil.criarToastCurto(getApplicationContext(), getResources().getString(R.string.login_facebook_erro));
            }
        });

    }

     public void recuperarInformacoesUsuarioFacebook() {
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object,GraphResponse response) {

                JSONObject json = response.getJSONObject();
                try {
                    if(!ValidadorUtil.isNulo(json)){
                        //String text = "<b>Name :</b> "+json.getString("name")+"<br><br><b>Email :</b> "+json.getString("email")+"<br><br><b>Profile link :</b> "+json.getString("link");
                        //ToastUtil.criarToastCurto(getApplicationContext(), "Email: " + Html.fromHtml(text)+"");
                        //ToastUtil.criarToastCurto(getApplicationContext(), "ID: " + json.getString("id")+"");
                        //Log.d("nome", json.getString("name"));
                        //Log.d("email", json.getString("email"));
                        //Log.d("profile", json.getString("link"));
                        //Log.d("ID", json.getString("id"));
                        //details_txt.setText(Html.fromHtml(text));
                        //profile.setProfileId(json.getString("id"));

                        parametros = new RequestParams();
                        parametros.put("nome", json.getString("name"));
                        parametros.put("email", json.getString("email"));
                        parametros.put("user_f", String.valueOf(true));
                        parametros.put("user_t", String.valueOf(false));
                        parametros.put("user_g", String.valueOf(false));
                        parametros.put("recebe_notificacao", String.valueOf(true));
                        parametros.put("versao_app", String.valueOf(BuildConfig.VERSION_CODE));

                        PreferenciasUtil.salvarPreferenciasLogin(PreferenciasUtil.KEY_PREFERENCIAS_USUARIO_LOGADO_NOME, json.getString("name"), getApplicationContext());
                        PreferenciasUtil.salvarPreferenciasLogin(PreferenciasUtil.KEY_PREFERENCIAS_USUARIO_LOGADO_EMAIL, json.getString("email"), getApplicationContext());

                        verificarEmail(parametros);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id, name, link, email, picture");
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

                Profile profile = Profile.getCurrentProfile();
                Bundle bundle = new Bundle();
                //Log.d("imagem", profile.getProfilePictureUri(20, 20)+"");
                //Bundle bundle = new Bundle();
                //bundle.putString(getResources().getString(R.string.bundle_nome_usuario), json.getString("name"));
                //bundle.putString(getResources().getString(R.string.bundle_email_usuario), json.getString("email"));
                bundle.putString(getResources().getString(R.string.bundle_foto_usuario), profile.getProfilePictureUri(50, 50).toString());

                Intent telaPosLogin = new Intent(Login.this, MainActivity.class);
                telaPosLogin.putExtras(bundle);
                startActivity(telaPosLogin);
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

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