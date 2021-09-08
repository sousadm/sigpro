package com.sousa.sigpro.repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import com.sousa.sigpro.model.Pessoa;
import com.sousa.sigpro.security.Seguranca;

public class UnidadesProdutivas implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Seguranca seguranca;
	@Inject
	private EntityManager manager;

	public Pessoa porId(Long id) {
		String condicao = "select p from Pessoa p where p.id = " + id;
		return manager.createQuery(condicao, Pessoa.class).getSingleResult();
	}

	public List<Pessoa> listaTodas() {
		try {
			String condicao = "from Pessoa where id = origem ";
			return manager.createQuery(condicao.toString(), Pessoa.class).getResultList();
		} catch (Exception e) {
			return new ArrayList<Pessoa>();
		}
	}

	public List<Pessoa> lista() {
		try {
			Pessoa matriz = matriz();
			String condicao = "from Pessoa ";
			if (seguranca.getPessoaLogadoOrigem().equals(matriz))
				condicao = condicao + " where id = origem ";
			else
				condicao = condicao + " where id = " + seguranca.getPessoaLogadoOrigem().getId();
			return manager.createQuery(condicao.toString(), Pessoa.class).getResultList();
		} catch (Exception e) {
			return new ArrayList<Pessoa>();
		}
	}

	public Pessoa matriz() {
		String condicao = "from Pessoa where id = origem order by id";
		List<Pessoa> lst = manager.createQuery(condicao.toString(), Pessoa.class).getResultList();
		return lst.get(0);
	}

}