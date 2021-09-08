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

import com.sousa.sigpro.model.Contato;
import com.sousa.sigpro.repository.Contatos;
import com.sousa.sigpro.repository.filter.CondicaoFilter;

@Named
@ViewScoped
public class SelecaoContatoBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Contatos contatos;
	private LazyDataModel<Contato> lista;
	private CondicaoFilter filtro;

	public SelecaoContatoBean() {
		filtro = new CondicaoFilter();
	}

	public void pesquisar() {
		lista = new LazyDataModel<Contato>() {

			private static final long serialVersionUID = 1L;

			@Override
			public List<Contato> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {

				filtro.setPrimeiroRegistro(Long.valueOf(first));
				filtro.setQuantidadeRegistros(Long.valueOf(pageSize));
				filtro.setSortField(sortField);
				filtro.setAscendente(SortOrder.ASCENDING.equals(sortOrder));

				setRowCount(contatos.quantidadeFiltrados(filtro));
				return contatos.lista(filtro);
			}
		};
	}

	public void selecionar(Contato contato) {
		Contato choosen = contatos.porId(contato.getId());
		PrimeFaces.current().dialog().closeDynamic(choosen);
	}

	public LazyDataModel<Contato> getLista() {
		return lista;
	}

	public CondicaoFilter getFiltro() {
		return filtro;
	}

	public void setFiltro(CondicaoFilter filtro) {
		this.filtro = filtro;
	}
}