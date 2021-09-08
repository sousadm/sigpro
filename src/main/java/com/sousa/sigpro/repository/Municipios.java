package com.sousa.sigpro.repository;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import com.sousa.sigpro.model.Municipio;
import com.sousa.sigpro.service.TabelaMunicipioService;

public class Municipios implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager manager;
	@Inject
	private TabelaMunicipioService tabelaMunicipioService;

	public Municipio porId(Long id) {
		Municipio municipio = localPorId(id);
		if (municipio == null)
			municipio = tabelaMunicipioService.codigo(id);
		return municipio;
	}

	public Municipio localPorId(Long id) {
		return manager.find(Municipio.class, id);
	}

	public List<Municipio> lista(String uf) {
		return tabelaMunicipioService.lista(uf, 0);
	}

}