package felipe.com.br.aguaparatodos.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.facebook.login.LoginManager;
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
import com.mikepenz.materialdrawer.holder.BadgeStyle;
import com.mikepenz.materialdrawer.holder.StringHolder;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import felipe.com.br.aguaparatodos.R;
import felipe.com.br.aguaparatodos.dominio.Ocorrencia;
import felipe.com.br.aguaparatodos.dominio.Usuario;
import felipe.com.br.aguaparatodos.singleton.ListaOcorrenciasSingleton;
import felipe.com.br.aguaparatodos.utils.ConexoesWS;
import felipe.com.br.aguaparatodos.utils.PreferenciasUtil;
import felipe.com.br.aguaparatodos.utils.ToastUtil;
import felipe.com.br.aguaparatodos.singleton.UsuarioSingleton;
import felipe.com.br.aguaparatodos.utils.ValidadorUtil;
import felipe.com.br.aguaparatodos.utils.WebService;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

/**
 * Created by felipe on 8/23/15.
 */
public class MainActivity extends AppCompatActivity {

    private Drawer navigationDrawer;
    private Toolbar toolbar;
    private FloatingActionButton fab;

    private static final int ID_MENU_MAPA = 1;
    private static final int ID_MENU_REGISTRAR_OCORRENCIA = 2;
    private static final int ID_MENU_LISTA_OCORRENCIAS = 3;
    private static final int ID_MENU_AO_REDOR = 4;
    private static final int ID_MENU_AJUDA = 5;
    private static final int ID_MENU_SOBRE = 6;
    private static final int ID_MENU_SAIR = 9;





    private SupportMapFragment mapFragment;
    private GoogleMap mapa;

    private static ProgressDialog progressDialog;
    private List<Ocorrencia> listaOcorrencias;
    private List<Ocorrencia> listaOcorrenciasNaCidade = new ArrayList<>();
    private HashMap<Marker, Ocorrencia> marcadoresHashMap;
    private RequestParams parametros;

    private FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapa);

        String nomeUsuario = PreferenciasUtil.getPreferenciasUsuarioLogado(PreferenciasUtil.KEY_PREFERENCIAS_USUARIO_LOGADO_NOME, getApplicationContext());
        String emailUsuario = PreferenciasUtil.getPreferenciasUsuarioLogado(PreferenciasUtil.KEY_PREFERENCIAS_USUARIO_LOGADO_EMAIL, getApplicationContext());
        String fotoUsuario = PreferenciasUtil.getPreferenciasUsuarioLogado(PreferenciasUtil.KEY_PREFERENCIAS_USUARIO_LOGADO_FOTO, getApplicationContext());

        this.fab = (FloatingActionButton) findViewById(R.id.fab_nova_ocorrencia);
        this.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RegistrarOcorrencia.class));
            }
        });


        this.toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        this.toolbar.setTitle(getResources().getString(R.string.app_name));
        this.toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        setSupportActionBar(this.toolbar);

        this.frameLayout = (FrameLayout) findViewById(R.id.frame_layout_mapa);
        this.mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);


        this.configurarMapa();
        this.atualizarMapa();

        PrimaryDrawerItem item1 = new PrimaryDrawerItem().withName(getResources().getString(R.string.menu_mapa)).withIcon(GoogleMaterial.Icon.gmd_map).withIdentifier(ID_MENU_MAPA);
        PrimaryDrawerItem item2 = new PrimaryDrawerItem().withName(getResources().getString(R.string.menu_registrar_ocorrencia)).withIcon(GoogleMaterial.Icon.gmd_new_releases).withIdentifier(ID_MENU_REGISTRAR_OCORRENCIA);
        PrimaryDrawerItem item6 = new PrimaryDrawerItem().withName("Lista").withBadgeStyle(new BadgeStyle().withColor(Color.RED).withTextColor(Color.WHITE)).withIcon(GoogleMaterial.Icon.gmd_list).withIdentifier(ID_MENU_LISTA_OCORRENCIAS);
        PrimaryDrawerItem item5 = new PrimaryDrawerItem().withName("Ao redor").withIcon(GoogleMaterial.Icon.gmd_location_on).withIdentifier(ID_MENU_AO_REDOR);
        PrimaryDrawerItem item3 = new PrimaryDrawerItem().withName(getResources().getString(R.string.menu_sobre)).withIcon(GoogleMaterial.Icon.gmd_help).withIdentifier(ID_MENU_SOBRE);
        PrimaryDrawerItem item7 = new PrimaryDrawerItem().withName(getResources().getString(R.string.tela_informacoes)).withIcon(GoogleMaterial.Icon.gmd_info).withIdentifier(ID_MENU_AJUDA);
        SwitchDrawerItem item4 = new SwitchDrawerItem().withName(getResources().getString(R.string.menu_notificacao)).withCheckable(true).withOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(IDrawerItem iDrawerItem, CompoundButton compoundButton, boolean b) {
                progressDialog = ProgressDialog.show(MainActivity.this, getResources().getString(R.string.aguarde), getResources().getString(R.string.aguarde));
                UsuarioSingleton.getInstancia().getUsuario().setReceberNotificacao(b);

                prepararParametros();
                acessarWs();

                navigationDrawer.getAdapter().notifyDataSetChanged();
            }
        });

        item4.withChecked(UsuarioSingleton.getInstancia().getUsuario().isReceberNotificacao());

        PrimaryDrawerItem item8 = new PrimaryDrawerItem().withName(getResources().getString(R.string.menu_sair)).withIcon(GoogleMaterial.Icon.gmd_exit_to_app).withIdentifier(ID_MENU_SAIR);

        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.color.material_drawer_primary_light)
                .addProfiles(
                        new ProfileDrawerItem().withName(nomeUsuario).withEmail(emailUsuario).withIcon(Uri.parse(fotoUsuario))
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        return false;
                    }

                    ;
                })
                .build();

        //create the drawer and remember the `Drawer` result object
        this.navigationDrawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(this.toolbar)
                .withDisplayBelowStatusBar(true)
                .withSelectedItem(0)
                .withTranslucentStatusBar(true)
                .withActionBarDrawerToggle(true)
                .withActionBarDrawerToggleAnimated(true)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        item1,
                        item2,
                        item6,
                        item5,
                        item7,
                        item3,
                        item4,
                        new DividerDrawerItem(),
                        item8
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        //Fragment fragment;
                        //FragmentTransaction transaction = getFragmentManager().beginTransaction();

                        //ToastUtil.criarToastCurto(getApplicationContext(), "posicao: " +position);
                        if (position == ID_MENU_REGISTRAR_OCORRENCIA) {
                            navigationDrawer.setSelection(1);
                            startActivity(new Intent(MainActivity.this, RegistrarOcorrencia.class));
                        } else if (position == ID_MENU_MAPA) {
                            navigationDrawer.setSelection(0);
                        } else if (position == ID_MENU_SAIR) {
                            logOut();
                        } else if (position == ID_MENU_LISTA_OCORRENCIAS) {
                            //navigationDrawer.setSelection(3);
                            startActivity(new Intent(MainActivity.this, ListaOcorrencias.class));
                        } else if (position == ID_MENU_AJUDA) {
                            startActivity(new Intent(MainActivity.this, AjudaActivity.class));
                        }
                        else if (position == ID_MENU_AO_REDOR) {
                            startActivity(new Intent(MainActivity.this, AoRedor.class));
                        }

                        //transaction.addToBackStack(null);
                        //transaction.commit();
                        navigationDrawer.closeDrawer();
                        return true;
                    }

                    ;
                })
                .build();

        // ---- hamburger icon ----
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        this.navigationDrawer.getActionBarDrawerToggle().setDrawerIndicatorEnabled(true);
        // ----

        this.navigationDrawer.getAdapter().notifyDataSetChanged();

        this.mapa.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                final Ocorrencia ocorrencia = marcadoresHashMap.get(marker);

                Intent telaDetalheOcorrencia = new Intent(MainActivity.this,
                        DetalheOcorrencia.class);

                telaDetalheOcorrencia.putExtra("ocorrencia_id",
                        String.valueOf(ocorrencia.getId()));
                telaDetalheOcorrencia.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
                        | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                startActivity(telaDetalheOcorrencia);
            }
        });


//        new MaterialShowcaseView.Builder(this)
//                .setTarget(this.fab)
//                .setDismissText(getResources().getString(R.string.msgEntendi))
//                .setDelay(200) // optional but starting animations immediately in onCreate can make them choppy
//                .setContentText(getResources().getString(R.string.msgBtnNovaOcorrencia)).show();
//
//        new MaterialShowcaseView.Builder(this)
//                .setTarget(this.toolbar)
//                .setDismissText(getResources().getString(R.string.msgEntendi))
//                .setContentText("Acesse outras funcionalidades aqui do lado esquerdo.")
//                .setDelay(500) // optional but starting animations immediately in onCreate can make them choppy
//        //.singleUse(SHOWCASE_ID) // provide a unique ID used to ensure it is only shown once
//                .show();

        /* new MaterialShowcaseView.Builder(this)
                .setTarget(this.frameLayout)
                .setDismissText(getResources().getString(R.string.msgEntendi))
                .setDelay(200) // optional but starting animations immediately in onCreate can make them choppy
                .setContentText(getResources().getString(R.string.msgMapa)).show(); */

        ConexoesWS.prepararCamposAtualizarGcm(MainActivity.this, UsuarioSingleton.getInstancia().getUsuario().getId(), UsuarioSingleton.getInstancia().getUsuario().getGcm());

        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(500); // half second between each showcase view

        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this, "");

        sequence.addSequenceItem(this.fab, getResources().getString(R.string.msgBtnNovaOcorrencia), getResources().getString(R.string.msgEntendi));

        sequence.addSequenceItem(this.toolbar, getResources().getString(R.string.msgBottom), getResources().getString(R.string.msgEntendi));

        sequence.start();
    }

    private void configurarMapa() {
        if (ValidadorUtil.isNuloOuVazio(this.mapa)) {
            this.mapa = this.mapFragment.getMap();

            /* if (!ValidadorUtil.isNuloOuVazio(this.mapa)) {
                this.mapa.setMyLocationEnabled(true);
            } */
        }
    }

    private void atualizarMapa() {
        this.listaOcorrencias = new ArrayList<Ocorrencia>();
        this.marcadoresHashMap = new HashMap<Marker, Ocorrencia>();

        ListaOcorrenciasSingleton.getInstancia().setLista(this.listaOcorrencias);
        this.listaOcorrencias = ListaOcorrenciasSingleton.getInstancia().getLista();

        progressDialog = ProgressDialog.show(this, getResources()
                        .getString(R.string.aguarde),
                "Carregando ocorrencias...");
        progressDialog.setCanceledOnTouchOutside(true);

        this.parametros = new RequestParams();
        this.buscarOcorrenciasWS();
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

                        ListaOcorrenciasSingleton.getInstancia().setLista(listaOcorrencias);

                        filtrarOcorrenciasUsuario();
                        try {
                            percorrerOcorrencias(UsuarioSingleton.getInstancia().getUsuario().getPreferenciaVisualizacao().equalsIgnoreCase(Usuario.PREFERENCIA_VISUALIZACAO_CIDADE));
                        } catch (NullPointerException e) {
                            percorrerOcorrencias(true);
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          byte[] errorResponse, Throwable e) {
                        progressDialog.dismiss();

                        ToastUtil.criarToastLongo(getApplicationContext(), getResources().getString(R.string.msgErroWS));
                    }
                });
    }

    private void filtrarOcorrenciasUsuario() {
        String cidadeUsuario = UsuarioSingleton.getInstancia().getUsuario().getEndereco().getCidade();

        for (Ocorrencia o : this.listaOcorrencias) {
            if (o.getEndereco().getCidade().equalsIgnoreCase(cidadeUsuario))
                this.listaOcorrenciasNaCidade.add(o);
        }
    }

    private void percorrerOcorrencias(boolean apenasMinhaCidade) {
        this.mapa.clear();

        try {
            configurarMapa();
            if (apenasMinhaCidade) {
                navigationDrawer.updateBadge(ID_MENU_LISTA_OCORRENCIAS, new StringHolder(String.valueOf(listaOcorrenciasNaCidade.size())));
                adicionarMarcadores(this.listaOcorrenciasNaCidade);
            }
            else {
                navigationDrawer.updateBadge(ID_MENU_LISTA_OCORRENCIAS, new StringHolder(String.valueOf(listaOcorrencias.size())));
                adicionarMarcadores(this.listaOcorrencias);
            }

        } catch (Exception e) {
            progressDialog.dismiss();
        }
    }

    private void adicionarMarcadores(List<Ocorrencia> listaOcorrencias) {

        if (!ValidadorUtil.isNuloOuVazio(listaOcorrencias) && listaOcorrencias.size() > 0) {
            for (Ocorrencia ocorrencia : listaOcorrencias) {
                if (!ocorrencia.isOcorrenciaSolucionada()) {
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
        }

        if (UsuarioSingleton.getInstancia().getUsuario().getPreferenciaVisualizacao().equalsIgnoreCase(Usuario.PREFERENCIA_VISUALIZACAO_CIDADE)) {
            this.mapa.animateCamera(CameraUpdateFactory.zoomTo(14));
            this.mapa.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
                    UsuarioSingleton.getInstancia().getUsuario().getEndereco().getLatitude(), UsuarioSingleton.getInstancia().getUsuario().getEndereco().getLongitude()), 10));
        } else {
            this.mapa.animateCamera(CameraUpdateFactory.zoomTo(10));
            this.mapa.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
                    -14.2392976, -53.1805017), 4));
        }


        progressDialog.dismiss();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_ocorrencias, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (item.getItemId()) {
            case R.id.action_visualizar_ocorrencias:
                criarDialogAbrangenciaVisualizacao();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        Log.d("resumiu", "resumiu");
        this.navigationDrawer.setSelection(0);
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

    public void criarDialog(final Context contexto, String titulo, String mensagem) {

        AlertDialog dialog = new AlertDialog.Builder(contexto)
                .setTitle(titulo)
                .setMessage(mensagem)
                .setPositiveButton(contexto.getResources().getString(R.string.msgSim), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            prepararParametros();
                            acessarWs();
                        } catch (Exception e) {
                            ToastUtil.criarToastLongo(getApplicationContext(), getResources().getString(R.string.erroBuscarEndereco));
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

    private void prepararParametros() {
        try {
            this.parametros = new RequestParams();
            this.parametros.put("id", UsuarioSingleton.getInstancia().getUsuario().getId());
            this.parametros.put("valor", String.valueOf(UsuarioSingleton.getInstancia().getUsuario().isReceberNotificacao()));
        } catch (Exception e) {

        }
    }

    private void acessarWs() {
        AsyncHttpClient client = new AsyncHttpClient();

        client.get(WebService.ENDERECO_WS.concat(getResources().getString(R.string.usuario_receber_notificacao)), this.parametros, new AsyncHttpResponseHandler() {

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
                    ToastUtil.criarToastCurto(MainActivity.this, getResources().getString(R.string.statusAtualizadoSucesso));
                } else if (str.equalsIgnoreCase("erro")) {
                    progressDialog.dismiss();

                    ToastUtil.criarToastLongo(getApplicationContext(),
                            getResources().getString(R.string.msgErroWS));
                }

            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                progressDialog.dismiss();

                ToastUtil.criarToastLongo(getApplicationContext(), getResources().getString(R.string.msgErroWS));
            }

        });
    }

    private void logOut() {
        PreferenciasUtil.salvarPreferenciasLogin(PreferenciasUtil.KEY_PREFERENCIAS_USUARIO_LOGADO_EMAIL, PreferenciasUtil.VALOR_INVALIDO, getApplicationContext());
        PreferenciasUtil.salvarPreferenciasLogin(PreferenciasUtil.KEY_PREFERENCIAS_USUARIO_LOGADO_FOTO, PreferenciasUtil.VALOR_INVALIDO, getApplicationContext());
        PreferenciasUtil.salvarPreferenciasLogin(PreferenciasUtil.KEY_PREFERENCIAS_USUARIO_LOGADO_NOME, PreferenciasUtil.VALOR_INVALIDO, getApplicationContext());

        if (UsuarioSingleton.getInstancia().getUsuario().isUsuarioFacebook()) {
            LoginManager.getInstance().logOut();
        }
        /* if (UsuarioSingleton.getInstancia().getUsuario().isUsuarioGooglePlus()) {
            Log.d("logout 1", "logout 1");
            if (mGoogleApiClient.isConnected()) {
                Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
                Log.d("logout 2", "logout 2");
                mGoogleApiClient.disconnect();
            }
        } */

        LoginManager.getInstance().logOut();
        UsuarioSingleton.getInstancia().setUsuario(new Usuario());

        Intent intent = new Intent(MainActivity.this, Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void criarDialogAbrangenciaVisualizacao() {
        View view = getLayoutInflater().inflate(R.layout.visualizar_ocorrencias_dialog, null);

        final Dialog dialog = new Dialog(this);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(view);
        dialog.setTitle("Visualização");

        final RadioGroup radioGroup = (RadioGroup) view
                .findViewById(R.id.rgAbrangenciaVisualizacao);

        Button btnFiltroLocal = (Button) view
                .findViewById(R.id.btnSalvarVisualizacao);


        btnFiltroLocal.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (radioGroup.getCheckedRadioButtonId() == R.id.rbCidade) {
                    UsuarioSingleton.getInstancia().getUsuario().setPreferenciaVisualizacao(Usuario.PREFERENCIA_VISUALIZACAO_CIDADE);
                    prepararOcorrencias(true);
                } else {
                    UsuarioSingleton.getInstancia().getUsuario().setPreferenciaVisualizacao(Usuario.PREFERENCIA_VISUALIZACAO_PAIS);
                    prepararOcorrencias(false);
                }

                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void prepararOcorrencias(boolean apenasMinhaCidade) {
        progressDialog = ProgressDialog.show(this, getResources()
                        .getString(R.string.aguarde),
                "Carregando ocorrencias...");
        progressDialog.setCanceledOnTouchOutside(true);

        percorrerOcorrencias(apenasMinhaCidade);
    }

}
