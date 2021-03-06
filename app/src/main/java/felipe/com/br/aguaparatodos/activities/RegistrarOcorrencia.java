package felipe.com.br.aguaparatodos.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;

import org.apache.http.Header;

import java.io.UnsupportedEncodingException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import felipe.com.br.aguaparatodos.R;
import felipe.com.br.aguaparatodos.dominio.Ocorrencia;
import felipe.com.br.aguaparatodos.dominio.Usuario;
import felipe.com.br.aguaparatodos.extras.PlacesAutoCompleteAdapter;
import felipe.com.br.aguaparatodos.gps.FusedLocationPosition;
import felipe.com.br.aguaparatodos.singleton.ListaOcorrenciasSingleton;
import felipe.com.br.aguaparatodos.utils.BuscarEnderecoGoogle;

import felipe.com.br.aguaparatodos.utils.ToastUtil;
import felipe.com.br.aguaparatodos.singleton.UsuarioSingleton;
import felipe.com.br.aguaparatodos.utils.ValidadorUtil;
import felipe.com.br.aguaparatodos.utils.WebService;

/**
 * Created by felipe on 8/24/15.
 */
public class RegistrarOcorrencia extends AppCompatActivity {

    private AutoCompleteTextView enderecoAutoComplete;

    private EditText edTituloOcorrencia, edObservacaoOcorrencia, edPontoReferenciaOcorrencia;
    private TextView txtContadorOcorrencia, txtContadorTitulo, txtContadorPontoReferencia;
    private static int TAMANHO_MAXIMO_DESCRICAO_OCORRENCIA = 50, TAMANHO_MAXIMO_TITULO_OCORRENCIA = 30, TAMANHO_MAXIMO_REFERENCIA_OCORRENCIA = 30;
    private int contadorDescricaoOcorrencia = TAMANHO_MAXIMO_DESCRICAO_OCORRENCIA;
    private int contadorTituloOcorrencia = TAMANHO_MAXIMO_TITULO_OCORRENCIA;
    private int contadorReferenciaOcorrencia = TAMANHO_MAXIMO_REFERENCIA_OCORRENCIA;
    private Button btnCadastrarOcorrencia;
    private RadioGroup radioGroupTipoEnderecoUtilizado;
    private RadioButton radioButtonUtilizarGps, radioButtonUtilizarEndereco;

    private boolean utilizarGps;
    private boolean gpsUsuarioAtivado;

    private Location localizacaoUsuario;
    private FusedLocationPosition fusedLocationService;
    private LocationManager myLocationManager;

    private RequestParams parametros;
    private static ProgressDialog progressDialog;

    private Drawer navigationDrawer;
    private Toolbar toolbar;

    private Map<String, String> valores;
    private List<Double> coordenadasEndereco;

    private String enderecoFormatado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registrar_ocorrencia);

        criarReferenciasComponentes();

        if (this.myLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            recuperarPosicaoUsuario();
            this.gpsUsuarioAtivado = true;
        }


        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.toolbar.setTitle(getResources().getString(R.string.tituloTelaRegistrarOcorrenia));
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

    private void recuperarPosicaoUsuario() {
        this.localizacaoUsuario = fusedLocationService.getLocation();
    }

    private void criarReferenciasComponentes() {
        this.fusedLocationService = new FusedLocationPosition(this);

        this.myLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        this.radioGroupTipoEnderecoUtilizado = (RadioGroup) findViewById(R.id.radioGroupTipoEndereco);
        this.radioButtonUtilizarGps = (RadioButton) findViewById(R.id.radioButtoUtilizarPosicao);
        this.radioButtonUtilizarEndereco = (RadioButton) findViewById(R.id.radioButtonInformarEndereco);

        this.enderecoAutoComplete = (AutoCompleteTextView) findViewById(R.id.editTextEnderecoOcorrencia);
        this.enderecoAutoComplete.setAdapter(new PlacesAutoCompleteAdapter(getApplicationContext(), R.layout.item_lista_busca_endereco));

        this.radioButtonUtilizarGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    recuperarPosicaoUsuario();
                    gpsUsuarioAtivado = true;
                } else
                    gpsUsuarioAtivado = false;

                utilizarGps = true;
                enderecoAutoComplete.setEnabled(false);
                if (!gpsUsuarioAtivado) {
                    alertaGPSDesativado();
                    ToastUtil.criarToastLongo(RegistrarOcorrencia.this, getResources().getString(R.string.msgAtivarGps));
                    enderecoAutoComplete.setEnabled(true);
                }
            }
        });

        this.radioButtonUtilizarEndereco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                utilizarGps = false;
                enderecoAutoComplete.setEnabled(true);
            }
        });

        this.txtContadorOcorrencia = (TextView) findViewById(R.id.textViewContadorDescricao);
        this.txtContadorOcorrencia.setText(contadorDescricaoOcorrencia + " " + getResources().getString(R.string.msgCaracteresRestantes));

        this.txtContadorTitulo = (TextView) findViewById(R.id.textViewContadorTitulo);
        this.txtContadorTitulo.setText(contadorTituloOcorrencia + " " + getResources().getString(R.string.msgCaracteresRestantes));

        this.txtContadorPontoReferencia = (TextView) findViewById(R.id.textViewContadorReferencia);
        this.txtContadorPontoReferencia.setText(contadorReferenciaOcorrencia + " " + getResources().getString(R.string.msgCaracteresRestantes));
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
                if (contadorReferenciaOcorrencia >= 1)
                    txtContadorPontoReferencia.setText((contadorReferenciaOcorrencia - tamanhoAtual) + " " + getResources().getString(R.string.msgCaracteresRestantes));
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
                if (contadorDescricaoOcorrencia >= 1)
                    txtContadorOcorrencia.setText((contadorDescricaoOcorrencia - tamanhoAtual) + " " + getResources().getString(R.string.msgCaracteresRestantes));
                contadorDescricaoOcorrencia = TAMANHO_MAXIMO_DESCRICAO_OCORRENCIA;
            }
        });

        this.btnCadastrarOcorrencia = (Button) findViewById(R.id.btnEnviarOcorrencia);
        this.btnCadastrarOcorrencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                criarDialog(RegistrarOcorrencia.this, getResources().getString(R.string.confirmarDados), getResources().getString(R.string.infoDadosVeridicos));

            }
        });
    }

    private void prepararParametros() {
        List<Address> endereco = new ArrayList<Address>();

        try {
            if (!utilizarGps) {
                this.coordenadasEndereco = BuscarEnderecoGoogle.buscarCoordenadasPorEndereco(getApplicationContext(), this.enderecoAutoComplete.getText().toString());
                this.valores = BuscarEnderecoGoogle.buscarEnderecoByNome(enderecoAutoComplete.getText().toString(), getApplicationContext());
            } else {
                endereco = BuscarEnderecoGoogle.buscarEnderecoByCoordenadas(RegistrarOcorrencia.this, this.localizacaoUsuario.getLatitude(), this.localizacaoUsuario.getLongitude());
                this.valores = BuscarEnderecoGoogle.buscarEnderecoByNome(String.valueOf(endereco.get(0).getAddressLine(0)), getApplicationContext());
            }

            this.enderecoFormatado = String.valueOf(valores.get("ENDERECO")).concat(", ").concat(String.valueOf(valores.get("CIDADE")));

            this.parametros = new RequestParams();

            this.parametros.put("titulo", this.edTituloOcorrencia.getText().toString());
            this.parametros.put("descricao", this.edObservacaoOcorrencia.getText().toString());
            this.parametros.put("referencia", this.edPontoReferenciaOcorrencia.getText().toString());
            if (!utilizarGps) {
                this.parametros.put("latitude", String.valueOf(coordenadasEndereco.get(0)));
                this.parametros.put("longitude", String.valueOf(coordenadasEndereco.get(1)));
            } else {
                this.parametros.put("latitude", String.valueOf(this.localizacaoUsuario.getLatitude()));
                this.parametros.put("longitude", String.valueOf(this.localizacaoUsuario.getLongitude()));
            }

            this.parametros.put("id_usuario", String.valueOf(UsuarioSingleton.getInstancia().getUsuario().getId()));
            this.parametros.put("endereco", String.valueOf(valores.get("ENDERECO")));
            this.parametros.put("cidade", String.valueOf(valores.get("CIDADE")));
            this.parametros.put("estado", String.valueOf(valores.get("ESTADO")));

            this.enviarOcorrencia();
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtil.criarToastLongo(getApplicationContext(), getResources().getString(R.string.erroBuscarEndereco));
        }
    }

    private void enviarOcorrencia() {
        AsyncHttpClient client = new AsyncHttpClient();

        client.get(WebService.ENDERECO_WS.concat(getResources().getString(R.string.ocorrencia_nova)), this.parametros, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {

                String str = "";
                try {
                    str = new String(bytes, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                progressDialog.dismiss();

                Gson gson = new GsonBuilder().setDateFormat(
                        "yyyy-MM-dd'T'HH:mm:ss").create();

                Ocorrencia ocorrencia = gson.fromJson(str, Ocorrencia.class);

                ListaOcorrenciasSingleton.getInstancia().getLista().add(ocorrencia);

                if (!ValidadorUtil.isNuloOuVazio(ocorrencia) && ocorrencia.getId() > 0) {
                    ToastUtil.criarToastLongo(getApplicationContext(),
                            getResources().getString(R.string.msgSucessoEnviarOcorrencia));

                    compartilharInformacao(RegistrarOcorrencia.this, edTituloOcorrencia.getText().toString(), "Você deseja compartilhar essa informação?");
                } else {
                    progressDialog.dismiss();

                    ToastUtil.criarToastLongo(getApplicationContext(),
                            getResources().getString(R.string.msgErroEnviarOcorrencia));
                }

                /* if (str.equalsIgnoreCase("sucesso")) {

                    ToastUtil.criarToastLongo(getApplicationContext(),
                            getResources().getString(R.string.msgSucessoEnviarOcorrencia));

                    compartilharInformacao(RegistrarOcorrencia.this, edTituloOcorrencia.getText().toString(), "Você deseja compartilhar essa informação?");
                } else if (str.equalsIgnoreCase("erro")) {
                    progressDialog.dismiss();

                    ToastUtil.criarToastLongo(getApplicationContext(),
                            getResources().getString(R.string.msgErroEnviarOcorrencia));
                } */

            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                progressDialog.dismiss();

                ToastUtil.criarToastLongo(getApplicationContext(), getResources().getString(R.string.msgErroWS));
            }

        });
    }

    private void compartilharInformacao(Context contexto, String titulo, String mensagem) {
        AlertDialog dialog = new AlertDialog.Builder(contexto)
                .setTitle(titulo)
                .setMessage(mensagem)
                .setPositiveButton(contexto.getResources().getString(R.string.msgSim), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        limparCampos();

                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT,
                                edTituloOcorrencia.getText().toString()
                                        + "\n" + enderecoFormatado);
                        sendIntent.setType("text/plain");
                        startActivity(sendIntent);
                    }
                })
                .setNegativeButton(contexto.getResources().getString(R.string.msgNao), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        limparCampos();

                        startActivity(new Intent(RegistrarOcorrencia.this, MainActivity.class));
                    }
                }).create();

        dialog.show();
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
        if (!gpsUsuarioAtivado || radioButtonUtilizarEndereco.isChecked())
            ValidadorUtil.validarCampoEmBranco(this.enderecoAutoComplete, getResources().getString(R.string.erroInformarEnderecoOcorrencia));

        if (ValidadorUtil.isNuloOuVazio(edTituloOcorrencia.getError()) && (ValidadorUtil.isNuloOuVazio(this.enderecoAutoComplete.getError()) || (gpsUsuarioAtivado && radioButtonUtilizarGps.isChecked()))) {
            progressDialog = ProgressDialog.show(RegistrarOcorrencia.this, getResources().getString(R.string.aguarde),
                    getResources().getString(R.string.msgCadastrandoOcorrencia));

            prepararParametros();
        }
    }

    public void criarDialog(final Context contexto, String titulo, String mensagem) {

        AlertDialog dialog = new AlertDialog.Builder(contexto)
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
        super.onBackPressed();
    }

    private void alertaGPSDesativado() {
        android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(this);
        alertDialogBuilder
                .setMessage(getResources().getString(R.string.gpsDesativado))
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.ativar),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }
                        });
        alertDialogBuilder.setNegativeButton(
                getResources().getString(android.R.string.cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        radioButtonUtilizarEndereco.setChecked(true);
                        radioButtonUtilizarGps.setChecked(false);
                        dialog.cancel();
                    }
                });
        android.support.v7.app.AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

}