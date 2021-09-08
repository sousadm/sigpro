package com.sousa.sigpro.model.tipo;

public enum TipoAgendaStatus {

	PLANEJADO("planejamento"),
	CANCELADO("cancelado"),
	ENCERRADO("encerrado"),
	SUCESSO("sucesso");
	
	private String descricao;
	
	private TipoAgendaStatus(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}

	@Override
	public String toString() {
		return descricao;
	}
}