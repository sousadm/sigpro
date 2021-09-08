package com.sousa.sigpro.controller;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.DefaultSubMenu;
import org.primefaces.model.menu.MenuModel;

import com.sousa.sigpro.model.Categoria;
import com.sousa.sigpro.model.Parametro;
import com.sousa.sigpro.model.Pessoa;
import com.sousa.sigpro.model.evento.EventoPessoa;
import com.sousa.sigpro.model.tipo.TipoAtividadePrincipal;
import com.sousa.sigpro.model.tipo.TipoEspecialidade;
import com.sousa.sigpro.model.tipo.TipoGrupo;
import com.sousa.sigpro.model.tipo.TipoModulo;
import com.sousa.sigpro.model.tipo.TipoRotina;
import com.sousa.sigpro.model.tipo.TipoTheme;
import com.sousa.sigpro.repository.Eventos;
import com.sousa.sigpro.repository.Parametros;
import com.sousa.sigpro.repository.Pessoas;
import com.sousa.sigpro.repository.UnidadesProdutivas;
import com.sousa.sigpro.security.Seguranca;
import com.sousa.sigpro.service.NegocioException;
import com.sousa.sigpro.util.Suporte;
import com.sousa.sigpro.util.SuporteData;
import com.sousa.sigpro.util.jpa.ConexaoManager;
import com.sousa.sigpro.util.jsf.FacesUtil;

@Named
@SessionScoped
public class LoginBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Parametros parametros;
	@Inject
	private HttpServletRequest request;
	@Inject
	private FacesContext facesContext;
	@Inject
	private HttpServletResponse response;
	@Inject
	private EntityManager manager;
	@Inject
	private Pessoas pessoas;
	@Inject
	private Eventos eventos;
	@Inject
	private UnidadesProdutivas unidades;
	@Inject
	private Seguranca seguranca;

	private List<TipoRotina> rotinas;
	private String nome;
	private String empresa;
	private String theme;
	private boolean selecionado = false;
	private MenuModel menuModel;
	private String menuTextoBootStrap;
	private List<String> images;

	public LoginBean() {
		try {

			Date hoje = SuporteData.somenteData(new Date());
			Date limite = SuporteData.somenteData("30/09/2021");
			if (hoje.compareTo(limite) <= 0) {
				empresa = "contrate";
				nome = "sousa";
			}

			images = new ArrayList<String>();
			for (int i = 1; i <= 5; i++) {
				images.add("publicidade" + i + ".jpg");
			}
		} catch (Exception e) {

		}
	}

	public void preRender() {
		if ("true".equals(request.getParameter("invalid"))) {
			FacesUtil.addErrorMessage("usuário ou senha inválido!");
		}
	}

	public void defineThemaDoUsuario() {
		try {
			String condicao = "from Parametro where codigo = :codigo and grupo = :grupo";
			Parametro the = manager.createQuery(condicao, Parametro.class).setParameter("grupo", nome)
					.setParameter("codigo", "theme").getSingleResult();
			theme = the.getValor();
		} catch (Exception e) {
			theme = "sunny";
		} finally {
			selecionado = true;
		}
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getEmpresa() {
		return empresa;
	}

	public void setEmpresa(String empresa) {
		this.empresa = empresa.toLowerCase();
	}

	public String getTheme() {
		theme = Suporte.theme;
		if (theme == null) {
			theme = "sunny";
		}
		return TipoTheme.valueOf(theme).getDescricao();
	}

	public void setTheme(String theme) {
		this.theme = theme;
	}

	public MenuModel getMenuModel() {
		return menuModel;
	}

	public void addMenu(DefaultSubMenu menu, String valor, String outcome, TipoRotina tipo, String icon,
			String comando) {
		if (tipo == null || rotinas.contains(tipo)) {
			DefaultMenuItem item = new DefaultMenuItem();
			item.setValue(valor);
			if (comando == null) {
				item.setOutcome(outcome == null ? "/home" : outcome);
			} else {
				item.setCommand(comando);
			}
			if (icon != null) {
				item.setIcon(icon);
			}
			menu.addElement(item);
		}
	}

	public String getMenuTextoBootStrap() {
		return menuTextoBootStrap;
	}

	public boolean temModuloServico(Pessoa empresa) {
		List<Categoria> lst = manager
				.createQuery("select c from Categoria c where c.pessoa = :empresa and c.especialidade is not null",
						Categoria.class)
				.setParameter("empresa", empresa).getResultList();
		return lst.size() > 0;
	}

	public boolean temModuloEspecialidade(TipoEspecialidade especialidade, Pessoa empresa) {
		List<Categoria> lst = manager
				.createQuery("select c from Categoria c where c.pessoa = :empresa and c.especialidade = :especialidade",
						Categoria.class)
				.setParameter("empresa", empresa).setParameter("especialidade", especialidade).getResultList();
		return lst.size() > 0;
	}

	public String getHostAddress() throws UnknownHostException {
		return InetAddress.getLocalHost().getHostAddress();
	}

	public void definirLogomarcaNaSessao(Pessoa usuario) {
		String arquivo = "";
		Parametro param = parametros.porCodigoGrupo("logomarca", "MAIL", usuario.getOrigem());
		if (param != null && param.getValor() != null && !param.getValor().equals(""))
			arquivo = Suporte.uploadLocal + param.getValor();

		File file = new File(arquivo);
		// if (!file.exists())
		// arquivo = this.getClass().getClassLoader().getResource("").getPath() +
		// "sigpro/images/logo.png";
		// file = new File(arquivo);
		if (!file.exists())
			arquivo = "/UPLOADS/logo.png";

		Suporte.logomarca = arquivo;
	}

	public List<String> getImages() {
		return images;
	}

	public void defineMenu() {

		Pessoa user = pessoas.porUsuario(nome);
		boolean principal = user.equals(user.getOrigem());
		definirLogomarcaNaSessao(user);

		Pessoa matriz = unidades.matriz();
		List<String> modulos = matriz.getModulos();
		List<TipoGrupo> grupos = user.getUsuario().getGrupos();
		rotinas = user.getUsuario().getRotinas();
		menuModel = new DefaultMenuModel();

		DefaultSubMenu submenu;
		if (principal || modulos.contains(TipoModulo.CADASTRO.name())) {
			submenu = new DefaultSubMenu("Cadastros");
			if (principal)
				addMenu(submenu, "Pessoa em geral", "/cadastros/CadastroPessoa", null, "ui-icon-person", null);
			else
				addMenu(submenu, "Pessoa em geral", "/cadastros/CadastroPessoa", TipoRotina.PESSOA_CADASTRO,
						"ui-icon-person", null);
			addMenu(submenu, "Produto/Serviços", "/cadastros/CadastroProduto", TipoRotina.PRODUTO_CADASTRO,
					"ui-icon-suitcase", null);
			addMenu(submenu, "Montagem de produto", "/cadastros/CadastroMontagem", TipoRotina.PRODUTO_CADASTRO, null,
					null);
			addMenu(submenu, "Centro de custo", "/financeiro/CadastroCentroDeCusto", TipoRotina.CENTROCUSTO, null,
					null);

			addMenu(submenu, "Agenda", "/cadastros/CadastroAgenda", TipoRotina.AGENDA, null, null);
			// addMenu(submenu, "Plano de Contas", "/cadastros/CadastroPlanoContas",
			// TipoRotina.PLANOCONTA, null);
			if (user.getOrigem().getAtividade() != null)
				if (user.getOrigem().getAtividade() == TipoAtividadePrincipal.CLINICA) {
					// addMenu(submenu, "Plano de saúde", null, TipoRotina.CLINICA_PLANO, null);
					addMenu(submenu, "Convênio", "/clinica/ClinicaConvenio", TipoRotina.CLINICA_CONVENIO, null, null);
					addMenu(submenu, "Horários", "/clinica/PesquisaHorario", TipoRotina.CLINICA_HORARIO, null, null);

				} else {
					addMenu(submenu, "Compra", "/cadastros/CadastroAquisicao", TipoRotina.AQUISICAOCADASTRO,
							"ui-icon-tag", null);
					addMenu(submenu, "Categoria de produto", "/cadastros/CadastroCategoria",
							TipoRotina.CATEGORIAPRODUTO, null, null);
				}
			addMenu(submenu, "Atividade funcional", "/producao/PesquisaFuncao", TipoRotina.ATIVIDADE, null, null);

			addMenu(submenu, "Veículo", "/equipamento/CadastroVeiculo", TipoRotina.VEICULO, null, null);

			if (submenu.getElements().size() > 0)
				menuModel.addElement(submenu);
		}

		if (modulos.contains(TipoModulo.COMERCIAL.name())) {
			submenu = new DefaultSubMenu("Comercial/Expedição");
			if (user.getOrigem().getAtividade() != null)
				if (user.getOrigem().getAtividade() == TipoAtividadePrincipal.CLINICA) {

				} else {
					addMenu(submenu, "Venda", "/expedicao/CadastroExpedicao", TipoRotina.VENDA, "ui-icon-calculator",
							null);
					addMenu(submenu, "Saída de veículos", "/expedicao/ControleSaidaVeiculo", TipoRotina.VEICULO, null,
							null);
					addMenu(submenu, "Relatório", "/financeiro/RelatorioExpedicao", TipoRotina.RELATORIOVENDA, null,
							null);
				}

			if (submenu.getElements().size() > 0)
				menuModel.addElement(submenu);
		}

		// if (modulos.contains(TipoModulo.SERVICO.name()) &&
		// temModuloServico(user.getOrigem())) {
		if (modulos.contains(TipoModulo.SERVICO.name())) {
			submenu = new DefaultSubMenu("Produção & Serviço");
			if (user.getOrigem().getAtividade() != null)
				if (user.getOrigem().getAtividade() == TipoAtividadePrincipal.CLINICA) {
					submenu.setLabel("Clínica");
					addMenu(submenu, "Acolhimento", "/clinica/ClinicaAcolhimento", TipoRotina.CLINICA_ACOLHIMENTO, null,
							null);
					// addMenu(submenu, "Agendamento", "/clinica/ClinicaAgendamento",
					// TipoRotina.CLINICA_AGENDAMENTO,
					// null);
					// addMenu(submenu, "Acompanhamento", "/clinica/ClinicaAgendamentoLista",
					// TipoRotina.CLINICA_AGENDAMENTO, null);
					addMenu(submenu, "Atendimento", "/clinica/ClinicaAtendimento", TipoRotina.CLINICA_ATENDIMENTO, null,
							null);

				} else if (user.getOrigem().getAtividade() == TipoAtividadePrincipal.OFICINA) {
					addMenu(submenu, "Cadastro de comando", "/producao/PesquisaComando", TipoRotina.SERVICOCOMANDO,
							null, null);
					addMenu(submenu, "Ordem de Serviço", "/expedicao/CadastroOrdemServico", TipoRotina.SERVICORDEM,
							null, null);
					addMenu(submenu, "Spool de Serviços", "/expedicao/PesquisaOrdemProducao", TipoRotina.SERVICOSPOOL,
							null, null);

					addMenu(submenu, "Gerador de remuneração", "/producao/PesquisaRemuneradorOrdem",
							TipoRotina.SERVICOREMUNERADOR, null, null);

				}

			addMenu(submenu, "spool de montagem", "/producao/SpoolDeMontagem", TipoRotina.MONTAGEMPRODUTOSPOOL, null,
					null);

			if (submenu.getElements().size() > 0)
				menuModel.addElement(submenu);
		}

		if (modulos.contains(TipoModulo.FINANCEIRO.name())) {
			submenu = new DefaultSubMenu("Financeiro");
			addMenu(submenu, "Diário", "/financeiro/CadastroDiario", TipoRotina.DIARIO, null, null);
			addMenu(submenu, "Caixa", "/financeiro/CadastroCaixa", TipoRotina.CAIXA, null, null);
			addMenu(submenu, "Titulos", "/financeiro/CadastroTitulo", TipoRotina.TITULO, null, null);
			addMenu(submenu, "Boleto digital", "/financeiro/PesquisaBoleto", TipoRotina.TITULO, null, null);
			addMenu(submenu, "Borderô de títulos", "/financeiro/CadastroRemessa", TipoRotina.REMESSA, null, null);
			addMenu(submenu, "Forma de pagamento", "/cadastros/CadastroFormaPgto", TipoRotina.FORMAPGTO, null, null);
			addMenu(submenu, "Conta bancária", "/financeiro/CadastroConta", TipoRotina.CONTABCO, null, null);
			addMenu(submenu, "Cartão de crédito", "/cadastros/CadastroCartao", TipoRotina.CARTAO, null, null);
			addMenu(submenu, TipoRotina.CHEQUE.getDescricao(), "/financeiro/CadastroCheque", TipoRotina.CHEQUE, null,
					null);
			addMenu(submenu, TipoRotina.POSFIN.getDescricao(), "/financeiro/PosicaoFinanceira", TipoRotina.POSFIN, null,
					null);
//			addMenu(submenu, TipoRotina.FLUXO_FINANCEIRO.getDescricao(), "/financeiro/FluxoFinanceiro", TipoRotina.FLUXO_FINANCEIRO, null, null);
			addMenu(submenu, "Relatório", "/financeiro/RelatorioFinanceiro", null, null, null);
			if (submenu.getElements().size() > 0)
				menuModel.addElement(submenu);
		}

		if (modulos.contains(TipoModulo.FISCAL.name())) {
			submenu = new DefaultSubMenu("Fiscal");
			addMenu(submenu, "Operação Fiscal", "/fiscal/PesquisaOperacaoFiscal", TipoRotina.FISCALOPERACAO, null,
					null);
			if (matriz.getUnidadeProdutiva().isUsaNFe())
				addMenu(submenu, "Nota Fiscal", "/fiscal/CadastroNotaFiscal", TipoRotina.NFE, null, null);

			if (matriz.getUnidadeProdutiva().isUsaNFSe())
				addMenu(submenu, "Nota Serviço", "/fiscal/CadastroNotaServico", TipoRotina.NFSE, null, null);

			if (matriz.getUnidadeProdutiva().isUsaNFCe())
				addMenu(submenu, "Nota Consumidor", "/fiscal/CadastroNotaConsumidor", TipoRotina.NFCE, null, null);
//			 addMenu(submenu, "Modulo Pagamento", "/fiscal/PesquisaModuloPagamento", TipoRotina.MODULO_FISCAL, null);

			if (submenu.getElements().size() > 0)
				menuModel.addElement(submenu);
		}

		if (grupos.contains(TipoGrupo.CON)) {
			submenu = new DefaultSubMenu("Consultas");
			if (user.getOrigem().getAtividade() != null)
				if (user.getOrigem().getAtividade() == TipoAtividadePrincipal.CLINICA) {

				} else {
					addMenu(submenu, "Veículo", "/convidado/PesquisaConvidadoVeiculo", null, null, null);
					addMenu(submenu, "Pedidos", "/convidado/PesquisaConvidadoExpedicao", null, null, null);
				}

			addMenu(submenu, "Agenda", "/convidado/PesquisaConvidadoAgenda", null, null, null);

			if (submenu.getElements().size() > 0)
				menuModel.addElement(submenu);
		}

		if (modulos.contains(TipoModulo.PLANEJAMENTO.name())) {
			submenu = new DefaultSubMenu("Planejamento");
			addMenu(submenu, "Formação de Preços", "/cadastros/FormacaoDePreco", TipoRotina.FORMACAO_PRECO, null, null);
			addMenu(submenu, "Analise Patrimonial", "/financeiro/PesquisaBalanco", TipoRotina.PATRIMONIO, null, null);
			addMenu(submenu, "Relatório", "/planner/RelatorioAnalise", null, null, null);

			addMenu(submenu, "Questionário", "/planner/CadastroQuestionario", TipoRotina.QUESTIONARIO, null, null);
			addMenu(submenu, "Projeto", "/planner/CadastroProjeto", TipoRotina.PROJETO, null, null);
//			addMenu(submenu, "Definição estratégica", "/planner/CadastroEstrategia", TipoRotina.DEFINICAO, null, null);
//			addMenu(submenu, "Análise SWOT", "/planner/PesquisaSwot", TipoRotina.SWOT, null, null);
//			addMenu(submenu, "Cruzamento SWOT", "/planner/CadastroSwotPlanner", TipoRotina.SWOTCRUZA, null, null);
//			addMenu(submenu, "Plano de ação", "/planner/PesquisaPlanner", TipoRotina.PLANODEACAO, null, null);
//			addMenu(submenu, "Metas", "/financeiro/CadastroMeta", TipoRotina.META, null, null);

			if (submenu.getElements().size() > 0)
				menuModel.addElement(submenu);
		}

		submenu = new DefaultSubMenu("Definições");
		if (!grupos.contains(TipoGrupo.CON) && modulos.contains(TipoModulo.ANALISE.name())) {
			addMenu(submenu, "Home", "/home", TipoRotina.INDICADORES, null, null);
		}
		addMenu(submenu, "Configuração", "/configuracao/Configuracao", null, null, null);
		if (principal || grupos.contains(TipoGrupo.CEO)) {
			addMenu(submenu, "Unidade Produtiva", "/configuracao/CadastroUnidadeProdutiva", null, null, null);
//			 addMenu(submenu, "Demanda técnica", "/configuracao/PesquisaDemanda", null, null);
		}
		if (Suporte.EMPRESA_PADRAO.equals(empresa.toUpperCase()))
			addMenu(submenu, "Contrato de Adesão", "/configuracao/CadastroContratoAdesao", null, null, null);
		addMenu(submenu, "Alterar senha", null, null, null, "#{pessoaSenhaBean.mostrarCaixaDialogo}");

		DefaultMenuItem item = new DefaultMenuItem();
		item.setIcon("ui-icon-home");
		item.setValue("Sair do sistema");
		item.setCommand("#{loginBean.sair}");
		submenu.addElement(item);
		if (submenu.getElements().size() > 0)
			menuModel.addElement(submenu);

	}

	public void sair() throws IOException {
		FacesContext.getCurrentInstance().getExternalContext().redirect("/sigpro/j_spring_security_logout");
	}

	public void login() {
		try {

			Suporte.USUARIO_LOGADO = nome;
			seguranca.prapara(empresa);

			if (empresa == null)
				throw new NegocioException("Defina a empresa para acesso ao sistema");

			if (!ConexaoManager.empresaExisteSchema(empresa))
				throw new NegocioException("Empresa não localizada");

			Suporte.setAtributoNaSessao("empresa", empresa.toLowerCase());
			RequestDispatcher dispatcher = request.getRequestDispatcher("/j_spring_security_check");
			dispatcher.forward(request, response);
			facesContext.responseComplete();

			if (!selecionado) {
				defineThemaDoUsuario();
				Suporte.theme = theme;
			}

			defineMenu();

			Pessoa user = pessoas.porUsuario(nome);
			if (user != null)
				eventos.guardar(new EventoPessoa(user.getUsuario(), user, "acesso ao sistema"));

			seguranca.definePastaDoCliente(empresa.toLowerCase());

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}

	}

}