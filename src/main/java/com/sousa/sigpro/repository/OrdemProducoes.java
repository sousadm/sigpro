package com.sousa.sigpro.repository;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.sousa.sigpro.model.Cronologia;
import com.sousa.sigpro.model.ExpedicaoItem;
import com.sousa.sigpro.model.OrdemServico;
import com.sousa.sigpro.model.tipo.TipoStatusProducao;
import com.sousa.sigpro.repository.filter.OrdemServicoFilter;
import com.sousa.sigpro.security.Seguranca;
import com.sousa.sigpro.service.NegocioException;
import com.sousa.sigpro.util.jpa.Transactional;

public class OrdemProducoes implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager manager;
	@Inject
	private Seguranca seguranca;

	public ExpedicaoItem itemProducaoPorId(Long id) {
		String condicao = "select p from ExpedicaoItem p where p.id = " + id;
		return manager.createQuery(condicao, ExpedicaoItem.class).getSingleResult();
	}

	@Transactional
	public ExpedicaoItem stop(ExpedicaoItem item) {

		if (item.getStatus() != TipoStatusProducao.PRODUCAO)
			throw new NegocioException("Item não está em produção");

		item.getCronologia().setTermino(new Date());
		item.setStatus(TipoStatusProducao.CONCLUIDO);

		OrdemServico ordem = item.getServico();
		if (ordem.getCronologia() == null)
			ordem.setCronologia(new Cronologia());

		if (ordem.getCronologia().getInicio() == null)
			throw new NegocioException("Ordem de serviço não foi iniciada");

		if (ordem.getCronologia().getTermino() != null)
			throw new NegocioException("Ordem de serviço já foi encerrada");

		// substituir no futuro
		List<ExpedicaoItem> lst = this.manager.createNamedQuery("ExpedicaoItem.listaPorServico", ExpedicaoItem.class)
				.setParameter("ordem", ordem).getResultList();
		int qt = 0;
		for (ExpedicaoItem itemX : lst) {
			if (itemX.getStatus() != TipoStatusProducao.CONCLUIDO
					|| itemX.getStatus() != TipoStatusProducao.ENCERRADO) {
				qt = qt + 1;
			}
		}

		if (qt == 1) {
			if (ordem.getCronologia() == null)
				ordem.setCronologia(new Cronologia());
			ordem.getCronologia().setTermino(item.getCronologia().getTermino());

			// String sqlString = "update ExpedicaoItem e set e.termino =
			// current_timestamp "
			// + " where e.servico = :servico";
			// Query st = manager.createQuery(sqlString).setParameter("servico",
			// ordem);
			// st.executeUpdate();

			manager.merge(ordem);
		}

		return manager.merge(item);

	}

	public int quantidadeFiltrados(OrdemServicoFilter filtro) {
		StringBuilder condicao = new StringBuilder();
		condicao.append("select count(*) from ExpedicaoItem s join s.servico t ");
		condicao.append(consulta(filtro));
		return ((Number) manager.createQuery(condicao.toString()).getSingleResult()).intValue();
	}

	private String consulta(OrdemServicoFilter filtro) {

		StringBuilder condicao = new StringBuilder();
		condicao.append("where s.servico.dataLiberaProducao is not null ");
		if (filtro.getTipo() <= 0) {
			if (filtro.getTipo() == 0)
				condicao.append(" and s.cronologia.inicio is null and s.servico.cronologia.termino is null) ");
			if (filtro.getInicio() != null)
				condicao.append(" and cast(s.servico.dataLiberaProducao) >= '" + filtro.getInicio() + "' ");
			if (filtro.getTermino() != null)
				condicao.append(" and cast(s.servico.dataLiberaProducao) <= '" + filtro.getTermino() + "' ");

		} else if (filtro.getTipo() == 1) {
			condicao.append(" and s.status = '" + TipoStatusProducao.PRODUCAO + "' ");
			if (filtro.getInicio() != null)
				condicao.append(" and cast(s.cronologia.inicio) >= '" + filtro.getInicio() + "' ");
			if (filtro.getTermino() != null)
				condicao.append(" and cast(s.cronologia.inicio) <= '" + filtro.getTermino() + "' ");

		} else if (filtro.getTipo() == 2) {
			condicao.append(" and s.status = '" + TipoStatusProducao.PAUSA + "' ");
			if (filtro.getInicio() != null)
				condicao.append(" and cast(s.cronologia.inicio) >= '" + filtro.getInicio() + "' ");
			if (filtro.getTermino() != null)
				condicao.append(" and cast(s.cronologia.inicio) <= '" + filtro.getTermino() + "' ");

		} else if (filtro.getTipo() == 3) {
			condicao.append(" and (s.cronologia.termino is not null or t.cronologia.termino is not null ) ");
			if (filtro.getInicio() != null)
				condicao.append(" and cast(s.cronologia.termino as date) >= '" + filtro.getInicio() + "' ");
			if (filtro.getTermino() != null)
				condicao.append(" and cast(s.cronologia.termino as date) <= '" + filtro.getTermino() + "' ");
		}

		if (!filtro.getPlaca().trim().equals(""))
			condicao.append(" and UPPER(s.servico.veiculo.placa) like '%" + filtro.getPlaca().toUpperCase() + "%' ");

		if (!filtro.getNome().trim().equals(""))
			condicao.append(" and UPPER(s.servico.responsavel.nome) like '%" + filtro.getNome().toUpperCase() + "%' ");

		condicao.append(" and s.produto.categoria.categoriaPai.tipoProduto = 'SERVICO' ");
		condicao.append(" and s.servico.responsavel.origem = " + seguranca.getPessoaLogadoOrigem().getId());

		return condicao.toString();
	}

	@SuppressWarnings("unchecked")
	public List<ExpedicaoItem> listaProducao(OrdemServicoFilter filtro) {
		StringBuilder condicao = new StringBuilder();
		condicao.append("select s from ExpedicaoItem s join s.servico t ");
		condicao.append(consulta(filtro));

		if (filtro.getPropriedadeOrdenacao() != null) {
			condicao.append(" ORDER BY " + filtro.getPropriedadeOrdenacao() + (filtro.isAscendente() ? "" : " desc"));
		}

		Query query = manager.createQuery(condicao.toString(), ExpedicaoItem.class);
		if (filtro.getPrimeiroRegistro() != null) {
			query.setFirstResult(filtro.getPrimeiroRegistro().intValue());
			query.setMaxResults(filtro.getQuantidadeRegistros().intValue());
		}

		return query.getResultList();

	}

}