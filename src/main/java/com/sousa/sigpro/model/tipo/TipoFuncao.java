package com.sousa.sigpro.model.tipo;

public enum TipoFuncao {
	
	ADM("Administração"), 
	COM("Comercial"),
	PRO("Produção"),
	OPE("Operacional");
	
	private String descricao;

	private TipoFuncao(String valor) {
		this.descricao = valor;
	}

	public String getDescricao() {
		return descricao;
	}

}
