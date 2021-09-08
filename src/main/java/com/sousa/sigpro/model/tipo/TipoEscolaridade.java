package com.sousa.sigpro.model.tipo;

public enum TipoEscolaridade {
	
	ANALFABETO("Alfabetizado"),
	FUNDAM_INC("Ensino fundamental incompleto"),
	FUNDAMENTAL("Ensino fundamental completo"),
	MEDIO_INC("Ensino médio incompleto"),
	MEDIO("Ensino médio completo"),
	SUPERIOR_INC("Superior incompleto"),
	GRADUADO("Superior completo"),
	ESPECIALIZACAO("Especialização"),
	MESTRADO("Mestrado"),
	DOUTORADO("Doutorado"),
	PHD("Pós doutorado");
	
	private String descricao;

	TipoEscolaridade(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}
}
