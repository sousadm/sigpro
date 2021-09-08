package com.sousa.sigpro.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.sousa.sigpro.model.RegraPgto;
import com.sousa.sigpro.repository.Regras;
import com.sousa.sigpro.util.cdi.CDIServiceLocator;

@FacesConverter(forClass = RegraPgto.class)
public class RegraPgtoConverter implements Converter {

	private Regras cores;

	public RegraPgtoConverter() {
		cores = CDIServiceLocator.getBean(Regras.class);
	}

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		RegraPgto retorno = null;
		
		if (value != null) {
			Long id = Long.parseLong(value);
			retorno = cores.porId(id);			
		}
		
		return retorno;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value != null) {
			RegraPgto cor = (RegraPgto) value;
			return cor.getId() == null ? null : cor.getId().toString();
		}
		
		return "";		
	}

}