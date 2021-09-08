package com.sousa.sigpro.repository;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.sousa.sigpro.model.Endereco;
import com.sousa.sigpro.model.Expedicao;
import com.sousa.sigpro.model.Municipio;
import com.sousa.sigpro.model.NotaFiscal;
import com.sousa.sigpro.model.NotaFiscalItem;
import com.sousa.sigpro.model.NotaFiscalPgto;
import com.sousa.sigpro.model.Pessoa;
import com.sousa.sigpro.model.ProdutoUnidade;
import com.sousa.sigpro.model.tipo.TipoEndereco;
import com.sousa.sigpro.model.tipo.TipoFocusStatus;
import com.sousa.sigpro.model.tipo.TipoModeloFiscal;
import com.sousa.sigpro.model.tipo.TipoProduto;
import com.sousa.sigpro.repository.filter.CondicaoFilter;
import com.sousa.sigpro.security.Seguranca;
import com.sousa.sigpro.service.NegocioException;
import com.sousa.sigpro.util.Suporte;
import com.sousa.sigpro.util.jpa.Transactional;

public class NotaFiscais implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Seguranca seguranca;
	@Inject
	private EntityManager manager;
	@Inject
	private Produtos produtos;
	@Inject
	private Pessoas pessoas;

	public NotaFiscal porId(Long id) {

		NotaFiscal nota = manager.find(NotaFiscal.class, id);

		List<NotaFiscalItem> items = items(nota);
		nota.setItems(items);

		List<NotaFiscalPgto> pgtos = pagamentos(nota);
		nota.setPgtos(pgtos);

		return nota;
	}

	public List<NotaFiscalPgto> pagamentos(NotaFiscal nota) {
		return manager.createNamedQuery("NotaFiscal.pagamentos", NotaFiscalPgto.class).setParameter("nota", nota)
				.getResultList();
	}

	public List<NotaFiscalItem> items(NotaFiscal nota) {
		return manager.createNamedQuery("NotaFiscal.items", NotaFiscalItem.class).setParameter("nota", nota)
				.getResultList();
	}

	public List<NotaFiscalItem> items(Expedicao expedicao) {
		return manager.createNamedQuery("NotaFiscalItem.listaPorPedido", NotaFiscalItem.class)
				.setParameter("expedicao", expedicao).getResultList();
	}

	public List<NotaFiscal> ultimosDocumentoFiscal(Pessoa pessoa, TipoModeloFiscal modelo) {
		try {
			StringBuilder condicao = new StringBuilder();
			condicao.append("select n from NotaFiscal n WHERE 1 = 1");
			condicao.append(" and n.fiscal.modelo = " + modelo.ordinal());
			condicao.append("	and n.responsavel = " + pessoa.getId());
			return manager.createQuery(condicao.toString(), NotaFiscal.class).getResultList();
		} catch (Exception e) {
			return null;
		}
	}

	@Transactional
	public void removePagamento(NotaFiscalPgto nota) {
		manager.remove(nota);
	}

	@Transactional
	public NotaFiscalPgto guardar(NotaFiscalPgto item) {
		return manager.merge(item);
	}

	@Transactional
	public NotaFiscalItem guardar(NotaFiscalItem item) {
		return manager.merge(item);
	}

	@Transactional
	public void excuir(NotaFiscal nota) {

		if (nota.getFiscal().getNumero() != null)
			throw new NegocioException("nota fiscal não pode ser excluida");

		String condicao = "delete from NotaFiscalItem where nota = " + nota.getId();
		Query query = manager.createQuery(condicao);
		query.executeUpdate();

		condicao = "delete from NotaFiscal where id = " + nota.getId();
		query = manager.createQuery(condicao);
		query.executeUpdate();

	}

	private String consulta(TipoModeloFiscal modelo, CondicaoFilter filtro) {
		StringBuilder condicao = new StringBuilder();
		condicao.append("where 1 = 1 ");

		if (modelo != null)
			condicao.append(" and n.fiscal.modelo = " + modelo.ordinal());

		if (filtro.getOperacaoFiscal() != null)
			condicao.append(" and n.fiscal.operacao = " + filtro.getOperacaoFiscal().getId());

		if (filtro.getAmbienteFiscal() != null)
			condicao.append(" and n.fiscal.ambiente = " + filtro.getAmbienteFiscal().ordinal());

		if (filtro.getEmissaoInicial() != null)
			condicao.append(" and cast(n.fiscal.dataEmissao as date) >= '" + filtro.getEmissaoInicial() + "' ");

		if (filtro.getEmissaoFinal() != null)
			condicao.append(" and cast(n.fiscal.dataEmissao as date) <= '" + filtro.getEmissaoFinal() + "' ");

		if (filtro.getCancelamentoInicial() != null)
			condicao.append(
					" and cast(n.fiscal.dataCancelamento as date) >= '" + filtro.getCancelamentoInicial() + "' ");

		if (filtro.getCancelamentoFinal() != null)
			condicao.append(" and cast(n.fiscal.dataCancelamento as date) <= '" + filtro.getCancelamentoFinal() + "' ");

		if (Suporte.stringComValor(filtro.getNome()))
			condicao.append(
					" and upper(n.fiscal.cliente.nome) like UPPER('%" + filtro.getNome().toUpperCase() + "%') ");

		return condicao.toString();
	}

	@SuppressWarnings("unchecked")
	public List<NotaFiscal> lista(TipoModeloFiscal modelo, CondicaoFilter filtro) {

		StringBuilder condicao = new StringBuilder();
		condicao.append("select n from NotaFiscal n ");
		condicao.append(consulta(modelo, filtro));
		if (filtro.getSortField() != null) {
			condicao.append(" ORDER BY n." + filtro.getSortField() + (filtro.isAscendente() ? "" : " desc"));
		} else {
			condicao.append(" ORDER BY n.id desc ");
		}

		Query query = manager.createQuery(condicao.toString(), NotaFiscal.class);
		if (filtro.getPrimeiroRegistro() != null) {
			query.setFirstResult(filtro.getPrimeiroRegistro().intValue());
			query.setMaxResults(filtro.getQuantidadeRegistros().intValue());
		}

		return query.getResultList();
	}

	public int quantidadeFiltrados(TipoModeloFiscal modelo, CondicaoFilter filtro) {
		StringBuilder condicao = new StringBuilder();
		condicao.append("select count(*) from NotaFiscal n ");
		condicao.append(consulta(modelo, filtro));
		return ((Number) manager.createQuery(condicao.toString()).getSingleResult()).intValue();
	}

	@Transactional
	public NotaFiscal guardar(NotaFiscal nota, Boolean movimenta) {

		if (nota.getFiscal().getCliente() == null || nota.getFiscal().getCliente().getId() == null)
			throw new NegocioException("Sem cliente definido");

		if (nota.getFiscal().getModelo().equals(TipoModeloFiscal.NFE) && nota.getItems().isEmpty())
			throw new NegocioException("Sem itens para gravar");

		if (nota.getFiscal().getStatus() == null)
			nota.getFiscal().setStatus(TipoFocusStatus.PROCESSANDO);

		nota.setEmpresa(seguranca.getPessoaLogadoOrigem());
		nota.setResponsavel(seguranca.getPessoaLogado().getUsuario());
		if (nota.getDataCadastro() == null)
			nota.setDataCadastro(new Date());

		if (nota.isNotaTipoNFSe()) {
			nota.getFiscal().getCliente().setRetencaoIss(nota.getIssqn().isRetencaoIss());
			manager.merge(nota.getFiscal().getCliente());
			manager.flush();
		}

		if (nota.getPgtos() != null && nota.getPgtos().size() > 0)
			for (NotaFiscalPgto pgto : nota.getPgtos())
				pgto.setNota(nota);

		if (movimenta) {
			int fator = nota.getFiscal().getDataCancelamento() != null || nota.getFiscal().getOperacao().isDevolucao()
					? 1
					: -1;
			for (NotaFiscalItem item : nota.getItems()) {

				ProdutoUnidade prodUnd = produtos.produtoUnidade(item.getProd(), item.getUnidade());
				double fatorUnd = prodUnd == null ? 1 : prodUnd.getFatorEstoque();

				if (item.getExpedicaoItem() == null
						&& item.getProd().getCategoria().getCategoriaPai().getTipoProduto() != TipoProduto.SERVICO)
					item.getProd().adicionarEstoque(item.getQuantidade() * fator * fatorUnd);
			}
		}

		if (nota.getItems() != null && nota.getItems().size() > 0)
			for (NotaFiscalItem item : nota.getItems()) {

				if (item.getIcms() != null && item.getIcms().getAliquotaICMS() > 0) {
					item.getIcms().setBaseICMS(item.getValorProduto());
					item.getIcms().setValorICMS(
							Suporte.arredondaValor(item.getValorProduto() * item.getIcms().getAliquotaICMS() / 100, 2));
				}

				if (item.getPis() != null && item.getPis().getAliquotaPIS() > 0) {
					item.getPis().setBasePIS(item.getValorProduto());
					item.getPis().setValorPIS(Suporte
							.arredondaValor(item.getPis().getValorPIS() * item.getPis().getAliquotaPIS() / 100, 2));
				}

				if (item.getCofins() != null && item.getCofins().getAliquotaCofins() > 0) {
					item.getCofins().setBaseCofins(item.getValorProduto());
					item.getCofins().setValorCofins(Suporte.arredondaValor(
							item.getCofins().getValorCofins() * item.getCofins().getAliquotaCofins() / 100, 2));
				}
			}

		if (nota.getFiscal().getEnderecoEntrega() == null
				|| nota.getFiscal().getEnderecoEntrega().getMunicipio() == null)
			throw new NegocioException("endereço de entrega indefinido");

		Municipio municipio = nota.getFiscal().getEnderecoEntrega().getMunicipio();
		municipio = manager.merge(municipio);
		manager.flush();

		Endereco endereco = nota.getFiscal().getEnderecoEntrega();
		endereco.setMunicipio(municipio);
		endereco.setCep(Suporte.onlyNumbers(endereco.getCep()));
		if (endereco.getId() == null) {
			Pessoa pessoa = pessoas.cliente(nota.getFiscal().getCliente());
			endereco.setPessoa(pessoa);
			endereco.setTipoEndereco(TipoEndereco.ENTREGA);
		}

		if (endereco.getMunicipio() == null || endereco.getMunicipio().getId() == null)
			throw new NegocioException("municipio indefinido para o clietne");

		endereco = manager.merge(endereco);
		manager.flush();

		nota.getFiscal().setEnderecoEntrega(endereco);

		return manager.merge(nota);

	}

}