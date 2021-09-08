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

import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.sousa.sigpro.model.Cliente;
import com.sousa.sigpro.model.Expedicao;
import com.sousa.sigpro.model.ExpedicaoItem;
import com.sousa.sigpro.model.Veiculo;
import com.sousa.sigpro.model.tipo.TipoExpedicao;
import com.sousa.sigpro.repository.Expedicoes;
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
public class ExpedicaoPesquisaBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private HttpServletResponse response;
	@Inject
	private Seguranca seguranca;
	@Inject
	private Expedicoes pedidos;

	Object[] tipos = new Object[2];
	private List<ExpedicaoItem> items;
	private CondicaoFilter filtro;
	private LazyDataModel<Expedicao> model;

	public ExpedicaoPesquisaBean() {
		limpar();
	}

	public void pesquisar() {
		
		model = new LazyDataModel<Expedicao>() {

			private static final long serialVersionUID = 1L;

			@Override
			public List<Expedicao> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {

				filtro.setPrimeiroRegistro(Long.valueOf(first));
				filtro.setQuantidadeRegistros(Long.valueOf(pageSize));
				filtro.setSortField(sortField);
				filtro.setAscendente(SortOrder.ASCENDING.equals(sortOrder));

				setRowCount(pedidos.quantidadeFiltrados(filtro));
				return pedidos.filtrados(filtro);
			}

		};
	}

	public LazyDataModel<Expedicao> getModel() {
		return model;
	}

	public void veiculoSelecionado(SelectEvent event) {
		Veiculo veiculo = (Veiculo) event.getObject();
		filtro.setVeiculo(veiculo);
	}

	public void clienteSelecionado(SelectEvent event) {
		Cliente cliente = (Cliente) event.getObject();
		filtro.setCliente(cliente);
	}

	public CondicaoFilter getFiltro() {
		return filtro;
	}

	public Object[] getTipos() {
		tipos[0] = TipoExpedicao.ORC;
		tipos[1] = TipoExpedicao.PED;
		return tipos;
	}

	public void limpar() {
		filtro = new CondicaoFilter();
	}

	public List<ExpedicaoItem> getItems() {
		return items;
	}

	public void preparaExpedicaoItens(Expedicao expedicao) {
		items = pedidos.itemsPorExpedicao(expedicao);
	}

	public void imprimir_lista() throws JRException, SQLException {
		try {

			filtro.setSortField(" tipo, data_pedido ");
			filtro.setPrimeiroRegistro(null);
			List<Expedicao> lst = pedidos.filtrados(filtro);

			if (lst.size() == 0)
				throw new Exception("Sem dados para imprimir.");

			HashMap<String, Object> parametros = new HashMap<String, Object>();
			parametros.put("empresa", seguranca.getPessoaLogadoOrigem());
			parametros.put("empresa_endereco", seguranca.enderecoEmpresa());

			String arquivo = seguranca.pathClass("relatorio") + "expedicao_lista.jasper";

			JRBeanCollectionDataSource jrds = new JRBeanCollectionDataSource(lst);
			JasperPrint printer = JasperFillManager.fillReport(arquivo, parametros, jrds);
			Suporte.ImprimirArquivo(response, printer, null);

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}
}