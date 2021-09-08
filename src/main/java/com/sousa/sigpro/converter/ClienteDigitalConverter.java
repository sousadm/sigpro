package com.sousa.sigpro.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.sousa.sigpro.model.ClienteDigital;
import com.sousa.sigpro.repository.Pessoas;
import com.sousa.sigpro.util.cdi.CDIServiceLocator;

@FacesConverter(forClass = ClienteDigital.class)
public class ClienteDigitalConverter implements Converter {

	private Pessoas pessoas;

	public ClienteDigitalConverter() {
		pessoas = CDIServiceLocator.getBean(Pessoas.class);
	}

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		ClienteDigital retorno = null;

		if (value != null) {
			Long id = Long.parseLong(value);
			retorno = pessoas.clienteDigitalPorId(id);
		}

		return retorno;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value != null) {
			ClienteDigital cor = (ClienteDigital) value;
			return cor.getId() == null ? null : cor.getId().toString();
		}

		return "";
	}

}