package com.sousa.sigpro.model.tipo;

public enum TipoComoConheceu {

	ENC("Encaminhamento médico"), 
	GUM("Guia médico"),
	HSP("Hospital"),
	IND("Indicação de paciente"),
	NET("Internet"),
	OUT("Outros");

	private String descricao;

	private TipoComoConheceu(String valor) {
		this.descricao = valor;
	}

	public String getDescricao() {
		return descricao;
	}

}
