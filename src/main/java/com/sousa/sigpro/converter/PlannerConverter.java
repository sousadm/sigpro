package com.sousa.sigpro.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.sousa.sigpro.model.Planner;
import com.sousa.sigpro.repository.Planners;
import com.sousa.sigpro.util.cdi.CDIServiceLocator;

@FacesConverter(forClass = Planner.class)
public class PlannerConverter implements Converter {

	private Planners planners;
	
	public PlannerConverter() {
		planners = CDIServiceLocator.getBean(Planners.class);
	}
	
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		Planner retorno = null;
		
		if (value != null) {
			Long id = Long.parseLong(value);
			retorno = planners.porId(id);
		}
		
		return retorno;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value != null) {
			Planner planner = (Planner) value;
			return planner.getId() == null ? null : planner.getId().toString();
		}
		
		return "";
	}

}