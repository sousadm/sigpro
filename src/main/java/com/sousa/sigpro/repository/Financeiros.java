package com.sousa.sigpro.repository;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.sousa.sigpro.model.Agente;
import com.sousa.sigpro.model.Caixa;
import com.sousa.sigpro.model.CaixaTitulo;
import com.sousa.sigpro.model.CaixaValor;
import com.sousa.sigpro.model.Cliente;
import com.sousa.sigpro.model.ContaCorrente;
import com.sousa.sigpro.model.Expedicao;
import com.sousa.sigpro.model.Titulo;
import com.sousa.sigpro.model.tipo.TipoTituloOrigem;
import com.sousa.sigpro.repository.filter.CondicaoFilter;
import com.sousa.sigpro.security.Seguranca;
import com.sousa.sigpro.service.NegocioException;
import com.sousa.sigpro.util.Suporte;
import com.sousa.sigpro.util.SuporteData;
import com.sousa.sigpro.util.jpa.Transactional;

public class Financeiros implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager manager;
	@Inject
	private Pessoas pessoas;
	@Inject
	private Seguranca seguranca;

	public Titulo porId(Long id) {
		String condicao = "select p from Titulo p where p.id = " + id;
		return manager.createQuery(condicao, Titulo.class).getSingleResult();
	}

	public Caixa caixaPorID(Long id) {
		return manager.find(Caixa.class, id);
	}

	public List<Titulo> lista(Expedicao expedicao) {
		String condicao = "select p from Titulo p where p.expedicao = " + expedicao.getId();
		return manager.createQuery(condicao, Titulo.class).getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<Titulo> lista(CondicaoFilter filtro) {
		Session session = this.manager.unwrap(Session.class);
		Criteria criteria = session.createCriteria(Titulo.class);

		if (filtro.getAgente() != null && filtro.getAgente().getId() != null) {
			criteria.add(Restrictions.eq("agente", filtro.getAgente()));
		}

		if (filtro.getNome() != null) {
			criteria.add(Restrictions.ilike("nominal", filtro.getNome().toUpperCase(), MatchMode.ANYWHERE));
		}

		if (filtro.getInicio() != null)
			criteria.add(Restrictions.ge("emissao", filtro.getInicio()));

		if (filtro.getTermino() != null)
			criteria.add(Restrictions.le("emissao", filtro.getTermino()));

		if (filtro.getVencimentoInicial() != null)
			criteria.add(Restrictions.ge("vencimento", filtro.getVencimentoInicial()));

		if (filtro.getVencimentoFinal() != null)
			criteria.add(Restrictions.le("vencimento", filtro.getVencimentoFinal()));

		criteria.addOrder(Order.asc("tipoDC"));

		return criteria.list();
	}

	@Transactional
	public Titulo guardar(Titulo titulo) {
		return manager.merge(titulo);
	}

	public Titulo guardarSemCommit(Titulo titulo) {
		return manager.merge(titulo);
	}

	@Transactional
	public void remover(Titulo titulo) throws NegocioException {
		titulo = porId(titulo.getId());
		manager.remove(titulo);
	}

	public void removerSemCommit(Titulo titulo) throws NegocioException {
		titulo = porId(titulo.getId());
		manager.remove(titulo);
	}

	@Transactional
	public Caixa guardarCaixa(Caixa caixa) {
		// ROTINA SOMENTE PARA INCLUSAO DE CAIXA
		Caixa caixaRetorno;
		double valorDif = caixa.getValorLiquido();
		double valor = caixa.getValor();

		if (valor > valorDif && caixa.getTitulos().size() > 0)
			valorDif = valor - valorDif;
		else
			valorDif = 0;

		caixa.setId(null);
		for (CaixaTitulo item : caixa.getTitulos()) {
			item.setId(null);
			item.setCaixa(caixa);
			Titulo titulo = item.getTitulo();
			titulo.setSaldo(titulo.getSaldo() - item.getValorPago());
			if (titulo.getSaldo() == 0) {
				titulo.setDataBaixa(caixa.getEmissao());
			}

			/* RETORNA CREDITO PARA O CLIENTE */
			if (titulo.getVencimento().after(titulo.getEmissao())) {
				Cliente cliente = titulo.getExpedicao().getCliente();
				cliente.setLimiteCredito(cliente.getLimiteCredito() + item.getValorPago());
				pessoas.guardarCliente(cliente);
			}

			manager.merge(titulo);
		}

		if (valorDif > 0) {
			Caixa caixaDif = new Caixa();
			caixaDif.setEmissao(new Date());
			caixaDif.setValor(valorDif);
			caixaDif.setAgente(caixa.getAgente());
			caixaDif.setCentroDeCusto(caixa.getCentroDeCusto());
			caixaDif.setObservacao("diferença do caixa " + caixa.getId());
			caixaDif.setPessoa(caixa.getPessoa());
			caixaDif.setTipoDC(TipoTituloOrigem.PAGAR);
			manager.merge(caixaDif);
		}

		caixaRetorno = manager.merge(caixa);

		return caixaRetorno;

	}

	public double saldo(TipoTituloOrigem tipo, Agente agente, Date data) {
		try {

			Date inicio = SuporteData.incrementaDiaNaData(data, -Suporte.PRAZO_VENCIMENTO_GERENCIAVEL);

			StringBuilder condicao = new StringBuilder();
			condicao.append("select sum(saldo) from Titulo where dataCancelamento is null ");
			condicao.append(
					"	and dataBaixa is null and saldo > 0.00 and tipoDC = " + Suporte.getQuotedStr(tipo.name()));
			if (agente != null)
				condicao.append("	and agente = " + agente.getId());
			condicao.append(
					"	and cast(coalesce(previsao, vencimento) as date) >= " + Suporte.formataDataSQL_Quoted(inicio));
			condicao.append(
					"	and cast(coalesce(previsao, vencimento) as date) <= " + Suporte.formataDataSQL_Quoted(data));
			Query query = manager.createQuery(condicao.toString(), Double.class);
			return (Double) query.getSingleResult();
		} catch (Exception e) {
			return 0;
		}

	}

	public List<CaixaValor> listaCaixaDiarioValores(CondicaoFilter filtro) {

		StringBuilder condicao = new StringBuilder();
		condicao.append("select v from CaixaValor v join v.caixa c ");
		condicao.append("where c.diario.origem = " + seguranca.getPessoaLogadoOrigem().getId());
		if (Suporte.dataIsValida(filtro.getPagamentoInicial()))
			condicao.append(" 	and cast(c.diario.dataMov as date) >= "
					+ Suporte.formataDataSQL_Quoted(filtro.getPagamentoInicial()));
		if (Suporte.dataIsValida(filtro.getPagamentoFinal()))
			condicao.append(" 	and cast(c.diario.dataMov as date) <= "
					+ Suporte.formataDataSQL_Quoted(filtro.getPagamentoFinal()));

		if (filtro.getTipoDC() != null && filtro.getTipoDC().length > 0) {
			String condTipo = "";
			for (TipoTituloOrigem tipo : filtro.getTipoDC()) {
				if (condTipo != "")
					condTipo = condTipo + ", ";
				condTipo = condTipo + "'" + tipo + "'";
			}
			if (condTipo != "")
				condicao.append(" and c.tipoDC in (" + condTipo + ") ");
		}

		if (filtro.getOrdem() != null && filtro.getOrdem().length() > 0)
			condicao.append(" order by " + filtro.getOrdem());
		else
			condicao.append(" order by c.centroDeCusto, c.id, v.id ");

		return manager.createQuery(condicao.toString(), CaixaValor.class).getResultList();
	}

	public List<CaixaTitulo> listaCaixaBaixaTitulos(CondicaoFilter filtro) {
		StringBuilder condicao = new StringBuilder();
		condicao.append("select i from CaixaTitulo i inner join i.caixa c join c.diario d ");
		condicao.append("where d.origem = " + seguranca.getPessoaLogadoOrigem().getId());

		if (filtro.getPagamentoInicial() != null)
			condicao.append(
					" 	and cast(d.dataMov as date) >= " + Suporte.formataDataSQL_Quoted(filtro.getPagamentoInicial()));
		if (filtro.getPagamentoFinal() != null)
			condicao.append(
					" 	and cast(d.dataMov as date) <= " + Suporte.formataDataSQL_Quoted(filtro.getPagamentoFinal()));

		if (filtro.getPrevisaoInicial() != null)
			condicao.append(" 	and cast(i.titulo.previsao as date) >= "
					+ Suporte.formataDataSQL_Quoted(filtro.getPrevisaoInicial()));
		if (filtro.getPrevisaoFinal() != null)
			condicao.append(" 	and cast(i.titulo.previsao as date) <= "
					+ Suporte.formataDataSQL_Quoted(filtro.getPrevisaoFinal()));

		if (filtro.getVencimentoInicial() != null)
			condicao.append(" 	and cast(i.titulo.vencimento as date) >= "
					+ Suporte.formataDataSQL_Quoted(filtro.getVencimentoInicial()));
		if (filtro.getVencimentoFinal() != null)
			condicao.append(" 	and cast(i.titulo.vencimento as date) <= "
					+ Suporte.formataDataSQL_Quoted(filtro.getVencimentoFinal()));

		if (filtro.getTipoDC() != null && filtro.getTipoDC().length > 0) {
			String condTipo = "";
			for (TipoTituloOrigem tipo : filtro.getTipoDC()) {
				if (condTipo != "")
					condTipo = condTipo + ", ";
				condTipo = condTipo + "'" + tipo + "'";
			}
			if (condTipo != "")
				condicao.append(" and c.tipoDC in (" + condTipo + ") ");
		}

		if (filtro.getOrdem() != null && filtro.getOrdem().length() > 0)
			condicao.append(" order by " + filtro.getOrdem());
		else
			condicao.append(" order by c.id, i.id ");

		return manager.createQuery(condicao.toString(), CaixaTitulo.class).getResultList();
	}

	@Transactional
	public Titulo compensarChequeEmitido(Titulo cheque) {

		if (cheque.getDataBaixa() == null)
			throw new NegocioException("defina data para compensação");

		Titulo titulo = manager.find(Titulo.class, cheque.getId());
		if (titulo.getDataBaixa() != null)
			throw new NegocioException("documento com baixa realizada em "
					+ SuporteData.formataDataToStr(titulo.getDataBaixa(), "dd/MM/yyyy"));

		ContaCorrente conta = cheque.getContaBancaria();
		conta.setSaldo(conta.getSaldo() - cheque.getValor());
		manager.merge(conta);
		manager.flush();

		return manager.merge(cheque);
	}

	public CaixaValor chequePorCaixaValor(Titulo cheque) {
		try {
			String condicao = "from CaixaValor c where c.caixa.estorno is null"
					+ " and c.titulo = ".concat(String.valueOf(cheque.getId()));
			return manager.createQuery(condicao, CaixaValor.class).getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}

}