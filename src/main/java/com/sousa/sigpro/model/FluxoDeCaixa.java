package com.sousa.sigpro.model;

public class FluxoDeCaixa {

	private String historico;
	private double valor;
	private double vertical;
	private String banda;

	public FluxoDeCaixa(String historico, String banda) {
		this.historico = historico;
		this.banda = banda;
	}

	public FluxoDeCaixa() {
		valor = 0;
		vertical = 0;
	}

	public void addValor(double valor) {
		this.valor = this.valor + valor;
	}

	public void calculaVertical(double padrao) {
		if (padrao > 0)
			vertical = valor / padrao;
	}

	public double getValor() {
		return valor;
	}

	public void setValor(double valor) {
		this.valor = valor;
	}

	public String getHistorico() {
		return historico;
	}

	public void setHistorico(String historico) {
		this.historico = historico;
	}

	public String getBanda() {
		return banda;
	}

	public void setBanda(String banda) {
		this.banda = banda;
	}

	public double getVertical() {
		return vertical;
	}

	public void setVertical(double vertical) {
		this.vertical = vertical;
	}

}