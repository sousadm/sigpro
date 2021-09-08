package com.sousa.sigpro.model.tipo;

public enum TipoRequisito {

	PRODUTO("requisito de produto"),
	PROJETO("requisito de projeto"),
	EXCLUDENTE("não faz parte do projeto"),
	ACEITACAO("critério de aceitação"),
	PREMISSA("premissa"),
	RESTRICAO("restrição");

	private String descricao;
	
	private TipoRequisito(String descricao) {
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