package com.sousa.sigpro.repository;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.sousa.sigpro.model.ContaCorrente;
import com.sousa.sigpro.model.Remessa;
import com.sousa.sigpro.model.RemessaItem;
import com.sousa.sigpro.repository.filter.CondicaoFilter;
import com.sousa.sigpro.service.NegocioException;
import com.sousa.sigpro.util.Suporte;
import com.sousa.sigpro.util.jpa.Transactional;

public class Remessas implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager manager;

	public Remessa porId(Long id) {
		return manager.find(Remessa.class, id);
	}

	public List<RemessaItem> itemsPorRemessa(Remessa remessa) {
		String condicao = "from RemessaItem where remessa = :remessa";
		return manager.createQuery(condicao, RemessaItem.class).setParameter("remessa", remessa).getResultList();
	}

	public RemessaItem itemPorId(Long id) {
		return manager.find(RemessaItem.class, id);
	}

	@Transactional
	public Remessa emitir(Remessa remessa) {

		if (remessa.getDataEmissao() != null)
			throw new NegocioException("remessa já foi emitida");

		remessa.setDataEmissao(new Date());
		return manager.merge(remessa);
	}

	@Transactional
	public Remessa cancelar(Remessa remessa) {

		if (remessa.getDataCancelamento() != null)
			throw new NegocioException("remessa já está cancelada");

		remessa.setDataCancelamento(new Date());
		return manager.merge(remessa);
	}

	@Transactional
	public Remessa guardar(Remessa remessa) {

		if (remessa.getItems() != null && remessa.getItems().isEmpty())
			throw new NegocioException("sem títulos para remessa");

		return manager.merge(remessa);
	}

	public int proximoLoteNaRemessa(ContaCorrente conta) {
		try {
			return (Integer) manager.createNamedQuery("Remessa.ultimo").setParameter("conta", conta).getSingleResult()
					+ 1;
		} catch (Exception e) {
			return 1;
		}
	}

	public Remessa remessaPorContaSequencia(CondicaoFilter filtro) {
		try {
			String condicao = "select r from Remessa r join r.titulos t where t.contaBancaria = "
					+ filtro.getContaCorrente().getId() + "  and r.sequencia = " + filtro.getOrdem();
			return manager.createQuery(condicao, Remessa.class).getSingleResult();
		} catch (Exception e) {
			return new Remessa();
		}
	}

	@SuppressWarnings("unchecked")
	public List<Remessa> lista(CondicaoFilter filtro) {

		StringBuilder condicao = new StringBuilder();
		condicao.append("select t from Remessa t ");
		condicao.append(consulta(filtro));
		if (filtro.getSortField() != null)
			condicao.append(" ORDER BY " + filtro.getSortField() + (filtro.isAscendente() ? "" : " desc"));

		Query query = manager.createQuery(condicao.toString(), Remessa.class);
		if (filtro.getPrimeiroRegistro() != null) {
			query.setFirstResult(filtro.getPrimeiroRegistro().intValue());
			query.setMaxResults(filtro.getQuantidadeRegistros().intValue());
		}

		return query.getResultList();
	}

	public int quantidadeFiltrados(CondicaoFilter filtro) {
		StringBuilder condicao = new StringBuilder();
		condicao.append("select count(*) from Remessa t ");
		condicao.append(consulta(filtro));
		return ((Number) manager.createQuery(condicao.toString()).getSingleResult()).intValue();
	}

	private String consulta(CondicaoFilter filtro) {
		StringBuilder condicao = new StringBuilder();
		condicao.append("where dataCancelamento is null ");

		if (filtro.getAgente() != null)
			condicao.append(" and destino = " + filtro.getAgente().getId());

		if (Suporte.dataIsValida(filtro.getInicio()))
			condicao.append(" and cast(dataCadastro as date) >= " + Suporte.formataDataSQL_Quoted(filtro.getInicio()));

		if (Suporte.dataIsValida(filtro.getTermino()))
			condicao.append(" and cast(dataCadastro as date) <= " + Suporte.formataDataSQL_Quoted(filtro.getTermino()));

		if (Suporte.dataIsValida(filtro.getEmissaoInicial()))
			condicao.append(
					" and cast(dataEmissao as date) >= " + Suporte.formataDataSQL_Quoted(filtro.getEmissaoInicial()));

		if (Suporte.dataIsValida(filtro.getEmissaoFinal()))
			condicao.append(
					" and cast(dataEmissao as date) <= " + Suporte.formataDataSQL_Quoted(filtro.getEmissaoFinal()));

		if (filtro.getContaCorrente() != null) {
			condicao.append(" AND contaBancaria = " + filtro.getContaCorrente().getId());
		}

		return condicao.toString();

	}

}