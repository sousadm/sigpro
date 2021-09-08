package com.sousa.sigpro.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Transient;

import com.sousa.sigpro.model.tipo.TipoMetodoCalculo;

@Embeddable
public class Custo implements Serializable {

	private static final long serialVersionUID = 1L;

	private double credito;
	private double imposto;
	private double despesa;
	private double frete;

	private double margem;
	private double lucro;
	private double residual;
	private double tributo;

	private TipoMetodoCalculo metodo = TipoMetodoCalculo.NONE;
	private double fatorCalculo;
	private double precoCusto;

	@Column(length = 4)
	@Enumerated(EnumType.STRING)
	public TipoMetodoCalculo getMetodo() {
		return metodo;
	}

	public void setMetodo(TipoMetodoCalculo metodo) {
		this.metodo = metodo;
	}

	@Column(precision = 12, scale = 4)
	public double getFatorCalculo() {
		return fatorCalculo;
	}

	public void setFatorCalculo(double fatorCalculo) {
		this.fatorCalculo = fatorCalculo;
	}

	@Column(precision = 12, scale = 4)
	public double getCredito() {
		return credito;
	}

	public void setCredito(double credito) {
		this.credito = credito;
	}

	@Column(precision = 12, scale = 4)
	public double getImposto() {
		return imposto;
	}

	public void setImposto(double imposto) {
		this.imposto = imposto;
	}

	@Column(precision = 12, scale = 4)
	public double getDespesa() {
		return despesa;
	}

	public void setDespesa(double despesa) {
		this.despesa = despesa;
	}

	@Column(precision = 12, scale = 4)
	public double getFrete() {
		return frete;
	}

	public void setFrete(double frete) {
		this.frete = frete;
	}

	@Column(precision = 12, scale = 4)
	public double getMargem() {
		return margem;
	}

	public void setMargem(double margem) {
		this.margem = margem;
	}

	@Column(precision = 12, scale = 4)
	public double getLucro() {
		return lucro;
	}

	public void setLucro(double lucro) {
		this.lucro = lucro;
	}

	@Column(precision = 12, scale = 4)
	public double getResidual() {
		return residual;
	}

	public void setResidual(double residual) {
		this.residual = residual;
	}

	@Column(precision = 12, scale = 4)
	public double getTributo() {
		return tributo;
	}

	public void setTributo(double tributo) {
		this.tributo = tributo;
	}

	@Column(name = "preco_custo", precision = 12, scale = 4)
	public double getPrecoCusto() {
		return precoCusto;
	}

	public void setPrecoCusto(double precoCusto) {
		this.precoCusto = precoCusto;
	}

	@Transient
	public boolean isMarkUpConsistente() {
		return (margem >= 0) && (lucro >= 0) && (residual >= 0) && (tributo >= 0);
	}

	@Transient
	public double getFatorCusto() {
		double valor = 1 + ((imposto + despesa + frete) / 100) - (credito / 100);
		return valor > 0 ? valor : 0;
	}

	@Transient
	public double getMarkupMultiplicador() {
		double mkd = 1 - ((margem + lucro + residual + tributo) / 100);
		return 1 / mkd;
	}

	@Transient
	public double getFatorVenda() {
		return getFatorCusto() * getMarkupMultiplicador();
	}

}