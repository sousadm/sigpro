package com.sousa.sigpro.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.sousa.sigpro.model.ProdutoFornecedor;
import com.sousa.sigpro.repository.ProdutoFornecedores;
import com.sousa.sigpro.util.cdi.CDIServiceLocator;

@FacesConverter(forClass = ProdutoFornecedor.class)
public class ProdutoFornecedorConverter implements Converter {

	private ProdutoFornecedores cores;

	public ProdutoFornecedorConverter() {
		cores = CDIServiceLocator.getBean(ProdutoFornecedores.class);
	}

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		ProdutoFornecedor retorno = null;

		if (value != null) {
			Long id = Long.parseLong(value);
			retorno = cores.porId(id);
		}

		return retorno;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value != null) {
			ProdutoFornecedor cor = (ProdutoFornecedor) value;
			return cor.getSequencia() == null ? null : cor.getSequencia().toString();
		}

		return "";
	}

}