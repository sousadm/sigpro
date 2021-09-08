package com.sousa.sigpro.repository;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import com.sousa.sigpro.model.Cfop;

public class Cfops implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager manager;

	public Cfop porId(Long id) {
		String condicao = "select p from Cfop p where p.id = " + id;
		return manager.createQuery(condicao, Cfop.class).getSingleResult();
	}

	public List<Cfop> lista() {
		return manager.createNamedQuery("Cfop.lista", Cfop.class).getResultList();
	}

	public List<Cfop> listaSaida() {
		return manager.createNamedQuery("Cfop.listaSaida", Cfop.class).getResultList();
	}

}