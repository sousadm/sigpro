package com.sousa.sigpro.repository;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import com.sousa.sigpro.model.ModuloPagamento;
import com.sousa.sigpro.model.Pessoa;
import com.sousa.sigpro.security.Seguranca;
import com.sousa.sigpro.util.jpa.Transactional;

public class ModuloPagamentos implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Seguranca seguranca;
	@Inject
	private EntityManager manager;

	public ModuloPagamento porId(Long id) {
		String condicao = "select p from ModuloPagamento p where p.id = " + id;
		return manager.createQuery(condicao, ModuloPagamento.class).getSingleResult();
	}

	@Transactional
	public ModuloPagamento guardar(ModuloPagamento modulo) {
		return manager.merge(modulo);
	}

	@Transactional
	public void remover(ModuloPagamento veiculo) {
		manager.remove(porId(veiculo.getId()));
	}

	public List<ModuloPagamento> lista(Pessoa unidade) {
		StringBuilder condicao = new StringBuilder();
		condicao.append("select m from ModuloPagamento m where 1 = 1 ");
		if (unidade != null && unidade.isExiste())
			condicao.append("  and (m.pessoa.origem = " + unidade.getId());
		else
			condicao.append("  and (m.pessoa.origem = " + seguranca.getPessoaLogadoOrigem().getId());
		condicao.append("  	 or m.pessoa.origem.unidadeProdutiva.permiteCompartilhaModulo = true )");
		return manager.createQuery(condicao.toString(), ModuloPagamento.class).getResultList();
	}

}