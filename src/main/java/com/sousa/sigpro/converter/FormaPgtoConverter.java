package com.sousa.sigpro.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.sousa.sigpro.model.FormaPgto;
import com.sousa.sigpro.repository.FormaPgtos;
import com.sousa.sigpro.util.cdi.CDIServiceLocator;

@FacesConverter(forClass = FormaPgto.class)
public class FormaPgtoConverter implements Converter {

	private FormaPgtos formaPgtos;
	
	public FormaPgtoConverter() { 
		formaPgtos = CDIServiceLocator.getBean(FormaPgtos.class);
	}
	
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		FormaPgto retorno = null;

		if (value != null) {
			Long id = Long.parseLong(value);
			retorno = formaPgtos.porId(id);
		}
		
		return retorno;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value != null) {
			FormaPgto formaPgto = (FormaPgto) value;
			return formaPgto.getId() == null ? null : formaPgto.getId().toString();
		}

		return "";
	}

} 