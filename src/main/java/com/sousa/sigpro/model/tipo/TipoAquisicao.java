package com.sousa.sigpro.model.tipo;

public enum TipoAquisicao {

	REQUISICAO("requisição", "Requisição de Materiais"),
	PEDIDO("pedido", "Pedido de Compra"),
	REPASSE("repasse", "Repasse de Compra");
	
	private String descricao;
	private String titulo;

	private TipoAquisicao(String valor, String titulo) {
		this.descricao = valor;
		this.titulo = titulo;
	}

	public String getDescricao() {
		return descricao;
	}
	
	public String getTitulo() {
		return titulo;
	}
}
