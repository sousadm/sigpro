package com.sousa.sigpro.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.sousa.sigpro.model.BalancoTitulo;
import com.sousa.sigpro.repository.Balancos;
import com.sousa.sigpro.util.cdi.CDIServiceLocator;

@FacesConverter(forClass = BalancoTitulo.class)
public class BalancoTituloConverter implements Converter {

	private Balancos balancos;

	public BalancoTituloConverter() {
		balancos = CDIServiceLocator.getBean(Balancos.class);
	}
	
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		BalancoTitulo retorno = null;
		
		if (value != null) {
			Long id = Long.parseLong(value);
			retorno = balancos.porTituloId(id);			
		}
		
		return retorno;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value != null) {
			BalancoTitulo balancoTitulo = (BalancoTitulo) value;
			return balancoTitulo.getId() == null ? null : balancoTitulo.getId().toString();
		}
		
		return "";
	}

}