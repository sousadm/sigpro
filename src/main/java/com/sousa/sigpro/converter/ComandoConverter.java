package com.sousa.sigpro.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.sousa.sigpro.model.Comando;
import com.sousa.sigpro.repository.Comandos;
import com.sousa.sigpro.util.cdi.CDIServiceLocator;

@FacesConverter(forClass = Comando.class)
public class ComandoConverter implements Converter {

	private Comandos cores;

	public ComandoConverter() {
		cores = CDIServiceLocator.getBean(Comandos.class);
	}

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		Comando retorno = null;
		
		if (value != null) {
			Long id = Long.parseLong(value);
			retorno = cores.porId(id);			
		}
		
		return retorno;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value != null) {
			Comando cor = (Comando) value;
			return cor.getId() == null ? null : cor.getId().toString();
		}
		
		return "";		
	}

}