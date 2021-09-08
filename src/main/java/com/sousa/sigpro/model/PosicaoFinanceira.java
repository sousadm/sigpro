package com.sousa.sigpro.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.sousa.sigpro.util.SuporteData;

public class PosicaoFinanceira implements Serializable {

	private static final long serialVersionUID = 1L;

	private Pessoa pessoa;
	private Date inicio;
	private Date termino;
	private double saldoInicial = 0;
	private double entrada = 0;
	private double saida = 0;
	private double saldoAtual = 0;
	private double valorCredito = 0;
	private List<PosicaoFinanceiraDetalhe> detalhes = new ArrayList<>();

	public PosicaoFinanceira() {
		// TODO Auto-generated constructor stub
	}

	public PosicaoFinanceira(Pessoa pessoa, Date inicio, Date termino) {
		this.pessoa = pessoa;
		this.inicio = inicio;
		this.termino = termino;
	}

	public String getTituloRelatorio() {
		return "Período: " + SuporteData.formataDataToStr(inicio, "dd/MM/yyyy") + " a "
				+ SuporteData.formataDataToStr(termino, "dd/MM/yyyy");
	}

	public Pessoa getPessoa() {
		return pessoa;
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}

	@Temporal(value = TemporalType.TIMESTAMP)
	public Date getInicio() {
		return inicio;
	}

	public void setInicio(Date inicio) {
		this.inicio = inicio;
	}

	@Temporal(value = TemporalType.TIMESTAMP)
	public Date getTermino() {
		return termino;
	}

	public void setTermino(Date termino) {
		this.termino = termino;
	}

	public double getSaldoInicial() {
		return saldoInicial;
	}

	public void setSaldoInicial(double saldoInicial) {
		this.saldoInicial = saldoInicial;
	}

	public double getEntrada() {
		return entrada;
	}

	public void setEntrada(double entrada) {
		this.entrada = entrada;
	}

	public double getSaida() {
		return saida;
	}

	public void setSaida(double saida) {
		this.saida = saida;
	}

	public double getSaldoAtual() {
		return saldoAtual;
	}

	public void setSaldoAtual(double saldoAtual) {
		this.saldoAtual = saldoAtual;
		this.valorCredito = saldoAtual < 0 ? 0 : saldoAtual;
	}

	public double getValorCredito() {
		return valorCredito;
	}

	public boolean isTemCredito() {
		return valorCredito > 0;
	}

	public List<PosicaoFinanceiraDetalhe> getDetalhes() {
		return detalhes;
	}

	public void setDetalhes(List<PosicaoFinanceiraDetalhe> detalhes) {
		this.detalhes = detalhes;
	}

}