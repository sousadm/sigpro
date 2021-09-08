package com.sousa.sigpro.model.tipo;

public enum TipoEngajamento {

	APOIADOR("Apoiador"), NEUTRO("Neutro"), RESISTENTE("Resistente");

	private String descricao;

	private TipoEngajamento(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}
}