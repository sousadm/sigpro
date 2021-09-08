package com.sousa.sigpro.model.tipo;

public enum TipoCategoriaHabilitacao {

	NA("Não habilitado"),
	A("A"),
	B("B"),
	C("C"),
	D("D"),
	E("E"),
	AB("AB"),
	AC("AC"),
	AD("AD");
	
	private String descricao;
	
	private TipoCategoriaHabilitacao(String descricao) {
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