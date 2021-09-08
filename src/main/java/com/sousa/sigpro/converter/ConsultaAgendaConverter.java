package com.sousa.sigpro.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.sousa.sigpro.model.clinica.ClinicaAgenda;
import com.sousa.sigpro.repository.ConsultaAgendas;
import com.sousa.sigpro.util.cdi.CDIServiceLocator;

@FacesConverter(forClass = ClinicaAgenda.class)
public class ConsultaAgendaConverter implements Converter {

	private ConsultaAgendas cores;

	public ConsultaAgendaConverter() {
		cores = CDIServiceLocator.getBean(ConsultaAgendas.class);
	}

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		ClinicaAgenda retorno = null;
		
		if (value != null) {
			Long id = Long.parseLong(value);
			retorno = cores.porId(id);			
		}
		
		return retorno;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value != null) {
			ClinicaAgenda cor = (ClinicaAgenda) value;
			return cor.getId() == null ? null : cor.getId().toString();
		}
		
		return "";		
	}

}