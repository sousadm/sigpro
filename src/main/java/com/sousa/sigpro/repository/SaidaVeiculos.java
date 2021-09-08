package com.sousa.sigpro.repository;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.sousa.sigpro.model.Veiculo;
import com.sousa.sigpro.model.VeiculoSaida;
import com.sousa.sigpro.repository.filter.CondicaoFilter;
import com.sousa.sigpro.service.NegocioException;
import com.sousa.sigpro.util.Suporte;
import com.sousa.sigpro.util.jpa.Transactional;

public class SaidaVeiculos implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager manager;

	public VeiculoSaida porId(Long id) {
		return manager.find(VeiculoSaida.class, id);
	}

	private String consulta(CondicaoFilter filtro) {
		StringBuilder condicao = new StringBuilder();
		condicao.append("where 1 = 1 ");
		if (Suporte.dataIsValida(filtro.getEmissaoInicial()))
			condicao.append(" and cast(dataSaida as date) >= '" + filtro.getInicio() + "' ");
		if (Suporte.dataIsValida(filtro.getEmissaoFinal()))
			condicao.append(" and cast(dataSaida as date) <= '" + filtro.getTermino() + "' ");
		return condicao.toString();
	}

	public int quantidadeFiltrados(CondicaoFilter filtro) {
		StringBuilder condicao = new StringBuilder();
		condicao.append("select count(*) from VeiculoSaida sv ");
		condicao.append(consulta(filtro));
		return ((Number) manager.createQuery(condicao.toString()).getSingleResult()).intValue();
	}

	@SuppressWarnings("unchecked")
	public List<VeiculoSaida> lista(CondicaoFilter filtro) {

		StringBuilder condicao = new StringBuilder();
		condicao.append("select s from VeiculoSaida s ");
		condicao.append(consulta(filtro));
		if (filtro.getSortField() != null)
			condicao.append(" ORDER BY " + filtro.getSortField() + (filtro.isAscendente() ? "" : " desc"));

		Query query = manager.createQuery(condicao.toString(), VeiculoSaida.class);
		if (filtro.getPrimeiroRegistro() != null) {
			query.setFirstResult(filtro.getPrimeiroRegistro().intValue());
			query.setMaxResults(filtro.getQuantidadeRegistros().intValue());
		}

		return query.getResultList();
	}

	@Transactional
	public VeiculoSaida guardar(VeiculoSaida saida) {

		if (saida.getMotorista() == null)
			throw new NegocioException("motorista indefinido");

		if (saida.getDataSaida() == null)
			throw new NegocioException("informe data/hora da saída do veículo");

		if (saida.getDataRetorno() != null)
			if (saida.getDataRetorno().before(saida.getDataSaida()))
				throw new NegocioException("data/hora retorno não pode ser inferior à saída");

		if (saida.getDataCadastro() == null)
			saida.setDataCadastro(new Date());

		Veiculo veiculo = saida.getVeiculo();
		veiculo = manager.merge(veiculo);
		manager.flush();

		saida.getVeiculo().setDisponivel(saida.getDataRetorno() != null);
		return manager.merge(saida);

	}

}