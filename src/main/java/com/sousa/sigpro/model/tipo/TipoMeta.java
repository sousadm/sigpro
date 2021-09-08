package com.sousa.sigpro.model.tipo;

public enum TipoMeta {

	FATURAMENTO("Faturamento"), 
	COMPRA("Compra"), 
	COBRANCA("Cobrança");

	public String descricao;

	private TipoMeta(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}
}