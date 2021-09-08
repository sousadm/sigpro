package com.sousa.sigpro.model.tipo;

public enum TipoLicaoAprendida {

	PRODUTO_CONFORME("O produto final foi entregue conforme o acordado (requisitos / especificações)?"),
	PRAZO_CONFORME("Houve desvios entre os prazos realizados e programados?"),
	CUSTO_CONFORME("Houve desvios entre os custos efetivos e os orçados?"),
	DESVIO_EVITADO("Os desvios poderiam ter sido evitados?"),
	RESULTADO_ALCANCADO("Os resultados propostos foram alcançados?"),
	INESPERADO_OCORRIDO("Eventos inesperados ocorreram?"),
	CLIENTE_SATISFEITO("Os clientes / usuários estão satisfeitos?"),
	SPONSER_APOIADOR("O apoio dos patrocinadores foi satisfatório?"),
	PROBLEMA_COMUNICACAO("Houve problemas de comunicação?"),
	FORNECEDOR_CONFORME("Os fornecedores entregaram seus produtos / serviços em conformidade com as especificações combinadas?"),
	PLANO_CONFORME("Houve mudanças no plano do projeto após o início da execução? Como foram gerenciadas?"),
	ESCOPO_CONFORME("Houve mudanças no escopo do projeto após o início da execução? Como foram gerenciadas?");

	private String descricao;
	
	private TipoLicaoAprendida(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}

	@Override
	public String toString() {
		return descricao;
	}
}