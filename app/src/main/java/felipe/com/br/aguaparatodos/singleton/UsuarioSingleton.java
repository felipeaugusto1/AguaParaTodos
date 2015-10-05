package felipe.com.br.aguaparatodos.singleton;

import felipe.com.br.aguaparatodos.dominio.Usuario;

/**
 * Created by felipe on 8/30/15.
 */
public class UsuarioSingleton {

    private static UsuarioSingleton instancia = new UsuarioSingleton();
    private Usuario usuario;

    public static UsuarioSingleton getInstancia() {
        return instancia;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

}
