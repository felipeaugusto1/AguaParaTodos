package felipe.com.br.aguaparatodos.dominio;

/**
 * Created by felipe on 8/29/15.
 */
public class Ocorrencia {

    private int id;
    private String titulo;
    private String descricao;
    private String pontoReferencia;
    private int qtdDenuncias;
    private boolean ocorrenciaSolucionada;
    private Usuario usuario;
    private String numeroEndereco;
    private Endereco endereco;

    public Ocorrencia() {
    }

    public Ocorrencia(String titulo, String descricao, String pontoReferencia, int qtdDenuncias, boolean ocorrenciaSolucionada, Usuario usuario, String numeroEndereco, Endereco endereco) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.pontoReferencia = pontoReferencia;
        this.qtdDenuncias = qtdDenuncias;
        this.ocorrenciaSolucionada = ocorrenciaSolucionada;
        this.usuario = usuario;
        this.numeroEndereco = numeroEndereco;
        this.endereco = endereco;
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

    public Endereco getEndereco() {
        return endereco;
    }

    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }

    public boolean isOcorrenciaSolucionada() {
        return ocorrenciaSolucionada;
    }

    public void setOcorrenciaSolucionada(boolean ocorrenciaSolucionada) {
        this.ocorrenciaSolucionada = ocorrenciaSolucionada;
    }

    public String getPontoReferencia() {
        return pontoReferencia;
    }

    public void setPontoReferencia(String pontoReferencia) {
        this.pontoReferencia = pontoReferencia;
    }

    public String getEnderecoFormatado() {
        return this.endereco.getEndereco() + ", " + this.endereco.getCidade();
    }
}
