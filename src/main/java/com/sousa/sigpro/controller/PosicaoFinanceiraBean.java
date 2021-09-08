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
import javax.servlet.http.HttpServletResponse;

import org.primefaces.event.SelectEvent;

import com.sousa.sigpro.model.Pessoa;
import com.sousa.sigpro.model.PosicaoFinanceira;
import com.sousa.sigpro.model.PosicaoFinanceiraDetalhe;
import com.sousa.sigpro.repository.Pessoas;
import com.sousa.sigpro.repository.PosicaoFinanceiras;
import com.sousa.sigpro.security.Seguranca;
import com.sousa.sigpro.util.Suporte;
import com.sousa.sigpro.util.SuporteData;
import com.sousa.sigpro.util.jsf.FacesUtil;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Named
@ViewScoped
public class PosicaoFinanceiraBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private HttpServletResponse response;
	@Inject
	private Seguranca seguranca;
	@Inject
	private PosicaoFinanceiras financeiras;
	@Inject
	private Pessoas pessoas;

	private PosicaoFinanceira posicao;
	private double valorCredito;

	public void inicializar() {
		if (FacesUtil.isNotPostback()) {
			posicao = new PosicaoFinanceira(null, SuporteData.primeiraDataDoMes(new Date()),
					SuporteData.ultimaDataDoMes(new Date()));
		}
	}

	public void gravarCredito() {
		try {
			Pessoa pessoa = pessoas.porId(posicao.getPessoa().getId());
			pessoa.setValorCredito(valorCredito);
			pessoa = pessoas.guardar(pessoa);
			FacesUtil.addInfoMessage("Crédito disponível para operação");
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Falha no processo: " + e.getMessage());
		}
	}

	public void onPessoaChosen(SelectEvent event) {
		Pessoa pessoa = (Pessoa) event.getObject();
		posicao.setPessoa(pessoa);
	}

	public double getValorCredito() {
		return valorCredito;
	}

	public void setValorCredito(double valorCredito) {
		this.valorCredito = valorCredito;
	}

	public PosicaoFinanceira getPosicao() {
		return posicao;
	}

	public void setPosicao(PosicaoFinanceira posicao) {
		this.posicao = posicao;
	}

	public void imprimir() throws JRException, SQLException {
		try {

			String path = seguranca.pathClass("relatorio");
			String arquivo = path + "posicao_financeira.jasper";

			Map<String, Object> parametros = new HashMap<>();
			parametros.put("empresa", seguranca.getPessoaLogadoOrigem());
			parametros.put("empresa_endereco", seguranca.enderecoEmpresa());
			parametros.put("posicao", posicao);

			List<PosicaoFinanceiraDetalhe> detalhes = posicao.getDetalhes();

			JRBeanCollectionDataSource jrds = new JRBeanCollectionDataSource(detalhes);
			JasperPrint printer = JasperFillManager.fillReport(arquivo, parametros, jrds);
			Suporte.ImprimirArquivo(response, printer, null);

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void pesquisar() {
		try {
			financeiras.prepara(posicao);
			valorCredito = posicao.getValorCredito();
		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

}