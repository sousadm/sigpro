package com.sousa.sigpro.repository;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import com.sousa.sigpro.model.Agenda;
import com.sousa.sigpro.model.Contato;
import com.sousa.sigpro.model.tipo.TipoAgendaStatus;
import com.sousa.sigpro.repository.filter.CondicaoFilter;
import com.sousa.sigpro.security.Seguranca;
import com.sousa.sigpro.service.NegocioException;
import com.sousa.sigpro.util.Suporte;
import com.sousa.sigpro.util.jpa.Transactional;

public class Agendas implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Seguranca seguranca;
	@Inject
	private EntityManager manager;

	public Agenda porId(Long id) {
		String condicao = "select p from Agenda p where p.id = " + id;
		return manager.createQuery(condicao, Agenda.class).getSingleResult();
	}

	@Transactional
	public Agenda guardar(Agenda agenda) {

		Contato contato = agenda.getContato();
		contato = manager.merge(contato);
		manager.flush();

		agenda.setContato(contato);
		return manager.merge(agenda);
	}

	@Transactional
	public Agenda finalizar(Agenda agenda) {
		if (agenda.getDataEncerramento() != null)
			throw new NegocioException("agenda já está finalizada");

		if (TipoAgendaStatus.PLANEJADO == agenda.getStatus())
			throw new NegocioException("status incorreto para finalizar agenda");

		agenda.setDataEncerramento(new Date());
		return manager.merge(agenda);
	}

	@Transactional
	public void remover(Agenda agenda) {
		agenda = porId(agenda.getId());
		manager.remove(agenda);
		manager.flush();
	}

	public List<Agenda> lista(CondicaoFilter filtro) {
		StringBuilder condicao = new StringBuilder();
		condicao.append("select a from Agenda a, Pessoa p join p.usuario u ");
		condicao.append("where a.usuario = p.usuario ");
		condicao.append(consulta(filtro));
		condicao.append(" order by a.dataPrevista");

		return manager.createQuery(condicao.toString(), Agenda.class).getResultList();
	}

	public int quantidadeFiltrados(CondicaoFilter filtro) {
		StringBuilder condicao = new StringBuilder();
		condicao.append("select count(*) from Agenda a, Pessoa p join p.usuario u ");
		condicao.append("where a.usuario = p.usuario ");
		condicao.append(consulta(filtro));
		return ((Number) manager.createQuery(condicao.toString()).getSingleResult()).intValue();
	}

	private String consulta(CondicaoFilter filtro) {

		StringBuilder condicao = new StringBuilder();

		if (seguranca.isUsuarioConvidado())
			condicao.append(" and p = " + seguranca.getPessoaLogado().getId());
		else
			condicao.append(" and p.origem = " + seguranca.getPessoaLogadoOrigem().getId());

		if (filtro.getNome() != null && filtro.getNome().length() > 0)
			condicao.append(" and UPPER(a.contato.nome) like '%" + filtro.getNome().toUpperCase() + "%' ");

		if (filtro.getTipoAgendaStatus() != null)
			condicao.append(" and a.status = " + Suporte.getQuotedStr(filtro.getTipoAgendaStatus().name()));
		
		if (filtro.getInicio() != null)
			condicao.append(" and cast(a.dataPrevista as date) >= '" + filtro.getInicio() + "' ");

		if (filtro.getTermino() != null)
			condicao.append(" and cast(a.dataPrevista as date) <= '" + filtro.getTermino() + "' ");

		return condicao.toString();

	}

}