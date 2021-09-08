package com.sousa.sigpro.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.sousa.sigpro.model.Titulo;
import com.sousa.sigpro.repository.Titulos;
import com.sousa.sigpro.util.cdi.CDIServiceLocator;

@FacesConverter(forClass = Titulo.class)
public class TituloConverter implements Converter {

	private Titulos titulos;

	public TituloConverter() {
		titulos = CDIServiceLocator.getBean(Titulos.class);
	}

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		Titulo retorno = null;
		
		if (value != null) {
			Long id = Long.parseLong(value);
			retorno = titulos.porId(id);			
		}
		
		return retorno;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value != null) {
			Titulo titulo = (Titulo) value;
			return titulo.getId() == null ? null : titulo.getId().toString();
		}
		
		return "";		
	}

}