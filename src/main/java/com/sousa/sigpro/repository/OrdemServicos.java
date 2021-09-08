package com.sousa.sigpro.repository;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.sousa.sigpro.model.Expedicao;
import com.sousa.sigpro.model.ExpedicaoItem;
import com.sousa.sigpro.model.OrdemServico;
import com.sousa.sigpro.model.Veiculo;
import com.sousa.sigpro.model.tipo.TipoStatusProducao;
import com.sousa.sigpro.repository.filter.OrdemServicoFilter;
import com.sousa.sigpro.security.Seguranca;
import com.sousa.sigpro.util.jpa.Transactional;

public class OrdemServicos implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager manager;
	@Inject
	private Seguranca seguranca;
	@Inject
	private Veiculos veiculos;

	public OrdemServico porId(Long id) {
		String condicao = "select p from OrdemServico p where p.id = " + id;
		return manager.createQuery(condicao, OrdemServico.class).getSingleResult();
	}

	public OrdemServico porExpedicao(Expedicao expedicao) {
		List<OrdemServico> lst = lista(expedicao);
		if (lst == null || lst.size() == 0)
			return null;
		else
			return lst.get(0);
	}

	public List<ExpedicaoItem> listaItems(OrdemServico ordem) {
		StringBuilder condicao = new StringBuilder();
		condicao.append("select e from ExpedicaoItem e ");
		condicao.append("where e.servico = " + ordem.getId());
		condicao.append(" ORDER BY e.expedicao, e.id ");
		return manager.createQuery(condicao.toString(), ExpedicaoItem.class).getResultList();
	}

	public List<OrdemServico> lista(Expedicao expedicao) {
		return manager.createNamedQuery("Ordem.porExpedicao", OrdemServico.class).setParameter("expedicao", expedicao)
				.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<OrdemServico> lista(OrdemServicoFilter filtro) {

		StringBuilder condicao = new StringBuilder();
		condicao.append("select s from OrdemServico s ");
		condicao.append(consulta(filtro));
		if (filtro.getPropriedadeOrdenacao() != null) {
			condicao.append(" ORDER BY s." + filtro.getPropriedadeOrdenacao() + (filtro.isAscendente() ? "" : " desc"));
		}

		Query query = manager.createQuery(condicao.toString(), OrdemServico.class);
		if (filtro.getPrimeiroRegistro() != null) {
			query.setFirstResult(filtro.getPrimeiroRegistro().intValue());
			query.setMaxResults(filtro.getQuantidadeRegistros().intValue());
		}

		return query.getResultList();
	}

	public int quantidadeFiltrados(OrdemServicoFilter filtro) {
		StringBuilder condicao = new StringBuilder();
		condicao.append("select count(*) from OrdemServico s ");
		condicao.append(consulta(filtro));
		return ((Number) manager.createQuery(condicao.toString()).getSingleResult()).intValue();
	}

	@Transactional
	public OrdemServico liberar(OrdemServico ordem) {
		ordem.setDataLiberaProducao(new Date());
		return manager.merge(ordem);
	}

	@Transactional
	public OrdemServico definirImpressao(OrdemServico ordem) {
		if (ordem.getDataImpressao() != null)
			ordem.setDataReimpressao(new Date());
		else
			ordem.setDataReimpressao(new Date());
		return manager.merge(ordem);
	}

	@Transactional
	public OrdemServico guardar(OrdemServico ordem) {

		String sqlString;

		if (ordem.getId() != null) {
			sqlString = "update ExpedicaoItem set servico = null where servico = :servico ";
			Query st = manager.createQuery(sqlString).setParameter("servico", ordem);
			st.executeUpdate();
		}

		Veiculo veiculo = veiculos.porId(ordem.getVeiculo().getId());
		if (ordem.getOdometro() > veiculo.getOdometroFinal()) {
			veiculo.setOdometroFinal(ordem.getOdometro());
			veiculo = veiculos.guardar(veiculo);
		}

		for (ExpedicaoItem item : ordem.getItems()) {
			if (item.getStatus() == null)
				item.setStatus(TipoStatusProducao.DISPONIVEL);
			item.setServico(ordem);
		}
		if (ordem.getDataEmissao() == null)
			ordem.setDataEmissao(new Date());

		return manager.merge(ordem);
	}

	private String consulta(OrdemServicoFilter filtro) {

		StringBuilder condicao = new StringBuilder();
		condicao.append("where s.responsavel.origem =" + seguranca.getPessoaLogadoOrigem().getId());

		if (filtro.getTipo() <= 0) {
			if (filtro.getTipo() == 0)
				condicao.append(" and s.dataLiberaProducao is null  ");
			if (filtro.getInicio() != null)
				condicao.append(" and cast(s.dataEmissao as date) >= '" + filtro.getInicio() + "' ");
			if (filtro.getTermino() != null)
				condicao.append(" and cast(s.dataEmissao as date) <= '" + filtro.getTermino() + "' ");

		} else if (filtro.getTipo() == 1) {
			condicao.append(" and s.dataLiberaProducao is not null and s.cronologia.inicio is null ");
			if (filtro.getInicio() != null)
				condicao.append(" and cast(s.dataLiberaProducao as date) >= '" + filtro.getInicio() + "' ");
			if (filtro.getTermino() != null)
				condicao.append(" and cast(s.dataLiberaProducao as date) <= '" + filtro.getTermino() + "' ");

		} else if (filtro.getTipo() == 2) {
			condicao.append(" and s.cronologia.inicio is not null and s.cronologia.termino is null ");
			if (filtro.getInicio() != null)
				condicao.append(" and cast(s.cronologia.inicio as date) >= '" + filtro.getInicio() + "' ");
			if (filtro.getTermino() != null)
				condicao.append(" and cast(s.cronologia.inicio as date) <= '" + filtro.getTermino() + "' ");

		} else if (filtro.getTipo() == 3) {
			condicao.append(" and s.cronologia.termino is not null ");
			if (filtro.getInicio() != null)
				condicao.append(" and cast(s.cronologia.termino as date) >= '" + filtro.getInicio() + "' ");
			if (filtro.getTermino() != null)
				condicao.append(" and cast(s.cronologia.termino as date) <= '" + filtro.getTermino() + "' ");
		}

		if (filtro.getPlaca() != null)
			condicao.append(" and UPPER(veiculo.placa) like '%" + filtro.getPlaca().toUpperCase() + "%' ");

		if (filtro.getNome() != null)
			condicao.append(" and UPPER(responsavel.nome) like '%" + filtro.getNome().toUpperCase() + "%' ");

		return condicao.toString();
	}

}