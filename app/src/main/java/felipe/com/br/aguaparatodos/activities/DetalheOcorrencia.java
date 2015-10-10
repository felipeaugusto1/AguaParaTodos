package felipe.com.br.aguaparatodos.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;

import org.apache.http.Header;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;

import felipe.com.br.aguaparatodos.R;
import felipe.com.br.aguaparatodos.dominio.Ocorrencia;
import felipe.com.br.aguaparatodos.singleton.ListaOcorrenciasSingleton;
import felipe.com.br.aguaparatodos.utils.ToastUtil;
import felipe.com.br.aguaparatodos.singleton.UsuarioSingleton;
import felipe.com.br.aguaparatodos.utils.ValidadorUtil;
import felipe.com.br.aguaparatodos.utils.WebService;

/**
 * Created by felipe on 9/4/15.
 */
public class DetalheOcorrencia extends AppCompatActivity {

    private Drawer navigationDrawer;
    private Toolbar toolbar;

    private SupportMapFragment mapFragment;
    private GoogleMap mapa;

    private RequestParams parametros;
    private Ocorrencia ocorrencia;

    private TextView txtTituloOcorrencia, txtQtdConfirmacoes,
            txtDescricaoOcorrencia, txtData, txtPontoReferenciaOcorrencia, txtEnderecoOcorrencia, txtStatusOcorrencia;
    private Button btnConfirmar, btnSolucionar;

    private RelativeLayout layoutCardConfirmacaoOcorrencia, layoutCardStatusOcorrencia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detalhe_ocorrencia);

        this.mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        criarReferenciasComponentes();

        this.toolbar = (Toolbar) findViewById(R.id.toolbar_detalhe_ocorrencia);
        this.toolbar.setTitle(getResources().getString(R.string.tituloTelaDetalheOcorrenia));
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

        Bundle b = getIntent().getExtras();

        if (!ValidadorUtil.isNuloOuVazio(b)) {
            String ocorrenciaId = b.getString("ocorrencia_id");
            //String classe = b.getString("classe");
            //Log.d("classe", classe);
            this.parametros = new RequestParams();
            this.parametros.put("id", ocorrenciaId);
            try {
                this.ocorrencia = ListaOcorrenciasSingleton.getInstancia().buscarPorId(Integer.parseInt(ocorrenciaId));
                if (ValidadorUtil.isNuloOuVazio(this.ocorrencia))
                    buscarOcorrenciasPorIdWS(parametros);
                else
                    percorrerOcorrencias();
            } catch (Exception e) {
                buscarOcorrenciasPorIdWS(parametros);
            }
        }
    }

    private void criarReferenciasComponentes() {
        this.txtTituloOcorrencia = (TextView) findViewById(R.id.txtTituloOcorrencia);
        this.txtPontoReferenciaOcorrencia = (TextView) findViewById(R.id.txtPontoReferenciaOcorrencia);
        this.txtDescricaoOcorrencia = (TextView) findViewById(R.id.txtObsOcorrencia);
        this.txtData = (TextView) findViewById(R.id.txtDataOcorrencia);
        this.txtEnderecoOcorrencia = (TextView) findViewById(R.id.txtEnderecoOcorrencia);
        this.txtQtdConfirmacoes = (TextView) findViewById(R.id.txtQtdConfirmacoes);
        this.txtStatusOcorrencia = (TextView) findViewById(R.id.txtStatusOcorrencia);
        this.layoutCardConfirmacaoOcorrencia = (RelativeLayout) findViewById(R.id.layout_detalhe_ocorrencia_confirmacoes);
        this.layoutCardStatusOcorrencia = (RelativeLayout) findViewById(R.id.layout_detalhe_status_ocorrencia);
        this.btnConfirmar = (Button) findViewById(R.id.btnEnviarOcorrencia);
        this.btnSolucionar = (Button) findViewById(R.id.btnOcorrenciaSolucionada);
    }

    private void buscarOcorrenciasPorIdWS(RequestParams parametros) {
        AsyncHttpClient client = new AsyncHttpClient();

        client.get(WebService.ENDERECO_WS.concat(getResources().getString(R.string.ocorrencia_buscar_id)), parametros,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          byte[] response) {
                        String str = "";
                        try {
                            str = new String(response, "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }

                        Gson gson = new Gson();

                        Type listType = new TypeToken<Ocorrencia>() {
                        }.getType();
                        ocorrencia = gson.fromJson(str, listType);
                        percorrerOcorrencias();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          byte[] errorResponse, Throwable e) {
                        ToastUtil.criarToastLongo(getApplicationContext(), getResources().getString(R.string.msgErroWS));
                    }
                });
    }

    private void percorrerOcorrencias() {
        if (this.ocorrencia.isOcorrenciaSolucionada()) {
            this.btnSolucionar.setBackgroundColor(getResources().getColor(R.color.prata));
            this.btnConfirmar.setBackgroundColor(getResources().getColor(R.color.prata));
            this.btnSolucionar.setClickable(false);
            this.btnConfirmar.setClickable(false);
        }

        try {
            configurarMapa();
            adicionarMarcador();
        } catch (Exception e) {

        }
    }

    private void configurarMapa() {
        if (this.mapa == null) {
            this.mapa = this.mapFragment.getMap();

            /* if (this.mapa != null) {
                this.mapa.setMyLocationEnabled(true);
            } */
        }
    }

    private void adicionarMarcador() {
        if (!ValidadorUtil.isNuloOuVazio(ocorrencia)) {
            this.txtTituloOcorrencia.setText(ocorrencia.getTitulo());
            this.txtEnderecoOcorrencia.setText(ocorrencia.getEnderecoFormatado());

            if (this.ocorrencia.isOcorrenciaSolucionada()) {
                this.layoutCardStatusOcorrencia.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
                this.txtStatusOcorrencia.setText(getResources().getString(R.string.msgStatusOcorrenciaSolucionada));
            }
            else {
                this.layoutCardStatusOcorrencia.setBackgroundColor(getResources().getColor(R.color.vermelho_claro));
                this.txtStatusOcorrencia.setText(getResources().getString(R.string.msgStatusOcorrenciaAguardando));
            }

            if (this.ocorrencia.getDescricao().length() > 0)
                this.txtDescricaoOcorrencia.setText(this.ocorrencia.getDescricao());
            else
                this.txtDescricaoOcorrencia.setText(getResources().getString(R.string.msgCampoNaoInformado));

            if (this.ocorrencia.getPontoReferencia().length() > 0)
                this.txtPontoReferenciaOcorrencia.setText(this.ocorrencia.getPontoReferencia());
            else
                this.txtPontoReferenciaOcorrencia.setText(getResources().getString(R.string.msgCampoNaoInformado));

            this.txtData.setText(new SimpleDateFormat("dd/MM/yyyy").format(this.ocorrencia.getDataCadastro()));

            if (this.ocorrencia.getQtdConfirmacoes() == 1) {
                this.layoutCardConfirmacaoOcorrencia.setBackgroundColor(getResources().getColor(R.color.vermelho_claro));
                this.txtQtdConfirmacoes.setText(String.valueOf(this.ocorrencia.getQtdConfirmacoes()).concat(" ").concat(getResources().getString(R.string.msgPessoasConfirmaramOcorrenciaSingular)));
            } else {
                this.layoutCardConfirmacaoOcorrencia.setBackgroundColor(getResources().getColor(R.color.vermelho_claro));
                this.txtQtdConfirmacoes.setText(String.valueOf(this.ocorrencia.getQtdConfirmacoes()).concat(" ").concat(getResources().getString(R.string.msgPessoasConfirmaramOcorrenciaPlural)));
            }

            LatLng c = new LatLng(this.ocorrencia.getEndereco().getLatitude(),
                    this.ocorrencia.getEndereco().getLongitude());

            MarkerOptions markerOption = null;

            markerOption = new MarkerOptions().position(c).icon(BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

            this.mapa.addMarker(markerOption);

            this.mapa.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
                    ocorrencia.getEndereco().getLatitude(), ocorrencia
                    .getEndereco().getLongitude()), 10));

            this.mapa.animateCamera(CameraUpdateFactory.zoomTo(14));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_compartilhar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (item.getItemId()) {
            case R.id.action_compartilhar:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,
                        ocorrencia.getTitulo()
                                + "\n" + ocorrencia.getEndereco().getEndereco());
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public void acoesBotoesDetalhesOcorrencia(View v) {
        switch (v.getId()) {
            case R.id.btnEnviarOcorrencia:
                criarDialog(DetalheOcorrencia.this, getResources().getString(R.string.dialogTituloConfirmar), getResources().getString(R.string.dialogTextoConfirmar));
                break;
            case R.id.btnOcorrenciaSolucionada:
                criarDialogSolucionarOcorrencia();
        }
    }

    public void criarDialog(final Context contexto, String titulo, String mensagem) {

        AlertDialog dialog = new AlertDialog.Builder(contexto)
                .setTitle(titulo)
                .setMessage(mensagem)
                .setPositiveButton(contexto.getResources().getString(R.string.msgSim), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            if (UsuarioSingleton.getInstancia().getUsuario().getId() != 0) {
                                prepararEnviarOcorrenciaWs();
                                confirmarOcorrenciaWs();
                            } else
                                ToastUtil.criarToastLongo(DetalheOcorrencia.this, "Logue no aplicativo para confirmar esta ocorrência.");

                        } catch (Exception e) {
                            ToastUtil.criarToastLongo(DetalheOcorrencia.this, getResources().getString(R.string.msgErroWS));
                        }
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

    public void criarDialogSolucionarOcorrencia() {

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Ocorrência Solucionada")
                .setMessage("Por favor, somente continue esta operação se o problema foi realmente solucionado.")
                .setPositiveButton("Continuar", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            if (UsuarioSingleton.getInstancia().getUsuario().getId() != 0) {
                                prepararSolucionarOcorrenciaWs();
                                ocorrenciaSolucionadaWs();
                            } else
                                ToastUtil.criarToastLongo(DetalheOcorrencia.this, "Logue no aplicativo para marcar esta ocorrência como solucionada.");
                        } catch (Exception e) {
                            ToastUtil.criarToastLongo(DetalheOcorrencia.this, getResources().getString(R.string.msgErroWS));
                        }
                    }
                })
                .setNegativeButton("Parar", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();

        dialog.show();

    }

    private void prepararEnviarOcorrenciaWs() {
        this.parametros = new RequestParams();
        parametros.put("ocorrencia_id", String.valueOf(this.ocorrencia.getId()));
        parametros.put("usuario_id", String.valueOf(UsuarioSingleton.getInstancia().getUsuario().getId()));
    }

    private void prepararSolucionarOcorrenciaWs() {
        this.parametros = new RequestParams();
        this.parametros.put("ocorrencia_id", String.valueOf(this.ocorrencia.getId()));
    }

    private void ocorrenciaSolucionadaWs() {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(WebService.ENDERECO_WS.concat(getResources().getString(R.string.ocorrencia_solucionada)), this.parametros,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          byte[] response) {
                        String str = "";
                        try {
                            str = new String(response, "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }

                        if (str.equalsIgnoreCase(WebService.RESPOSTA_SUCESSO)) {
                            layoutCardStatusOcorrencia.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
                            txtStatusOcorrencia.setText(getResources().getString(R.string.msgStatusOcorrenciaSolucionada));

                            ToastUtil.criarToastLongo(DetalheOcorrencia.this,
                                    "Ocorrência marcada como solucionada!"
                            );


                        } else
                            ToastUtil.criarToastLongo(DetalheOcorrencia.this,
                                    getResources().getString(R.string.msgErroWS)
                            );
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          byte[] errorResponse, Throwable e) {

                    }
                });
    }


    private void confirmarOcorrenciaWs() {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(WebService.ENDERECO_WS.concat(getResources().getString(R.string.ocorrencia_confirmar)), parametros,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          byte[] response) {
                        String str = "";
                        try {
                            str = new String(response, "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }

                        if (str.equalsIgnoreCase(WebService.RESPOSTA_SUCESSO)) {
                            ToastUtil.criarToastLongo(DetalheOcorrencia.this,
                                    getResources().getString(R.string.msgSucessoConfirmar)
                            );

                            try {
                                if ((ocorrencia.getQtdConfirmacoes() + 1) > 1) {
                                    txtQtdConfirmacoes.setText(String.valueOf((ocorrencia.getQtdConfirmacoes() + 1)).concat(" ").concat(
                                            getResources().getString(R.string.msgPessoasConfirmaramOcorrenciaPlural)
                                    ));
                                    layoutCardConfirmacaoOcorrencia.setBackgroundColor(getResources().getColor(R.color.vermelho_claro));
                                } else if ((ocorrencia.getQtdConfirmacoes() + 1) == 1) {
                                    txtQtdConfirmacoes.setText(String.valueOf((ocorrencia.getQtdConfirmacoes() + 1)).concat(" ").concat(
                                            getResources().getString(R.string.msgPessoasConfirmaramOcorrenciaSingular)
                                    ));
                                    layoutCardConfirmacaoOcorrencia.setBackgroundColor(getResources().getColor(R.color.vermelho_claro));
                                }
                            } catch (Exception e) {
                            }

                        } else if (str.equalsIgnoreCase(WebService.RESPOSTA_ACESSO_NEGADO)) {
                            ToastUtil.criarToastLongo(DetalheOcorrencia.this,
                                    getResources().getString(R.string.msgErroConfirmarProibido)
                            );
                        } else
                            ToastUtil.criarToastLongo(DetalheOcorrencia.this,
                                    getResources().getString(R.string.msgErroWS)
                            );
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          byte[] errorResponse, Throwable e) {

                    }
                });
    }

}
