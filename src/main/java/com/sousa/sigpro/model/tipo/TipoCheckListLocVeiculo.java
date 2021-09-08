package com.sousa.sigpro.model.tipo;

public enum TipoCheckListLocVeiculo {

	MACACO("macaco"),
	ESTEPE("estepe"),
	CHAVE_RODA("chave de roda"),
	TRIANGULO("triângulo"),
	DOCUMENTO("documento"),
	EXTINTOR("extintor"),
	CALOTA("calotas"),
	PLAYER("cd-player ou similar");
	
	private String descricao;
	
	private TipoCheckListLocVeiculo(String descricao) {
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