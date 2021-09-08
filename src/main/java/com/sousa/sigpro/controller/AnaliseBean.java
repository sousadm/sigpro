package com.sousa.sigpro.controller;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;

import com.sousa.sigpro.model.Expedicao;
import com.sousa.sigpro.model.FluxoDeCaixa;
import com.sousa.sigpro.repository.Balancos;
import com.sousa.sigpro.repository.Expedicoes;
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
public class AnaliseBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private HttpServletResponse response;
	@Inject
	private Seguranca seguranca;
	@Inject
	private Expedicoes expedicoes;
	@Inject
	private Balancos balancos;
	private CondicaoFilter filtro;

	public AnaliseBean() {
		limpar();
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

	public void imprimirFluxoDeCaixa() throws JRException, SQLException {
		try {

			List<FluxoDeCaixa> lst = balancos.FluxoDeCaixaDetalhado(filtro);
			if (lst.size() == 0)
				throw new Exception("Sem dados para imprimir.");

			Map<String, Object> parametros = new HashMap<>();
			parametros.put("empresa", seguranca.getPessoaLogadoOrigem());
			parametros.put("empresa_endereco", seguranca.enderecoEmpresa());
			parametros.put("titulo", "FLUXO DE CAIXA");

			String path = seguranca.pathClass("relatorio");
			String arquivo = path + "fluxoDeCaixa.jasper";

			JRBeanCollectionDataSource jrds = new JRBeanCollectionDataSource(lst);
			JasperPrint printer = JasperFillManager.fillReport(arquivo, parametros, jrds);

			if (printer.getPages().size() == 0)
				throw new NegocioException("Dados não encontrados!");

			Suporte.ImprimirArquivo(response, printer, null);

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void imprimirMargemNegociacao() throws JRException, SQLException {
		try {

			filtro.analisaPeriodoEmissao();
			filtro.setOrdem("e.dataEmissao asc");

			List<Expedicao> lista = expedicoes.filtrados(filtro);
			if (lista.size() == 0)
				throw new Exception("Sem dados para imprimir.");

			String path = seguranca.pathClass("relatorio");
			String arquivo = path + "margemNegociacao.jasper";

			Map<String, Object> parametros = new HashMap<>();
			parametros.put("empresa", seguranca.getPessoaLogadoOrigem());
			parametros.put("empresa_endereco", seguranca.enderecoEmpresa());
			parametros.put("titulo", "MARGEM DE NEGOCIAÇÃO");

			JRBeanCollectionDataSource jrds = new JRBeanCollectionDataSource(lista);
			JasperPrint printer = JasperFillManager.fillReport(arquivo, parametros, jrds);
			Suporte.ImprimirArquivo(response, printer, null);

			filtro.setOrdem(null);

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void imprimir() throws JRException, SQLException {
		try {
			switch (filtro.getOpcao()) {
			case 1:
				imprimirFluxoDeCaixa();
				break;
			case 2:
				filtro.setEmissaoInicial(filtro.getInicio());
				filtro.setEmissaoFinal(filtro.getTermino());
				filtro.setInicio(null);
				filtro.setTermino(null);
				imprimirMargemNegociacao();
				break;
			default:
				FacesUtil.addInfoMessage("Em desenvolvimento!");
				break;
			}
		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

}