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

import com.sousa.sigpro.model.clinica.Exame;
import com.sousa.sigpro.repository.Exames;
import com.sousa.sigpro.repository.filter.CondicaoFilter;
import com.sousa.sigpro.util.jsf.FacesUtil;

@Named
@ViewScoped
public class selecaoExameBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Exames exames;
	private CondicaoFilter filtro = new CondicaoFilter();
	private LazyDataModel<Exame> lista;

	public void inicializar() {
		if (FacesUtil.isNotPostback()) {

		}
	}

	public void selecionar(Exame exame) {
		PrimeFaces.current().dialog().closeDynamic(exame);
	}

	public CondicaoFilter getFiltro() {
		return filtro;
	}

	public void setFiltro(CondicaoFilter filtro) {
		this.filtro = filtro;
	}

	public int getQtde() {
		if (lista == null)
			return 0;
		else
			return lista.getRowCount();
	}

	public LazyDataModel<Exame> getLista() {
		return lista;
	}

	public void pesquisar() {

		lista = new LazyDataModel<Exame>() {

			private static final long serialVersionUID = 1L;

			@Override
			public List<Exame> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {

				filtro.setFirst(Long.valueOf(first));
				filtro.setPageSize(Long.valueOf(pageSize));
				filtro.setSortField(sortField);
				filtro.setAscendente(SortOrder.ASCENDING.equals(sortOrder));
				filtro.setSize(exames.quantidadeFiltrados(filtro));
				filtro.setPagina((int) Math.floor(first / pageSize));

				setRowCount(filtro.getSize());
				List<Exame> lst = exames.lista(filtro);

				return lst;
			}
		};

	}

}