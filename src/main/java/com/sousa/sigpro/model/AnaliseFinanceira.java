package com.sousa.sigpro.model;

public class AnaliseFinanceira {

	private double receita = 0;
	private double despesa = 0;
	private double ativoPatrimonial = 0;

	private double lucro = 0;
	private double lucratividade = 0;
	private double rentabilidade = 0;

	public void calcular() {
		lucro = receita - despesa;
		lucratividade = lucro / receita;
		rentabilidade = lucro / ativoPatrimonial;
	}

	public double getReceita() {
		return receita;
	}

	public void setReceita(double receita) {
		this.receita = receita;
	}

	public double getDespesa() {
		return despesa;
	}

	public void setDespesa(double despesa) {
		this.despesa = despesa;
	}

	public double getAtivoPatrimonial() {
		return ativoPatrimonial;
	}

	public void setAtivoPatrimonial(double ativoPatrimonial) {
		this.ativoPatrimonial = ativoPatrimonial;
	}

	public double getLucratividade() {
		return lucratividade;
	}

	public double getRentabilidade() {
		return rentabilidade;
	}

	public double getLucro() {
		return lucro;
	}
}