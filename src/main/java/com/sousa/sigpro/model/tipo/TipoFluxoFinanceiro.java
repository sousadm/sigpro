package com.sousa.sigpro.model.tipo;

public enum TipoFluxoFinanceiro {

	CAIXA("Fluxo de Caixa"), DRE("DRE");

	private String descricao;

	@Override
	public String toString() {
		return this.descricao;
	}

	private TipoFluxoFinanceiro(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}

}
