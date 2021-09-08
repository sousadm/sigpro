package com.sousa.sigpro.controller;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.PersistenceException;
import javax.servlet.http.HttpServletResponse;

import org.jrimum.bopepo.BancosSuportados;
import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;

import com.sousa.sigpro.model.Banco;
import com.sousa.sigpro.model.CaixaDiario;
import com.sousa.sigpro.model.CaixaValor;
import com.sousa.sigpro.model.ContaCorrente;
import com.sousa.sigpro.model.Pessoa;
import com.sousa.sigpro.model.Titulo;
import com.sousa.sigpro.model.evento.EventoContaCorrente;
import com.sousa.sigpro.model.tipo.TipoBoleto;
import com.sousa.sigpro.model.tipo.TipoRotina;
import com.sousa.sigpro.repository.CaixaDiarios;
import com.sousa.sigpro.repository.Contas;
import com.sousa.sigpro.repository.Eventos;
import com.sousa.sigpro.repository.Financeiros;
import com.sousa.sigpro.repository.Titulos;
import com.sousa.sigpro.repository.filter.CondicaoFilter;
import com.sousa.sigpro.security.Seguranca;
import com.sousa.sigpro.service.NegocioException;
import com.sousa.sigpro.util.Suporte;
import com.sousa.sigpro.util.SuporteBoleto;
import com.sousa.sigpro.util.SuporteData;
import com.sousa.sigpro.util.jsf.FacesUtil;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Named
@ViewScoped
public class ContaBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private HttpServletResponse response;
	@Inject
	private Seguranca seguranca;
	@Inject
	private Eventos eventos;
	@Inject
	private Contas contas;
	@Inject
	private CaixaDiarios diarios;
	@Inject
	private Titulos titulos;
	@Inject
	private Financeiros financeiros;

	private List<ContaCorrente> lista;
	private ContaCorrente conta;
	private Titulo titulo;
	private List<Titulo> listaTitulo;
	private double novoSaldo = 0;
	private int tabIndex = 0;
	private boolean conciliacao;
	private boolean permiteConciliacaoBancaria;
	private boolean emitidoNoCaixa;

	private double saldoInicial;
	private double saldoFinal;
	private CondicaoFilter filtro;

	public void inicializar() {
		if (FacesUtil.isNotPostback()) {
			permiteConciliacaoBancaria = seguranca.isUsuarioTemRotina(TipoRotina.CONCILIACAO);
			filtro = new CondicaoFilter();
			filtro.setInicio(SuporteData.primeiraDataDoMes(new Date()));
			filtro.setTermino(SuporteData.ultimaDataDoMes(new Date()));
			pesquisar();
		}
	}

	public boolean isPermiteConciliacaoBancaria() {
		return permiteConciliacaoBancaria;
	}

	public double getSaldoFinal() {
		return saldoFinal;
	}

	public double getSaldoInicial() {
		return saldoInicial;
	}

	public CondicaoFilter getFiltro() {
		return filtro;
	}

	public void setFiltro(CondicaoFilter filtro) {
		this.filtro = filtro;
	}

	public List<Titulo> getListaTitulo() {
		return listaTitulo;
	}

	public void limpar() {
		this.conta = new ContaCorrente();
	}

	public void pesquisar() {
		lista = contas.lista();
	}

	public void novo() {
		conta = new ContaCorrente();
	}

	public List<ContaCorrente> getLista() {
		return lista;
	}

	public ContaCorrente getConta() {
		return conta;
	}

	public void setConta(ContaCorrente conta) {
		this.conta = conta;
		if (conta != null)
			pesquisarConciliacao();
	}

	public void selecionar(ContaCorrente conta) {
		PrimeFaces.current().dialog().closeDynamic(conta);
	}

	public double getNovoSaldo() {
		return novoSaldo;
	}

	public void setNovoSaldo(double novoSaldo) {
		this.novoSaldo = novoSaldo;
	}

	public void onBancoChosen(SelectEvent event) {
		this.conta.setBanco((Banco) event.getObject());
	}

	public int getTabIndex() {
		return tabIndex;
	}

	public void setTabIndex(int tabIndex) {
		this.tabIndex = tabIndex;
	}

	public void defineTab() {
		if (conta.getTipoBoleto() == null)
			tabIndex = 0;
	}

	public Titulo getTitulo() {
		return titulo;
	}

	public void ajustarSaldoConta() {
		try {
			conta = contas.ajustarSaldoNaData(conta, titulo);
			titulo = null;
			FacesUtil.addInfoMessage("realizado com sucesso");
		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public boolean isConciliacao() {
		return conciliacao;
	}

	public void onPessoaChosen(SelectEvent event) {
		Pessoa pessoa = (Pessoa) event.getObject();
		titulo.setResponsavel(pessoa);
		titulo.setNominal(pessoa.getNome());
	}

	public void setConciliacao(boolean conciliacao) {
		this.conciliacao = conciliacao;
	}

	public void pesquisarConciliacao() {
		try {
			if (filtro.getInicio() == null)
				throw new NegocioException("Informe data inicial");
			if (filtro.getTermino() == null)
				throw new NegocioException("Informe data final");
			listaTitulo = contas.conciliacao(conta, filtro.getInicio(), filtro.getTermino());
			saldoInicial = contas.saldoInicialEmConta(conta, filtro.getInicio());
			saldoFinal = saldoInicial + contas.saldoMovimentoEmConta(conta, filtro.getInicio(), filtro.getTermino());
		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void novoEvento() {
		emitidoNoCaixa = false;
		titulo = new Titulo();
	}

	public void imprimir() throws JRException, SQLException {
		try {

			lista = contas.lista();

			if (lista.size() == 0)
				throw new Exception("Sem dados para imprimir.");

			HashMap<String, Object> parametros = new HashMap<String, Object>();
			parametros.put("empresa", seguranca.getPessoaLogadoOrigem());
			parametros.put("empresa_endereco", seguranca.enderecoEmpresa());

			String arquivo = seguranca.pathClass("relatorio") + "contacorrente_lista.jasper";

			JRBeanCollectionDataSource jrds = new JRBeanCollectionDataSource(lista);
			JasperPrint printer = JasperFillManager.fillReport(arquivo, parametros, jrds);
			Suporte.ImprimirArquivo(response, printer, null);

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void imprimirExtrato() throws JRException, SQLException {
		try {

			listaTitulo = contas.conciliacao(conta, filtro.getInicio(), filtro.getTermino());
			saldoInicial = contas.saldoInicialEmConta(conta, filtro.getInicio());
			saldoFinal = saldoInicial + contas.saldoMovimentoEmConta(conta, filtro.getInicio(), filtro.getTermino());

			Map<String, Object> parametros = new HashMap<>();
			parametros.put("empresa", seguranca.getPessoaLogadoOrigem());
			parametros.put("empresa_endereco", seguranca.enderecoEmpresa());
			parametros.put("saldoInicial", saldoInicial);
			parametros.put("saldoFinal", saldoFinal);
			parametros.put("valorTotal", saldoFinal - saldoInicial);

			String arquivo = seguranca.pathClass("relatorio") + "extrato.jasper";
			JRBeanCollectionDataSource jrds = new JRBeanCollectionDataSource(listaTitulo);
			JasperPrint printer = JasperFillManager.fillReport(arquivo, parametros, jrds);
			Suporte.ImprimirArquivo(response, printer, null);

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void excluir(int item) {
		try {
			conta = lista.get(item);
			contas.remover(conta);
			lista.remove(item);
			conta = null;
			FacesUtil.addInfoMessage("Excluído com sucesso");
		} catch (PersistenceException e) {
			FacesUtil.addErrorMessage("Este registro não pode ser excluído");
		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void cancelar() {
		try {

			conta = contas.cancelar(conta);
			FacesUtil.addInfoMessage("cancelado com sucesso");

			eventos.guardar(new EventoContaCorrente(seguranca.getPessoaLogadoOrigem().getUsuario(), conta,
					"cancelamento de conta " + conta.getDescricao()));

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void salvar() {
		try {

			if (conta.getTipoBoleto() != null && conta.getTipoBoleto() == TipoBoleto.BOPEPO) {
				BancosSuportados suportado = SuporteBoleto.getBopepoBancoSuportado(conta.getBanco().getCodigo());
				if (suportado == null)
					throw new NegocioException("banco não suportado para esta modalidade de boleto");
			}

			conta = contas.guardar(conta);
			FacesUtil.addInfoMessage("Gravado com sucesso");

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public boolean isEmitidoNoCaixa() {
		return emitidoNoCaixa;
	}

	private void analisaOrigemCheque() {
		CaixaValor origem = financeiros.chequePorCaixaValor(titulo);
		emitidoNoCaixa = origem != null;
	}

	public void setTitulo(Titulo titulo) {
		this.titulo = titulo;
		analisaOrigemCheque();
	}

	public void excluirEvento(int linha) {
		try {
			Titulo titulo = titulos.porId(listaTitulo.get(linha).getId());
			if (titulos.existeNoCaixa(titulo))
				throw new NegocioException("Este registro não pode ser removido");

			CaixaDiario diario = diarios.porData(titulo.getDataBaixa());
			if (diario != null && diario.getFechamento() != null)
				throw new Exception("diário financeiro na data se encontra encerrado");

			diario = diarios.fechadoPosteriorData(titulo.getDataBaixa());
			if (diario != null && diario.getFechamento() != null)
				throw new Exception("diário financeiro posterior encerrado na data "
						+ SuporteData.formataDataToStr(titulo.getDataBaixa(), "dd/MM/yyyy"));

			titulos.remover(titulo);
			conta = contas.guardar(conta);
			listaTitulo.remove(linha);

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void preparaAjusteSaldo() {
		titulo = new Titulo();
		titulo.setDataBaixa(new Date());
		titulo.setResponsavel(seguranca.getPessoaLogado());
		titulo.setNominal(titulo.getResponsavel().getNome());
		titulo.setContaBancaria(conta);
		titulo.setDescricao("ajuste de saldo");
	}

	public void gravarEvento() {
		try {

			CaixaDiario diario = null;

			diario = diarios.porData(titulo.getDataBaixa());
			if (diario == null)
				throw new Exception("diário financeiro não localizado");
			else if (diario.getFechamento() != null)
				throw new Exception("diário financeiro na data se encontra encerrado");

			diario = diarios.fechadoPosteriorData(titulo.getDataBaixa());
			if (diario != null && diario.getFechamento() != null)
				throw new Exception("diário financeiro posterior encerrado na data "
						+ SuporteData.formataDataToStr(diario.getDataMov(), "dd/MM/yyyy"));

			Titulo tituloInicial = null;
			if (titulo.isExiste()) {

				CaixaValor caixaValor = financeiros.chequePorCaixaValor(titulo);
				if (caixaValor != null)
					throw new Exception("cheque emitido pelo caixa não pode ser modificado!");

				if (titulos.existeNoCaixa(titulo)) {

					tituloInicial = titulos.porId(titulo.getId());
					CaixaDiario origem = diarios.porData(tituloInicial.getDataBaixa());
					if (origem != null && origem.getFechamento() != null)
						throw new Exception("Diário financeiro fechado na data de origem!");

					CaixaDiario destino = diarios.porData(titulo.getDataBaixa());
					if (destino != null && destino.getFechamento() != null)
						throw new Exception("Diário financeiro fechado na data destino!");
				}
			}

			if (titulo.getContaBancaria() == null) {
				titulo.setContaBancaria(conta);
				titulo.setBanco(conta.getBanco());
				titulo.setAgencia(conta.getAgenciaToStr());
				titulo.setConta(conta.getNumeroToStr());
			}

			titulo.setVencimento(titulo.getDataBaixa());
			titulo.setPrevisao(titulo.getDataBaixa());

			if (tituloInicial != null)
				titulo.setValorInicial(tituloInicial.getValor());

			titulo = titulos.guardar(titulo);

			// atualizar saldo na conta destino
			conta = contas.guardar(conta);

			// atualizar saldo na conta origem
			if (tituloInicial != null && !tituloInicial.getContaBancaria().equals(conta)) {
				ContaCorrente contaOrigem = contas.porId(tituloInicial.getContaBancaria().getId());
				contaOrigem = contas.guardar(contaOrigem);
			}

			pesquisarConciliacao();

			FacesUtil.addInfoMessage("gravado com sucesso");

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

}