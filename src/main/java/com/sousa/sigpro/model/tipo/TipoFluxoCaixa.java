package com.sousa.sigpro.model.tipo;

public enum TipoFluxoCaixa {
	
	RECEITA("Receita"), 
	VARIAVEL("Variável"), 
	FIXO("Fixo"), 
	INVESTIMENTO("Investimento");
	
	private String descricao;
	
	private TipoFluxoCaixa(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}
}