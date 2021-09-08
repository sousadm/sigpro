package com.sousa.sigpro.model.tipo;

public enum TipoVeiculo {

	AUTO("automovel"), 
	MOTO("motocicleta"),
	PICK("Pick-up"),
	TRUCK("caminhão");
	
	private String descricao;
	
	private TipoVeiculo(String valor) {
		this.descricao = valor;
	}

	public String getDescricao() {
		return descricao;
	}
	
}
