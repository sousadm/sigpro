package com.sousa.sigpro.repository;

import java.io.Serializable;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import com.sousa.sigpro.model.Profissao;
import com.sousa.sigpro.util.Suporte;

public class Profissoes implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager manager;

	public Profissao porId(String codigo) {
		String condicao = "select p from Profissao p where p.codigo = " + Suporte.getQuotedStr(codigo);
		return manager.createQuery(condicao, Profissao.class).getSingleResult();
	}

}