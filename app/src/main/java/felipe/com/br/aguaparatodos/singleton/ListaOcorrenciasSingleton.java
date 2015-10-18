package felipe.com.br.aguaparatodos.singleton;

import java.util.ArrayList;
import java.util.List;

import felipe.com.br.aguaparatodos.dominio.Ocorrencia;
import felipe.com.br.aguaparatodos.utils.ValidadorUtil;

/**
 * Created by felipe on 10/4/15.
 */
public class ListaOcorrenciasSingleton {

    private static ListaOcorrenciasSingleton instancia = new ListaOcorrenciasSingleton();
    private List<Ocorrencia> lista = new ArrayList<Ocorrencia>();

    public static ListaOcorrenciasSingleton getInstancia() {
        return instancia;
    }

    public List<Ocorrencia> getLista() {
        return this.lista;
    }

    public void setLista(List<Ocorrencia> lista) {
        this.lista = lista;
    }

    public Ocorrencia buscarPorId(int id) {
        for (Ocorrencia o: this.lista) {
            if (o.getId() == id)
                return o;
        }

        return null;
    }

    public void setOcorrenciaConfirmada(int idOcorrencia) {
        if (!ValidadorUtil.isNuloOuVazio(this.lista)) {
            Ocorrencia o;
            o = buscarPorId(idOcorrencia);

            if (!ValidadorUtil.isNuloOuVazio(o)) {
                o.setOcorrenciaSolucionada(true);
            }
        }
    }
}
