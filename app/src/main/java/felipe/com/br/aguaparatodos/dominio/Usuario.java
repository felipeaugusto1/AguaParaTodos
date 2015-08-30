package felipe.com.br.aguaparatodos.dominio;

import java.util.Date;

/**
 * Created by felipe on 8/29/15.
 */
public class Usuario {

    private int id;
    private String nomeCompleto;
    private String email;
    private String gcm;
    private Date dataCadastro;
    private boolean usuarioFacebook;
    private boolean usuarioTwitter;
    private boolean usuarioGooglePlus;
    private boolean receberNotificacao;

    public Usuario() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public void setNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGcm() {
        return gcm;
    }

    public void setGcm(String gcm) {
        this.gcm = gcm;
    }

    public Date getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(Date dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public boolean isUsuarioFacebook() {
        return usuarioFacebook;
    }

    public void setUsuarioFacebook(boolean usuarioFacebook) {
        this.usuarioFacebook = usuarioFacebook;
    }

    public boolean isUsuarioTwitter() {
        return usuarioTwitter;
    }

    public void setUsuarioTwitter(boolean usuarioTwitter) {
        this.usuarioTwitter = usuarioTwitter;
    }

    public boolean isUsuarioGooglePlus() {
        return usuarioGooglePlus;
    }

    public void setUsuarioGooglePlus(boolean usuarioGooglePlus) {
        this.usuarioGooglePlus = usuarioGooglePlus;
    }

    public boolean isReceberNotificacao() {
        return receberNotificacao;
    }

    public void setReceberNotificacao(boolean receberNotificacao) {
        this.receberNotificacao = receberNotificacao;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", nomeCompleto='" + nomeCompleto + '\'' +
                ", email='" + email + '\'' +
                ", gcm='" + gcm + '\'' +
                ", dataCadastro=" + dataCadastro +
                ", usuarioFacebook=" + usuarioFacebook +
                ", usuarioTwitter=" + usuarioTwitter +
                ", usuarioGooglePlus=" + usuarioGooglePlus +
                ", receberNotificacao=" + receberNotificacao +
                '}';
    }
}
