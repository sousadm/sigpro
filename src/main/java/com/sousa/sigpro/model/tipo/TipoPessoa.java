package com.sousa.sigpro.model.tipo;

public enum TipoPessoa {
	
	NA("indefinido"),
	PF("física"), 
	PJ("jurídica");
	
	private String descricao;

	TipoPessoa(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}
}
