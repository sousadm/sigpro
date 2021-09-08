package com.sousa.sigpro.model.tipo;

public enum TipoPorteEmpresa {
	
	MEI("Micro Empreendedor Individual"), 
	ME("Micro Empresa"),
	EPP("Empresa de Pequeno Porte"),
	MDE("Média Empresa"),
	GE("Grande Empresa");
	
	private String descricao;

	private TipoPorteEmpresa(String valor) {
		this.descricao = valor;
	}

	public String getDescricao() {
		return descricao;
	}

}
