package com.sousa.sigpro.controller.clinica;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.PrimeFaces;

import com.sousa.sigpro.model.clinica.ClinicaAgenda;
import com.sousa.sigpro.repository.ClinicaAgendas;
import com.sousa.sigpro.repository.filter.CondicaoFilter;

@Named
@ViewScoped
public class SelecaoClinicaAgendaBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private ClinicaAgendas agendas;

	private CondicaoFilter filtro = new CondicaoFilter();
	private List<ClinicaAgenda> lista = new ArrayList<>();

	public SelecaoClinicaAgendaBean() {

	}

	public void pesquisar() {
		lista = agendas.pesquisar(filtro);
	}

	public List<ClinicaAgenda> getLista() {
		return lista;
	}

	public CondicaoFilter getFiltro() {
		return filtro;
	}

	public void setFiltro(CondicaoFilter filtro) {
		this.filtro = filtro;
	}

	public void selecionar(ClinicaAgenda agenda) {
		ClinicaAgenda choosen = agendas.porId(agenda.getId());
		PrimeFaces.current().dialog().closeDynamic(choosen);
	}

}
