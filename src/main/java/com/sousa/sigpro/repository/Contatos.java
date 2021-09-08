package com.sousa.sigpro.repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import com.sousa.sigpro.model.Contato;
import com.sousa.sigpro.model.Pessoa;
import com.sousa.sigpro.repository.filter.CondicaoFilter;
import com.sousa.sigpro.service.NegocioException;
import com.sousa.sigpro.util.Suporte;
import com.sousa.sigpro.util.jpa.Transactional;

public class Contatos implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager manager;

	public Contato porId(Long id) {
		return manager.find(Contato.class, id);
	}

	@Transactional
	public List<Contato> guardar(List<Contato> lista) {
		List<Contato> lst = new ArrayList<>();
		for (Contato contato : lista) {
			if (contato.getNome() != null) {
				contato = manager.merge(contato);
				lst.add(contato);
			}
		}
		return lst;
	}

	@Transactional
	public void remover(Contato contato) {
		try {
			contato = porId(contato.getId());
			manager.remove(contato);
			manager.flush();
		} catch (PersistenceException e) {
			throw new NegocioException("contato não pode ser excluído.");
		}
	}

	public List<Contato> porPessoa(Pessoa pessoa) {
		return manager.createNamedQuery("Contato.pessoa", Contato.class).setParameter("pessoa", pessoa).getResultList();
	}

	public Contato ContatoDePessoa(Pessoa pessoa) {
		List<Contato> lst = porPessoa(pessoa);
		if (lst != null && lst.size() > 0)
			return lst.get(0);
		else
			return null;
	}

	public int quantidadeFiltrados(CondicaoFilter filtro) {
		StringBuilder condicao = new StringBuilder();
		condicao.append("select count(*) from Contato c ");
		condicao.append(consulta(filtro));
		return ((Number) manager.createQuery(condicao.toString()).getSingleResult()).intValue();
	}

	@SuppressWarnings("unchecked")
	public List<Contato> lista(CondicaoFilter filtro) {
		StringBuilder condicao = new StringBuilder();
		condicao.append("select c from Contato c ");
		condicao.append(consulta(filtro));
		if (filtro.getSortField() != null)
			condicao.append(" ORDER BY " + filtro.getSortField() + (filtro.isAscendente() ? "" : " desc"));
		else
			condicao.append(" ORDER BY nome");

		Query query = manager.createQuery(condicao.toString(), Contato.class);
		if (filtro.getPrimeiroRegistro() != null) {
			query.setFirstResult(filtro.getPrimeiroRegistro().intValue());
			query.setMaxResults(filtro.getQuantidadeRegistros().intValue());
		}

		return query.getResultList();
	}

	private String consulta(CondicaoFilter filtro) {
		StringBuilder condicao = new StringBuilder();
		condicao.append("where 1 = 1 ");
		if (Suporte.stringComValor(filtro.getNome()))
			condicao.append(" and upper(c.nome) like UPPER('%" + filtro.getNome().toUpperCase() + "%') ");
		if (Suporte.stringComValor(filtro.getCodigo()))
			condicao.append(" and upper(c.email) like UPPER('%" + filtro.getCodigo().toUpperCase() + "%') ");
		if (Suporte.stringComValor(filtro.getCnpj()))
			condicao.append(" and upper(c.fone) like UPPER('%" + filtro.getCnpj().toUpperCase() + "%') ");
		if (Suporte.stringComValor(filtro.getCpf()))
			condicao.append(" and upper(c.celular) like UPPER('%" + filtro.getCpf().toUpperCase() + "%') ");
		return condicao.toString();
	}

}