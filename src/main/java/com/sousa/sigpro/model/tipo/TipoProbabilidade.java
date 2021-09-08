package com.sousa.sigpro.model.tipo;

public enum TipoProbabilidade {

	MAXIMA("acima de 70%", 10), 
	MEDIA("a partir 30,01%", 5), 
	MINIMA("até 30%", 1);

	private String descricao;
	private int valor;

	private TipoProbabilidade(String desc, int valor) {
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
