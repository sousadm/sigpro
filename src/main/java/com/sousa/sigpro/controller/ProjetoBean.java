package com.sousa.sigpro.controller;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;

import org.primefaces.component.organigram.OrganigramHelper;
import org.primefaces.component.tabview.TabView;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.event.organigram.OrganigramNodeCollapseEvent;
import org.primefaces.event.organigram.OrganigramNodeDragDropEvent;
import org.primefaces.event.organigram.OrganigramNodeExpandEvent;
import org.primefaces.event.organigram.OrganigramNodeSelectEvent;
import org.primefaces.model.DefaultOrganigramNode;
import org.primefaces.model.OrganigramNode;

import com.sousa.sigpro.model.CentroDeCusto;
import com.sousa.sigpro.model.Participante;
import com.sousa.sigpro.model.Pergunta;
import com.sousa.sigpro.model.Pessoa;
import com.sousa.sigpro.model.Planner;
import com.sousa.sigpro.model.Projeto;
import com.sousa.sigpro.model.ProjetoDefinicao;
import com.sousa.sigpro.model.ProjetoRequisito;
import com.sousa.sigpro.model.Questionario;
import com.sousa.sigpro.model.Resposta;
import com.sousa.sigpro.model.Titulo;
import com.sousa.sigpro.model.evento.EventoHistorico;
import com.sousa.sigpro.model.evento.EventoProjeto;
import com.sousa.sigpro.model.tipo.TipoEntradaSaida;
import com.sousa.sigpro.model.tipo.TipoPlanner;
import com.sousa.sigpro.model.tipo.TipoStakeHolder;
import com.sousa.sigpro.model.tipo.TipoTituloOrigem;
import com.sousa.sigpro.repository.CentrosDeCusto;
import com.sousa.sigpro.repository.Eventos;
import com.sousa.sigpro.repository.Pessoas;
import com.sousa.sigpro.repository.Planners;
import com.sousa.sigpro.repository.Projetos;
import com.sousa.sigpro.repository.Questionarios;
import com.sousa.sigpro.repository.Titulos;
import com.sousa.sigpro.security.Seguranca;
import com.sousa.sigpro.service.NegocioException;
import com.sousa.sigpro.util.Suporte;
import com.sousa.sigpro.util.SuporteData;
import com.sousa.sigpro.util.jsf.FacesUtil;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Named
@ViewScoped
public class ProjetoBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private HttpServletResponse response;
	@Inject
	private Seguranca seguranca;
	@Inject
	private Projetos projetos;
	@Inject
	private Questionarios questionarios;
	@Inject
	private CentrosDeCusto centros;
	@Inject
	private Titulos titulos;
	@Inject
	private Planners planners;
	@Inject
	private Eventos eventos;
	@Inject
	private Pessoas pessoas;

	private Projeto projeto;
	private Titulo titulo;
	private Planner planner;
	private Planner objetivo;
	private Planner risco;
	private ProjetoRequisito requisito;
	private Participante participante;

	private Date dataPrevistaEncerramento;
	private double valorIncrementoFinanceiro;
	private List<ProjetoRequisito> listaRequisitos;
	private List<Participante> listaParticipantes;
	private List<CentroDeCusto> listaCusto;
	private List<Projeto> lista;
	private List<EventoHistorico> listaEventoHistorico;
	private List<Titulo> listaTitulo;
	private List<SelectItem> listaPacoteMenu;
	private List<Planner> listaEntregaMenu;
	private List<Planner> listaRiscos;
	private List<Planner> listaObjetivos;
	private List<Planner> listaMarcos;
	private List<Planner> lstEap;
	private List<Planner> listaDicionarioEap = new ArrayList<Planner>();

	// private String editorControl = "bold italic underline | font size style color
	// highlight | bullets numbering | alignleft center alignright justify | undo
	// redo | copy cut paste pastetext | outdent indent | removeformat";
	private OrganigramNode rootNode;
	private OrganigramNode selection;
	private boolean zoom = false;
	private int leafNodeConnectorHeight = 0;
	private boolean autoScrollToSelection = false;
	private boolean pacoteEmFoco = false;
	private boolean entregaEmFoco = false;
	private boolean temEntrega;
	private boolean temPacote;
	private String employeeName;
	private String definicao;
	private String definicaoTitulo;
	private int activeIndex;

	public void consultar() {
		lista = projetos.lista();
	}

	public List<Projeto> getLista() {
		return lista;
	}

	public void setLista(List<Projeto> lista) {
		this.lista = lista;
	}

	public Titulo getTitulo() {
		return titulo;
	}

	public List<CentroDeCusto> getListaCusto() {
		return listaCusto;
	}

	public Date getDataPrevistaEncerramento() {
		return dataPrevistaEncerramento;
	}

	public void setDataPrevistaEncerramento(Date dataPrevistaEncerramento) {
		this.dataPrevistaEncerramento = dataPrevistaEncerramento;
	}

	public double getValorIncrementoFinanceiro() {
		return valorIncrementoFinanceiro;
	}

	public void setValorIncrementoFinanceiro(double valorIncrementoFinanceiro) {
		this.valorIncrementoFinanceiro = valorIncrementoFinanceiro;
	}

	public void nodeCollapseListener(OrganigramNodeCollapseEvent event) {
		FacesUtil.addInfoMessage("Node '" + event.getOrganigramNode().getData() + "' collapsed.");
	}

	public void nodeExpandListener(OrganigramNodeExpandEvent event) {
		FacesUtil.addInfoMessage("Node '" + event.getOrganigramNode().getData() + "' expanded.");
	}

	public OrganigramNode getRootNode() {
		return rootNode;
	}

	public void setRootNode(OrganigramNode rootNode) {
		this.rootNode = rootNode;
	}

	public OrganigramNode getSelection() {
		return selection;
	}

	public void setSelection(OrganigramNode selection) {
		this.selection = selection;
	}

	public boolean isZoom() {
		return zoom;
	}

	public void setZoom(boolean zoom) {
		this.zoom = zoom;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public int getLeafNodeConnectorHeight() {
		return leafNodeConnectorHeight;
	}

	public void setLeafNodeConnectorHeight(int leafNodeConnectorHeight) {
		this.leafNodeConnectorHeight = leafNodeConnectorHeight;
	}

	public boolean isAutoScrollToSelection() {
		return autoScrollToSelection;
	}

	public void setAutoScrollToSelection(boolean autoScrollToSelection) {
		this.autoScrollToSelection = autoScrollToSelection;
	}

	// public String getEditorControl() {
	// return editorControl;
	// }

	public Planner getPlanner() {
		return planner;
	}

	public void setPlanner(Planner planner) {
		this.planner = planner;
	}

	public void removeEscopoItem() {
		try {
			Planner eap = (Planner) selection.getData();
			planners.remover(eap);
			OrganigramNode currentSelection = OrganigramHelper.findTreeNode(rootNode, selection);
			currentSelection.getParent().getChildren().remove(currentSelection);
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Falha ao excluir o item do escopo");
		}
	}

	public List<Planner> getListaDicionarioEap() {
		return listaDicionarioEap;
	}

	public void setListaCusto(List<CentroDeCusto> listaCusto) {
		this.listaCusto = listaCusto;
	}

	public Planner currentSelectionToPlanner() {
		try {
			OrganigramNode currentSelection = OrganigramHelper.findTreeNode(rootNode, selection);
			return (Planner) currentSelection.getData();
		} catch (Exception e) {
			return null;
		}
	}

	public void nodeDragDropListener(OrganigramNodeDragDropEvent event) {

		Planner eapDestino = null;

		try {

			if (!event.getTargetOrganigramNode().getRowKey().equals("root")) {
				eapDestino = (Planner) event.getTargetOrganigramNode().getData();
				if (eapDestino.getTipo() == TipoPlanner.PROJETO_PACOTE)
					throw new NegocioException("Operação não permitida para pacotes");
			}

			Planner eap = (Planner) event.getOrganigramNode().getData();
			eap.setOrigem(eapDestino);
			eap = planners.guardar(eap);

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		} finally {
			preparaEAP();
		}

	}

	public boolean isEntregaEmFoco() {
		return entregaEmFoco;
	}

	public boolean isPacoteEmFoco() {
		return pacoteEmFoco;
	}

	public void nodeSelectListener(OrganigramNodeSelectEvent event) {
		try {
			entregaEmFoco = event.getOrganigramNode().getType().equals(TipoPlanner.PROJETO_ENTREGA.getDescricao());
			pacoteEmFoco = event.getOrganigramNode().getType().equals(TipoPlanner.PROJETO_PACOTE.getDescricao());
			planner = (Planner) event.getOrganigramNode().getData();
		} catch (Exception e) {
			planner = null;
		}
	}

	/* MUDANÇA */

	public ProjetoRequisito getRequisito() {
		return requisito;
	}

	public void setRequisito(ProjetoRequisito requisito) {
		this.requisito = requisito;
	}

	public void removerObjetivo(int linha) {
		if (linha >= 0)
			projeto.getObjetivos().remove(linha);
	}

	public Planner getObjetivo() {
		return objetivo;
	}

	public void setObjetivo(Planner objetivo) {
		this.objetivo = objetivo;
	}

	public void modificarData() {
		Date dataOld = projeto.getAbertura().getDataPrevistaTermino();
		try {

			if (projeto.getDataCadastro() != null && projeto.getAbertura().getDataPrevistaTermino() != null
					&& projeto.getAbertura().getDataPrevistaTermino().before(projeto.getDataCadastro()))
				throw new NegocioException("Data incorreta para previsão de encerramento");

			projeto.getAbertura().setDataPrevistaTermino(dataPrevistaEncerramento);
			projeto = projetos.guardar(projeto, null);
			FacesUtil.addInfoMessage("Evento registrado com sucesso");

			eventos.guardar(new EventoProjeto(seguranca.getPessoaLogadoOrigem().getUsuario(), projeto,
					"previsão de encerramento do projeto " + Suporte.zerosAEsquerda(projeto.getId(), 6) + " para "
							+ SuporteData.formataDataToStr(dataPrevistaEncerramento, "dd/MM/yyyy")));

		} catch (Exception e) {
			projeto.getAbertura().setDataPrevistaTermino(dataOld);
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void definirAberturaProjeto() {
		try {
			projeto.getAbertura().setDataAbertura(new Date());
			projeto = projetos.guardar(projeto, null);
			eventos.guardar(new EventoProjeto(seguranca.getPessoaLogadoOrigem().getUsuario(), projeto,
					"abertura do projeto " + Suporte.zerosAEsquerda(projeto.getId(), 6)));
			FacesUtil.addInfoMessage("Abertura do projeto realizada com sucesso");
		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}

	}

	public void incrementarValorAoProjeto() {

		double valorOld = projeto.getAbertura().getValorPrevisto();

		try {

			if (this.valorIncrementoFinanceiro < 0)
				throw new NegocioException("valor incorreto");

			projeto.incrementarValorOrcamento(valorIncrementoFinanceiro);
			projeto = projetos.guardar(projeto, null);
			FacesUtil.addInfoMessage("Evento registrado com sucesso");
			eventos.guardar(new EventoProjeto(seguranca.getPessoaLogadoOrigem().getUsuario(), projeto,
					"ajustes financeiro ao projeto " + Suporte.zerosAEsquerda(projeto.getId(), 6) + " no valor de R$ "
							+ Suporte.formataCurrency(valorIncrementoFinanceiro)));

		} catch (Exception e) {
			projeto.getAbertura().setValorPrevisto(valorOld);
			FacesUtil.addErrorMessage(e.getMessage());
		}

	}

	public List<EventoHistorico> getListaEventoHistorico() {
		if (projeto != null && projeto.isExiste())
			listaEventoHistorico = eventos.lista(projeto);
		else
			listaEventoHistorico = new ArrayList<>();
		return listaEventoHistorico;
	}

	public Projeto getProjeto() {
		return projeto;
	}

	public void inicializar() {
		if (FacesUtil.isNotPostback()) {
			listaDicionarioEap = new ArrayList<>();
			planner = new Planner();
			listaCusto = centros.lista(TipoEntradaSaida.SAIDA);
			lista = new ArrayList<Projeto>();
		}
	}

	public void excluirParticipante(int linha) {
		projeto.getParticipantes().remove(linha);
	}

	public int getActiveIndex() {
		return activeIndex;
	}

	public void setActiveIndex(int activeIndex) {
		this.activeIndex = activeIndex;
	}

	public void onParticipanteChosen(SelectEvent event) {
		try {
			Pessoa pessoa = (Pessoa) event.getObject();
			for (Participante parte : projeto.getParticipantes()) {
				if (parte.getPessoa().equals(pessoa))
					throw new NegocioException("participante já encontrado na lista");
			}
			pessoa = pessoas.porId(pessoa.getId());
			participante = new Participante();
			participante.setPessoa(pessoa);
		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public Participante getParticipante() {
		return participante;
	}

	public void setParticipante(Participante participante) {
		this.participante = participante;
	}

	public void voltarListaParticipante() {
		participante = null;
	}

	public void addParticipante() {
		participante = new Participante();
	}

	public void aplicar_objetivo() {
		if (objetivo.getProjeto() == null) {
			objetivo.setProjeto(projeto);
			projeto.getObjetivos().add(objetivo);
		}
		objetivo = null;
	}

	public void onQuestionarioChosen(SelectEvent event) {
		try {
			Questionario questionario = (Questionario) event.getObject();
			questionario = questionarios.porId(questionario.getId());
			if (questionario.getPerguntas() != null && questionario.getPerguntas().size() > 0) {
				for (Pergunta question : questionario.getPerguntas()) {
					projeto.getRespostas().add(new Resposta(question, projeto));
				}
			}
			projeto = projetos.guardar(projeto, null);
			FacesUtil.addErrorMessage("questionário associado ao seu projeto");
		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void removerRespostas() {
		try {
			projeto.getRespostas().clear();
			projeto = projetos.guardar(projeto, null);
			eventos.guardar(new EventoProjeto(seguranca.getPessoaLogadoOrigem().getUsuario(), projeto,
					"remoção de respostas para projeto " + Suporte.zerosAEsquerda(projeto.getId(), 6)));
			activeIndex = 0;
			FacesUtil.addInfoMessage("Removido com sucesso");
		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void encerrar() {
		try {
			projeto.getEncerramento().setDataEncerramento(new Date());
			projeto = projetos.guardar(projeto, null);
			FacesUtil.addInfoMessage("Evento registrado com sucesso");
			eventos.guardar(new EventoProjeto(seguranca.getPessoaLogadoOrigem().getUsuario(), projeto,
					"registro de encerramento de projeto em " + SuporteData
							.formataDataToStr(projeto.getEncerramento().getDataEncerramento(), "dd/MM/yyyy")));
			activeIndex = 0;
		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void reabrir() {
		try {
			projeto.getEncerramento().setDataEncerramento(null);
			projeto = projetos.guardar(projeto, null);
			FacesUtil.addInfoMessage("Evento registrado com sucesso");
			eventos.guardar(new EventoProjeto(seguranca.getPessoaLogadoOrigem().getUsuario(), projeto,
					"reabertura de projeto em " + SuporteData.formataDataToStr(new Date(), "dd/MM/yyyy")));
			activeIndex = 0;
		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void setTitulo(Titulo titulo) {
		this.titulo = titulo;
	}

	/************ 09/04/2020 ****************************/

	public List<Planner> getListaObjetivos() {
		return listaObjetivos;
	}

	public List<Titulo> getListaTitulo() {
		return listaTitulo;
	}

	public void onFornecedorChosen(SelectEvent event) {
		Pessoa pessoa = (Pessoa) event.getObject();
		titulo.setResponsavel(pessoa);
		titulo.setNominal(pessoa.getNome());
		if (pessoa.getFornecedor() != null && pessoa.getFornecedor().getCentroDeCusto() != null) {
			titulo.setCentroDeCusto(pessoa.getFornecedor().getCentroDeCusto());
		}
	}

	public void liberarTitulo() {
		try {
			if (titulo.getId() == null)
				throw new NegocioException("Documento não pode ser liberado");
			titulo.liberarSaldo();
			titulo = titulos.guardar(titulo);
			titulo = null;
			FacesUtil.addInfoMessage("Liberado com sucesso");
		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public double getValorRealizado() {
		double valor = 0;
		if (listaTitulo != null && listaTitulo.size() > 0)
			for (Titulo titulo : listaTitulo) {
				if (titulo.getPrevisao() != null) {
					valor = valor + titulo.getValor() - titulo.getSaldo();
				}
			}
		return valor;
	}

	public void novoTarefaEap() {
		planner = new Planner(TipoPlanner.PROJETO_TAREFA);
	}

	public List<Participante> getListaParticipantes() {
		return listaParticipantes;
	}

	/* riscos do projeto */

	public Planner getRisco() {
		return risco;
	}

	public void setRisco(Planner risco) {
		this.risco = risco;
	}

	public List<Planner> getListaRiscos() {
		return listaRiscos;
	}

	public void addRisco() {
		risco = new Planner(TipoPlanner.PROJETO_RISCO);
	}

	public List<SelectItem> getListaPacoteMenu() {
		return listaPacoteMenu;
	}

	public void aplicar_participante() {
		if (participante.getProjeto() == null) {
			participante.setProjeto(projeto);
			participante.getPessoa().setOrigem(seguranca.getPessoaLogadoOrigem());
			projeto.getParticipantes().add(participante);
		}
		participante = null;
	}

	public boolean isPodeGravar() {
		return titulo == null && objetivo == null && risco == null && requisito == null && participante == null;
	}

	public void salvar() {
		try {

			if (projeto.isEncerrado())
				throw new NegocioException("projeto está encerrado");

			projeto = projetos.guardar(projeto, listaTitulo);
			listaObjetivos = projetos.objetivos(projeto);
			FacesUtil.addInfoMessage("gravado com sucesso");

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void novoEntreaEap() {
		planner = new Planner(TipoPlanner.PROJETO_ENTREGA);
	}

	public void novoPacoteEap() {
		planner = new Planner(TipoPlanner.PROJETO_PACOTE);
		planner.setQuando(projeto.getDataCadastro());
		planner.setOrigem(currentSelectionToPlanner());
	}

	public boolean isTemEntrega() {
		return temEntrega;
	}

	public boolean isTemPacote() {
		return temPacote;
	}

	public void preparaEAP() {

		temEntrega = false;
		temPacote = false;

		listaParticipantes = projetos.participantes(projeto);
		rootNode = new DefaultOrganigramNode("root", projeto.getEntregaPrincipal(), null);
		rootNode.setCollapsible(false);
		rootNode.setDroppable(true);
		rootNode.setSelectable(true);

		lstEap = planners.listaEapRaiz(projeto);

		for (Planner eap : lstEap)
			addDivision(rootNode, eap);
		selection = rootNode;
	}

	protected OrganigramNode addDivision(OrganigramNode parent, Planner data) {

		if (data.getTipo() == TipoPlanner.PROJETO_ENTREGA)
			temEntrega = true;
		else if (data.getTipo() == TipoPlanner.PROJETO_PACOTE)
			temPacote = true;

		OrganigramNode divisionNode = new DefaultOrganigramNode(data.getTipo().getDescricao(), data, parent);
		divisionNode.setDroppable(true);
		divisionNode.setDraggable(true);
		divisionNode.setSelectable(true);

		listaDicionarioEap.add(data);

		List<Planner> lst = planners.listaEapDetalhe(data);
		if (lst != null && lst.size() > 0)
			for (Planner eap : lst)
				addDivision(divisionNode, eap);

		return divisionNode;
	}

	public void gravarPacoteEap() {
		OrganigramNode currentSelection = OrganigramHelper.findTreeNode(rootNode, selection);
		if (!planner.isExiste()) {
			planner.setProjeto(projeto);
			planner.setOrigem(currentSelectionToPlanner());
			planner.setQuando(projeto.getAbertura().getDataPrevistaTermino());
			planner.setQuem(seguranca.getPessoaLogadoOrigem());
			planner = planners.guardar(planner);
			OrganigramNode pacote = new DefaultOrganigramNode(planner.getTipo().getDescricao(), planner,
					currentSelection);
			pacote.setSelectable(true);
			pacote.setDraggable(true);
			pacote.setDroppable(true);
		} else {
			planner = planners.guardar(planner);
		}

		if (planner.getTipo() == TipoPlanner.PROJETO_ENTREGA)
			temEntrega = true;
		else if (planner.getTipo() == TipoPlanner.PROJETO_PACOTE)
			temPacote = true;

		planner = new Planner();
	}

	public void addObjetivo() {
		objetivo = new Planner(TipoPlanner.PROJETO_OBJETIVO);
		objetivo.setQuando(projeto.getAbertura().getDataPrevistaTermino());
	}

	public List<ProjetoRequisito> getListaRequisitos() {
		return listaRequisitos;
	}

	public void setListaRequisitos(List<ProjetoRequisito> listaRequisitos) {
		this.listaRequisitos = listaRequisitos;
	}

	public void gravar_requisito() {
		requisito = projetos.guardar(requisito);
		if (listaRequisitos.indexOf(requisito) < 0)
			listaRequisitos.add(requisito);
		requisito = null;
	}

	public void removerRequisito(int linha) {
		requisito = listaRequisitos.get(linha);
		projetos.remover(requisito);
		listaRequisitos.remove(linha);
		requisito = null;
	}

	public void addRequisito() {
		requisito = new ProjetoRequisito();
	}

	public List<Planner> getListaEntregaMenu() {
		return listaEntregaMenu;
	}

	public void praparaMenuEntrega() {
		listaEntregaMenu = planners.listaEapRaiz(projeto);
	}

	public void preparaMenuPacote() {
		praparaMenuEntrega();
		listaPacoteMenu = new ArrayList<>();
		SelectItemGroup grupo;
		List<Planner> lstItem;
		for (Planner p : listaEntregaMenu) {
			lstItem = planners.listaEapDetalhe(p);
			if (lstItem != null && lstItem.size() > 0) {
				SelectItem[] items = new SelectItem[lstItem.size()];
				for (int i = 0; i < lstItem.size(); i++)
					items[i] = new SelectItem(lstItem.get(i), lstItem.get(i).getQue());
				grupo = new SelectItemGroup(p.getQue());
				grupo.setSelectItems(items);
				listaPacoteMenu.add(grupo);
			}
		}
	}

	public void gravar_risco() {
		risco.setProjeto(projeto);
		risco.setData(projeto.getAbertura().getDataPrevistaTermino());
		risco = planners.guardar(risco);
		if (listaRiscos.indexOf(risco) < 0)
			listaRiscos.add(risco);
		risco = null;
	}

	public void excluirRisco(int linha) {
		try {
			risco = listaRiscos.get(linha);
			planners.remover(risco);
			listaRiscos.remove(linha);
			risco = null;
		} catch (Exception e) {
			FacesUtil.addErrorMessage("registro não pode ser removido");
		}
	}

	public void atualizaListaRisco() {
		listaRiscos = projetos.riscos(projeto);
	}

	public void atualizaListaRequisito() {
		listaRequisitos = projetos.requisitos(projeto);
	}

	/**********************************/

	public void atualizarListaTitulo() {
		listaTitulo = titulos.lista(projeto);
	}

	public void addTitulo() {
		titulo = new Titulo();
		titulo.setTipoDC(TipoTituloOrigem.PAGAR);
	}

	public void aplicar_titulo() {
		titulo = titulos.guardar(titulo);
		if (listaTitulo.indexOf(titulo) < 0)
			listaTitulo.add(titulo);
		titulo = null;
	}

	public void excluirTitulo(int linha) {
		try {
			titulo = listaTitulo.get(linha);
			titulos.remover(titulo);
			eventos.guardar(new EventoProjeto(seguranca.getPessoaLogadoOrigem().getUsuario(), projeto,
					"exclusão de título no valor de " + Suporte.formataCurrency(titulo.getSaldo()) + " do projeto "
							+ Suporte.zerosAEsquerda(projeto.getId(), 6)));
			listaTitulo.remove(linha);
			titulo = null;
		} catch (Exception e) {
			FacesUtil.addErrorMessage("erro ao excluir o título");
		}
	}

	public List<Planner> getListaMarcos() {
		return listaMarcos;
	}

	public void setProjeto(Projeto projeto) {
		activeIndex = 0;
		if (projeto != null) {
			projeto = projetos.porId(projeto.getId());
			if (projeto.getDefinicao() == null)
				projeto.setDefinicao(new ProjetoDefinicao());
			listaObjetivos = projetos.objetivos(projeto);
			projeto.setObjetivos(listaObjetivos);
			projeto.setParticipantes(projetos.participantes(projeto));
			projeto.setRespostas(projetos.respostas(projeto));

			listaTitulo = titulos.lista(projeto);
			listaRiscos = projetos.riscos(projeto);
			listaRequisitos = projetos.requisitos(projeto);
			listaMarcos = projetos.marcos(projeto);

		} else {
			listaTitulo = new ArrayList<>();
		}

		this.projeto = projeto;

		preparaMenuPacote();
		temEntrega = listaEntregaMenu != null && listaEntregaMenu.size() > 0;
		temPacote = listaPacoteMenu != null && listaPacoteMenu.size() > 0;

	}

	public void imprimirTermoDeAbertura() throws JRException, SQLException {
		try {

			String path = seguranca.pathClass("relatorio");
			String arquivo = path + "projeto_abertura.jasper";

			List<ProjetoRequisito> premissas = projetos.premissas(projeto);
			List<ProjetoRequisito> restricoes = projetos.restricoes(projeto);
			List<Planner> marcos = projetos.marcos(projeto);
			listaRiscos = projetos.riscos(projeto);

			Participante gerente = null;
			Participante sponsor = null;
			List<Participante> interessados = new ArrayList<>();
			listaParticipantes = projetos.participantes(projeto);
			for (Participante parte : listaParticipantes)
				if (parte.getTipo() == TipoStakeHolder.RESPONSAVEL)
					gerente = parte;
				else if (parte.getTipo() == TipoStakeHolder.PROPRIETARIO)
					sponsor = parte;
				else
					interessados.add(parte);

			Map<String, Object> parametros = new HashMap<>();
			parametros.put("SUBREPORT_DIR", path);
			parametros.put("empresa", seguranca.getPessoaLogadoOrigem());
			parametros.put("empresa_endereco", seguranca.enderecoEmpresa());
			parametros.put("gerente", gerente);
			parametros.put("sponsor", sponsor);
			if (premissas != null && premissas.size() > 0)
				parametros.put("premissas", new JRBeanCollectionDataSource(premissas));
			if (listaEntregaMenu != null && listaEntregaMenu.size() > 0)
				parametros.put("entregas", listaEntregaMenu);
			if (restricoes != null && restricoes.size() > 0)
				parametros.put("restricoes", new JRBeanCollectionDataSource(restricoes));
			if (marcos != null && marcos.size() > 0)
				parametros.put("marcos", new JRBeanCollectionDataSource(marcos));
			if (interessados != null && interessados.size() > 0)
				parametros.put("interessados", new JRBeanCollectionDataSource(interessados));
			if (listaRiscos != null && listaRiscos.size() > 0)
				parametros.put("riscos", new JRBeanCollectionDataSource(listaRiscos));

			List<Projeto> lst = new ArrayList<Projeto>();
			lst.add(projeto);
			JRBeanCollectionDataSource jrds = new JRBeanCollectionDataSource(lst);
			JasperPrint printer = JasperFillManager.fillReport(arquivo, parametros, jrds);
			Suporte.ImprimirArquivo(response, printer, null);

			eventos.guardar(new EventoProjeto(seguranca.getPessoaLogadoOrigem().getUsuario(), projeto,
					"impressão de termo de abertura para projeto " + Suporte.zerosAEsquerda(projeto.getId(), 6)));

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void novo() {
		projeto = new Projeto();

		titulo = null;
		planner = null;
		objetivo = null;
		risco = null;
		requisito = null;
		listaCusto = new ArrayList<>();
		listaEventoHistorico = new ArrayList<>();
		listaTitulo = new ArrayList<>();
		listaPacoteMenu = new ArrayList<>();
		listaEntregaMenu = new ArrayList<>();
		listaParticipantes = new ArrayList<>();
		addParticipantePadrao();
		participante = null;
		temEntrega = false;
		temPacote = false;
		activeIndex = 0;
	}

	public void addParticipantePadrao() {
		projeto.getParticipantes()
				.add(new Participante(seguranca.getPessoaLogadoOrigem(), projeto, TipoStakeHolder.PROPRIETARIO));
	}

	public String getDefinicaoTitulo() {
		return definicaoTitulo;
	}

	public void setDefinicaoTitulo(String definicaoTitulo) {
		this.definicaoTitulo = definicaoTitulo;
	}

	public String getDefinicao() {
		return definicao;
	}

	public void setDefinicao(String definicao) {
		this.definicao = definicao;
	}

	public void aplicar_definicao() {
		if (definicaoTitulo.toUpperCase().contains("ENTREGA"))
			projeto.getDefinicao().setSobreEntrega(definicao);
		else if (definicaoTitulo.toUpperCase().contains("PREMISSA"))
			projeto.getDefinicao().setSobrePremissa(definicao);
		else if (definicaoTitulo.toUpperCase().contains("RESTRIÇÃO"))
			projeto.getDefinicao().setSobreRestricao(definicao);
		else if (definicaoTitulo.toUpperCase().contains("MARCO"))
			projeto.getDefinicao().setSobreMarco(definicao);
		else if (definicaoTitulo.toUpperCase().contains("PARTICIPANTE"))
			projeto.getDefinicao().setSobreParticipante(definicao);
		else if (definicaoTitulo.toUpperCase().contains("RISCO"))
			projeto.getDefinicao().setSobreRisco(definicao);
	}

	public void onTabChange(TabChangeEvent event) {
		TabView tabView = (TabView) event.getComponent();
		if (event.getTab().getTitle().equals("EAP")) {
			planner = new Planner();
			preparaEAP();
		} else if (event.getTab().getTitle().equals("Risco")) {
			preparaMenuPacote();
		} else if (event.getTab().getTitle().equals("Requisito")) {
			praparaMenuEntrega();
		} else if (event.getTab().getTitle().equals("Financeiro")) {
			atualizarListaTitulo();
		}
		activeIndex = tabView.getChildren().indexOf(event.getTab());
	}

}