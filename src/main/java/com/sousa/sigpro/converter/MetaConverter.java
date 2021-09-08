package com.sousa.sigpro.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.sousa.sigpro.model.Meta;
import com.sousa.sigpro.repository.Metas;
import com.sousa.sigpro.util.cdi.CDIServiceLocator;

@FacesConverter(forClass = Metas.class)
public class MetaConverter implements Converter {

	private Metas swots;

	public MetaConverter() {
		swots = CDIServiceLocator.getBean(Metas.class);
	}

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		Meta retorno = null;

		if (value != null) {
			Long id = Long.parseLong(value);
			retorno = swots.porId(id);
		}

		return retorno;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value != null) {
			Meta planner = (Meta) value;
			return planner.getId() == null ? null : planner.getId().toString();
		}

		return "";
	}

}