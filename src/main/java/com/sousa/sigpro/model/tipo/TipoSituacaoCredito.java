package com.sousa.sigpro.model.tipo;

public enum TipoSituacaoCredito {
	
	RESTRITO("restrição"),
	LIBERADO("Liberado"),
	BLOQUEADO("Bloqueado");
	
	private String descricao;

	TipoSituacaoCredito(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}
}
