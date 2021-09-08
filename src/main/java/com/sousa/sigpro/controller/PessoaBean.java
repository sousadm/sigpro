package com.sousa.sigpro.controller;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletResponse;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.fincatto.documentofiscal.DFUnidadeFederativa;
import com.sousa.sigpro.model.Agente;
import com.sousa.sigpro.model.CentroDeCusto;
import com.sousa.sigpro.model.Cliente;
import com.sousa.sigpro.model.ClienteDigital;
import com.sousa.sigpro.model.Colaborador;
import com.sousa.sigpro.model.Contato;
import com.sousa.sigpro.model.Endereco;
import com.sousa.sigpro.model.EnderecoFocus;
import com.sousa.sigpro.model.Fornecedor;
import com.sousa.sigpro.model.Funcao;
import com.sousa.sigpro.model.Motorista;
import com.sousa.sigpro.model.Municipio;
import com.sousa.sigpro.model.Pessoa;
import com.sousa.sigpro.model.PessoaFisica;
import com.sousa.sigpro.model.PessoaJuridica;
import com.sousa.sigpro.model.Profissao;
import com.sousa.sigpro.model.Sms;
import com.sousa.sigpro.model.Transportador;
import com.sousa.sigpro.model.Usuario;
import com.sousa.sigpro.model.ValorEstrategico;
import com.sousa.sigpro.model.Vendedor;
import com.sousa.sigpro.model.evento.EventoPessoa;
import com.sousa.sigpro.model.tipo.TipoApiCobranca;
import com.sousa.sigpro.model.tipo.TipoAtividadePrincipal;
import com.sousa.sigpro.model.tipo.TipoEndereco;
import com.sousa.sigpro.model.tipo.TipoGrupo;
import com.sousa.sigpro.model.tipo.TipoModulo;
import com.sousa.sigpro.model.tipo.TipoPessoa;
import com.sousa.sigpro.model.tipo.TipoRotina;
import com.sousa.sigpro.repository.CentrosDeCusto;
import com.sousa.sigpro.repository.Contatos;
import com.sousa.sigpro.repository.Enderecos;
import com.sousa.sigpro.repository.Eventos;
import com.sousa.sigpro.repository.Funcoes;
import com.sousa.sigpro.repository.Municipios;
import com.sousa.sigpro.repository.Pessoas;
import com.sousa.sigpro.repository.filter.CondicaoFilter;
import com.sousa.sigpro.security.Seguranca;
import com.sousa.sigpro.service.AnaliseResultadoPessoa;
import com.sousa.sigpro.service.CobrancaAsaasService;
import com.sousa.sigpro.service.CobrancaService;
import com.sousa.sigpro.service.FocusEndereco;
import com.sousa.sigpro.service.NegocioException;
import com.sousa.sigpro.service.TabelaMunicipioService;
import com.sousa.sigpro.util.Criptografia;
import com.sousa.sigpro.util.Suporte;
import com.sousa.sigpro.util.SuporteData;
import com.sousa.sigpro.util.jsf.FacesUtil;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Named
@ViewScoped
public class PessoaBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private HttpServletResponse response;
	@Inject
	private Seguranca seguranca;
	@Inject
	private Pessoas pessoas;
	@Inject
	private Municipios municipios;
	@Inject
	private Funcoes funcoes;
	@Inject
	private CentrosDeCusto centros;
	@Inject
	private Contatos contatos;
	@Inject
	private TabelaMunicipioService tabelaMunicipios;
	@Inject
	private EntityManager manager;
	@Inject
	private Eventos eventos;
	@Inject
	private Enderecos enderecos;

	private boolean incluindo;
	private Cliente cliente;
	private Fornecedor fornecedor;
	private Pessoa pessoa;
	private Contato contato;
	private CondicaoFilter filtro;
	private LazyDataModel<Pessoa> model;
	private List<ValorEstrategico> valores;
	private List<Funcao> listaFuncao;
	private List<Municipio> listaMunicipio;
	private List<CentroDeCusto> listaCusto;
	private int tabIndex = 0;
	private int tabIndexEndereco = 0;
	private boolean somenteLeitura;
	private DFUnidadeFederativa xUf;
	private boolean contrate;
	private boolean clinica;
	private Sms sms;
	private Endereco endereco;
	private ClienteDigital clienteDigital;

	private List<Pessoa> listaAgenteAPI;
	private Pessoa agenteCobrancaAPI;
	CobrancaService cobra;

	public PessoaBean() {
		Suporte.removerAtributoDaSessao("imagem");
		sms = new Sms();
		endereco = new Endereco();
		filtro = new CondicaoFilter();
		filtro.setDescricao("Pessoa");
	}

	public boolean isSomenteLeitura() {
		return somenteLeitura;
	}

	public boolean isContrate() {
		return contrate;
	}

	public boolean isClinica() {
		return clinica;
	}

	public void inicializar() {
		if (FacesUtil.isNotPostback()) {
			listaAgenteAPI = pessoas.listaAgenteAPI();
			clinica = seguranca.getPessoaLogadoOrigem().getAtividade() == TipoAtividadePrincipal.CLINICA;
			contrate = seguranca.isEmpresaContrate();
			somenteLeitura = seguranca.isUsuarioTemRotina(TipoRotina.PESSOA_LEITURA);
			listaFuncao = funcoes.lista("");
			listaCusto = centros.lista();
			contato = new Contato();
			clienteDigital = new ClienteDigital();
			tabIndex = 0;
		}
	}

	public void definePaciente() {
		if (FacesUtil.isNotPostback()) {
			this.pessoa.setTipo(TipoPessoa.PF);
			this.pessoa.setDefineCliente(true);
		}
	}

	public List<Funcao> getListaFuncao() {
		return listaFuncao;
	}

	public CondicaoFilter getFiltro() {
		return filtro;
	}

	public List<ValorEstrategico> getValores() {
		return valores;
	}

	public void setValores(List<ValorEstrategico> valores) {
		this.valores = valores;
	}

	public boolean isEditando() {
		return this.pessoa.getId() != null;
	}

	public String novoCadastro() {

		/* definição de senha inicial */
		String senha = Criptografia.gerarNovaSenha();

		try {
			if ("".equals(this.pessoa.getFone()))
				throw new IllegalArgumentException("Defina um fone para contato.");

			if ("".equals(this.pessoa.getUsuario().getNome()))
				throw new IllegalArgumentException("Defina um nome de usuário.");

			if ("".equals(this.pessoa.getUsuario().getSenha()))
				throw new IllegalArgumentException("Defina uma senha de acesso.");

			this.pessoa.setTipo(TipoPessoa.PJ);
			this.pessoa.setDefineUsuario(true);
			this.pessoa.getUsuario().setNome(this.pessoa.getEmail());
			this.pessoa.getUsuario().setSenha(Criptografia.convertPasswordToMD5(senha));
			this.pessoa = pessoas.guardar(pessoa);

			FacesUtil.addInfoMessage("Registro salvo com sucesso!");

			// ENVIAR EMAIL DE BOAS VINDAS
			pessoa.getUsuario().setSenha(senha);
			pessoas.enviarEmailBemvindo(pessoa);

			return "/login.xhtml";

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
			return null;
		}
	}

	public String login() {
		return "/login.xhtml";
	}

	public void salvarPaciente() {
		try {
			if (this.pessoa.getPF().getNascimento() == null)
				throw new IllegalArgumentException("Informe a data de nascimento.");
			if (this.pessoa.getPF().getSexo() == null)
				throw new IllegalArgumentException("Informe o sexo.");
			if (this.pessoa.getFone().isEmpty() && this.pessoa.getCelular().isEmpty())
				throw new IllegalArgumentException("Informe pelo menos um número de telefone");
			this.pessoa.setOrigem(seguranca.getPessoaLogadoOrigem());
			this.pessoa.setDefineCliente(Boolean.TRUE);
			salvar();
		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public List<Pessoa> getVendedores() {
		return pessoas.vendedores();
	}

	/* CONTROLE DE PACIENTE */

	public void salvarProfissional() {
		String senha = "";

		try {

			if (this.pessoa.getId() == null) {
				senha = Criptografia.gerarNovaSenha();
				this.pessoa.setOrigem(seguranca.getPessoaLogadoOrigem());
				this.pessoa.setTipo(TipoPessoa.PF);
				this.pessoa.setDefineVendedor(Boolean.TRUE);
				this.pessoa.getUsuario().setNome(this.pessoa.getEmail());
				this.pessoa.getUsuario().setSenha(Criptografia.convertPasswordToMD5(senha));
			}

			this.pessoa = pessoas.guardar(pessoa);

			FacesUtil.addInfoMessage("Registro salvo com sucesso!");

			// if (senha != "") {
			// pessoa.getUsuario().setSenha(senha);
			// pessoas.enviarEmailBemvindo(pessoa);
			// }

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void pesquisarPaciente() {
		// pessoasFiltradas = pessoas.filtrados(filtro);
	}

	public void pesquisarProfissional() {
		filtro.setDefineVendedor(true);
		// pessoasFiltradas = pessoas.filtrados(filtro);
	}

	public void imprimirFicha() throws JRException, SQLException {
		imprimirFicha(pessoa);
	}

	public List<Municipio> getListaMunicipio() {
		return listaMunicipio;
	}

	public int getTabIndexEndereco() {
		return tabIndexEndereco;
	}

	public void setTabIndexEndereco(int tabIndexEndereco) {
		this.tabIndexEndereco = tabIndexEndereco;
	}

	public int getTabIndex() {
		return tabIndex;
	}

	public void setTabIndex(int tabIndex) {
		this.tabIndex = tabIndex;
	}

	public List<CentroDeCusto> getListaCusto() {
		return listaCusto;
	}

	public void setListaCusto(List<CentroDeCusto> listaCusto) {
		this.listaCusto = listaCusto;
	}

	public DFUnidadeFederativa getxUf() {
		return xUf;
	}

	public void setxUf(DFUnidadeFederativa xUf) {
		this.xUf = xUf;
	}

	public LazyDataModel<Pessoa> getModel() {
		return model;
	}

	public Contato getContato() {
		return contato;
	}

	public void carregarPesquisaEndereco(SelectEvent event) {
		EnderecoFocus ende = (EnderecoFocus) event.getObject();
		defineEnderecoFocus(ende);
	}

	public Pessoa getPessoa() {
		carregaMunicipios();
		return pessoa;
	}

	public void profissaoSelecionado(SelectEvent event) {
		Profissao profissao = (Profissao) event.getObject();
		if (pessoa.getPF() != null) {
			pessoa.getPF().setProfissao(profissao);
		}
	}

	public void carregaMunicipios() {
		if (xUf != null)
			if (listaMunicipio == null || listaMunicipio.size() == 0 || !listaMunicipio.get(0).getUf().equals(xUf))
				listaMunicipio = tabelaMunicipios.lista(xUf.getCodigo(), 0);
	}

	public boolean isIncluindo() {
		return incluindo;
	}

	public void setSms(Sms sms) {
		this.sms = sms;
	}

	public Sms getSms() {
		return sms;
	}

	public void preparaSms() {
		if (pessoa != null) {
			sms = new Sms();
			sms.setDdd(pessoa.getDdd());
			sms.setTelefone(pessoa.getCelular());
			sms.setMensagem("Sr." + pessoa.getNome());
			sms.setTipoModulo(TipoModulo.CADASTRO);
			sms.setId(pessoa.getId());
		}
	}

	public void analisa_cpf() {
		if (!pessoa.isExiste()) {
			Pessoa p = pessoas.porCpf(Suporte.onlyNumbers(pessoa.getPF().getCpf()));
			if (p != null)
				FacesUtil.addRequestInfoMessage("CPF já cadastrado para " + p.getNome());
		}
	}

	public void analisa_cnpj() {
		if (!pessoa.isExiste()) {
			Pessoa p = pessoas.porCnpj(Suporte.onlyNumbers(pessoa.getPJ().getCnpj()));
			if (p != null)
				FacesUtil.addRequestInfoMessage("CNPJ já cadastrado para " + p.getNome());
		}
	}

	public void novo(String tipo) {
		Suporte.removerAtributoDaSessao("imagem");
		pessoa = new Pessoa();
		if (seguranca.getPessoaLogadoOrigem().getEnderecos().size() > 0) {
			Endereco ende = seguranca.getPessoaLogadoOrigem().getEnderecos().get(0);
			endereco = new Endereco(ende.getMunicipio());
		}

		if (tipo.equals("cliente")) {
			pessoa.setDefineCliente(true);
			if (seguranca.getPessoaLogadoOrigem().getAtividade() == TipoAtividadePrincipal.CLINICA) {
				pessoa.setTipo(TipoPessoa.PF);
			}
		} else if (tipo.equals("fornecedor")) {
			pessoa.setDefineFornecedor(true);
		} else if (tipo.equals("vendedor")) {
			pessoa.setDefineVendedor(true);
		} else if (tipo.equals("transportador")) {
			pessoa.setDefineTransportador(true);
		} else if (tipo.equals("colaborador")) {
			pessoa.setDefineColaborador(true);
		} else if (tipo.equals("motorista")) {
			pessoa.setDefineMotorista(true);
		} else if (tipo.equals("agente")) {
			pessoa.setDefineAgente(true);
		}

		carregaMunicipios();
		incluindo = true;
	}

	public void gerarNovaSenha() {
		try {
			String senha = Criptografia.gerarNovaSenha();
			Usuario usuario = pessoa.getUsuario();
			usuario.setSenha(Criptografia.convertPasswordToMD5(senha));

			pessoas.guardar(usuario);
			pessoas.enviarEmailSenha(pessoa, senha);
			pessoa.getUsuario().setSenha(usuario.getSenha());

			eventos.guardar(new EventoPessoa(seguranca.getPessoaLogadoOrigem().getUsuario(), pessoa,
					"nova senha enviada por email"));

			FacesUtil.addInfoMessage("Enviado por e-mail com sucesso!");

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void setTabDerivado() {
		if (pessoa.getDefineCliente() != null && pessoa.getCliente() == null)
			pessoa.setCliente(new Cliente());
		if (pessoa.getDefineAgente() != null && pessoa.getAgente() == null)
			pessoa.setAgente(new Agente());
		if (pessoa.getDefineColaborador() != null && pessoa.getColaborador() == null)
			pessoa.setColaborador(new Colaborador());
		if (pessoa.getDefineFornecedor() != null && pessoa.getFornecedor() == null)
			pessoa.setFornecedor(new Fornecedor());
		if (pessoa.getDefineTransportador() != null && pessoa.getTransportador() == null)
			pessoa.setTransportador(new Transportador());
		if (pessoa.getDefineUsuario() != null && pessoa.getUsuario() == null)
			pessoa.setUsuario(new Usuario());
		if (pessoa.getDefineVendedor() != null && pessoa.getVendedor() == null)
			pessoa.setVendedor(new Vendedor());
		if (pessoa.getDefineMotorista() != null && pessoa.getMotorista() == null)
			pessoa.setMotorista(new Motorista());
		setTabGeral();
		tabIndex = pessoa.isExisteAlgumComplemento() ? 2 : 0;
	}

	public void setTabGeral() {
		if (pessoa != null) {
			if (pessoa.getTipo() == TipoPessoa.PF && pessoa.getPF() == null)
				pessoa.setPF(new PessoaFisica());
			else if (pessoa.getTipo() == TipoPessoa.PJ && pessoa.getPJ() == null)
				pessoa.setPJ(new PessoaJuridica());
		}
		tabIndex = 0;
	}

	public String getOpcaoCliente() {
		return clinica ? "Paciente" : "Cliente";
	}

	public void imprimir() throws JRException, SQLException {
		try {

			filtro.setSortField(null);
			filtro.setPrimeiroRegistro(null);
			List<Pessoa> lst = pessoas.lista(filtro);

			if (lst.size() == 0)
				throw new Exception("Sem dados para imprimir.");

			HashMap<String, Object> parametros = new HashMap<String, Object>();
			parametros.put("empresa", seguranca.getPessoaLogadoOrigem());
			parametros.put("empresa_endereco", seguranca.enderecoEmpresa());
			parametros.put("titulo_relatorio", "Lista de " + filtro.getDescricao());

			String path = seguranca.pathClass("relatorio");
			String arquivo = path + "pessoa_lista.jasper";

			JRBeanCollectionDataSource jrds = new JRBeanCollectionDataSource(lst);
			JasperPrint printer = JasperFillManager.fillReport(arquivo, parametros, jrds);
			Suporte.ImprimirArquivo(response, printer, null);

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void setContato(Contato contato) {
		this.contato = contato;
		if (pessoa == null && contato.getPessoa() != null) {
			pessoa = new Pessoa();
			setPessoa(contato.getPessoa());
		}
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
		if (cliente != null)
			pessoa = pessoas.cliente(cliente);
	}

	public Fornecedor getFornecedor() {
		return fornecedor;
	}

	public void setFornecedor(Fornecedor fornecedor) {
		this.fornecedor = fornecedor;
		if (fornecedor != null)
			pessoa = pessoas.fornecedor(fornecedor);
	}

	public Endereco getEndereco() {
		return endereco;
	}

	public void setEndereco(Endereco endereco) {
		this.endereco = endereco;
		if (endereco != null && endereco.getMunicipio() != null)
			this.xUf = endereco.getMunicipio().getUf();
	}

	public void carregarEndereco() {
		try {
			FocusEndereco focus = new FocusEndereco(seguranca.getPessoaLogadoOrigem().getPJ());
			EnderecoFocus ende = focus.pesquisa(endereco.getCep());
			defineEnderecoFocus(ende);
		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	private void defineEnderecoFocus(EnderecoFocus ende) {
		endereco.setCep(ende.getCep());
		endereco.setBairro(ende.getBairro());
		endereco.setLogradouro(ende.getNome_logradouro());
		Municipio municipio = municipios.porId(Long.parseLong(ende.getCodigo_ibge()));
		endereco.setMunicipio(municipio);
		xUf = municipio.getUf();
		carregaMunicipios();
	}

	public void novoEndereco() {
		endereco = new Endereco();
	}

	public void aplicarEndereco() {
		try {

			if (endereco.getMunicipio() == null)
				throw new NegocioException("município indefinido");

			if (endereco.getPessoa() == null) {
				endereco.setPessoa(pessoa);
				pessoa.getEnderecos().add(endereco);
			}

			endereco = new Endereco();

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public Pessoa getAgenteCobrancaAPI() {
		return agenteCobrancaAPI;
	}

	public void setAgenteCobrancaAPI(Pessoa agenteCobrancaAPI) {
		if (agenteCobrancaAPI != null && pessoa != null)
			clienteDigital = pessoas.clienteDigital(pessoa, agenteCobrancaAPI.getAgente());
		this.agenteCobrancaAPI = agenteCobrancaAPI;
	}

	public List<Pessoa> getListaAgenteAPI() {
		return listaAgenteAPI;
	}

	public ClienteDigital getClienteDigital() {
		return clienteDigital;
	}

	public void excluirEnderecoItem(int linha) {
		try {
			endereco = pessoa.getEnderecos().get(linha);
			if (endereco.getId() == null) {
				pessoa.getEnderecos().remove(linha);
			} else {
				enderecos.remover(endereco);
			}
			endereco = new Endereco();
		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void excluir(Pessoa pessoa) {

		String texto = "exclusão de pessoa " + pessoa.getNome();

		try {

			pessoas.remover(pessoa);
			Suporte.removerAtributoDaSessao("imagem");

			eventos.guardar(new EventoPessoa(seguranca.getPessoaLogadoOrigem().getUsuario(),
					seguranca.getPessoaLogado(), texto));

			FacesUtil.addInfoMessage(pessoa.getNome() + " excluída com sucesso.");

			this.setPessoa(null);

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void enviarParaPlataforma() {

		try {

			if (agenteCobrancaAPI.getAgente().getApiDeCobranca() == TipoApiCobranca.ASAAS) {
				cobra = new CobrancaAsaasService(agenteCobrancaAPI.getAgente(), pessoas);
			}

			ClienteDigital clienteDigital = cobra.atualizaClienteNaPlataformaAPI(pessoa, "");
			FacesUtil.addInfoMessage("Cadastro enviado com sucesso");

			EventoPessoa evento = new EventoPessoa();
			evento.setDocumento(clienteDigital.getIdentificador());
			eventos.guardar(new EventoPessoa(seguranca.getPessoaLogadoOrigem().getUsuario(), pessoa,
					"atualizado na plataforma Asaas em "
							+ SuporteData.formataDataToStr(clienteDigital.getDataCadastro())
									.concat(" id=" + clienteDigital.getIdentificador())));

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void salvar() {
		try {

			if (Suporte.existeAtributoNaSessao("imagem"))
				pessoa.setImagem((byte[]) Suporte.getAtributoDaSessao("imagem"));

			// DEFINIÇÃO DE USUARIO DO SISTEMA
			if (this.pessoa.getDefineUsuario() != null)
				if (this.pessoa.getDefineUsuario() == false) {
					if (pessoa.equals(pessoa.getOrigem()))
						throw new NegocioException("o usuário principal não pode ser removido");
					if (pessoa.getUsuario() != null && this.pessoa.getUsuario().getId() == null)
						this.pessoa.setUsuario(null);
				} else {
					if (this.pessoa.getUsuario().getNome() == null)
						this.pessoa.getUsuario().setNome(
								this.pessoa.getNome() == null ? this.pessoa.getEmail() : this.pessoa.getNome());
					if (this.pessoa.getUsuario().getNome() == null || this.pessoa.getUsuario().getNome().length() < 3)
						throw new NegocioException("nome de usuário inválido");
				}

			if (pessoa.getUsuario() != null && pessoa.getUsuario().getId() != null) {
				if (pessoa.getUsuario().getGrupos() == null || pessoa.getUsuario().getGrupos().isEmpty())
					throw new NegocioException("grupo indefinido para o usuário");
			}

			// DEFINIÇÃO DE CLIENTE DO SISTEMA
			if (this.pessoa.getDefineCliente() != null)
				if (this.pessoa.getDefineCliente() == false) {
					if (this.pessoa.getCliente() != null && this.pessoa.getCliente().getId() == null)
						this.pessoa.setCliente(null);
				} else {
					pessoa.getCliente().setNome(pessoa.getNome());
				}

			// DEFINIÇÃO VENDEDOR DO SISTEMA
			if (this.pessoa.getDefineVendedor() != null)
				if (this.pessoa.getDefineVendedor() == false) {
					if (this.pessoa.getVendedor() != null && this.pessoa.getVendedor().getId() == null)
						this.pessoa.setVendedor(null);
				} else {
					pessoa.getVendedor().setNome(pessoa.getNome());
				}

			// DEFINIÇÃO FORNECEDOR DO SISTEMA
			if (this.pessoa.getDefineFornecedor() != null)
				if (this.pessoa.getDefineFornecedor() == false) {
					if (this.pessoa.getFornecedor() != null && this.pessoa.getFornecedor().getId() == null)
						this.pessoa.setFornecedor(null);
				} else {
					pessoa.getFornecedor().setNome(pessoa.getNome());
				}

			// DEFINIÇÃO AGENTE DO SISTEMA
			if (this.pessoa.getDefineAgente() != null)
				if (this.pessoa.getDefineAgente() == false) {
					if (this.pessoa.getAgente() != null && this.pessoa.getAgente().getId() == null)
						this.pessoa.setAgente(null);
				} else {
					pessoa.getAgente().setNome(pessoa.getNome());
				}

			// DEFINICAO DE COLABORADOR / FUNCIONARIO
			if (this.pessoa.getDefineColaborador() != null)
				if (this.pessoa.getDefineColaborador() == false) {
					if (this.pessoa.getColaborador() != null && this.pessoa.getColaborador().getId() == null)
						this.pessoa.setColaborador(null);
				} else {
					pessoa.getColaborador().setNome(pessoa.getNome());
				}

			// DEFINIÇÃO TRANSPORTADOR
			if (this.pessoa.getDefineTransportador() != null)
				if (this.pessoa.getDefineTransportador() == false) {
					if (this.pessoa.getTransportador() != null && this.pessoa.getTransportador().getId() == null)
						this.pessoa.setTransportador(null);
				} else {
					pessoa.getTransportador().setNome(pessoa.getNome());
				}

			// DEFINIÇÃO MOTORISTA
			if (this.pessoa.getDefineMotorista() != null)
				if (this.pessoa.getDefineMotorista() == false) {
					if (this.pessoa.getMotorista() != null && this.pessoa.getMotorista().getId() == null)
						this.pessoa.setMotorista(null);
				} else {
					pessoa.setDefineMotorista(true);
					pessoa.getMotorista().setNome(pessoa.getNome());
				}

			pessoa.setOrigem(seguranca.getPessoaLogadoOrigem());

			pessoa = pessoas.guardar(pessoa);

			FacesUtil.addInfoMessage("Registro salvo com sucesso!");

		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao gravar: " + e.getMessage());
		}
	}

	public void pesquisar(String condicao, String titulo) {
		filtro.setDescricao(titulo);
		filtro.setSql(condicao);
		model = new LazyDataModel<Pessoa>() {

			private static final long serialVersionUID = 1L;

			@Override
			public List<Pessoa> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {

				filtro.setPrimeiroRegistro(Long.valueOf(first));
				filtro.setQuantidadeRegistros(Long.valueOf(pageSize));
				filtro.setSortField(sortField);
				filtro.setAscendente(SortOrder.ASCENDING.equals(sortOrder));

				setRowCount(pessoas.quantidadeFiltrados(filtro));
				return pessoas.lista(filtro);
			}

		};
	}

	public void setPessoa(Pessoa pessoa) {

		tabIndex = 0;
		if (pessoa != null) {
			pessoa = pessoas.porId(pessoa.getId());

			if (pessoa.getImagem() != null)
				Suporte.setAtributoNaSessao("imagem", pessoa.getImagem());
			else
				Suporte.removerAtributoDaSessao("imagem");

			if (pessoa.getUsuario() != null && pessoa.getUsuario().getGrupos().isEmpty())
				pessoa.getUsuario().setGrupos(new ArrayList<TipoGrupo>());

			if (pessoa.getDefineColaborador() != null) {
				if (pessoa.getColaborador() == null)
					pessoa.setColaborador(new Colaborador());
				if (pessoa.getDefineColaborador() == Boolean.FALSE || pessoa.getColaborador().getFuncoes() == null)
					pessoa.getColaborador().setFuncoes(new ArrayList<Funcao>());
			}

			if (pessoa.getDefineCliente() != null && pessoa.getCliente() == null)
				pessoa.setCliente(new Cliente());

			if (pessoa.getDefineVendedor() != null && pessoa.getVendedor() == null)
				pessoa.setVendedor(new Vendedor());

			if (pessoa.getDefineMotorista() != null && pessoa.getMotorista() == null)
				pessoa.setMotorista(new Motorista());

			if (pessoa.getDefineAgente() != null && pessoa.getAgente() == null)
				pessoa.setAgente(new Agente());

			if (pessoa.getDefineFornecedor() != null && pessoa.getFornecedor() == null)
				pessoa.setFornecedor(new Fornecedor());

			if (pessoa.getDefineTransportador() != null && pessoa.getTransportador() == null)
				pessoa.setTransportador(new Transportador());

			if (agenteCobrancaAPI != null)
				clienteDigital = pessoas.clienteDigital(pessoa, agenteCobrancaAPI.getAgente());

			pessoa.setEnderecos(pessoas.listaEnderecos(pessoa));

		}

		this.pessoa = pessoa;
	}

	public void imprimirFicha(Pessoa pessoa) throws JRException, SQLException {
		try {

			pessoa = pessoas.porId(pessoa.getId());
			Endereco endereco = pessoa.getEndereco(TipoEndereco.RESIDENCIAL);
			List<Contato> lista_contatos = contatos.porPessoa(pessoa);

			List<Pessoa> lst = new ArrayList<Pessoa>();
			lst.add(pessoa);

			String path = seguranca.pathClass("relatorio");
			String arquivo = path + "pessoa.jasper";

			Map<String, Object> parametros = new HashMap<>();
			parametros.put("SUBREPORT_DIR", path);
			parametros.put("contatos", new JRBeanCollectionDataSource(lista_contatos));
			parametros.put("analise", new AnaliseResultadoPessoa(pessoa, manager));
			parametros.put("endereco", endereco);

			JRBeanCollectionDataSource jrds = new JRBeanCollectionDataSource(lst);
			JasperPrint printer = JasperFillManager.fillReport(arquivo, parametros, jrds);
			
			Suporte.ImprimirArquivo(response, printer, null);
			
//			Suporte.ExportarArquivo(printer, nome_arquivo);
//			File file = new File(nome_arquivo);
//			byte[] bytes = Files.readAllBytes(file.toPath());
//
//			response.reset();
//			response.setContentType("application/pdf");
//			response.setContentLength(bytes.length);
//			response.setHeader("Content-disposition", "inline; filename=arquivo.pdf");
//			try {
//				response.getOutputStream().write(bytes);
//				response.getOutputStream().flush();
//				response.getOutputStream().close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}

			eventos.guardar(new EventoPessoa(seguranca.getPessoaLogadoOrigem().getUsuario(), pessoa,
					"impressão de ficha cadastral"));

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}

	}

}