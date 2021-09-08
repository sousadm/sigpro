package com.sousa.sigpro.repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;

import com.sousa.sigpro.model.Categoria;
import com.sousa.sigpro.model.Composto;
import com.sousa.sigpro.model.Funcao;
import com.sousa.sigpro.model.Produto;
import com.sousa.sigpro.model.ProdutoUnidade;
import com.sousa.sigpro.model.tipo.TipoProduto;
import com.sousa.sigpro.model.tipo.TipoReposicao;
import com.sousa.sigpro.model.tipo.TipoUnidade;
import com.sousa.sigpro.repository.filter.ProdutoFilter;
import com.sousa.sigpro.security.Seguranca;
import com.sousa.sigpro.service.NegocioException;
import com.sousa.sigpro.util.Suporte;
import com.sousa.sigpro.util.jpa.Transactional;

public class Produtos implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager manager;
	@Inject
	private Seguranca seguranca;
	@Inject
	private Categorias categorias;

	public ProdutoUnidade produtoUnidadePorId(Long id) {
		return manager.find(ProdutoUnidade.class, id);
	}

	public Produto porId(Long id) {
		Produto produto = manager.find(Produto.class, id);
		produto.setUnidades(produtoUnidades(produto));
		return produto;
	}

	public Composto compostoPorId(Long id) {
		return manager.find(Composto.class, id);
	}

	public List<ProdutoUnidade> produtoUnidades(Produto produto) {
		String condicao = "SELECT c FROM ProdutoUnidade c WHERE c.produto.id = " + produto.getId();
		List<ProdutoUnidade> lst = manager.createQuery(condicao, ProdutoUnidade.class).getResultList();
		if (lst == null || lst.size() == 0) {
			lst = new ArrayList<>();
			lst.add(new ProdutoUnidade(produto));
		}
		return lst;
	}

	public ProdutoUnidade produtoUnidade(Produto produto, TipoUnidade unidade) {
		StringBuilder condicao = new StringBuilder();
		condicao.append("SELECT c FROM ProdutoUnidade c ");
		condicao.append("WHERE c.produto.id = " + produto.getId());
		condicao.append(" and c.unidade = " + Suporte.getQuotedStr(unidade.getCodigo()));
		try {
			return manager.createQuery(condicao.toString(), ProdutoUnidade.class).getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}

	public List<Composto> compostosPorProduto(Produto produto) {
		String condicao = "SELECT c FROM Composto c WHERE c.produto.id = " + produto.getId();
		return manager.createQuery(condicao, Composto.class).getResultList();
	}

	public List<Produto> componentesPorProduto(Produto produto) {
		String condicao = "SELECT c.componente FROM Composto c WHERE c.produto.id = " + produto.getId();
		return manager.createQuery(condicao, Produto.class).getResultList();
	}

	public List<Funcao> funcoesPorProduto(Produto produto) {
		String condicao = "SELECT c FROM Funcao c WHERE c.produto.id = " + produto.getId();
		return manager.createQuery(condicao, Funcao.class).getResultList();
	}

	@Transactional
	public void remover(Produto produto) {
		try {
			produto = porId(produto.getId());
			manager.remove(produto);
			manager.flush();
		} catch (PersistenceException e) {
			throw new NegocioException("registro não pode ser excluído.");
		} catch (Exception e) {
			throw new NegocioException(e.getMessage());
		}
	}

	public double valorAtivoPatrimonial() {
		double valor = 0;
		try {
			String ativos = categorias.ativos();
			String condicao = "select sum(precoCompra * quantidadeEstoque) from Produto p where p.categoria in ("
					+ ativos + ") ";
			Query query = manager.createQuery(condicao, Double.class);
			valor = (Double) query.getSingleResult();
			return valor;
		} catch (Exception e) {
			return valor;
		}

	}

	public Produto porSku(String sku) {
		try {
			return manager.createQuery("from Produto where upper(sku) = :sku", Produto.class)
					.setParameter("sku", sku.toUpperCase()).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public Produto porCodigoBarra(String valor) {
		try {
			return manager.createQuery("from Produto where upper(codigoBARRA) = :codigo", Produto.class)
					.setParameter("codigo", valor.toUpperCase()).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public Produto porSkuOuCodigoBarra(String codigo) {
		try {
			List<Produto> lst = manager.createNamedQuery("Produto.porSkuOuCodbarra", Produto.class)
					.setParameter("codigo", codigo).setParameter("empresa", seguranca.getPessoaLogadoOrigem())
					.getResultList();
			if (lst.size() > 0)
				return lst.get(0);
			else
				return null;
		} catch (NoResultException e) {
			return null;
		}
	}

	public List<Produto> porNome(String nome) {
		try {
			return manager.createNamedQuery("Produto.porNome", Produto.class)
					.setParameter("nome", nome.toUpperCase() + "%")
					.setParameter("origem", seguranca.getPessoaLogado().getOrigem()).getResultList();
		} catch (Exception e) {
			return new ArrayList<Produto>();
		}

	}

	public List<Produto> ativoPatrimonial() {
		return manager.createNamedQuery("Produto.patrimonio", Produto.class).getResultList();
	}

	private String consulta(ProdutoFilter filtro) {

		StringBuilder condicao = new StringBuilder();

		if (StringUtils.isNotBlank(filtro.getSku()))
			condicao.append(" and p.sku = '" + filtro.getSku() + "' ");

		if (StringUtils.isNotBlank(filtro.getNome()))
			condicao.append(" and Upper(p.nome) like '%" + filtro.getNome().toUpperCase() + "%' ");

		if (filtro.isComProduto() && !filtro.isComServico()) {
			condicao.append(" and p.categoria.categoriaPai.tipoProduto <> '" + TipoProduto.SERVICO + "' ");
		} else if (!filtro.isComProduto() && filtro.isComServico()) {
			condicao.append(" and p.categoria.categoriaPai.tipoProduto = '" + TipoProduto.SERVICO + "' ");
		}

		if (filtro.getCategorias().length > 0) {
			String condicaoCategoria = "";
			for (Categoria categoria : filtro.getCategorias()) {
				if (condicaoCategoria != "")
					condicaoCategoria = condicaoCategoria + ", ";
				condicaoCategoria = condicaoCategoria + categoria.getId();
			}
			if (condicaoCategoria != "") {
				condicao.append(" and p.categoria.categoriaPai in (" + condicaoCategoria + ") ");
			}
		}

		if (filtro.getSubcategorias().length > 0) {
			String condicaoSubCategoria = "";
			for (Categoria categoria : filtro.getSubcategorias()) {
				if (condicaoSubCategoria != "")
					condicaoSubCategoria = condicaoSubCategoria + ", ";
				condicaoSubCategoria = condicaoSubCategoria + categoria.getId();
			}
			if (condicaoSubCategoria != "") {
				condicao.append(" and p.categoria in (" + condicaoSubCategoria + ") ");
			}
		}

		if (filtro.getTipoReposicao() != null)
			if (filtro.getTipoReposicao().equals(TipoReposicao.NA)) {
				condicao.append(" AND p.tipoReposicao IS NULL ");
			} else
				condicao.append(" AND p.tipoReposicao = " + Suporte.getQuotedStr(filtro.getTipoReposicao().name()));

		if (filtro.getAtivo() != null) {
			condicao.append(" AND p.ativo = " + filtro.getAtivo());
		}

		if (filtro.getCondicaoExtra().length() > 0)
			condicao.append(" and " + filtro.getCondicaoExtra());

		condicao.append(" and (p.categoria.pessoa = " + seguranca.getPessoaLogadoOrigem().getId());
		condicao.append("  		or p.categoria.pessoa.origem.unidadeProdutiva.permiteCompartilhaProduto = true) ");

		return condicao.toString();
	}

	public int quantidadeFiltrados(ProdutoFilter filtro) {
		StringBuilder condicao = new StringBuilder();
		if (filtro.isComComposto()) {
			condicao.append("select count(*) from Produto p join p.categoria c ");
			condicao.append("where exists (select c from Composto c where c.produto = p ) ");
		} else {
			condicao.append("select count(*) from Produto p join p.categoria c ");
			condicao.append("where 1 = 1 ");
		}
		condicao.append(consulta(filtro));
		return ((Number) manager.createQuery(condicao.toString()).getSingleResult()).intValue();
	}

	@Transactional
	public Produto guardar(Produto produto) {

		Produto produtoExistente = porSku(produto.getSku());

		if (produtoExistente != null && !produtoExistente.equals(produto))
			throw new NegocioException("Já existe um produto com o SKU informado.");

		if (produto.getUnidades() != null && produto.getUnidades().size() == 0) {

			if (produto.getUnidade() == null)
				throw new NegocioException("necessário pelo menos uma unidade de medida");

			produto.getUnidades().add(new ProdutoUnidade(produto));
		}

		if (produto.getUnidade() == null)
			produto.setUnidade(produto.getUnidades().get(0).getUnidade());

		if (produto.getCategoria().getCategoriaPai().getTipoProduto() != TipoProduto.SERVICO
				&& produto.getListaComposto() != null && produto.getListaComposto().size() > 0) {
			for (Composto composto : produto.getListaComposto()) {
				if (composto.getComponente().getUnidade().equals(TipoUnidade.UND)
						&& !Suporte.numeroEInteiro(composto.getQuantidade())) {
					throw new NegocioException(
							"O composto " + composto.getComponente().getNome() + " não pode ter conteúdo fracionado");
				}
			}

			double valor = 0;
			for (Composto composto : produto.getListaComposto()) {
				valor = valor + (composto.getComponente().getPrecoCusto() * composto.getQuantidade());
			}

			produto.setPrecoCompra(valor);

		}

		if (produto.getId() == null)
			produto.setQuantidadeEstoque(produto.getEstoqueInicial());

		if (produto.getPrecoMedio() == 0)
			produto.setPrecoMedio(produto.getCusto().getPrecoCusto());

		return manager.merge(produto);
	}

	@SuppressWarnings("unchecked")
	public List<Produto> lista(ProdutoFilter filtro) {

		StringBuilder condicao = new StringBuilder();
		if (filtro.isComComposto()) {
			condicao.append("select p from Produto p join p.categoria c ");
			condicao.append("where exists (select c from Composto c where c.produto = p ) ");
		} else {
			condicao.append("select p from Produto p join p.categoria c ");
			condicao.append("where 1 = 1 ");
		}

		condicao.append(consulta(filtro));
		if (filtro.getPropriedadeOrdenacao() != null) {
			condicao.append(" ORDER BY p." + filtro.getPropriedadeOrdenacao() + (filtro.isAscendente() ? "" : " desc"));
		}

		Query query = manager.createQuery(condicao.toString(), Produto.class);
		if (filtro.getPrimeiroRegistro() != null) {
			query.setFirstResult(filtro.getPrimeiroRegistro().intValue());
			query.setMaxResults(filtro.getQuantidadeRegistros().intValue());
		}

		return query.getResultList();
	}

}