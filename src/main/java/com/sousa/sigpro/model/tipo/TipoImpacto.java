package com.sousa.sigpro.model.tipo;

public enum TipoImpacto {

	GRANDE("4-Grande", 4),
	MODERADO("3-Moderado", 3),
	PEQUENO("1-Pequeno", 1);

	private String descricao;
	private int valor;

	private TipoImpacto(String desc, int valor) {
		this.descricao = desc;
		this.valor = valor;
	}

	public String getDescricao() {
		return descricao;
	}
	
	public int getValor() {
		return valor;
	}

}