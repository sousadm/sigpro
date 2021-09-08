package com.sousa.sigpro.model.tipo;

public enum TipoProduto {

	REVENDA("Mercadoria para Revenda", "Produto", true), 
	MATERIA_PRIMA("Matéria-Prima", "Insumo", true), 
	EMBALAGEM("Embalagem", "Embalagem", true), 
	EMPROCESSO("Produto em Processo", "Produto", false), 
	ACABADO("Produto Acabado", "Produto", true), 
	SUBPRODUTO("Subproduto", "Subproduto", false), 
	INTERMEDIARIO("Produto Intermediário", "Produto", false), 
	CONSUMO("Material de Uso e Consumo", "Consumo", false), 
	IMOBILIZADO("Ativo Imobilizado", "Ativo", true), 
	SERVICO("Serviços", "Serviço", false), 
	INSUMOS("Outros insumos", "Insumo", false);

	private String descricao;
	private String reduzido;
	private boolean ativo;

	private TipoProduto(String descricao, String reduzido, boolean ativo) {
		this.descricao = descricao;
		this.reduzido = reduzido;
		this.ativo = ativo;
	}

	public String getDescricao() {
		return descricao;
	}

	public String getReduzido() {
		return reduzido;
	}

	public boolean isAtivo() {
		return ativo;
	}
}