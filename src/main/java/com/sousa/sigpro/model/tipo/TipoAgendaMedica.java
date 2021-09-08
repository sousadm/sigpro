package com.sousa.sigpro.model.tipo;

public enum TipoAgendaMedica {

	CNS("consulta"),
	RET("retorno");
	
	private String descricao;

	private TipoAgendaMedica(String valor) {
		this.descricao = valor;
	}

	public String getDescricao() {
		return descricao;
	}
}
