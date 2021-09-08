package com.sousa.sigpro.repository;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import com.sousa.sigpro.model.MedicamentoFilter;
import com.sousa.sigpro.model.clinica.Medicamento;
import com.sousa.sigpro.util.Suporte;
import com.sousa.sigpro.util.jpa.ConexaoManager;
import com.sousa.sigpro.util.jpa.Transactional;

public class Medicamentos implements Serializable {

	private static final long serialVersionUID = 1L;

	String url = Suporte.URL_DATABASE + "?currentSchema=tabelas";

	@Inject
	private EntityManager manager;

	@Transactional
	public Medicamento guardar(Medicamento medicamento) {
		return manager.merge(medicamento);
	}

	private String consulta(MedicamentoFilter filtro) {
		StringBuilder condicao = new StringBuilder();
		condicao.append("where 1 = 1 ");
		if (filtro.getClasse() != null && filtro.getClasse().length() > 0)
			condicao.append(" and UPPER(classe) like "
					+ Suporte.getQuotedStr("%" + filtro.getClasse().replace(" ", "%").toUpperCase() + "%"));
		if (filtro.getCodigo() != null && filtro.getCodigo().length() > 0)
			condicao.append(" and codigo like " + Suporte.getQuotedStr("%" + filtro.getCodigo() + "%"));
		if (filtro.getLaboratorio() != null && filtro.getLaboratorio().length() > 0)
			condicao.append(" and UPPER(detentor) like "
					+ Suporte.getQuotedStr("%" + filtro.getLaboratorio().replace(" ", "%").toUpperCase() + "%"));
		if (filtro.getPrincipio() != null && filtro.getPrincipio().length() > 0)
			condicao.append(" and UPPER(principio) like "
					+ Suporte.getQuotedStr("%" + filtro.getPrincipio().replace(" ", "%").toUpperCase() + "%"));
		if (filtro.getProduto() != null && filtro.getProduto().length() > 0)
			condicao.append(" and UPPER(descricao) like "
					+ Suporte.getQuotedStr("%" + filtro.getProduto().replace(" ", "%").toUpperCase() + "%"));
		return condicao.toString();
	}

	public Medicamento porId(String codigo) {
		try {
			return manager.createNamedQuery("Medicamento.porCodigo", Medicamento.class).setParameter("codigo", codigo)
					.getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}

	public Medicamento porCodigo(String codigo) {
		Medicamento medicamento = null;

		MedicamentoFilter filtro = new MedicamentoFilter();
		filtro.setCodigo(codigo);
		List<Medicamento> lst = lista(filtro);
		if (lst != null && lst.size() > 0)
			medicamento = lst.get(0);

		return medicamento;
	}

	public int quantidadeFiltrados(MedicamentoFilter filtro) {

		int qtde = 0;

		try {

			Connection con = ConexaoManager.conexao(url, "postgres", "contrate");
			StringBuilder condicao = new StringBuilder();
			condicao.append("select count(*) from medicamento ");
			condicao.append(consulta(filtro));

			Statement stm = con.createStatement();
			ResultSet rs = stm.executeQuery(condicao.toString());
			while (rs.next())
				qtde = rs.getInt(1);

		} catch (Exception e) {
			qtde = 0;
		}

		return qtde;

	}

	public List<Medicamento> lista(MedicamentoFilter filtro) {

		List<Medicamento> lst = new ArrayList<>();

		try {

			Connection con = ConexaoManager.conexao(url, "postgres", "contrate");
			StringBuilder condicao = new StringBuilder();
			condicao.append("SELECT * FROM Medicamento " + consulta(filtro));
			if (filtro.getSortField() == null)
				condicao.append(" ORDER BY codigo ");
			else
				condicao.append(" ORDER BY " + filtro.getSortField() + (filtro.isAscendente() ? "" : " desc"));

			if (filtro.getPageSize() != null && filtro.getPageSize() > 0) {
				condicao.append(" LIMIT " + filtro.getPageSize().intValue());
				condicao.append(" OFFSET(" + filtro.getPagina() + ") ");
			}

			Statement stm = con.createStatement();
			ResultSet rs = stm.executeQuery(condicao.toString());

			while (rs.next()) {
				Medicamento medicamento = new Medicamento();
				medicamento.setCodigo(rs.getString("codigo"));
				medicamento.setDescricao(rs.getString("descricao"));
				medicamento.setPrincipio(rs.getString("principio"));
				medicamento.setGrupo(rs.getString("grupo"));
				medicamento.setClasse(rs.getString("classe"));
				medicamento.setForma(rs.getString("forma"));
				medicamento.setDetentor(rs.getString("detentor"));
				medicamento.setAnvisa(rs.getString("anvisa"));
				medicamento.setTipo(rs.getString("tipo"));
				lst.add(medicamento);
			}
			con.close();

		} catch (Exception e) {

		}

		return lst;
	}

}