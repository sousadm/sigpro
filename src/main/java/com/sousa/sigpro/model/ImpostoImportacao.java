package com.sousa.sigpro.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class ImpostoImportacao implements Serializable {

	private static final long serialVersionUID = 1L;

	private double baseCalculoII;
	private double valorDespesaAduaneiraII;
	private double valorImpostoImportacao;
	private double valorIOF;

	public ImpostoImportacao() {
		baseCalculoII = 0;
		valorDespesaAduaneiraII = 0;
		valorImpostoImportacao = 0;
		valorIOF = 0;
	}

	@Column(precision = 12, scale = 2)
	public double getBaseCalculoII() {
		return baseCalculoII;
	}

	public void setBaseCalculoII(double baseCalculoII) {
		this.baseCalculoII = baseCalculoII;
	}

	@Column(precision = 12, scale = 2)
	public double getValorDespesaAduaneiraII() {
		return valorDespesaAduaneiraII;
	}

	public void setValorDespesaAduaneiraII(double valorDespesaAduaneiraII) {
		this.valorDespesaAduaneiraII = valorDespesaAduaneiraII;
	}

	@Column(precision = 12, scale = 2)
	public double getValorImpostoImportacao() {
		return valorImpostoImportacao;
	}

	public void setValorImpostoImportacao(double valorImpostoImportacao) {
		this.valorImpostoImportacao = valorImpostoImportacao;
	}

	@Column(precision = 12, scale = 2)
	public double getValorIOF() {
		return valorIOF;
	}

	public void setValorIOF(double valorIOF) {
		this.valorIOF = valorIOF;
	}

}