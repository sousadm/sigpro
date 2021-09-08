package com.sousa.sigpro.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.sousa.sigpro.model.Parametro;
import com.sousa.sigpro.repository.Parametros;
import com.sousa.sigpro.util.cdi.CDIServiceLocator;

@FacesConverter(forClass = Parametro.class)
public class ParametroConverter implements Converter {

	private Parametros cores;

	public ParametroConverter() {
		cores = CDIServiceLocator.getBean(Parametros.class);
	}

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		Parametro retorno = null;
		
		if (value != null) {
			Long id = Long.parseLong(value);
			retorno = cores.porId(id);			
		}
		
		return retorno;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value != null) {
			Parametro cor = (Parametro) value;
			return cor.getId() == null ? null : cor.getId().toString();
		}
		
		return "";		
	}

}