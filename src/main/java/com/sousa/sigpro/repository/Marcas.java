package com.sousa.sigpro.repository;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import com.sousa.sigpro.model.Marca;
import com.sousa.sigpro.service.TabelaMarcaService;

public class Marcas implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager manager;
	@Inject
	private TabelaMarcaService marcas;

	public Marca porId(Long id) {
		return manager.find(Marca.class, id);
	}

	public List<Marca> lista(String filtro) {
		return marcas.veiculos(filtro);
	}
}