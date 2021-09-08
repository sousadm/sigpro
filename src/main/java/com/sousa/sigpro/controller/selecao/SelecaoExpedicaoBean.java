package com.sousa.sigpro.controller.selecao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.annotation.PreDestroy;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.PrimeFaces;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.sousa.sigpro.model.Expedicao;
import com.sousa.sigpro.repository.Expedicoes;
import com.sousa.sigpro.repository.filter.CondicaoFilter;
import com.sousa.sigpro.util.Suporte;

@Named
@ViewScoped
public class SelecaoExpedicaoBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Expedicoes expedicoes;
	private CondicaoFilter filtro;
	private LazyDataModel<Expedicao> lista;
	private Expedicao[] selecionados;
	private Expedicao selecionado;
	private boolean multiplo;

	public SelecaoExpedicaoBean() {
		filtro = new CondicaoFilter();
		Boolean multi = (Boolean) Suporte.getAtributoDaSessao("multiplo");
		if (multi != null)
			multiplo = multi;
	}

	@PreDestroy
	public void destroy() {
		Suporte.removerAtributoDaSessao("multiplo");
	}

	public Expedicao getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(Expedicao selecionado) {
		this.selecionado = selecionado;
	}

	public boolean isMultiplo() {
		return multiplo;
	}

	public Expedicao[] getSelecionados() {
		return selecionados;
	}

	public void setSelecionados(Expedicao[] selecionados) {
		this.selecionados = selecionados;
	}

	public LazyDataModel<Expedicao> getLista() {
		return lista;
	}

	public CondicaoFilter getFiltro() {
		return filtro;
	}

	public void setFiltro(CondicaoFilter filtro) {
		this.filtro = filtro;
	}

	public void selecionar() {
		if (multiplo)
			PrimeFaces.current().dialog().closeDynamic(selecionados);
		else
			PrimeFaces.current().dialog().closeDynamic(selecionado);
	}

	public void pesquisar() {

		if (filtro.getNumeroDe() != null)
			filtro.setNumeroAte(filtro.getNumeroDe());

		filtro.setSql(" e.tipo = 'PED' and e.dataCancelamento is null and e.dataEmissao is not null ");

		lista = new LazyDataModel<Expedicao>() {

			private static final long serialVersionUID = 1L;

			private List<Expedicao> titulosLazy;

			@Override
			public List<Expedicao> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {

				filtro.setPrimeiroRegistro(Long.valueOf(first));
				filtro.setQuantidadeRegistros(Long.valueOf(pageSize));
				filtro.setSortField(sortField);
				filtro.setAscendente(SortOrder.ASCENDING.equals(sortOrder));

				setRowCount(expedicoes.quantidadeFiltrados(filtro));
				titulosLazy = expedicoes.filtrados(filtro);
				return titulosLazy;
			}

			@Override
			public Expedicao getRowData(String id) {
				return expedicoes.porId(Long.parseLong(id));
			}

		};
	}

}