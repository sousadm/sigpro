package com.sousa.sigpro.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.sousa.sigpro.model.Municipio;
import com.sousa.sigpro.repository.Municipios;
import com.sousa.sigpro.util.cdi.CDIServiceLocator;

@FacesConverter(forClass = Municipio.class)
public class MunicipioConverter implements Converter {

	private Municipios municipios;

	public MunicipioConverter() {
		municipios = CDIServiceLocator.getBean(Municipios.class);
	}

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		Municipio retorno = null;

		if (value != null) {
			Long id = Long.parseLong(value);
			retorno = municipios.porId(id);
		}

		return retorno;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value != null) {
			Municipio municipio = (Municipio) value;
			return municipio.getId() == null ? null : municipio.getId().toString();
		}

		return "";
	}

}