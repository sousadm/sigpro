package com.sousa.sigpro.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.sousa.sigpro.model.Balanco;
import com.sousa.sigpro.repository.Balancos;
import com.sousa.sigpro.util.cdi.CDIServiceLocator;

@FacesConverter(forClass = Balanco.class)
public class BalancoConverter implements Converter {

	private Balancos balancos;

	public BalancoConverter() {
		balancos = CDIServiceLocator.getBean(Balancos.class);
	}

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		Balanco retorno = null;

		if (value != null) {
			Long id = Long.parseLong(value);
			retorno = balancos.porId(id);
		}

		return retorno;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value != null) {
			Balanco balanco = (Balanco) value;
			return balanco.getId() == null ? null : balanco.getId().toString();
		}

		return "";
	}

}