package felipe.com.br.aguaparatodos.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;

import felipe.com.br.aguaparatodos.R;
import felipe.com.br.aguaparatodos.singleton.UsuarioSingleton;

public class MinhaPontuacao extends AppCompatActivity {

    private Drawer navigationDrawer;

    private Toolbar toolbar;

    private TextView txtQtdPontos, txtMsgPontos, txtFraseAjudar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minha_pontuacao);

        criarReferenciasComponentes();

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
                Intent intent = new Intent(MinhaPontuacao.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

    }

    private void criarReferenciasComponentes() {
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.txtQtdPontos = (TextView) findViewById(R.id.qtdPontos);
        this.txtMsgPontos = (TextView) findViewById(R.id.qtdFrases);
        this.txtFraseAjudar = (TextView) findViewById(R.id.fraseContinuarAjudando);

        int qtd = UsuarioSingleton.getInstancia().getUsuario().getQtdPontos();

        this.txtQtdPontos.setText(String.valueOf(qtd));

        if (qtd == 1) {
            this.txtMsgPontos.setText("ponto obtido até o momento!");
            this.txtFraseAjudar.setText("Continue ajudando a sociedade a combater este problema, e acumule pontos!");
        } else if (qtd > 1) {
            this.txtMsgPontos.setText("pontos obtidos até o momento!");
            this.txtFraseAjudar.setText("Continue ajudando a sociedade a combater este problema, e acumule pontos!");
        } else if (qtd == 0) {
            this.txtMsgPontos.setText("pontos obtidos até o momento! :(");
            this.txtFraseAjudar.setText("Ajude a sociedade a combater este problema, e acumule pontos!");
        }

    }

    public void acaoBotoes(View v) {
        switch (v.getId()) {
            case R.id.btnCompartilharAmigos:
                compartilhar("COLOCAR_MENSAGEM_AQUI");
        }
    }

    private void compartilhar(String texto) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        if (texto.equalsIgnoreCase(""))
            sendIntent.putExtra(Intent.EXTRA_TEXT, texto);
        else
            sendIntent.putExtra(Intent.EXTRA_TEXT, texto);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

}
