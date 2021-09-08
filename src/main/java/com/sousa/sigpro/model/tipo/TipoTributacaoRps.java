package com.sousa.sigpro.model.tipo;

public enum TipoTributacaoRps {

	NORMAL("Operação normal", "T"), 
	ISENTA_POR_SAOPAULO("Isenta, executadas no Município de São Paulo", "I"), 
	ISENTA_FORA_SAOPAULO("Isenta em SP, executada em outro Município", "F"), 
	SUSPENSAO("ISS Suspenso por Decisão Judicial", "J");

	private String descricao;
	private String codigo;

	private TipoTributacaoRps(String descricao, String codigo) {
		this.descricao = descricao;
		this.codigo = codigo;
	}

	public String getDescricao() {
		return descricao;
	}

	public String getCodigo() {
		return codigo;
	}

}