package com.sousa.sigpro.repository;

import java.io.Serializable;
import java.io.StringWriter;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NamedQuery;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import com.sousa.sigpro.controller.parametro.ParametroMail;
import com.sousa.sigpro.model.Agente;
import com.sousa.sigpro.model.Cliente;
import com.sousa.sigpro.model.ClienteDigital;
import com.sousa.sigpro.model.Colaborador;
import com.sousa.sigpro.model.Email;
import com.sousa.sigpro.model.Endereco;
import com.sousa.sigpro.model.Fornecedor;
import com.sousa.sigpro.model.Funcao;
import com.sousa.sigpro.model.Motorista;
import com.sousa.sigpro.model.Pessoa;
import com.sousa.sigpro.model.Produto;
import com.sousa.sigpro.model.Transportador;
import com.sousa.sigpro.model.Usuario;
import com.sousa.sigpro.model.Vendedor;
import com.sousa.sigpro.model.tipo.TipoEndereco;
import com.sousa.sigpro.model.tipo.TipoPessoa;
import com.sousa.sigpro.model.tipo.TipoRotina;
import com.sousa.sigpro.repository.filter.CondicaoFilter;
import com.sousa.sigpro.security.Seguranca;
import com.sousa.sigpro.service.CobrancaAsaasService.Customer;
import com.sousa.sigpro.service.NegocioException;
import com.sousa.sigpro.util.SendMail;
import com.sousa.sigpro.util.Suporte;
import com.sousa.sigpro.util.jpa.Transactional;
import com.sousa.sigpro.util.jsf.FacesUtil;

public class Pessoas implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Seguranca seguranca;
	@Inject
	private EntityManager manager;
	@Inject
	private ParametroMail param;

	public List<ClienteDigital> listaClienteDigital(Pessoa pessoa) {
		return manager.createNamedQuery("Pessoa.listaClienteDigital", ClienteDigital.class)
				.setParameter("pessoa", pessoa).getResultList();
	}

	public ClienteDigital clienteDigital(String identificador, Agente agente) {
		try {
			return manager.createNamedQuery("ClienteDigital.porIdentificadorAgenteAmbiente", ClienteDigital.class)
					.setParameter("identificador", identificador).setParameter("agente", agente)
					.setParameter("ambiente", agente.getAmbiente()).getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}

	public ClienteDigital clienteDigital(Pessoa pessoa, Agente agente) {
		try {
			return manager.createNamedQuery("ClienteDigital.pesquisa", ClienteDigital.class)
					.setParameter("pessoa", pessoa).setParameter("agente", agente)
					.setParameter("ambiente", agente.getAmbiente()).getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}

	@Transactional
	public Usuario guardar(Usuario usuario) {
		return manager.merge(usuario);
	}

	@Transactional
	public ClienteDigital guardar(Pessoa pessoa, Agente agente, Customer cobraCliente) {

		ClienteDigital clienteDigital = clienteDigital(pessoa, agente);
		if (clienteDigital == null)
			clienteDigital = new ClienteDigital();

		clienteDigital.setAgente(agente);
		clienteDigital.setPessoa(pessoa);
		clienteDigital.setDataCadastro(cobraCliente.dateCreated);
		clienteDigital.setIdentificador(cobraCliente.id);
		clienteDigital.setAmbiente(agente.getAmbiente());

		return manager.merge(clienteDigital);
	}

	public List<Endereco> listaEnderecos(Pessoa pessoa) {
		return manager.createNamedQuery("Endereco.porPessoa", Endereco.class).setParameter("pessoa", pessoa)
				.getResultList();
	}

	public List<Colaborador> listaOperadores() {
		String consulta = "select distinct t from Colaborador t join t.funcoes f ";
		return manager.createQuery(consulta, Colaborador.class).getResultList();
	}

	public List<Pessoa> listaAgenteAPI() {
		return manager.createNamedQuery("Pessoa.listaAgenteApi", Pessoa.class).getResultList();
	}

	public List<Colaborador> listaOperadorPorProduto(Produto produto) {

		String consulta = "select distinct t from Colaborador t join t.funcoes f where f in (:lista)";
		List<Colaborador> lstColaborador = null;
		List<Funcao> lstFuncao = new ArrayList<>();

		for (Funcao funcao : produto.getFuncoes())
			lstFuncao.add(funcao);

		if (lstFuncao.size() > 0)
			lstColaborador = manager.createQuery(consulta, Colaborador.class).setParameter("lista", lstFuncao)
					.getResultList();

		return lstColaborador;

	}

	public List<TipoRotina> rotinas(Usuario usuario) {
		usuario = manager.find(Usuario.class, usuario.getId());
		return usuario.getRotinas();
	}

	public Pessoa cliente(Long cliente) {
		try {
			String sql = "select p from Pessoa p where p.cliente = " + cliente;
			return manager.createQuery(sql, Pessoa.class).getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}

	public Pessoa cliente(Cliente cliente) {
		return cliente(cliente.getId());
	}

	public Pessoa fornecedor(Long fornecedor) {
		try {
			String sql = "select p from Pessoa p where p.fornecedor = " + fornecedor;
			return manager.createQuery(sql, Pessoa.class).getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}

	public Pessoa fornecedor(Fornecedor fornecedor) {
		return fornecedor(fornecedor.getId());
	}

	public void guardarRotinas(Pessoa pessoa, List<TipoRotina> lista) {

		String condicao;
		Query query;
		EntityTransaction trx = manager.getTransaction();
		trx.begin();

		condicao = "delete from " + seguranca.getEmpresa() + ".usuario_rotinas where usuario_id = "
				+ pessoa.getUsuario().getId();
		query = manager.createNativeQuery(condicao);
		query.executeUpdate();

		for (Iterator iterator = lista.iterator(); iterator.hasNext();) {
			String tipo = (String) iterator.next();
			condicao = "insert into " + seguranca.getEmpresa() + ".usuario_rotinas values("
					+ pessoa.getUsuario().getId() + ", '" + tipo + "') ";
			query = manager.createNativeQuery(condicao);
			query.executeUpdate();
		}
		trx.commit();

	}

	public Cliente guardarCliente(Cliente cliente) {
		return manager.merge(cliente);
	}

	public List<Pessoa> usuarios(Pessoa unidade) {
		return manager.createNamedQuery("Pessoa.usuarios", Pessoa.class).setParameter("origem", unidade.getOrigem())
				.getResultList();
	}

	@Transactional
	public void remover(Pessoa pessoa) throws NegocioException {
		try {
			pessoa = porId(pessoa.getId());
			manager.remove(pessoa);
			manager.flush();
		} catch (PersistenceException e) {
			throw new NegocioException("registro não pode ser excluído.");
		} catch (Exception e) {
			throw new NegocioException(e.getMessage());
		}
	}

	public Pessoa porCpf(String codigo) {
		try {
			StringBuilder condicao = new StringBuilder();
			condicao.append(
					"select p from Pessoa p where p.PF.cpf like " + Suporte.getQuotedStr(Suporte.onlyNumbers(codigo)));
			condicao.append(" and p.origem = " + seguranca.getPessoaLogadoOrigem().getId());
			return manager.createQuery(condicao.toString(), Pessoa.class).getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}

	public Pessoa porCnpj(String codigo) {
		try {
			return manager.createNamedQuery("Pessoa.porCnpj", Pessoa.class)
					.setParameter("cnpj", Suporte.onlyNumbers(codigo))
					.setParameter("origem", seguranca.getPessoaLogadoOrigem()).getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}

	public Pessoa porCpfCnpj(String codigo) {
		try {
			return manager.createNamedQuery("Pessoa.porCpfCnpj", Pessoa.class)
					.setParameter("codigo", Suporte.onlyNumbers(codigo))
					.setParameter("origem", seguranca.getPessoaLogadoOrigem()).getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}

	public Cliente clientePorId(Long id) {
		return manager.find(Cliente.class, id);
	}

	public Agente agentePorId(Long id) {
		return manager.find(Agente.class, id);
	}

	public Pessoa agente(Agente agente) {
		return manager.createNamedQuery("Pessoa.porAgente", Pessoa.class).setParameter("agente", agente)
				.getSingleResult();
	}

	public Transportador transportadorPorId(Long id) {
		return manager.find(Transportador.class, id);
	}

	public Vendedor vendedorPorId(Long id) {
		return manager.find(Vendedor.class, id);
	}

	public Motorista motoristaPorId(Long id) {
		return manager.find(Motorista.class, id);
	}

	public Colaborador colaboradorPorId(Long id) {
		return manager.find(Colaborador.class, id);
	}

	public Pessoa porColaborador(Colaborador colaborador) {
		String query = "select p from Pessoa p where p.colaborador = " + colaborador.getId();
		Pessoa p = null;
		try {
			p = manager.createQuery(query, Pessoa.class).getSingleResult();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return p;
	}

	public Pessoa porUsuario(String nome) {
		Pessoa pessoa = this.manager.createNamedQuery("Pessoa.porUsuario", Pessoa.class).setParameter("nome", nome)
				.getSingleResult();
		return pessoa;
	}

	public List<Pessoa> colaboradores() {
		StringBuilder condicao = new StringBuilder();
		condicao.append("select p from Pessoa p where p.defineColaborador = true ");
		condicao.append("	and p.origem = :origem order by p.nome");
		return manager.createQuery(condicao.toString(), Pessoa.class)
				.setParameter("origem", seguranca.getPessoaLogadoOrigem()).getResultList();
	}

	public List<Pessoa> vendedores() {
		return manager.createNamedQuery("Pessoa.vendedores", Pessoa.class).setParameter("nome", "%")
				.setParameter("origem", seguranca.getPessoaLogadoOrigem()).getResultList();
	}

	public List<Pessoa> usuarios() {
		return manager.createNamedQuery("Pessoa.usuarios", Pessoa.class)
				.setParameter("origem", seguranca.getPessoaLogadoOrigem()).getResultList();
	}

	public List<Pessoa> pacientes() {
		return manager.createNamedQuery("Pessoa.fisicas", Pessoa.class).setParameter("nome", "%")
				.setParameter("origem", seguranca.getPessoaLogadoOrigem()).getResultList();
	}

	public List<Pessoa> agentes() {
		return manager.createNamedQuery("Pessoa.agentes", Pessoa.class)
				.setParameter("origem", seguranca.getPessoaLogadoOrigem()).getResultList();
	}

	public List<Pessoa> profissionais(String nome) {
		if (nome == null)
			nome = "";
		return manager.createNamedQuery("Pessoa.vendedores", Pessoa.class)
				.setParameter("origem", seguranca.getPessoaLogadoOrigem())
				.setParameter("nome", nome.toUpperCase() + "%").getResultList();
	}

	public List<Pessoa> porNome(String nome) {
		return this.manager.createNamedQuery("Pessoa.porNome", Pessoa.class)
				.setParameter("origem", seguranca.getPessoaLogadoOrigem())
				.setParameter("nome", "%" + nome.toUpperCase() + "%").getResultList();
	}

	public void enviarEmailBemvindo(Pessoa p) {
		try {

			URL resource = getClass().getResource("/");
			String path = resource.getPath() + "/emails";
			Properties props = new Properties();
			props.setProperty("file.resource.loader.path", path);
			VelocityEngine engine = new VelocityEngine(props);
			VelocityContext context = new VelocityContext();
			engine.init();

			Template template = engine.getTemplate("bemvindo.template");
			context.put("usuario", p.getUsuario().getNome());
			context.put("senha", p.getUsuario().getSenha());
			context.put("locale", new Locale("pt", "BR"));
			StringWriter writer = new StringWriter();
			template.merge(context, writer);

			Email email = new Email();
			email.setConteudo(writer.toString());
			email.setAssunto(seguranca.getEmpresa());
			email.setDestinatarioMail(p.getEmail());
			email.setDestinatarioNome(p.getNome());

			param.ler();
			SendMail sm = new SendMail(param);
			sm.enviar(email);

			FacesUtil.addInfoMessage("Enviado com sucesso.");

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void enviarEmailSenha(Pessoa p, String senha) {
		try {

			SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			String dataStr = fmt.format(new Date());

			String arquivo = "senha.template";
			URL resource = getClass().getResource("/");
			String path = resource.getPath() + "/emails";
			Properties props = new Properties();
			props.setProperty("file.resource.loader.path", path);
			VelocityEngine engine = new VelocityEngine(props);
			VelocityContext context = new VelocityContext();
			engine.init();

			Endereco enderecoEmpresa = seguranca.enderecoEmpresa();

			Template template = engine.getTemplate(arquivo);
			context.put("usuario", p.getUsuario().getNome());
			context.put("senha", senha);
			context.put("data", dataStr);
			context.put("representante", p.getNome());
			context.put("locale", new Locale("pt", "BR"));
			context.put("home", seguranca.getPessoaLogadoOrigem().getHome());
			context.put("empresa", seguranca.getPessoaLogadoOrigem().getPJ().getFantasia());
			context.put("endereco", enderecoEmpresa.toString());
			context.put("contato", seguranca.getPessoaLogadoOrigem().getFone());
			context.put("contratada", seguranca.getEmpresa());
			StringWriter writer = new StringWriter();
			template.merge(context, writer);

			Email email = new Email();
			email.setConteudo(writer.toString());
			email.setAssunto("Senha de acesso");
			email.setDestinatarioMail(p.getEmail());
			email.setDestinatarioNome(p.getNome());
			email.setRemetenteNome(seguranca.getEmpresa());

			param.ler();
			SendMail sm = new SendMail(param);
			sm.enviar(email);

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	public List<Pessoa> lista(CondicaoFilter filtro) {
		StringBuilder condicao = new StringBuilder();
		condicao.append("select p from Pessoa p ");
		condicao.append(consulta(filtro));
		if (filtro.getSortField() != null)
			condicao.append(" ORDER BY " + filtro.getSortField() + (filtro.isAscendente() ? "" : " desc"));
		else
			condicao.append(" ORDER BY nome");

		Query query = manager.createQuery(condicao.toString(), Pessoa.class);
		if (filtro.getPrimeiroRegistro() != null) {
			query.setFirstResult(filtro.getPrimeiroRegistro().intValue());
			query.setMaxResults(filtro.getQuantidadeRegistros().intValue());
		}

		return query.getResultList();
	}

	private String consultaCliente(CondicaoFilter filtro) {
		StringBuilder condicao = new StringBuilder();
		condicao.append("where p.defineCliente = true ");
		condicao.append(" 	and (p.origem = " + seguranca.getPessoaLogadoOrigem().getId());
		condicao.append("  		or p.origem.unidadeProdutiva.permiteCompartilhaCliente = true) ");
		if (filtro != null) {
			if (filtro.getNome().length() > 0)
				condicao.append(" and upper(p.nome) like UPPER('%" + filtro.getNome().toUpperCase() + "%') ");
			if (filtro.getCodigo().length() > 0) {
				condicao.append(" and ( (p.tipo = 'PF' and p.PF.cpf like '%" + filtro.getCodigo() + "%') ");
				condicao.append("  	or (p.tipo = 'PJ' and p.PJ.cnpj like '%" + filtro.getCodigo() + "%') ) ");
			}
		}
		return condicao.toString();
	}

	@SuppressWarnings("unchecked")
	public List<Pessoa> clientePorNome(CondicaoFilter filtro) {
		StringBuilder condicao = new StringBuilder();
		condicao.append("select p from Pessoa p join p.cliente c ");
		condicao.append(consultaCliente(filtro));
		if (filtro.getSortField() != null)
			condicao.append(" ORDER BY p." + filtro.getSortField() + (filtro.isAscendente() ? "" : " desc"));

		Query query = manager.createQuery(condicao.toString(), Pessoa.class);
		if (filtro.getPrimeiroRegistro() != null) {
			query.setFirstResult(filtro.getPrimeiroRegistro().intValue());
			query.setMaxResults(filtro.getQuantidadeRegistros().intValue());
		}

		return query.getResultList();
	}

	private String consulta(CondicaoFilter filtro) {

		String condTipo = "";
		StringBuilder condicao = new StringBuilder();
		condicao.append("where p.origem = " + seguranca.getPessoaLogadoOrigem().getId());
		if (filtro != null) {
			if (Suporte.stringComValor(filtro.getNome()))
				condicao.append(" and upper(p.nome) like UPPER('%" + filtro.getNome().toUpperCase() + "%') ");
			if (filtro.getCodigo() != null && filtro.getCodigo().length() > 0) {
				condicao.append(" and ( (p.tipo = 'PF' and p.PF.cpf like '%" + filtro.getCodigo() + "%') ");
				condicao.append("  	or (p.tipo = 'PJ' and p.PJ.cnpj like '%" + filtro.getCodigo() + "%') ) ");
			}
		}

		if (filtro.getTipoPessoa() != null && filtro.getTipoPessoa().length > 0)
			for (TipoPessoa tipo : filtro.getTipoPessoa()) {
				if (condTipo != "")
					condTipo += ", ";
				condTipo += "'" + tipo + "'";
			}

		if (condTipo != "")
			condicao.append(" and tipo in (" + condTipo + ")");

		if (filtro.getNome() != null)
			condicao.append(" and UPPER(nome) like UPPER('%" + filtro.getNome().toUpperCase() + "%') ");

		if (filtro.getSql() != null && !filtro.getSql().equals(""))
			condicao.append(" and " + filtro.getSql());

		return condicao.toString();

	}

	public int quantidadeFiltrados(CondicaoFilter filtro) {
		StringBuilder condicao = new StringBuilder();
		condicao.append("select count(*) from Pessoa p ");
		condicao.append(consulta(filtro));
		return ((Number) manager.createQuery(condicao.toString()).getSingleResult()).intValue();
	}

	public int quantidadeClienteFiltrados(CondicaoFilter filtro) {
		StringBuilder condicao = new StringBuilder();
		condicao.append("select count(*) from Pessoa p join p.cliente c ");
		condicao.append(consultaCliente(filtro));
		return ((Number) manager.createQuery(condicao.toString()).getSingleResult()).intValue();
	}

	public List<Pessoa> fornecedores(CondicaoFilter filtro) {
		try {

			StringBuilder condicao = new StringBuilder();
			condicao.append("select p from Pessoa p join p.fornecedor c where p.defineFornecedor = true ");
			condicao.append(" and (p.origem = " + seguranca.getPessoaLogadoOrigem().getId());
			condicao.append("  		or p.origem.unidadeProdutiva.permiteCompartilhaFornecedor = true) ");
			if (filtro != null) {
				if (filtro.getNome().length() > 0)
					condicao.append(" and upper(p.nome) like UPPER('%" + filtro.getNome().toUpperCase() + "%') ");
				if (filtro.getCodigo().length() > 0) {
					condicao.append(" and ( (p.tipo = 'PF' and p.PF.cpf like '%" + filtro.getCodigo() + "%') ");
					condicao.append("  	or (p.tipo = 'PJ' and p.PJ.cnpj like '%" + filtro.getCodigo() + "%') ) ");
				}
			}
			condicao.append(" ORDER BY p.nome");
			return manager.createQuery(condicao.toString(), Pessoa.class).getResultList();

		} catch (Exception e) {
			return new ArrayList<Pessoa>();
		}
	}

	public List<Pessoa> motoristas(CondicaoFilter filtro) {
		try {

			StringBuilder condicao = new StringBuilder();
			condicao.append("select p from Pessoa p join p.motorista m where p.defineMotorista = true ");
			condicao.append(" and (p.origem = " + seguranca.getPessoaLogadoOrigem().getId());
			condicao.append("  		or p.origem.unidadeProdutiva.permiteCompartilhaMotorista = true) ");
			if (filtro != null) {
				if (filtro.getNome().length() > 0)
					condicao.append(" and UPPER(p.nome) like UPPER('%" + filtro.getNome().toUpperCase() + "%') ");
				if (filtro.getCodigo().length() > 0)
					condicao.append(" and p.tipo = 'PF' and p.PF.cpf like '%" + filtro.getCodigo() + "%' ");
			}
			condicao.append(" ORDER BY p.nome");
			return manager.createQuery(condicao.toString(), Pessoa.class).getResultList();

		} catch (Exception e) {
			return new ArrayList<Pessoa>();
		}
	}

	public List<Pessoa> transportadores(CondicaoFilter filtro) {
		try {

			StringBuilder condicao = new StringBuilder();
			condicao.append("select p from Pessoa p join p.transportador c where p.defineTransportador = true ");
			// condicao.append(" and (p.origem = " +
			// seguranca.getPessoaLogadoOrigem().getId());
			// condicao.append(" or
			// p.origem.unidadeProdutiva.permiteCompartilhaTransportador = true) ");
			if (filtro != null) {
				if (filtro.getNome().length() > 0)
					condicao.append(" and upper(p.nome) like UPPER('%" + filtro.getNome().toUpperCase() + "%') ");
				if (filtro.getCodigo().length() > 0) {
					condicao.append(" and ( (p.tipo = 'PF' and p.PF.cpf like '%" + filtro.getCodigo() + "%') ");
					condicao.append("  	or (p.tipo = 'PJ' and p.PJ.cnpj like '%" + filtro.getCodigo() + "%') ) ");
				}
			}
			condicao.append(" ORDER BY p.nome");
			return manager.createQuery(condicao.toString(), Pessoa.class).getResultList();

		} catch (Exception e) {
			return new ArrayList<Pessoa>();
		}
	}

	@Transactional
	public Pessoa guardar(Pessoa pessoa) {

		if (pessoa.getTipo() == TipoPessoa.PF) {
			pessoa.getPF().setCpf(Suporte.onlyNumbers(pessoa.getPF().getCpf()));

			if (!Suporte.isCPF(pessoa.getPF().getCpf()))
				throw new NegocioException("CPF inválido!");

			Pessoa PF = this.porCpf(pessoa.getPF().getCpf());
			if (PF != null && !PF.equals(pessoa))
				throw new NegocioException("CPF já cadastrado!");

		} else if (pessoa.getTipo() == TipoPessoa.PJ) {
			pessoa.getPJ().setCnpj(Suporte.onlyNumbers(pessoa.getPJ().getCnpj()));

			if (!Suporte.isCNPJ(pessoa.getPJ().getCnpj()))
				throw new NegocioException("CNPJ inválido!");

			Pessoa PJ = this.porCnpj(pessoa.getPJ().getCnpj());
			if (PJ != null && !PJ.equals(pessoa))
				throw new NegocioException("CNPJ já cadastrado!");
		}

		if (pessoa.getOrigem() == null) {
			pessoa.setOrigem(seguranca.getPessoaLogadoOrigem());
		}

		if (pessoa.getPF() != null && pessoa.getPF().getProfissao() != null) {
			manager.merge(pessoa.getPF().getProfissao());
			manager.flush();
		}

		for (Endereco endereco : pessoa.getEnderecos()) {
			endereco.setCep(Suporte.onlyNumbers(endereco.getCep()));
			if (endereco.getMunicipio() != null) {
				endereco.setPessoa(pessoa);
				manager.merge(endereco.getMunicipio());
				manager.flush();
			}
		}
		return manager.merge(pessoa);
	}

	public Usuario usuarioPorId(Long id) {
		return manager.find(Usuario.class, id);
	}

	public Pessoa porId(Long id) {
		return manager.find(Pessoa.class, id);
	}

	public ClienteDigital clienteDigitalPorId(Long id) {
		return manager.find(ClienteDigital.class, id);
	}

	public Endereco endereco(Pessoa pessoa, TipoEndereco tipo) {
		Endereco endereco = null;
		List<Endereco> lst = listaEnderecos(pessoa);
		if (lst != null && lst.size() > 0) {
			endereco = lst.get(0);
			for (Endereco ende : lst) {
				if (ende.getTipoEndereco() == tipo) {
					return ende;
				}
			}
		}

		return endereco;
	}

}