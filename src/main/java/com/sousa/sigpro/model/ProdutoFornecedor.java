package com.sousa.sigpro.model;

import java.io.Serializable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery; 
import javax.persistence.Table;

@Entity
@Table(name = "produto_fornecedor")
@NamedQueries({
		@NamedQuery(name = "ProdutoFornecedor.porProdutoFornecedor", query = "select p from ProdutoFornecedor p where p.sequencia.fornecedor = :fornecedor and p.sequencia.produto = :produto "),
		@NamedQuery(name = "ProdutoFornecedor.porFornecedorCodigo", query = "select p from ProdutoFornecedor p join p.aquisicaoItem i where p.sequencia.fornecedor = :fornecedor and i.codigoFornecedor = :codigo ") })
public class ProdutoFornecedor implements Serializable {

	private static final long serialVersionUID = 1L;

	private ProdutoFornecedorSequencia sequencia;
	private AquisicaoItem aquisicaoItem;

	@Id
	@Embedded
	public ProdutoFornecedorSequencia getSequencia() {
		return sequencia;
	}

	public void setSequencia(ProdutoFornecedorSequencia sequencia) {
		this.sequencia = sequencia;
	}

	@ManyToOne
	public AquisicaoItem getAquisicaoItem() {
		return aquisicaoItem;
	}

	public void setAquisicaoItem(AquisicaoItem aquisicaoItem) {
		this.aquisicaoItem = aquisicaoItem;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((sequencia == null) ? 0 : sequencia.hashCode());
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
		ProdutoFornecedor other = (ProdutoFornecedor) obj;
		if (sequencia == null) {
			if (other.sequencia != null)
				return false;
		} else if (!sequencia.equals(other.sequencia))
			return false;
		return true;
	}

}