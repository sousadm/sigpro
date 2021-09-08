package com.sousa.sigpro.repository;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import com.sousa.sigpro.model.clinica.Convenio;
import com.sousa.sigpro.security.Seguranca;
import com.sousa.sigpro.util.jpa.Transactional;

public class Convenios implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager manager;
	@Inject
	private Seguranca seguranca;

	public Convenio porId(Long id) {
		return manager.find(Convenio.class, id);
	}

	public List<Convenio> lista() {
		return manager.createNamedQuery("Convenio.lista", Convenio.class)
				.setParameter("empresa", seguranca.getPessoaLogadoOrigem()).getResultList();
	}

	@Transactional
	public Convenio guardar(Convenio convenio) {
		return manager.merge(convenio);
	}

	@Transactional
	public void Excluir(Convenio convenio) {
		convenio = porId(convenio.getId());
		manager.remove(convenio);
	}

}
