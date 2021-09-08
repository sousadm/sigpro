package com.sousa.sigpro.repository;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import com.sousa.sigpro.model.Pergunta;
import com.sousa.sigpro.model.Questionario;
import com.sousa.sigpro.security.Seguranca;
import com.sousa.sigpro.util.jpa.Transactional;

public class Questionarios implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Seguranca seguranca;
	@Inject
	private EntityManager manager;

	public Questionario porId(Long id) {
		String condicao = "select p from Questionario p where p.id = " + id;
		return manager.createQuery(condicao, Questionario.class).getSingleResult();
	}

	public List<Pergunta> perguntaPorOrdem(Questionario questionario) {
		return manager.createNamedQuery("Pergunta.ordemAreaDescricao", Pergunta.class)
				.setParameter("questionario", questionario).getResultList();
	}

	public List<Questionario> lista(String filtro) {
		StringBuilder condicao = new StringBuilder();
		condicao.append("from Questionario where 1 = 1 ");
		if (filtro != null)
			condicao.append(" and UPPER(descricao) like '%" + filtro.toUpperCase() + "%' ");
		condicao.append(" order by descricao");
		return manager.createQuery(condicao.toString(), Questionario.class).getResultList();
	}

	public List<Questionario> listaComPergunta(String filtro) {
		StringBuilder condicao = new StringBuilder();
		condicao.append("select distinct q from Pergunta p join p.questionario q where 1 = 1 ");
		if (filtro != null)
			condicao.append(" and UPPER(q.descricao) like '%" + filtro.toUpperCase() + "%' ");
		condicao.append(" order by q.descricao");
		return manager.createQuery(condicao.toString(), Questionario.class).getResultList();
	}

	@Transactional
	public Questionario guardar(Questionario questionario) {
		if (questionario.getEmpresa() == null) {
			questionario.setEmpresa(seguranca.getPessoaLogadoOrigem());
		}
		return manager.merge(questionario);
	}

	@Transactional
	public void remover(Questionario questionario) {
		manager.remove(questionario);
	}

}