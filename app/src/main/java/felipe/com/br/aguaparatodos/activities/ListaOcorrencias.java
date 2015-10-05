package felipe.com.br.aguaparatodos.activities;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.SearchRecentSuggestions;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

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
import java.util.ArrayList;
import java.util.List;

import felipe.com.br.aguaparatodos.R;
import felipe.com.br.aguaparatodos.dominio.Ocorrencia;
import felipe.com.br.aguaparatodos.dominio.Usuario;
import felipe.com.br.aguaparatodos.extras.RecyclerViewAdapterOcorrencias;
import felipe.com.br.aguaparatodos.provider.SearchableProvider;
import felipe.com.br.aguaparatodos.singleton.ListaOcorrenciasSingleton;
import felipe.com.br.aguaparatodos.utils.StringUtil;
import felipe.com.br.aguaparatodos.utils.ToastUtil;
import felipe.com.br.aguaparatodos.singleton.UsuarioSingleton;
import felipe.com.br.aguaparatodos.utils.WebService;

/**
 * Created by felipe on 9/4/15.
 */
public class ListaOcorrencias extends AppCompatActivity {

    private Drawer navigationDrawer;
    private Toolbar toolbar;

    private RequestParams parametros;
    private List<Ocorrencia> listaOcorrencias;
    private List<Ocorrencia> listaOcorrenciasAux;
    private List<Ocorrencia> listaOcorrenciasCidadeUsuario;

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerViewAdapterOcorrencias adapter;

    private static ProgressDialog progressDialog;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lista_ocorrencias);

        this.listaOcorrencias = new ArrayList<Ocorrencia>();
        this.listaOcorrenciasAux = new ArrayList<Ocorrencia>();
        this.listaOcorrenciasCidadeUsuario = new ArrayList<Ocorrencia>();

        criarReferenciasComponentes();

        this.toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        this.toolbar.setTitle(getResources().getString(R.string.tituloListaOcorrencias));
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
                Intent intent = new Intent(ListaOcorrencias.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });


        progressDialog = ProgressDialog.show(this, getResources()
                        .getString(R.string.aguarde),
                "Carregando ocorrencias...");

        this.recyclerView.setHasFixedSize(true);
        this.linearLayoutManager = new LinearLayoutManager(this);
        this.linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        this.recyclerView.setLayoutManager(this.linearLayoutManager);

        this.adapter = new RecyclerViewAdapterOcorrencias(this.listaOcorrenciasAux);
        this.recyclerView.setAdapter(adapter);

        //buscarOcorrenciasWS();
        this.listaOcorrencias = ListaOcorrenciasSingleton.getInstancia().getLista();
        this.listaOcorrenciasAux = ListaOcorrenciasSingleton.getInstancia().getLista();
        configurarLista();
        progressDialog.dismiss();

        handleSearch(getIntent());
    }

    private void handleSearch(Intent intent) {
        if (Intent.ACTION_SEARCH.equalsIgnoreCase(intent.getAction())) {
            String q = intent.getStringExtra(SearchManager.QUERY);

            filtrarOcorrencias(q);
            this.toolbar.setTitle(q);
            this.toolbar.setSubtitle(this.listaOcorrenciasAux.size() + " ocorrência(s).");

            SearchRecentSuggestions searchRecentSuggestions = new SearchRecentSuggestions(this, SearchableProvider.AUTHORITY, SearchableProvider.MODE);
            searchRecentSuggestions.saveRecentQuery(q, null);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleSearch(intent);
    }

    private void filtrarOcorrencias(String q) {
        this.listaOcorrenciasAux = new ArrayList<Ocorrencia>();

        for (Ocorrencia ocorrencia : this.listaOcorrencias) {
            if (StringUtil.retirarAcentosDaPalavra(ocorrencia.getEndereco().getCidade().toLowerCase()).startsWith(StringUtil.retirarAcentosDaPalavra(q.toLowerCase()))) {
                this.listaOcorrenciasAux.add(ocorrencia);
            }
        }

        if (this.listaOcorrenciasAux.isEmpty()) {
            this.adapter = new RecyclerViewAdapterOcorrencias(this.listaOcorrenciasAux);
            this.recyclerView.setAdapter(this.adapter);

            ToastUtil.criarToastLongo(this, "Nenhuma ocorrência encontrada.");
        } else {
            this.adapter = new RecyclerViewAdapterOcorrencias(this.listaOcorrenciasAux);
            this.recyclerView.setAdapter(this.adapter);

            ToastUtil.criarToastCurto(getApplicationContext(), this.listaOcorrenciasAux.size() + " ocorrência(s) encontrada(s).");
        }

        this.adapter.notifyDataSetChanged();
    }

    private void criarReferenciasComponentes() {

        this.toolbar = (Toolbar) findViewById(R.id.toolbar_lista_ocorrencias);
        this.recyclerView = (RecyclerView) findViewById(R.id.cardList);

        /* this.mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_orange_dark, android.R.color.holo_green_dark, android.R.color.holo_blue_bright);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshContent();
            }
        }); */
    }

    private void refreshContent() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //mAdapter.refreshContent();
                //mAdapter.notifyDataSetChanged();
                mSwipeRefreshLayout.setRefreshing(false);

            }
        }, 5000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView;
        MenuItem item = menu.findItem(R.id.menu_search);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            searchView = (SearchView) item.getActionView();
        } else {
            searchView = (SearchView) MenuItemCompat.getActionView(item);
        }

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint("Pesquise por cidade");

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        /* if (id == R.id.action_settings) {
            return true;
        } */

        if (item.getItemId() == R.id.menu_delete) {
            SearchRecentSuggestions searchRecentSuggestions = new SearchRecentSuggestions(this, SearchableProvider.AUTHORITY, SearchableProvider.MODE);
            searchRecentSuggestions.clearHistory();
            Toast.makeText(this, "Historico excluído com sucesso.", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
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
                        listaOcorrenciasAux = gson.fromJson(str, listType);

                        configurarLista();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          byte[] errorResponse, Throwable e) {
                        progressDialog.dismiss();

                        ToastUtil.criarToastLongo(getApplicationContext(), getResources().getString(R.string.msgErroWS));
                    }
                });
    }

    private void configurarLista() {
        filtrarOcorrenciasUsuario();

        adapter = new RecyclerViewAdapterOcorrencias(listaOcorrenciasAux);
        recyclerView.setAdapter(adapter);

        //RecyclerViewAdapterOcorrencias adapter = new RecyclerViewAdapterOcorrencias(listaOcorrenciasAux);
        //recyclerView.setAdapter(adapter);
    }

    private void filtrarOcorrenciasUsuario() {
        if (UsuarioSingleton.getInstancia().getUsuario().getPreferenciaVisualizacao().equalsIgnoreCase(Usuario.PREFERENCIA_VISUALIZACAO_CIDADE)) {
            this.listaOcorrenciasCidadeUsuario = new ArrayList<Ocorrencia>();
            String cidadeUsuario = UsuarioSingleton.getInstancia().getUsuario().getEndereco().getCidade();

            for (Ocorrencia o : this.listaOcorrencias) {
                if (o.getEndereco().getCidade().equalsIgnoreCase(cidadeUsuario))
                    this.listaOcorrenciasCidadeUsuario.add(o);
            }

            if (this.listaOcorrenciasCidadeUsuario.size() > 0) {
                this.listaOcorrenciasAux = this.listaOcorrenciasCidadeUsuario;

                this.adapter = new RecyclerViewAdapterOcorrencias(this.listaOcorrenciasAux);
                this.recyclerView.setAdapter(this.adapter);
            }
        }

    }
}