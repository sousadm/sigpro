package com.sousa.sigpro.repository;

import java.io.Serializable;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import com.sousa.sigpro.model.Procedimento;
import com.sousa.sigpro.util.Suporte;
import com.sousa.sigpro.util.jpa.Transactional;

public class Procedimentos implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager manager;

	public Procedimento porId(String codigo) {
		String condicao = "from Procedimento where codigo = "+Suporte.getQuotedStr(codigo);
		return manager.createQuery(condicao, Procedimento.class).getSingleResult();
	}

	@Transactional
	public Procedimento guardar(Procedimento procedimento) {
		return manager.merge(procedimento);
	}

}
