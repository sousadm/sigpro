package com.sousa.sigpro.repository;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.jrimum.domkee.financeiro.banco.febraban.TipoDeTitulo;

import com.fincatto.documentofiscal.DFAmbiente;
import com.sousa.sigpro.model.Agente;
import com.sousa.sigpro.model.Aquisicao;
import com.sousa.sigpro.model.BoletoDigital;
import com.sousa.sigpro.model.Cartao;
import com.sousa.sigpro.model.ContaCorrente;
import com.sousa.sigpro.model.ContratoAdesao;
import com.sousa.sigpro.model.Expedicao;
import com.sousa.sigpro.model.ExpedicaoItem;
import com.sousa.sigpro.model.Pessoa;
import com.sousa.sigpro.model.Producao;
import com.sousa.sigpro.model.Projeto;
import com.sousa.sigpro.model.Titulo;
import com.sousa.sigpro.model.tipo.TipoRepeticao;
import com.sousa.sigpro.model.tipo.TipoSituacaoFinanceira;
import com.sousa.sigpro.model.tipo.TipoTituloCaracteristica;
import com.sousa.sigpro.model.tipo.TipoTituloOrigem;
import com.sousa.sigpro.repository.filter.CondicaoFilter;
import com.sousa.sigpro.security.Seguranca;
import com.sousa.sigpro.service.CobrancaAsaasService.CobraBoleto;
import com.sousa.sigpro.service.NegocioException;
import com.sousa.sigpro.util.Suporte;
import com.sousa.sigpro.util.SuporteData;
import com.sousa.sigpro.util.jpa.Transactional;

public class Titulos implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager manager;
	@Inject
	private Seguranca seguranca;
	@Inject
	private Producoes producoes;

	@Transactional
	public Titulo guardar(Titulo titulo) {
		Titulo res = manager.merge(titulo);
		return res;
	}

	public Titulo porId(Long id) {
		return manager.find(Titulo.class, id);
	}

	public BoletoDigital boletoDigitalporId(Long id) {
		return manager.find(BoletoDigital.class, id);
	}

	public BoletoDigital boletoDigitalporId(String identificador, Agente agente, DFAmbiente ambiente) {
		try {
			return manager.createNamedQuery("BoletoDigital.porIdAmbiente", BoletoDigital.class)
					.setParameter("agente", agente).setParameter("ambiente", ambiente)
					.setParameter("identificador", identificador).getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}

	public List<BoletoDigital> listaBoletoDigital(Titulo titulo) {
		try {
			return manager.createNamedQuery("BoletoDigital.listaPorTitulo", BoletoDigital.class)
					.setParameter("ambiente", titulo.getAgente().getAmbiente()).setParameter("titulo", titulo)
					.getResultList();
		} catch (Exception e) {
			return null;
		}
	}

	public List<BoletoDigital> listaBoletoDigital(Expedicao expedicao) {
		return manager.createNamedQuery("BoletoDigital.listaPorExpedicao", BoletoDigital.class)
				.setParameter("expedicao", expedicao).getResultList();
	}

	@Transactional
	public Titulo gravarComissao(Titulo titulo, List<ExpedicaoItem> lista) {

		titulo.setEmissao(new Date());
		titulo.setVencimento(titulo.getPrevisao());
		titulo = manager.merge(titulo);
		manager.flush();

		for (ExpedicaoItem item : lista) {
			Producao producao = producoes.ultimoPorItem(item);
			producao.setTitulo(titulo);
			manager.merge(producao);
			manager.flush();

			item.setDataComissao(titulo.getEmissao());
			item.setValorComissao(item.getValorComissao());
			manager.merge(item);
			manager.flush();
		}

		return titulo;
	}

	public Titulo faturaCartao(Cartao cartao, Date vencimento) {
		try {
			StringBuilder condicao = new StringBuilder();
			condicao.append("select t from Titulo t where t.cartao = " + cartao.getId());
			condicao.append("	and cast(t.vencimento as date) = " + Suporte.formataDataSQL_Quoted(vencimento));
			return manager.createQuery(condicao.toString(), Titulo.class).getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}

	public List<Titulo> lista(Cartao cartao) {
		return manager.createNamedQuery("Titulo.listaParcelaOrigem", Titulo.class).setParameter("cartao", cartao)
				.getResultList();
	}

	public List<Titulo> lista(Titulo origem) {
		return manager.createNamedQuery("Titulo.listaPorOrigem", Titulo.class).setParameter("origem", origem)
				.getResultList();
	}

	public List<Titulo> lista(Projeto projeto) {
		return manager.createNamedQuery("Titulo.listaPorProjeto", Titulo.class).setParameter("projeto", projeto)
				.getResultList();
	}

	public List<Titulo> lista(ContratoAdesao contrato) {
		return manager.createNamedQuery("Titulo.listaPorContratoAdesao", Titulo.class)
				.setParameter("contrato", contrato).getResultList();
	}

	@Transactional
	public Titulo inativar(Titulo titulo) {
		titulo.setDataCancelamento(new Date());
		titulo.setDesconto(titulo.getSaldo());
		titulo.setSaldo(0);
		return manager.merge(titulo);
	}

	@Transactional
	public Titulo atualizaPorCartao(Titulo titulo) {

		titulo = porId(titulo.getId());

		double valor = 0;
		String condicao = "select SUM(valor) from Titulo where origem = " + titulo.getId();
		try {
			valor = manager.createQuery(condicao, Double.class).getSingleResult();
		} catch (Exception e) {
			valor = 0;
		}
		titulo.setValor(valor);

		return manager.merge(titulo);

	}

	public Boolean existeNoCaixa(Titulo titulo) {

		Query query;
		String condicao;
		Long tt;

		try {
			condicao = "select count(*) from CaixaTitulo c where c.titulo = " + titulo.getId();
			query = manager.createQuery(condicao);
			tt = (Long) query.getSingleResult();

			if (tt == 0) {
				condicao = "select count(*) from CaixaValor c where c.titulo = " + titulo.getId();
				query = manager.createQuery(condicao);
				tt = (Long) query.getSingleResult();
			}

			return tt > 0;
		} catch (Exception e) {
			return false;
		}
	}

	public List<Titulo> porExpedicaoEmAberto(Long expedicao) {
		return manager.createNamedQuery("Titulo.listaPorExpedicaoAberto", Titulo.class)
				.setParameter("agente", seguranca.getPessoaLogado().getAgente()).setParameter("expedicao", expedicao)
				.getResultList();
	}

	public List<Titulo> lista(Expedicao expedicao) {
		return manager.createNamedQuery("Titulo.listaPorExpedicao", Titulo.class).setParameter("expedicao", expedicao)
				.getResultList();
	}

	public List<Titulo> lista(Aquisicao aquisicao) {
		return manager.createNamedQuery("Titulo.listaPorAquisicao", Titulo.class).setParameter("aquisicao", aquisicao)
				.getResultList();
	}

	@Transactional
	public void remover(Titulo titulo) throws NegocioException {
		try {
			titulo = porId(titulo.getId());
			manager.remove(titulo);
			manager.flush();
		} catch (PersistenceException e) {
			throw new NegocioException("registro não pode ser excluído.");
		} catch (Exception e) {
			throw new NegocioException(e.getMessage());
		}
	}

	public Titulo guardarSemCommit(Titulo titulo) {
		return manager.merge(titulo);
	}

	private String consulta(CondicaoFilter filtro) {
		StringBuilder condicao = new StringBuilder();
		condicao.append(" WHERE dataCancelamento is null ");
		condicao.append(" 	AND tipoDC <> " + Suporte.getQuotedStr(TipoTituloOrigem.COMPENSA.name()));

		if (filtro.getCartao() != null)
			condicao.append(" AND cartao = " + filtro.getCartao().getId());

		if (filtro.getNome() != null && filtro.getNome().length() > 0)
			condicao.append(" and UPPER(nominal) like UPPER('%" + filtro.getNome() + "%') ");

		if (filtro.getDescricao() != null && filtro.getDescricao().length() > 0)
			condicao.append(" and UPPER(descricao) like UPPER('%" + filtro.getDescricao() + "%') ");

		if (Suporte.dataIsValida(filtro.getVencimentoInicial()))
			condicao.append(" and cast(vencimento as date) >= '" + Suporte.formataDataSQL(filtro.getVencimentoInicial())
					+ "' ");

		if (Suporte.dataIsValida(filtro.getVencimentoFinal()))
			condicao.append(
					" and cast(vencimento as date) <= '" + Suporte.formataDataSQL(filtro.getVencimentoFinal()) + "' ");

		if (Suporte.dataIsValida(filtro.getPrevisaoInicial()))
			condicao.append(" and cast(coalesce(previsao, vencimento) as date) >= '"
					+ Suporte.formataDataSQL(filtro.getPrevisaoInicial()) + "' ");

		if (Suporte.dataIsValida(filtro.getPrevisaoFinal()))
			condicao.append(" and cast(coalesce(previsao, vencimento) as date) <= '"
					+ Suporte.formataDataSQL(filtro.getPrevisaoFinal()) + "' ");

		if (Suporte.dataIsValida(filtro.getEmissaoInicial()))
			condicao.append(
					" and cast(emissao as date) >= '" + Suporte.formataDataSQL(filtro.getEmissaoInicial()) + "' ");

		if (Suporte.dataIsValida(filtro.getEmissaoFinal()))
			condicao.append(
					" and cast(emissao as date) <= '" + Suporte.formataDataSQL(filtro.getEmissaoFinal()) + "' ");

		if (filtro.getTituloStatus() != null) {
			if (filtro.getTituloStatus() == TipoSituacaoFinanceira.ABERTO)
				condicao.append(" and saldo > 0.009 AND dataBaixa IS NULL ");
			else if (filtro.getTituloStatus() == TipoSituacaoFinanceira.VENCIDO)
				condicao.append(
						" AND (saldo > 0)  AND (CURRENT_DATE > cast(vencimento as date)) and dataBaixa IS NULL ");
			else if (filtro.getTituloStatus() == TipoSituacaoFinanceira.LIQUIDADO)
				condicao.append(" AND saldo <= 0 ");
		}

		if (filtro.getTipoDC() != null && filtro.getTipoDC().length > 0) {
			String condTipo = "";
			for (TipoTituloOrigem tipo : filtro.getTipoDC()) {
				if (condTipo != "")
					condTipo += ", ";
				condTipo += "'" + tipo + "'";
			}
			if (condTipo != "")
				condicao.append(" and tipoDC in (" + condTipo + ") ");
		}

		if (filtro.getContaCorrente() != null)
			condicao.append(" AND contaBancaria = " + filtro.getContaCorrente().getId());

		if (filtro.getContas() != null && filtro.getContas().length > 0) {
			String condConta = "";
			for (ContaCorrente ct : filtro.getContas()) {
				if (condConta != "")
					condConta += ", ";
				condConta += String.valueOf(ct.getId());
			}
			if (condConta != "")
				condicao.append(" and contaBancaria in (" + condConta + ") ");
		}

		if (filtro.getSql() != null && filtro.getSql().length() > 0)
			condicao.append(" and " + filtro.getSql() + " ");

		return condicao.toString();

	}

	public Titulo porCartaoVencimento(Cartao cartao, Date vencimento) {

		Titulo titulo = null;
		DateFormat formatter = new SimpleDateFormat("MMyyyy");
		String documento = formatter.format(vencimento);

		try {
			String condicao = "from Titulo where documento = '" + documento + "' and cartao = " + cartao.getId();
			titulo = manager.createQuery(condicao, Titulo.class).getSingleResult();
		} catch (Exception e) {
			if (titulo == null) {
				titulo = new Titulo();
				titulo.setCartao(cartao);
				titulo.setEmissao(new Date());
				titulo.setDescricao("CARTAO DE CRÉDITO " + cartao.getNumero());
				titulo.setNominal(cartao.getNome());
				titulo.setDocumento(documento);
				titulo.setVencimento(vencimento);
				titulo.setPrevisao(vencimento);
				titulo.setTipoDocto(TipoDeTitulo.NCC_NOTA_DE_CREDITO_COMERCIAL);
				titulo.setTipoDC(TipoTituloOrigem.PAGAR);
				titulo.setResponsavel(seguranca.getPessoaLogadoOrigem());
			}
		}

		return titulo;
	}

	public int quantidadeFiltrados(CondicaoFilter filtro) {
		StringBuilder condicao = new StringBuilder();
		condicao.append("select count(*) from Titulo t ");
		condicao.append(consulta(filtro));
		return ((Number) manager.createQuery(condicao.toString()).getSingleResult()).intValue();
	}

	public Titulo chequePorNumero(String numero, ContaCorrente conta) {
		try {
			String condicao = "from Titulo where documento = :documento and contaBancaria = :conta";
			return manager.createQuery(condicao, Titulo.class).setParameter("documento", numero)
					.setParameter("conta", conta).getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}

	@Transactional
	public Titulo gerarProximoTitulo(Titulo titulo) {

		Calendar atual = Calendar.getInstance();
		atual.setTime(titulo.getVencimento());
		if (titulo.getRepete() == TipoRepeticao.DIA)
			atual.add(Calendar.DATE, 1);
		else if (titulo.getRepete() == TipoRepeticao.SEM)
			atual.add(Calendar.DATE, 7);
		else if (titulo.getRepete() == TipoRepeticao.QUI)
			atual.add(Calendar.DATE, 15);
		else if (titulo.getRepete() == TipoRepeticao.MES)
			atual.add(Calendar.MONTH, 1);
		else if (titulo.getRepete() == TipoRepeticao.BIM)
			atual.add(Calendar.MONTH, 2);
		else if (titulo.getRepete() == TipoRepeticao.TRI)
			atual.add(Calendar.MONTH, 3);
		else if (titulo.getRepete() == TipoRepeticao.SMT)
			atual.add(Calendar.MONTH, 6);
		else if (titulo.getRepete() == TipoRepeticao.ANO)
			atual.add(Calendar.MONTH, 12);

		Titulo proximo = new Titulo(titulo);
		proximo.setOrigem(titulo);
		proximo.setVencimento(atual.getTime());
		proximo.setPrevisao(atual.getTime());
		proximo.setSaldo(titulo.getValor());

		return manager.merge(proximo);

	}

	@Transactional
	public BoletoDigital guardar(Titulo titulo, Agente agente, CobraBoleto boleto) {

		titulo.setDocumento(boleto.id);
		titulo.setValor(boleto.value);
		titulo.setSaldo(boleto.netValue);
		titulo.setDesconto(boleto.interestValue - boleto.netValue);
		titulo = manager.merge(titulo);
		manager.flush();

		BoletoDigital boletoDigital = new BoletoDigital();
		boletoDigital.setTitulo(titulo);
		boletoDigital.setAgente(agente);
		boletoDigital.setAmbiente(agente.getAmbiente());
		boletoDigital.setDataCadastro(boleto.dateCreated);
		boletoDigital.setIdentificador(boleto.id);
		boletoDigital.setNumeroDaFatura(boleto.invoiceNumber);
		boletoDigital.setStatus(boleto.status);
		boletoDigital.setUrlDaFatura(boleto.invoiceUrl);
		boletoDigital.setUrlDoArquivo(boleto.bankSlipUrl);

		return manager.merge(boletoDigital);
	}

	@Transactional
	public BoletoDigital compensar(BoletoDigital boletoDigital, Pessoa responsavel) {

		Agente agenteOrigem = boletoDigital.getTitulo().getAgente();

		Titulo titulo = boletoDigital.getTitulo();
		titulo.setAgente(boletoDigital.getAgente());
		titulo.setDataRemessa(new Date());
		boletoDigital.setTitulo(manager.merge(titulo));
		manager.flush();

		Date vencimento = SuporteData.incrementaDiaNaData(titulo.getDataBaixa(),
				boletoDigital.getAgente().getTempoParaCompensacao());

		Titulo compensa = new Titulo();
		compensa.setOrigem(titulo);
		compensa.setResponsavel(responsavel);
		compensa.setVencimento(vencimento);
		compensa.setEmissao(new Date());
		compensa.setTipoDC(TipoTituloOrigem.RECEBER);
		compensa.setTipoDocto(TipoDeTitulo.DM_DUPLICATA_MERCANTIL);
		compensa.setRepete(TipoRepeticao.NAO);
		compensa.setCaracteristica(TipoTituloCaracteristica.SIMPLES);
		compensa.setDescricao(titulo.getResponsavel().getNome() + " / " + titulo.getDescricao());
		compensa.setDocumento(boletoDigital.getIdentificador());
		compensa.setValor(titulo.getValor() - titulo.getDesconto());
		compensa.setSaldo(compensa.getValor());
		compensa.setAgente(agenteOrigem);
		compensa = manager.merge(compensa);
		manager.flush();

		return manager.merge(boletoDigital);

	}

	@Transactional
	public BoletoDigital guardar(BoletoDigital boletoDigital) {

		Titulo titulo = boletoDigital.getTitulo();

		Pessoa responsavel = titulo.getResponsavel();
		responsavel = manager.merge(responsavel);
		manager.flush();

		titulo.setResponsavel(responsavel);
		titulo = manager.merge(titulo);

		boletoDigital.setTitulo(titulo);
		manager.flush();

		return manager.merge(boletoDigital);
	}

	@SuppressWarnings("unchecked")
	public List<Titulo> lista(CondicaoFilter filtro) {

		StringBuilder condicao = new StringBuilder();
		condicao.append("select t from Titulo t ");
		condicao.append(consulta(filtro));
		if (filtro.getSortField() != null)
			condicao.append(" ORDER BY t." + filtro.getSortField() + (filtro.isAscendente() ? "" : " desc"));

		Query query = manager.createQuery(condicao.toString(), Titulo.class);
		if (filtro.getPrimeiroRegistro() != null) {
			query.setFirstResult(filtro.getPrimeiroRegistro().intValue());
			query.setMaxResults(filtro.getQuantidadeRegistros().intValue());
		}

		return query.getResultList();
	}

	@Transactional
	public void transferirTituloCartao(Titulo[] selecionados, Titulo destino) {

		Titulo origem = this.porId(selecionados[0].getOrigem().getId());
		destino = this.porId(destino.getId());

		for (Titulo titulo : selecionados) {

			titulo.setOrigem(destino);
			titulo = guardar(titulo);

			origem.addValor(-titulo.getValor());
			destino.addValor(titulo.getValor());

		}

		origem = guardar(origem);
		destino = guardar(destino);

	}

}