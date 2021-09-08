package com.sousa.sigpro.model.tipo;

public enum TipoAreaQuestionario {

	ESTRATEGIA("Estratégia"),
	PLANEJAMENTO("Planejamento"),
	INDICADOR("Indicadores"),
	CONTROLE("Avaliação/Controle"),
	INFORMACAO("Sist.Informação"),
	FINANCEIRO("Financeiro"),
	LOGISTICA("Logística"),
	MKT_VENDAS("Marketing e vendas"),
	PRODUTO("Produtos ou Serviços"),
	RECURSO_HUMANO("Recursos Humanos");
	
	private String descricao;

	private TipoAreaQuestionario(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}

}