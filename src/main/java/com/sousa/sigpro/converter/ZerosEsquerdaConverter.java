package com.sousa.sigpro.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@FacesConverter(value = "zerosEsquerdaConverter")
public class ZerosEsquerdaConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String objeto) {
		if (objeto != null) {
			return objeto;
		}
		return null;
	}

	@Override
	public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object objeto) {
		return zerosEsquerda((String) objeto);
	}

	public String zerosEsquerda(String valor) {
		try {
			return String.format("%06d", Integer.valueOf(valor));
		} catch (Exception e) {
			return valor;
		}
	}
}