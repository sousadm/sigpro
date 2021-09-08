package com.sousa.sigpro.controller;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;

import com.sousa.sigpro.model.CaixaTitulo;
import com.sousa.sigpro.model.CaixaValor;
import com.sousa.sigpro.model.Titulo;
import com.sousa.sigpro.model.tipo.TipoSituacaoFinanceira;
import com.sousa.sigpro.model.tipo.TipoTituloOrigem;
import com.sousa.sigpro.repository.Financeiros;
import com.sousa.sigpro.repository.Titulos;
import com.sousa.sigpro.repository.filter.CondicaoFilter;
import com.sousa.sigpro.security.Seguranca;
import com.sousa.sigpro.service.NegocioException;
import com.sousa.sigpro.util.Suporte;
import com.sousa.sigpro.util.jsf.FacesUtil;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Named
@ViewScoped
public class RelatorioFinanceiroBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private HttpServletResponse response;
	@Inject
	private Seguranca seguranca;
	@Inject
	private Financeiros financeiros;
	@Inject
	private Titulos titulos;

	private CondicaoFilter filtro;
	private List<SelectItem> relatorios;

	@PostConstruct
	public void init() {

		limpar();

		SelectItemGroup g1 = new SelectItemGroup("Realizado");
		g1.setSelectItems(new SelectItem[] { new SelectItem("1", "Movimentação Financeira"),
				new SelectItem("2", "Contas pagas"), new SelectItem("7", "Pagamentos por centro de custo"),
				new SelectItem("3", "Contas recebidas"), new SelectItem("8", "Recebimentos por centro de custo") });

		SelectItemGroup g2 = new SelectItemGroup("A realizar");
		g2.setSelectItems(
				new SelectItem[] { new SelectItem("4", "Contas a pagar"), new SelectItem("5", "Contas a receber") });

		SelectItemGroup g3 = new SelectItemGroup("Outros");
		g3.setSelectItems(new SelectItem[] { new SelectItem("6", "Lista de títulos") });

		relatorios = new ArrayList<SelectItem>();
		relatorios.add(g1);
		relatorios.add(g2);
		relatorios.add(g3);
	}

	public CondicaoFilter getFiltro() {
		return filtro;
	}

	public void setFiltro(CondicaoFilter filtro) {
		this.filtro = filtro;
	}

	public void limpar() {
		filtro = new CondicaoFilter();
		filtro.setOpcao(1);
	}

	public void imprimirContasARealizar(TipoTituloOrigem tipoDC) {
		try {

			filtro.setTituloStatus(TipoSituacaoFinanceira.ABERTO);
			TipoTituloOrigem[] tipo = new TipoTituloOrigem[1];
			tipo[0] = tipoDC;
			filtro.setTipoDC(tipo);
			List<Titulo> lst = titulos.lista(filtro);

			if (lst == null || lst.size() == 0)
				throw new Exception("Sem dados para imprimir.");

			String path = seguranca.pathClass("relatorio");
			String arquivo = path + "conta_realizar.jasper";

			Map<String, Object> parametros = new HashMap<>();
			parametros.put("empresa", seguranca.getPessoaLogadoOrigem());
			parametros.put("empresa_endereco", seguranca.enderecoEmpresa());
			parametros.put("titulo", tipoDC.getDescricao().toUpperCase());

			JRBeanCollectionDataSource jrds = new JRBeanCollectionDataSource(lst);
			JasperPrint printer = JasperFillManager.fillReport(arquivo, parametros, jrds);
			Suporte.ImprimirArquivo(response, printer, null);

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public List<SelectItem> getRelatorios() {
		return relatorios;
	}

	public void setRelatorios(List<SelectItem> relatorios) {
		this.relatorios = relatorios;
	}

	public void imprimirListaDeTitulos() throws JRException, SQLException {
		try {

			List<Titulo> lst = titulos.lista(filtro);
			if (lst.size() == 0)
				throw new Exception("Sem dados para imprimir.");

			Map<String, Object> parametros = new HashMap<>();
			parametros.put("empresa", seguranca.getPessoaLogadoOrigem());
			parametros.put("empresa_endereco", seguranca.enderecoEmpresa());
			parametros.put("titulo", "LISTA DE TÍTULOS");

			String path = seguranca.pathClass("relatorio");
			String arquivo = path + "titulos.jasper";

			JRBeanCollectionDataSource jrds = new JRBeanCollectionDataSource(lst);
			JasperPrint printer = JasperFillManager.fillReport(arquivo, parametros, jrds);

			if (printer.getPages().size() == 0)
				throw new NegocioException("Dados não encontrados!");

			Suporte.ImprimirArquivo(response, printer, null);

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public boolean isMostraVencimento() {
		return (filtro.getOpcao() == 2) || (filtro.getOpcao() == 3) || (filtro.getOpcao() == 4)
				|| (filtro.getOpcao() == 5) || (filtro.getOpcao() == 6);
	}

	public boolean isMostraPrevisao() {
		return (filtro.getOpcao() == 2) || (filtro.getOpcao() == 3) || (filtro.getOpcao() == 4)
				|| (filtro.getOpcao() == 5) || (filtro.getOpcao() == 6);
	}

	public boolean isMostraPagamento() {
		return (filtro.getOpcao() == 1) || (filtro.getOpcao() == 2) || (filtro.getOpcao() == 3)
				|| (filtro.getOpcao() == 7) || (filtro.getOpcao() == 8);
	}

	public boolean isMostraEmissao() {
		return (filtro.getOpcao() == 6);
	}

	public void imprimirMovimentoPorCentroCusto(TipoTituloOrigem tipoDC) {
		try {

			if (filtro.getPagamentoInicial() == null || filtro.getPagamentoFinal() == null)
				throw new NegocioException("defina um período");

			filtro.setTituloStatus(TipoSituacaoFinanceira.LIQUIDADO);
			TipoTituloOrigem[] tipo = new TipoTituloOrigem[1];
			tipo[0] = tipoDC;
			filtro.setTipoDC(tipo);

			filtro.setOrdem("c.centroDeCusto, c.id, v.id");
			List<CaixaValor> lst = financeiros.listaCaixaDiarioValores(filtro);

			if (lst == null || lst.size() == 0)
				throw new Exception("Sem dados para imprimir.");

			String titulo = filtro.getTituloRelatorio();
			String path = seguranca.pathClass("relatorio");
			String arquivo = path + "movimento_realizado.jasper";

			Map<String, Object> parametros = new HashMap<>();
			parametros.put("empresa", seguranca.getPessoaLogadoOrigem());
			parametros.put("empresa_endereco", seguranca.enderecoEmpresa());
			parametros.put("titulo", tipoDC.getTitulo().toUpperCase() + " / Centro de Custo");
			parametros.put("subtitulo", titulo);
			JRBeanCollectionDataSource jrds = new JRBeanCollectionDataSource(lst);
			JasperPrint printer = JasperFillManager.fillReport(arquivo, parametros, jrds);
			Suporte.ImprimirArquivo(response, printer, null);

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void imprimirMovimentacaoFinanceira() {
		try {

			filtro.setOrdem("c.diario.dataMov, v.id");
			List<CaixaValor> lst = financeiros.listaCaixaDiarioValores(filtro);

			if (lst == null || lst.size() == 0)
				throw new Exception("Sem dados para imprimir.");

			String titulo = filtro.getTituloRelatorio();
			String path = seguranca.pathClass("relatorio");
			String arquivo = path + "movimento_financeiro.jasper";

			Map<String, Object> parametros = new HashMap<>();
			parametros.put("empresa", seguranca.getPessoaLogadoOrigem());
			parametros.put("empresa_endereco", seguranca.enderecoEmpresa());
			parametros.put("subtitulo", titulo);

			JRBeanCollectionDataSource jrds = new JRBeanCollectionDataSource(lst);
			JasperPrint printer = JasperFillManager.fillReport(arquivo, parametros, jrds);
			Suporte.ImprimirArquivo(response, printer, null);

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void imprimirContasRealizadas(TipoTituloOrigem tipoDC) {
		try {

			filtro.setTituloStatus(TipoSituacaoFinanceira.LIQUIDADO);
			TipoTituloOrigem[] tipo = new TipoTituloOrigem[1];
			tipo[0] = tipoDC;
			filtro.setTipoDC(tipo);

			List<CaixaTitulo> lst = financeiros.listaCaixaBaixaTitulos(filtro);

			if (lst == null || lst.size() == 0)
				throw new Exception("Sem dados para imprimir.");

			String titulo = filtro.getTituloRelatorio();
			String path = seguranca.pathClass("relatorio");
			String arquivo = path + "conta_realizada.jasper";

			Map<String, Object> parametros = new HashMap<>();
			parametros.put("empresa", seguranca.getPessoaLogadoOrigem());
			parametros.put("empresa_endereco", seguranca.enderecoEmpresa());
			parametros.put("titulo", tipoDC.getRealizado().toUpperCase());
			parametros.put("subtitulo", titulo);

			JRBeanCollectionDataSource jrds = new JRBeanCollectionDataSource(lst);
			JasperPrint printer = JasperFillManager.fillReport(arquivo, parametros, jrds);
			Suporte.ImprimirArquivo(response, printer, null);

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void imprimir() throws JRException, SQLException {
		try {
			if (filtro.getOpcao() == 1)
				imprimirMovimentacaoFinanceira();
			else if (filtro.getOpcao() == 2)
				imprimirContasRealizadas(TipoTituloOrigem.PAGAR);
			else if (filtro.getOpcao() == 3)
				imprimirContasRealizadas(TipoTituloOrigem.RECEBER);
			else if (filtro.getOpcao() == 4)
				imprimirContasARealizar(TipoTituloOrigem.PAGAR);
			else if (filtro.getOpcao() == 5)
				imprimirContasARealizar(TipoTituloOrigem.RECEBER);
			else if (filtro.getOpcao() == 6)
				imprimirListaDeTitulos();
			else if (filtro.getOpcao() == 7)
				imprimirMovimentoPorCentroCusto(TipoTituloOrigem.PAGAR);
			else if (filtro.getOpcao() == 8)
				imprimirMovimentoPorCentroCusto(TipoTituloOrigem.RECEBER);
		} finally {
			filtro.setOrdem(null);
		}
	}

}