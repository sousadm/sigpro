package com.sousa.sigpro.repository;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.sousa.sigpro.model.CaixaDiario;
import com.sousa.sigpro.model.Pessoa;
import com.sousa.sigpro.model.Planner;
import com.sousa.sigpro.model.Projeto;
import com.sousa.sigpro.model.tipo.TipoPlanner;
import com.sousa.sigpro.repository.filter.PlannerFilter;
import com.sousa.sigpro.security.Seguranca;
import com.sousa.sigpro.util.jpa.Transactional;

public class Planners implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager manager;

	@Inject
	private Seguranca seguranca;

	public Planner porId(Long id) {
		String condicao = "select p from Planner p where p.id = " + id;
		return manager.createQuery(condicao, Planner.class).getSingleResult();
	}

	public List<Planner> lista() {
		Pessoa pessoa = seguranca.getPessoaLogado().getOrigem();
		return manager.createQuery("from Planner where pessoa.origem = :pessoa", Planner.class)
				.setParameter("pessoa", pessoa).getResultList();
	}

	public List<Planner> lista(CaixaDiario diario) {
		String query = "select p from Planner p where p.diario = :diario order by p.id";
		return manager.createQuery(query, Planner.class).setParameter("diario", diario).getResultList();
	}

	public List<Planner> listaEapRaiz(Projeto projeto) {
		String query = "select p from Planner p where p.tipo = :tipo and p.projeto = :projeto and p.origem is null order by p.id";
		return manager.createQuery(query, Planner.class).setParameter("tipo", TipoPlanner.PROJETO_ENTREGA)
				.setParameter("projeto", projeto).getResultList();
	}

	public List<Planner> listaEapDetalhe(Planner planner) {
		String query = "select p from Planner p where p.tipo = :tipo and p.origem = :origem order by p.id";
		return manager.createQuery(query, Planner.class).setParameter("tipo", TipoPlanner.PROJETO_PACOTE)
				.setParameter("origem", planner).getResultList();
	}

	@Transactional
	public void guardar(List<Planner> lst) {
		for (Planner planner : lst) {
			if (planner.getData() == null)
				planner.setData(new Date());
			if (planner.getQuem() == null)
				planner.setQuem(seguranca.getPessoaLogado());
			manager.merge(planner);
		}
	}

	@Transactional
	public Planner guardar(Planner planner) {
		if (planner.getData() == null)
			planner.setData(new Date());
		if (planner.getQuem() == null)
			planner.setQuem(seguranca.getPessoaLogado());
		return manager.merge(planner);
	}

	@Transactional
	public void remover(Planner planner) {
		planner = porId(planner.getId());
		manager.remove(planner);
	}

	@SuppressWarnings("unchecked")
	public List<Planner> filtrado(PlannerFilter filtro) {
		Session session = this.manager.unwrap(Session.class);
		Criteria criteria = session.createCriteria(Planner.class);

		if (filtro.getInicio() != null)
			criteria.add(Restrictions.ge("quando", filtro.getInicio()));

		if (filtro.getTermino() != null)
			criteria.add(Restrictions.le("quando", filtro.getTermino()));

		if (filtro.getPessoa() != null)
			criteria.add(Restrictions.eq("quem", filtro.getPessoa()));

		return criteria.addOrder(Order.asc("id")).list();

	}

}