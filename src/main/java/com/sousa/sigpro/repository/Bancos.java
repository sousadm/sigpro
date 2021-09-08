package com.sousa.sigpro.repository;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import com.sousa.sigpro.model.Banco;
import com.sousa.sigpro.service.TabelaBancoService;

public class Bancos implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager manager;
	@Inject
	private TabelaBancoService servico;

	public Banco porId(Long id) {
		return manager.find(Banco.class, id);
	}

	public Banco porCodigo(String codigo) {
		try {
			return manager.createQuery("from Banco where codigo = :codigo", Banco.class).setParameter("codigo", codigo)
					.getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}

	public List<Banco> lista(String descricao) {
		return servico.listaOcupacao(descricao);
	}

	// public List<Banco> lista(CondicaoFilter filtro) {
	// String condicao = "select b from Banco b where 1 = 1 ";
	// if (!filtro.getCodigo().equals(""))
	// condicao += " and UPPER(codigo) like UPPER('%" + filtro.getCodigo() + "%') ";
	// if (!filtro.getNome().equals(""))
	// condicao += " and UPPER(descricao) like UPPER('%" + filtro.getNome() + "%')
	// ";
	// condicao += " order by codigo";
	// return manager.createQuery(condicao, Banco.class).getResultList();
	// }
}