package com.sousa.sigpro.repository;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import com.sousa.sigpro.model.PlanoConta;
import com.sousa.sigpro.util.jpa.Transactional;

public class PlanoContas implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager manager;

	public PlanoConta porId(Long id) {
		String condicao = "select p from PlanoConta p where p.id = " + id;
		return manager.createQuery(condicao, PlanoConta.class).getSingleResult();
	}

	public List<PlanoConta> todas() {
		return manager.createQuery("from PlanoConta", PlanoConta.class).getResultList();
	}

	@Transactional
	public PlanoConta guardar(PlanoConta planoconta) {
		return manager.merge(planoconta);
	}

	@Transactional
	public void remover(PlanoConta planoconta) {
		planoconta = porId(planoconta.getId());
		manager.remove(planoconta);
	}

}
