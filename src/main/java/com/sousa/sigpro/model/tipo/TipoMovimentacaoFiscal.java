package com.sousa.sigpro.model.tipo;

public enum TipoMovimentacaoFiscal {

	COMPRA_INTERNA("Aquisição interna"), 
	COMPRA_DE_FORA("Aquisição de fora"), 
	VENDA_INTERNA("Venda interna"), 
	VENDA_PRA_FORA("Venda pra fora");

	private String descricao;

	private TipoMovimentacaoFiscal(String valor) {
		this.descricao = valor;
	}

	@Override
	public String toString() {
		return descricao;
	}
	
	public String getDescricao() {
		return descricao;
	}
}