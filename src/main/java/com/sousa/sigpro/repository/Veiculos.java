package com.sousa.sigpro.repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import com.fincatto.documentofiscal.nfe400.classes.NFNotaInfoCombustivelTipo;
import com.sousa.sigpro.model.Abastecimento;
import com.sousa.sigpro.model.Marca;
import com.sousa.sigpro.model.Veiculo;
import com.sousa.sigpro.model.VeiculoDespesa;
import com.sousa.sigpro.repository.filter.CondicaoFilter;
import com.sousa.sigpro.security.Seguranca;
import com.sousa.sigpro.service.NegocioException;
import com.sousa.sigpro.util.Suporte;
import com.sousa.sigpro.util.jpa.Transactional;

public class Veiculos implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Seguranca seguranca;
	@Inject
	private EntityManager manager;

	public Veiculo porId(Long id) {
		return manager.find(Veiculo.class, id);
	}

	public Abastecimento abastecimentoPorId(Long id) {
		return manager.find(Abastecimento.class, id);
	}

	public VeiculoDespesa despesaPorId(Long id) {
		return manager.find(VeiculoDespesa.class, id);
	}

	public Veiculo porPlaca(String placa) {
		try {
			String condicao = "select p from Veiculo p where UPPER(p.placa) = "
					+ Suporte.getQuotedStr(placa.toUpperCase());
			return manager.createQuery(condicao, Veiculo.class).getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}

	@Transactional
	public void remover(Veiculo veiculo) {
		manager.remove(porId(veiculo.getId()));
	}

	@Transactional
	public void remover(Abastecimento abastecimento) {
		try {
			abastecimento = abastecimentoPorId(abastecimento.getId());
			manager.remove(abastecimento);
			manager.flush();
		} catch (PersistenceException e) {
			throw new NegocioException("registro não pode ser excluído.");
		} catch (Exception e) {
			throw new NegocioException(e.getMessage());
		}
	}

	@Transactional
	public void remover(VeiculoDespesa despesa) {
		try {
			despesa = despesaPorId(despesa.getId());
			manager.remove(despesa);
			manager.flush();
		} catch (PersistenceException e) {
			throw new NegocioException("registro não pode ser excluído.");
		} catch (Exception e) {
			throw new NegocioException(e.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	public List<Veiculo> lista(CondicaoFilter filtro) {

		StringBuilder condicao = new StringBuilder();
		condicao.append("select v from Veiculo v ");
		condicao.append(consulta(filtro));
		if (filtro.getSortField() != null)
			condicao.append(" ORDER BY " + filtro.getSortField() + (filtro.isAscendente() ? "" : " desc"));
		else
			condicao.append(" ORDER BY placa");

		Query query = manager.createQuery(condicao.toString(), Veiculo.class);
		if (filtro.getPrimeiroRegistro() != null) {
			query.setFirstResult(filtro.getPrimeiroRegistro().intValue());
			query.setMaxResults(filtro.getQuantidadeRegistros().intValue());
		}

		return query.getResultList();
	}

	public int quantidadeFiltrados(CondicaoFilter filtro) {
		StringBuilder condicao = new StringBuilder();
		condicao.append("select count(*) from Veiculo v ");
		condicao.append(consulta(filtro));
		return ((Number) manager.createQuery(condicao.toString()).getSingleResult()).intValue();
	}

	private String consulta(CondicaoFilter filtro) {

		StringBuilder condicao = new StringBuilder();
		condicao.append("where 1 = 1 ");
		if (seguranca.isUsuarioConvidado())
			condicao.append(" and v.pessoa = " + seguranca.getPessoaLogado().getId());
		else
			condicao.append(" and v.pessoa.origem = " + seguranca.getPessoaLogadoOrigem().getId());

		if (filtro != null) {
			if (filtro.getDisponivel() != null)
				condicao.append(" and v.disponivel = " + filtro.getDisponivel());
			if (filtro.getCodigo() != null && !"".equals(filtro.getCodigo()))
				condicao.append(" and UPPER(v.placa) like UPPER('%" + filtro.getCodigo() + "%') ");
			if (filtro.getNome() != null && !"".equals(filtro.getNome()))
				condicao.append(" and UPPER(v.pessoa.nome) like UPPER('%" + filtro.getNome() + "%') ");
		}

		return condicao.toString();
	}

	public NFNotaInfoCombustivelTipo[] lista_combustiveis(NFNotaInfoCombustivelTipo tipo) {
		List<NFNotaInfoCombustivelTipo> lst = new ArrayList<>();
		if (tipo == NFNotaInfoCombustivelTipo.ALCOOL || tipo == NFNotaInfoCombustivelTipo.ALCOOL_GAS_NATURAL
				|| tipo == NFNotaInfoCombustivelTipo.ALCOOL_GAS_NATURAL_VEICULAR
				|| tipo == NFNotaInfoCombustivelTipo.ALCOOL_GASOLINA
				|| tipo == NFNotaInfoCombustivelTipo.GASOLINA_ALCOOL_GAS_NATURAL) {
			lst.add(NFNotaInfoCombustivelTipo.ALCOOL);
		}
		if (tipo == NFNotaInfoCombustivelTipo.DIESEL || tipo == NFNotaInfoCombustivelTipo.DIESEL_GAS_NATURAL
				|| tipo == NFNotaInfoCombustivelTipo.DIESEL_GAS_NATURAL_VEICULAR) {
			lst.add(NFNotaInfoCombustivelTipo.DIESEL);
		}

		if (tipo == NFNotaInfoCombustivelTipo.GASOL_GAS_NATURAL_COMBUSTIVEL
				|| tipo == NFNotaInfoCombustivelTipo.GASOLINA
				|| tipo == NFNotaInfoCombustivelTipo.GASOLINA_ALCOOL_GAS_NATURAL
				|| tipo == NFNotaInfoCombustivelTipo.GASOLINA_GAS_NATURAL_VEICULAR) {
			lst.add(NFNotaInfoCombustivelTipo.GASOLINA);
		}

		if (tipo == NFNotaInfoCombustivelTipo.GAS_METANO || tipo == NFNotaInfoCombustivelTipo.GAS_NATURAL_VEICULAR
				|| tipo == NFNotaInfoCombustivelTipo.GASOL_GAS_NATURAL_COMBUSTIVEL
				|| tipo == NFNotaInfoCombustivelTipo.GASOGENIO
				|| tipo == NFNotaInfoCombustivelTipo.GASOLINA_GAS_NATURAL_VEICULAR
				|| tipo == NFNotaInfoCombustivelTipo.ALCOOL_GAS_NATURAL
				|| tipo == NFNotaInfoCombustivelTipo.DIESEL_GAS_NATURAL
				|| tipo == NFNotaInfoCombustivelTipo.DIESEL_GAS_NATURAL_VEICULAR
				|| tipo == NFNotaInfoCombustivelTipo.GASOLINA_ALCOOL_GAS_NATURAL) {
			lst.add(NFNotaInfoCombustivelTipo.GAS_NATURAL_VEICULAR);
		}

		NFNotaInfoCombustivelTipo[] itemsArray = new NFNotaInfoCombustivelTipo[lst.size()];
		itemsArray = lst.toArray(itemsArray);
		return itemsArray;

	}

	/**************************************
	 ************ ABASTECIMENTO ***********
	 **************************************/

	public int quantidadeDespesaFiltrados(CondicaoFilter filtro) {
		StringBuilder condicao = new StringBuilder();
		condicao.append("select count(*) from VeiculoDespesa a "); // join a.veiculo v join a.titulo t
		condicao.append(consulta_despesa_abastece(filtro));
		return ((Number) manager.createQuery(condicao.toString()).getSingleResult()).intValue();
	}

	@SuppressWarnings("unchecked")
	public List<VeiculoDespesa> lista_despesa(CondicaoFilter filtro) {

		StringBuilder condicao = new StringBuilder();
		condicao.append("select a from VeiculoDespesa a "); // join a.veiculo v join a.titulo t
		condicao.append(consulta_despesa_abastece(filtro));
		if (filtro.getSortField() == null) {
			condicao.append(" ORDER BY a.id desc");
		} else {
			condicao.append(" ORDER BY a." + filtro.getSortField() + (filtro.isAscendente() ? "" : " desc"));
		}

		Query query = manager.createQuery(condicao.toString(), VeiculoDespesa.class);
		if (filtro.getPrimeiroRegistro() != null) {
			query.setFirstResult(filtro.getPrimeiroRegistro().intValue());
			query.setMaxResults(filtro.getQuantidadeRegistros().intValue());
		}

		return query.getResultList();
	}

	@Transactional
	public Veiculo guardar(Veiculo veiculo) {

		if (veiculo.getPlaca() == null)
			throw new NegocioException("placa indefinida para cadastro");

		if (veiculo.getId() == null) {
			Veiculo outro = porPlaca(veiculo.getPlaca());
			if (outro != null)
				throw new NegocioException("placa já cadastrada");
		}

		if (veiculo.getPessoa() == null)
			throw new NegocioException("Defina o proprietário");
		if (veiculo.getDataCadastro() == null)
			veiculo.setDataCadastro(new Date());
		if (veiculo.getAno() < 1900)
			throw new NegocioException("Ano muito antigo para veículo");
		if (veiculo.getOdometroInicial() == null)
			veiculo.setOdometroInicial(0L);
		if (veiculo.getOdometroFinal() == null || veiculo.getOdometroFinal() < veiculo.getOdometroInicial()) {
			veiculo.setOdometroFinal(veiculo.getOdometroInicial());
		}
		if (veiculo.getMarca() == null || veiculo.getMarca().getDescricao() == null)
			throw new NegocioException("Defina a marca do veículo");

		Veiculo vei = manager.find(Veiculo.class, veiculo.getId());
		if (veiculo.getOdometroFinal() < vei.getOdometroFinal())
			veiculo.setOdometroFinal(vei.getOdometroFinal());

		if (veiculo.getOdometroAbastecimento() < vei.getOdometroAbastecimento())
			veiculo.setOdometroAbastecimento(vei.getOdometroAbastecimento());

		if (veiculo.getMarca() != null && veiculo.getMarca().getDescricao() != null) {
			Marca marca = manager.merge(veiculo.getMarca());
			veiculo.setMarca(marca);
			manager.flush();
		}
		veiculo.setPlaca(veiculo.getPlaca().toUpperCase());
		return manager.merge(veiculo);
	}

	@Transactional
	public Abastecimento guardar(Abastecimento abastecimento) {
		Veiculo veiculo = manager.find(Veiculo.class, abastecimento.getVeiculo().getId());

		if (abastecimento.getOdometro() > veiculo.getOdometroAbastecimento()) {
			veiculo.setOdometroAbastecimento(abastecimento.getOdometro());
			Long percorrido = abastecimento.getOdometro() - abastecimento.getVeiculo().getOdometroAbastecimento();
			double media = percorrido / abastecimento.getVolume();
			veiculo.setConsumoMedio(media);
		}
		if (abastecimento.getOdometro() > veiculo.getOdometroFinal())
			veiculo.setOdometroFinal(abastecimento.getOdometro());

		abastecimento.setVeiculo(veiculo);
		return manager.merge(abastecimento);

	}

	@Transactional
	public VeiculoDespesa guardar(VeiculoDespesa despesa) {
		Veiculo veiculo = manager.find(Veiculo.class, despesa.getVeiculo().getId());
		if (despesa.getOdometro() > veiculo.getOdometroFinal())
			veiculo.setOdometroFinal(despesa.getOdometro());

		despesa.setVeiculo(veiculo);
		return manager.merge(despesa);
	}

	/**************************************
	 ************ ABASTECIMENTO ***********
	 **************************************/

	private String consulta_despesa_abastece(CondicaoFilter filtro) {
		StringBuilder condicao = new StringBuilder();
		condicao.append("where 1 = 1 ");
		if (filtro.getVeiculo() != null && filtro.getVeiculo().getPlaca() != null)
			condicao.append(" and UPPER(a.veiculo.placa) like UPPER('%" + filtro.getVeiculo().getPlaca() + "%') ");
		if (filtro.getNome() != null && !"".equals(filtro.getNome()))
			condicao.append(" and UPPER(a.titulo.responsavel.nome) like UPPER('%" + filtro.getNome() + "%') ");
		if (seguranca.isUsuarioConvidado())
			condicao.append(" and a.veiculo.pessoa = " + seguranca.getPessoaLogado().getId());
		else
			condicao.append(" and a.veiculo.pessoa.origem = " + seguranca.getPessoaLogadoOrigem().getId());
		return condicao.toString();
	}

	public int quantidadeAbastecimentoFiltrados(CondicaoFilter filtro) {
		StringBuilder condicao = new StringBuilder();
		condicao.append("select count(*) from Abastecimento a ");
		condicao.append(consulta_despesa_abastece(filtro));
		return ((Number) manager.createQuery(condicao.toString()).getSingleResult()).intValue();
	}

	@SuppressWarnings("unchecked")
	public List<Abastecimento> lista_abastecimento(CondicaoFilter filtro) {

		List<Abastecimento> lst = null;

		try {

			StringBuilder condicao = new StringBuilder();
			condicao.append("select a from Abastecimento a ");
			condicao.append(consulta_despesa_abastece(filtro));
			if (filtro.getSortField() == null) {
				condicao.append(" ORDER BY a.id desc");
			} else {
				condicao.append(
						" ORDER BY a." + filtro.getSortField() + (filtro.isAscendente() ? "" : " desc"));
			}

			Query query = manager.createQuery(condicao.toString(), Abastecimento.class);
			if (filtro.getPrimeiroRegistro() != null) {
				query.setFirstResult(filtro.getPrimeiroRegistro().intValue());
				query.setMaxResults(filtro.getQuantidadeRegistros().intValue());
			}

			lst = query.getResultList();

			return lst;

		} catch (Exception e) {
			throw new NegocioException(e.getMessage());
		}

	}

}