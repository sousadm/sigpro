package com.sousa.sigpro.repository;

import java.io.Serializable;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import com.sousa.sigpro.model.clinica.ClinicaAgenda;

public class ConsultaAgendas implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager manager;

	public ClinicaAgenda porId(Long id) {
		return manager.find(ClinicaAgenda.class, id);
	}

}
