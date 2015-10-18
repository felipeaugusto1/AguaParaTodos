package felipe.com.br.aguaparatodos.dominio;

import java.util.Date;
import java.util.List;

/**
 * Created by felipe on 8/29/15.
 */
public class Ocorrencia {

    private int id;
    private String titulo;
    private String descricao;
    private String pontoReferencia;
    private int qtdConfirmacoes;
    private List<Integer> usuariosConfirmaram;
    private boolean ocorrenciaSolucionada;
    private Usuario usuario;
    private Endereco endereco;
    private Date dataCadastro;
    private Date dataSolucao;

    public Ocorrencia() {
    }

    public Ocorrencia(String titulo, String descricao, String pontoReferencia, int qtdConfirmacoes, boolean ocorrenciaSolucionada, Usuario usuario, Endereco endereco, Date dataCadastro) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.pontoReferencia = pontoReferencia;
        this.qtdConfirmacoes = qtdConfirmacoes;
        this.ocorrenciaSolucionada = ocorrenciaSolucionada;
        this.usuario = usuario;
        this.endereco = endereco;
        this.dataCadastro = dataCadastro;
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

    public int getQtdConfirmacoes() {
        return qtdConfirmacoes;
    }

    public void setQtdConfirmacoes(int qtdConfirmacoes) {
        this.qtdConfirmacoes = qtdConfirmacoes;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
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

    public Date getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(Date dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public List<Integer> getUsuariosConfirmaram() {
        return usuariosConfirmaram;
    }

    public void setUsuariosConfirmaram(List<Integer> usuariosConfirmaram) {
        this.usuariosConfirmaram = usuariosConfirmaram;
    }

    public Date getDataSolucao() {
        return dataSolucao;
    }

    public void setDataSolucao(Date dataSolucao) {
        this.dataSolucao = dataSolucao;
    }

    @Override
    public String toString() {
        return "Ocorrencia{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", descricao='" + descricao + '\'' +
                ", pontoReferencia='" + pontoReferencia + '\'' +
                ", qtdConfirmacoes=" + qtdConfirmacoes +
                ", usuariosConfirmaram=" + usuariosConfirmaram +
                ", ocorrenciaSolucionada=" + ocorrenciaSolucionada +
                ", usuario=" + usuario +
                ", endereco=" + endereco +
                ", dataCadastro=" + dataCadastro +
                '}';
    }
}
