package com.sousa.sigpro.repository;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import com.sousa.sigpro.model.Banco;
import com.sousa.sigpro.model.ContaCorrente;
import com.sousa.sigpro.model.Pessoa;
import com.sousa.sigpro.model.Titulo;
import com.sousa.sigpro.model.tipo.TipoTituloOrigem;
import com.sousa.sigpro.security.Seguranca;
import com.sousa.sigpro.service.NegocioException;
import com.sousa.sigpro.util.Suporte;
import com.sousa.sigpro.util.SuporteData;
import com.sousa.sigpro.util.jpa.Transactional;

public class Contas implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Seguranca seguranca;
	@Inject
	private EntityManager manager;
	@Inject
	private Bancos bancos;

	public ContaCorrente porId(Long id) {
		return manager.find(ContaCorrente.class, id);
	}

	public List<ContaCorrente> listaParaBoleto() {
		try {
			StringBuilder condicao = new StringBuilder();
			condicao.append("from ContaCorrente c where c.tipoBoleto is not null");
			return manager.createQuery(condicao.toString(), ContaCorrente.class).getResultList();
		} catch (Exception e) {
			return null;
		}
	}

	@Transactional
	public ContaCorrente atualiza(ContaCorrente conta, double valor) {
		conta = porId(conta.getId());
		conta.setSaldo(conta.getSaldo() + valor);
		return manager.merge(conta);
	}

	@Transactional
	public void remover(ContaCorrente conta) {
		conta = porId(conta.getId());
		manager.remove(conta);
		manager.flush();
	}

	public List<ContaCorrente> listaContaCheque() {
		return manager.createNamedQuery("Conta.listaContaCheque", ContaCorrente.class)
				.setParameter("origem", seguranca.getPessoaLogadoOrigem()).getResultList();
	}

	public List<ContaCorrente> listaContaDebito() {
		return manager.createNamedQuery("Conta.listaContaDebito", ContaCorrente.class)
				.setParameter("origem", seguranca.getPessoaLogadoOrigem()).getResultList();
	}

	@Transactional
	public ContaCorrente cancelar(ContaCorrente conta) {
		conta.setDataCancelamento(new Date());
		return manager.merge(conta);
	}

	public List<Titulo> conciliacao(ContaCorrente conta, Date inicio, Date termino) {
		String condicao = "select t from Titulo t where contaBancaria = " + conta.getId();
		if (inicio != null)
			condicao += " and cast(dataBaixa as date) >= '" + Suporte.formataDataSQL(inicio) + "' ";

		if (termino != null)
			condicao += " and cast(dataBaixa as date) <= '" + Suporte.formataDataSQL(termino) + "' ";

		condicao += " order by dataBaixa, id";
		return manager.createQuery(condicao, Titulo.class).getResultList();
	}

	public double saldoInicialEmConta(ContaCorrente conta, Date data) {

		double valor = 0;
		double saldo = 0;
		StringBuilder condicao = new StringBuilder();

		condicao.append("select coalesce(sum(case when tipoDC = 'RECEBER' then 1 else -1 end * valor"
				+ " - case when tipoDC = 'PAGAR' then 1 else 0 end * valor), 0) ");
		condicao.append("from Titulo t where tipoDC <> 'COMPENSA' ");
		if (conta != null) {
			saldo = conta.getSaldoInicial();
			condicao.append("	and contaBancaria = " + conta.getId());

			if (conta.getDataInicio() != null)
				condicao.append(
						"	and cast(dataBaixa as date) >= " + Suporte.formataDataSQL_Quoted(conta.getDataInicio()));
		}

		if (data != null)
			condicao.append(" 	and cast(dataBaixa as date) < " + Suporte.formataDataSQL_Quoted(data));

		try {
			valor = manager.createQuery(condicao.toString(), Double.class).getSingleResult();
		} catch (Exception e) {
			valor = 0;
		}

		return saldo + valor;
	}

	public double saldoMovimentoEmConta(ContaCorrente conta, Date inicio, Date termino) {

		double valor;

		StringBuilder condicao = new StringBuilder();
		condicao.append("select coalesce(sum(case when tipoDC = 'RECEBER' then 1 else 0 end * valor"
				+ " - case when tipoDC = 'PAGAR' then 1 else 0 end * valor), 0) ");
		condicao.append("from Titulo t where tipoDC <> 'COMPENSA' and contaBancaria = " + conta.getId());
		if (inicio != null)
			condicao.append(" 	and cast(dataBaixa as date) > " + Suporte.formataDataSQL_Quoted(inicio));
		if (termino != null)
			condicao.append(" 	and cast(dataBaixa as date) <= " + Suporte.formataDataSQL_Quoted(termino));

		try {
			valor = manager.createQuery(condicao.toString(), Double.class).getSingleResult();
		} catch (Exception e) {
			valor = 0;
		}

		return valor;
	}

	public double saldoMovimentoEmContaOrigem(Pessoa origem, Date inicio, Date termino) {

		double valor;

		StringBuilder condicao = new StringBuilder();
		condicao.append("select coalesce(sum(case when tipoDC = 'RECEBER' then 1 else 0 end * valor"
				+ " - case when tipoDC = 'PAGAR' then 1 else 0 end * valor), 0) ");
		condicao.append("from Titulo t where tipoDC <> 'COMPENSA' and contaBancaria.origem = " + origem.getId());
		if (inicio != null)
			condicao.append(" 	and cast(dataBaixa as date) > " + Suporte.formataDataSQL_Quoted(inicio));
		if (termino != null)
			condicao.append(" 	and cast(dataBaixa as date) <= " + Suporte.formataDataSQL_Quoted(termino));

		try {
			valor = manager.createQuery(condicao.toString(), Double.class).getSingleResult();
		} catch (Exception e) {
			valor = 0;
		}

		return valor;
	}

	@Transactional
	public ContaCorrente ajustarSaldoNaData(ContaCorrente conta, Titulo titulo) {

		double valor = saldoInicialEmConta(conta, SuporteData.incrementaDiaNaData(titulo.getDataBaixa(), 1));
		if (valor < 0 || valor > 0) {
			Titulo zera = new Titulo();
			zera.setResponsavel(titulo.getResponsavel());
			zera.setDataBaixa(titulo.getDataBaixa());
			zera.setNominal(titulo.getResponsavel().getNome());
			zera.setContaBancaria(conta);
			zera.setValor(Suporte.valorAbsoluto(valor));
			zera.setPrevisao(titulo.getDataBaixa());
			zera.setVencimento(titulo.getDataBaixa());
			zera.setTipoDC(valor >= 0 ? TipoTituloOrigem.PAGAR : TipoTituloOrigem.RECEBER);
			zera.setResponsavel(seguranca.getPessoaLogado());
			zera.setNominal(titulo.getResponsavel().getNome());
			zera.setContaBancaria(conta);
			zera.setDescricao("zera conta na data");
			zera = manager.merge(zera);
		}

		if (titulo.getValor() > 0) {
			titulo.setTipoDC(TipoTituloOrigem.RECEBER);
			titulo.setPrevisao(titulo.getDataBaixa());
			titulo.setVencimento(titulo.getDataBaixa());
			titulo = manager.merge(titulo);
		}

		conta.setSaldo(titulo.getValor());
		return manager.merge(conta);

	}

	public List<ContaCorrente> lista() {
		return manager.createNamedQuery("Conta.lista", ContaCorrente.class)
				.setParameter("origem", seguranca.getPessoaLogadoOrigem()).getResultList();
	}

	@Transactional
	public ContaCorrente guardar(ContaCorrente conta) {

		StringBuilder condicao;
		double valorRecebido = 0;
		double valorPago = 0;

//		if (conta.getDataCancelamento() != null)
//			throw new NegocioException("conta já está cancelada");

		if (conta.getOrigem() == null)
			conta.setOrigem(seguranca.getPessoaLogadoOrigem());

		if (conta.getNumero() <= 0)
			throw new NegocioException("Número inválido para conta.");

		if (conta.getAgencia() <= 0)
			throw new NegocioException("Número inválido para conta.");

		if (conta.getBanco() == null)
			throw new NegocioException("banco indefinido");

		try {
			condicao = new StringBuilder();
			condicao.append("select SUM(valor) from Titulo where tipoDC = 'PAGAR' ");
			condicao.append("	and contaBancaria = " + conta.getId());
			condicao.append(
					"	and cast(dataBaixa as date) >= " + Suporte.formataDataSQL_Quoted(conta.getDataInicio()));
			valorPago = manager.createQuery(condicao.toString(), Double.class).getSingleResult();
		} catch (Exception e) {
			valorPago = 0;
		}

		try {
			condicao = new StringBuilder();
			condicao.append("select SUM(valor) from Titulo where tipoDC = 'RECEBER' ");
			condicao.append("	and contaBancaria = " + conta.getId());
			condicao.append(
					"	and cast(dataBaixa as date) >= " + Suporte.formataDataSQL_Quoted(conta.getDataInicio()));
			valorRecebido = manager.createQuery(condicao.toString(), Double.class).getSingleResult();
		} catch (Exception e) {
			valorRecebido = 0;
		}

		Banco banco = bancos.porCodigo(conta.getBanco().getCodigo());
		if (banco == null) {
			banco = manager.merge(conta.getBanco());
			manager.flush();
			conta.setBanco(banco);
		} else {
			banco = manager.find(Banco.class, banco.getId());
			conta.setBanco(banco);
		}

		conta.setSaldo(conta.getSaldoInicial() + valorRecebido - valorPago);

		return manager.merge(conta);

	}

	public double valorEntradaPorOrigem(Pessoa pessoa, Date inicio, Date termino) {
		return valorMovimentacaoPorOrigem(pessoa, TipoTituloOrigem.RECEBER, inicio, termino);
	}

	public double valorSaidaPorOrigem(Pessoa pessoa, Date inicio, Date termino) {
		return valorMovimentacaoPorOrigem(pessoa, TipoTituloOrigem.PAGAR, inicio, termino);
	}

	private double valorMovimentacaoPorOrigem(Pessoa pessoa, TipoTituloOrigem tipo, Date inicio, Date termino) {

		double valorRecebido = 0;

		try {
			StringBuilder condicao = new StringBuilder();
			condicao.append("select SUM(valor) from Titulo t where tipoDC = " + Suporte.getQuotedStr(tipo.name()));
			condicao.append("	and t.contaBancaria.origem = " + pessoa.getId());
			condicao.append("	and cast(dataBaixa as date) > " + Suporte.formataDataSQL_Quoted(inicio));
			condicao.append("	and cast(dataBaixa as date) <= " + Suporte.formataDataSQL_Quoted(termino));
			valorRecebido = manager.createQuery(condicao.toString(), Double.class).getSingleResult();
		} catch (Exception e) {
			valorRecebido = 0;
		}

		return valorRecebido;
	}
}