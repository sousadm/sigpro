package com.sousa.sigpro.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.sousa.sigpro.sigproapi.domain.Demanda;
import com.sousa.sigpro.sigproapi.repository.Demandas;
import com.sousa.sigpro.util.cdi.CDIServiceLocator;
 
@FacesConverter(forClass = Demanda.class)
public class DemandaConverter implements Converter {

	private Demandas demandas;

	public DemandaConverter() {
		demandas = CDIServiceLocator.getBean(Demandas.class);
	}

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		Demanda retorno = null;

		if (value != null) {
			Long id = Long.parseLong(value);
			retorno = demandas.porId(id);
		}

		return retorno;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value != null) {
			Demanda cor = (Demanda) value;
			return cor.getId() == null ? null : cor.getId().toString();
		}

		return "";
	}

}