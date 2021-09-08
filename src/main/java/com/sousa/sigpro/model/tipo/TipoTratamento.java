package com.sousa.sigpro.model.tipo;

public enum TipoTratamento {
	
	SRA("Sr(a)"),
	DRA("Dr(a)"),
	DOM("dom");
	
	private String descricao;

	private TipoTratamento(String valor) {
		this.descricao = valor;
	}

	public String getDescricao() {
		return descricao;
	}

}
