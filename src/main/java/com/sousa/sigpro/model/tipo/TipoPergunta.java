package com.sousa.sigpro.model.tipo;

public enum TipoPergunta {
	
	LOGICO("Sim/não"),
	DATA("data"),
	IMPORTANCIA("importância"),
	VALOR("valor"),
	TEXTO("Texto");
	
	private String descricao;

	TipoPergunta(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}
}