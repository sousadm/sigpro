package com.sousa.sigpro.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

@Entity
public class BalancoProduto implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private Balanco balanco;
	private Produto produto;
	private double precoMedio;
	private double precoCusto;
	private double estoqueInicial;
	private double entrada;
	private double saida;
	private double entradaFiscal;
	private double saidaFiscal;
	private double giroDeEstoque;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(precision = 12, scale = 4)
	public double getPrecoMedio() {
		return precoMedio;
	}

	public void setPrecoMedio(double precoMedio) {
		this.precoMedio = precoMedio;
	}

	@ManyToOne
	public Produto getProduto() {
		return produto;
	}

	public void setProduto(Produto produto) {
		this.produto = produto;
	}

	@ManyToOne
	public Balanco getBalanco() {
		return balanco;
	}

	public void setBalanco(Balanco balanco) {
		this.balanco = balanco;
	}

	@Column(precision = 12, scale = 4)
	public double getEntrada() {
		return entrada;
	}

	public void setEntrada(double entrada) {
		this.entrada = entrada;
	}

	@Column(precision = 12, scale = 4)
	public double getEntradaFiscal() {
		return entradaFiscal;
	}

	public void setEntradaFiscal(double entradaFiscal) {
		this.entradaFiscal = entradaFiscal;
	}

	@Column(precision = 12, scale = 4)
	public double getEstoqueInicial() {
		return estoqueInicial;
	}

	public void setEstoqueInicial(double estoqueInicial) {
		this.estoqueInicial = estoqueInicial;
	}

	@Column(precision = 12, scale = 4)
	public double getSaida() {
		return saida;
	}

	public void setSaida(double saida) {
		this.saida = saida;
	}

	@Column(precision = 12, scale = 4)
	public double getSaidaFiscal() {
		return saidaFiscal;
	}

	public void setSaidaFiscal(double saidaFiscal) {
		this.saidaFiscal = saidaFiscal;
	}

	@Column(precision = 12, scale = 4)
	public double getPrecoCusto() {
		return precoCusto;
	}

	public void setPrecoCusto(double precoCusto) {
		this.precoCusto = precoCusto;
	}

	public double getGiroDeEstoque() {
		return giroDeEstoque;
	}

	public void setGiroDeEstoque(double giroDeEstoque) {
		this.giroDeEstoque = giroDeEstoque;
	}

	@Transient
	public double getValorGerencial() {
		return precoCusto * getEstoqueGerencial();
	}

	@Transient
	public double getValorContabil() {
		return precoMedio * getEstoqueContabil();
	}

	@Transient
	public double getEstoqueContabil() {
		double valor = estoqueInicial + entradaFiscal - saidaFiscal;
		return valor > 0 ? valor : 0;
	}

	@Transient
	public double getEstoqueGerencial() {
		double valor = estoqueInicial + entrada - saida;
		return valor > 0 ? valor : 0;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BalancoProduto other = (BalancoProduto) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}