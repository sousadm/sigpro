package com.sousa.sigpro.controller;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;

import org.jrimum.domkee.financeiro.banco.febraban.TipoDeTitulo;
import org.primefaces.event.SelectEvent;

import com.sousa.sigpro.model.CentroDeCusto;
import com.sousa.sigpro.model.ContratoAdesao;
import com.sousa.sigpro.model.Pessoa;
import com.sousa.sigpro.model.Titulo;
import com.sousa.sigpro.model.evento.EventoContratoAdesao;
import com.sousa.sigpro.model.tipo.TipoEntradaSaida;
import com.sousa.sigpro.model.tipo.TipoTituloCaracteristica;
import com.sousa.sigpro.model.tipo.TipoTituloOrigem;
import com.sousa.sigpro.repository.CentrosDeCusto;
import com.sousa.sigpro.repository.ContratoAdesaos;
import com.sousa.sigpro.repository.Eventos;
import com.sousa.sigpro.repository.Pessoas;
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
public class ContratoAdesaoBean implements Controlador, Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private HttpServletResponse response;
	@Inject
	private CentrosDeCusto centros;
	@Inject
	private ContratoAdesaos contratos;
	@Inject
	private Seguranca seguranca;
	@Inject
	private Titulos titulos;
	@Inject
	private Pessoas pessoas;
	@Inject
	private Eventos eventos;

	private CentroDeCusto centroDeCusto;
	private ContratoAdesao contrato;
	private Pessoa contratante;
	private Titulo licenca;
	private List<Titulo> listaLicenca;
	private List<ContratoAdesao> lista = new ArrayList<>();
	private List<CentroDeCusto> listaCusto = new ArrayList<>();
	private boolean principal;
	private String historico = "contrato de adesão de uso de sistema";
	private Date dataMinima;
	private Date dataMaxima;
	private int tabIndex = 0;

	public int getTabIndex() {
		return tabIndex;
	}

	public void setTabIndex(int tabIndex) {
		this.tabIndex = tabIndex;
	}

	public List<ContratoAdesao> getLista() {
		return lista;
	}

	public void setLista(List<ContratoAdesao> lista) {
		this.lista = lista;
	}

	public ContratoAdesao getContrato() {
		return contrato;
	}

	public void onClienteChosen(SelectEvent event) {
		Pessoa p = (Pessoa) event.getObject();
		contrato.setCliente(p.getCliente());
	}

	@Override
	public void salvar() {
		try {
			contrato = contratos.guardar(contrato);
			FacesUtil.addInfoMessage("Gravado com sucesso");
		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	@Override
	public void pesquisar() {
		lista = contratos.lista();
	}

	@Override
	public void excluir() {

	}

	@Override
	public void limpar() {
		contrato = new ContratoAdesao();
		listaLicenca = new ArrayList<>();
	}

	public CentroDeCusto getCentroDeCusto() {
		return centroDeCusto;
	}

	public void setCentroDeCusto(CentroDeCusto centroDeCusto) {
		this.centroDeCusto = centroDeCusto;
	}

	public List<CentroDeCusto> getListaCusto() {
		return listaCusto;
	}

	public String getHistorico() {
		return historico;
	}

	public void setHistorico(String historico) {
		this.historico = historico;
	}

	public boolean isPrincipal() {
		return principal;
	}

	public Titulo getLicenca() {
		return licenca;
	}

	public void setLicenca(Titulo licenca) {
		this.licenca = licenca;
	}

	public void liberar() {
		try {
			contrato = contratos.liberar(contrato);
			sincronizar();
			FacesUtil.addInfoMessage("contrato liberado com sucesso");
		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void addLicenca() {
		licenca = new Titulo();
		if (contrato != null) {

			String documento = SuporteData.formataDataToStr(contrato.getDataLiberado(), "MMyyyy");
			Pessoa responsavel = pessoas.cliente(contrato.getCliente());

			licenca.setContratoAdesao(contrato);
			licenca.setAgente(seguranca.getPessoaLogadoOrigem().getAgente());
			licenca.setDocumento(documento);
			licenca.setResponsavel(responsavel);
			licenca.setDescricao("contrato de licença de uso de sistema");
			licenca.setNominal(contrato.getCliente().getNome());
			licenca.setTipoDC(TipoTituloOrigem.RECEBER);
			licenca.setTipoDocto(TipoDeTitulo.DM_DUPLICATA_MERCANTIL);
			licenca.setCaracteristica(TipoTituloCaracteristica.SIMPLES);
			licenca.setCentroDeCusto(contrato.getCliente().getCentroDeCustoCliente());
		}
	}

	@Override
	public void inicializar() {
		if (FacesUtil.isNotPostback()) {
			dataMinima = new Date();
			dataMaxima = SuporteData.incrementaMesNaData(new Date(), 1);
			addLicenca();
			principal = seguranca.isUsuarioPrincipal();
			listaCusto = centros.lista(TipoEntradaSaida.ENTRADA);
			listaLicenca = new ArrayList<>();
			pesquisar();
		}
	}

	public Date getDataMaxima() {
		return dataMaxima;
	}

	public Date getDataMinima() {
		return dataMinima;
	}

	public List<Titulo> getListaLicenca() {
		return listaLicenca;
	}

	public void gravarLicenca() {
		try {
			tabIndex = 1;
			licenca.setSaldo(licenca.getValor());
			licenca = titulos.guardar(licenca);
			listaLicenca = titulos.lista(contrato);
			FacesUtil.addInfoMessage("gravado com sucesso");
		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void removerLicenca() {
		try {
			contrato = contratos.removerLicenca(contrato);
			FacesUtil.addInfoMessage("licença removida com sucesso");
			eventos.guardar(
					new EventoContratoAdesao(seguranca.getPessoaLogado().getUsuario(), contrato, "licença removida"));
		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void liberarTempoLicenca(int tempo) {
		try {

			if (tempo <= 0)
				throw new NegocioException("tempo incorreto");

			if (contrato.getDataValidade() != null && contrato.getDataValidade().after(new Date()))
				throw new NegocioException("licença ainda em vigência para este contrato");

			Date dataValidade = SuporteData.incrementaDiaNaData(new Date(), tempo);
			contrato.setDataValidade(dataValidade);
			contrato = contratos.guardar(contrato);

			String texto = "licença liberada até " + SuporteData.formataDataToStr(dataValidade, "dd/MM/yyyy");
			FacesUtil.addInfoMessage(texto);

			eventos.guardar(new EventoContratoAdesao(seguranca.getPessoaLogado().getUsuario(), contrato, texto));

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}

	}

	public void sincronizar() {
		try {

			Pessoa cliente = pessoas.cliente(contrato.getCliente());
			if (cliente.getEnderecos() == null || cliente.getEnderecos().size() == 0)
				throw new NegocioException("defina o endereço do cliente");

			if (contrato.getDataLiberado() == null)
				contrato = contratos.liberar(contrato);
			else {
				contrato.setDataAtualizacao(new Date());
				contrato = contratos.guardar(contrato);
			}

			contratos.definirAcessos(contrato);
			FacesUtil.addInfoMessage("opções do contrato atualizadas no cliente");

			eventos.guardar(new EventoContratoAdesao(seguranca.getPessoaLogado().getUsuario(), contrato,
					"sincronização de contrato"));

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void cancelar() {
		try {
			contrato.setDataCancelamento(new Date());
			contrato = contratos.guardar(contrato);
			sincronizar();
			FacesUtil.addInfoMessage("Cancelado com sucesso");
		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void imprimir_lista() throws JRException, SQLException {
		try {

			if (lista.size() == 0)
				throw new Exception("Sem dados para imprimir.");

			String path = seguranca.pathClass("relatorio");
			String arquivo = path + "contratoadesao_lista.jasper";

			HashMap<String, Object> parametros = new HashMap<String, Object>();
			parametros.put("SUBREPORT_DIR", path);
			parametros.put("empresa", seguranca.getPessoaLogadoOrigem());
			parametros.put("empresa_endereco", seguranca.enderecoEmpresa());

			JRBeanCollectionDataSource jrds = new JRBeanCollectionDataSource(lista);
			JasperPrint printer = JasperFillManager.fillReport(arquivo, parametros, jrds);
			Suporte.ImprimirArquivo(response, printer, null);

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public Pessoa getContratante() {
		return contratante;
	}

	public void setContrato(ContratoAdesao contrato) {
		if (contrato != null) {
			contrato = contratos.porId(contrato.getId());
			listaLicenca = titulos.lista(contrato);
			contratante = pessoas.cliente(contrato.getCliente());
		} else {
			listaLicenca = new ArrayList<>();
		}
		this.contrato = contrato;
	}

}