package com.sousa.sigpro.repository;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import com.sousa.sigpro.model.clinica.Doenca;
import com.sousa.sigpro.util.Suporte;
import com.sousa.sigpro.util.jpa.ConexaoManager;

public class Doencas implements Serializable {

	private static final long serialVersionUID = 1L;

	String url = Suporte.URL_DATABASE + "?currentSchema=tabelas";

	@Inject
	private EntityManager manager;

	public Doenca porId(String id) {
		return manager.find(Doenca.class, id);
	}

	public Doenca poCodigo(String codigo) {
		Doenca cid = null;
		try {
			Connection con = ConexaoManager.conexao(url, "postgres", "contrate");
			String sql = "SELECT * FROM cid where codigo = " + Suporte.getQuotedStr(codigo);
			Statement stm = con.createStatement();
			ResultSet rs = stm.executeQuery(sql);
			while (rs.next())
				cid = new Doenca(rs.getString("codigo"), rs.getString("descricao"));
			con.close();
		} catch (Exception e) {
			cid = new Doenca();
		}
		return cid;
	}

	public List<Doenca> lista(String codigo, String descricao) {

		List<Doenca> lista = new ArrayList<>();

		try {

			Connection con = ConexaoManager.conexao(url, "postgres", "contrate");

			StringBuilder sql = new StringBuilder();
			sql.append("SELECT * FROM cid where 1 = 1");
			if (codigo != null && codigo.length() > 0)
				sql.append(" AND UPPER(codigo) LIKE " + Suporte.getQuotedStr(codigo.toUpperCase() + "%"));
			if (descricao != null && descricao.length() > 0)
				sql.append(" AND UPPER(descricao) LIKE " + Suporte.getQuotedStr("%" + descricao.toUpperCase() + "%"));
			sql.append(" order by descricao");
			Statement stm = con.createStatement();
			ResultSet rs = stm.executeQuery(sql.toString());
			while (rs.next()) {
				lista.add(new Doenca(rs.getString("codigo"), rs.getString("descricao")));
			}
			con.close();

		} catch (Exception e) {

		}

		return lista;
	}

}
