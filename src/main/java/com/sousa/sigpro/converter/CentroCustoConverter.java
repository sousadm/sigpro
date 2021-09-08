package com.sousa.sigpro.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.sousa.sigpro.model.CentroDeCusto;
import com.sousa.sigpro.repository.CentrosDeCusto;
import com.sousa.sigpro.util.cdi.CDIServiceLocator;

@FacesConverter(forClass = CentroDeCusto.class)
public class CentroCustoConverter implements Converter {

	private CentrosDeCusto centros;

	public CentroCustoConverter() {
		centros = CDIServiceLocator.getBean(CentrosDeCusto.class);
	}

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		CentroDeCusto retorno = null;
		
		if (value != null) {
			Long id = Long.parseLong(value);
			retorno = centros.porId(id);			
		}
		
		return retorno;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value != null) {
			CentroDeCusto centroDeCusto = (CentroDeCusto) value;
			return centroDeCusto.getId() == null ? null : centroDeCusto.getId().toString();
		}
		
		return "";		
	}

}