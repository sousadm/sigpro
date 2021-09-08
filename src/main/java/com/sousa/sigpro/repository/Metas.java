package com.sousa.sigpro.repository;

import java.io.Serializable;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import com.sousa.sigpro.model.Meta;
import com.sousa.sigpro.model.Pessoa;
import com.sousa.sigpro.model.tipo.TipoMeta;
import com.sousa.sigpro.util.jpa.Transactional;

public class Metas implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager manager;

	public Meta porId(Long id) {
		String condicao = "select p from Meta p where p.id = " + id;
		return manager.createQuery(condicao, Meta.class).getSingleResult();
	}

	@Transactional
	public Meta guardar(Meta meta) {
		return manager.merge(meta);
	}

	public Meta pesquisa(Pessoa pessoa, TipoMeta tipo, int ano, int mes) {
		try {
			return manager.createNamedQuery("Meta.consulta", Meta.class).setParameter("ano", ano)
					.setParameter("tipo", tipo).setParameter("mes", mes).setParameter("pessoa", pessoa)
					.getSingleResult();
		} catch (Exception e) {
			Meta meta = new Meta();
			meta = new Meta();
			meta.setPessoa(pessoa);
			meta.setAno(ano);
			meta.setMes(mes);
			meta.setTipo(tipo);
			return meta;
		}
	}

}
