package com.sousa.sigpro.model.tipo;

public enum TipoEscopo {

	REQUISITO_PRODUTO("requisito de produto"),
	REQUISITO_PROJETO("requisito de projeto"),
	ENTREGA_PRODUTO("entrega de produto"),
	ENTREGA_PROJETO("entrega de projeto"),
	CRITERIO_ACEITACAO("critério de aceitação"),
	EXCLUDENTE("não faz parte do projeto"),
	PREMISSA("premissa"),
	RESTRICAO("restrição");

	private String descricao;
	
	private TipoEscopo(String descricao) {
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