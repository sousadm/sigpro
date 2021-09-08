package com.sousa.sigpro.repository;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import com.sousa.sigpro.model.clinica.ClinicaConsulta;
import com.sousa.sigpro.util.jpa.Transactional;

public class Consultas implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager manager;

	public ClinicaConsulta porId(Long id) {
		String condicao = "select p from Consulta p where p.id = " + id;
		return manager.createQuery(condicao, ClinicaConsulta.class).getSingleResult();
	}

	public List<ClinicaConsulta> lista() {
		return manager.createQuery("from Consulta", ClinicaConsulta.class).getResultList();
	}

	@Transactional
	public ClinicaConsulta guardar(ClinicaConsulta consulta) {
		return manager.merge(consulta);
	}
}