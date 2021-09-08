package com.sousa.sigpro.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.sousa.sigpro.model.clinica.Anamnese;
import com.sousa.sigpro.repository.Anamneses;
import com.sousa.sigpro.util.cdi.CDIServiceLocator;

@FacesConverter(forClass = Anamnese.class)
public class AnamneseConverter implements Converter {

	private Anamneses anamneses;

	public AnamneseConverter() {
		anamneses = CDIServiceLocator.getBean(Anamneses.class);
	}

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		Anamnese retorno = null;
		
		if (value != null) {
			Long id = Long.parseLong(value);
			retorno = anamneses.porId(id);			
		}
		
		return retorno;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value != null) {
			Anamnese anamnese = (Anamnese) value;
			return anamnese.getId() == null ? null : anamnese.getId().toString();
		}
		
		return "";		
	}

}