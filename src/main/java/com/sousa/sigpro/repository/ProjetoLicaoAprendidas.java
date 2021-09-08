package com.sousa.sigpro.repository;

import java.io.Serializable;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import com.sousa.sigpro.model.ProjetoLicaoAprendida;

public class ProjetoLicaoAprendidas implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager manager;

	public ProjetoLicaoAprendida porId(Long id) {
		String condicao = "select p from ProjetoLicaoAprendida p where p.id = " + id;
		return manager.createQuery(condicao, ProjetoLicaoAprendida.class).getSingleResult();
	}

}
