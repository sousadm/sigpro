package com.sousa.sigpro.model;

public class ProducaoMontagemLista {

	private String status;
	private String codigo;
	private String unidade;
	private String descricao;
	private double quantidade;

	public ProducaoMontagemLista(String status, String codigo, String unidade, String descricao, double quantidade) {
		this.status = status;
		this.codigo = codigo;
		this.unidade = unidade;
		this.descricao = descricao;
		this.quantidade = quantidade;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public double getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(double quantidade) {
		this.quantidade = quantidade;
	}

	public String getUnidade() {
		return unidade;
	}

	public void setUnidade(String unidade) {
		this.unidade = unidade;
	}
}