package felipe.com.br.aguaparatodos.dominio;

import java.util.Date;

/**
 * Created by felipe on 8/29/15.
 */
public class Usuario {

    public static String PREFERENCIA_VISUALIZACAO_CIDADE = "cidade";
    public static String PREFERENCIA_VISUALIZACAO_PAIS = "pais";

    private int id;
    private String nomeCompleto;
    private String email;
    private String gcm;
    private String senha;
    private String senhaConfirmacao;
    private String senhaDeSeguranca;
    private String codigoVerificacao;
    private Date dataCadastro;
    private boolean usuarioNativo;
    private boolean usuarioFacebook;
    private boolean usuarioTwitter;
    private boolean usuarioGooglePlus;
    private boolean receberNotificacao;
    private Endereco endereco;
    private boolean primeiroLogin;
    private String preferenciaVisualizacao;
    private int qtdPontos;
    private boolean esqueceuSenha;

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

    public Endereco getEndereco() {
        return endereco;
    }

    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }

    public boolean isPrimeiroLogin() {
        return primeiroLogin;
    }

    public void setPrimeiroLogin(boolean primeiroLogin) {
        this.primeiroLogin = primeiroLogin;
    }

    public String getPreferenciaVisualizacao() {
        return preferenciaVisualizacao;
    }

    public void setPreferenciaVisualizacao(String preferenciaVisualizacao) {
        this.preferenciaVisualizacao = preferenciaVisualizacao;
    }

    public String getSenha() {
        return senha;
    }

    public String getSenhaConfirmacao() {
        return senhaConfirmacao;
    }

    public String getSenhaDeSeguranca() {
        return senhaDeSeguranca;
    }

    public boolean isUsuarioNativo() {
        return usuarioNativo;
    }

    public void setUsuarioNativo(boolean usuarioNativo) {
        this.usuarioNativo = usuarioNativo;
    }

    public String getCodigoVerificacao() {
        return codigoVerificacao;
    }

    public int getQtdPontos() {
        return qtdPontos;
    }

    public void setQtdPontos(int qtdPontos) {
        this.qtdPontos = qtdPontos;
    }

    public boolean isEsqueceuSenha() {
        return esqueceuSenha;
    }

    public void setEsqueceuSenha(boolean esqueceuSenha) {
        this.esqueceuSenha = esqueceuSenha;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", nomeCompleto='" + nomeCompleto + '\'' +
                ", email='" + email + '\'' +
                ", gcm='" + gcm + '\'' +
                ", senha='" + senha + '\'' +
                ", senhaConfirmacao='" + senhaConfirmacao + '\'' +
                ", senhaDeSeguranca='" + senhaDeSeguranca + '\'' +
                ", codigoVerificacao='" + codigoVerificacao + '\'' +
                ", dataCadastro=" + dataCadastro +
                ", usuarioNativo=" + usuarioNativo +
                ", usuarioFacebook=" + usuarioFacebook +
                ", usuarioTwitter=" + usuarioTwitter +
                ", usuarioGooglePlus=" + usuarioGooglePlus +
                ", receberNotificacao=" + receberNotificacao +
                ", endereco=" + endereco +
                ", primeiroLogin=" + primeiroLogin +
                ", preferenciaVisualizacao='" + preferenciaVisualizacao + '\'' +
                ", qtdPontos=" + qtdPontos +
                '}';
    }
}
