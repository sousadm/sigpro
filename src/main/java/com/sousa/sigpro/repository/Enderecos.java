package com.sousa.sigpro.repository;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import com.sousa.sigpro.model.Endereco;
import com.sousa.sigpro.service.NegocioException;
import com.sousa.sigpro.util.jpa.Transactional;

public class Enderecos implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager manager;

	public Endereco porId(Long id) {
		return manager.find(Endereco.class, id);
	}

	public Endereco porCepNumero(String cep, int numero) {
		try {
			List<Endereco> lst = manager.createNamedQuery("Endereco.porCepNumero", Endereco.class)
					.setParameter("cep", cep).setParameter("numero", numero).getResultList();
			return lst.get(0);
		} catch (Exception e) {
			return null;
		}
	}

	@Transactional
	public void remover(Endereco endereco) {
		try {
			endereco = porId(endereco.getId());
			manager.remove(endereco);
			manager.flush();
		} catch (PersistenceException e) {
			throw new NegocioException("registro não pode ser excluído.");
		} catch (Exception e) {
			throw new NegocioException(e.getMessage());
		}
	}

}