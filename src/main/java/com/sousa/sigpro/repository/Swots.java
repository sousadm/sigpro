package com.sousa.sigpro.repository;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import com.sousa.sigpro.model.Pessoa;
import com.sousa.sigpro.model.Swot;
import com.sousa.sigpro.model.tipo.TipoImportancia;
import com.sousa.sigpro.model.tipo.TipoSwot;
import com.sousa.sigpro.security.Seguranca;

public class Swots implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager manager;

	@Inject
	private Seguranca seguranca;

	public Swot porId(Long id) {
		String condicao = "select p from Swot p where p.id = " + id;
		return manager.createQuery(condicao, Swot.class).getSingleResult();
	}

	public List<Swot> lista(TipoSwot tipo) {
		Pessoa pessoa = seguranca.getPessoaLogado().getOrigem();
		return manager
				.createQuery("from Swot where pessoa.origem = :pessoa " + " and tipo = :tipo "
						+ " and importancia not like :importancia " + " order by pontuacao desc", Swot.class)
				.setParameter("importancia", TipoImportancia.ALTO).setParameter("pessoa", pessoa)
				.setParameter("tipo", tipo).getResultList();
	}

	public List<Swot> lista() {
		Pessoa pessoa = seguranca.getPessoaLogado().getOrigem();
		return manager.createQuery("from Swot where pessoa.origem = :pessoa " + " order by pontuacao desc", Swot.class)
				.setParameter("pessoa", pessoa).getResultList();
	}

	public Swot guardar(Swot swot) {
		if (swot.getId() == null)
			swot.setPessoa(seguranca.getPessoaLogado());
		swot.setPontuacao(swot.getImportancia().getValor() * swot.getProbabilidade().getValor());
		return manager.merge(swot);
	}

	public void remover(Swot swot) {
		swot = porId(swot.getId());
		manager.remove(swot);
	}

}