package felipe.com.br.aguaparatodos.singleton;

import java.util.ArrayList;
import java.util.List;

import felipe.com.br.aguaparatodos.dominio.Ocorrencia;

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
}
