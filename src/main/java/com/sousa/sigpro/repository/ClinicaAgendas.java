package com.sousa.sigpro.repository;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.sousa.sigpro.model.Colaborador;
import com.sousa.sigpro.model.Pessoa;
import com.sousa.sigpro.model.clinica.ClinicaAgenda;
import com.sousa.sigpro.model.clinica.ClinicaAtendimento;
import com.sousa.sigpro.model.clinica.ClinicaExame;
import com.sousa.sigpro.model.clinica.ClinicaMedicamento;
import com.sousa.sigpro.model.clinica.Doenca;
import com.sousa.sigpro.model.clinica.Exame;
import com.sousa.sigpro.model.clinica.Medicamento;
import com.sousa.sigpro.repository.filter.CondicaoFilter;
import com.sousa.sigpro.service.NegocioException;
import com.sousa.sigpro.util.Suporte;
import com.sousa.sigpro.util.jpa.Transactional;

public class ClinicaAgendas implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager manager;

	public ClinicaAgenda porId(Long id) {

		ClinicaAgenda agenda = manager.find(ClinicaAgenda.class, id);

		if (agenda.getAtendimento() == null)
			agenda.setAtendimento(new ClinicaAtendimento());

		// if (agenda.getExames() == null)
		// agenda.setExames(new ArrayList<Exame>());

		return agenda;
	}

	public String titulo(ClinicaAgenda agenda) {
		String value = "Acolhimento";
		if (agenda != null) {
			if (agenda.isExiste())
				value = value + " [" + Suporte.zerosAEsquerda(agenda.getId(), 6) + "] ";
			if (agenda.getDataCancela() != null)
				value = value + "cancelado";
			else if (agenda.getAtendimento() != null) {
				if (agenda.getAtendimento().getDataTerminoAtendimento() != null)
					value = value + "encerrado";
				else if (agenda.getAtendimento().getDataInicioAtendimento() != null)
					value = value + "em atendimento";
			}
		}
		return value;
	}

	@Transactional
	public ClinicaAgenda retornar(ClinicaAgenda agenda) {
		agenda.getAcolhimento().setDataLiberaClinica(null);
		return manager.merge(agenda);
	}

	@Transactional
	public ClinicaAgenda liberar(ClinicaAgenda agenda) {
		agenda.getAcolhimento().setDataLiberaClinica(new Date());
		return manager.merge(agenda);
	}

	@Transactional
	public ClinicaAgenda cancelar(ClinicaAgenda agenda) {
		agenda.setDataCancela(new Date());
		return manager.merge(agenda);
	}

	@SuppressWarnings("unchecked")
	public List<ClinicaAgenda> pesquisar(CondicaoFilter filtro) {

		StringBuilder condicao = new StringBuilder();
		condicao.append("select a from ClinicaAgenda a, Pessoa p where p.cliente = a.paciente ");

		if (filtro.getSql() != null && filtro.getSql().length() > 0)
			condicao.append(" and " + filtro.getSql());

		if (filtro.getNome() != null)
			condicao.append(
					" and LOWER(p.nome) like " + Suporte.getQuotedStr("%" + filtro.getNome().toLowerCase() + "%"));

		if (filtro.getCodigo() != null) {
			condicao.append(" and ( p.PF.cpf like " + Suporte.getQuotedStr("%" + filtro.getCodigo() + "%"));
			condicao.append(" or a.documento like " + Suporte.getQuotedStr("%" + filtro.getCodigo() + "%") + " ) ");
		}

		if (filtro.getInicio() != null)
			condicao.append(" and cast(a.dataCadastro as date) >= '" + filtro.getInicio() + "' ");

		if (filtro.getTermino() != null)
			condicao.append(" and cast(a.dataCadastro as date) <= '" + filtro.getTermino() + "' ");

		if (filtro.getSortField() == null)
			condicao.append(" ORDER BY a.id desc");
		else
			condicao.append(" ORDER BY " + filtro.getSortField() + (filtro.isAscendente() ? "" : " desc"));

		Query query = manager.createQuery(condicao.toString(), ClinicaAgenda.class);
		List<ClinicaAgenda> lst = query.getResultList();
		return lst;
	}

	@Transactional
	public ClinicaAgenda iniciar(ClinicaAgenda agenda, Colaborador colaborador) {

		if (colaborador == null || colaborador.getId() == null)
			throw new NegocioException("para iniciar um atendimento é necessário um colaborador");

		agenda.getAtendimento().setConsultor(colaborador);
		agenda.getAtendimento().setDataInicioAtendimento(new Date());
		ClinicaAgenda clinica = manager.merge(agenda);
		return clinica;

	}

	@Transactional
	public ClinicaAgenda guardar_acolhimento(ClinicaAgenda agenda) {
		NegocioException.analisa(agenda.getPaciente() == null, "defina o paciente");
		return manager.merge(agenda);
	}

	@Transactional
	public ClinicaAgenda guardar_atendimento(ClinicaAgenda agenda) {

		if (agenda.getAtendimento() != null && agenda.getAtendimento().getPatologia() != null) {
			Doenca doenca = agenda.getAtendimento().getPatologia();
			if (doenca.getDataCadastro() == null) {
				doenca.setDataCadastro(new Date());
				doenca = manager.merge(doenca);
				manager.flush();
			}
			agenda.getAtendimento().setPatologia(doenca);
		}

		if (agenda.getMedicamentos() != null && agenda.getMedicamentos().size() > 0) {
			for (ClinicaMedicamento item : agenda.getMedicamentos()) {
				Medicamento med = item.getMedicamento();
				if (med.getDataCadastro() == null) {
					med.setDataCadastro(new Date());
					med = manager.merge(med);
					manager.flush();
				}
			}
		}

		for (ClinicaExame item : agenda.getExames()) {
			Exame exame = item.getExame();
			if (exame.getDataCadastro() == null) {
				exame.setDataCadastro(new Date());
				exame = manager.merge(exame);
				manager.flush();
			}
		}

		return manager.merge(agenda);
	}

}