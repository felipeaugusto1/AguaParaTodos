package felipe.com.br.aguaparatodos.dominio;

/**
 * Created by felipe on 8/29/15.
 */
public class Ocorrencia {

    private int id;
    private String titulo;
    private String descricao;
    private Localidade localidade;
    private int qtdDenuncias;
    private Usuario usuario;
    private String numeroEndereco;
    private String endereco;
    private String cidade;
    private String estado;

    public Ocorrencia() {
    }

    public Ocorrencia(String titulo, String descricao, Localidade localidade, Usuario usuario, String numeroEndereco, String endereco, String cidade, String estado) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.localidade = localidade;
        this.usuario = usuario;
        this.numeroEndereco = numeroEndereco;
        this.endereco = endereco;
        this.cidade = cidade;
        this.estado = estado;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Localidade getLocalidade() {
        return localidade;
    }

    public void setLocalidade(Localidade localidade) {
        this.localidade = localidade;
    }

    public int getQtdDenuncias() {
        return qtdDenuncias;
    }

    public void setQtdDenuncias(int qtdDenuncias) {
        this.qtdDenuncias = qtdDenuncias;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getNumeroEndereco() {
        return numeroEndereco;
    }

    public void setNumeroEndereco(String numeroEndereco) {
        this.numeroEndereco = numeroEndereco;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
