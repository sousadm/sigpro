package com.sousa.sigpro.repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import com.sousa.sigpro.model.CentroDeCusto;
import com.sousa.sigpro.model.tipo.TipoCentroCusto;
import com.sousa.sigpro.model.tipo.TipoEntradaSaida;
import com.sousa.sigpro.security.Seguranca;
import com.sousa.sigpro.service.NegocioException;
import com.sousa.sigpro.util.Suporte;
import com.sousa.sigpro.util.jpa.Transactional;;

public class CentrosDeCusto implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager manager;
	@Inject
	private Seguranca seguranca;

	public CentroDeCusto porId(Long id) {
		return manager.find(CentroDeCusto.class, id);
	}

	public List<CentroDeCusto> lista(TipoEntradaSaida tipo) {
		List<CentroDeCusto> lista = new ArrayList<>();
		List<CentroDeCusto> lst = manager.createNamedQuery("CentroCusto.lista", CentroDeCusto.class)
				.setParameter("empresa", seguranca.getPessoaLogadoOrigem()).getResultList();

		for (CentroDeCusto centro : lst) {
			if (centro.getTipo().getMovimento().equals(tipo)) {
				lista.add(centro);
			}
		}

		return lista;
	}

	public List<CentroDeCusto> listaCustoVeiculo() {
		StringBuilder condicao = new StringBuilder();
		condicao.append("select c from CentroDeCusto c ");
		condicao.append("where c.empresa.id = " + seguranca.getPessoaLogadoOrigem().getId());
		condicao.append("	and c.tipo = " + Suporte.getQuotedStr(TipoCentroCusto.DESPESA_COM_VEICULOS.name()));
		condicao.append("	and c.abastecimento = false  ");
		condicao.append("order by c.descricao");
		return manager.createQuery(condicao.toString(), CentroDeCusto.class).getResultList();
	}

	public List<CentroDeCusto> listaCustoAbastecimento() {
		StringBuilder condicao = new StringBuilder();
		condicao.append("select c from CentroDeCusto c ");
		condicao.append("where c.empresa.id = " + seguranca.getPessoaLogadoOrigem().getId());
		condicao.append("	and c.tipo = " + Suporte.getQuotedStr(TipoCentroCusto.DESPESA_COM_VEICULOS.name()));
		condicao.append("	and c.abastecimento = true  ");
		condicao.append("order by c.descricao");
		return manager.createQuery(condicao.toString(), CentroDeCusto.class).getResultList();
	}

	public List<CentroDeCusto> listaComissao() {
		List<CentroDeCusto> lst = lista(TipoCentroCusto.DESPESA_COM_PESSOAL);

		for (Iterator<CentroDeCusto> iterator = lst.iterator(); iterator.hasNext();) {
			CentroDeCusto custo = (CentroDeCusto) iterator.next();
			if (!custo.isComissao())
				iterator.remove();
		}

		return lst;
	}

	public List<CentroDeCusto> lista(TipoCentroCusto tipo) {
		return manager.createNamedQuery("CentroCusto.listaTipo", CentroDeCusto.class)
				.setParameter("empresa", seguranca.getPessoaLogadoOrigem()).setParameter("tipo", tipo).getResultList();
	}

	@Transactional
	public CentroDeCusto guardar(CentroDeCusto centro) {
		return manager.merge(centro);
	}

	@Transactional
	public void remover(CentroDeCusto custo) throws NegocioException {
		custo = porId(custo.getId());
		manager.remove(custo);
	}

	public CentroDeCusto porTipo(TipoCentroCusto tipo) {

		CentroDeCusto centroDeCusto = null;

		String condicao = "select c from CentroDeCusto c where empresa = :empresa and c.tipo = :tipo order by c.descricao";
		List<CentroDeCusto> lst = manager.createQuery(condicao, CentroDeCusto.class)
				.setParameter("empresa", seguranca.getPessoaLogadoOrigem()).setParameter("tipo", tipo).getResultList();

		if (lst != null && lst.size() > 0)
			return lst.get(0);

		return centroDeCusto;
	}

	public List<CentroDeCusto> lista() {
		StringBuilder condicao = new StringBuilder();
		condicao.append("select c from CentroDeCusto c where c.empresa = :empresa ");
		condicao.append("order by c.tipo, c.descricao");
		return manager.createQuery(condicao.toString(), CentroDeCusto.class)
				.setParameter("empresa", seguranca.getPessoaLogadoOrigem()).getResultList();
	}

}