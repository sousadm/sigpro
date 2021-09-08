package com.sousa.sigpro.repository;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import com.sousa.sigpro.model.ContratoAdesao;
import com.sousa.sigpro.model.Endereco;
import com.sousa.sigpro.model.Pessoa;
import com.sousa.sigpro.model.tipo.TipoEndereco;
import com.sousa.sigpro.model.tipo.TipoModulo;
import com.sousa.sigpro.model.tipo.TipoRotina;
import com.sousa.sigpro.service.NegocioException;
import com.sousa.sigpro.util.Suporte;
import com.sousa.sigpro.util.jpa.Transactional;

public class ContratoAdesaos implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager manager;
	@Inject
	private Pessoas pessoas;

	Statement stmnt = null;
	Connection conexao = null;

	public ContratoAdesao porId(Long id) {
		String condicao = "SELECT p from ContratoAdesao p WHERE p.id = " + id;
		return manager.createQuery(condicao, ContratoAdesao.class).getSingleResult();
	}

	@Transactional
	public ContratoAdesao guardar(ContratoAdesao contrato) {
		if (contrato.getCliente() == null)
			throw new NegocioException("Cliente indefinido para o contrato");
		if (contrato.getDataCadastro() == null)
			contrato.setDataCadastro(new Date());
		return manager.merge(contrato);
	}

	public List<ContratoAdesao> lista() {
		return manager.createQuery("from ContratoAdesao", ContratoAdesao.class).getResultList();
	}

	@Transactional
	public ContratoAdesao liberar(ContratoAdesao contrato) {

		if (contrato.getModulos() == null || contrato.getModulos().isEmpty())
			throw new NegocioException("módulos indefinidos para o contrato");

		contrato.setDataAtualizacao(new Date());
		contrato.setDataLiberado(new Date());
		return manager.merge(contrato);
	}

	public Pessoa pessoaContrato(ContratoAdesao contrato) {

		Long codigo = null;
		StringBuilder sql = new StringBuilder();
		Pessoa cliente = pessoas.cliente(contrato.getCliente().getId());
		Endereco endereco = cliente.getEndereco(TipoEndereco.COMERCIAL);
		String cpf_cnpj = Suporte.onlyNumbers(cliente.getCpfCnpjToString());

		try {
			sql = new StringBuilder();
			sql.append("SELECT id FROM " + contrato.getNomeSchema() + ".pessoa ");
			sql.append(
					"WHERE cpf = " + Suporte.getQuotedStr(cpf_cnpj) + " OR cnpj = " + Suporte.getQuotedStr(cpf_cnpj));
			ResultSet rs = stmnt.executeQuery(sql.toString());
			while (rs.next())
				codigo = rs.getLong("id");
		} catch (Exception e) {
			codigo = null;
		}

		if (codigo == null) {
			try {

				sql = new StringBuilder();
				sql.append("INSERT INTO " + contrato.getNomeSchema() + ".usuario ");
				sql.append(" (datacadastro, nome, permiteemailproprio, senha) ");
				sql.append("values(current_timestamp, " + Suporte.getQuotedStr(contrato.getNomeSchema())
						+ " ,false ,'202cb962ac59075b964b07152d234b70') ");
				stmnt.executeUpdate(sql.toString());

				sql = new StringBuilder();
				sql.append("INSERT INTO " + contrato.getNomeSchema() + ".municipio  (id, descricao, uf) values ( ");
				sql.append("  " + endereco.getMunicipio().getId());
				sql.append(" ," + Suporte.getQuotedStr(endereco.getLogradouro()));
				sql.append(" ," + Suporte.getQuotedStr(endereco.getMunicipio().getUf().getCodigo()) + ") ");
				stmnt.executeUpdate(sql.toString());

				sql = new StringBuilder();
				sql.append("INSERT INTO " + contrato.getNomeSchema() + ".pessoa ");
				sql.append(" (datacadastro, defineusuario, altura, valorCredito, nome, ");
				sql.append("  cnpj, cpf, documento, emissao, idestrangeiro, identidade, ");
				sql.append("  mae, nacionalidade, naturalidade, orgao, pai, peso, ie, ");
				sql.append("  iesub, im, codigoatividade, codigosuframa, nascimento, ");
				sql.append("  fantasia, fundacao, porte, tipoie, atividade, celular, ddd, email, ");
				sql.append("  fone, tipo, incentivocultural, inativausuariotransferido, permitecompartilhacliente, ");
				sql.append("  permitecompartilhafornecedor, permitecompartilhamodulo, permitecompartilhamotorista, ");
				sql.append("  permitecompartilhaproduto, usuario_id, nomedaloja ) 		"); // , endereco_id

				sql.append("SELECT current_timestamp, true, altura, valorCredito, nome, ");
				sql.append("  cnpj, cpf, documento, emissao, idestrangeiro, identidade, ");
				sql.append("  mae, nacionalidade, naturalidade, orgao, pai, peso, ie, ");
				sql.append("  iesub, im, codigoatividade, codigosuframa, nascimento, ");
				sql.append("  fantasia, fundacao, porte, tipoie, atividade, celular, ddd, email, ");
				sql.append("  fone, tipo, incentivocultural, inativausuariotransferido, permitecompartilhacliente, ");
				sql.append("  permitecompartilhafornecedor, permitecompartilhamodulo, permitecompartilhamotorista, ");
				sql.append("  permitecompartilhaproduto ");
				sql.append("	,(SELECT MAX(id) FROM " + contrato.getNomeSchema() + ".usuario ) ");
				sql.append(" ," + Suporte.getQuotedStr(contrato.getNomeSchema()));
				sql.append("FROM contrate.pessoa WHERE id = " + cliente.getId());
				stmnt.executeUpdate(sql.toString());

				sql = new StringBuilder();
				sql.append("INSERT INTO " + contrato.getNomeSchema() + ".endereco  ");
				sql.append("(bairro, cep, complemento, logradouro, numero, tipoendereco, municipio_id, pessoa_id) ");
				sql.append("VALUES(" + Suporte.getQuotedStr(endereco.getBairro()));
				sql.append(" , " + Suporte.getQuotedStr(endereco.getCep()));
				sql.append(" , " + Suporte.getQuotedStr(endereco.getComplemento()));
				sql.append(" , " + Suporte.getQuotedStr(endereco.getLogradouro()));
				sql.append(" , " + endereco.getNumero());
				sql.append(" , " + Suporte.getQuotedStr(endereco.getTipoEndereco().name()));
				sql.append(" , " + endereco.getMunicipio().getId());
				sql.append(" ,(SELECT max(id) from " + contrato.getNomeSchema() + ".pessoa) ) ");
				stmnt.executeUpdate(sql.toString());

				sql = new StringBuilder();
				sql.append("UPDATE " + contrato.getNomeSchema() + ".pessoa ");
				sql.append("	SET origem_id = (SELECT max(id) from " + contrato.getNomeSchema() + ".pessoa) ");
				stmnt.executeUpdate(sql.toString());

				sql = new StringBuilder();
				sql.append("INSERT INTO " + contrato.getNomeSchema() + ".usuario_grupos (grupos, usuario_id) ");
				sql.append(" VALUES('CEO', (SELECT MAX(id) FROM " + contrato.getNomeSchema() + ".usuario ) ) ");
				stmnt.executeUpdate(sql.toString());

				for (TipoRotina tipo : TipoRotina.values()) {
					sql = new StringBuilder();
					sql.append("INSERT INTO " + contrato.getNomeSchema() + ".usuario_rotinas (rotinas, usuario_id) ");
					sql.append(" VALUES(" + Suporte.getQuotedStr(tipo.name()) + ", (SELECT MAX(id) FROM "
							+ contrato.getNomeSchema() + ".usuario ) ) ");
					stmnt.executeUpdate(sql.toString());
				}

			} catch (Exception ex) {
				throw new NegocioException(ex.getMessage());
			}
		}

		try {
			sql = new StringBuilder();
			sql.append("SELECT id FROM " + contrato.getNomeSchema() + ".pessoa ");
			sql.append(
					"WHERE cpf = " + Suporte.getQuotedStr(cpf_cnpj) + " OR cnpj = " + Suporte.getQuotedStr(cpf_cnpj));
			ResultSet rs = stmnt.executeQuery(sql.toString());
			while (rs.next())
				codigo = rs.getLong("id");

			cliente.setId(codigo);

		} catch (Exception e) {
			throw new NegocioException(e.getMessage());
		}

		return cliente;

	}

	public void definirAcessos(ContratoAdesao contrato) throws SQLException {

		StringBuilder sql = new StringBuilder();
		conexao = DriverManager.getConnection(Suporte.getUrlDoSchema("contrate"), "postgres",
				Suporte.SENHA_BANCO_DADOS);
		stmnt = conexao.createStatement();

//		try {
//			
//			CallableStatement properCase = conexao.prepareCall("{ call contrate.clone_schema(?,?) }");
//			properCase.setString(1, "_padrao_");
//			properCase.setString(2, contrato.getNomeSchema());
//			properCase.execute();
//
//		} catch (Exception e) {
//			throw new NegocioException(e.getMessage());
//		}

		Pessoa contratante = pessoaContrato(contrato);

		if (contratante.getAtividade() == null)
			throw new NegocioException("atividade principal indefinida");

		sql = new StringBuilder();
		sql.append("UPDATE " + contrato.getNomeSchema() + ".pessoa ");
		sql.append("SET tokenhomologacao = " + Suporte.getQuotedStr(contrato.getTokenHomologacao()));
		sql.append("	,tokenproducao = " + Suporte.getQuotedStr(contrato.getTokenProducao()));
		sql.append("	,atividade = " + Suporte.getQuotedStr(contratante.getAtividade().name()));
		sql.append(" WHERE id = " + contratante.getId());
		stmnt.executeUpdate(sql.toString());

		sql = new StringBuilder();
		sql.append("DELETE FROM " + contrato.getNomeSchema() + ".pessoa_modulos ");
		sql.append("WHERE pessoa_id = " + contratante.getId());
		stmnt.executeUpdate(sql.toString());

		for (TipoModulo modulo : contrato.getModulos()) {
			sql = new StringBuilder();
			sql.append("INSERT INTO " + contrato.getNomeSchema() + ".pessoa_modulos (pessoa_id, modulos) ");
			sql.append("VALUES(" + contratante.getId() + ", " + Suporte.getQuotedStr(modulo.name()) + ") ");
			stmnt.executeUpdate(sql.toString());
		}

		conexao.close();

	}

	@Transactional
	public ContratoAdesao removerLicenca(ContratoAdesao contrato) {
		contrato.setDataValidade(null);
		return manager.merge(contrato);
	}

	public Date getDataValidade(String schema) throws SQLException {

		Date validade = null;
		Statement stmnt;
		Connection conexao = DriverManager.getConnection(Suporte.getUrlDoSchema("contrate"), "postgres",
				Suporte.SENHA_BANCO_DADOS);
		stmnt = conexao.createStatement();

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT a.datavalidade from contrate.contratoadesao a ");
		sql.append("INNER JOIN contrate.cliente c on c.id = a.cliente_id ");
		sql.append("WHERE a.nomeschema = " + Suporte.getQuotedStr(schema));
		ResultSet rs = stmnt.executeQuery(sql.toString());
		while (rs.next())
			validade = rs.getDate("datavalidade");

		return validade;
	}

}