package com.sousa.sigpro.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.sousa.sigpro.model.Genero;
import com.sousa.sigpro.repository.Generos;
import com.sousa.sigpro.util.cdi.CDIServiceLocator;

@FacesConverter(forClass = Genero.class)
public class GeneroConverter implements Converter {

	private Generos generos;

	public GeneroConverter() {
		generos = CDIServiceLocator.getBean(Generos.class);
	}

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		Genero retorno = null;
		
		if (value != null) {
			Long id = Long.parseLong(value);
			retorno = generos.porId(id);			
		}
		
		return retorno;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value != null) {
			Genero genero = (Genero) value;
			return genero.getId() == null ? null : genero.getId().toString();
		}
		
		return "";		
	}

}