package felipe.com.br.aguaparatodos.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.interfaces.OnCheckedChangeListener;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SwitchDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import org.apache.http.Header;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import felipe.com.br.aguaparatodos.R;
import felipe.com.br.aguaparatodos.dominio.Ocorrencia;
import felipe.com.br.aguaparatodos.utils.PreferenciasUtil;
import felipe.com.br.aguaparatodos.utils.ToastUtil;
import felipe.com.br.aguaparatodos.utils.UsuarioSingleton;
import felipe.com.br.aguaparatodos.utils.ValidadorUtil;
import felipe.com.br.aguaparatodos.utils.WebService;

import static felipe.com.br.aguaparatodos.R.string.app_name;

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

    private TextView txtTituloOcorrencia,
            txtDescricaoOcorrencia, txtData, txtPontoReferenciaOcorrencia, txtEnderecoOcorrencia;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detalhe_ocorrencia);

        this.mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        //configurarMapa();
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
                Intent intent = new Intent(DetalheOcorrencia.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        Bundle b = getIntent().getExtras();

        if (b != null) {
            String ocorrenciaId = b.getString("ocorrencia_id");
            this.parametros = new RequestParams();
            this.parametros.put("id", ocorrenciaId);
            buscarOcorrenciasPorIdWS(parametros);
        }
    }

    private void criarReferenciasComponentes() {
        this.txtTituloOcorrencia = (TextView) findViewById(R.id.txtTituloOcorrencia);
        this.txtPontoReferenciaOcorrencia = (TextView) findViewById(R.id.txtPontoReferenciaOcorrencia);
        this.txtDescricaoOcorrencia = (TextView) findViewById(R.id.txtObsOcorrencia);
        this.txtData = (TextView) findViewById(R.id.txtDataOcorrencia);
        this.txtEnderecoOcorrencia = (TextView) findViewById(R.id.txtEnderecoOcorrencia);
        //this.qtdConfirmacoes = (TextView) findViewById(R.id.txtQtdConfirmacoes);
        //this.qtdDenuncias = (TextView) findViewById(R.id.txtQtdDenuncias);
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
        try {
            configurarMapa();
            adicionarMarcador();
        } catch (Exception e) {

        }
    }

    private void configurarMapa() {
        /* this.txtTituloOcorrencia.setText(ocorrencia.getTitulo());
        this.txtDescricaoOcorrencia.setText(ocorrencia.getDescricao());

        String dataFormatada = new SimpleDateFormat("dd/MM/yyyy").format(ocorrencia.getDataCadastro());

        this.txtData.setText(dataFormatada);
        this.qtdConfirmacoes.setText("quantidade denuncias: " + ocorrencia.getQtdDenuncias()); */

        if (this.mapa == null) {
            this.mapa = this.mapFragment.getMap();

            if (this.mapa != null) {
                this.mapa.setMyLocationEnabled(true);
            }
        }



    }

    private void adicionarMarcador() {
        Log.d("ocorrencia", ocorrencia.toString());
        if (!ValidadorUtil.isNuloOuVazio(ocorrencia)) {
            this.txtTituloOcorrencia.setText(ocorrencia.getTitulo());
            this.txtEnderecoOcorrencia.setText(ocorrencia.getEnderecoFormatado());

            if (ocorrencia.getDescricao().length() > 0)
                this.txtDescricaoOcorrencia.setText(ocorrencia.getDescricao());
            else
                this.txtDescricaoOcorrencia.setText("Campo não informado.");

            if (ocorrencia.getPontoReferencia().length() > 0)
                this.txtPontoReferenciaOcorrencia.setText(ocorrencia.getPontoReferencia());
            else
                this.txtPontoReferenciaOcorrencia.setText("Campo não informado.");

            String dataFormatada = new SimpleDateFormat("dd/MM/yyyy").format(ocorrencia.getDataCadastro());

            this.txtData.setText(dataFormatada);
            //this.qtdConfirmacoes.setText("quantidade denuncias: " + ocorrencia.getQtdDenuncias());

            LatLng c = new LatLng(ocorrencia.getEndereco().getLatitude(),
                    ocorrencia.getEndereco().getLongitude());

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
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
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

}
