package com.sousa.sigpro.repository;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import com.sousa.sigpro.model.Colaborador;
import com.sousa.sigpro.model.Profissao;
import com.sousa.sigpro.model.clinica.ClinicaAgenda;
import com.sousa.sigpro.model.clinica.ClinicaHorario;
import com.sousa.sigpro.model.tipo.TipoDiaSemana;
import com.sousa.sigpro.service.NegocioException;
import com.sousa.sigpro.util.Suporte;
import com.sousa.sigpro.util.SuporteData;
import com.sousa.sigpro.util.jpa.Transactional;

public class ClinicaHorarios implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager manager;

	public ClinicaHorario porId(Long id) {
		return manager.find(ClinicaHorario.class, id);
	}

	public Date prepara(Date hora, Date data) {
		try {

			DateFormat dfdata = new SimpleDateFormat("dd/MM/yyyy");
			String data_str = dfdata.format(data);

			DateFormat dfhora = new SimpleDateFormat("HH:mm:ss");
			String hora_str = dfhora.format(hora);

			Date retorno = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(data_str + " " + hora_str);

			return retorno;

		} catch (ParseException e) {
			return null;
		}
	}

	public List<ClinicaAgenda> listaClinicaAgenda(Colaborador consultor, Date data) throws Exception {

		Date horaInicial = null;

		TipoDiaSemana diaSemana = SuporteData.diaDaSemana(data);
		System.out.println(diaSemana.getDescricao());

		List<ClinicaAgenda> lstAgenda = new ArrayList<>();

		if (consultor.getTempo() == null)
			throw new Exception("tempo de atendimento indefinido");

		int tempo = SuporteData.tempo(consultor.getTempo());

		String condicao = "select h from ClinicaHorario h where h.consultor.id = " + consultor.getId();
		List<ClinicaHorario> lst = manager.createQuery(condicao, ClinicaHorario.class).getResultList();
		for (ClinicaHorario horario : lst) {

			horaInicial = prepara(horario.getInicio(), data);
			horario.setEncerramento(prepara(horario.getEncerramento(), data));
			horario.prepara();

			horario.setIntervalo(prepara(horario.getIntervalo(), data));
			// horario.setIntervalo(SuporteData.horaAddMinuto(horario.getIntervalo(), -1));

			horario.setReinicio(prepara(horario.getReinicio(), data));
			horario.setReinicio(SuporteData.horaAddMinuto(horario.getReinicio(), -1));

			for (TipoDiaSemana ds : horario.getDias()) {
				if (ds == diaSemana) {
					while (horaInicial.before(horario.getEncerramento())) {
						if (horaInicial.before(horario.getIntervalo()) || horaInicial.after(horario.getReinicio())) {
							ClinicaAgenda agenda = new ClinicaAgenda();
							agenda.getAtendimento().setConsultor(consultor);
							agenda.setDataMarcada(horaInicial);
							lstAgenda.add(agenda);
						}
						horaInicial = SuporteData.horaAddMinuto(horaInicial, tempo);
					}
				}
			}
		}

		return lstAgenda;

	}

	public List<Profissao> listaProfissao() {
		String condicao = "select a from Pessoa p join p.PF.profissao a order by a.descricao";
		return manager.createQuery(condicao, Profissao.class).getResultList();
	}

	public List<Colaborador> listaProfissional(Profissao profissao) {
		StringBuilder condicao = new StringBuilder();
		condicao.append("select p.colaborador from Pessoa p join p.PF.profissao a ");
		condicao.append("where a.codigo = " + Suporte.getQuotedStr(profissao.getCodigo()));
		condicao.append("order by a.descricao");
		return manager.createQuery(condicao.toString(), Colaborador.class).getResultList();
	}

	public List<ClinicaHorario> lista() {
		StringBuilder condicao = new StringBuilder();
		condicao.append("from ClinicaHorario order by consultor, inicio");
		return manager.createQuery(condicao.toString(), ClinicaHorario.class).getResultList();
	}

	@Transactional
	public ClinicaHorario guardar(ClinicaHorario horario) {
		NegocioException.analisa(horario.getInicio() == null, "horário inicial indefinido");
		NegocioException.analisa(horario.getEncerramento() == null, "horário final indefinido");
		if (horario.getIntervalo() != null && horario.getIntervalo() != null) {
			NegocioException.analisa(horario.getInicio().compareTo(horario.getEncerramento()) > 0
					|| horario.getInicio().compareTo(horario.getIntervalo()) > 0
					|| horario.getInicio().compareTo(horario.getReinicio()) > 0, "horário inicial incorreto");
			NegocioException.analisa(
					horario.getIntervalo().compareTo(horario.getReinicio()) > 0
							|| horario.getReinicio().compareTo(horario.getEncerramento()) > 0,
					"horário de intervalo incorreto");
		}
		NegocioException.analisa(
				horario.getReinicio() != null && horario.getReinicio().compareTo(horario.getEncerramento()) > 0,
				"horário de reinício incorreto");
		return manager.merge(horario);
	}

	@Transactional
	public void remover(ClinicaHorario horario) {
		try {
			horario = porId(horario.getId());
			manager.remove(horario);
			manager.flush();
		} catch (PersistenceException e) {
			throw new NegocioException("falha ao excluir registro");
		}
	}
}
