package com.sousa.sigpro.model.tipo;

public enum TipoSituacaoFinanceira {

	ABERTO("Em aberto", "Aberto"), 
	VENCIDO("Vencido", "Parcial"), 
	LIQUIDADO("Liquidado", "Total");

	private String descricao;
	private String status;

	private TipoSituacaoFinanceira(String valor, String status) {
		this.descricao = valor;
		this.status = status;
	}

	public String getDescricao() {
		return descricao;
	}

	public String getStatus() {
		return status;
	}
}
