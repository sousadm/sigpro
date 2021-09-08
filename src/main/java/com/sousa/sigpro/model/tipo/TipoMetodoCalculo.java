package com.sousa.sigpro.model.tipo;

public enum TipoMetodoCalculo {

	NONE("não calcular"),
	ITEM("valor fixo sobre o item"),
	PERC("percentual sobre valor");
	
	private String descricao;
	
	private TipoMetodoCalculo(String valor) {
		this.descricao = valor;
	}

	public String getDescricao() {
		return descricao;
	}
	
}
