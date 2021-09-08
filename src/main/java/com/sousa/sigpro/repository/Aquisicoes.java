package com.sousa.sigpro.repository;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.sousa.sigpro.model.Aquisicao;
import com.sousa.sigpro.model.AquisicaoItem;
import com.sousa.sigpro.model.Fornecedor;
import com.sousa.sigpro.model.Pessoa;
import com.sousa.sigpro.model.Produto;
import com.sousa.sigpro.model.ProdutoFornecedor;
import com.sousa.sigpro.model.ProdutoFornecedorSequencia;
import com.sousa.sigpro.model.Titulo;
import com.sousa.sigpro.repository.filter.CondicaoFilter;
import com.sousa.sigpro.security.Seguranca;
import com.sousa.sigpro.service.NegocioException;
import com.sousa.sigpro.util.Suporte;
import com.sousa.sigpro.util.jpa.Transactional;

public class Aquisicoes implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager manager;
	@Inject
	private Seguranca seguranca;
	@Inject
	private Produtos produtos;
	@Inject
	private Titulos titulos;
	@Inject
	private ProdutoFornecedores produtoFornecedores;

	@Transactional
	public void Excluir(Aquisicao aquisicao) {
		aquisicao = porId(aquisicao.getId());

		List<Titulo> lst = titulos.lista(aquisicao);
		for (Titulo titulo : lst) {
			if (titulo.getDataBaixa() != null) {
				throw new NegocioException("Existe titulo com baixa confirmada para esta aquisição!");
			} else {
				manager.remove(titulo);
			}
		}

		if (aquisicao.getDataCancelamento() != null)
			throw new NegocioException("Registro com status de cancelado");
		manager.remove(aquisicao);
	}

	@Transactional
	public void associaProdutoFornecedor(Aquisicao aquisicao) {
		if (aquisicao.getItems() != null)
			for (AquisicaoItem item : aquisicao.getItems()) {
				ProdutoFornecedor prodForne = produtoFornecedores.porProdutoFornecedor(item.getProduto(),
						aquisicao.getFornecedor());
				if (prodForne == null) {
					prodForne = new ProdutoFornecedor();
					prodForne
							.setSequencia(new ProdutoFornecedorSequencia(item.getProduto(), aquisicao.getFornecedor()));
				}
				prodForne.setAquisicaoItem(item);
				prodForne = manager.merge(prodForne);
				manager.flush();
			}
	}

	@Transactional
	public Aquisicao guardar(Aquisicao aquisicao, Pessoa fornecedor) {

		if (aquisicao.getDataCancelamento() != null)
			throw new NegocioException("documento está cancelado");

		if (aquisicao.getDataEncerramento() != null)
			throw new NegocioException("documento já está confirmado");

		if (aquisicao.getItems().isEmpty())
			throw new NegocioException("Sem itens para gravar");

		if (aquisicao.getDataCancelamento() != null)
			throw new NegocioException("Registro com status de cancelado");

		if (aquisicao.getEmpresa() == null)
			aquisicao.setEmpresa(seguranca.getPessoaLogadoOrigem());

		if (aquisicao.getDataCadastro() == null)
			aquisicao.setDataCadastro(new Date());

		if (aquisicao.getFornecedor() == null)
			throw new NegocioException("Fornecedor indefinido");

		double valor = 0;
		for (Titulo pgto : aquisicao.getTitulos())
			valor = valor + pgto.getValor();

		for (AquisicaoItem item : aquisicao.getItems()) {
			Produto produto = item.getProduto();
			produto = manager.merge(produto);
			manager.flush();
			item.setProduto(produto);

		}
		return manager.merge(aquisicao);
	}

	public AquisicaoItem itemFornecedorCodigoProduto(Fornecedor fornecedor, String codigo) {
		try {
			return manager.createNamedQuery("AquisicaoItem.porProdutoFornecedor", AquisicaoItem.class)
					.setParameter("codigo", codigo).setParameter("fornecedor", fornecedor).getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}

	@Transactional
	public Aquisicao encerrar(Aquisicao aquisicao) {
		aquisicao = porId(aquisicao.getId());
		for (Titulo titulo : aquisicao.getTitulos()) {
			titulo.setSaldo(titulo.getValor());
			manager.merge(titulo);
		}

		double estoque = 0;
		double valorTotal = 0;
		double precoMedio = 0;

		for (AquisicaoItem item : aquisicao.getItems()) {
			Produto produto = produtos.porId(item.getProduto().getId());

			if (aquisicao.isTemNotaFiscal()) {
				estoque = produto.getQuantidadeEstoqueContabil() + item.getQuantidade();
				valorTotal = (produto.getQuantidadeEstoqueContabil() * produto.getPrecoMedio())
						+ (item.getQuantidade() * item.getValorCustoUnitario());
				if (estoque > 0) {
					precoMedio = Suporte.arredondaValor(valorTotal / estoque);
				} else {
					precoMedio = item.getValorCustoUnitario();
				}
			}

			if (produto.getDataCadastro() == null) {
				produto.setDataCadastro(new Date());
				produto.setPrecoMedio(item.getValorUnitario());
				produto.setEstoqueInicial(item.getQuantidade());
				produto.setQuantidadeEstoque(item.getQuantidade());
				produto.setQuantidadeEstoqueContabil(item.getQuantidade());

			} else {
				produto.setPrecoMedio(precoMedio);
				produto.adiciona(item.getQuantidade());
			}

			manager.merge(produto);

		}

		aquisicao.setDataEncerramento(new Date());
		aquisicao.getNota().setDataConfirmacao(aquisicao.getDataEncerramento());

		return manager.merge(aquisicao);

	}

	public Aquisicao porChaveNFe(String chave) {
		try {
			String query = "select t from Aquisicao t where t.nota.chave = " + Suporte.getQuotedStr(chave);
			List<Aquisicao> lst = manager.createQuery(query, Aquisicao.class).getResultList();
			if (lst != null && lst.size() > 0)
				return lst.get(0);
			else
				return null;
		} catch (Exception e) {
			return null;
		}
	}

	private String consulta(CondicaoFilter filtro) {
		StringBuilder condicao = new StringBuilder();
		condicao.append("where 1 = 1 ");
		// a.empresa = + seguranca.getPessoaLogadoOrigem().getId());
		if (!filtro.getNome().equals(""))
			condicao.append(" and Upper(a.fornecedor.nome) like '%" + filtro.getNome().toUpperCase() + "%' ");
		if (Suporte.dataIsValida(filtro.getEmissaoInicial()))
			condicao.append(" and cast(dataCadastro as date) >= '" + filtro.getEmissaoInicial() + "' ");
		if (Suporte.dataIsValida(filtro.getEmissaoFinal()))
			condicao.append(" and cast(dataCadastro as date) <= '" + filtro.getEmissaoFinal() + "' ");
		return condicao.toString();
	}

	public int quantidadeFiltrados(CondicaoFilter filtro) {
		StringBuilder condicao = new StringBuilder();
		condicao.append("select count(*) from Aquisicao a ");
		condicao.append(consulta(filtro));
		return ((Number) manager.createQuery(condicao.toString()).getSingleResult()).intValue();
	}

	@SuppressWarnings("unchecked")
	public List<Aquisicao> lista(CondicaoFilter filtro) {

		StringBuilder condicao = new StringBuilder();
		condicao.append("select a from Aquisicao a ");
		condicao.append(consulta(filtro));
		if (filtro.getSortField() != null) {
			condicao.append(" ORDER BY " + filtro.getSortField() + (filtro.isAscendente() ? "" : " desc"));
		} else if (filtro.getOrdem() != null) {
			condicao.append(" ORDER BY " + filtro.getOrdem());
		} else {
			condicao.append(" ORDER BY a.id desc");
		}

		Query query = manager.createQuery(condicao.toString(), Aquisicao.class);
		if (filtro.getPrimeiroRegistro() != null) {
			query.setFirstResult(filtro.getPrimeiroRegistro().intValue());
			query.setMaxResults(filtro.getQuantidadeRegistros().intValue());
		}

		return query.getResultList();
	}

	public Aquisicao porId(Long id) {
		return manager.find(Aquisicao.class, id);
	}

	@Transactional
	public Aquisicao cancelar(Aquisicao aquisicao) {
		aquisicao = porId(aquisicao.getId());
		List<Titulo> lst = titulos.lista(aquisicao);
		if (lst != null && lst.size() > 0) {
			for (Titulo titulo : lst) {
				if (titulo.isExistePagamento() || titulo.getDataBaixa() != null) {
					throw new NegocioException("Existe titulo com baixa confirmada para esta aquisição!");
				} else {
					titulo.setDataCancelamento(new Date());
					titulo.setSaldo(0);
					manager.merge(titulo);
				}
			}
		} 
		
		for (AquisicaoItem item : aquisicao.getItems()) {
			Produto produto = produtos.porId(item.getProduto().getId());
			produto.baixarEstoque(item.getQuantidade());
			manager.merge(produto);
		}
		
		aquisicao.setDataCancelamento(new Date());
		return manager.merge(aquisicao);

	}

}