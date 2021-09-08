package com.sousa.sigpro.model;

import java.math.BigInteger;
import java.util.Date;

import com.sousa.sigpro.model.tipo.TipoPagamento;

public class Movimento {

	private BigInteger id;
	private Date data;
	private String descricao;
	private String nominal;
	private TipoPagamento tipo;
	private double valorEntrada = 0;
	private double valorSaida = 0;
	private double valor = 0;
	private String statusCor;

	public Movimento(TipoPagamento tipo, double valorEntrada, double valorSaida) {
		this.tipo = tipo;
		this.valorEntrada = valorEntrada;
		this.valorSaida = valorSaida;
	}

	public Movimento(BigInteger id, Date data, TipoPagamento tipo, String descricao, String nominal, double valor) {
		this.id = id;
		this.data = data;
		this.tipo = tipo;
		this.descricao = descricao;
		this.nominal = nominal;
		this.valor = valor;
		
		if (valor < 0)
			statusCor = "red";
		else
			statusCor = "black";
		
	}

	public String getStatusCor() {
		return statusCor;
	}

	public double getValor() {
		return valor;
	}

	public void setValor(double valor) {
		this.valor = valor;
	}

	public BigInteger getId() {
		return id;
	}

	public void setId(BigInteger id) {
		this.id = id;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public String getNominal() {
		return nominal;
	}

	public void setNominal(String nominal) {
		this.nominal = nominal;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public void addEntrada(double valor) {
		valorEntrada = valorEntrada + valor;
	}

	public void addSaida(double valor) {
		valorSaida = valorSaida + valor;
	}

	public TipoPagamento getTipo() {
		return tipo;
	}

	public void setTipo(TipoPagamento tipo) {
		this.tipo = tipo;
	}

	public double getValorEntrada() {
		return valorEntrada;
	}

	public void setValorEntrada(double valorEntrada) {
		this.valorEntrada = valorEntrada;
	}

	public double getValorSaida() {
		return valorSaida;
	}

	public void setValorSaida(double valorSaida) {
		this.valorSaida = valorSaida;
	}

}