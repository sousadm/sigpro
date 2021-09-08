package com.sousa.sigpro.repository;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import com.sousa.sigpro.model.clinica.Exame;
import com.sousa.sigpro.repository.filter.CondicaoFilter;
import com.sousa.sigpro.util.Suporte;
import com.sousa.sigpro.util.jpa.ConexaoManager;

public class Exames implements Serializable {

	private static final long serialVersionUID = 1L;

	String url = Suporte.URL_DATABASE + "?currentSchema=tabelas";

	@Inject
	private EntityManager manager;

	public Exame porId(String id) {
		Exame cid = manager.find(Exame.class, id);
		if (cid == null)
			cid = poCodigo(id);
		return cid;
	}

	public Exame poCodigo(String codigo) {
		Exame cid = null;
		try {
			Connection con = ConexaoManager.conexao(url, "postgres", "contrate");
			String sql = "SELECT * FROM tuss where codigo = " + Suporte.getQuotedStr(codigo);
			Statement stm = con.createStatement();
			ResultSet rs = stm.executeQuery(sql);
			while (rs.next())
				cid = new Exame(rs.getString("codigo"), rs.getString("descricao"));
			con.close();
		} catch (Exception e) {
			cid = new Exame();
		}
		return cid;
	}

	public String consulta(CondicaoFilter filtro) {
		StringBuilder condicao = new StringBuilder();
		condicao.append(" where 1 = 1 ");
		if (filtro.getCodigo() != null && filtro.getCodigo().length() > 0)
			condicao.append(" AND UPPER(codigo) LIKE " + Suporte.getQuotedStr(filtro.getCodigo().toUpperCase() + "%"));
		if (filtro.getDescricao() != null && filtro.getDescricao().length() > 0)
			condicao.append(" AND UPPER(descricao) LIKE "
					+ Suporte.getQuotedStr("%" + filtro.getDescricao().toUpperCase() + "%"));
		return condicao.toString();
	}

	public int quantidadeFiltrados(CondicaoFilter filtro) {

		int qtde = 0;

		try {

			Connection con = ConexaoManager.conexao(url, "postgres", "contrate");
			StringBuilder condicao = new StringBuilder();
			condicao.append("SELECT count(*) FROM tuss ");
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

	public List<Exame> lista(CondicaoFilter filtro) {

		List<Exame> lista = new ArrayList<>();

		try {

			Connection con = ConexaoManager.conexao(url, "postgres", "contrate");

			StringBuilder condicao = new StringBuilder();
			condicao.append("SELECT * FROM tuss ");
			condicao.append(consulta(filtro));
			condicao.append(" order by descricao");

			if (filtro.getPageSize() != null && filtro.getPageSize() > 0) {
				condicao.append(" LIMIT " + filtro.getPageSize().intValue());
				condicao.append(" OFFSET(" + filtro.getPagina() + ") ");
			}

			Statement stm = con.createStatement();
			ResultSet rs = stm.executeQuery(condicao.toString());
			while (rs.next()) {
				lista.add(new Exame(rs.getString("codigo"), rs.getString("descricao")));
			}
			con.close();

		} catch (Exception e) {

		}

		return lista;
	}

}
