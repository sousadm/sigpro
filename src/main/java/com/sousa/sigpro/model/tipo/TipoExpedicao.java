package com.sousa.sigpro.model.tipo;

public enum TipoExpedicao {
//	PRO("produção"), 
//	REQ("requisição"), 
	ORC("orçamento"),
	PED("Pedido"), 
	CAN("cancelado");
	
	private String descricao;
	
	private TipoExpedicao(String valor) {
		this.descricao = valor;
	}

	public String getDescricao() {
		return descricao;
	}
	
}
