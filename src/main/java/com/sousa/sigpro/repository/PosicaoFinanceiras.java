package com.sousa.sigpro.repository;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.sousa.sigpro.model.Pessoa;
import com.sousa.sigpro.model.PosicaoFinanceira;
import com.sousa.sigpro.model.PosicaoFinanceiraDetalhe;
import com.sousa.sigpro.model.tipo.TipoPagamento;
import com.sousa.sigpro.security.Seguranca;
import com.sousa.sigpro.service.NegocioException;
import com.sousa.sigpro.util.Suporte;
import com.sousa.sigpro.util.SuporteData;

public class PosicaoFinanceiras implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager manager;
	@Inject
	private Seguranca seguranca;

	private String consulta(Pessoa pessoa, Date inicio, Date termino) {

		StringBuilder condicao = new StringBuilder();
		condicao.append("select cast(d.dataMov as date), c.id,  c.observacao ");
		condicao.append("	,v.valor * case when (c.tipoDC = 'PAGAR') or (v.tipoPagamento = "
				+ Suporte.getQuotedStr(TipoPagamento.CAIXA.name()) + ") then -1 else 1 end as valor ");
		condicao.append("from " + seguranca.getEmpresa() + ".caixa c ");
		condicao.append("inner join " + seguranca.getEmpresa() + ".caixavalor v on v.caixa_id = c.id ");
		condicao.append("inner join " + seguranca.getEmpresa() + ".diario d on d.id = c.diario_id ");
		condicao.append("where c.tipoDC <> 'COMPENSA' ");
		condicao.append(" and c.pessoa_id = " + pessoa.getId());
		if (inicio != null)
			condicao.append(" and cast(d.dataMov as date) >= " + Suporte.formataDataSQL_Quoted(inicio));
		if (termino != null)
			condicao.append(" and cast(d.dataMov as date) <= " + Suporte.formataDataSQL_Quoted(termino));

		if (pessoa.getCliente() != null && pessoa.getCliente().getId() != null) {
			condicao.append("union all ");
			condicao.append("select cast(e.data_pedido as date), e.id, 'venda de produto / serviços', - e.valorTotal as valor ");
			condicao.append("from " + seguranca.getEmpresa() + ".expedicao e ");
			condicao.append("inner join " + seguranca.getEmpresa() + ".cliente c on c.id = e.cliente_id ");
			condicao.append("where e.dataCancelamento is null ");
			condicao.append(" 	and e.cliente_id = " + pessoa.getCliente().getId());
			if (inicio != null)
				condicao.append(" and cast(e.data_pedido as date) >= " + Suporte.formataDataSQL_Quoted(inicio));
			if (termino != null)
				condicao.append(" and cast(e.data_pedido as date) <= " + Suporte.formataDataSQL_Quoted(termino));
		}

		return condicao.toString();
	}

	@SuppressWarnings("unchecked")
	public void prepara(PosicaoFinanceira posicao) {

		Query query;
		List<Object[]> lst;
		PosicaoFinanceiraDetalhe detalhe;
		StringBuilder condicao;
		double saldoInicial = 0;
		double valorEntrada = 0;
		double valorSaida = 0;
		double saldoFinal = 0;

		try {

			if (posicao.getPessoa() == null)
				throw new NegocioException("Pessoa indefinida");

			Suporte.validaPeriodo(posicao.getInicio(), posicao.getTermino());
			if (posicao.getInicio() == null && posicao.getTermino() == null)
				throw new NegocioException("Período indefinido");

			posicao.setDetalhes(new ArrayList<>());

			condicao = new StringBuilder();
			condicao.append("select * from (" + consulta(posicao.getPessoa(), posicao.getInicio(), posicao.getTermino())
					+ ") t ");
			condicao.append(" order by 1 ");
			query = manager.createNativeQuery(condicao.toString());
			lst = query.getResultList();
			for (Object[] obj : lst) {
				detalhe = new PosicaoFinanceiraDetalhe();
				detalhe.setData((Date) obj[0]);
				detalhe.setDocumento(String.valueOf((BigInteger) obj[1]));
				detalhe.setMemorando((String) obj[2]);
				detalhe.setValor((double) obj[3]);
				posicao.getDetalhes().add(detalhe);
				if (detalhe.getValor() < 0)
					valorSaida = valorSaida - detalhe.getValor();
				else
					valorEntrada = valorEntrada + detalhe.getValor();
			}

			condicao = new StringBuilder();
			Date inicio = SuporteData.incrementaDiaNaData(posicao.getInicio(), -1);
			condicao.append("select sum(t.valor) from (" + consulta(posicao.getPessoa(), null, inicio) + ") t ");
			query = manager.createNativeQuery(condicao.toString());
			try {
				saldoInicial = (Double) query.getSingleResult();
			} catch (Exception e) {
			}

			condicao = new StringBuilder();
			condicao.append(
					"select sum(t.valor) from (" + consulta(posicao.getPessoa(), null, posicao.getTermino()) + ") t ");
			query = manager.createNativeQuery(condicao.toString());
			try {
				saldoFinal = (Double) query.getSingleResult();
			} catch (Exception e) {
			}

			posicao.setSaldoInicial(saldoInicial);
			posicao.setEntrada(Suporte.valorAbsoluto(valorEntrada));
			posicao.setSaida(Suporte.valorAbsoluto(valorSaida));
			posicao.setSaldoAtual(saldoFinal);

		} catch (Exception e) {
			throw new NegocioException(e.getCause().getMessage());
		}
	}

}