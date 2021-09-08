package com.sousa.sigpro.repository;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import com.sousa.sigpro.model.Cartao;
import com.sousa.sigpro.security.Seguranca;
import com.sousa.sigpro.util.SuporteData;
import com.sousa.sigpro.util.jpa.Transactional;

public class Cartoes implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager manager;
	@Inject
	private Seguranca seguranca;

	public Cartao porId(Long id) {
		String condicao = "select p from Cartao p where p.id = " + id;
		return manager.createQuery(condicao, Cartao.class).getSingleResult();
	}

	@Transactional
	public Cartao guardar(Cartao cartao) {
		cartao.setEmpresa(seguranca.getPessoaLogadoOrigem());
		return manager.merge(cartao);
	}

	@Transactional
	public void remover(Cartao cartao) {
		cartao = porId(cartao.getId());
		manager.remove(cartao);
		manager.flush();
	}

	public List<Cartao> lista() {
		return manager.createNamedQuery("Cartao.lista", Cartao.class)
				.setParameter("empresa", seguranca.getPessoaLogadoOrigem()).getResultList();
	}

	public Date faturaCartao(Cartao cartao, Date data) {
		Calendar vencimento = Calendar.getInstance();
		vencimento.setTime(data);
		vencimento.set(Calendar.DAY_OF_MONTH, cartao.getDiaVencimento());
		if (SuporteData.diaDoMes(data) > cartao.getDiaFechamento())
			vencimento.add(Calendar.MONTH, 1);
		return vencimento.getTime();
	}
	
}