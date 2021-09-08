package com.sousa.sigpro.model.tipo;

public enum TipoEstrategia {

	EVITAR("evitar"),
	MITIGAR("mitigar"),
	ACEITAR("aceitar");

	private String descricao;

	private TipoEstrategia(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}

	@Override
	public String toString() {
		return descricao;
	}
}