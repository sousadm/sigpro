package com.sousa.sigpro.controller.selecao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.PrimeFaces;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.sousa.sigpro.model.Veiculo;
import com.sousa.sigpro.repository.Veiculos;
import com.sousa.sigpro.repository.filter.CondicaoFilter;

@Named
@ViewScoped
public class SelecaoVeiculoBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Veiculos veiculos;

	private CondicaoFilter filtro;
	private LazyDataModel<Veiculo> model;

	public SelecaoVeiculoBean() {
		filtro = new CondicaoFilter();
	}

	public void pesquisar() {
		model = new LazyDataModel<Veiculo>() {

			private static final long serialVersionUID = 1L;

			@Override
			public List<Veiculo> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {

				filtro.setPrimeiroRegistro(Long.valueOf(first));
				filtro.setQuantidadeRegistros(Long.valueOf(pageSize));
				filtro.setSortField(sortField);
				filtro.setAscendente(SortOrder.ASCENDING.equals(sortOrder));

				setRowCount(veiculos.quantidadeFiltrados(filtro));
				return veiculos.lista(filtro);
			}
		};
	}

	public LazyDataModel<Veiculo> getModel() {
		return model;
	}

	public CondicaoFilter getFiltro() {
		return filtro;
	}

	public void setFiltro(CondicaoFilter filtro) {
		this.filtro = filtro;
	}

	public void selecionar(Veiculo veiculo) {
		Veiculo choosen = veiculos.porId(veiculo.getId());
		PrimeFaces.current().dialog().closeDynamic(choosen);
	}

}