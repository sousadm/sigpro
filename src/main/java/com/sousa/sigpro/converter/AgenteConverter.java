package com.sousa.sigpro.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.sousa.sigpro.model.Agente;
import com.sousa.sigpro.repository.Pessoas;
import com.sousa.sigpro.util.cdi.CDIServiceLocator;

@FacesConverter(forClass = Agente.class)
public class AgenteConverter implements Converter {

	private Pessoas pessoas;

	public AgenteConverter() {
		pessoas = CDIServiceLocator.getBean(Pessoas.class);
	}

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {

		Agente retorno = null;

		if (value != null) {
			Long id = Long.parseLong(value);
			retorno = pessoas.agentePorId(id);
		}

		return retorno;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value != null) {
			Agente agente = (Agente) value;
			return agente.getId() == null ? null : agente.getId().toString();
		}

		return "";
	}

}