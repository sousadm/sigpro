package com.sousa.sigpro.repository;

import java.io.Serializable;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import com.sousa.sigpro.model.Pessoa;
import com.sousa.sigpro.model.Usuario;
import com.sousa.sigpro.util.jsf.FacesUtil;

public class Usuarios implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager manager;

	public Usuario porId(Long id) {
		String condicao = "select p from Usuario p where p.id = " + id;
		return manager.createQuery(condicao, Usuario.class).getSingleResult();
	}

	public Usuario guardar(Usuario usuario) {
		return manager.merge(usuario);
	}

	public Usuario porPessoa(Pessoa pessoa) {
		Usuario usuario;
		try {
			String consultaSQL = "from Usuario where pessoa = " + pessoa.getId();
			usuario = manager.createQuery(consultaSQL, Usuario.class).getSingleResult();
		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
			usuario = new Usuario();
		}

		return usuario;

	}

	public Usuario porNome(String nome) {
		Usuario usuario = null;
		try {
			usuario = this.manager.createQuery("from Usuario where nome like :nome", Usuario.class)
					.setParameter("nome", nome).getSingleResult();

		} catch (NoResultException e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
		return usuario;
	}

}