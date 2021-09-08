package com.sousa.sigpro.repository;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import com.sousa.sigpro.model.Funcao;
import com.sousa.sigpro.security.Seguranca;
import com.sousa.sigpro.util.Suporte;
import com.sousa.sigpro.util.jpa.Transactional;

public class Funcoes implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager manager;
	@Inject
	private Seguranca seguranca;

	public Funcao porId(Long id) {
		String condicao = "select p from Funcao p where p.id = " + id;
		return manager.createQuery(condicao, Funcao.class).getSingleResult();
	}

	public List<Funcao> lista(String descricao) {
		StringBuilder condicao = new StringBuilder();
		condicao.append("select f from Funcao f where 1 = 1 ");
		condicao.append(" and f.origem = " + seguranca.getPessoaLogadoOrigem().getId());
		if (Suporte.stringComValor(descricao))
			condicao.append(
					" and UPPER(f.descricao) like " + Suporte.getQuotedStr("%" + descricao.toUpperCase() + "%"));
		condicao.append(" ORDER BY f.descricao ");
		return manager.createQuery(condicao.toString(), Funcao.class).getResultList();
	}

	@Transactional
	public Funcao guardar(Funcao funcao) {
		if (funcao.getId() != null)
			funcao.setDataModifica(new Date());
		return manager.merge(funcao);
	}

	@Transactional
	public void remover(Funcao funcao) {
		manager.remove(porId(funcao.getId()));
	}

}