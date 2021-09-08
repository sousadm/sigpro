package com.sousa.sigpro.model;

import java.io.Serializable;
import java.util.Date;

public class PosicaoFinanceiraDetalhe implements Serializable {

	private static final long serialVersionUID = 1L;

	private Date data;
	private String documento;
	private String memorando;
	private Long caixa;
	private double valor;

	public PosicaoFinanceiraDetalhe() {
		// TODO Auto-generated constructor stub
	}

	public PosicaoFinanceiraDetalhe(Date data, String memorando, double valor) {
		this.data = data;
		this.memorando = memorando;
		this.valor = valor;
	}

	public PosicaoFinanceiraDetalhe(Date data, String documento, String memorando, Long caixa, double valor) {
		this.data = data;
		this.memorando = memorando;
		this.documento = documento;
		this.caixa = caixa;
		this.valor = valor;
	}

	public Long getCaixa() {
		return caixa;
	}

	public void setCaixa(Long caixa) {
		this.caixa = caixa;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public String getMemorando() {
		return memorando;
	}

	public void setMemorando(String memorando) {
		this.memorando = memorando;
	}

	public String getDocumento() {
		return documento;
	}

	public void setDocumento(String documento) {
		this.documento = documento;
	}

	public double getValor() {
		return valor;
	}

	public void setValor(double valor) {
		this.valor = valor;
	}

}