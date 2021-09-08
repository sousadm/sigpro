package com.sousa.sigpro.model.tipo;

public enum TipoOcorrencia {

	PEDIDO_BAIXA("02", "Pedido de Baixa"), 
	CONCESSAO_ABATIMENTO("04", "Concessão de Abatimento"), 
	CANCELAMENTO_ABATIMENTO("05", "Cancelamento de Abatimento concedido"), 
	ALTERAR_VENCIMENTO("06", "Alteração de vencimento"), 
	ALTERAR_PARTICIPANTE("07", "Alteração do número de controle do participante"), 
	ALTERAR_NUMERO("08", "Alteração de seu número"), 
	PEDIDO_PROTESTO("09", "Pedido de protesto"), 
	SUSTAR_PROTESTO_BAIXAR("10", "Sustar protesto e baixar"), 
	SUSTAR_PROTESTO_MANTER("10", "Sustar protesto e manter na carteira"), 
	DISPENSAR_JURO("11", "Instrução para dispensar juros"), 
	ALTERAR_NOME_ENDERECO("12", "Alteração de nome e endereço do Sacado"), 
	ALTERAR_OUTROS_DADOS("31", "Alteração de Outros Dados"), 
	NAO_CONCEDER_DESCONTO("32", "Não conceder desconto"), 
	ALTERAR_MODALIDADE("40", "Alterar modalidade (Vide Observações)");

	private String codigo;
	private String descricao;

	private TipoOcorrencia(String codigo, String descricao) {
		this.codigo = codigo;
		this.descricao = descricao;
	}

	public String getCodigo() {
		return codigo;
	}

	public String getDescricao() {
		return descricao;
	}

}