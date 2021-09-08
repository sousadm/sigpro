package com.sousa.sigpro.model.tipo;

public enum TipoSwot {

	FORCA("força"),
	FRAQUEZA("fraqueza"),
	AMEACA("ameaça"),
	OPORTUNIDADE("oportunidade");
	
	private String descricao;

	private TipoSwot(String valor) {
		this.descricao = valor;
	}

	public String getDescricao() {
		return descricao;
	}
}
