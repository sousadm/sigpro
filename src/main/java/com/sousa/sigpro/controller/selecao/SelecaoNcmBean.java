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

import com.sousa.sigpro.model.NcmFocus;
import com.sousa.sigpro.security.Seguranca;
import com.sousa.sigpro.service.FocusNcm;
import com.sousa.sigpro.util.jsf.FacesUtil;

@Named
@ViewScoped
public class SelecaoNcmBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Seguranca seguranca;

	private String codigo;
	private String descricao;
	private LazyDataModel<NcmFocus> lista;

	public void inicializar() {
		if (FacesUtil.isNotPostback()) {

		}
	}

	public void pesquisar() {

		FocusNcm focus = new FocusNcm(seguranca.getPessoaLogadoOrigem().getPJ());

		lista = new LazyDataModel<NcmFocus>() {

			private static final long serialVersionUID = 1L;

			@Override
			public List<NcmFocus> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {

				List<NcmFocus> lst = focus.pesquisa(descricao, codigo, first);

				setRowCount(focus.getTotal());
				return lst;
			}

		};

	}

	public void selecionar(NcmFocus ncmFocus) {
		PrimeFaces.current().dialog().closeDynamic(ncmFocus);
	}

	public LazyDataModel<NcmFocus> getLista() {
		return lista;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

}