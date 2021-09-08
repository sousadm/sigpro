package com.sousa.sigpro.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.sousa.sigpro.model.Vendedor;
import com.sousa.sigpro.repository.Pessoas;
import com.sousa.sigpro.util.cdi.CDIServiceLocator;

@FacesConverter(forClass = Vendedor.class)
public class VendedorConverter implements Converter {

	private Pessoas pessoas;

	public VendedorConverter() {
		pessoas = CDIServiceLocator.getBean(Pessoas.class);
	}

	@Override
	public Object getAsObject(FacesContext context, UIComponent component,
			String value) {
		Vendedor retorno = null;
		if (value != null) {
			Long id = Long.parseLong(value);
			retorno = this.pessoas.vendedorPorId(id);
		}
		return retorno;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component,
			Object value) {
		if (value != null) {
			return ((Vendedor) value).getId().toString();
		}

		return "";
	}

}