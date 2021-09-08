package com.sousa.sigpro.repository;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import com.sousa.sigpro.model.Fornecedor;
import com.sousa.sigpro.model.Produto;
import com.sousa.sigpro.model.ProdutoFornecedor;

public class ProdutoFornecedores implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager manager;

	public ProdutoFornecedor porId(Long id) {
		String condicao = "select p from ProdutoFornecedor p where p.id = " + id;
		return manager.createQuery(condicao, ProdutoFornecedor.class).getSingleResult();
	}

	public ProdutoFornecedor porFornecedorCodigo(Fornecedor fornecedor, String codigo) {
		try {
			return manager.createNamedQuery("ProdutoFornecedor.porFornecedorCodigo", ProdutoFornecedor.class)
					.setParameter("fornecedor", fornecedor).setParameter("codigo", codigo).getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}

	public ProdutoFornecedor porProdutoFornecedor(Produto produto, Fornecedor fornecedor) {
		try {
			return manager.createNamedQuery("ProdutoFornecedor.porProdutoFornecedor", ProdutoFornecedor.class)
					.setParameter("fornecedor", fornecedor).setParameter("produto", produto).getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}

	public List<ProdutoFornecedor> listaPorFornecedor(Fornecedor fornecedor) {
		return manager.createNamedQuery("ProdutoFornecedor.porFornecedor", ProdutoFornecedor.class)
				.setParameter("fornecedor", fornecedor).getResultList();
	}

	public List<ProdutoFornecedor> listaPorProduto(Produto produto) {
		return manager.createNamedQuery("ProdutoFornecedor.porProduto", ProdutoFornecedor.class)
				.setParameter("produto", produto).getResultList();
	}

}