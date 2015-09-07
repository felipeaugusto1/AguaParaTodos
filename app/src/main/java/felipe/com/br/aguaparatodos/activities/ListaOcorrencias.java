package felipe.com.br.aguaparatodos.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

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
import felipe.com.br.aguaparatodos.extras.RecyclerViewAdapterOcorrencias;
import felipe.com.br.aguaparatodos.utils.ToastUtil;
import felipe.com.br.aguaparatodos.utils.WebService;

/**
 * Created by felipe on 9/4/15.
 */
public class ListaOcorrencias extends AppCompatActivity {

    private Drawer navigationDrawer;
    private Toolbar toolbar;

    private RequestParams parametros;
    private List<Ocorrencia> listaOcorrencias;

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;

    private static ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lista_ocorrencias);

        this.listaOcorrencias = new ArrayList<Ocorrencia>();

        criarReferenciasComponentes();

        this.toolbar.setTitle(getResources().getString(R.string.tituloTelaDetalheOcorrenia));
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
                "carregando ocorrencias...");

        this.recyclerView.setHasFixedSize(true);
        this.linearLayoutManager = new LinearLayoutManager(this);
        this.linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        this.recyclerView.setLayoutManager(this.linearLayoutManager);

        buscarOcorrenciasWS();
        progressDialog.dismiss();
    }

    private void criarReferenciasComponentes() {

        this.toolbar = (Toolbar) findViewById(R.id.toolbar_lista_ocorrencias);
        this.recyclerView = (RecyclerView) findViewById(R.id.cardList);


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

                        RecyclerViewAdapterOcorrencias adapter = new RecyclerViewAdapterOcorrencias(listaOcorrencias);
                        recyclerView.setAdapter(adapter);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          byte[] errorResponse, Throwable e) {
                        progressDialog.dismiss();

                        ToastUtil.criarToastLongo(getApplicationContext(), getResources().getString(R.string.msgErroWS));
                    }
                });
    }
}