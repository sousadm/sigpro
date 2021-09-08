package com.sousa.sigpro.model.tipo;

public enum TipoEncerramentoMotivo {
	
	CONCLUIDO("concluído"),
	CANCELADO("cancelado");

	private String descricao;
	
	private TipoEncerramentoMotivo(String descricao) {
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