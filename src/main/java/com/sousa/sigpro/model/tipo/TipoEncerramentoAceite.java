package com.sousa.sigpro.model.tipo;

public enum TipoEncerramentoAceite {
	
	TOTAL("concluído"), 
	PARCIAL("cancelado"),
	RECUSADO("recusado");

	private String descricao;

	private TipoEncerramentoAceite(String descricao) {
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