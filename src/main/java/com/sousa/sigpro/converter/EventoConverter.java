package com.sousa.sigpro.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.sousa.sigpro.model.Evento;
import com.sousa.sigpro.repository.Eventos;
import com.sousa.sigpro.util.cdi.CDIServiceLocator;

@FacesConverter(forClass = Evento.class)
public class EventoConverter implements Converter {

	private Eventos eventos;

	public EventoConverter() {
		eventos = CDIServiceLocator.getBean(Eventos.class);
	}
	
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		Evento retorno = null;
		
		if (value != null) {
			Long id = Long.parseLong(value);
			retorno = eventos.porId(id);			
		}
		
		return retorno;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value != null) {
			Evento evento = (Evento) value;
			return evento.getId() == null ? null : evento.getId().toString();
		}
		
		return "";		
	}
}