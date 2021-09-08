package com.sousa.sigpro.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.sousa.sigpro.model.clinica.Convenio;
import com.sousa.sigpro.repository.Convenios;
import com.sousa.sigpro.util.cdi.CDIServiceLocator;

@FacesConverter(forClass = Convenio.class)
public class ConvenioConverter implements Converter {

	private Convenios planosDeSaude;

	public ConvenioConverter() {
		planosDeSaude = CDIServiceLocator.getBean(Convenios.class);
	}

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		Convenio retorno = null;
		
		if (value != null) {
			Long id = Long.parseLong(value);
			retorno = planosDeSaude.porId(id);			
		}
		
		return retorno;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value != null) {
			Convenio planoDeSaude = (Convenio) value;
			return planoDeSaude.getId() == null ? null : planoDeSaude.getId().toString();
		}
		
		return "";		
	}

}