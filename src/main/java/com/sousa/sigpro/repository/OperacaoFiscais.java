package com.sousa.sigpro.repository;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import com.sousa.sigpro.model.OperacaoFiscal;
import com.sousa.sigpro.util.jpa.Transactional;
import com.sousa.sigpro.util.jsf.FacesUtil;

public class OperacaoFiscais implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager manager;

	public OperacaoFiscal porId(Long id) {
		String condicao = "select p from OperacaoFiscal p where p.id = " + id;
		return manager.createQuery(condicao, OperacaoFiscal.class).getSingleResult();
	}

	@Transactional
	public OperacaoFiscal guardar(OperacaoFiscal cfop) {
		return manager.merge(cfop);
	}

	@Transactional
	public void remover(OperacaoFiscal cfop) {
		try {
			cfop = porId(cfop.getId());
			manager.remove(cfop);
			manager.flush();
		} catch (PersistenceException e) {
			FacesUtil.addErrorMessage("Este registro não pode ser excluído");
		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public List<OperacaoFiscal> lista() {
		String condicao = "select c from OperacaoFiscal c order by c.id";
		return manager.createQuery(condicao, OperacaoFiscal.class).getResultList();
	}

	public List<OperacaoFiscal> listaNFe() {
		return manager.createNamedQuery("OperacaoFiscal.listaNFe", OperacaoFiscal.class).getResultList();
	}

	public List<OperacaoFiscal> listaNFSe() {
		return manager.createNamedQuery("OperacaoFiscal.listaNFSe", OperacaoFiscal.class).getResultList();
	}

	public List<OperacaoFiscal> listaCFe() {
		return manager.createNamedQuery("OperacaoFiscal.listaCFe", OperacaoFiscal.class).getResultList();
	}

}