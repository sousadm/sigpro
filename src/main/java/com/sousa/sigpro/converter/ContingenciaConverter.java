package com.sousa.sigpro.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.sousa.sigpro.model.Contingencia;
import com.sousa.sigpro.repository.Contingencias;
import com.sousa.sigpro.util.cdi.CDIServiceLocator;

@FacesConverter(forClass = Contingencia.class)
public class ContingenciaConverter implements Converter {

	private Contingencias cores;

	public ContingenciaConverter() {
		cores = CDIServiceLocator.getBean(Contingencias.class);
	}

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		Contingencia retorno = null;
		
		if (value != null) {
			Long id = Long.parseLong(value);
			retorno = cores.porId(id);			
		}
		
		return retorno;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value != null) {
			Contingencia cor = (Contingencia) value;
			return cor.getId() == null ? null : cor.getId().toString();
		}
		
		return "";		
	}

}