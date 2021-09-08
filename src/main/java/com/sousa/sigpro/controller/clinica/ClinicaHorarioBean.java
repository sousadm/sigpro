package com.sousa.sigpro.controller.clinica;

import java.io.Serializable;
import java.util.List;

import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.PersistenceException;

import com.sousa.sigpro.model.Pessoa;
import com.sousa.sigpro.model.clinica.ClinicaHorario;
import com.sousa.sigpro.repository.ClinicaHorarios;
import com.sousa.sigpro.repository.Pessoas;
import com.sousa.sigpro.service.NegocioException;
import com.sousa.sigpro.util.jsf.FacesUtil;

@Named
@ViewScoped
public class ClinicaHorarioBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private ClinicaHorarios horarios;
	@Inject
	private Pessoas pessoas;

	private ClinicaHorario horario;
	private List<ClinicaHorario> lista;
	private List<Pessoa> colaboradores;

	public ClinicaHorarioBean() {
		horario = new ClinicaHorario();
	}

	public void inicializar() {
		if (FacesUtil.isNotPostback()) {
			colaboradores = pessoas.colaboradores();
			pesquisar();
		}
	}

	public void pesquisar() {
		lista = horarios.lista();
	}

	public void salvar() {
		try {

			if (horario.getInicio() == null)
				throw new NegocioException("informe hora inicial");

			if (horario.getEncerramento() == null)
				throw new NegocioException("informe hora final");

			horario = horarios.guardar(horario);
			FacesUtil.addInfoMessage("Registro gravado com sucesso.");

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void excluir(ClinicaHorario horario) {
		try {
			horarios.remover(horario);
			FacesUtil.addInfoMessage("Registro excluído com sucesso.");
		} catch (PersistenceException e) {
			FacesUtil.addErrorMessage("Este registro não pode ser excluído");
		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public ClinicaHorario getHorario() {
		return horario;
	}

	public void setHorario(ClinicaHorario horario) {
		this.horario = horario;
	}

	public List<ClinicaHorario> getLista() {
		return lista;
	}

	public void setLista(List<ClinicaHorario> lista) {
		this.lista = lista;
	}

	public List<Pessoa> getColaboradores() {
		return colaboradores;
	}

}
