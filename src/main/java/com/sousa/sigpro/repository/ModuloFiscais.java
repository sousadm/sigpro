package com.sousa.sigpro.repository;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import com.sousa.sigpro.model.ModuloFiscal;
import com.sousa.sigpro.model.Pessoa;
import com.sousa.sigpro.security.Seguranca;
import com.sousa.sigpro.util.jpa.Transactional;

public class ModuloFiscais implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Seguranca seguranca;
	@Inject
	private EntityManager manager;

	public ModuloFiscal porId(Long id) {
		String condicao = "select p from ModuloFiscal p where p.id = " + id;
		return manager.createQuery(condicao, ModuloFiscal.class).getSingleResult();
	}

	@Transactional
	public ModuloFiscal guardar(ModuloFiscal modulo) {
		return manager.merge(modulo);
	}

	@Transactional
	public void remover(ModuloFiscal moduloFiscal) {
		manager.remove(porId(moduloFiscal.getId()));
	}

	public List<ModuloFiscal> lista(Pessoa unidade) {
		StringBuilder condicao = new StringBuilder();
		condicao.append("select m from ModuloFiscal m where 1 = 1 ");
		if (unidade != null && unidade.isExiste())
			condicao.append("  and (m.pessoa.origem = " + unidade.getId());
		else
			condicao.append("  and (m.pessoa.origem = " + seguranca.getPessoaLogadoOrigem().getId());
		condicao.append("  	 or m.pessoa.origem.unidadeProdutiva.permiteCompartilhaModulo = true )");
		return manager.createQuery(condicao.toString(), ModuloFiscal.class).getResultList();
	}

}