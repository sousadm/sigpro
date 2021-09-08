package com.sousa.sigpro.repository;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import com.sousa.sigpro.model.Comando;
import com.sousa.sigpro.model.tipo.TipoComando;
import com.sousa.sigpro.security.Seguranca;
import com.sousa.sigpro.util.jpa.Transactional;

public class Comandos implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager manager;
	@Inject
	private Seguranca seguranca;

	public Comando porId(Long id) {
		String condicao = "select p from Comando p where p.id = " + id;
		return manager.createQuery(condicao, Comando.class).getSingleResult();
	}

	public List<Comando> lista(String descricao) {
		return manager.createNamedQuery("Comando.porDescricao", Comando.class)
				.setParameter("descricao", "%" + descricao + "%")
				.setParameter("origem", seguranca.getPessoaLogadoOrigem()).getResultList();
	}

	public List<Comando> lista(TipoComando tipo) {
		return manager.createNamedQuery("Comando.porTipo", Comando.class).setParameter("tipo", tipo)
				.setParameter("origem", seguranca.getPessoaLogadoOrigem()).getResultList();
	}

	@Transactional
	public Comando guardar(Comando comando) {
		return manager.merge(comando);
	}

	@Transactional
	public void remover(Comando comando) {
		manager.remove(porId(comando.getId()));
	}

}