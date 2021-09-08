package com.sousa.sigpro.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

@Entity
@NamedQueries({
		@NamedQuery(name = "CaixaTitulo.TESTE", query = "select t from CaixaTitulo t join t.caixa c join c.diario d order by d.agente, d.dataMov, c.emissao, t.id"),
		@NamedQuery(name = "CaixaTitulo.listaPorTitulo", query = "select c from CaixaTitulo c where c.caixa.estorno is null and c.caixa.encerramento is not null and c.titulo = :titulo order by c.id"),
		@NamedQuery(name = "CaixaTitulo.listaPorDiario", query = "select c from CaixaTitulo c where c.caixa.diario = :diario order by c.id") })
public class CaixaTitulo implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private Caixa caixa;
	private Titulo titulo;
	private double valor;
	private double valorParcial;
	private double valorPago;
	private double desconto;
	private double multa;
	private double juro;
	private double valorJuro;
	private double valorMulta;
	private double valorDesconto;
	private int dia;

	public CaixaTitulo() {
		valor = 0;
		valorPago = 0;
		desconto = 0;
		multa = 0;
		juro = 0;
		valorJuro = 0;
		valorMulta = 0;
		valorDesconto = 0;
		dia = 0;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@ManyToOne
	public Caixa getCaixa() {
		return caixa;
	}

	public void setCaixa(Caixa caixa) {
		this.caixa = caixa;
	}

	@ManyToOne
	public Titulo getTitulo() {
		return titulo;
	}

	public void setTitulo(Titulo titulo) {
		this.titulo = titulo;
	}

	@Column(precision = 12, scale = 2)
	public double getValorParcial() {
		return valorParcial;
	}

	public void setValorParcial(double valorParcial) {
		this.valorParcial = valorParcial;
	}

	@Column(precision = 12, scale = 2)
	public double getDesconto() {
		return desconto;
	}

	public void setDesconto(double desconto) {
		this.desconto = desconto;
	}

	@Column(precision = 12, scale = 2)
	public double getJuro() {
		return juro;
	}

	public void setJuro(double juro) {
		this.juro = juro;
	}

	@Column(precision = 12, scale = 2)
	public double getMulta() {
		return multa;
	}

	public void setMulta(double multa) {
		this.multa = multa;
	}

	@Column(precision = 12, scale = 2)
	public double getValorDesconto() {
		return valorDesconto;
	}

	public void setValorDesconto(double valorDesconto) {
		this.valorDesconto = valorDesconto;
	}

	@Column(precision = 12, scale = 2)
	public double getValorJuro() {
		return valorJuro;
	}

	public void setValorJuro(double valorJuro) {
		this.valorJuro = valorJuro;
	}

	@Column(precision = 12, scale = 2)
	public double getValorMulta() {
		return valorMulta;
	}

	public void setValorMulta(double valorMulta) {
		this.valorMulta = valorMulta;
	}

	public int getDia() {
		return dia;
	}

	public void setDia(int dia) {
		this.dia = dia;
	}

	@Column(precision = 12, scale = 2)
	public double getValor() {
		return valor;
	}

	public void setValor(double valor) {
		this.valor = valor;
	}

	@Column(precision = 12, scale = 2)
	public double getValorPago() {
		return valorPago;
	}

	public void setValorPago(double valorPago) {
		this.valorPago = valorPago;
	}

	public void calcularDesconto() {
		valorParcial = 0;
		if (valor > 0) {
			desconto = valorDesconto / valor * 100;
		} else {
			desconto = 0;
		}
		calcularItem();
	}

	public void calcularValorDesconto() {
		valorParcial = 0;
		if (desconto > 0) {
			valorDesconto = valor * desconto / 100;
		} else {
			valorDesconto = 0;
		}
		calcularItem();
	}

	public void calcularJuro() {
		valorParcial = 0;
		if (valor > 0 && valorJuro > 0 && dia > 0) {
			juro = ((valorJuro / valor) * 100) / dia;
		} else {
			juro = 0;
		}
		calcularItem();
	}

	public void calcularValorJuro() {
		valorParcial = 0;
		if (dia > 0) {
			valorJuro = dia * (valor * juro / 100);
		} else {
			valorJuro = 0;
		}
		calcularItem();
	}

	public void calcularMulta() {
		valorParcial = 0;
		if (valor > 0)
			multa = valorMulta / valor * 100;
		else
			multa = 0;
		calcularItem();
	}

	public void calcularValorMulta() {
		valorParcial = 0;
		if (dia > 0)
			valorMulta = valor * multa / 100;
		else
			valorMulta = 0;
		calcularItem();
	}

	public void calcularValorParcial() {
		if (valorParcial > 0) {
			if (valorParcial > titulo.getSaldo())
				valorParcial = titulo.getSaldo();
			multa = 0;
			valorMulta = 0;
			juro = 0;
			valorJuro = 0;
			valorDesconto = 0;
			desconto = 0;
			calcularItem();
		}
	}

	public void calcularItem() {
		if (valorParcial > 0) {
			valorPago = valorParcial;
		} else {
			valorPago = valor + valorMulta + valorJuro - valorDesconto;
		}
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
		CaixaTitulo other = (CaixaTitulo) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}