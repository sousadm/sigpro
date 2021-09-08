package com.sousa.sigpro.model.tipo;

public enum TipoGraficoModelo {

	FATURAMENTO("Faturamento"),
	FLUXO_CAIXA("Fluxo de caixa"),
	FLUXO_PREVISTO("Previsão de Fluxo"),
	PAGAMENTOS("Pagamentos");

	private String descricao;

	private TipoGraficoModelo(String descricao) {
		this.descricao = descricao;
	}

	@Override
	public String toString() {
		return descricao;
	}

	public String getDescricao() {
		return descricao;
	}
}
