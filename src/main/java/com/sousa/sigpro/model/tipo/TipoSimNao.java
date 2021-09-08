package com.sousa.sigpro.model.tipo;

public enum TipoSimNao {

	SIM("Sim", true), 
	NAO("Não", false);

	private String descricao;
	private boolean valor;

	private TipoSimNao(String descricao, boolean valor) {
		this.descricao = descricao;
		this.valor = valor;
	}

	@Override
	public String toString() {
		return this.descricao;
	}

	public boolean isValor() {
		return valor;
	}
}