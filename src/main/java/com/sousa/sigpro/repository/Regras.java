package com.sousa.sigpro.repository;

import java.io.Serializable;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import com.sousa.sigpro.model.RegraPgto;

public class Regras implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager manager;

	public RegraPgto porId(Long id) {
		String condicao = "select p from RegraPgto p where p.id = " + id;
		return manager.createQuery(condicao, RegraPgto.class).getSingleResult();
	}

}
