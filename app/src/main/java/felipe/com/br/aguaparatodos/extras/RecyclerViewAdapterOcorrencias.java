package felipe.com.br.aguaparatodos.extras;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
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

    private static final int TYPE_HEADER = 2;
    private static final int TYPE_ITEM = 1;

    public RecyclerViewAdapterOcorrencias(List<Ocorrencia> contactList) {
        this.listaOcorrencias = contactList;
    }

    @Override
    public int getItemCount() {
        return listaOcorrencias.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;
        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    @Override
    public void onBindViewHolder(OcorrenciaViewHolder ocorrenciaViewHolder, int i) {
        if (!isPositionHeader(i)) {
            Ocorrencia ocorrencia = listaOcorrencias.get(i - 1); //-1 eh o cabecalho que foi adicionado
            ocorrenciaViewHolder.id = ocorrencia.getId();
            ocorrenciaViewHolder.titulo.setText(ocorrencia.getTitulo());
            ocorrenciaViewHolder.dataCadastro.setText("Data de cadastro: ".concat(new SimpleDateFormat("dd/MM/yyyy").format(ocorrencia.getDataCadastro())));
            ocorrenciaViewHolder.descricao.setText(ocorrencia.getDescricao());
            ocorrenciaViewHolder.endereco.setText(ocorrencia.getEndereco().getEndereco());
            if (ocorrencia.isOcorrenciaSolucionada())
                ocorrenciaViewHolder.status.setText("Status: Solucionada");
            else
                ocorrenciaViewHolder.status.setText("Status: Aguardando solução");
        }
    }

    @Override
    public OcorrenciaViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = null;
        if (i == TYPE_ITEM)
            itemView = LayoutInflater.
                    from(viewGroup.getContext()).
                    inflate(R.layout.card_view_ocorrencia, viewGroup, false);
        else if (i == TYPE_HEADER)
            itemView = LayoutInflater.
                    from(viewGroup.getContext()).
                    inflate(R.layout.recycler_header, viewGroup, false);

        return new OcorrenciaViewHolder(itemView);
    }

    public static class OcorrenciaViewHolder extends RecyclerView.ViewHolder {

        protected int id;
        protected TextView titulo, descricao, endereco, dataCadastro, status;
        protected View view;

        private Context context;

        public OcorrenciaViewHolder(View v) {
            super(v);
            view = v;

            titulo = (TextView) v.findViewById(R.id.txtTitulo);
            descricao = (TextView) v.findViewById(R.id.txtDescricao);
            endereco = (TextView) v.findViewById(R.id.txtEndereco);
            dataCadastro = (TextView) v.findViewById(R.id.txtDataCadastro);
            status = (TextView) v.findViewById(R.id.txtStatus);

            view.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    if (id != 0) {
                        context = v.getContext();

                        Intent telaDetalheOcorrencia = new Intent(context,
                                DetalheOcorrencia.class);

                        telaDetalheOcorrencia.putExtra("ocorrencia_id", String.valueOf(id));
                        //telaDetalheOcorrencia.putExtra("classe", String.valueOf(RecyclerViewAdapterOcorrencias.class));
                        //telaDetalheOcorrencia.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                        context.startActivity(telaDetalheOcorrencia);
                    }
                }
            });

        }
    }
}
