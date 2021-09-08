package com.sousa.sigpro.model.tipo;

public enum TipoAcesso {

	NEGADO("sem acesso"), 
	LEITURA("leitura"), 
	GRAVACAO("gravação");
	
	private String descricao;
	
	private TipoAcesso(String descricao) {
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