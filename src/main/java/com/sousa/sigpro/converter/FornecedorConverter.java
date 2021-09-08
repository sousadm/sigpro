package com.sousa.sigpro.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.sousa.sigpro.model.Fornecedor;
import com.sousa.sigpro.repository.Pessoas;
import com.sousa.sigpro.util.cdi.CDIServiceLocator;

@FacesConverter(forClass = Fornecedor.class)
public class FornecedorConverter implements Converter {

	private Pessoas pessoas;

	public FornecedorConverter() {
		pessoas = CDIServiceLocator.getBean(Pessoas.class);
	}

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		Fornecedor retorno = null;

		if (value != null) {
			Long id = Long.parseLong(value);
			retorno = pessoas.fornecedor(id).getFornecedor();
		}

		return retorno;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value != null) {
			Fornecedor fornecedor = (Fornecedor) value;
			return fornecedor.getId() == null ? null : fornecedor.getId().toString();
		}

		return "";
	}

}