package com.sousa.sigpro.repository;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import com.sousa.sigpro.model.Contingencia;
import com.sousa.sigpro.model.tipo.TipoMedidaContingencia;
import com.sousa.sigpro.security.Seguranca;

public class Contingencias implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager manager;
	@Inject
	private Seguranca seguranca;

	public Contingencia porId(Long id) {
		String condicao = "select p from Contingencia p where p.id = " + id;
		return manager.createQuery(condicao, Contingencia.class).getSingleResult();
	}

	public List<Contingencia> lista() {
		return manager.createNamedQuery("Contingencia.lista", Contingencia.class)
				.setParameter("empresa", seguranca.getPessoaLogadoOrigem()).getResultList();
	}

	public List<Contingencia> lista(TipoMedidaContingencia medida) {
		return manager.createNamedQuery("Contingencia.medida", Contingencia.class).setParameter("medida", medida)
				.setParameter("empresa", seguranca.getPessoaLogadoOrigem()).getResultList();
	}

}