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
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.sousa.sigpro.model.Pessoa;
import com.sousa.sigpro.model.Veiculo;
import com.sousa.sigpro.model.VeiculoSaida;
import com.sousa.sigpro.repository.SaidaVeiculos;
import com.sousa.sigpro.repository.Veiculos;
import com.sousa.sigpro.repository.filter.CondicaoFilter;
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
public class SaidaVeiculoBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private HttpServletResponse response;
	@Inject
	private Seguranca seguranca;
	@Inject
	private SaidaVeiculos saidas;
	@Inject
	private Veiculos veiculos;

	private VeiculoSaida saida;
	private CondicaoFilter filtro = new CondicaoFilter();
	private LazyDataModel<VeiculoSaida> lista;
	private List<Veiculo> listaVeiculos;
	private Date horaSaida;
	private Date horaRetorno;

	public SaidaVeiculoBean() {
		// TODO Auto-generated constructor stub
	}

	public Date getHoraRetorno() {
		return horaRetorno;
	}

	public void setHoraRetorno(Date horaRetorno) {
		this.horaRetorno = horaRetorno;
	}

	public Date getHoraSaida() {
		return horaSaida;
	}

	public void setHoraSaida(Date horaSaida) {
		this.horaSaida = horaSaida;
	}

	public VeiculoSaida getSaida() {
		return saida;
	}

	public void onVeiculoChosen(SelectEvent event) {
		saida.setVeiculo((Veiculo) event.getObject());
	}

	public void onMotoristaChosen(SelectEvent event) {
		Pessoa pessoa = (Pessoa) event.getObject();
		saida.setMotorista(pessoa.getMotorista());
	}

	public LazyDataModel<VeiculoSaida> getLista() {
		return lista;
	}

	public CondicaoFilter getFiltro() {
		return filtro;
	}

	public void setFiltro(CondicaoFilter filtro) {
		this.filtro = filtro;
	}

	public List<Veiculo> getListaVeiculos() {
		return listaVeiculos;
	}

	public void inicializar() {
		if (FacesUtil.isNotPostback()) {

		}
	}

	public void iniciar() {
		try {
			saida.setDataSaida(SuporteData.concatenaDataHora(saida.getDataSaida(), horaSaida));
			saida = saidas.guardar(saida);
			saida = null;
		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void encerrar() {
		try {
			saida.setDataRetorno(SuporteData.concatenaDataHora(saida.getDataRetorno(), horaRetorno));
			saida = saidas.guardar(saida);
			saida = null;
		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void pesquisar() {
		lista = new LazyDataModel<VeiculoSaida>() {

			private static final long serialVersionUID = 1L;

			@Override
			public List<VeiculoSaida> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {

				filtro.setPrimeiroRegistro(Long.valueOf(first));
				filtro.setQuantidadeRegistros(Long.valueOf(pageSize));
				filtro.setSortField(sortField);
				filtro.setAscendente(SortOrder.ASCENDING.equals(sortOrder));

				setRowCount(saidas.quantidadeFiltrados(filtro));
				return saidas.lista(filtro);
			}

		};
	}

	public void setSaida(VeiculoSaida saida) {
		this.saida = saida;
		if (saida != null && saida.getDataSaida() != null)
			this.horaSaida = SuporteData.somenteHora(saida.getDataSaida());
		if (saida != null && saida.getDataRetorno() != null)
			this.horaRetorno = SuporteData.somenteHora(saida.getDataRetorno());
	}

	public void preparaEncerramento(VeiculoSaida saida) {
		this.saida = saida;
		saida.setDataRetorno(SuporteData.somenteData(new Date()));
		horaRetorno = SuporteData.somenteHora(new Date());
	}

	public void novo() {

		CondicaoFilter filtroVeiculo = new CondicaoFilter();
		filtroVeiculo.setDisponivel(true);
		listaVeiculos = veiculos.lista(filtroVeiculo);

		saida = new VeiculoSaida();
		saida.setDataSaida(SuporteData.somenteData(new Date()));
		horaSaida = SuporteData.somenteHora(new Date());
		horaRetorno = null;
	}

	public void imprimir() throws JRException, SQLException {
		try {

			filtro.setSortField(" dataSaida, veiculo.placa ");
			filtro.setPrimeiroRegistro(null);
			List<VeiculoSaida> lst = saidas.lista(filtro);

			if (lst.size() == 0)
				throw new Exception("Sem dados para imprimir.");

			HashMap<String, Object> parametros = new HashMap<String, Object>();
			parametros.put("empresa", seguranca.getPessoaLogadoOrigem());
			parametros.put("empresa_endereco", seguranca.enderecoEmpresa());

			String path = seguranca.pathClass("relatorio");
			String arquivo = path + "saidaveiculo_lista.jasper";

			JRBeanCollectionDataSource jrds = new JRBeanCollectionDataSource(lst);
			JasperPrint printer = JasperFillManager.fillReport(arquivo, parametros, jrds);
			Suporte.ImprimirArquivo(response, printer, null);

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

}