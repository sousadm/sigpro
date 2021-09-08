package com.sousa.sigpro.controller;

import java.io.Serializable;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.PersistenceException;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.VelocityContext;
import org.jrimum.domkee.financeiro.banco.febraban.TipoDeTitulo;
import org.primefaces.component.tabview.TabView;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.sousa.sigpro.controller.parametro.ParametroMail;
import com.sousa.sigpro.model.Banco;
import com.sousa.sigpro.model.Caixa;
import com.sousa.sigpro.model.CaixaDiario;
import com.sousa.sigpro.model.CaixaTitulo;
import com.sousa.sigpro.model.CaixaValor;
import com.sousa.sigpro.model.Cartao;
import com.sousa.sigpro.model.CentroDeCusto;
import com.sousa.sigpro.model.ContaCorrente;
import com.sousa.sigpro.model.Email;
import com.sousa.sigpro.model.FormaPgto;
import com.sousa.sigpro.model.Movimento;
import com.sousa.sigpro.model.Pessoa;
import com.sousa.sigpro.model.Titulo;
import com.sousa.sigpro.model.tipo.TipoCaixaStatus;
import com.sousa.sigpro.model.tipo.TipoEntradaSaida;
import com.sousa.sigpro.model.tipo.TipoPagamento;
import com.sousa.sigpro.model.tipo.TipoRepeticao;
import com.sousa.sigpro.model.tipo.TipoTituloOrigem;
import com.sousa.sigpro.repository.Bancos;
import com.sousa.sigpro.repository.CaixaDiarios;
import com.sousa.sigpro.repository.Caixas;
import com.sousa.sigpro.repository.Cartoes;
import com.sousa.sigpro.repository.CentrosDeCusto;
import com.sousa.sigpro.repository.Contas;
import com.sousa.sigpro.repository.FormaPgtos;
import com.sousa.sigpro.repository.Titulos;
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
public class CaixaBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private HttpServletResponse response;
	@Inject
	private Seguranca seguranca;
	@Inject
	private CaixaDiarios caixaDiarios;
	@Inject
	private CentrosDeCusto centros;
	@Inject
	private FormaPgtos formaPgtos;
	@Inject
	private Caixas caixas;
	@Inject
	private Cartoes cartoes;
	@Inject
	private Titulos titulos;
	@Inject
	private Contas contas;
	@Inject
	private Bancos bancos;
	@Inject
	private ParametroMail mail;

	private ContaCorrente contaBancaria;
	private double valorSaldoEmCaixa = 0;
	private FormaPgto formaPgto;
	private Caixa caixa;
	private CaixaValor caixaValor;
	private CaixaTitulo caixaTitulo;
	private CondicaoFilter filtro = new CondicaoFilter();
	private Date dataParaEncerramento;
	private Long codigoPesquisa;
	private List<FormaPgto> listaFormaPgtos;
	private List<CentroDeCusto> listaCusto;
	private LazyDataModel<Caixa> listaCaixa;
	private List<Cartao> listaCartao = new ArrayList<>();
	private List<ContaCorrente> listaContaDebito = new ArrayList<>();
	private List<ContaCorrente> listaContaBancaria = new ArrayList<>();
	private List<Integer> listaParcelas;
	private Email email;
	private CaixaDiario diario;
	private int activeIndex;
	private boolean existeFormaCartao;
	String logoImagem;

	public CaixaBean() {
		novoCaixaValor();
		caixaTitulo = new CaixaTitulo();
		contaBancaria = new ContaCorrente();
	}

	public void novoCaixaValor() {
		caixaValor = new CaixaValor();
		caixaValor.setTitulo(new Titulo());
		caixaValor.getTitulo().setContaBancaria(new ContaCorrente());
		caixaValor.getTitulo().setCartao(new Cartao());
	}

	public void inicializar() {
		if (FacesUtil.isNotPostback()) {
			diario = caixaDiarios.ultimo();
			existeFormaCartao = !formaPgtos.listaDebCred().isEmpty();
			listaCartao = cartoes.lista();
			listaParcelas = new ArrayList<Integer>();
			for (int i = 1; i <= 10; i++) {
				listaParcelas.add(i);
			}
			listaFormaPgtos = formaPgtos.listaDebCred();
			listaContaDebito = contas.listaContaDebito();
			listaContaBancaria = contas.listaContaCheque();
			mail.ler();
			logoImagem = Suporte.uploadLocal + mail.getLogomarca();
			listaCusto = centros.lista(TipoEntradaSaida.ENTRADA);
		}
	}

	public CaixaValor getCaixaValor() {
		return caixaValor;
	}

	public CaixaTitulo getCaixaTitulo() {
		return caixaTitulo;
	}

	public void setCaixaTitulo(CaixaTitulo caixaTitulo) {
		this.caixaTitulo = caixaTitulo;
	}

	public FormaPgto getFormaPgto() {
		return formaPgto;
	}

	public void setFormaPgto(FormaPgto formaPgto) {
		this.formaPgto = formaPgto;
	}

	public Email getEmail() {
		return email;
	}

	public void setEmail(Email email) {
		this.email = email;
	}

	public List<FormaPgto> getListaFormaPgtos() {
		return listaFormaPgtos;
	}

	public void setListaFormaPgtos(List<FormaPgto> listaFormaPgtos) {
		this.listaFormaPgtos = listaFormaPgtos;
	}

	public LazyDataModel<Caixa> getListaCaixa() {
		return listaCaixa;
	}

	public CondicaoFilter getFiltro() {
		return filtro;
	}

	public void setFiltro(CondicaoFilter filtro) {
		this.filtro = filtro;
	}

	public List<CentroDeCusto> getListaCusto() {
		return listaCusto;
	}

	public String mostraCaixa() {
		return "/financeiro/CadastroCaixa";
	}

	public Caixa getCaixa() {
		return caixa;
	}

	public boolean isTemCaixaIniciado() {
		return caixa.getTipoDC() != null;
	}

	public boolean isTemCaixaPagar() {
		return caixa.getTipoDC() != null && caixa.getTipoDC() == TipoTituloOrigem.PAGAR;
	}

	public boolean isTemCaixaReceber() {
		return caixa.getTipoDC() != null && caixa.getTipoDC() == TipoTituloOrigem.RECEBER;
	}

	public boolean isExisteFormaDinheiro() {
		return formaPgtos.regraDinheiro() != null;
	}

	public boolean isExisteFormaCheque() {
		return caixa != null
				&& ((caixa.isTemCaixaPagar() && !contas.listaContaCheque().isEmpty()) || caixa.isTemCaixaReceber());
	}

	public boolean isExisteFormaDebito() {
		return caixa != null
				&& ((caixa.isTemCaixaPagar() && !contas.listaContaDebito().isEmpty()) || caixa.isTemCaixaReceber());
	}

	public List<Cartao> getListaCartao() {
		return listaCartao;
	}

	public List<Integer> getListaParcelas() {
		return listaParcelas;
	}

	public Long getCodigoPesquisa() {
		return codigoPesquisa;
	}

	public void setCodigoPesquisa(Long codigoPesquisa) {
		this.codigoPesquisa = codigoPesquisa;
	}

	public Date getDataParaEncerramento() {
		return dataParaEncerramento;
	}

	public void setDataParaEncerramento(Date dataParaEncerramento) {
		this.dataParaEncerramento = dataParaEncerramento;
	}

	public String getTituloCaixa() {
		String texto = "CAIXA - ";
		if (caixa.isTemCaixaIniciado()) {
			texto += caixa.getTipoDC().getTitulo();
			if (caixa.getId() != null)
				texto += " [" + String.format("%06d", caixa.getId()) + "]";
		} else
			texto += "ABERTO";
		return texto;
	}

	public List<ContaCorrente> getListaContaDebito() {
		return listaContaDebito;
	}

	public ContaCorrente getContaBancaria() {
		return contaBancaria;
	}

	public void setContaBancaria(ContaCorrente contaBancaria) {
		this.contaBancaria = contaBancaria;
	}

	public void gravarComponente() {
		caixa.calcular();
	}

	public void removeComponente(int linha) {
		caixa.getTitulos().remove(linha);
		caixa.calcular();
	}

	public void removeCompensacao(int linha) {
		caixa.getValores().remove(linha);
		caixa.calcular();
	}

	public boolean existeTituloNoCaixa(Titulo titulo) {
		boolean tem = false;
		for (CaixaTitulo cxt : caixa.getTitulos()) {
			tem = cxt.getTitulo().equals(titulo);
			if (tem)
				break;
		}
		return tem;
	}

	public void imprimirRecibo() throws JRException, SQLException {
		imprimir(false);
	}

	public void imprimirDetalhado() throws JRException, SQLException {
		imprimir(true);
	}

	public void pesquisaPorCodigo() {
		try {
			Caixa cx = caixas.porId(codigoPesquisa);
			caixa = cx;
		} catch (Exception e) {
			FacesUtil.addRequestErrorMessage(e.getMessage());
		}
	}

	public void limparFiltro() {
		filtro = new CondicaoFilter();
	}

	public List<ContaCorrente> getListaContaBancaria() {
		return listaContaBancaria;
	}

	public void setListaContaBancaria(List<ContaCorrente> listaContaBancaria) {
		this.listaContaBancaria = listaContaBancaria;
	}

	public void GerarArquivoPdf() {
		try {

			String assunto = "Recibo de Pagamento";
			String path = seguranca.pathClass("relatorios");
			String relatorio = path + "recibo.jasper";
			String arquivo = path + "recibo_" + String.format("%06d", caixa.getId()) + ".pdf";

			Map<String, Object> parametros = new HashMap<>();
			parametros.put("REPORT_LOCALE", new Locale("pt", "BR"));
			parametros.put("empresa", seguranca.getPessoaLogadoOrigem());
			parametros.put("empresa_endereco", seguranca.enderecoEmpresa());
			parametros.put("logo", logoImagem);

			List<Caixa> lista = new ArrayList<>();
			caixa = caixas.porId(caixa.getId());
			caixa.setValorPorExtenso(Suporte.valorPorExtenso(caixa.getValorLiquido()));
			lista.add(caixa);

			JRBeanCollectionDataSource jrds = new JRBeanCollectionDataSource(lista);
			JasperPrint printer = JasperFillManager.fillReport(relatorio, parametros, jrds);
			Suporte.ExportarArquivo(printer, arquivo);

			VelocityContext context = new VelocityContext();
			context.put("pessoa", caixa.getPessoa().getNome());
			context.put("empresa", seguranca.getPessoaLogadoOrigem().getNome().toUpperCase());
			context.put("telefone", seguranca.getPessoaLogadoOrigem().getFone());

			email = new Email();
			email.setAnexo(arquivo);
			email.setAssunto(assunto);
			// email.setConteudo(seguranca.conteudoTemplate("anexo.template", context));
			email.setDestinatarioMail(caixa.getPessoa().getEmail());
			email.setDestinatarioNome(caixa.getPessoa().getNome());
			email.setRemetenteNome(seguranca.getPessoaLogado().getNome());

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public boolean isPodeCompensarDeposito() {
		return caixa.isTemCaixaIniciado() && caixa.isPodeGravar() && caixa.isTemCaixaPagar();
	}

	public void setCaixa(Caixa caixa) {
		if (caixa != null) {
			caixa = caixas.porId(caixa.getId());
			if (caixa.isTemCaixaPagar()) {
				listaCusto = centros.lista(TipoEntradaSaida.SAIDA);
			} else if (caixa.isTemCaixaReceber()) {
				listaCusto = centros.lista(TipoEntradaSaida.ENTRADA);
			}
		}
		this.caixa = caixa;
	}

	public void excluirCaixaAtual() {
		try {
			TipoTituloOrigem tipo = caixa.getTipoDC();
			caixas.Excluir(caixa);
			FacesUtil.addInfoMessage("Sucesso na exclusão");

			if (tipo == TipoTituloOrigem.PAGAR)
				iniciaCaixaPagamento();
			else if (tipo == TipoTituloOrigem.RECEBER)
				iniciaCaixaRecebimento();

		} catch (PersistenceException e) {
			FacesUtil.addErrorMessage("Este registro não pode ser excluído");
		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void imprimir_titulo() throws JRException, SQLException {
		try {

			Suporte.validaPeriodo(filtro.getEmissaoInicial(), filtro.getEmissaoFinal());

			filtro.setSortField("d.agente, d.dataMov, c.emissao, t.id");
			filtro.setPrimeiroRegistro(null);
			List<CaixaTitulo> lst = caixas.lista_titulo(filtro);

			if (lst.size() == 0)
				throw new Exception("Sem dados para imprimir.");

			HashMap<String, Object> parametros = new HashMap<String, Object>();
			parametros.put("empresa", seguranca.getPessoaLogadoOrigem());
			parametros.put("empresa_endereco", seguranca.enderecoEmpresa());

			String path = seguranca.pathClass("relatorio");
			String arquivo = path + "caixa_titulo_lista.jasper";

			JRBeanCollectionDataSource jrds = new JRBeanCollectionDataSource(lst);
			JasperPrint printer = JasperFillManager.fillReport(arquivo, parametros, jrds);
			Suporte.ImprimirArquivo(response, printer, null);

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		} finally {
			filtro.setSortField(null);
		}
	}

	public void addTitulo(Titulo titulo) {
		if (!existeTituloNoCaixa(titulo)) {
			int dias = SuporteData.diasEntreDatas(titulo.getVencimento(), new Date());
			CaixaTitulo item = new CaixaTitulo();
			item.setCaixa(caixa);
			item.setTitulo(titulo);
			if (titulo.getAgente() != null) {
				item.setJuro(titulo.getAgente().getJuro());
				item.setMulta(titulo.getAgente().getMulta());
				item.setDesconto(titulo.getAgente().getDesconto());
			}
			item.setValor(titulo.getSaldo());
			item.setDia(dias > 0 ? dias : 0);
			caixa.getTitulos().add(item);

			if (caixa.getCentroDeCusto() == null)
				caixa.setCentroDeCusto(titulo.getCentroDeCusto());

			if (caixa.getObservacao() == null || caixa.getObservacao().isEmpty())
				caixa.setObservacao(titulo.getDescricao());

			if (caixa.getPessoa() == null || caixa.getPessoa().getId() == null) {
				caixa.setPessoa(titulo.getResponsavel());
				caixa.setNominal(titulo.getNominal());
			}

		}
	}

	public void pesquisar() {
		listaCaixa = new LazyDataModel<Caixa>() {

			private static final long serialVersionUID = 1L;

			@Override
			public List<Caixa> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {

				filtro.setPrimeiroRegistro(Long.valueOf(first));
				filtro.setQuantidadeRegistros(Long.valueOf(pageSize));
				filtro.setSortField(sortField);
				filtro.setAscendente(SortOrder.ASCENDING.equals(sortOrder));

				setRowCount(caixas.quantidadeFiltrados(filtro));
				return caixas.lista(filtro);
			}

		};
	}

	public int getActiveIndex() {
		return activeIndex;
	}

	public void setActiveIndex(int activeIndex) {
		this.activeIndex = activeIndex;
	}

	public void estornar() {
		try {

			if (seguranca.getPessoaLogado().getAgente() == null
					|| seguranca.getPessoaLogado().getAgente().getId() == null)
				throw new Exception("O usuário logado deve ser um agente financeiro!");

			diario = caixaDiarios.ultimo();
			if (diario == null)
				throw new Exception("Sem diário financeiro para operar!");
			if (diario.getFechamento() != null)
				throw new Exception("Sem diário aberto para operar!");

			caixa = caixas.estornar(caixa, diario);

			FacesUtil.addInfoMessage("Estorno realizado com sucesso.");

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void onBancoChosen(SelectEvent event) {
		Banco banco = (Banco) event.getObject();
		banco = bancos.porCodigo(banco.getCodigo());
		caixaValor.getTitulo().setBanco(banco);
	}

	public void onContaChosen(SelectEvent event) {
		ContaCorrente conta = (ContaCorrente) event.getObject();
		caixaValor.getTitulo().setContaBancaria(conta);
		caixa.setPessoa(conta.getOrigem());
	}

	public String getDataDiario() {
		String valor = "";
		try {
			if (diario != null)
				return SuporteData.formataDataToStr(diario.getDataMov(), "dd/MM/yyyy");
		} catch (Exception e) {
		}
		return valor;
	}

	public boolean isExisteFormaCartao() {
		return existeFormaCartao;
	}

	public void gravarCompensacao() {

		try {

			if (caixaValor.getValor() <= 0)
				throw new Exception("Valor incorreto");

			if (caixaValor.getCaixa() == null) {
				caixaValor.setCaixa(caixa);
				caixa.getValores().add(caixaValor);
				if (caixaValor.getTitulo() != null) {
					if (caixa.getObservacao() == null) {
						caixa.setObservacao(caixaValor.getTitulo().getDescricao());
						caixa.setPessoa(seguranca.getPessoaLogadoOrigem());
						caixa.setNominal(caixa.getPessoa().getNome());
					}
				}
			}

			caixa.calcular();
			novoCaixaValor();

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void imprimir_movimentacao() throws JRException, SQLException {
		try {

			Suporte.validaPeriodo(filtro.getEmissaoInicial(), filtro.getEmissaoFinal());

			filtro.setSortField("d.agente, d.dataMov, c.emissao, v.id");
			filtro.setPrimeiroRegistro(null);
			List<CaixaValor> lst = caixas.lista_movimentacao(filtro);

			List<Movimento> lstResumo = caixas.lista_movimentacao_tipoPagamento(filtro);

			if (lst.size() == 0)
				throw new Exception("Sem dados para imprimir.");

			String path = seguranca.pathClass("relatorio");
			String arquivo = path + "caixa_movimentacao_lista.jasper";

			HashMap<String, Object> parametros = new HashMap<String, Object>();
			parametros.put("SUBREPORT_DIR", path);
			parametros.put("empresa", seguranca.getPessoaLogadoOrigem());
			parametros.put("empresa_endereco", seguranca.enderecoEmpresa());
			parametros.put("resumo", new JRBeanCollectionDataSource(lstResumo));

			JRBeanCollectionDataSource jrds = new JRBeanCollectionDataSource(lst);
			JasperPrint printer = JasperFillManager.fillReport(arquivo, parametros, jrds);
			Suporte.ImprimirArquivo(response, printer, null);

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		} finally {
			filtro.setSql(null);
			filtro.setSortField(null);
		}
	}

	public double getValorSaldoEmCaixa() {
		return valorSaldoEmCaixa;
	}

	public void onPessoaChosen(SelectEvent event) {
		Pessoa pessoa = (Pessoa) event.getObject();
		caixa.setPessoa(pessoa);
		caixa.setNominal(pessoa.getNome());
		valorSaldoEmCaixa = pessoa.getValorCredito();
	}

	public boolean isExisteSaldoEmCaixa() {
		return valorSaldoEmCaixa > 0;
	}

	public void iniciarCaixaItem(String pgto) {
		try {

			novoCaixaValor();

			if ("DINHEIRO".equals(pgto)) {
				caixaValor.setTipoPagamento(TipoPagamento.DINHEIRO);
				caixaValor.setTitulo(null);

			} else if ("SAQUE".equals(pgto)) {
				caixaValor.setTipoPagamento(TipoPagamento.SAQUE);
				caixaValor.getTitulo().setTipoDC(TipoTituloOrigem.PAGAR);
				caixaValor.getTitulo().setTipoDocto(TipoDeTitulo.OUTROS);
				caixaValor.getTitulo().setDescricao("saque na conta ");

			} else if ("DEBITO".equals(pgto)) {
				caixaValor.setTipoPagamento(TipoPagamento.DEBITO);
				caixaValor.getTitulo().setTipoDocto(TipoDeTitulo.ND_NOTA_DE_DEBITO);
				caixaValor.getTitulo().setTipoDC(TipoTituloOrigem.PAGAR);
				caixaValor.getTitulo().setDescricao("saque na conta ");

			} else if ("TRANSFERENCIA".equals(pgto)) {
				caixaValor.setTipoPagamento(TipoPagamento.DEPOSITO);
				caixaValor.getTitulo().setTipoDC(TipoTituloOrigem.PAGAR);
				caixaValor.getTitulo().setDescricao("depósito na conta ");

			} else if ("CHEQUE".equals(pgto)) {
				caixaValor.setTipoPagamento(TipoPagamento.CHEQUE);
				caixaValor.getTitulo().setContaBancaria(null);
				caixaValor.getTitulo().setCartao(null);
				caixaValor.getTitulo().setNominal(caixa.getNominal());
				caixaValor.getTitulo().setTipoDocto(TipoDeTitulo.CH_CHEQUE);
				caixaValor.getTitulo().setTipoDC(caixa.getTipoDC());
				caixaValor.getTitulo().setCentroDeCusto(caixa.getCentroDeCusto());
				caixaValor.getTitulo().setResponsavel(caixa.getPessoa());

			} else if ("CREDITO".equals(pgto)) {
				caixaValor.setTipoPagamento(TipoPagamento.CREDITO);
				caixaValor.setTitulo(null);
				listaCartao = cartoes.lista();

			} else if ("SALDO".equals(pgto)) {
				caixaTitulo = new CaixaTitulo();
				caixaValor.getTitulo().setTipoDC(TipoTituloOrigem.COMPENSA);
				caixaValor.getTitulo().setContaBancaria(null);
				caixaValor.getTitulo().setCartao(null);
				caixaValor.setTipoPagamento(TipoPagamento.CAIXA);
				caixaValor.setValor(valorSaldoEmCaixa);
			}

			if (caixa.getValorFinal() < 0)
				caixaValor.setValor(caixa.getValorFinal() * -1);

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}

	}

	public void setCaixaValor(CaixaValor caixaValor) {
		this.caixaValor = caixaValor;
		if (caixaValor != null && caixaValor.getRegraPgto() != null) {
			formaPgto = caixaValor.getRegraPgto().getFormaPgto();
		}
	}

	public void iniciaCaixaRecebimento() {
		valorSaldoEmCaixa = 0;
		caixa = new Caixa();
		caixa.setTipoDC(TipoTituloOrigem.RECEBER);
		listaCusto = centros.lista(TipoEntradaSaida.ENTRADA);
	}

	public void iniciaCaixaPagamento() {
		valorSaldoEmCaixa = 0;
		caixa = new Caixa();
		caixa.setDiario(null);
		caixa.setTipoDC(TipoTituloOrigem.PAGAR);
		listaCusto = centros.lista(TipoEntradaSaida.SAIDA);
	}

	public void gravarCaixa() {
		try {

			if (!caixa.isPodeGravar())
				throw new NegocioException("Este registro não pode ser gravado.");

			if (caixa.getValores().isEmpty())
				throw new NegocioException("Sem valores para compensação.");

			if (caixa.getPessoa() == null || caixa.getPessoa().getId() == null)
				throw new NegocioException("Defina um participante.");

			for (CaixaValor item : caixa.getValores())
				item.setCaixa(caixa);

			for (CaixaTitulo item : caixa.getTitulos())
				item.setCaixa(caixa);

			caixa.setEmissao(new Date());
			caixa = caixas.guardar(caixa);

			FacesUtil.addInfoMessage("Gravado com sucesso");

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void onTituloBorderoChosen(SelectEvent event) {
		Titulo[] selecionados = (Titulo[]) event.getObject();
		for (Titulo titulo : selecionados) {
			addTitulo(titulo);
		}
		caixa.setValor(caixa.getValorLiquido());
		caixa.calcular();
	}

	public void imprimir(boolean detalhado) throws JRException, SQLException {
		try {

			String path = seguranca.pathClass("relatorio");
			String arquivo = path + "recibo.jasper";

			Caixa cxa = caixas.porId(caixa.getId());
			Map<String, Object> parametros = new HashMap<>();
			parametros.put("REPORT_LOCALE", new Locale("pt", "BR"));
			parametros.put("empresa", seguranca.getPessoaLogadoOrigem());
			parametros.put("empresa_endereco", seguranca.enderecoEmpresa());
			parametros.put("logo", logoImagem);
			if (detalhado) {
				parametros.put("SUBREPORT_DIR", path);
				parametros.put("detalhado", true);
				parametros.put("pagamentos", new JRBeanCollectionDataSource(cxa.getValores()));
				parametros.put("titulos", new JRBeanCollectionDataSource(cxa.getTitulos()));
			}

			List<Caixa> lista = new ArrayList<>();
			cxa.setValorPorExtenso(Suporte.valorPorExtenso(cxa.getValorLiquido()));
			lista.add(cxa);

			JRBeanCollectionDataSource jrds = new JRBeanCollectionDataSource(lista);
			JasperPrint printer = JasperFillManager.fillReport(arquivo, parametros, jrds);
			Suporte.ImprimirArquivo(response, printer, null);

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void onTabChange(TabChangeEvent event) {
		TabView tabView = (TabView) event.getComponent();
		activeIndex = tabView.getChildren().indexOf(event.getTab());
	}

	public void encerrar() {
		try {

			diario = caixaDiarios.ultimo();

			if (diario == null)
				throw new NegocioException("Diário financeiro indefinido.");

			Date hoje = new Date();
			if (diario.getDataMov().before(hoje)) {
				int diferencaDias = SuporteData.diasEntreDatas(hoje, diario.getDataMov());
				if (diferencaDias > Suporte.TEMPO_PARA_ENCERRAMENTO_CAIXA) {
					throw new NegocioException("Encerrar o movimento do dia "
							+ new SimpleDateFormat("dd/MM/yyyy").format(diario.getDataMov()));
				} else if (diferencaDias == Suporte.TEMPO_PARA_ENCERRAMENTO_CAIXA) {
					int hora = Calendar.HOUR_OF_DAY;
					if (hora > 6) {
						throw new NegocioException("Encerrar o movimento do dia "
								+ new SimpleDateFormat("dd/MM/yyyy").format(diario.getDataMov()));
					}
				}
			}

			encerrar(caixaDiarios.ultimo());

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void encerrarNaData() {
		try {
			CaixaDiario ultimo = caixaDiarios.porData(dataParaEncerramento);
			encerrar(ultimo);
		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void encerrar(CaixaDiario ultimo) {
		try {

			if (caixa.getObservacao().isEmpty())
				throw new NegocioException("Preencha o histório do caixa.");

			if (caixa.getValorLiquido() < caixa.getValor())
				throw new NegocioException("Valor insuficiente para compensação");

			if (ultimo == null)
				throw new NegocioException("Diário financeiro não foi encontrado!");

			if (ultimo.getFechamento() != null)
				throw new NegocioException("Caixa diário está encerrado!");

			caixa.setDiario(ultimo);
			caixa.setStatus(TipoCaixaStatus.REALIZADO);
			caixa = caixas.encerrar(caixa);

			for (CaixaTitulo item : caixa.getTitulos())
				if (item.getTitulo().getSaldo() <= 0 && item.getTitulo().getRepete() != TipoRepeticao.NAO) {
					titulos.gerarProximoTitulo(item.getTitulo());
				}

			FacesUtil.addInfoMessage(
					"Encerrado no diário: " + SuporteData.formataDataToStr(ultimo.getDataMov(), "dd/MM/yyyy"));

		} catch (Exception e) {
			caixa = caixas.porId(caixa.getId());
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void reposicionar() {
		try {

			caixaDiarios.reposicionarCaixaParaDiario(caixa, dataParaEncerramento);
			caixa = caixas.porId(caixa.getId());

			FacesUtil.addInfoMessage("caixa reposicionado com sucesso");

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

}