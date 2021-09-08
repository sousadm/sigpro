package com.sousa.sigpro.repository;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import com.sousa.sigpro.model.Genero;

public class Generos implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager manager;

	public Genero porId(Long id) {
		String condicao = "select p from Genero p where p.id = " + id;
		return manager.createQuery(condicao, Genero.class).getSingleResult();
	}

	public List<Genero> lista(boolean isProduto) {
		String sql;
		if (isProduto)
			sql = "Genero.produto";
		else
			sql = "Genero.servico";
		return manager.createNamedQuery(sql, Genero.class).getResultList();
	}
}