package com.sousa.sigpro.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.sousa.sigpro.model.Swot;
import com.sousa.sigpro.repository.Swots;
import com.sousa.sigpro.util.cdi.CDIServiceLocator;

@FacesConverter(forClass = Swot.class)
public class SwotConverter implements Converter {

	private Swots swots;
	
	public SwotConverter() {
		swots = CDIServiceLocator.getBean(Swots.class);
	}
	
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		Swot retorno = null;
		
		if (value != null) {
			Long id = Long.parseLong(value);
			retorno = swots.porId(id);
		}
		
		return retorno;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value != null) {
			Swot planner = (Swot) value;
			return planner.getId() == null ? null : planner.getId().toString();
		}
		
		return "";
	}

}