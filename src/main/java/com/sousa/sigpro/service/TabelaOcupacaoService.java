package com.sousa.sigpro.service;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.sousa.sigpro.model.Profissao;
import com.sousa.sigpro.util.Suporte;
import com.sousa.sigpro.util.jpa.ConexaoManager;

public class TabelaOcupacaoService implements Serializable {

	private static final long serialVersionUID = 1L;

	String url = Suporte.URL_DATABASE + "?currentSchema=tabelas";

	public TabelaOcupacaoService() {

	}

	public List<Profissao> listaOcupacao(String descricao, int first) throws UnsupportedEncodingException {
		return listaOcupacao(descricao);
	}

	public List<Profissao> listaOcupacao(String descricao) {

		List<Profissao> lista = new ArrayList<>();

		try {
			Connection con = ConexaoManager.conexao(url, Suporte.USUARIO_BANCO_DADOS, Suporte.SENHA_BANCO_DADOS);
			String sql = "SELECT * FROM ocupacao where UPPER(descricao) like "
					+ Suporte.getQuotedStr("%" + descricao.toUpperCase() + "%") + " order by descricao";
			Statement stm = con.createStatement();
			ResultSet rs = stm.executeQuery(sql);
			while (rs.next()) {
				lista.add(new Profissao(rs.getString("codigo"), rs.getString("descricao")));
			}
			con.close();
		} catch (Exception e) {

		}

		return lista;

	}

}
