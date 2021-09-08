package com.sousa.sigpro.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.sousa.sigpro.model.Cfop;
import com.sousa.sigpro.repository.Cfops;
import com.sousa.sigpro.util.cdi.CDIServiceLocator;

@FacesConverter(forClass = Cfop.class)
public class CfopConverter implements Converter {

	private Cfops cfops;

	public CfopConverter() {
		cfops = CDIServiceLocator.getBean(Cfops.class);
	}

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		Cfop retorno = null;
		
		if (value != null) {
			Long id = Long.parseLong(value);
			retorno = cfops.porId(id);			
		}
		
		return retorno;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value != null) {
			Cfop cor = (Cfop) value;
			return cor.getId() == null ? null : cor.getId().toString();
		}
		
		return "";		
	}

}