package felipe.com.br.aguaparatodos.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.maps.android.SphericalUtil;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;

import org.apache.http.Header;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import felipe.com.br.aguaparatodos.R;
import felipe.com.br.aguaparatodos.dominio.Ocorrencia;
import felipe.com.br.aguaparatodos.gps.FusedLocationPosition;
import felipe.com.br.aguaparatodos.utils.ToastUtil;
import felipe.com.br.aguaparatodos.utils.ValidadorUtil;
import felipe.com.br.aguaparatodos.utils.WebService;

/**
 * Created by felipe on 9/20/15.
 */
public class AoRedor extends AppCompatActivity {

    private Drawer navigationDrawer;
    private Toolbar toolbar;

    private SupportMapFragment mapFragment;
    private LocationRequest locationRequest;
    private GoogleMap mapa;

    private static ProgressDialog progressDialog;
    private List<Ocorrencia> listaOcorrencias;
    private HashMap<Marker, Ocorrencia> marcadoresHashMap;
    private static final long ONE_MIN = 1000 * 60;

    private static final int DISTANCIA_RAIO = 1000;
    private Location localizacaoUsuario;
    private FusedLocationPosition fusedLocationService;
    private LocationManager myLocationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ao_redor);

        this.fusedLocationService = new FusedLocationPosition(this);

        this.myLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!this.myLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            alertaGPSDesativado();
            ToastUtil.criarToastLongo(AoRedor.this, "Por favor ative o GPS para usar esta funcionalidade.");
        } else
           recuperarPosicaoUsuario();



        this.toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        this.toolbar.setTitle(getResources().getString(R.string.tela_ao_redor));
        this.toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        setSupportActionBar(this.toolbar);

        this.mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

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
                Intent intent = new Intent(AoRedor.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        this.configurarMapa();

        this.mapa.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                final Ocorrencia ocorrencia = marcadoresHashMap.get(marker);

                Intent telaDetalheOcorrencia = new Intent(AoRedor.this,
                        DetalheOcorrencia.class);

                telaDetalheOcorrencia.putExtra("ocorrencia_id",
                        String.valueOf(ocorrencia.getId()));
                telaDetalheOcorrencia.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
                        | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                startActivity(telaDetalheOcorrencia);
            }
        });


        this.atualizarMapa();

    }

    private void recuperarPosicaoUsuario() {
        progressDialog = ProgressDialog.show(AoRedor.this,
                "Aguarde...",
                "Estamos recuperando sua posição...");

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                localizacaoUsuario = fusedLocationService.getLocation();
                adicionarMarcadorPosicaoUsuario();
                criarCirculo();
                adicionarMarcadores();
                progressDialog.dismiss();
            }
        }, 5000);
    }

    private void configurarMapa() {
        if (ValidadorUtil.isNuloOuVazio(this.mapa)) {
            this.mapa = this.mapFragment.getMap();

            if (!ValidadorUtil.isNuloOuVazio(this.mapa)) {
                this.mapa.setMyLocationEnabled(true);
            }
        }
    }

    private void atualizarMapa() {
        this.listaOcorrencias = new ArrayList<Ocorrencia>();
        this.marcadoresHashMap = new HashMap<Marker, Ocorrencia>();

        this.buscarOcorrenciasWS();
    }

    private void adicionarMarcadorPosicaoUsuario() {
        if (ValidadorUtil.isNuloOuVazio(this.localizacaoUsuario))
            recuperarPosicaoUsuario();

        if (!ValidadorUtil.isNuloOuVazio(this.localizacaoUsuario)) {
            MarkerOptions markerOption = null;
            markerOption = new MarkerOptions().position(
                    new LatLng(this.localizacaoUsuario.getLatitude(), this.localizacaoUsuario
                            .getLongitude())).icon(
                    BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_RED));

            this.mapa.addMarker(markerOption);
        }


        //this.mapa.animateCamera(CameraUpdateFactory.zoomTo(14));
        //this.mapa.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(this.localizacaoUsuario.getLatitude(), this.localizacaoUsuario.getLongitude()), 10));
    }

    private void criarCirculo() {
        Circle circle = null;
        try {
            this.mapa.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(this.localizacaoUsuario.getLatitude(),
                            this.localizacaoUsuario.getLongitude()), 10));

            this.mapa.animateCamera(CameraUpdateFactory.zoomTo(14));

            circle = this.mapa.addCircle(new CircleOptions()
                    .center(new LatLng(this.localizacaoUsuario.getLatitude(),
                            this.localizacaoUsuario.getLongitude()))
                    .radius(DISTANCIA_RAIO).strokeWidth(1)
                    .strokeColor(Color.BLACK));

            // verde: 0x7F00FF00
            // amarelo: 0x40e5ff00
            // vermelho: 0x40ff0000
            circle.setFillColor(0x40ff0000);
        } catch (Exception e) {
        }


    }

    private void buscarOcorrenciasWS() {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(WebService.ENDERECO_WS.concat(getResources().getString(R.string.ocorrencia_listar)),
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

                        Type listType = new TypeToken<ArrayList<Ocorrencia>>() {
                        }.getType();
                        listaOcorrencias = gson.fromJson(str, listType);

                        percorrerOcorrencias();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          byte[] errorResponse, Throwable e) {
                        progressDialog.dismiss();

                        ToastUtil.criarToastLongo(getApplicationContext(), getResources().getString(R.string.msgErroWS));
                    }
                });
    }

    private void percorrerOcorrencias() {
        this.mapa.clear();

        try {
            configurarMapa();
            //adicionarMarcadores();
        } catch (Exception e) {
            if (!ValidadorUtil.isNuloOuVazio(progressDialog))
                progressDialog.dismiss();
        }
    }

    private void adicionarMarcadores() {
        int contadorOcorrencias = 0;

        try {
            if (!ValidadorUtil.isNuloOuVazio(listaOcorrencias) && listaOcorrencias.size() > 0) {
                for (Ocorrencia ocorrencia : this.listaOcorrencias) {
                    double result =  SphericalUtil.computeDistanceBetween(
                            new LatLng(this.localizacaoUsuario.getLatitude(), this.localizacaoUsuario.getLongitude()), new LatLng(ocorrencia.getEndereco()
                                    .getLatitude(), ocorrencia.getEndereco().getLongitude()));

                    if (!ocorrencia.isOcorrenciaSolucionada() && result < DISTANCIA_RAIO) {
                        contadorOcorrencias++;
                        LatLng c = new LatLng(ocorrencia.getEndereco().getLatitude(),
                                ocorrencia.getEndereco().getLongitude());

                        MarkerOptions markerOption = null;

                        markerOption = new MarkerOptions().position(c).icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

                        Marker marcadorAtual = this.mapa.addMarker(markerOption);

                        if (!ValidadorUtil.isNuloOuVazio(marcadorAtual)) {
                            this.marcadoresHashMap.put(marcadorAtual, ocorrencia);
                            this.mapa.setInfoWindowAdapter(new MarkerInfoWindowAdapter());
                        }
                    }
                }

                ToastUtil.criarToastLongo(AoRedor.this, contadorOcorrencias +" ocorrência(s) encontrada(s) em um raio de 10 km.");
            }

            progressDialog.dismiss();
        } catch (Exception ex) {
            ToastUtil.criarToastLongo(AoRedor.this, "Ocorreu um erro ao tentar recuperar sua posição. Por favor, tente novamente.");
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        this.navigationDrawer.setSelection(0);
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

    public class MarkerInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        public MarkerInfoWindowAdapter() {
        }

        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }

        @Override
        public View getInfoContents(Marker marker) {
            LayoutInflater inflater = (LayoutInflater) getApplicationContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View v = null;
            try {
                Ocorrencia ocorrencia = marcadoresHashMap.get(marker);
                if (ocorrencia.getId() != 0) {
                    v = inflater.inflate(R.layout.descricao_ocorrencia_mapa,
                            null);
                    TextView titulo = (TextView) v.findViewById(R.id.txtTitulo);
                    TextView descricao = (TextView) v.findViewById(R.id.txtDescricao);
                    TextView endereco = (TextView) v.findViewById(R.id.txtEndereco);
                    //TextView denuncias = (TextView) v.findViewById(R.id.txtDenuncias);

                    titulo.setText(ocorrencia.getTitulo());
                    descricao.setText(ocorrencia.getDescricao());
                    endereco.setText(ocorrencia.getEnderecoFormatado());
                    //denuncias.setText(getResources().getString(
                    //       R.string.qtdDenunciasOcorrencia) + " " + +ocorrencia.getDenuncias());
                }

            } catch (Exception e) {
            }

            return v;
        }
    }

    private void alertaGPSDesativado() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
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
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

}
