package com.sousa.sigpro.repository;

import java.io.Serializable;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import com.sousa.sigpro.model.ManifestoCiot;

public class ManifestoCiots implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager manager;

	public ManifestoCiot porId(Long id) {
		try {
			String condicao = "from ManifestoCiot where id = " + id;
			return manager.createQuery(condicao, ManifestoCiot.class).getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}

}