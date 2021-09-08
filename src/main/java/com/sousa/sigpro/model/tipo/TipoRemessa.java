package com.sousa.sigpro.model.tipo;

public enum TipoRemessa {

	INCLUSAO("inclusão"), ALTERACAO("alteração"), EXCLUSAO("exclusão");

	private String descricao;

	private TipoRemessa(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}

	@Override
	public String toString() {
		return this.ordinal() + 1 + "-" + this.descricao;
	}

}