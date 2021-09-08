package com.sousa.sigpro.controller;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;

import com.sousa.sigpro.model.Balanco;
import com.sousa.sigpro.model.BalancoProduto;
import com.sousa.sigpro.model.BalancoTitulo;
import com.sousa.sigpro.model.tipo.TipoTituloOrigem;
import com.sousa.sigpro.repository.Balancos;
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
public class BalancoBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private HttpServletResponse response;
	@Inject
	private Balancos balancos;
	@Inject
	private Seguranca seguranca;

	private BalancoTitulo balancoTitulo;
	private BalancoProduto balancoProduto;
	private Balanco balanco;
	private List<Balanco> lista = new ArrayList<>();

	public BalancoBean() {
		balanco = new Balanco();
	}

	public void pesquisar() {
		lista = balancos.lista();
	}

	public void inicializar() {
		if (FacesUtil.isNotPostback()) {

		}
	}

	public void excluir(Balanco balanco) {
		try {
			balancos.remover(balanco);
			lista.remove(balanco);
			FacesUtil.addInfoMessage("excluído com sucesso.");
		} catch (Exception e) {
			FacesUtil.addRequestErrorMessage(e.getMessage());
		}
	}

	public Balanco getBalanco() {
		return balanco;
	}

	public void setBalanco(Balanco balanco) {
		this.balanco = balanco;
	}

	public List<Balanco> getLista() {
		return lista;
	}

	public BalancoProduto getBalancoProduto() {
		return balancoProduto;
	}

	public void setBalancoProduto(BalancoProduto balancoProduto) {
		this.balancoProduto = balancoProduto;
	}

	public BalancoTitulo getBalancoTitulo() {
		return balancoTitulo;
	}

	public void setBalancoTitulo(BalancoTitulo balancoTitulo) {
		this.balancoTitulo = balancoTitulo;
	}

	public void salvar() {
		try {

			if (balanco.getDataApuracao() == null)
				throw new NegocioException("defina a data de apuração");

			if (!balanco.getDataApuracao().equals(SuporteData.ultimaDataDoMes(balanco.getDataApuracao())))
				throw new NegocioException("Defina a data do último dia do mês em análise");

			if (!balanco.isExiste()) {
				List<Balanco> teste = balancos.lista(balanco.getDataApuracao());
				if (teste != null && teste.size() > 0)
					throw new NegocioException("já existe uma análise patrimonial na data de apuração");
			}

			double valor = 0.0;
			Date hoje = SuporteData.somenteData(new Date());
			Date apuracao = SuporteData.somenteData(balanco.getDataApuracao());
			int res = apuracao.compareTo(hoje);
			if (res > 0)
				throw new NegocioException("data de apuração incorreta");

			Balanco proximo = balancos.proximo(balanco.getDataApuracao());
			if (proximo != null)
				throw new NegocioException("existe apuração em data superior");

			balanco.setUsuario(seguranca.getPessoaLogado().getUsuario());
			balancos.definirPrazoMedio(balanco);
			balancos.definirGiroDeEstoque(balanco);

			if (balanco.getEmpresa() == null)
				balanco.setEmpresa(seguranca.getPessoaLogadoOrigem());

			if (balanco.getDataCadastro() == null)
				balanco.setDataCadastro(new Date());

			if (balanco.getTitulos() == null || balanco.getTitulos().size() == 0)
				balancos.montarBalancoPatrimonialTitulo(balanco);

			if (balanco.getProdutos() == null || balanco.getProdutos().size() == 0)
				balancos.montarBalancoPatrimonialProduto(balanco, res == 0);

			balanco.calcular();

			Balanco anterior = balancos.anterior(balanco.getDataApuracao());
			double variacao = 0;
			if (anterior != null && anterior.getValorPatrimonioLiquido() > 0) {
				variacao = (balanco.getValorPatrimonioLiquido() / anterior.getValorPatrimonioLiquido()) * 100;
				balanco.setVariacao(variacao);
			}

			if (balanco.isExiste())
				valor = balancos.balancoValor(balanco.getId());

//			boolean grava_evento = !balanco.isExiste() || (balanco.getValorPatrimonioLiquido() != valor);

			balanco = balancos.guardar(balanco);

			FacesUtil.addInfoMessage("Gravado com sucesso");

			// if (grava_evento) {
			// String historico = "análise patrimonial "
			// + SuporteData.formataDataToStr(balanco.getDataApuracao(), "dd/MM/yyyy") + "
			// valor: "
			// + Suporte.formataCurrency(balanco.getValorPatrimonioLiquido());
			// Evento evento = new Evento(seguranca.getPessoaLogado().getUsuario(),
			// TipoModulo.ANALISE,
			// balanco.getId(), historico);
			// evento = eventos.guardar(evento, balanco);
			// }

		} catch (Exception e) {
			FacesUtil.addRequestErrorMessage(e.getMessage());
		}
	}

	public void imprimir() throws JRException, SQLException {
		try {

			if (!balanco.isExiste())
				throw new NegocioException("balanço não localizado");

			List<Balanco> lst = new ArrayList<Balanco>();
			lst.add(balanco);

			String path = seguranca.pathClass("relatorio");
			String arquivo = path + "balanco.jasper";

			Map<String, Object> parametros = new HashMap<>();
			parametros.put("empresa", seguranca.getPessoaLogadoOrigem());
			parametros.put("empresa_endereco", seguranca.enderecoEmpresa());
			parametros.put("titulo",
					"Balanço Patrimonial - " + SuporteData.formataDataToStr(balanco.getDataApuracao(), "dd/MM/yyyy"));
			JRBeanCollectionDataSource jrds = new JRBeanCollectionDataSource(lst);
			JasperPrint printer = JasperFillManager.fillReport(arquivo, parametros, jrds);
			Suporte.ImprimirArquivo(response, printer, null);

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void imprimirGiroDeEstoque() throws JRException, SQLException {
		try {

			if (!balanco.isExiste())
				throw new NegocioException("balanço não localizado");

			List<BalancoProduto> lst = balancos.listaGiroProduto(balanco);

			String path = seguranca.pathClass("relatorio");
			String arquivo = path + "balancoGiroProduto.jasper";

			Map<String, Object> parametros = new HashMap<>();
			parametros.put("empresa", seguranca.getPessoaLogadoOrigem());
			parametros.put("empresa_endereco", seguranca.enderecoEmpresa());
			parametros.put("titulo",
					"GIRO DE ESTOQUE - " + SuporteData.formataDataToStr(balanco.getDataApuracao(), "dd/MM/yyyy"));
			JRBeanCollectionDataSource jrds = new JRBeanCollectionDataSource(lst);
			JasperPrint printer = JasperFillManager.fillReport(arquivo, parametros, jrds);
			Suporte.ImprimirArquivo(response, printer, null);

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void imprimirInventario() throws JRException, SQLException {
		try {

			if (!balanco.isExiste())
				throw new NegocioException("balanço não localizado");

			List<BalancoProduto> lst = balancos.listaInventario(balanco);

			String path = seguranca.pathClass("relatorio");
			String arquivo = path + "balancoInventario.jasper";

			Map<String, Object> parametros = new HashMap<>();
			parametros.put("empresa", seguranca.getPessoaLogadoOrigem());
			parametros.put("empresa_endereco", seguranca.enderecoEmpresa());
			parametros.put("titulo",
					"REGISTRO DE IVENTÁRIO - " + SuporteData.formataDataToStr(balanco.getDataApuracao(), "dd/MM/yyyy"));
			JRBeanCollectionDataSource jrds = new JRBeanCollectionDataSource(lst);
			JasperPrint printer = JasperFillManager.fillReport(arquivo, parametros, jrds);
			Suporte.ImprimirArquivo(response, printer, null);

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void imprimirEstoque() throws JRException, SQLException {
		try {

			if (!balanco.isExiste())
				throw new NegocioException("balanço não localizado");

			List<BalancoProduto> lst = balancos.listaEstoque(balanco);

			String path = seguranca.pathClass("relatorio");
			String arquivo = path + "balancoEstoque.jasper";

			Map<String, Object> parametros = new HashMap<>();
			parametros.put("empresa", seguranca.getPessoaLogadoOrigem());
			parametros.put("empresa_endereco", seguranca.enderecoEmpresa());
			parametros.put("titulo",
					"ESTOQUE DE PRODUTOS - " + SuporteData.formataDataToStr(balanco.getDataApuracao(), "dd/MM/yyyy"));
			JRBeanCollectionDataSource jrds = new JRBeanCollectionDataSource(lst);
			JasperPrint printer = JasperFillManager.fillReport(arquivo, parametros, jrds);
			Suporte.ImprimirArquivo(response, printer, null);

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void imprimirContaPagar() throws JRException, SQLException {
		imprimirTitulo(TipoTituloOrigem.PAGAR);
	}

	public void imprimirContaReceber() throws JRException, SQLException {
		imprimirTitulo(TipoTituloOrigem.RECEBER);
	}

	public void imprimirTitulo(TipoTituloOrigem tipo) throws JRException, SQLException {
		try {

			if (!balanco.isExiste())
				throw new NegocioException("balanço não localizado");

			List<BalancoTitulo> lst = balancos.listaTitulo(tipo);

			String path = seguranca.pathClass("relatorio");
			String arquivo = path + "balancoTitulo.jasper";

			Map<String, Object> parametros = new HashMap<>();
			parametros.put("empresa", seguranca.getPessoaLogadoOrigem());
			parametros.put("empresa_endereco", seguranca.enderecoEmpresa());
			parametros.put("titulo", tipo.getDescricao().toUpperCase() + " - "
					+ SuporteData.formataDataToStr(balanco.getDataApuracao(), "dd/MM/yyyy"));
			JRBeanCollectionDataSource jrds = new JRBeanCollectionDataSource(lst);
			JasperPrint printer = JasperFillManager.fillReport(arquivo, parametros, jrds);
			Suporte.ImprimirArquivo(response, printer, null);

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

}