package com.sousa.sigpro.controller.clinica;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.PrimeFaces;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.sousa.sigpro.model.MedicamentoFilter;
import com.sousa.sigpro.model.clinica.Medicamento;
import com.sousa.sigpro.repository.Medicamentos;

@Named
@ViewScoped
public class SelecaoMedicamentoBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Medicamentos medicamentos;

	private MedicamentoFilter filtro;
	private LazyDataModel<Medicamento> lista;

	public SelecaoMedicamentoBean() {
		filtro = new MedicamentoFilter();
	}

	public void selecionar(Medicamento medicamento) {
		PrimeFaces.current().dialog().closeDynamic(medicamento);
	}

	public MedicamentoFilter getFiltro() {
		return filtro;
	}

	public void setFiltro(MedicamentoFilter filtro) {
		this.filtro = filtro;
	}

	public LazyDataModel<Medicamento> getLista() {
		return lista;
	}

	public void pesquisar() {

		lista = new LazyDataModel<Medicamento>() {

			private static final long serialVersionUID = 1L;

			@Override
			public List<Medicamento> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {

				filtro.setFirst(Long.valueOf(first));
				filtro.setPageSize(Long.valueOf(pageSize));
				filtro.setSortField(sortField);
				filtro.setAscendente(SortOrder.ASCENDING.equals(sortOrder));
				filtro.setSize(medicamentos.quantidadeFiltrados(filtro));
				filtro.setPagina((int) Math.floor(first / pageSize));

				setRowCount(filtro.getSize());
				List<Medicamento> lst = medicamentos.lista(filtro);

				return lst;
			}
		};

	}

}