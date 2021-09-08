package com.sousa.sigpro.model.tipo;

public enum TipoPlanner {

	GERAL("geral"), 
	PROJETO_OBJETIVO("objetivo"),
	PROJETO_ENTREGA("division"), 
	PROJETO_PACOTE("employee"),
	PROJETO_RISCO("risco"),
	PROJETO_TAREFA("tarefa"),
	PROJETO_MARCO("marco");

	private String descricao;
	
	private TipoPlanner(String descricao) {
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