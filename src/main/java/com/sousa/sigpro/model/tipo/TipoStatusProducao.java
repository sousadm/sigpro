package com.sousa.sigpro.model.tipo;

public enum TipoStatusProducao {

	DISPONIVEL("disponível"),
	PRODUCAO("produção"), 
	PAUSA("pausa"), 
	ENCERRADO("encerrado"),
	CONCLUIDO("concluído");
	
	private String descricao;
	
	private TipoStatusProducao(String valor) {
		this.descricao = valor;
	}

	public String getDescricao() {
		return descricao;
	}
	
}
