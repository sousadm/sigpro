package com.sousa.sigpro.model.tipo;

public enum TipoGrupo {

	CEO("Staff"),
	OPE("Operação"),
	CON("Convidado");
	
	private String descricao;
	
	private TipoGrupo(String descricao) {
		this.descricao = descricao;
	}
	
	public String getDescricao() {
		return descricao;
	}
}