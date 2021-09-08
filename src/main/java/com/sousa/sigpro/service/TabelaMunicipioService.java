package com.sousa.sigpro.service;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.sousa.sigpro.model.Municipio;
import com.sousa.sigpro.util.Suporte;
import com.sousa.sigpro.util.jpa.ConexaoManager;

public class TabelaMunicipioService implements Serializable {

	private static final long serialVersionUID = 1L;

	String url = Suporte.URL_DATABASE + "?currentSchema=tabelas";

	public TabelaMunicipioService() {

	}

	public Municipio codigo(Long id) {
		Municipio municipio = null;
		try {
			Connection con = ConexaoManager.conexao(url, Suporte.USUARIO_BANCO_DADOS, Suporte.SENHA_BANCO_DADOS);
			String sql = "SELECT * FROM municipio where id = " + id;
			Statement stm = con.createStatement();
			ResultSet rs = stm.executeQuery(sql);
			while (rs.next())
				municipio = new Municipio(rs.getLong("id"), rs.getString("descricao"), rs.getString("uf"));
			con.close();

		} catch (Exception e) {
			municipio = new Municipio();
		}

		return municipio;
	}

	public List<Municipio> lista(String uf, int first) {
		return lista(uf);
	}

	public List<Municipio> lista(String uf) {

		List<Municipio> lista = new ArrayList<>();

		try {

//			Connection con = ConexaoManager.conexao(url, "postgres", Suporte.SENHA_BANCO_DADOS);
			Connection con = ConexaoManager.conexao(url, Suporte.USUARIO_BANCO_DADOS, Suporte.SENHA_BANCO_DADOS);

			String sql = "SELECT * FROM municipio where UPPER(uf) = " + Suporte.getQuotedStr(uf.toUpperCase())
					+ " order by descricao";
			Statement stm = con.createStatement();
			ResultSet rs = stm.executeQuery(sql);
			while (rs.next()) {
				lista.add(new Municipio(rs.getLong("id"), rs.getString("descricao"), rs.getString("uf")));
			}
			con.close();

		} catch (Exception e) {

		}

		return lista;
	}

}