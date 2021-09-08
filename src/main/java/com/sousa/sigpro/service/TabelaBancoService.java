package com.sousa.sigpro.service;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.sousa.sigpro.model.Banco;
import com.sousa.sigpro.util.Suporte;
import com.sousa.sigpro.util.jpa.ConexaoManager;

public class TabelaBancoService implements Serializable {

	private static final long serialVersionUID = 1L;

	String url = Suporte.URL_DATABASE + "?currentSchema=tabelas";

	public List<Banco> listaOcupacao(String descricao) {

		List<Banco> lista = new ArrayList<>();

		try {

//			Connection con = ConexaoManager.conexao(url, "postgres", Suporte.SENHA_BANCO_DADOS);
			Connection con = ConexaoManager.conexao(url, Suporte.USUARIO_BANCO_DADOS, Suporte.SENHA_BANCO_DADOS);

			String sql = "SELECT * FROM banco where UPPER(descricao) like "
					+ Suporte.getQuotedStr("%" + descricao.toUpperCase() + "%") + " order by descricao";
			Statement stm = con.createStatement();
			ResultSet rs = stm.executeQuery(sql);
			while (rs.next()) {
				lista.add(new Banco(rs.getString("codigo"), rs.getString("descricao")));
			}
			con.close();

		} catch (Exception e) {
			throw new NegocioException(e.getMessage());
		}

		return lista;

	}

}
