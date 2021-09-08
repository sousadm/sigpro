package com.sousa.sigpro.model.tipo;

public enum TipoEndereco {
	
	RESIDENCIAL("Residencial"), 
	COMERCIAL("Comercial"),
	ENTREGA("Entrega");
	
	private String descricao;

	private TipoEndereco(String valor) {
		this.descricao = valor;
	}

	public String getDescricao() {
		return descricao;
	}

}
