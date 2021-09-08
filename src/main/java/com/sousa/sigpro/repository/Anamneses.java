package com.sousa.sigpro.repository;

import java.io.Serializable;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import com.sousa.sigpro.model.clinica.Anamnese;;

public class Anamneses implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager manager;

	public Anamnese porId(Long id) {
		String condicao = "select p from Anamnese p where p.id = " + id;
		return manager.createQuery(condicao, Anamnese.class).getSingleResult();
	}

}