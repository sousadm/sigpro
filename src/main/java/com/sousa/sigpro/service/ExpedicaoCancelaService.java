package com.sousa.sigpro.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import com.sousa.sigpro.model.Cliente;
import com.sousa.sigpro.model.Expedicao;
import com.sousa.sigpro.model.Titulo;
import com.sousa.sigpro.model.tipo.TipoExpedicao;
import com.sousa.sigpro.repository.Expedicoes;
import com.sousa.sigpro.repository.Pessoas;
import com.sousa.sigpro.repository.Titulos;
import com.sousa.sigpro.util.jpa.Transactional;

public class ExpedicaoCancelaService implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Pessoas pessoas;
	@Inject
	private Titulos titulos;
	@Inject
	private EntityManager manager;
	@Inject
	private Expedicoes expedicoes;

	@Transactional
	public Expedicao cancelar(Expedicao expedicao) {

		expedicao = this.expedicoes.porId(expedicao.getId());

		if (expedicao.isNaoCancelavel()) {
			throw new NegocioException("Não pode ser cancelado no status " + expedicao.getTipo().getDescricao() + ".");
		}

		if (expedicoes.existeExpedicaoComTituloPago(expedicao)) {
			throw new NegocioException("Operação não pode ser concluída, existe parcelas pagas");
		}

		/* CANCELAMENTO DE TITULOS */
		List<Titulo> lst = new ArrayList<>();
		lst = manager.createNamedQuery("Titulo.listaPorExpedicao", Titulo.class).setParameter("expedicao", expedicao)
				.getResultList();
		for (Titulo titulo : lst) {
			titulo.setDataCancelamento(new Date());
			titulo.setSaldo(0);
			titulos.guardarSemCommit(titulo);
		}

		if (expedicao.isEmitido()) {

			expedicoes.retornarItensEstoqueExpedicao(expedicao);

			/* RETORNA CREDITO PARA O CLIENTE */
			double difPerc = 0; // 1 - expedicao.getFormaPgto().getEntrada() / 100;
			double diferenca = difPerc * expedicao.getValorTotal();
			if (diferenca > 0) {
				Cliente cli = expedicao.getCliente();
				cli.setLimiteCredito(cli.getLimiteCredito() + diferenca);
				pessoas.guardarCliente(cli);
			}

		}

		expedicao.setTipo(TipoExpedicao.CAN);
		expedicao.setDataCancelamento(new Date());
		return expedicoes.guardar(expedicao);

	}
}