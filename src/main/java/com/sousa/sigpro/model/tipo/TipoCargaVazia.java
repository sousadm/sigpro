package com.sousa.sigpro.model.tipo;

public enum TipoCargaVazia {

	CN("Container", 1),
	CR("Carreta", 2);
	
	private String descricao;
	private int codigo;

	private TipoCargaVazia(String valor, int codigo) {
		this.descricao = valor;
		this.codigo = codigo;
	}

	public String getDescricao() {
		return descricao;
	}

	public int getCodigo() {
		return codigo;
	}
}