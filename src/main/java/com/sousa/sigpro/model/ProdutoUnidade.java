package com.sousa.sigpro.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.sousa.sigpro.model.tipo.TipoUnidade;

@Entity
public class ProdutoUnidade implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private Produto produto;
	private TipoUnidade unidade;
	private double fatorEstoque;
	private double fatorPreco;

	public ProdutoUnidade() {

	}

	public ProdutoUnidade(Produto produto) {
		this.produto = produto;
		this.fatorEstoque = 1;
		this.fatorPreco = 1;
		this.unidade = produto.getUnidade();
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
	public Produto getProduto() {
		return produto;
	}

	public void setProduto(Produto produto) {
		this.produto = produto;
	}

	@Column(length = 10)
	@Enumerated(EnumType.STRING)
	public TipoUnidade getUnidade() {
		return unidade;
	}

	public void setUnidade(TipoUnidade unidade) {
		this.unidade = unidade;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Column(precision = 12, scale = 4)
	public double getFatorEstoque() {
		return fatorEstoque;
	}

	public void setFatorEstoque(double fatorEstoque) {
		this.fatorEstoque = fatorEstoque;
	}

	@Column(precision = 12, scale = 4)
	public double getFatorPreco() {
		return fatorPreco;
	}

	public void setFatorPreco(double fatorPreco) {
		this.fatorPreco = fatorPreco;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProdutoUnidade other = (ProdutoUnidade) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}