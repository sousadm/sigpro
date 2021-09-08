package com.sousa.sigpro.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.sousa.sigpro.model.Agenda;
import com.sousa.sigpro.repository.Agendas;
import com.sousa.sigpro.util.cdi.CDIServiceLocator;

@FacesConverter(forClass = Agenda.class)
public class AgendaConverter implements Converter {

	private Agendas cores;

	public AgendaConverter() {
		cores = CDIServiceLocator.getBean(Agendas.class);
	}

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		Agenda retorno = null;
		
		if (value != null) {
			Long id = Long.parseLong(value);
			retorno = cores.porId(id);			
		}
		
		return retorno;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value != null) {
			Agenda cor = (Agenda) value;
			return cor.getId() == null ? null : cor.getId().toString();
		}
		
		return "";		
	}

}