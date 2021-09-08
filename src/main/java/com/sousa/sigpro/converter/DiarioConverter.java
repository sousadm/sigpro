package com.sousa.sigpro.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.sousa.sigpro.model.CaixaDiario;
import com.sousa.sigpro.repository.CaixaDiarios;
import com.sousa.sigpro.util.cdi.CDIServiceLocator;

@FacesConverter(forClass = CaixaDiario.class)
public class DiarioConverter implements Converter {

	private CaixaDiarios diarios;

	public DiarioConverter() {
		diarios = CDIServiceLocator.getBean(CaixaDiarios.class);
	}

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		CaixaDiario retorno = null;
		
		if (value != null) {
			Long id = Long.parseLong(value);
			retorno = diarios.porId(id);			
		}
		
		return retorno;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value != null) {
			CaixaDiario diario = (CaixaDiario) value;
			return diario.getId() == null ? null : diario.getId().toString();
		}
		
		return "";		
	}

}