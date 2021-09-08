package com.sousa.sigpro.model.tipo;

public enum TipoApiCobranca {

	ASAAS("Asaas", "https://www.asaas.com/");
	
	private String descricao;
	private String homepage;
	
	private TipoApiCobranca(String descricao, String homepage) {
		this.descricao = descricao;
		this.homepage = homepage;
	}

	public String getDescricao() {
		return descricao;
	}
	
	public String getHomepage() {
		return homepage;
	}

	@Override
	public String toString() {
		return descricao;
	}
}