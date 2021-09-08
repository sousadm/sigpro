package com.sousa.sigpro.model.tipo;

public enum TipoMes {

	JAN("Janeiro", "Jan"), FEV("Fevereiro", "Fev"), MAR("Março", "Mar"), ABR("Abril", "Abr"), MAI("Maio", "Mai"), JUN(
			"Junho", "Jun"), JUL("Julho", "Jul"), AGO("Agosto", "Ago"), SET("Setembro",
					"Set"), OUT("Outubro", "Out"), NOV("Novembro", "Nov"), DEZ("Dezembro", "Dez");

	private String descricao;
	private String reduzido;

	private TipoMes(String descricao, String reduzido) {
		this.descricao = descricao;
		this.reduzido = reduzido;
	}

	@Override
	public String toString() {
		return this.descricao;
	}

	public String getDescricao() {
		return descricao;
	}

	public String getReduzido() {
		return reduzido;
	}

}
