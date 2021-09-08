package com.sousa.sigpro.model.tipo;

import org.jrimum.domkee.financeiro.banco.febraban.TipoDeCobranca;

public enum TipoCobranca {

	COM_REGISTRO("com registro", TipoDeCobranca.COM_REGISTRO),
	SEM_REGISTRO("sem registro", TipoDeCobranca.SEM_REGISTRO);

	private String descricao;
	private TipoDeCobranca tipoDeCobranca;

	private TipoCobranca(String descricao, TipoDeCobranca tipoDeCobranca) {
		this.descricao = descricao;
		this.tipoDeCobranca = tipoDeCobranca;
	}

	public String getDescricao() {
		return descricao;
	}

	public TipoDeCobranca getTipoDeCobranca() {
		return tipoDeCobranca;
	}

}