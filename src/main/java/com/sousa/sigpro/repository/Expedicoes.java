package com.sousa.sigpro.repository;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jrimum.domkee.financeiro.banco.febraban.TipoDeTitulo;

import com.sousa.sigpro.model.Agenda;
import com.sousa.sigpro.model.BoletoDigital;
import com.sousa.sigpro.model.Categoria;
import com.sousa.sigpro.model.CentroDeCusto;
import com.sousa.sigpro.model.Cliente;
import com.sousa.sigpro.model.Contato;
import com.sousa.sigpro.model.CurvaFaturamentoProduto;
import com.sousa.sigpro.model.Expedicao;
import com.sousa.sigpro.model.ExpedicaoItem;
import com.sousa.sigpro.model.NotaFiscalItem;
import com.sousa.sigpro.model.OrdemServico;
import com.sousa.sigpro.model.Pessoa;
import com.sousa.sigpro.model.ProdutoUnidade;
import com.sousa.sigpro.model.Titulo;
import com.sousa.sigpro.model.tipo.TipoCentroCusto;
import com.sousa.sigpro.model.tipo.TipoEspecialidade;
import com.sousa.sigpro.model.tipo.TipoExpedicao;
import com.sousa.sigpro.model.tipo.TipoMes;
import com.sousa.sigpro.model.tipo.TipoPagamento;
import com.sousa.sigpro.model.tipo.TipoProduto;
import com.sousa.sigpro.model.tipo.TipoReposicao;
import com.sousa.sigpro.model.tipo.TipoTituloOrigem;
import com.sousa.sigpro.repository.filter.CondicaoFilter;
import com.sousa.sigpro.security.Seguranca;
import com.sousa.sigpro.service.NegocioException;
import com.sousa.sigpro.util.Suporte;
import com.sousa.sigpro.util.SuporteData;
import com.sousa.sigpro.util.jpa.Transactional;

public class Expedicoes implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager manager;
	@Inject
	private Seguranca seguranca;
	@Inject
	private Agendas agendas;
	@Inject
	private Pessoas pessoas;
	@Inject
	private Financeiros financeiros;
	@Inject
	private CentrosDeCusto centrosDeCusto;
	@Inject
	private Produtos produtos;
	@Inject
	private Contatos contatos;
	@Inject
	private Titulos titulos;
	@Inject
	private NotaFiscais notas;

	public Expedicao porId(Long id) {
		return manager.find(Expedicao.class, id);
	}

	public ExpedicaoItem itemPorId(Long id) {
		return manager.find(ExpedicaoItem.class, id);
	}

	public List<ExpedicaoItem> itemsPorServico(OrdemServico ordem) {
		return manager.createNamedQuery("ExpedicaoItem.listaPorServico", ExpedicaoItem.class)
				.setParameter("ordem", ordem).getResultList();
	}

	public List<ExpedicaoItem> listaItemsNaoCancelados(Expedicao expedicao) {
		return manager.createNamedQuery("ExpedicaoItem.listaExpedicaoNaoCancelada", ExpedicaoItem.class)
				.setParameter("expedicao", expedicao).getResultList();
	}

	public List<ExpedicaoItem> listaServicoLivre(Expedicao expedicao) {
		return manager.createNamedQuery("ExpedicaoItem.servicoLivre", ExpedicaoItem.class)
				.setParameter("expedicao", expedicao).setParameter("tipo", TipoProduto.SERVICO).getResultList();
	}

	public List<ExpedicaoItem> itemsPorExpedicao(Expedicao expedicao) {
		return manager.createNamedQuery("ExpedicaoItem.listaExpedicao", ExpedicaoItem.class)
				.setParameter("expedicao", expedicao).getResultList();
	}

	public boolean existeExpedicaoComTituloPago(Expedicao expedicao) {
		List<Titulo> lst = new ArrayList<>();
		lst = manager.createNamedQuery("Titulo.temExpedicaoBaixa", Titulo.class).setParameter("expedicao", expedicao)
				.getResultList();
		return lst != null && !lst.isEmpty();
	}

	public List<Expedicao> filtrados() {
		return manager.createNamedQuery("Expedicao.lista", Expedicao.class)
				.setParameter("origem", seguranca.getPessoaLogadoOrigem()).getResultList();
	}

	public boolean temModuloEspecialidade(TipoEspecialidade especialidade) {
		List<Categoria> lst = manager
				.createQuery("select c from Categoria c where c.pessoa = :empresa and c.especialidade = :especialidade",
						Categoria.class)
				.setParameter("empresa", seguranca.getPessoaLogadoOrigem()).setParameter("especialidade", especialidade)
				.getResultList();
		return lst.size() > 0;
	}

	@Transactional
	public ExpedicaoItem guardarExpedicaoItem(ExpedicaoItem expedicaoItem) {
		return manager.merge(expedicaoItem);
	}

	@SuppressWarnings("unchecked")
	public Collection<CurvaFaturamentoProduto> listaCurvaFaturamentoCliente(CondicaoFilter filtro) {

		String condicao = "select c.nome, sum(e.valorTotal) " + "from expedicao e JOIN e.cliente c WHERE 1 = 1 ";
		condicao += consulta(filtro);
		condicao += " GROUP BY 1  ";
		condicao += " ORDER BY 2 DESC";

		List<CurvaFaturamentoProduto> lista = new ArrayList<>();

		List<Object[]> lstObj = manager.createQuery(condicao).getResultList();
		for (Object[] obj : lstObj) {
			if (obj != null) {
				CurvaFaturamentoProduto cfp = new CurvaFaturamentoProduto();
				cfp.setNome(obj[0].toString());
				cfp.setValor(Double.parseDouble(obj[1].toString()));
				lista.add(cfp);
			}
		}

		return lista;
	}

	@SuppressWarnings("unchecked")
	public Collection<CurvaFaturamentoProduto> listaCurvaFaturamentoProduto(CondicaoFilter filtro) {

		String condicao = "select p.id, g.categoriaPai.tipoProduto.descricao, p.sku, p.nome,"
				+ " sum(i.quantidade), sum(i.quantidade * (i.unitario - i.desconto)) "
				+ "from ExpedicaoItem i JOIN i.expedicao e JOIN e.vendedor v "
				+ "JOIN i.produto p JOIN e.cliente c JOIN p.categoria g WHERE 1 = 1 ";
		condicao += consulta(filtro);
		condicao += " GROUP BY 1, 2, 3 ";
		condicao += " ORDER BY 2, 6 DESC";

		List<CurvaFaturamentoProduto> lista = new ArrayList<>();

		List<Object[]> lstObj = manager.createQuery(condicao).getResultList();
		for (Object[] obj : lstObj) {
			if (obj != null) {
				CurvaFaturamentoProduto cfp = new CurvaFaturamentoProduto();
				cfp.setTipoProduto(obj[1].toString());
				cfp.setSku(obj[2].toString());
				cfp.setNome(obj[3].toString());
				cfp.setQuantidade(Double.parseDouble(obj[4].toString()));
				cfp.setValor(Double.parseDouble(obj[5].toString()));
				lista.add(cfp);
			}
		}

		return lista;

	}

	public Map<Integer, Double> graficoFaturamentoNoMes(int ano, TipoMes mes) {

		Map<Integer, Double> mapa = new TreeMap<>();

		Calendar atual = Calendar.getInstance();
		atual.set(Calendar.YEAR, ano);
		atual.set(Calendar.MONTH, mes.ordinal());
		atual.set(Calendar.DAY_OF_MONTH, 1);

		for (int dia = SuporteData.primeiroDiaDoMes(atual.getTime()); dia <= SuporteData
				.ultimoDiaDoMes(atual.getTime()); dia++) {
			mapa.put(dia, Double.valueOf(0));
		}

		String condicao = "select EXTRACT(DAY FROM e.dataEmissao), sum(e.valorTotal) from expedicao e "
				+ "where dataEmissao is not null and dataCancelamento is null "
				+ " AND EXTRACT(YEAR FROM e.dataEmissao) = :ano " + " AND EXTRACT(MONTH FROM e.dataEmissao) = :mes "
				+ "group by 1 order by 1";

		@SuppressWarnings("unchecked")
		List<Object[]> lstObj = manager.createQuery(condicao).setParameter("ano", ano)
				.setParameter("mes", mes.ordinal() + 1).getResultList();
		for (Object[] obj : lstObj) {
			if (obj != null) {
				mapa.put((Integer) obj[0], (Double) obj[1]);
			}
		}

		return mapa;
	}

	public double valorFaturado(Date inicio, Date termino) {
		String condicao = "select sum(e.valorTotal) from expedicao e where cast(e.dataEmissao as date) "
				+ "between :inicio and :termino and e.empresa = :empresa";
		Query query = manager.createQuery(condicao, Double.class)
				.setParameter("empresa", seguranca.getPessoaLogadoOrigem()).setParameter("inicio", inicio)
				.setParameter("termino", termino);
		return (Double) query.getSingleResult();
	}

	private String consulta(CondicaoFilter filtro) {

		StringBuilder condicao = new StringBuilder();

		if (filtro.getNumeroDe() != null)
			condicao.append(" and e.id >= " + filtro.getNumeroDe());

		if (filtro.getNumeroAte() != null)
			condicao.append(" and e.id <= " + filtro.getNumeroAte());

		if (filtro.getNomeCliente() != null && filtro.getNomeCliente().length() > 0)
			condicao.append(" and Upper(c.nome) like '%" + filtro.getNomeCliente().toUpperCase() + "%' ");

		if (filtro.getExpedicaoTipos() != null && filtro.getExpedicaoTipos().length > 0) {
			String condTipo = "";
			for (TipoExpedicao tipo : filtro.getExpedicaoTipos()) {
				if (condTipo != "")
					condTipo += ", ";
				condTipo += "'" + tipo + "'";
			}
			if (condTipo != "")
				condicao.append(" and e.tipo in (" + condTipo + ")");
		}

		condicao.append(" and e.dataCancelamento is null ");

		if (filtro.getInicio() != null)
			condicao.append(" and cast(e.dataCadastro as date) >= '" + filtro.getInicio() + "' ");

		if (filtro.getTermino() != null)
			condicao.append(" and cast(e.dataCadastro as date) <= '" + filtro.getTermino() + "' ");

		if (filtro.getEmissaoInicial() != null)
			condicao.append(" and cast(e.dataEmissao as date) >= '" + filtro.getEmissaoInicial() + "' ");

		if (filtro.getEmissaoFinal() != null)
			condicao.append(" and cast(e.dataEmissao as date) <= '" + filtro.getEmissaoFinal() + "' ");

		if (!seguranca.isUsuarioConvidado()) {
			condicao.append(" and e.empresa = " + seguranca.getPessoaLogado().getOrigem().getId());
			if (filtro.getCliente() != null && filtro.getCliente().isExiste())
				condicao.append(" and e.cliente = " + filtro.getCliente().getId());
		} else
			condicao.append(" and e.cliente = " + seguranca.getPessoaLogado().getCliente().getId());

		if (filtro.getVeiculo() != null && filtro.getVeiculo().isExiste()) {
			condicao.append(
					" and exists (select 1 from ExpedicaoItem i join i.servico s where i.expedicao = e.id and s.veiculo = "
							+ filtro.getVeiculo().getId() + " ) ");
		}

		if (filtro.getSql() != null && filtro.getSql().length() > 0)
			condicao.append(" and " + filtro.getSql() + " ");

		return condicao.toString();
	}

	@SuppressWarnings("unchecked")
	public List<Expedicao> filtrados(CondicaoFilter filtro) {
		StringBuilder condicao = new StringBuilder();
		condicao.append("select e from expedicao e join e.cliente c join e.vendedor v where 1 = 1 ");
		condicao.append(consulta(filtro));

		if (filtro.getSortField() == null) {
			condicao.append(" ORDER BY e.dataCadastro desc ");
		} else {
			condicao.append(" ORDER BY e." + filtro.getSortField() + (filtro.isAscendente() ? "" : " desc"));
		}

		Query query = manager.createQuery(condicao.toString(), Expedicao.class);
		if (filtro.getPrimeiroRegistro() != null) {
			query.setFirstResult(filtro.getPrimeiroRegistro().intValue());
			query.setMaxResults(filtro.getQuantidadeRegistros().intValue());
		}

		return query.getResultList();
	}

	public int quantidadeFiltrados(CondicaoFilter filtro) {
		StringBuilder condicao = new StringBuilder();
		condicao.append("select count(*) from expedicao e join e.cliente c join e.vendedor v where 1 = 1 ");
		condicao.append(consulta(filtro));
		return ((Number) manager.createQuery(condicao.toString()).getSingleResult()).intValue();
	}

	public void baixarItensEstoqueExpedicao(Expedicao expedicao) {

		expedicao = porId(expedicao.getId());

		for (ExpedicaoItem item : expedicao.getItems()) {
			if (item.getProduto().getCategoria().getCategoriaPai().getTipoProduto() != TipoProduto.SERVICO) {

				ProdutoUnidade prodUnd = produtos.produtoUnidade(item.getProduto(), item.getUnidade());
				double fator = prodUnd == null ? 1 : prodUnd.getFatorEstoque();
				double quantidade = fator * item.getQuantidade();

				if (quantidade <= item.getProduto().getQuantidadeEstoque()) {
					item.getProduto().baixarEstoque(quantidade);
				} else {
					throw new NegocioException("Estoque indisponível do produtos " + item.getProduto().getNome());
				}

//				else if (item.getProduto().getListaComposto() != null
//						&& item.getProduto().getListaComposto().size() > 0) {
//					for (Composto composto : item.getProduto().getListaComposto()) {
//						double qtde = composto.getQuantidade() * item.getQuantidade() * fator;
//						composto.getComponente().baixarEstoque(qtde);
//					}
//				}

			}
		}

	}

	public void retornarItensEstoqueExpedicao(Expedicao expedicao) {

		expedicao = porId(expedicao.getId());

		for (ExpedicaoItem item : expedicao.getItems()) {

			ProdutoUnidade prodUnd = produtos.produtoUnidade(item.getProduto(), item.getUnidade());
			double fator = prodUnd == null ? 1 : prodUnd.getFatorEstoque();

			if (item.getProduto().getCategoria().getCategoriaPai().getTipoProduto() != TipoProduto.SERVICO)
				item.getProduto().adicionarEstoque(item.getQuantidade() * fator);
		}

	}

	@Transactional
	public Expedicao guardar(Expedicao expedicao) {

		if (expedicao.getEnderecoEntrega() != null && expedicao.getEnderecoEntrega().getMunicipio() != null) {
			manager.merge(expedicao.getEnderecoEntrega().getMunicipio());
			manager.flush();
		}

		for (ExpedicaoItem item : expedicao.getItems()) {
			OrdemServico servico = item.getServico();
			if (servico != null) {
				servico = manager.merge(servico);
				item.setServico(servico);
				manager.flush();
			}
			item.setCusto(item.getProduto().getCusto());
		}

		return manager.merge(expedicao);

	}

	public Expedicao agendarPedido(Expedicao expedicao) {

		Long odometro = null;
		Long media = null;
		Pessoa pessoa = pessoas.cliente(expedicao.getCliente());
		Contato contato = contatos.ContatoDePessoa(pessoa);

		for (ExpedicaoItem item : expedicao.getItems()) {
			if (item.getProduto().isTipoServico() && item.getServico() != null) {
				odometro = item.getServico().getOdometro();
				media = item.getServico().getVeiculo().getMediaKmMes();
				break;
			}
		}

		if (odometro != null && media != null && media > 0) {

			if (contato == null) {
				contato = new Contato();
				contato.setNome(pessoa.getNome());
				contato.setDdd(pessoa.getDdd());
				contato.setCelular(pessoa.getCelular());
				contato.setFone(pessoa.getFone());
			}

			for (ExpedicaoItem item : expedicao.getItems()) {
				if (item.getProduto().getFatorReposicao() > 0 && item.getProduto().getTipoReposicao() != null) {

					int tempo;
					Calendar c = Calendar.getInstance();

					if (item.getProduto().getTipoReposicao() == TipoReposicao.DIA) {
						tempo = item.getProduto().getFatorReposicao();
					} else {
						tempo = (int) (item.getProduto().getFatorReposicao() / media * 30);
					}
					c.add(Calendar.DATE, tempo);
					Agenda agenda = new Agenda();
					agenda.setUsuario(seguranca.getPessoaLogado().getUsuario());
					agenda.setContato(contato);
					agenda.setDataEmissao(new Date());
					agenda.setDataPrevista(c.getTime());
					agenda.setObservacao(item.getProduto().getNome().toLowerCase());

					agendas.guardar(agenda);

					item.setDataAgendamento(new Date());
					item = manager.merge(item);

				}
			}
		}

		return expedicao;

	}

	@Transactional
	public Expedicao emitir(Expedicao expedicao) {

		expedicao.getEnderecoEntrega().setCep(Suporte.onlyNumbers(expedicao.getEnderecoEntrega().getCep()));
		for (ExpedicaoItem item : expedicao.getItems())
			if (item.getProduto().isTipoServico() && item.getCronologia() != null
					&& item.getCronologia().getInicio() != null && item.getCronologia().getTermino() == null)
				throw new NegocioException("Serviço pendente: " + item.getProduto().getNome());

		String descricao;
		String documento;
		CentroDeCusto centroDeCusto = centrosDeCusto.porTipo(TipoCentroCusto.FATURAMENTO);
		if (centroDeCusto == null)
			throw new NegocioException(
					"Centro de custo indefinido no comercial. Defina centro de custo para faturamento");

		Pessoa responsavel = pessoas.cliente(expedicao.getCliente());
		Calendar vencimento = Calendar.getInstance();
		vencimento.setTime(new Date());

		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		String emissaoStr = format
				.format(expedicao.getDataEmissao() == null ? expedicao.getDataCadastro() : expedicao.getDataEmissao());

		if (expedicao.isNaoEmissivel()) {
			throw new NegocioException("Não pode ser emitido com status " + expedicao.getTipo().getDescricao() + ".");
		}

		if (expedicao.getRegraPgto().getFormaPgto().getEntrada() > 0) {
			Titulo titulo = new Titulo();

			titulo.setTipoDocto(TipoDeTitulo.DM_DUPLICATA_MERCANTIL);
			titulo.setResponsavel(responsavel);
			titulo.setEmissao(expedicao.getDataEmissao());
			titulo.setVencimento(vencimento.getTime());
			titulo.setPrevisao(titulo.getEmissao());
			titulo.setTipoDC(TipoTituloOrigem.RECEBER);
			titulo.setValor(expedicao.getValorTotal() * expedicao.getRegraPgto().getFormaPgto().getEntrada() / 100);
			if (expedicao.getRegraPgto().getDesconto() > 0)
				titulo.setDesconto(titulo.getValor() * expedicao.getRegraPgto().getDesconto() / 100);
			titulo.setSaldo(titulo.getValor() - titulo.getDesconto() + titulo.getAcrescimo());
			titulo.setNominal(responsavel.getNome());
			if (expedicao.getRegraPgto().getFormaPgto().getEntrada() >= 100) {
				documento = "PV" + String.format("%06d", expedicao.getId());
				descricao = "pedido " + String.format("%06d", expedicao.getId());
			} else {
				documento = "PV" + String.format("%06d", expedicao.getId()) + "/00";
				descricao = "entrada de " + expedicao.getRegraPgto().getFormaPgto().getEntrada() + "% ref. pedido seq: "
						+ String.format("%06d", expedicao.getId());
			}
			descricao = descricao + " de " + emissaoStr + " / " + responsavel.getNome();
			titulo.setCentroDeCusto(centroDeCusto);
			titulo.setDescricao(descricao);
			titulo.setDocumento(documento);
			titulo.setExpedicao(expedicao);
			titulo.setAgente(seguranca.getPessoaLogadoOrigem().getAgente());
			this.financeiros.guardarSemCommit(titulo);
		}

		int qtParc;
		int tempo = expedicao.getRegraPgto().getTempo();
		double difPerc = 1 - expedicao.getRegraPgto().getFormaPgto().getEntrada() / 100;

		if (expedicao.getRegraPgto().getTempo() >= 7) {
			qtParc = expedicao.getRegraPgto().getQuantidade();
		} else {
			qtParc = 1;
		}

		double diferenca = difPerc * expedicao.getValorTotal();
		if (!(expedicao.getRegraPgto().getFormaPgto().getTipo().equals(TipoPagamento.CREDITO)
				|| expedicao.getRegraPgto().getFormaPgto().getTipo().equals(TipoPagamento.DEBITO))) {
			if (diferenca > responsavel.getCliente().getLimiteCredito()) {
				throw new NegocioException("Limite de crédito excedido!");
			} else if (diferenca > 0) {
				responsavel.getCliente().setLimiteCredito(responsavel.getCliente().getLimiteCredito() - diferenca);
			}
		}

		double valor = 0;
		if (diferenca > 0) {
			double parcela = Suporte.arredondaValor(diferenca / (qtParc > 0 ? qtParc : 1), 2);
			double residuo = Suporte.arredondaValor(diferenca - (parcela * qtParc), 2);

			for (int i = 1; i <= qtParc; i++) {

				if (i == 1)
					valor = Suporte.arredondaValor(parcela + residuo);
				else
					valor = Suporte.arredondaValor(parcela * 1, 2);

				if (valor < expedicao.getRegraPgto().getFormaPgto().getMinimo()) {
					throw new NegocioException("Valor mínimo para parcela: "
							+ String.format("%1$,.2f", expedicao.getRegraPgto().getFormaPgto().getMinimo()));
				}

				Titulo titulo = new Titulo();

				titulo.setTipoDocto(TipoDeTitulo.DM_DUPLICATA_MERCANTIL);
				vencimento.add(Calendar.DATE, tempo * i);
				titulo.setValor(valor);
				if (expedicao.getRegraPgto().getDesconto() > 0)
					titulo.setDesconto(titulo.getValor() * expedicao.getRegraPgto().getDesconto() / 100);

				if (expedicao.getRegraPgto().getJuro() > 0) {
					if (expedicao.getRegraPgto().getFormaPgto().getTipo().equals(TipoPagamento.CHEQUE)) {
						titulo.setAcrescimo(
								titulo.getValor() * (expedicao.getRegraPgto().getJuro() / 30 * (tempo * i)) / 100);
					}
				}

				titulo.setSaldo(titulo.getValor() - titulo.getDesconto() + titulo.getAcrescimo());
				titulo.setResponsavel(responsavel);
				titulo.setCentroDeCusto(centroDeCusto);
				titulo.setEmissao(new Date());
				titulo.setVencimento(vencimento.getTime());
				titulo.setPrevisao(titulo.getEmissao());
				titulo.setTipoDC(TipoTituloOrigem.RECEBER);
				titulo.setNominal(responsavel.getNome());
				documento = "PV" + String.format("%06d", expedicao.getId()) + "/" + String.format("%02d", i);
				descricao = "parcela ref. pedido seq: " + String.format("%06d", expedicao.getId()) + " de "
						+ emissaoStr;
				titulo.setDescricao(descricao);
				titulo.setCentroDeCusto(centroDeCusto);
				titulo.setDocumento(documento);
				titulo.setExpedicao(expedicao);
				titulo.setAgente(seguranca.getPessoaLogadoOrigem().getAgente());
				titulo = manager.merge(titulo);
				manager.flush();
			}
		}

		expedicao.setTipo(TipoExpedicao.PED);
		baixarItensEstoqueExpedicao(expedicao);

		if (expedicao.getDataEmissao() == null)
			expedicao.setDataEmissao(new Date());

		responsavel = manager.merge(responsavel);
		manager.flush();

		expedicao = agendarPedido(expedicao);

		return guardar(expedicao);

	}

	public void analisarItensEstoqueExpedicao(Expedicao expedicao) {

		expedicao = porId(expedicao.getId());

		for (ExpedicaoItem item : expedicao.getItems()) {
			if (item.getProduto().getCategoria().getCategoriaPai().getTipoProduto() != TipoProduto.SERVICO) {
				ProdutoUnidade prodUnd = produtos.produtoUnidade(item.getProduto(), item.getUnidade());
				double fator = prodUnd == null ? 1 : prodUnd.getFatorEstoque();
				fator = fator * item.getQuantidade();
				if (fator > item.getProduto().getQuantidadeEstoque()) {
					throw new NegocioException("Estoque indisponível do produtos " + item.getProduto().getNome());
				}
			}
		}

	}

	@Transactional
	public Expedicao reabrir(Expedicao expedicao) {

		if (existeExpedicaoComTituloPago(expedicao))
			throw new NegocioException("Operação não pode ser concluída, existe parcelas pagas");

		List<BoletoDigital> lstBoleto = titulos.listaBoletoDigital(expedicao);
		if (lstBoleto != null && lstBoleto.size() > 0)
			throw new NegocioException("Operação não pode ser concluída, existe boleto emitido para este pedido");

		List<NotaFiscalItem> lstNotaItem = notas.items(expedicao);
		if (lstNotaItem != null && lstNotaItem.size() > 0)
			throw new NegocioException("Operação não pode ser concluída, existe nota fiscal para este pedido");

		List<Titulo> lst = new ArrayList<>();
		lst = manager.createNamedQuery("Titulo.listaPorExpedicao", Titulo.class).setParameter("expedicao", expedicao)
				.getResultList();
		for (Titulo titulo : lst) {
			manager.remove(titulo);
			manager.flush();
		}

		retornarItensEstoqueExpedicao(expedicao);

		expedicao.setTipo(TipoExpedicao.ORC);
		expedicao.setDataEmissao(null);
		expedicao = guardar(expedicao);

		double difPerc = 1 - expedicao.getRegraPgto().getFormaPgto().getEntrada() / 100;
		double diferenca = difPerc * expedicao.getValorTotal();
		if (diferenca > 0) {
			Cliente cliente = expedicao.getCliente();
			cliente.setLimiteCredito(cliente.getLimiteCredito() + diferenca);
			pessoas.guardarCliente(cliente);
		}

		return expedicao;
	}

}