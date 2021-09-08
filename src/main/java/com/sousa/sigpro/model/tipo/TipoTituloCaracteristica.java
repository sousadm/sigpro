package com.sousa.sigpro.model.tipo;

public enum TipoTituloCaracteristica {
	
	SIMPLES("1", "Cobrança Simples"),
	VINCULADO("2", "Cobrança Vinculada"),
	CAUCIONADO("3", "Cobrança Caucionada"),
	DESCONTADO("4", "Cobrança Descontada"),
	VENDOR("5", "Cobrança Vendor"); 
	
	private String codigo;
	private String descricao;

	private TipoTituloCaracteristica(String codigo, String descricao) {
		this.codigo = codigo;
		this.descricao = descricao;
	}

	public String getCodigo() {
		return codigo;
	}

	public String getDescricao() {
		return descricao;
	}

}