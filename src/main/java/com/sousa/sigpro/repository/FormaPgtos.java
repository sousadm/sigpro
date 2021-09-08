package com.sousa.sigpro.repository;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import com.sousa.sigpro.model.FormaPgto;
import com.sousa.sigpro.model.RegraPgto;
import com.sousa.sigpro.security.Seguranca;
import com.sousa.sigpro.util.jpa.Transactional;

public class FormaPgtos implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager manager;
	@Inject
	private Seguranca seguranca;

	public FormaPgto porId(Long id) {
		String condicao = "select p from FormaPgto p where p.id = " + id;
		return manager.createQuery(condicao, FormaPgto.class).getSingleResult();
	}

	public List<FormaPgto> lista() {
		return this.manager.createNamedQuery("Pgto.lista", FormaPgto.class)
				.setParameter("origem", seguranca.getPessoaLogado().getOrigem()).getResultList();
	}

	public List<FormaPgto> listaDebCred() {
		return this.manager.createNamedQuery("Pgto.listaDebCre", FormaPgto.class)
				.setParameter("origem", seguranca.getPessoaLogado().getOrigem()).getResultList();
	}

	public RegraPgto regraDinheiro() {
		List<FormaPgto> lst = this.manager.createNamedQuery("Pgto.listaDinheiro", FormaPgto.class)
				.setParameter("origem", seguranca.getPessoaLogado().getOrigem()).getResultList();
		if (lst.isEmpty()) {
			return null;
		} else {
			return lst.get(0).getRegras().get(0);
		}
	}

	public RegraPgto regraCheque() {
		List<FormaPgto> lst = this.manager.createNamedQuery("Pgto.listaCheque", FormaPgto.class)
				.setParameter("origem", seguranca.getPessoaLogado().getOrigem()).getResultList();
		if (lst.isEmpty()) {
			return null;
		} else {
			return lst.get(0).getRegras().get(0);
		}
	}

	@Transactional
	public void remover(FormaPgto formaPgto) {
		formaPgto = porId(formaPgto.getId());
		manager.remove(formaPgto);
		manager.flush();
	}

	public RegraPgto regraSaldoCaixa() {
		List<FormaPgto> lst = this.manager.createNamedQuery("Pgto.listaSaldoCaixa", FormaPgto.class)
				.setParameter("origem", seguranca.getPessoaLogado().getOrigem()).getResultList();
		if (lst.isEmpty()) {
			return null;
		} else {
			return lst.get(0).getRegras().get(0);
		}
	}

	@Transactional
	public FormaPgto guardar(FormaPgto formaPgto) {

		if (formaPgto.getEntrada() == 100) {
			formaPgto.setMinimo(0);
			formaPgto.setDesconto(0);
		}

//		if (formaPgto.getRegras().size() == 0)
//			throw new NegocioException("Quantidade de parcelas inválida.");
//		else if (formaPgto.getEntrada() > 100)
//			throw new NegocioException("A entrada deve ser no máximo 100%");
		//else if (formaPgto.getRegras().size() == 1 && formaPgto.getEntrada() < 100)
		//	throw new NegocioException("Defina percentual de entrada em 100%!");
//		else if (formaPgto.getRegras().size() > 1 && formaPgto.getEntrada() >= 100)
//			throw new NegocioException("Defina percentual de entrada menor que 100%!");
//		else if (formaPgto.getDesconto() > 100)
//			throw new NegocioException("Desconto incorreto!");

		return manager.merge(formaPgto);
	}

}