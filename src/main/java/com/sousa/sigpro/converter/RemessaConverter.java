package com.sousa.sigpro.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.sousa.sigpro.model.Remessa;
import com.sousa.sigpro.repository.Remessas;
import com.sousa.sigpro.util.cdi.CDIServiceLocator;

@FacesConverter(forClass = Remessa.class)
public class RemessaConverter implements Converter {

	private Remessas remessas;

	public RemessaConverter() {
		remessas = CDIServiceLocator.getBean(Remessas.class);
	}

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		Remessa retorno = null;

		if (value != null) {
			Long id = Long.parseLong(value);
			retorno = remessas.porId(id);
		}

		return retorno;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value != null) {
			Remessa remessa = (Remessa) value;
			return remessa.getId() == null ? null : remessa.getId().toString();
		}

		return "";
	}

}