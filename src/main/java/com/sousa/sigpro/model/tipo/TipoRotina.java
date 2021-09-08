package com.sousa.sigpro.model.tipo;

public enum TipoRotina {

	// rotinas de cadastro
	PESSOA_CADASTRO("cadastro de pessoas", "cadastro"),
	PESSOA_LEITURA("cadastro de pessoa somente leitura", "extra"),
	PRODUTO_CADASTRO("cadastro de produtos / serviços", "cadastro"),
	FORMACAO_PRECO("formação de preços", "cadastro"),
	PRODUTO_LEITURA("cadastro de produtos / serviços somente leitura", "extra"),
	CENTROCUSTO("cadastro de centro de custos", "cadastro"),
	PLANOCONTA("cadastro de plano de contas", "cadastro"),
	CATEGORIAPRODUTO("cadastro de categoria de produtos", "cadastro"),
	ATIVIDADE("cadastro de atividade funcional", "cadastro"),
	CLINICA_CONVENIO("cadastro de convênio", "cadastro"),
	CLINICA_HORARIO("cadastro de horário", "cadastro"),
	
	// rotinas do comercial
	VENDA("cadastro de pedido de vendas", "comercial"),
	VENDA_LEITURA("cadastro de pedido de vendas somente leitura", "extra"),
	VEICULO("cadastro de veículos", "comercial"),
	AGENDA("gerenciamento de agenda de retorno", "comercial"),
	RELATORIOVENDA("emissão de relatórios de vendas", "comercial"),
	
	// rotinas de aquisição de materiais
	AQUISICAOCADASTRO("cadastro de aquisição de materiais", "aquisicao"),

	// rotinas de serviços
	SERVICORDEM("controle de ordem de serviços", "servico"),
	SERVICOSPOOL("controle de spool de serviços", "servico"),
	SERVICOCOMANDO("cadastro de comando de serviços", "servico"),
	SERVICOREMUNERADOR("controle de remuneração de serviços", "servico"),
	CLINICA_AGENDAMENTO("controle de agendamento", "servico"),
	CLINICA_ACOLHIMENTO("acolhimento clínico", "servico"),
	CLINICA_ATENDIMENTO("atendimento clínico", "servico"),
	
	MONTAGEMPRODUTOSPOOL("spool de montagem de produtos", "servico"),

	// rotinas do financeiro
	DIARIO("Diário financeiro", "financeiro"),
	CAIXA("Caixa", "financeiro"),
	TITULO("cadastro de títulos financeiros", "financeiro"),
	CHEQUE("controle de cheques emitidos", "financeiro"),
	PATRIMONIO("análise patrimonial", "financeiro"),
	POSFIN("posição financeira", "financeiro"),
	CONCILIACAO("conciliação bancária", "financeiro"),
	FORMAPGTO("cadastro forma de pagamento", "financeiro"),
	CONTABCO("cadastro de conta bancária", "financeiro"),
	CARTAO("cadastro de cartão bancário", "financeiro"),
	META("cadastro de metas financeiras", "financeiro"),
	FLUXO_FINANCEIRO("fluxo financeiro", "financeiro"),
	REMESSA("Controle de bordero de títulos", "financeiro"),
	INDICADORES("Indicadores financeiros", "financeiro"),

	// rotinas fiscais
	NFE("emissão de nota fiscal eletrônica", "fiscal"),
	NFCE("nota fiscal consumidor eletrônica", "fiscal"),
	NFSE("emissão de nota fiscal de serviço", "fiscal"),
	FISCALOPERACAO("cadastro de operação fiscal", "fiscal"),
	MODULO_FISCAL("cadastro equipamento fiscal", "fiscal"),
	
	// rotinas de planejamento
	QUESTIONARIO("cadastro de questionário", "planejamento"),
	PROJETO("cadastro de projetos", "planejamento"),
	DEFINICAO("cadastro de definição estratégica", "planejamento"),
	SWOT("cadastro de análise de SWOT", "planejamento"),
	SWOTCRUZA("cruzamento de análise de SWOT", "planejamento"),
	PLANODEACAO("gerenciamento de plano de ação", "planejamento");
	
	private String descricao;
	private String grupo;
	
	private TipoRotina(String descricao, String grupo) {
		this.descricao = descricao;
		this.grupo = grupo;
	}

	public String getDescricao() {
		return descricao;
	}
	
	public String getGrupo() {
		return grupo;
	}
}