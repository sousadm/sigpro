package com.sousa.sigpro.model.tipo;

public enum TipoCentroCusto {

	FATURAMENTO("receita de vendas", TipoEntradaSaida.ENTRADA, TipoFluxoCaixa.RECEITA), //entradas
	OUTRA_DESPESA("outras despesas", TipoEntradaSaida.SAIDA, TipoFluxoCaixa.FIXO), //despesa não-operacional
	OUTRA_RECEITA("outras receitas", TipoEntradaSaida.ENTRADA, TipoFluxoCaixa.RECEITA),	//receita não-operacional
	CUSTO_VENDA("custo da venda", TipoEntradaSaida.SAIDA, TipoFluxoCaixa.VARIAVEL), //custo variável
	DESPESA_COMERCIAL("despesas comerciais", TipoEntradaSaida.SAIDA, TipoFluxoCaixa.FIXO), //custo fixo
	DESPESA_ADMINISTRATIVA("despesas administrativas", TipoEntradaSaida.SAIDA, TipoFluxoCaixa.FIXO), //custo fixo
	DESPESA_COM_SERVICO("despesa com serviços", TipoEntradaSaida.SAIDA, TipoFluxoCaixa.FIXO), //custo fixo
	DESPESA_COM_PESSOAL("despesa com pessoal", TipoEntradaSaida.SAIDA, TipoFluxoCaixa.FIXO), //custo fixo
	DESPESA_COM_VEICULOS("despesa com veículos", TipoEntradaSaida.SAIDA, TipoFluxoCaixa.VARIAVEL),
	DESPESA_FINANCEIRA("despesas financeiras", TipoEntradaSaida.SAIDA, TipoFluxoCaixa.FIXO), //custo fixo
	IMPOSTO_CONTRIBUICAO("impostos e contribuições", TipoEntradaSaida.SAIDA, TipoFluxoCaixa.VARIAVEL), //custo fixo
	INVESTIMENTO("investimento", TipoEntradaSaida.SAIDA, TipoFluxoCaixa.INVESTIMENTO); //investimento

	private String descricao;
	private TipoEntradaSaida movimento;
	private TipoFluxoCaixa fluxo;

	TipoCentroCusto(String descricao, TipoEntradaSaida movimento, TipoFluxoCaixa fluxo) {
		this.descricao = descricao;
		this.movimento = movimento;
		this.fluxo = fluxo;
	}

	@Override
	public String toString() {
		return descricao;
	}

	public String getDescricao() {
		return descricao;
	}
	
	public TipoEntradaSaida getMovimento() {
		return movimento;
	}

	public TipoFluxoCaixa getFluxo() {
		return fluxo;
	}
}