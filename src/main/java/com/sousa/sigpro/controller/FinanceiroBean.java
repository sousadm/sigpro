package com.sousa.sigpro.controller;

import java.io.Serializable;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;

import org.primefaces.event.SelectEvent;

import com.sousa.sigpro.model.CentroDeCusto;
import com.sousa.sigpro.model.CurvaFaturamentoProduto;
import com.sousa.sigpro.model.Expedicao;
import com.sousa.sigpro.model.Pessoa;
import com.sousa.sigpro.model.Titulo;
import com.sousa.sigpro.model.Veiculo;
import com.sousa.sigpro.model.tipo.TipoExpedicao;
import com.sousa.sigpro.model.tipo.TipoPagamento;
import com.sousa.sigpro.model.tipo.TipoRepeticao;
import com.sousa.sigpro.model.tipo.TipoTituloOrigem;
import com.sousa.sigpro.repository.Expedicoes;
import com.sousa.sigpro.repository.Pessoas;
import com.sousa.sigpro.repository.filter.CondicaoFilter;
import com.sousa.sigpro.security.Seguranca;
import com.sousa.sigpro.util.Suporte;
import com.sousa.sigpro.util.jsf.FacesUtil;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Named
@ViewScoped
public class FinanceiroBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Seguranca seguranca;
	@Inject
	private HttpServletResponse response;
	@Inject
	private Pessoas pessoas;
	@Inject
	private Expedicoes expedicoes;

	private Titulo titulo;
	private List<Titulo> lista;
	private List<CentroDeCusto> listaCusto;
	private CondicaoFilter filtro = new CondicaoFilter();
	private int tipoRelatorio;
	Object[] tipos = new Object[2];

	public void limparFiltro() {
		filtro = new CondicaoFilter();
	}

	public void veiculoSelecionado(SelectEvent event) {
		Veiculo veiculo = (Veiculo) event.getObject();
		filtro.setVeiculo(veiculo);
	}

	public void clienteSelecionado(SelectEvent event) {
		Pessoa pessoa = (Pessoa) event.getObject();
		filtro.setCliente(pessoa.getCliente());
		;
	}

	public List<Pessoa> completarPessoa(String nome) {
		return this.pessoas.porNome(nome);
	}

	public List<Titulo> getLista() {
		return lista;
	}

	public Titulo getTitulo() {
		return titulo;
	}

	public void setTitulo(Titulo titulo) {
		this.titulo = titulo;
	}

	public TipoTituloOrigem[] getTiposDC() {
		return TipoTituloOrigem.values();
	}

	public TipoRepeticao[] getRepeticao() {
		return TipoRepeticao.values();
	}

	public TipoPagamento[] getFormaPgtos() {
		return TipoPagamento.values();
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

	public int getTipoRelatorio() {
		return tipoRelatorio;
	}

	public void setTipoRelatorio(int tipoRelatorio) {
		this.tipoRelatorio = tipoRelatorio;
	}

	public Object[] getTipos() {
		tipos[0] = TipoExpedicao.ORC;
		tipos[1] = TipoExpedicao.PED;
		return tipos;
	}

	public String nomeFaturamentoPDF() {
		SimpleDateFormat fmt = new SimpleDateFormat("ddMMyyyy");
		return "faturamento_" + fmt.format(filtro.getEmissaoInicial()) + "_" + fmt.format(filtro.getEmissaoFinal())
				+ ".pdf";
	}

	public void imprimirAnaliseFaturamento() throws JRException, SQLException {
		try {
			switch (tipoRelatorio) {
			case 1:
				imprimirFaturamento();
				break;
			case 2:
				imprimirCurvaFaturamentoProduto();
				break;
			case 3:
				imprimirCurvaFaturamentoCliente();
				break;
			default:
				FacesUtil.addInfoMessage("Em desenvolvimento!");
				break;
			}
		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void imprimirFaturamento() throws JRException, SQLException {
		try {

			filtro.analisaPeriodoEmissao();
			filtro.setOrdem("e.dataEmissao asc");

			List<Expedicao> lst = expedicoes.filtrados(filtro);
			if (lst.size() == 0)
				throw new Exception("Sem dados para imprimir.");

			String path = seguranca.pathClass("relatorio");
			String arquivo = path + "faturamento.jasper";

			JRBeanCollectionDataSource jrds = new JRBeanCollectionDataSource(lst);
			JasperPrint printer = JasperFillManager.fillReport(arquivo, null, jrds);
			Suporte.ImprimirArquivo(response, printer, null);

			filtro.setOrdem(null);

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void imprimirCurvaFaturamentoProduto() throws JRException, SQLException {
		try {

			filtro.analisaPeriodoEmissao();
			filtro.setOrdem("e.dataEmissao");
			Collection<CurvaFaturamentoProduto> lista = expedicoes.listaCurvaFaturamentoProduto(filtro);
			if (lista.size() == 0)
				throw new Exception("Sem dados para imprimir.");

			String path = seguranca.pathClass("relatorio");
			String arquivo = path + "curva_demanda.jasper";

			JRBeanCollectionDataSource jrds = new JRBeanCollectionDataSource(lista);
			JasperPrint printer = JasperFillManager.fillReport(arquivo, null, jrds);
			Suporte.ImprimirArquivo(response, printer, null);

			filtro.setOrdem(null);

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void imprimirCurvaFaturamentoCliente() throws JRException, SQLException {
		try {

			filtro.analisaPeriodoEmissao();
			filtro.setOrdem("e.dataEmissao");
			Collection<CurvaFaturamentoProduto> lista = expedicoes.listaCurvaFaturamentoCliente(filtro);
			if (lista.size() == 0)
				throw new Exception("Sem dados para imprimir.");

			String path = seguranca.pathClass("relatorio");
			String arquivo = path + "curva_cliente.jasper";

			JRBeanCollectionDataSource jrds = new JRBeanCollectionDataSource(lista);
			JasperPrint printer = JasperFillManager.fillReport(arquivo, null, jrds);
			Suporte.ImprimirArquivo(response, printer, null);

			filtro.setOrdem(null);

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

}