package com.sousa.sigpro.model;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;

@Embeddable
public class ProdutoFornecedorSequencia implements Serializable {

	private static final long serialVersionUID = 1L;

	private Produto produto;
	private Fornecedor fornecedor;

	public ProdutoFornecedorSequencia() {

	}

	public ProdutoFornecedorSequencia(Produto produto, Fornecedor fornecedor) {
		super();
		this.produto = produto;
		this.fornecedor = fornecedor;
	}

	@ManyToOne
	public Fornecedor getFornecedor() {
		return fornecedor;
	}

	public void setFornecedor(Fornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}

	@ManyToOne
	public Produto getProduto() {
		return produto;
	}

	public void setProduto(Produto produto) {
		this.produto = produto;
	}

}