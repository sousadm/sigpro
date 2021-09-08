package com.sousa.sigpro.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.sousa.sigpro.model.Composto;
import com.sousa.sigpro.repository.Produtos;
import com.sousa.sigpro.util.cdi.CDIServiceLocator;

@FacesConverter(forClass = Composto.class)
public class CompostoConverter implements Converter {

	private Produtos produtos;

	public CompostoConverter() {
		produtos = CDIServiceLocator.getBean(Produtos.class);
	}

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		Composto retorno = null;

		if (value != null) {
			Long id = Long.parseLong(value);
			retorno = produtos.compostoPorId(id);
		}

		return retorno;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value != null) {
			Composto composto = (Composto) value;
			return composto.getId() == null ? null : composto.getId().toString();
		}

		return "";
	}

}