package com.sousa.sigpro.model.tipo;

public enum TipoSexo {
	
	MAS("masculino"),
	FEM("feminino");
	
	private String descricao;

	private TipoSexo(String valor) {
		this.descricao = valor;
	}

	public String getDescricao() {
		return descricao;
	}

}
