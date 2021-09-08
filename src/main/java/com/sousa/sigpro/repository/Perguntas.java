package com.sousa.sigpro.repository;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import com.sousa.sigpro.model.Pergunta;
import com.sousa.sigpro.model.Profissao;
import com.sousa.sigpro.util.jpa.Transactional;

public class Perguntas implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager manager;

	public Pergunta porId(Long id) {
		String condicao = "select p from Pergunta p where p.id = " + id;
		return manager.createQuery(condicao, Pergunta.class).getSingleResult();
	}

	@Transactional
	public void excluir(Pergunta pergunta) {
		pergunta = porId(pergunta.getId());
		manager.remove(pergunta);
	}

	@Transactional
	public Pergunta guardar(Pergunta pergunta) {
		return manager.merge(pergunta);
	}

	public List<Pergunta> lista(Profissao profissao) {
		TypedQuery<Pergunta> query = manager.createNamedQuery("Pergunta.porProfissao", Pergunta.class)
				.setParameter("profissao", profissao);
		List<Pergunta> lista = query.getResultList();
		return lista;
	}

}