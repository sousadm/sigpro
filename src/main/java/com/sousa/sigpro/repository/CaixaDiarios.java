package com.sousa.sigpro.repository;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.sousa.sigpro.model.Caixa;
import com.sousa.sigpro.model.CaixaDiario;
import com.sousa.sigpro.model.Movimento;
import com.sousa.sigpro.model.tipo.TipoMes;
import com.sousa.sigpro.model.tipo.TipoPagamento;
import com.sousa.sigpro.model.tipo.TipoTituloOrigem;
import com.sousa.sigpro.repository.filter.CondicaoFilter;
import com.sousa.sigpro.security.Seguranca;
import com.sousa.sigpro.service.NegocioException;
import com.sousa.sigpro.util.Suporte;
import com.sousa.sigpro.util.SuporteData;
import com.sousa.sigpro.util.jpa.Transactional;

public class CaixaDiarios implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager manager;
	@Inject
	private Seguranca seguranca;
	@Inject
	private Contas contas;

	public CaixaDiario porId(Long id) {
		return manager.find(CaixaDiario.class, id);
	}

	@Transactional
	public void reabrirAPartirDe(CaixaDiario diario) {
		String sql = "UPDATE diario SET fechamento = NULL WHERE id >= " + diario.getId();
		Query query = manager.createQuery(sql);
		query.executeUpdate();
	}

	@Transactional
	public CaixaDiario guardar(CaixaDiario diario) {
		return manager.merge(diario);
	}

	public CaixaDiario diarioAnteriorData(Date data) {
		try {
			StringBuilder condicao = new StringBuilder();
			condicao.append("select max(id) from diario d ");
			condicao.append("where d.dataMov < " + Suporte.formataDataSQL_Quoted(data));
			condicao.append("	and d.origem = " + seguranca.getPessoaLogadoOrigem().getId());
			Query query = manager.createQuery(condicao.toString());
			Long id = (Long) query.getSingleResult();
			return this.porId(id);
		} catch (Exception e) {
			return null;
		}
	}

	public CaixaDiario ultimo() {
		try {
			StringBuilder condicao = new StringBuilder();
			condicao.append("select max(id) from diario d ");
			condicao.append("where d.origem = " + seguranca.getPessoaLogadoOrigem().getId());
			Query query = manager.createQuery(condicao.toString());
			Long id = (Long) query.getSingleResult();

			return this.porId(id);

		} catch (Exception e) {
			return null;
		}
	}

	public CaixaDiario abertoAnteriorData(Date data) {
		try {
			StringBuilder condicao = new StringBuilder();
			condicao.append("select max(id) from diario d ");
			condicao.append("where fechamento is null ");
			condicao.append("	and d.origem = " + seguranca.getPessoaLogadoOrigem().getId());
			condicao.append("	and d.dataMov < " + Suporte.formataDataSQL_Quoted(data));

			Query query = manager.createQuery(condicao.toString());
			Long id = (Long) query.getSingleResult();

			return this.porId(id);

		} catch (Exception e) {
			return null;
		}
	}

	public CaixaDiario fechadoPosteriorData(Date data) {
		try {
			StringBuilder condicao = new StringBuilder();
			condicao.append("select min(id) from diario d ");
			condicao.append("where fechamento is not null ");
			condicao.append("	and d.origem = " + seguranca.getPessoaLogadoOrigem().getId());
			condicao.append("	and d.dataMov > " + Suporte.formataDataSQL_Quoted(data));

			Query query = manager.createQuery(condicao.toString());
			Long id = (Long) query.getSingleResult();

			return this.porId(id);

		} catch (Exception e) {
			return null;
		}
	}

	public CaixaDiario porData(Date data) {
		try {

			StringBuilder condicao = new StringBuilder();
			condicao.append("select d from diario d ");
			condicao.append("where d.dataMov = " + Suporte.formataDataSQL_Quoted(data));
			condicao.append("	and d.origem = " + seguranca.getPessoaLogadoOrigem().getId());

			return manager.createQuery(condicao.toString(), CaixaDiario.class).getSingleResult();

		} catch (Exception e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public Map<Integer, Double> graficoFluxoCaixa(int ano, TipoMes mes, TipoTituloOrigem tipo) {

		Calendar atual = Calendar.getInstance();
		atual.set(Calendar.YEAR, ano);
		atual.set(Calendar.MONTH, mes.ordinal());
		atual.set(Calendar.DAY_OF_MONTH, 1);

		Map<Integer, Double> mapa = new TreeMap<>();
		for (int dia = SuporteData.primeiroDiaDoMes(atual.getTime()); dia <= SuporteData
				.ultimoDiaDoMes(atual.getTime()); dia++) {
			mapa.put(dia, Double.valueOf(0));
		}

		Date inicio = SuporteData.primeiraDataDoMes(atual.getTime());
		Date termino = SuporteData.ultimaDataDoMes(atual.getTime());
		String empresa = seguranca.getEmpresa().toLowerCase();

		StringBuilder condicao = new StringBuilder();
		condicao.append("SELECT cast(EXTRACT(DAY FROM t.data) as integer) dia, SUM(t.valor) ");
		condicao.append("FROM (" + consultaGrafico(empresa, inicio, termino, tipo) + ") t ");
		condicao.append(" GROUP BY 1 ORDER BY 1 ");

		Query query = manager.createNativeQuery(condicao.toString());

		List<Object[]> lstObj = (List<Object[]>) query.getResultList();
		for (Object[] obj : lstObj) {
			if (obj != null) {
				mapa.put((Integer) obj[0], Suporte.valorAbsoluto((Double) obj[1]));
			}
		}

		return mapa;
	}

	public String consultaMovimento(String empresa, Date inicio, Date termino, TipoTituloOrigem tipo) {

		StringBuilder condicao = new StringBuilder();

		condicao.append(consultaGrafico(empresa, inicio, termino, tipo));

		/********** movimento de saque no banco ****************/
		condicao.append("UNION 																");
		condicao.append("SELECT 															");
		condicao.append("	 d.datamov as data												");
		condicao.append("	,v.id as id														");
		condicao.append("	,c.id as caixa													");
		condicao.append("	,t.descricao as descricao										");
		condicao.append("	,t.nominal as nominal											");
		condicao.append("	,case when t.tipodc = 'RECEBER' then 1 							");
		condicao.append("		  when t.tipodc = 'PAGAR' then -1 else 0 end * v.valor as valor ");
		condicao.append("	,'BANCO' as pagamento											");
		condicao.append("FROM " + empresa + ".caixa c  										");
		condicao.append("INNER JOIN " + empresa + ".caixavalor v ON v.caixa_id = c.id		");
		condicao.append("INNER JOIN " + empresa + ".titulo t ON t.id = v.titulo_id 			");
		condicao.append("INNER JOIN " + empresa + ".diario d ON c.diario_id = d.id 			");
		condicao.append("WHERE t.datacancelamento is null 									");
		condicao.append("	and v.tipoPagamento = " + Suporte.getQuotedStr(TipoPagamento.SAQUE.name()));
		if (tipo != null)
			condicao.append("	and t.tipoDC = " + Suporte.getQuotedStr(tipo.name()));
		if (inicio != null)
			condicao.append("	and cast(d.datamov as date) >= " + Suporte.formataDataSQL_Quoted(inicio));
		if (termino != null)
			condicao.append("	and cast(d.datamov as date) <= " + Suporte.formataDataSQL_Quoted(termino));

		/********** outras movimentações do caixa *****************/
		condicao.append("UNION 									");
		condicao.append("SELECT 								");
		condicao.append("	 d.datamov as data					");
		condicao.append("	,v.id as id							");
		condicao.append("	,c.id as caixa						");
		condicao.append("	,c.observacao as descricao			");
		condicao.append("	,c.nominal as nominal				");
		condicao.append("	,case when c.tipodc = 'RECEBER' then 1 								");
		condicao.append("		  when c.tipodc = 'PAGAR' then -1 else 0 end * v.valor as valor ");
		condicao.append("	,v.tipoPagamento as pagamento									");
		condicao.append("FROM " + empresa + ".caixa c  										");
		condicao.append("INNER JOIN " + empresa + ".caixavalor v ON v.caixa_id = c.id		");
		condicao.append("INNER JOIN " + empresa + ".diario d ON c.diario_id = d.id 			");
		condicao.append("LEFT JOIN " + empresa + ".titulo t ON t.id = v.titulo_id 			");
		condicao.append("WHERE t.datacancelamento is null 									");
		condicao.append("	and v.tipoPagamento = " + Suporte.getQuotedStr(TipoPagamento.SAQUE.name()));
		if (inicio != null)
			condicao.append("	and cast(d.datamov as date) >= " + Suporte.formataDataSQL_Quoted(inicio));
		if (termino != null)
			condicao.append("	and cast(d.datamov as date) <= " + Suporte.formataDataSQL_Quoted(termino));

		return condicao.toString();
	}

//	public void calcularCaixaDiario(CaixaDiario diario, Date inicio, Date termino) {
//		List<Movimento> listaMovimento = listaDiarioMovimento(inicio, diario.getDataMov());
//		calcularCaixaDiario(diario, listaMovimento);
//	}

	public String consultaGrafico(String empresa, Date inicio, Date termino, TipoTituloOrigem tipo) {

		StringBuilder condicao = new StringBuilder();

		/********** outras movimentações do caixa *****************/
		condicao.append("SELECT 															");
		condicao.append("	 d.datamov as data 												");
		condicao.append("	,null as id 													");
		condicao.append("	,c.id as caixa													");
		condicao.append("	,c.observacao as descricao 										");
		condicao.append("	,c.nominal as nominal											");
		condicao.append("	,case when c.tipodc = 'RECEBER' then 1 							");
		condicao.append("		  when c.tipodc = 'PAGAR' then -1 else 0 end * v.valor as valor ");
		condicao.append("	,v.tipoPagamento as pagamento									");
		condicao.append("FROM " + empresa + ".caixa c  										");
		condicao.append("INNER JOIN " + empresa + ".caixavalor v ON v.caixa_id = c.id		");
		condicao.append("INNER JOIN " + empresa + ".diario d ON c.diario_id = d.id 			");
		condicao.append("LEFT JOIN " + empresa + ".titulo t ON t.id = v.titulo_id 			");
		condicao.append("		and t.datacancelamento is null and t.agente_id is not null	");
		condicao.append("WHERE 1 = 1 ");
		condicao.append("	and v.tipoPagamento <> " + Suporte.getQuotedStr(TipoPagamento.SAQUE.name()));
		condicao.append("	and v.tipoPagamento <> " + Suporte.getQuotedStr(TipoPagamento.CREDITO.name()));
		if (tipo != null)
			condicao.append("	and c.tipoDC = " + Suporte.getQuotedStr(tipo.name()));
		if (inicio != null)
			condicao.append("	and cast(d.datamov as date) >= " + Suporte.formataDataSQL_Quoted(inicio));
		if (termino != null)
			condicao.append("	and cast(d.datamov as date) <= " + Suporte.formataDataSQL_Quoted(termino));

		/************** conta bancária *****************/
		condicao.append("UNION 							");
		condicao.append("SELECT 						");
		condicao.append("	 t.databaixa as data 		");
		condicao.append("	,t.id as id 				");
		condicao.append("	,null as caixa				");
		condicao.append("	,t.descricao as descricao	");
		condicao.append("	,t.nominal as nominal  		");
		condicao.append("	,case when tipodc = 'RECEBER' then 1 							");
		condicao.append("		  when tipodc = 'PAGAR' then -1 else 0 end * valor as valor ");
		condicao.append("	,'BANCO' as pagamento		");
		condicao.append("FROM " + empresa + ".titulo t 						");
		condicao.append("WHERE t.datacancelamento is null 					");
		condicao.append("	and t.agente_id is null							");
		condicao.append("	and t.contabancaria_id is not null				");
		if (tipo != null)
			condicao.append("	and t.tipoDC = " + Suporte.getQuotedStr(tipo.name()));
		if (inicio != null)
			condicao.append("	and cast(t.dataBaixa as date) >= " + Suporte.formataDataSQL_Quoted(inicio));
		if (termino != null)
			condicao.append("	and cast(t.dataBaixa as date) <= " + Suporte.formataDataSQL_Quoted(termino));

		return condicao.toString();

	}

	private String consulta(CondicaoFilter filtro) {

		StringBuilder condicao = new StringBuilder();

		if (filtro.getInicio() != null)
			condicao.append(" and cast(d.dataMov as date) >= '" + filtro.getInicio() + "' ");

		if (filtro.getTermino() != null)
			condicao.append(" and cast(d.dataMov as date) <= '" + filtro.getTermino() + "' ");

		return condicao.toString();
	}

	@SuppressWarnings("unchecked")
	public List<CaixaDiario> lista(CondicaoFilter filtro) {
		StringBuilder condicao = new StringBuilder();
		condicao.append("select d from diario d where 1 = 1 ");
		condicao.append(consulta(filtro));

		if (filtro.getSortField() == null) {
			condicao.append(" ORDER BY id desc ");
		} else {
			condicao.append(" ORDER BY d." + filtro.getSortField() + (filtro.isAscendente() ? "" : " desc"));
		}

		Query query = manager.createQuery(condicao.toString(), CaixaDiario.class);
		if (filtro.getPrimeiroRegistro() != null) {
			query.setFirstResult(filtro.getPrimeiroRegistro().intValue());
			query.setMaxResults(filtro.getQuantidadeRegistros().intValue());
		}

		return query.getResultList();

	}

	public int quantidadeFiltrados(CondicaoFilter filtro) {
		StringBuilder condicao = new StringBuilder();
		condicao.append("select count(*) from diario d where 1 = 1 ");
		condicao.append(consulta(filtro));
		return ((Number) manager.createQuery(condicao.toString()).getSingleResult()).intValue();
	}

//	@Transactional
//	public void fecharAPartirDe(CaixaDiario caixaDiario) {
//
//		Date dataInicial = caixaDiario.getDataMov();
//		double valorEntradaBanco = 0;
//		double valorSaidaBanco = 0;
//
//		double saldoEmDinheiro = 0;
//		double saldoEmCheque = 0;
//		double saldoEmBanco = 0;
//
//		boolean iniciou = false;
//		StringBuilder sql = new StringBuilder();
//		sql.append("select d from diario d where 1 = 1 ");
//		sql.append(" 	and origem = " + seguranca.getPessoaLogadoOrigem().getId());
//		sql.append("	and dataMov >= " + Suporte.formataDataSQL_Quoted(dataInicial));
//		sql.append("order by dataMov ");
//		List<CaixaDiario> lst = manager.createQuery(sql.toString(), CaixaDiario.class).getResultList();
//
//		for (CaixaDiario diario : lst) {
//
//			CaixaDiario cxdia = this.porId(diario.getId());
//			cxdia.limpaSaldoFinal();
//
//			if (iniciou) {
//
//				valorEntradaBanco = contas.valorEntradaPorOrigem(seguranca.getPessoaLogado(), dataInicial,
//						cxdia.getDataMov());
//				valorSaidaBanco = contas.valorSaidaPorOrigem(seguranca.getPessoaLogado(), dataInicial,
//						cxdia.getDataMov());
//
//				cxdia.setInicialBanco(saldoEmBanco);
//				cxdia.setFinalBanco(saldoEmBanco + valorEntradaBanco - valorSaidaBanco);
//				cxdia.setInicialDinheiro(saldoEmDinheiro);
//
//				cxdia.setInicialCheque(saldoEmCheque);
//				cxdia.setFechamento(new Date());
//				cxdia = manager.merge(cxdia);
//
//				dataInicial = cxdia.getDataMov();
//
//			}
//
//			saldoEmDinheiro = cxdia.getFinalDinheiro();
//			saldoEmCheque = cxdia.getFinalCheque();
//			saldoEmBanco = cxdia.getFinalBanco();
//			iniciou = true;
//
//		}
//	}

//	public void calcularCaixaDiario(CaixaDiario diario, List<Movimento> listaMovimento) {
//		diario.limpaSaldoFinal();
//		if (listaMovimento != null)
//			for (Movimento item : listaMovimento) {
//				if (item.getTipo() == TipoPagamento.DINHEIRO || item.getTipo() == TipoPagamento.SAQUE
//						|| item.getTipo() == TipoPagamento.CAIXA) {
//					diario.adicionaDinheiro(item.getValor());
//				} else if (item.getTipo() == TipoPagamento.CHEQUE) {
//					diario.adicionaCheque(item.getValor());
//				} else {
//					diario.adicionaBanco(item.getValor());
//				}
//			}
//	}

	@Transactional
	public void reposicionarCaixaParaDiario(Caixa caixa, Date dataParaEncerramento) {

		Caixa cxa = manager.find(Caixa.class, caixa.getId());
		CaixaDiario destino = null;
		CaixaDiario origem = null;

		origem = this.porId(caixa.getDiario().getId());
		if (caixa.getDiario() == null || origem == null)
			throw new NegocioException("caixa atual não está definido em um diário financeiro");

		if (cxa.getEstorno() != null)
			throw new NegocioException(
					"caixa com estorno em " + SuporteData.formataDataToStr(cxa.getEstorno(), "dd/MM/yyyy"));

		if (cxa.getEncerramento() == null)
			throw new NegocioException("caixa sem data de encerramento para reposicionamento");

		destino = porData(dataParaEncerramento);
		if (destino == null)
			throw new NegocioException("diário financeiro não foi localizado para reposicionametno");

		cxa.setDiario(destino);
		cxa = manager.merge(cxa);

		Date dataPara = dataParaEncerramento.before(caixa.getDataMovimento()) ? dataParaEncerramento
				: caixa.getDataMovimento();

		origem = porData(dataPara);

	}

	@SuppressWarnings("unchecked")
	public List<Movimento> listaDiarioMovimento(Date inicio, Date termino) {

		Movimento item;
		List<Movimento> lista = new ArrayList<>();

		String empresa = seguranca.getEmpresa().toLowerCase();
		StringBuilder condicao = new StringBuilder();
		condicao.append("select caixa, data, pagamento, descricao, nominal, valor from ( ");
		condicao.append(consultaMovimento(empresa, inicio, termino, null));
		condicao.append(" ) t ");
		condicao.append("ORDER BY 1, 2 ");

		Query query = manager.createNativeQuery(condicao.toString());

		List<Object[]> lstObj = (List<Object[]>) query.getResultList();
		for (Object[] obj : lstObj) {
			if (obj != null) {
				item = new Movimento((BigInteger) obj[0], (Date) obj[1], TipoPagamento.valueOf((String) obj[2]),
						(String) obj[3], (String) obj[4], (Double) obj[5]);
				lista.add(item);
			}
		}

		return lista;
	}

	@Transactional
	public void salvarDiarioEPosteriores(CaixaDiario caixaDiario, boolean encerra) throws Exception {

		Date dataInicial = caixaDiario.getDataMov();
		double saldoEmDinheiro = caixaDiario.getFinalDinheiro();
		double saldoEmCheque = caixaDiario.getFinalCheque();
		double saldoEmBanco = caixaDiario.getFinalBanco();

		StringBuilder sql = new StringBuilder();
		sql.append("select d from diario d where 1 = 1 ");
		sql.append(" 	and origem = " + seguranca.getPessoaLogadoOrigem().getId());
		sql.append("	and dataMov > " + Suporte.formataDataSQL_Quoted(dataInicial));
		sql.append("order by dataMov ");
		List<CaixaDiario> lst = manager.createQuery(sql.toString(), CaixaDiario.class).getResultList();

		for (CaixaDiario diario : lst) {

			diario.setInicialDinheiro(saldoEmDinheiro);
			diario.setInicialCheque(saldoEmCheque);
			diario.setInicialBanco(saldoEmBanco);
			preparaSaldoFinal(diario);
			if (encerra)
				diario.setFechamento(new Date());
			diario = manager.merge(diario);

			saldoEmDinheiro = diario.getFinalDinheiro();
			saldoEmCheque = diario.getFinalCheque();
			saldoEmBanco = diario.getFinalBanco();

		}

	}

	public void preparaSaldoFinal(CaixaDiario diario) throws Exception {

		String empresa = seguranca.getEmpresa().toLowerCase();
		StringBuilder condicao;
		Query query;
		Object[] obj;

		condicao = new StringBuilder();
		condicao.append("SELECT ");
		condicao.append("	SUM(v.valor * case when c.tipodc = 'PAGAR' then -1 else 1 end ");
		condicao.append("				* case when v.tipopagamento = 'DINHEIRO' then 1 else 0 end) as dinheiro, ");
		condicao.append("	SUM(v.valor * case when c.tipodc = 'PAGAR' then -1 else 1 end ");
		condicao.append("				* case when v.tipopagamento = 'SAQUE' then 1 else 0 end) as saque, ");
		condicao.append("	SUM(v.valor * case when c.tipodc = 'PAGAR' then -1 else 1 end ");
		condicao.append("				* case when v.tipopagamento = 'CHEQUE' then 1 else 0 end) as cheque ");
		condicao.append("FROM " + empresa + ".caixa as c ");
		condicao.append("INNER JOIN " + empresa + ".caixavalor as v ON c.id = v.caixa_id ");
		condicao.append("WHERE c.diario_id = " + diario.getId());
		query = manager.createNativeQuery(condicao.toString());
		obj = (Object[]) query.getSingleResult();

		double dinheiro = obj[0] == null ? 0.0 : (Double) obj[0];
		double saque = obj[1] == null ? 0.0 : (Double) obj[1];
		double cheque = obj[2] == null ? 0.0 : (Double) obj[2];

		CaixaDiario anterior = diarioAnteriorData(diario.getDataMov());
		Date dataInicial = anterior == null ? null : anterior.getDataMov();

//		double valorEntradaBanco = contas.valorEntradaPorOrigem(diario.getOrigem(), dataInicial, diario.getDataMov());
//		double valorSaidaBanco = contas.valorSaidaPorOrigem(diario.getOrigem(), dataInicial, diario.getDataMov());
		
		double valor_banco = contas.saldoMovimentoEmContaOrigem(diario.getOrigem(), dataInicial, diario.getDataMov());
		diario.setFinalBanco(diario.getInicialBanco() + valor_banco);

		diario.setFinalDinheiro(diario.getInicialDinheiro() + dinheiro + saque);
		diario.setFinalCheque(diario.getInicialCheque() + cheque);
//		diario.setFinalBanco(diario.getInicialBanco() + valorEntradaBanco - valorSaidaBanco);

	}

}