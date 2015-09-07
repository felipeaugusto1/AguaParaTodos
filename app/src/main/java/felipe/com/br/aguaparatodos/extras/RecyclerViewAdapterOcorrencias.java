package felipe.com.br.aguaparatodos.extras;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import felipe.com.br.aguaparatodos.R;
import felipe.com.br.aguaparatodos.activities.DetalheOcorrencia;
import felipe.com.br.aguaparatodos.dominio.Ocorrencia;
import felipe.com.br.aguaparatodos.utils.ToastUtil;

/**
 * Created by felipe on 9/6/15.
 */
public class RecyclerViewAdapterOcorrencias extends RecyclerView.Adapter<RecyclerViewAdapterOcorrencias.OcorrenciaViewHolder> {

    private List<Ocorrencia> listaOcorrencias;

    public RecyclerViewAdapterOcorrencias(List<Ocorrencia> contactList) {
        this.listaOcorrencias = contactList;
    }

    @Override
    public int getItemCount() {
        return listaOcorrencias.size();
    }

    @Override
    public void onBindViewHolder(OcorrenciaViewHolder contactViewHolder, int i) {
        Ocorrencia ocorrencia = listaOcorrencias.get(i);
        contactViewHolder.id = ocorrencia.getId();
        contactViewHolder.titulo.setText(ocorrencia.getTitulo());
        contactViewHolder.descricao.setText(ocorrencia.getDescricao());
        contactViewHolder.endereco.setText(ocorrencia.getEndereco().getEndereco());
    }

    @Override
    public OcorrenciaViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.card_view_ocorrencia, viewGroup, false);

        return new OcorrenciaViewHolder(itemView);
    }

    public static class OcorrenciaViewHolder extends RecyclerView.ViewHolder {

        protected int id;
        protected TextView titulo, descricao, endereco;
        protected View view;

        private Context context;

        public OcorrenciaViewHolder(View v) {
            super(v);
            view = v;

            titulo = (TextView) v.findViewById(R.id.txtTitulo);
            descricao = (TextView) v.findViewById(R.id.txtDescricao);
            endereco = (TextView) v.findViewById(R.id.txtEndereco);

            view.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    context = v.getContext();

                    Intent telaDetalheOcorrencia = new Intent(context,
                            DetalheOcorrencia.class);

                    telaDetalheOcorrencia.putExtra("ocorrencia_id", String.valueOf(id));
                    //telaDetalheOcorrencia.putExtra("classe", String.valueOf(RecyclerViewAdapterOcorrencias.class));
                    //telaDetalheOcorrencia.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    context.startActivity(telaDetalheOcorrencia);
                }
            });

        }
    }
}
