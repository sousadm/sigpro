package com.sousa.sigpro.model.tipo;

public enum StatusFuncionario {

	ATIVO("Ativo"), 
	FERIAS("Em férias"),
	INATIVO("Inativo");
	
	private String descricao;

	StatusFuncionario(String descricao) {
		this.descricao = descricao;
	}
	
	public String getDescricao() {
		return descricao;
	}
	
}