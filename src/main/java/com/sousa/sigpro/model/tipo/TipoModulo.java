package com.sousa.sigpro.model.tipo;

public enum TipoModulo {

	CADASTRO("Cadastro"), 
	AQUISICAO("Aquisição"),
	EXPEDICAO("Expedição"),
	COMERCIAL("Comercial"),
	PRODUCAO("Produção"),
	SERVICO("Serviço"),
	FINANCEIRO("Financeiro"),
	ESTOQUE("Estoque"),
	FISCAL("Fiscal"), 
	ANALISE("Análise"), 
	PLANEJAMENTO("Planejamento"),
	PROJETO("Projeto");
		
	private String descricao;
	
	private TipoModulo(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}
	
}