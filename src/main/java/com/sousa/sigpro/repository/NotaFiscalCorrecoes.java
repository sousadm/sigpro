package com.sousa.sigpro.repository;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import com.sousa.sigpro.model.NotaFiscal;
import com.sousa.sigpro.model.NotaFiscalCorrecao;
import com.sousa.sigpro.util.jpa.Transactional;

public class NotaFiscalCorrecoes implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager manager;

	public NotaFiscalCorrecao porId(Long id) {
		String condicao = "select p from NotaFiscalCorrecao p where p.id = " + id;
		return manager.createQuery(condicao, NotaFiscalCorrecao.class).getSingleResult();
	}

	public List<NotaFiscalCorrecao> lista(NotaFiscal nota) {
		return manager.createNamedQuery("NotaFiscalCorrecao.lista", NotaFiscalCorrecao.class).setParameter("nota", nota)
				.getResultList();
	}

	@Transactional
	public NotaFiscalCorrecao guardar(NotaFiscalCorrecao correcao) {
		return manager.merge(correcao);
	}

}