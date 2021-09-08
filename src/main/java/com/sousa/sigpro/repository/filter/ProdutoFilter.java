package com.sousa.sigpro.repository.filter;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.sousa.sigpro.model.Categoria;
import com.sousa.sigpro.model.tipo.TipoReposicao;
import com.sousa.sigpro.model.tipo.TipoStatusProducao;
import com.sousa.sigpro.util.SuporteData;

public class ProdutoFilter implements Serializable {

	private static final long serialVersionUID = 1L;

	private int opcao;
	private String sku;
	private String nome;
	private Categoria[] categorias;
	private Categoria[] subcategorias;
	private boolean comProduto;
	private boolean comServico;
	private boolean comPrecoInferior;
	private boolean semPrecoCompra;
	private boolean semPrecoVenda;
	private boolean comComposto;
	private Boolean ativo;
	private Date inicio;
	private Date termino;
	private TipoReposicao tipoReposicao;
	private TipoStatusProducao statusProducao;
	private String condicaoExtra;

	private Long primeiroRegistro;
	private Long quantidadeRegistros;
	private String propriedadeOrdenacao;
	private boolean ascendente;

	public ProdutoFilter() {
		categorias = new Categoria[0];
		subcategorias = new Categoria[0];
		condicaoExtra = "";
		comProduto = true;
		comServico = true;
		nome = "";
		sku = "";
	}

	public String getTituloRelatorio() {
		String valor = "";

		if (inicio != null && termino != null)
			valor += (valor.length() == 0 ? "" : " / ") + "Período: "
					+ SuporteData.formataDataToStr(inicio, "dd/MM/yyyy") + " a "
					+ SuporteData.formataDataToStr(termino, "dd/MM/yyyy");

		return valor;
	}

	
	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku == null ? null : sku.toUpperCase();
	}

	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Categoria[] getCategorias() {
		return categorias;
	}

	public void setCategorias(Categoria[] categorias) {
		this.categorias = categorias;
	}

	public boolean isComProduto() {
		return comProduto;
	}

	public void setComProduto(boolean comProduto) {
		this.comProduto = comProduto;
	}

	public boolean isComServico() {
		return comServico;
	}

	public void setComServico(boolean comServico) {
		this.comServico = comServico;
	}

	public TipoReposicao getTipoReposicao() {
		return tipoReposicao;
	}

	public void setTipoReposicao(TipoReposicao tipoReposicao) {
		this.tipoReposicao = tipoReposicao;
	}

	public String getCondicaoExtra() {
		return condicaoExtra;
	}

	public void setCondicaoExtra(String condicaoExtra) {
		this.condicaoExtra = condicaoExtra;
	}

	public Categoria[] getSubcategorias() {
		return subcategorias;
	}

	public void setSubcategorias(Categoria[] subcategorias) {
		this.subcategorias = subcategorias;
	}

	public boolean isComPrecoInferior() {
		return comPrecoInferior;
	}

	public void setComPrecoInferior(boolean comPrecoInferior) {
		this.comPrecoInferior = comPrecoInferior;
	}

	public boolean isSemPrecoCompra() {
		return semPrecoCompra;
	}

	public void setSemPrecoCompra(boolean semPrecoCompra) {
		this.semPrecoCompra = semPrecoCompra;
	}

	public boolean isSemPrecoVenda() {
		return semPrecoVenda;
	}

	public void setSemPrecoVenda(boolean semPrecoVenda) {
		this.semPrecoVenda = semPrecoVenda;
	}

	public int getOpcao() {
		return opcao;
	}

	public void setOpcao(int opcao) {
		this.opcao = opcao;
	}

	public Long getPrimeiroRegistro() {
		return primeiroRegistro;
	}

	public void setPrimeiroRegistro(Long primeiroRegistro) {
		this.primeiroRegistro = primeiroRegistro;
	}

	public Long getQuantidadeRegistros() {
		return quantidadeRegistros;
	}

	public void setQuantidadeRegistros(Long quantidadeRegistros) {
		this.quantidadeRegistros = quantidadeRegistros;
	}

	public String getPropriedadeOrdenacao() {
		return propriedadeOrdenacao;
	}

	public void setPropriedadeOrdenacao(String propriedadeOrdenacao) {
		this.propriedadeOrdenacao = propriedadeOrdenacao;
	}

	public boolean isAscendente() {
		return ascendente;
	}

	public void setAscendente(boolean ascendente) {
		this.ascendente = ascendente;
	}

	public Date getInicio() {
		return inicio;
	}

	public void setInicio(Date inicio) {
		this.inicio = inicio;
	}

	public Date getTermino() {
		return termino;
	}

	public void setTermino(Date termino) {
		this.termino = termino;
	}

	public boolean isComComposto() {
		return comComposto;
	}

	public void setComComposto(boolean comComposto) {
		this.comComposto = comComposto;
	}

	@Enumerated(EnumType.STRING)
	public TipoStatusProducao getStatusProducao() {
		return statusProducao;
	}

	public void setStatusProducao(TipoStatusProducao statusProducao) {
		this.statusProducao = statusProducao;
	}

}