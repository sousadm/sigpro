package com.sousa.sigpro.model.tipo;

public enum TipoTituloDoc {

	DIN("Dinheiro"), 
	CHE("Cheque"),
	BLT("Boleto"),
	CRE("Crédito"),
	DEB("Débito"),
	CRT("Carteira");

	private String descricao;

	private TipoTituloDoc(String valor) {
		this.descricao = valor;
	}

	public String getDescricao() {
		return descricao;
	}

}