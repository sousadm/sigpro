package com.sousa.sigpro.model.clinica;

public enum TipoAtendimentoPadrao {

	NORMAL("normal"), 
	PRIORIDADE("prioridade"), 
	URGENTE("urgente");
	
	private String descricao;
	
	private TipoAtendimentoPadrao(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}

	@Override
	public String toString() {
		return descricao;
	}
}