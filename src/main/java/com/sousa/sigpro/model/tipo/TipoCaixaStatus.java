package com.sousa.sigpro.model.tipo;

public enum TipoCaixaStatus {

	ABERTO("Aberto", ""), 
	REALIZADO("Realizado", "R"), 
	CONFERIDO("Conferido", "C");

	private String descricao;
	private String letra;

	private TipoCaixaStatus(String valor, String letra) {
		this.descricao = valor;
		this.letra = letra;
	}

	public String getDescricao() {
		return descricao;
	}

	public String getLetra() {
		return letra;
	}

}