package com.sousa.sigpro.repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jrimum.domkee.financeiro.banco.febraban.TipoDeTitulo;

import com.sousa.sigpro.model.Banco;
import com.sousa.sigpro.model.Caixa;
import com.sousa.sigpro.model.CaixaDiario;
import com.sousa.sigpro.model.CaixaTitulo;
import com.sousa.sigpro.model.CaixaValor;
import com.sousa.sigpro.model.ContaCorrente;
import com.sousa.sigpro.model.ContratoAdesao;
import com.sousa.sigpro.model.Movimento;
import com.sousa.sigpro.model.Pessoa;
import com.sousa.sigpro.model.Titulo;
import com.sousa.sigpro.model.tipo.TipoCaixaStatus;
import com.sousa.sigpro.model.tipo.TipoCentroCusto;
import com.sousa.sigpro.model.tipo.TipoMes;
import com.sousa.sigpro.model.tipo.TipoPagamento;
import com.sousa.sigpro.model.tipo.TipoRepeticao;
import com.sousa.sigpro.model.tipo.TipoTituloOrigem;
import com.sousa.sigpro.repository.filter.CondicaoFilter;
import com.sousa.sigpro.security.Seguranca;
import com.sousa.sigpro.service.NegocioException;
import com.sousa.sigpro.util.Suporte;
import com.sousa.sigpro.util.SuporteData;
import com.sousa.sigpro.util.jpa.Transactional;

public class Caixas implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Seguranca seguranca;
	@Inject
	private EntityManager manager;
	@Inject
	private Titulos titulos;
	@Inject
	private Pessoas pessoas;
	@Inject
	private Cartoes cartoes;

	public Caixa porId(Long id) {
		Caixa caixa = null;
		try {
			String condicao = "select p from Caixa p where p.id = " + id;
			caixa = manager.createQuery(condicao, Caixa.class).getSingleResult();
		} catch (Exception e) {
			throw new NegocioException("caixa não localizado");
		}
		return caixa;
	}

	public List<Caixa> listaPorTipoCentroCusto(TipoTituloOrigem tipoOrigem, TipoCentroCusto tipoCentroCusto) {
		StringBuilder condicao = new StringBuilder();
		condicao.append("select c from Caixa c where 1 = 1 ");
		condicao.append(" and c.tipoDC = " + Suporte.getQuotedStr(tipoOrigem.name()));
		condicao.append(" and c.centroDeCusto.tipo = " + Suporte.getQuotedStr(tipoCentroCusto.name()));
		return manager.createQuery(condicao.toString(), Caixa.class).getResultList();
	}

	public List<Caixa> listaPorDiario(CaixaDiario diario) {
		try {
			return manager.createNamedQuery("Caixa.listaPorDiario", Caixa.class).setParameter("diario", diario)
					.getResultList();
		} catch (Exception e) {
			return null;
		}
	}

	@Transactional
	public void moverCaixaParaDiario(Caixa caixa, CaixaDiario diario) {
		String sql = "UPDATE Caixa c SET c.diario = :diario WHERE c = :caixa";
		manager.createQuery(sql).setParameter("caixa", caixa).setParameter("diario", diario).executeUpdate();
	}

	public Map<Integer, Double> graficoFluxoPrevisto(int ano, TipoMes mes, TipoTituloOrigem tipo) {

		Map<Integer, Double> mapa = new TreeMap<>();

		Calendar atual = Calendar.getInstance();
		atual.set(Calendar.YEAR, ano);
		atual.set(Calendar.MONTH, mes.ordinal());
		atual.set(Calendar.DAY_OF_MONTH, 1);

		for (int dia = SuporteData.primeiroDiaDoMes(atual.getTime()); dia <= SuporteData
				.ultimoDiaDoMes(atual.getTime()); dia++) {
			mapa.put(dia, Double.valueOf(0));
		}

		String condicao = "select EXTRACT(DAY FROM COALESCE(previsao, vencimento)), sum(saldo) from Titulo t "
				+ "WHERE tipoDC = :tipo and EXTRACT(YEAR FROM COALESCE(previsao, vencimento)) = :ano "
				+ "and EXTRACT(MONTH FROM COALESCE(previsao, vencimento)) = :mes GROUP BY 1 ORDER BY 1";

		@SuppressWarnings("unchecked")
		List<Object[]> lstObj = manager.createQuery(condicao).setParameter("ano", ano)
				.setParameter("mes", mes.ordinal() + 1).setParameter("tipo", tipo).getResultList();
		for (Object[] obj : lstObj) {
			if (obj != null) {
				mapa.put((Integer) obj[0], (Double) obj[1]);
			}
		}

		return mapa;
	}

	@Transactional
	public void Excluir(Caixa caixa) {
		caixa = porId(caixa.getId());
		if (caixa.getDiario() != null)
			throw new NegocioException("Operação somente para estorno.");
		manager.remove(caixa);
	}

	public List<CaixaTitulo> movimentoPorTitulo(Titulo titulo) {
		return manager.createNamedQuery("CaixaTitulo.listaPorTitulo", CaixaTitulo.class).setParameter("titulo", titulo)
				.getResultList();
	}

	public List<CaixaTitulo> movimentoTituloPorDiario(CaixaDiario diario) {
		try {
			return manager.createNamedQuery("CaixaTitulo.listaPorDiario", CaixaTitulo.class)
					.setParameter("diario", diario).getResultList();
		} catch (Exception e) {
			return null;
		}
	}

	public List<CaixaValor> movimentoValorPorDiario(CaixaDiario diario) {
		try {
			return manager.createNamedQuery("CaixaValor.listaPorDiario", CaixaValor.class)
					.setParameter("diario", diario).getResultList();
		} catch (Exception e) {
			return null;
		}
	}

	private String consulta(CondicaoFilter filtro) {
		StringBuilder condicao = new StringBuilder();
		condicao.append("where 1 = 1 ");

		if (!filtro.getNome().equals(""))
			condicao.append(" and UPPER(c.nominal) like UPPER('%" + filtro.getNome() + "%') ");

		if (Suporte.dataIsValida(filtro.getEmissaoInicial()))
			condicao.append(" and cast(d.dataMov as date) >= '" + filtro.getEmissaoInicial() + "' ");

		if (Suporte.dataIsValida(filtro.getEmissaoFinal()))
			condicao.append(" and cast(d.dataMov as date) <= '" + filtro.getEmissaoFinal() + "' ");

		if (filtro.getCaixaStatus() != null)
			condicao.append(" and c.status = '" + filtro.getCaixaStatus() + "' ");

		if (filtro.getTipoDC() != null && filtro.getTipoDC().length > 0) {
			String condTipo = "";
			for (TipoTituloOrigem tipo : filtro.getTipoDC()) {
				if (condTipo != "") {
					condTipo += " or ";
				}
				if (tipo == TipoTituloOrigem.PAGAR) {
					condTipo += " c.tipoDC = 'PAGAR' ";
				} else if (tipo == TipoTituloOrigem.RECEBER) {
					condTipo += " c.tipoDC = 'RECEBER' ";
				}
			}
			if (condTipo != "")
				condicao.append(" and (" + condTipo + ") ");
		}

		return condicao.toString();
	}

	@SuppressWarnings("unchecked")
	public List<CaixaTitulo> lista_titulo(CondicaoFilter filtro) {

		StringBuilder condicao = new StringBuilder();
		condicao.append("select t from CaixaTitulo t join t.caixa c join c.diario d ");
		condicao.append(consulta(filtro));
		condicao.append(" ORDER BY " + filtro.getSortField());

		Query query = manager.createQuery(condicao.toString(), CaixaTitulo.class);
		if (filtro.getPrimeiroRegistro() != null) {
			query.setFirstResult(filtro.getPrimeiroRegistro().intValue());
			query.setMaxResults(filtro.getQuantidadeRegistros().intValue());
		}

		return query.getResultList();
	}

	public int quantidadeFiltrados(CondicaoFilter filtro) {
		StringBuilder condicao = new StringBuilder();
		// condicao.append("select count(*) from Caixa v ");
		condicao.append("select count(*) from Caixa c left join c.diario d ");
		condicao.append(consulta(filtro));
		return ((Number) manager.createQuery(condicao.toString()).getSingleResult()).intValue();
	}

	@SuppressWarnings("unchecked")
	public List<Caixa> lista(CondicaoFilter filtro) {

		StringBuilder condicao = new StringBuilder();
		condicao.append("select c from Caixa c left join c.diario d ");
		condicao.append(consulta(filtro));
		if (filtro.getSortField() != null)
			condicao.append(" ORDER BY c." + filtro.getSortField() + (filtro.isAscendente() ? "" : " desc"));
		else
			condicao.append(" ORDER BY c.id desc");

		Query query = manager.createQuery(condicao.toString(), Caixa.class);
		if (filtro.getPrimeiroRegistro() != null) {
			query.setFirstResult(filtro.getPrimeiroRegistro().intValue());
			query.setMaxResults(filtro.getQuantidadeRegistros().intValue());
		}

		return query.getResultList();
	}

	private CaixaDiario movimentaCaixaValor(CaixaValor caixaValor, CaixaDiario diario) {

		if (caixaValor.getCartao() == null) {

			TipoTituloOrigem tipo;
			TipoPagamento pgto = caixaValor.getTipoPagamento();
			tipo = caixaValor.getTitulo() != null ? caixaValor.getTitulo().getTipoDC()
					: caixaValor.getCaixa().getTipoDC();

			double valor = caixaValor.getValor();

			if (caixaValor.getTitulo() != null) {
				ContaCorrente conta = caixaValor.getTitulo().getContaBancaria();
				if (conta != null) {
					if (tipo.equals(TipoTituloOrigem.PAGAR))
						conta.addValor(-caixaValor.getValor());
					else
						conta.addValor(caixaValor.getValor());
					manager.merge(conta);
					manager.flush();
				}
			}

			if (tipo.equals(TipoTituloOrigem.PAGAR)) {
				if (pgto.equals(TipoPagamento.DINHEIRO)) {
					diario.retiraDinheiro(valor);
				} else if (pgto.equals(TipoPagamento.SAQUE)) {
					diario.adicionaDinheiro(valor);
					diario.retiraBanco(valor);
				} else if (pgto.equals(TipoPagamento.BANCO) || pgto.equals(TipoPagamento.DEPOSITO)
						|| pgto.equals(TipoPagamento.CREDITO) || pgto.equals(TipoPagamento.DEBITO)) {
					diario.retiraBanco(valor);
				} else if (pgto.equals(TipoPagamento.CHEQUE)) {
					diario.retiraCheque(valor);
				}
			} else if (tipo.equals(TipoTituloOrigem.RECEBER)) {
				if (pgto.equals(TipoPagamento.DINHEIRO)) {
					diario.adicionaDinheiro(valor);
				} else if (pgto.equals(TipoPagamento.SAQUE)) {
					diario.retiraDinheiro(valor);
					diario.adicionaBanco(valor);
				} else if (pgto.equals(TipoPagamento.BANCO) || pgto.equals(TipoPagamento.DEPOSITO)
						|| pgto.equals(TipoPagamento.CREDITO) || pgto.equals(TipoPagamento.DEBITO)) {
					diario.adicionaBanco(valor);
				} else if (pgto.equals(TipoPagamento.CHEQUE)) {
					diario.adicionaCheque(valor);
				}
			}
		}

		return diario;

	}

	@Transactional
	public Caixa estornar(Caixa caixa, CaixaDiario ultimo) {

		Titulo titulo = null;
		CaixaValor caixaValor = null;
		CaixaDiario diario = manager.find(CaixaDiario.class, ultimo.getId());
		caixa = porId(caixa.getId());

		Caixa caixaNew = new Caixa(caixa);
		caixaNew.setTipoDC(
				caixa.getTipoDC().equals(TipoTituloOrigem.PAGAR) ? TipoTituloOrigem.RECEBER : TipoTituloOrigem.PAGAR);
		caixaNew.setEncerramento(new Date());
		caixaNew.setStatus(TipoCaixaStatus.REALIZADO);
		caixaNew.setDiario(ultimo);
		caixaNew.setObservacao("Referente estorno do caixa " + Suporte.zerosAEsquerda(caixa.getId(), 6));
		caixaNew.setOrigem(caixa);

		for (CaixaTitulo item : caixa.getTitulos()) {
			titulo = item.getTitulo();
			titulo.setDataBaixa(null);
			titulo.setSaldo(titulo.getSaldo() + item.getValor());
			titulo = manager.merge(titulo);
			manager.flush();
		}

		for (CaixaValor item : caixa.getValores()) {
			if (item.getTitulo() != null) {

				titulo = item.getTitulo();

				titulo.setDataCancelamento(new Date());
				titulo.setSaldo(0);

				if (titulo.getContaBancaria() != null) {
					titulo.setBanco(titulo.getContaBancaria().getBanco());
					titulo.setAgencia(String.valueOf(titulo.getContaBancaria().getAgencia()));
					titulo.setConta(String.valueOf(titulo.getContaBancaria().getNumero()));
					titulo.setContaBancaria(null);
				}
				titulo = manager.merge(titulo);
				manager.flush();

				caixaValor = new CaixaValor(item);
				caixaValor.setCaixa(caixaNew);
				caixaValor.setTitulo(titulo);
				caixaNew.getValores().add(caixaValor);

				estornarTituloDerivado(titulo);

			} else if (item.getTipoPagamento().equals(TipoPagamento.DINHEIRO)) {
				caixaValor = new CaixaValor(item);
				caixaValor.setCaixa(caixaNew);
				caixaNew.getValores().add(caixaValor);
			}

			movimentaCaixaValor(caixaValor, diario);

		}

		diario = manager.merge(diario);
		manager.flush();

		caixaNew.calcular();
		caixaNew = manager.merge(caixaNew);
		manager.flush();

		caixa.setObservacao("ESTORNADO: " + caixa.getObservacao());
		caixa.setEstorno(new Date());
		manager.merge(caixa);

		return caixaNew;

	}

	private void estornarTituloDerivado(Titulo origem) {
		List<Titulo> lst = titulos.lista(origem);
		if (lst != null && lst.size() > 0)
			for (Titulo titulo : lst) {
				titulo.setDataCancelamento(new Date());
				titulo.setSaldo(0);
				if (titulo.getContaBancaria() != null) {
					titulo.setBanco(titulo.getContaBancaria().getBanco());
					titulo.setAgencia(String.valueOf(titulo.getContaBancaria().getAgencia()));
					titulo.setConta(String.valueOf(titulo.getContaBancaria().getNumero()));
					titulo.setContaBancaria(null);
				}
				titulo = manager.merge(titulo);
				manager.flush();
			}
	}

	@Transactional
	public Caixa guardar(Caixa caixa) {

		if (caixa == null)
			throw new NegocioException("Caixa indefinido.");

		caixa.calcular();
		if (seguranca.getPessoaLogado().getAgente() == null)
			throw new NegocioException("Agente financeiro indefinido.");
		if (caixa.getCentroDeCusto() == null)
			throw new NegocioException("Defina o centro de custo.");
		if (caixa.getNominal() == null)
			throw new NegocioException("Preencha o campo nominal.");
		if (caixa.getValorFinal() < 0 && caixa.getValorLiquido() <= 0)
			throw new NegocioException("Existe diferença no caixa");

		if (caixa.getAgente() == null)
			caixa.setAgente(seguranca.getPessoaLogado().getAgente());

		ContaCorrente conta;

		for (CaixaValor item : caixa.getValores()) {
			conta = null;
			if (item.getTitulo() != null) {
				Titulo titulo = item.getTitulo();

				if (titulo.getTipoDC() == null) {
					if (caixa.getTipoDC() == TipoTituloOrigem.PAGAR)
						titulo.setTipoDC(TipoTituloOrigem.RECEBER);
					else
						titulo.setTipoDC(TipoTituloOrigem.PAGAR);
				}

				if (titulo.getCartao() != null && titulo.getCartao().getId() == null)
					titulo.setCartao(null);

				if (item.getRegraPgto() != null) {
					conta = item.getRegraPgto().getFormaPgto().getConta();
				} else if (item.getTitulo().getContaBancaria() != null)
					conta = item.getTitulo().getContaBancaria();

				if (conta != null) {
					titulo.setContaBancaria(conta);
					titulo.setResponsavel(conta.getOrigem());
					titulo.setNominal(conta.getDescricao());
					titulo.setDocumento(String.valueOf(conta.getNumero()));
				} else if (titulo.getCartao() != null) {
					titulo.setNominal(titulo.getCartao().getNome());
					titulo.setDocumento(titulo.getCartao().getNumero());
				}

				if (item.getTitulo().getContaBancaria() != null && titulo.getTipoDocto() == TipoDeTitulo.CH_CHEQUE) {
					if (titulo.getId() == null) {
						Titulo cheque = titulos.chequePorNumero(titulo.getDocumento(), titulo.getContaBancaria());
						if (cheque != null)
							throw new NegocioException("cheque " + cheque.getDocumento() + "/"
									+ cheque.getContaBancaria().getDescricao() + " já foi emitido");
					}
					if (titulo.getResponsavel() == null)
						titulo.setResponsavel(caixa.getPessoa());
				}

				if (titulo.getResponsavel() == null || titulo.getResponsavel().getId() == null)
					titulo.setResponsavel(caixa.getPessoa());

				titulo.setDescricao(caixa.getObservacao());
				titulo.setAgente(caixa.getAgente());
				titulo.setValor(item.getValor());
				titulo.setCentroDeCusto(caixa.getCentroDeCusto());

				if (titulo.getBanco() != null && titulo.getBanco().getId() == null) {
					Banco banco = titulo.getBanco();
					banco = manager.merge(banco);
					manager.flush();
				}

				if (item.getRegraPgto() != null)
					titulo.setSaldo(0);

				titulo = manager.merge(titulo);
				manager.flush();
				item.setTitulo(titulo);
			}
		}

		return manager.merge(caixa);
	}

	private void baixaTituloCaixaValor(CaixaValor item) {

		if (item.getTitulo().getContaBancaria() != null && item.getTitulo().getTipoDocto() != TipoDeTitulo.CH_CHEQUE
				&& item.getTitulo().getTipoDC().equals(TipoTituloOrigem.PAGAR)
				&& item.getTitulo().getContaBancaria().getSaldo() < item.getValor())
			throw new NegocioException(
					"saldo insuficiente na conta " + item.getTitulo().getContaBancaria().getNumeroToStr());

		Titulo titulo = item.getTitulo();
		titulo.setEmissao(item.getCaixa().getEmissao());
		titulo.setVencimento(item.getCaixa().getEmissao());
		if (titulo.getTipoDocto() == TipoDeTitulo.CH_CHEQUE) {
			titulo.setSaldo(titulo.getValor());
			titulo.setResponsavel(item.getCaixa().getPessoa());
		} else {
			titulo.setDataBaixa(item.getCaixa().getEncerramento());
		}

		titulo = manager.merge(titulo);
		manager.flush();

	}

	private void baixaCartaoCaixaValor(CaixaValor item) {

		double valorParcela = Suporte.arredondaValor(item.getValor() / item.getParcelas(), 2);
		double valorResidual = Suporte.arredondaValor(item.getValor() - (valorParcela * item.getParcelas()), 2);

		Calendar vencimento = Calendar.getInstance();
		vencimento.setTime(cartoes.faturaCartao(item.getCartao(), item.getCaixa().getDiario().getDataMov()));

		for (int i = 1; i <= item.getParcelas(); i++) {

			vencimento.add(Calendar.MONTH, i > 1 ? 1 : 0);
			Titulo fatura = faturaCartaoCredito(item, vencimento.getTime(),
					valorParcela + (i == 1 ? valorResidual : 0));

			if (fatura.getDataCancelamento() != null)
				throw new NegocioException("fatura do cartão " + fatura.getCartao().getNome() + " ref."
						+ fatura.getDocumento() + " foi cancelada em "
						+ SuporteData.formataDataToStr(fatura.getDataCancelamento(), "dd/MM/yyyy"));

			if (fatura.getDataBaixa() != null)
				throw new NegocioException("fatura do cartão " + fatura.getCartao().getNome() + " ref."
						+ fatura.getDocumento() + " foi encerrada em "
						+ SuporteData.formataDataToStr(fatura.getDataBaixa(), "dd/MM/yyyy"));

			Titulo titulo = new Titulo(fatura);

			titulo.setOrigem(fatura);
			titulo.setCartao(null);
			titulo.setResponsavel(item.getCaixa().getPessoa());
			titulo.setNominal(item.getCaixa().getPessoa().getNome());
			titulo.setDescricao(item.getCaixa().getObservacao());
			titulo.setDocumento(Suporte.zerosAEsquerda(i, 2) + "/" + Suporte.zerosAEsquerda(item.getParcelas(), 2));
			titulo.setTipoDC(TipoTituloOrigem.COMPENSA);
			titulo.setCentroDeCusto(item.getCaixa().getCentroDeCusto());
			titulo.setDataBaixa(item.getCaixa().getDiario().getDataMov());
			titulo.setValor(valorParcela + (i == 1 ? valorResidual : 0));
			titulo.setSaldo(0);
			titulo = manager.merge(titulo);
			manager.flush();

		}
	}

	private Titulo faturaCartaoCredito(CaixaValor item, Date vencimento, double valor) {
		Titulo fatura = titulos.faturaCartao(item.getCartao(), vencimento);
		if (fatura == null) {
			fatura = new Titulo();
			fatura.setEmissao(new Date());
			fatura.setVencimento(vencimento);
			fatura.setTipoDC(TipoTituloOrigem.PAGAR);
			fatura.setRepete(TipoRepeticao.NAO);
			fatura.setTipoDocto(TipoDeTitulo.DM_DUPLICATA_MERCANTIL);
			fatura.setCartao(item.getCartao());
			fatura.setCentroDeCusto(item.getCartao().getCentroDeCusto());
			fatura.setDescricao("fatura cartão " + item.getCartao().getNome());
			fatura.setDocumento(SuporteData.formataDataToStr(vencimento, "MM/yyyy"));
			fatura.setResponsavel(item.getCartao().getEmpresa());
			fatura.setNominal(item.getCartao().getNome());
			fatura.setAgente(item.getCaixa().getAgente());
		}
		fatura.addValor(valor);
		fatura.setSaldo(fatura.getValor());
		fatura = manager.merge(fatura);
		manager.flush();
		return fatura;
	}

	private void baixaCartaoCompensaTitulo(Titulo titulo) {
		String condicao = "UPDATE Titulo t SET t.dataBaixa = :data, t.saldo = 0 " + "where t.origem = :origem ";
		Query query = manager.createQuery(condicao);
		query.setParameter("data", titulo.getDataBaixa());
		query.setParameter("origem", titulo);
		query.executeUpdate();
	}

	@SuppressWarnings("unchecked")
	public List<CaixaValor> lista_movimentacao(CondicaoFilter filtro) {

		StringBuilder condicao = new StringBuilder();
		condicao.append("select v from CaixaValor v join v.caixa c join c.diario d ");
		condicao.append(consulta(filtro));
		condicao.append(" ORDER BY " + filtro.getSortField());

		Query query = manager.createQuery(condicao.toString(), CaixaValor.class);
		if (filtro.getPrimeiroRegistro() != null) {
			query.setFirstResult(filtro.getPrimeiroRegistro().intValue());
			query.setMaxResults(filtro.getQuantidadeRegistros().intValue());
		}

		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<Movimento> lista_movimentacao_tipoPagamento(CondicaoFilter filtro) {

		List<Movimento> lista = new ArrayList<>();

		StringBuilder condicao = new StringBuilder();
		condicao.append("select v.tipoPagamento");
		condicao.append(" ,SUM(v.valor * (CASE WHEN v.caixa.tipoDC = 'RECEBER' THEN 1 ELSE 0 END))");
		condicao.append(" ,SUM(v.valor * (CASE WHEN v.caixa.tipoDC = 'PAGAR' THEN 1 ELSE 0 END))");
		condicao.append("from CaixaValor v join v.caixa c join c.diario d ");
		condicao.append(consulta(filtro));
		condicao.append("GROUP BY v.tipoPagamento");

		Query query = manager.createQuery(condicao.toString());
		List<Object[]> resultado = (List<Object[]>) query.getResultList();
		for (Object[] obj : resultado) {
			lista.add(new Movimento((TipoPagamento) obj[0], (Double) obj[1], (Double) obj[2]));
		}

		return lista;
	}

	@Transactional
	public Caixa encerrar(Caixa caixa) {

		Titulo titulo = null;
		ContaCorrente conta = null;
		CaixaDiario diario = caixa.getDiario();

		caixa.setEncerramento(new Date());
		if (caixa.getEmissao() == null)
			caixa.setEmissao(caixa.getEncerramento());

		for (CaixaTitulo item : caixa.getTitulos()) {

			titulo = titulos.porId(item.getTitulo().getId());

			if (titulo.getSaldo() <= 0)
				throw new NegocioException(
						"Baixa já confirmada para título no valor de : R$ " + Suporte.formataCurrency(item.getValor()));

			titulo.setAgente(caixa.getAgente());
			titulo.setAcrescimo(item.getValorMulta() + item.getValorJuro());
			titulo.setDesconto(item.getValorDesconto());
			titulo.setSaldo(titulo.getSaldo() - item.getValorPago() - item.getValorDesconto());
			if (titulo.getSaldo() <= 0) {
				titulo.setSaldo(0);
				titulo.setDataBaixa(caixa.getEncerramento());
			}

			if (titulo.getContaBancaria() != null && caixa.getTipoDC().equals(TipoTituloOrigem.PAGAR)) {
				conta = titulo.getContaBancaria();
				if (conta.getSaldo() < item.getValor())
					throw new NegocioException("saldo insuficiente na conta " + conta.getNumeroToStr());
				conta.addValor(-item.getValor());
			}

			titulo = manager.merge(titulo);
			manager.flush();

			baixaCartaoCompensaTitulo(titulo);

			liberarFranquiaUsoSistema(titulo);

			item.setTitulo(titulo);
			item.setCaixa(caixa);

		}

		for (CaixaValor item : caixa.getValores()) {

			if (caixa.isTemCaixaReceber() && item.getRegraPgto() != null
					&& item.getRegraPgto().getFormaPgto().getTipo().equals(TipoPagamento.CAIXA)) {
				Pessoa pessoa = pessoas.porId(caixa.getPessoa().getId());
				if (pessoa.getValorCredito() < item.getValor())
					throw new NegocioException("Valor do crédito em caixa insuficiente para esta operação");
				pessoa.setValorCredito(0);
				pessoa = manager.merge(pessoa);
			}

			if (item.getTitulo() != null)
				baixaTituloCaixaValor(item);

			if (item.getCartao() != null)
				baixaCartaoCaixaValor(item);

			if (item.getRegraPgto() != null) {

				Calendar vencimento = Calendar.getInstance();
				vencimento.setTime(new Date());

				int qtde = item.getRegraPgto().isUnifica() ? 1 : item.getRegraPgto().getQuantidade();
				double difPerc = 1 - Suporte.arredondaValor(item.getRegraPgto().getFormaPgto().getEntrada() / 100, 2);

				double valorTotal = Suporte.arredondaValor(difPerc * item.getValor(), 2);
				double valorParcela = Suporte.arredondaValor(valorTotal / qtde, 2);
				double valorResidual = Suporte.arredondaValor(valorTotal - (valorParcela * qtde), 2);

				double valorDescontoTotal = Suporte
						.arredondaValor(item.getValor() * item.getRegraPgto().getDesconto() / 100, 2);
				double valorParcelaDesconto = Suporte.arredondaValor(valorDescontoTotal / qtde, 2);
				double valorResidualDesconto = Suporte
						.arredondaValor(valorDescontoTotal - (valorParcelaDesconto * qtde), 2);

				if (valorParcela > 0) {

					for (int i = 1; i <= qtde; i++) {

						if (valorParcela < item.getRegraPgto().getFormaPgto().getMinimo())
							throw new NegocioException("Valor mínimo para parcela: "
									+ String.format("%1$,.2f", item.getRegraPgto().getFormaPgto().getMinimo()));

						titulo = new Titulo();
						titulo.setTipoDocto(TipoDeTitulo.DM_DUPLICATA_MERCANTIL);
						vencimento.add(Calendar.DAY_OF_MONTH, item.getRegraPgto().getTempo());

						titulo.setValor(valorParcela + (i == 1 ? valorResidual : 0));
						titulo.setDesconto(valorParcelaDesconto + (i == 1 ? valorResidualDesconto : 0));
						titulo.setSaldo(titulo.getValor() - titulo.getDesconto() + titulo.getAcrescimo());

						titulo.setResponsavel(caixa.getPessoa());
						titulo.setDocumento(Suporte.zerosAEsquerda(caixa.getId(), 6) + "/" + String.valueOf(i));
						titulo.setEmissao(new Date());
						titulo.setVencimento(vencimento.getTime());
						titulo.setPrevisao(vencimento.getTime());
						titulo.setTipoDC(TipoTituloOrigem.RECEBER);
						titulo.setNominal(caixa.getNominal());

						if (item.getRegraPgto().getFormaPgto().getConta() != null)
							titulo.setContaBancaria(item.getRegraPgto().getFormaPgto().getConta());

						titulo.setDescricao(caixa.getObservacao());
						titulo.setCentroDeCusto(caixa.getCentroDeCusto());
						titulo.setAgente(seguranca.getPessoaLogadoOrigem().getAgente());
						titulo.setCentroDeCusto(caixa.getCentroDeCusto());
						titulo.setDataBaixa(null);

						if (item.getTitulo() != null)
							titulo.setOrigem(item.getTitulo());

						titulo = manager.merge(titulo);
						manager.flush();

						if (item.getTitulo() == null) {
							item.setTitulo(titulo);
							item.setCaixa(caixa);
						}

					}
				}
			}

			diario = movimentaCaixaValor(item, diario);

		}

		diario = manager.merge(diario);
		manager.flush();

		return manager.merge(caixa);

	}

	private void liberarFranquiaUsoSistema(Titulo titulo) {
		if (titulo.getFranquia() > 0 && titulo.getContratoAdesao() != null) {
			ContratoAdesao contrato = manager.find(ContratoAdesao.class, titulo.getContratoAdesao().getId());
			Date validade = contrato.getDataValidade();
			if (validade == null || validade.before(new Date())) {
				validade = new Date();
			}
			validade = SuporteData.incrementaDiaNaData(validade, titulo.getFranquia());
			contrato.setDataValidade(validade);
			contrato = manager.merge(contrato);
			manager.flush();
		}
	}

}