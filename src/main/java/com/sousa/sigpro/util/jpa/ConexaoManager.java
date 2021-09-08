package com.sousa.sigpro.util.jpa;

import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.sousa.sigpro.util.Suporte;

public class ConexaoManager {

	public static Connection conexao() throws SQLException, ClassNotFoundException, UnknownHostException {
		Class.forName("org.postgresql.Driver");
		return DriverManager.getConnection(Suporte.URL_DATABASE, Suporte.USUARIO_BANCO_DADOS,
				Suporte.SENHA_BANCO_DADOS);
	}

	public static Connection conexao(String url, String usuario, String senha)
			throws SQLException, ClassNotFoundException, UnknownHostException {
		Class.forName("org.postgresql.Driver");
		return DriverManager.getConnection(url, usuario, senha);
	}

	public static boolean empresaExisteSchema(String nome) throws UnknownHostException {
		Connection con = null;
		boolean existe = false;
		try {
			con = ConexaoManager.conexao();
			String sql = "SELECT * FROM pg_namespace WHERE Upper(nspname) = '" + nome.toUpperCase() + "'";
			Statement stm = con.createStatement();
			ResultSet rs = stm.executeQuery(sql);
			while (rs.next()) {
				existe = true;
			}
			con.close();
			return existe;
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static boolean matarSessaoNoSchema(int numero) throws UnknownHostException {
		Connection con = null;
		boolean existe = false;
		try {
			con = ConexaoManager.conexao();
			String sql = "SELECT pg_terminate_backend(" + numero + ") FROM pg_stat_activity "
					+ "WHERE ".concat(String.valueOf(numero)).concat(" <> pg_backend_pid() ")
					+ " AND datname = 'contrate' "
					+ "    AND usename = ".concat(Suporte.getQuotedStr(Suporte.USUARIO_BANCO_DADOS) + "; ");

			Statement stm = con.createStatement();
			ResultSet rs = stm.executeQuery(sql);
			while (rs.next()) {
				existe = true;
			}
			con.close();
			return existe;
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

}
