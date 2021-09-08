package com.sousa.sigpro.service;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.sousa.sigpro.model.Marca;
import com.sousa.sigpro.util.Suporte;
import com.sousa.sigpro.util.jpa.ConexaoManager;

public class TabelaMarcaService implements Serializable {

	private static final long serialVersionUID = 1L;

	String url = Suporte.URL_DATABASE + "?currentSchema=tabelas";

	public List<Marca> veiculos(String descricao) {

		List<Marca> lista = new ArrayList<>();

		try {

			Connection con = ConexaoManager.conexao(url, Suporte.USUARIO_BANCO_DADOS, Suporte.SENHA_BANCO_DADOS);

			String sql = "SELECT * FROM marca where UPPER(descricao) like "
					+ Suporte.getQuotedStr("%" + descricao.toUpperCase() + "%") + " order by descricao";

			Statement stm = con.createStatement();
			ResultSet rs = stm.executeQuery(sql);
			while (rs.next()) {
				lista.add(new Marca(rs.getLong("id"), rs.getString("descricao")));
			}
			con.close();

		} catch (Exception e) {

		}

		return lista;

	}

}