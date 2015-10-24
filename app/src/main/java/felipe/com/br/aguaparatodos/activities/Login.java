package felipe.com.br.aguaparatodos.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.os.AsyncTask;
import android.os.Bundle;

import com.facebook.login.LoginManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.*;

import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import felipe.com.br.aguaparatodos.BuildConfig;
import felipe.com.br.aguaparatodos.R;
import felipe.com.br.aguaparatodos.dominio.Usuario;
import felipe.com.br.aguaparatodos.gcm.AndroidSystemUtil;
import felipe.com.br.aguaparatodos.utils.PreferenciasUtil;
import felipe.com.br.aguaparatodos.utils.ToastUtil;
import felipe.com.br.aguaparatodos.singleton.UsuarioSingleton;
import felipe.com.br.aguaparatodos.utils.ValidadorUtil;
import felipe.com.br.aguaparatodos.utils.WebService;

/**
 * Created by felipe on 8/29/15.
 */
public class Login extends FragmentActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    /* Request code used to invoke sign in user interactions. */
    private static final int RC_SIGN_IN = 0;

    /* Client used to interact with Google APIs. */
    private GoogleApiClient mGoogleApiClient;

    /* Is there a ConnectionResult resolution in progress? */
    private boolean mIsResolving = false;

    /* Should we automatically resolve ConnectionResults when possible? */
    private boolean mShouldResolve = false;


    private CallbackManager callbackManager;
    private LoginButton btnLoginFacebook;
    private static ProgressDialog progressDialog;
    private Usuario usuarioLogado;
    private RequestParams parametros;

    private static final String TAG = "Projeto Agua Para Todos";
    private GoogleCloudMessaging gcm;
    private String regId;
    private String SENDER_ID = "682647867821"; // id do projeto no google console

    private EditText edEmail, edSenha;
    private Button btnEntrar;
    private TextView txtOu, txtCriarConta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.login);

        this.edEmail = (EditText) findViewById(R.id.editTextEmailLogin);
        this.edSenha = (EditText) findViewById(R.id.editTextSenhaLogin);
        this.btnEntrar = (Button) findViewById(R.id.btnLogin);
        this.txtOu = (TextView) findViewById(R.id.txtOu);
        this.txtCriarConta = (TextView) findViewById(R.id.txtCriarConta);

        this.btnLoginFacebook = (LoginButton) findViewById(R.id.btnLoginFacebook);

        // Build GoogleApiClient with access to basic profile
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(new Scope(Scopes.PROFILE))
                .build();

        if (!PreferenciasUtil.getPreferenciasUsuarioLogado(PreferenciasUtil.KEY_PREFERENCIAS_USUARIO_LOGADO_EMAIL, getApplicationContext()).equalsIgnoreCase(PreferenciasUtil.VALOR_INVALIDO)) {

            this.edEmail.setText(PreferenciasUtil.getPreferenciasUsuarioLogado(PreferenciasUtil.KEY_PREFERENCIAS_USUARIO_LOGADO_NOME, Login.this));
            this.edSenha.setText(PreferenciasUtil.getPreferenciasUsuarioLogado(PreferenciasUtil.KEY_PREFERENCIAS_USUARIO_LOGADO_EMAIL, Login.this));
            this.edEmail.setClickable(false);
            this.edSenha.setClickable(false);

            this.btnEntrar.setClickable(false);

            this.txtCriarConta.setClickable(false);

            ToastUtil.criarToastLongo(Login.this, "Entrando...");

            this.btnLoginFacebook.setClickable(false);
            this.parametros = new RequestParams();
            this.parametros.put("email", PreferenciasUtil.getPreferenciasUsuarioLogado(PreferenciasUtil.KEY_PREFERENCIAS_USUARIO_LOGADO_EMAIL, getApplicationContext()));
            this.verificarEmail(this.parametros);
        } else {
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

    private void prepararParametros(String nome, String email, String senha, boolean usuarioFacebook, boolean usuarioTwitter, boolean usuarioGooglePlus, boolean recebeNotificacao, String endereco) {
        parametros = new RequestParams();
        parametros.put("nome", nome);
        parametros.put("email", email);
        parametros.put("senha", "");
        parametros.put("user_n", String.valueOf(Boolean.FALSE));
        parametros.put("user_f", String.valueOf(usuarioFacebook));
        parametros.put("user_t", String.valueOf(usuarioTwitter));
        parametros.put("user_g", String.valueOf(usuarioGooglePlus));
        parametros.put("recebe_notificacao", String.valueOf(recebeNotificacao));
        parametros.put("versao_app", String.valueOf(BuildConfig.VERSION_CODE));
        parametros.put("endereco", endereco);
    }

    public void recuperarInformacoesUsuarioFacebook() {
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {

                JSONObject json = response.getJSONObject();
                try {
                    if (!ValidadorUtil.isNuloOuVazio(json)) {
                        String endereco = "";
                        String email = "";
                        try {
                            endereco = String.valueOf(json.getJSONObject("location").getString("name"));
                            email = json.getString("email");
                        } catch (Exception e) {
                            //email = "erro_".concat(json.getString("name").replace(" ", "_").concat(regId));
                            email = "erro_".concat(json.getString("name").replace(" ", "_"));
                        }

                        prepararParametros(json.getString("name"), email, "", true, false, false, true, endereco);

                        try {
                            PreferenciasUtil.salvarPreferenciasLogin(PreferenciasUtil.KEY_PREFERENCIAS_USUARIO_LOGADO_FOTO, Profile.getCurrentProfile().getProfilePictureUri(50, 50).toString(), getApplicationContext());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

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

    private void verificarEmail(RequestParams parametros) {
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

                Log.d("usuario: ", usuarioLogado.toString());

                UsuarioSingleton.getInstancia().setUsuario(usuarioLogado);

                registerIdInBackground();

            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                LoginManager.getInstance().logOut();
                ToastUtil.criarToastLongo(getApplicationContext(), getResources().getString(R.string.login_erro));
            }

        });
    }

    private void loginNativo() {
        AsyncHttpClient client = new AsyncHttpClient();

        client.get(WebService.ENDERECO_WS.concat(getResources().getString(R.string.usuario_login_nativo)), this.parametros, new AsyncHttpResponseHandler() {

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

                Log.d("usuario", usuarioLogado.toString());

                if (usuarioLogado.getId() != 0) {
                    UsuarioSingleton.getInstancia().setUsuario(usuarioLogado);
                    UsuarioSingleton.getInstancia().getUsuario().setPreferenciaVisualizacao(Usuario.PREFERENCIA_VISUALIZACAO_CIDADE);

                    PreferenciasUtil.salvarPreferenciasLogin(PreferenciasUtil.KEY_PREFERENCIAS_USUARIO_LOGADO_NOME, usuarioLogado.getNomeCompleto(), getApplicationContext());
                    PreferenciasUtil.salvarPreferenciasLogin(PreferenciasUtil.KEY_PREFERENCIAS_USUARIO_LOGADO_EMAIL, usuarioLogado.getEmail(), getApplicationContext());

                    registerIdInBackground();
                } else
                    ToastUtil.criarToastLongo(Login.this, "Usuário ou senha inválidos.");
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                LoginManager.getInstance().logOut();
                ToastUtil.criarToastLongo(getApplicationContext(), getResources().getString(R.string.login_erro));
            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            // If the error resolution was not successful we should not resolve further.
            if (resultCode != RESULT_OK) {
                mShouldResolve = false;
            }

            mIsResolving = false;
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected())
            mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Could not connect to Google Play Services.  The user needs to select an account,
        // grant permissions or resolve an error in order to sign in. Refer to the javadoc for
        // ConnectionResult to see possible error codes.


        if (!mIsResolving && mShouldResolve) {
            if (connectionResult.hasResolution()) {
                try {
                    connectionResult.startResolutionForResult(this, RC_SIGN_IN);
                    mIsResolving = true;
                } catch (IntentSender.SendIntentException e) {

                    mIsResolving = false;
                    mGoogleApiClient.connect();
                }
            } else {
                // Could not resolve the connection result, show the user an
                // error dialog.

            }
        } else {
            // Show the signed-out UI

        }
    }

    private boolean validarCamposLoginNativo() {
        this.edEmail.setError(null);
        this.edSenha.setError(null);

        ValidadorUtil.validarCampoEmBranco(this.edEmail, "Informe seu email");
        ValidadorUtil.validarCampoEmBranco(this.edSenha, "Informe sua senha");

        if (!ValidadorUtil.isNuloOuVazio(this.edEmail.getError()) || !ValidadorUtil.isNuloOuVazio(this.edSenha.getError()))
            return false;

        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.txtCriarConta:
                startActivity(new Intent(Login.this, CriarConta.class));
                break;
            case R.id.btnLogin:
                if (validarCamposLoginNativo()) {
                    this.parametros = new RequestParams();
                    this.parametros.put("email", edEmail.getText().toString());
                    this.parametros.put("senha", edSenha.getText().toString());

                    loginNativo();
                }
                break;
            case R.id.txtEsqueciSenha:
                startActivity(new Intent(Login.this, EsqueciSenha.class));
                break;
        }
    }

    private void onSignInClicked() {
        // User clicked the sign-in button, so begin the sign-in process and automatically
        // attempt to resolve any errors that occur.
        mShouldResolve = true;
        mGoogleApiClient.connect();

        // Show a message to the user that we are signing in.

    }

    @Override
    public void onConnected(Bundle bundle) {
        // onConnected indicates that an account was selected on the device, that the selected
        // account has granted any requested permissions to our app and that we were able to
        // establish a service connection to Google Play services.

        mShouldResolve = false;

        String nome = "", email = "", foto = "";
        if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
            Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
            nome = currentPerson.getDisplayName();
            email = Plus.AccountApi.getAccountName(mGoogleApiClient);
            foto = currentPerson.getImage().getUrl();
            String personGooglePlusProfile = currentPerson.getUrl();
        }

        try {
            PreferenciasUtil.salvarPreferenciasLogin(PreferenciasUtil.KEY_PREFERENCIAS_USUARIO_LOGADO_FOTO, foto, getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        prepararParametros(nome, email, "", false, false, true, true, "");
        verificarEmail(this.parametros);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    private void iniciarProximaActivity() {
        if (UsuarioSingleton.getInstancia().getUsuario().getId() != 0) {
            if (usuarioLogado.isPrimeiroLogin()) {
                if (usuarioLogado.isUsuarioNativo()) {
                    Bundle b = new Bundle();

                    Intent telaPosLogin = new Intent(Login.this, ConfirmarCodigoLogin.class);

                    telaPosLogin.putExtra("gcm", regId);
                    startActivity(telaPosLogin);
                } else {
                    Bundle b = new Bundle();

                    UsuarioSingleton.getInstancia().getUsuario().setGcm(regId);

                    Intent telaPosLogin = new Intent(Login.this, ConfirmarCidade.class);
                    telaPosLogin.putExtra("cidade", usuarioLogado.getEndereco().getCidade());
                    telaPosLogin.putExtra("gcm", regId);
                    startActivity(telaPosLogin);
                }

            } else {
                Intent telaPosLogin;
                if (usuarioLogado.isEsqueceuSenha())
                    telaPosLogin = new Intent(Login.this, NovaSenha.class);
                else
                    telaPosLogin = new Intent(Login.this, MainActivity.class);

                startActivity(telaPosLogin);
            }

            UsuarioSingleton.getInstancia().getUsuario().setPreferenciaVisualizacao(Usuario.PREFERENCIA_VISUALIZACAO_CIDADE);

            PreferenciasUtil.salvarPreferenciasLogin(PreferenciasUtil.KEY_PREFERENCIAS_USUARIO_LOGADO_NOME, usuarioLogado.getNomeCompleto(), getApplicationContext());
            PreferenciasUtil.salvarPreferenciasLogin(PreferenciasUtil.KEY_PREFERENCIAS_USUARIO_LOGADO_EMAIL, usuarioLogado.getEmail(), getApplicationContext());
        }
    }

    public void registerIdInBackground() {
        new AsyncTask() {
            @Override
            protected Object doInBackground(Object... params) {
                String msg = "";
                try {
                    if (ValidadorUtil.isNuloOuVazio(gcm)) {
                        gcm = GoogleCloudMessaging.getInstance(Login.this);
                    }

                    regId = gcm.register(SENDER_ID);
                    msg = "Register Id: " + regId;

                    Log.d("gcm", regId);

                    iniciarProximaActivity();

                    AndroidSystemUtil.storeRegistrationId(Login.this, regId);
                } catch (IOException e) {
                    Log.i(TAG, e.getMessage());
                }

                return msg;
            }

            @Override
            public void onPostExecute(Object msg) {
            }

        }.execute(null, null, null);
    }

    private void logOutGooglePlus() {
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
        }
    }

}