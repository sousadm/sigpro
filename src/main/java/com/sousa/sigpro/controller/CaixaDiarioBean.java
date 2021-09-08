package com.sousa.sigpro.controller;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.sousa.sigpro.controller.parametro.ParametroMail;
import com.sousa.sigpro.model.Caixa;
import com.sousa.sigpro.model.CaixaDiario;
import com.sousa.sigpro.model.CaixaValor;
import com.sousa.sigpro.model.Contingencia;
import com.sousa.sigpro.model.Movimento;
import com.sousa.sigpro.model.Pessoa;
import com.sousa.sigpro.model.Planner;
import com.sousa.sigpro.model.evento.EventoDiario;
import com.sousa.sigpro.model.tipo.TipoMedidaContingencia;
import com.sousa.sigpro.model.tipo.TipoPlanner;
import com.sousa.sigpro.model.tipo.TipoTituloOrigem;
import com.sousa.sigpro.repository.CaixaDiarios;
import com.sousa.sigpro.repository.Caixas;
import com.sousa.sigpro.repository.Contas;
import com.sousa.sigpro.repository.Contingencias;
import com.sousa.sigpro.repository.Eventos;
import com.sousa.sigpro.repository.Financeiros;
import com.sousa.sigpro.repository.Planners;
import com.sousa.sigpro.repository.filter.CondicaoFilter;
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
public class CaixaDiarioBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private HttpServletResponse response;
	@Inject
	private Seguranca seguranca;
	@Inject
	private CaixaDiarios diarios;
	@Inject
	private Caixas caixas;
	@Inject
	private Eventos eventos;
	@Inject
	private Contas contas;
	@Inject
	private Financeiros financeiros;
	@Inject
	private Contingencias contingencias;
	@Inject
	private Planners planners;
	@Inject
	private ParametroMail mail;

	private CondicaoFilter filtro;
	private Date novaDataDiario;
	private Caixa caixa;
	private Planner planner;
	private CaixaDiario diario;
	private List<Contingencia> listaContingencia;
	private LazyDataModel<CaixaDiario> lista;
	private List<Movimento> listaMovimento = new ArrayList<>();

	public CaixaDiarioBean() {

	}

	public void inicializar() {
		if (FacesUtil.isNotPostback()) {
			listaContingencia = contingencias.lista();
			filtro = new CondicaoFilter();
			filtro.setInicio(SuporteData.primeiraDataDoMes(new Date()));
			filtro.setTermino(new Date());
			planner = new Planner();
			pesquisar();
		}
	}

	public Date getNovaDataDiario() {
		return novaDataDiario;
	}

	public void setNovaDataDiario(Date data) {
		this.novaDataDiario = data;
	}

	public Caixa getCaixa() {
		return caixa;
	}

	public void setCaixa(Caixa caixa) {
		this.caixa = caixa;
	}

	public void novo() {
		diario = new CaixaDiario();
	}

	public void editar() {
		diario = diarios.ultimo();
	}

	public CaixaDiario getDiario() {
		return diario;
	}

	public LazyDataModel<CaixaDiario> getLista() {
		return lista;
	}

	public CondicaoFilter getFiltro() {
		return filtro;
	}

	public void setFiltro(CondicaoFilter filtro) {
		this.filtro = filtro;
	}

	public Planner getPlanner() {
		return planner;
	}

	public void setPlanner(Planner planner) {
		this.planner = planner;
	}

	public List<Contingencia> getListaContingencia() {
		return listaContingencia;
	}

	public void addContingnencia() {
		planner.setQue(planner.getContingencia().getDescricao());
		if (diario.getContingencias().indexOf(planner) < 0)
			diario.getContingencias().add(planner);
	}

	public void editContingencia(Planner pl) {
		planner = pl;
	}

	public void removeContingencia(int linha) {
		if (linha >= 0) {
			diario.getContingencias().remove(linha);
		}
	}

	public void novoPlanner() {
		try {
			double valor = diario.getSaldoContingente();
			for (Planner planner : diario.getContingencias()) {
				valor = valor + planner.getValor()
						* (planner.getContingencia().getMedida().equals(TipoMedidaContingencia.POSITIVO) ? -1 : 1);
			}

			if (valor == 0) {
				throw new NegocioException("Sem valor para contingência");
			}

			planner = new Planner();
			planner.setTipo(TipoPlanner.GERAL);
			planner.setData(new Date());
			planner.setDiario(diario);
			planner.setQuando(new Date());
			planner.setQuem(seguranca.getPessoaLogado());
			planner.setValor(Suporte.valorAbsoluto(valor));
			if (diario.getSaldoContingente() < 0)
				listaContingencia = contingencias.lista(TipoMedidaContingencia.NEGATIVO);
			else
				listaContingencia = contingencias.lista(TipoMedidaContingencia.POSITIVO);
		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}

	}

	public void imprimir() {
		try {

			if (diario.getDataImpressao() == null) {
				diario.setDataImpressao(new Date());
			} else {
				diario.setDataReimpressao(new Date());
			}

			List<CaixaDiario> lst = new ArrayList<>();
			lst.add(diario);

			String path = seguranca.pathClass("relatorio");
			String arquivo = path + "diario.jasper";

			Map<String, Object> parametros = new HashMap<String, Object>();
			parametros.put("REPORT_LOCALE", new Locale("pt", "BR"));

			JRBeanCollectionDataSource jrds = new JRBeanCollectionDataSource(lst);
			JasperPrint printer = JasperFillManager.fillReport(arquivo, parametros, jrds);
			Suporte.ImprimirArquivo(response, printer, null);

			diario = diarios.guardar(diario);

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void imprimirMovimentacao() {
		try {

			List<CaixaValor> lst = caixas.movimentoValorPorDiario(diario);

			String path = seguranca.pathClass("relatorio");
			String arquivo = path + "diarioValores.jasper";

			Map<String, Object> parametros = new HashMap<String, Object>();
			parametros.put("REPORT_LOCALE", new Locale("pt", "BR"));

			JRBeanCollectionDataSource jrds = new JRBeanCollectionDataSource(lst);
			JasperPrint printer = JasperFillManager.fillReport(arquivo, parametros, jrds);
			Suporte.ImprimirArquivo(response, printer, null);

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void imprimir_lista() throws JRException, SQLException {
		try {

			Suporte.validaPeriodo(filtro.getInicio(), filtro.getTermino());

			filtro.setSortField("dataMov");
			filtro.setAscendente(true);
			filtro.setPrimeiroRegistro(null);
			List<CaixaDiario> lst = diarios.lista(filtro);

			if (lst.size() == 0)
				throw new Exception("Sem dados para imprimir.");

			HashMap<String, Object> parametros = new HashMap<String, Object>();
			parametros.put("empresa", seguranca.getPessoaLogadoOrigem());
			parametros.put("empresa_endereco", seguranca.enderecoEmpresa());

			String path = seguranca.pathClass("relatorio");
			String arquivo = path + "diario_lista.jasper";

			JRBeanCollectionDataSource jrds = new JRBeanCollectionDataSource(lst);
			JasperPrint printer = JasperFillManager.fillReport(arquivo, parametros, jrds);
			Suporte.ImprimirArquivo(response, printer, null);

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public List<Movimento> getListaMovimento() {
		return listaMovimento;
	}

	public void reabrir() {
		try {

			diarios.reabrirAPartirDe(diario);
			diario = diarios.porId(diario.getId());
			diario.setContingencias(planners.lista(diario));

			FacesUtil.addInfoMessage("Concuido com sucesso");
			eventos.guardar(new EventoDiario(seguranca.getPessoaLogadoOrigem().getUsuario(), diario,
					"reabertura de diário financeiro "
							+ SuporteData.formataDataToStr(diario.getDataMov(), "dd/MM/yyyy")));

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void recibo(Long caixa_id) {
		try {

			mail.ler();
			String path = seguranca.pathClass("relatorio");
			String arquivo = path + "recibo.jasper";
			String logo = Suporte.uploadLocal + mail.getLogomarca();

			Caixa caixa = financeiros.caixaPorID(caixa_id);
			caixa.setValorPorExtenso(Suporte.valorPorExtenso(caixa.getValor()));
			String conteudo = caixa.getTipoDC() == TipoTituloOrigem.PAGAR ? "Pagamos a " + caixa.getNominal()
					: "Recebemos de " + caixa.getNominal() + " o valor de "
							+ Suporte.formataCurrency(caixa.getValor()).trim() + " (" + caixa.getValorPorExtenso()
							+ ") referente: " + caixa.getObservacao().trim();
			caixa.setObservacao(conteudo);

			List<Caixa> lista = new ArrayList<>();
			lista.add(caixa);

			Map<String, Object> parametros = new HashMap<>();
			parametros.put("REPORT_LOCALE", new Locale("pt", "BR"));
			parametros.put("empresa", seguranca.getPessoaLogadoOrigem());
			parametros.put("empresa_endereco", seguranca.enderecoEmpresa());
			parametros.put("logo", logo);

			JRBeanCollectionDataSource jrds = new JRBeanCollectionDataSource(lista);
			JasperPrint printer = JasperFillManager.fillReport(arquivo, parametros, jrds);
			Suporte.ImprimirArquivo(response, printer, null);

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void setDiario(CaixaDiario diario) {
		try {

			if (diario != null && diario.isExiste())
				diario = diarios.porId(diario.getId());

			this.diario = diario;

			if (diario != null && diario.isExiste())
				calcularCaixaDiario();

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void pesquisar() {

		lista = new LazyDataModel<CaixaDiario>() {

			private static final long serialVersionUID = 1L;

			@Override
			public List<CaixaDiario> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {

				filtro.setPrimeiroRegistro(Long.valueOf(first));
				filtro.setQuantidadeRegistros(Long.valueOf(pageSize));
				filtro.setSortField(sortField);
				filtro.setAscendente(SortOrder.ASCENDING.equals(sortOrder));
				int total = diarios.quantidadeFiltrados(filtro);
				setRowCount(total);
				return diarios.lista(filtro);
			}

		};

	}

	public void calcularPrevisao(SelectEvent event) {
		try {

			CaixaDiario atual = diarios.porData(diario.getDataMov());
			if (atual != null)
				throw new NegocioException("Existe diário financeiro nesta data");

			calcularCaixaDiario();

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void encerrar(boolean todos) {
		try {

			if (seguranca.getPessoaLogado().getAgente() == null
					|| seguranca.getPessoaLogado().getAgente().getId() == null)
				throw new Exception("O usuário logado deve ser um agente financeiro!");

			if (diario.getFechamento() != null)
				throw new Exception("Caixa atual já está fechado!");

			CaixaDiario anterior = diarios.abertoAnteriorData(diario.getDataMov());
			if (anterior != null) {
				throw new Exception("Existe diário aberto anterior a este fechamento: "
						+ SuporteData.formataDataToStr(anterior.getDataMov(), "dd/MM/yyyy"));
			}

			diario.setFechamento(new Date());
			diario.setOrigem(seguranca.getPessoaLogadoOrigem());
			diario.setAgente(seguranca.getPessoaLogado().getAgente());
			diario = diarios.guardar(diario);

			if (todos) {
				CaixaDiario ultimo = diarios.ultimo();
				if (diario.getDataMov().before(ultimo.getDataMov()))
					diarios.salvarDiarioEPosteriores(diario, true);
			}

			FacesUtil.addInfoMessage("encerrado com sucesso");

			eventos.guardar(new EventoDiario(seguranca.getPessoaLogadoOrigem().getUsuario(), diario,
					"encerramento de diário financeiro"));

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}

	}

	public void calcularCaixaDiario() {
		try {

			double valor_banco = 0;
			Date inicio = null;
			Date data = diario.getDataMov();

			listaMovimento = diarios.listaDiarioMovimento(data, diario.getDataMov());

			diario.setPrevisaoPagamento(
					financeiros.saldo(TipoTituloOrigem.PAGAR, seguranca.getPessoaLogadoOrigem().getAgente(), data));

			diario.setPrevisaoRecebimento(
					financeiros.saldo(TipoTituloOrigem.RECEBER, seguranca.getPessoaLogadoOrigem().getAgente(), data));

			CaixaDiario anterior = diarios.diarioAnteriorData(data);
			if (anterior != null) {
				diario.setInicialDinheiro(anterior.getFinalDinheiro());
				diario.setInicialCheque(anterior.getFinalCheque());
				diario.setInicialBanco(contas.saldoInicialEmConta(null, diario.getDataMov()));
				inicio = SuporteData.incrementaDiaNaData(anterior.getDataMov(), 1);
			}

			Date dataInicial = anterior == null ? null : anterior.getDataMov();			
			valor_banco = contas.saldoMovimentoEmContaOrigem(diario.getOrigem(), dataInicial, diario.getDataMov());
			diario.setFinalBanco(diario.getInicialBanco() + valor_banco);
			
			listaMovimento = diarios.listaDiarioMovimento(inicio, diario.getDataMov());

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}

	}

	public void salvar() {
		try {

			if (seguranca.getPessoaLogado().getAgente() == null
					|| seguranca.getPessoaLogado().getAgente().getId() == null)
				throw new Exception("O usuário logado deve ser um agente financeiro!");

			if (diario.getFechamento() != null)
				throw new Exception("Caixa atual já está fechado!");

			if (diario.getId() == null) {
				CaixaDiario caixa = diarios.porData(diario.getDataMov());
				if (caixa != null)
					throw new Exception("Já existe diário nesta data!");
			}

			if (diario.getAbertura() == null)
				diario.setAbertura(new Date());

			diario.setOrigem(seguranca.getPessoaLogadoOrigem());
			diario.setAgente(seguranca.getPessoaLogado().getAgente());
			diarios.preparaSaldoFinal(diario);
			diario = diarios.guardar(diario);

			CaixaDiario ultimo = diarios.ultimo();
			if (diario.getDataMov().before(ultimo.getDataMov()))
				diarios.salvarDiarioEPosteriores(diario, false);

			FacesUtil.addInfoMessage("Registro gravado com sucesso.");

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

}