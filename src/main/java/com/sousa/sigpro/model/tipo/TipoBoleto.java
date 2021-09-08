package com.sousa.sigpro.model.tipo;

public enum TipoBoleto {

	BOPEPO("Bopepo");

	private String descricao;

	private TipoBoleto(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}

}