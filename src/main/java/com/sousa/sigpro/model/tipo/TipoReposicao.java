package com.sousa.sigpro.model.tipo;

public enum TipoReposicao {

	NA("indefinido"),
	KM("Quilômetro"),
	DIA("dia");

	private String descricao;

	private TipoReposicao(String valor) {
		this.descricao = valor;
	}

	public String getDescricao() {
		return descricao;
	}

}
