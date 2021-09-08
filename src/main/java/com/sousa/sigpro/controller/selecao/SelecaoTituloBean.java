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

import com.sousa.sigpro.model.Titulo;
import com.sousa.sigpro.model.tipo.TipoSituacaoFinanceira;
import com.sousa.sigpro.model.tipo.TipoTituloOrigem;
import com.sousa.sigpro.repository.Titulos;
import com.sousa.sigpro.repository.filter.CondicaoFilter;
import com.sousa.sigpro.util.Suporte;
import com.sousa.sigpro.util.jsf.FacesUtil;

@Named
@ViewScoped
public class SelecaoTituloBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Titulos titulos;
	private CondicaoFilter filtro;
	private LazyDataModel<Titulo> lista;
	private Titulo[] selecionados;
	private Titulo selecionado;
	private boolean multiplo;
	private TipoTituloOrigem tipoTituloOrigem;

	public SelecaoTituloBean() {
		filtro = new CondicaoFilter();
		Boolean multi = (Boolean) Suporte.getAtributoDaSessao("multiplo");
		if (multi != null)
			multiplo = multi;
		tipoTituloOrigem = (TipoTituloOrigem) Suporte.getAtributoDaSessao("TipoTituloOrigem");
	}

	@PreDestroy
	public void destroy() {
		Suporte.removerAtributoDaSessao("multiplo");
		Suporte.removerAtributoDaSessao("TipoTituloOrigem");
	}

	public TipoTituloOrigem getTipoTituloOrigem() {
		return tipoTituloOrigem;
	}

	public Titulo getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(Titulo selecionado) {
		this.selecionado = selecionado;
	}

	public boolean isMultiplo() {
		return multiplo;
	}

	public Titulo[] getSelecionados() {
		return selecionados;
	}

	public void setSelecionados(Titulo[] selecionados) {
		this.selecionados = selecionados;
	}

	public LazyDataModel<Titulo> getLista() {
		return lista;
	}

	public CondicaoFilter getFiltro() {
		return filtro;
	}

	public void setFiltro(CondicaoFilter filtro) {
		this.filtro = filtro;
	}

	public void pesquisar() {
		FacesUtil.addInfoMessage("em produção");
	}

	public void selecionar(Titulo titulo) {
		Titulo choosen = titulos.porId(titulo.getId());
		PrimeFaces.current().dialog().closeDynamic(choosen);
	}

	public void selecionar() {
		if (multiplo)
			PrimeFaces.current().dialog().closeDynamic(selecionados);
		else
			PrimeFaces.current().dialog().closeDynamic(selecionado);
	}

	public void contasReceber() {
		TipoTituloOrigem[] mat = new TipoTituloOrigem[1];
		mat[0] = TipoTituloOrigem.RECEBER;
		filtro.setSql("contaBancaria is null");
		filtro.setTituloStatus(TipoSituacaoFinanceira.ABERTO);
		filtro.setTipoDC(mat);
		montaListaDados();
	}

	public void contasPagar() {
		TipoTituloOrigem[] mat = new TipoTituloOrigem[1];
		mat[0] = TipoTituloOrigem.PAGAR;
		filtro.setSql("contaBancaria is null");
		filtro.setTituloStatus(TipoSituacaoFinanceira.ABERTO);
		filtro.setTipoDC(mat);
		montaListaDados();
	}

	private void montaListaDados() {
		lista = new LazyDataModel<Titulo>() {

			private static final long serialVersionUID = 1L;

			private List<Titulo> titulosLazy;

			@Override
			public List<Titulo> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {

				filtro.setPrimeiroRegistro(Long.valueOf(first));
				filtro.setQuantidadeRegistros(Long.valueOf(pageSize));
				filtro.setSortField(sortField);
				filtro.setAscendente(SortOrder.ASCENDING.equals(sortOrder));

				setRowCount(titulos.quantidadeFiltrados(filtro));
				titulosLazy = titulos.lista(filtro);
				return titulosLazy;
			}

			@Override
			public Titulo getRowData(String id) {
				return titulos.porId(Long.parseLong(id));
			}

		};
	}

}