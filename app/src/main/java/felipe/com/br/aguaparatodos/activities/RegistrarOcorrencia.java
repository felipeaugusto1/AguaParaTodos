package felipe.com.br.aguaparatodos.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import felipe.com.br.aguaparatodos.R;
import felipe.com.br.aguaparatodos.utils.BuscarEnderecoGoogle;
import felipe.com.br.aguaparatodos.utils.ToastUtil;
import felipe.com.br.aguaparatodos.utils.UsuarioSingleton;
import felipe.com.br.aguaparatodos.utils.ValidadorUtil;
import felipe.com.br.aguaparatodos.utils.WebService;

/**
 * Created by felipe on 8/24/15.
 */
public class RegistrarOcorrencia extends AppCompatActivity {

    private static final String LOG_TAG = "Projeto Agua para Todos";

    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    private static final String PAIS = "br";
    private static final String LANGUAGE = "pt-BR";
    private static final String GOOGLE_API_KEY = "AIzaSyApev4-PxnD258_TnkDCcCL_KTOXwhjU7M";
    private AutoCompleteTextView enderecoAutoComplete;

    private EditText edTituloOcorrencia, edObservacaoOcorrencia, edPontoReferenciaOcorrencia;
    private TextView txtContadorOcorrencia, txtContadorTitulo, txtContadorPontoReferencia;
    private static int TAMANHO_MAXIMO_DESCRICAO_OCORRENCIA = 50, TAMANHO_MAXIMO_TITULO_OCORRENCIA = 30, TAMANHO_MAXIMO_REFERENCIA_OCORRENCIA = 30;
    private int contadorDescricaoOcorrencia = TAMANHO_MAXIMO_DESCRICAO_OCORRENCIA;
    private int contadorTituloOcorrencia = TAMANHO_MAXIMO_TITULO_OCORRENCIA;
    private int contadorReferenciaOcorrencia = TAMANHO_MAXIMO_REFERENCIA_OCORRENCIA;
    private Button btnCadastrarOcorrencia;

    private RequestParams parametros;
    private static ProgressDialog progressDialog;

    private Drawer navigationDrawer;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registrar_ocorrencia);

        criarReferenciasComponentes();

        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.toolbar.setTitle(getResources().getString(R.string.tituloTelaRegistrarOcorrenia));
        //this.toolbar.setBackgroundColor(getResources().getColor(R.color.vermelho));
        this.toolbar.setTitleTextColor(getResources().getColor(R.color.branco));
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
                Intent intent = new Intent(RegistrarOcorrencia.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

    }

    private void criarReferenciasComponentes() {
        this.enderecoAutoComplete = (AutoCompleteTextView) findViewById(R.id.editTextEnderecoOcorrencia);
        this.enderecoAutoComplete.setAdapter(new PlacesAutoCompleteAdapter(getApplicationContext(), R.layout.item_lista_busca_endereco));

        this.txtContadorOcorrencia = (TextView) findViewById(R.id.textViewContadorDescricao);
        this.txtContadorOcorrencia.setText(contadorDescricaoOcorrencia + " " +getResources().getString(R.string.msgCaracteresRestantes));

        this.txtContadorTitulo = (TextView) findViewById(R.id.textViewContadorTitulo);
        this.txtContadorTitulo.setText(contadorTituloOcorrencia + " " + getResources().getString(R.string.msgCaracteresRestantes));

        this.txtContadorPontoReferencia = (TextView) findViewById(R.id.textViewContadorReferencia);
        this.txtContadorPontoReferencia.setText(contadorReferenciaOcorrencia + " " +getResources().getString(R.string.msgCaracteresRestantes));
        this.edPontoReferenciaOcorrencia = (EditText) findViewById(R.id.editTextPontoReferenciaOcorrencia);
        this.edPontoReferenciaOcorrencia.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                int tamanhoAtual = edPontoReferenciaOcorrencia.getText().toString().length();
                if (contadorReferenciaOcorrencia > 1)
                    txtContadorPontoReferencia.setText((contadorReferenciaOcorrencia - tamanhoAtual) + " " + getResources().getString(R.string.msgCaracteresRestantes));
                else if (contadorReferenciaOcorrencia == 1)
                    txtContadorPontoReferencia.setText((contadorReferenciaOcorrencia - tamanhoAtual) + " " + getResources().getString(R.string.msgCaracterRestante));
                contadorReferenciaOcorrencia = TAMANHO_MAXIMO_REFERENCIA_OCORRENCIA;
            }
        });


        this.edTituloOcorrencia = (EditText) findViewById(R.id.editTextTituloOcorrencia);
        this.edTituloOcorrencia.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                int tamanhoAtual = edTituloOcorrencia.getText().toString().length();
                if (contadorTituloOcorrencia > 1)
                    txtContadorTitulo.setText((contadorTituloOcorrencia - tamanhoAtual) + " " + getResources().getString(R.string.msgCaracteresRestantes));
                else if (contadorTituloOcorrencia == 1)
                    txtContadorTitulo.setText((contadorTituloOcorrencia - tamanhoAtual) + " " + getResources().getString(R.string.msgCaracterRestante));
                contadorTituloOcorrencia = TAMANHO_MAXIMO_TITULO_OCORRENCIA;
            }
        });

        this.edObservacaoOcorrencia = (EditText) findViewById(R.id.editTextDescricaoOcorrencia);
        this.edObservacaoOcorrencia.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int tamanhoAtual = edObservacaoOcorrencia.getText().toString().length();
                if (contadorDescricaoOcorrencia > 1)
                    txtContadorOcorrencia.setText((contadorDescricaoOcorrencia - tamanhoAtual) + " " + getResources().getString(R.string.msgCaracteresRestantes));
                else if (contadorDescricaoOcorrencia == 1)
                    txtContadorOcorrencia.setText((contadorDescricaoOcorrencia - tamanhoAtual) + " " + getResources().getString(R.string.msgCaracterRestante));
                contadorDescricaoOcorrencia = TAMANHO_MAXIMO_DESCRICAO_OCORRENCIA;
            }
        });

        this.btnCadastrarOcorrencia = (Button) findViewById(R.id.btnEnviarOcorrencia);
        this.btnCadastrarOcorrencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                criarDialog(RegistrarOcorrencia.this, "Confirma os dados?", "Por favor...");

            }
        });
    }

    private void prepararParametros() {
        try {
            List<Double> coordenadasEndereco = BuscarEnderecoGoogle.buscarCoordenadasPorEndereco(getApplicationContext(), this.enderecoAutoComplete.getText().toString());
            Map<String, String> valores = BuscarEnderecoGoogle.buscarEnderecoByNome(enderecoAutoComplete.getText().toString(), getApplicationContext());

            Log.d("ENDERECO", valores.get("ENDERECO"));
            Log.d("CIDADE", valores.get("CIDADE"));
            Log.d("ESTADO", valores.get("ESTADO"));

            this.parametros = new RequestParams();

            this.parametros.put("titulo", this.edTituloOcorrencia.getText().toString());
            this.parametros.put("descricao", this.edObservacaoOcorrencia.getText().toString());
            this.parametros.put("referencia", this.edPontoReferenciaOcorrencia.getText().toString());
            this.parametros.put("latitude", String.valueOf(coordenadasEndereco.get(0)));
            this.parametros.put("longitude", String.valueOf(coordenadasEndereco.get(1)));
            this.parametros.put("id_usuario", String.valueOf(UsuarioSingleton.getInstancia().getUsuario().getId()));
            this.parametros.put("endereco", String.valueOf(valores.get("ENDERECO")));
            this.parametros.put("cidade", String.valueOf(valores.get("CIDADE")));
            this.parametros.put("estado", String.valueOf(valores.get("ESTADO")));

            this.enviarOcorrencia(this.parametros);
        } catch (Exception e) {
            ToastUtil.criarToastLongo(getApplicationContext(), getResources().getString(R.string.erroBuscarEndereco));
        }


    }

    private boolean enviarOcorrencia(RequestParams parametros) {
        AsyncHttpClient client = new AsyncHttpClient();

        client.get(WebService.ENDERECO_WS.concat(getResources().getString(R.string.ocorrencia_nova)), parametros, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {

                String str = "";
                try {
                    str = new String(bytes, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                if (str.equalsIgnoreCase("sucesso")) {
                    progressDialog.dismiss();

                    ToastUtil.criarToastLongo(getApplicationContext(),
                            getResources().getString(R.string.msgSucessoEnviarOcorrencia));

                    limparCampos();

                    startActivity(new Intent(RegistrarOcorrencia.this, MainActivity.class));
                } else if (str.equalsIgnoreCase("erro")) {
                    progressDialog.dismiss();

                    ToastUtil.criarToastLongo(getApplicationContext(),
                            getResources().getString(R.string.msgErroEnviarOcorrencia));
                }

            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                progressDialog.dismiss();

                ToastUtil.criarToastLongo(getApplicationContext(), getResources().getString(R.string.msgErroWS));
            }

        });

        return true;
    }

    private void limparCampos() {
        this.edTituloOcorrencia.setText("");
        this.edObservacaoOcorrencia.setText("");
        this.enderecoAutoComplete.setText("");
        this.edPontoReferenciaOcorrencia.setText("");

        this.edTituloOcorrencia.setError(null);
        this.edObservacaoOcorrencia.setError(null);
        this.enderecoAutoComplete.setError(null);
        this.edPontoReferenciaOcorrencia.setError(null);
    }

    public void limparCampos(View v) {
        this.edTituloOcorrencia.setText("");
        this.edObservacaoOcorrencia.setText("");
        this.enderecoAutoComplete.setText("");
        this.edPontoReferenciaOcorrencia.setText("");

        this.edTituloOcorrencia.setError(null);
        this.edObservacaoOcorrencia.setError(null);
        this.enderecoAutoComplete.setError(null);
        this.edPontoReferenciaOcorrencia.setError(null);
    }

    private void validarCamposEnviarWs() {
        this.edTituloOcorrencia.setError(null);
        //edObservacaoOcorrencia.setError(null);
        this.enderecoAutoComplete.setError(null);
        //edPontoReferenciaOcorrencia.setError(null);

        ValidadorUtil.validarCampoEmBranco(this.edTituloOcorrencia, getResources().getString(R.string.erroInformarTituloOcorrencia));
        ValidadorUtil.validarCampoEmBranco(this.enderecoAutoComplete, getResources().getString(R.string.erroInformarEnderecoOcorrencia));

        if (ValidadorUtil.isNuloOuVazio(edTituloOcorrencia.getError()) && ValidadorUtil.isNuloOuVazio(this.enderecoAutoComplete.getError())) {
            progressDialog = ProgressDialog.show(RegistrarOcorrencia.this, getResources().getString(R.string.aguarde),
                    getResources().getString(R.string.msgCadastrandoOcorrencia));

            prepararParametros();
        }
    }

    public void criarDialog(final Context contexto, String titulo, String mensagem) {

        AlertDialog dialog =  new AlertDialog.Builder(contexto)
                .setTitle(titulo)
                .setMessage(mensagem)
                .setPositiveButton(contexto.getResources().getString(R.string.msgSim), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        validarCamposEnviarWs();
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

    @Override
    public void onBackPressed() {
        this.navigationDrawer.setSelection(0);
        super.onBackPressed();
    }

    private class PlacesAutoCompleteAdapter extends ArrayAdapter<String>
            implements Filterable {
        private ArrayList<String> resultList;

        public PlacesAutoCompleteAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        @Override
        public int getCount() {
            return resultList.size();
        }

        @Override
        public String getItem(int index) {
            return resultList.get(index);
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (constraint != null) {
                        // Retrieve the autocomplete results.
                        resultList = autocomplete(constraint.toString());

                        // Assign the data to the FilterResults
                        filterResults.values = resultList;
                        filterResults.count = resultList.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint,
                                              FilterResults results) {
                    if (results != null && results.count > 0) {
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }
            };
            return filter;
        }
    }

    private ArrayList<String> autocomplete(String input) {
        ArrayList<String> resultList = null;

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE
                    + TYPE_AUTOCOMPLETE + OUT_JSON);
            sb.append("?key=" + GOOGLE_API_KEY);
            sb.append("&components=country:" + PAIS);
            sb.append("&language=" + LANGUAGE);
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));

            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error processing Places API URL", e);
            return resultList;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error connecting to Places API", e);
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

            // Extract the Place descriptions from the results
            resultList = new ArrayList<String>(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                resultList.add(predsJsonArray.getJSONObject(i).getString(
                        "description"));
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Cannot process JSON results", e);
        }

        return resultList;
    }
}