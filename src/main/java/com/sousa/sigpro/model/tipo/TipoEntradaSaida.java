package com.sousa.sigpro.model.tipo;

public enum TipoEntradaSaida {

	ENTRADA("Entrada"), 
	SAIDA("Saída");
	
	private String descricao;
	
	private TipoEntradaSaida(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}
}