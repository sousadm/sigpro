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
import javax.validation.constraints.DecimalMin;

@Entity
@NamedQueries({
		@NamedQuery(name = "RegraPgto.listaPorPgto", query = "select r from RegraPgto r where r.formaPgto = :pgto") })
public class RegraPgto implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private FormaPgto formaPgto;
	private int quantidade;
	private int tempo;
	private double desconto;
	private double juro;
	private boolean unifica;

	public RegraPgto() {
		unifica = false;
		desconto = 0;
		tempo = 0;
		quantidade = 1;
		juro = 0;
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
	public FormaPgto getFormaPgto() {
		return formaPgto;
	}

	public void setFormaPgto(FormaPgto formaPgto) {
		this.formaPgto = formaPgto;
	}

	@DecimalMin("0")
	public int getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(int quantidade) {
		this.quantidade = quantidade;
	}

	@DecimalMin("0")
	public int getTempo() {
		return tempo;
	}

	public void setTempo(int tempo) {
		this.tempo = tempo;
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

	public boolean isUnifica() {
		return unifica;
	}

	public void setUnifica(boolean unifica) {
		this.unifica = unifica;
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
		RegraPgto other = (RegraPgto) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}