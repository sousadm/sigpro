package com.sousa.sigpro.repository;

import java.io.Serializable;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import com.sousa.sigpro.model.ConsultaSQL;
import com.sousa.sigpro.model.Usuario;
import com.sousa.sigpro.util.jpa.Transactional;

public class ConsultaSQLs implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager manager;

	@Transactional
	public ConsultaSQL guardar(ConsultaSQL consulta) {
		return manager.merge(consulta);
	}

	public Object consulta(Usuario usuario, String descricao) {
		return manager.createNamedQuery("ConsultaSQL.consulta", String.class).setParameter("usuario", usuario)
				.setParameter("descricao", descricao).getSingleResult();
	}

}