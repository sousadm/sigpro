package com.sousa.sigpro.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.sousa.sigpro.model.Marca;
import com.sousa.sigpro.repository.Marcas;
import com.sousa.sigpro.util.cdi.CDIServiceLocator;

@FacesConverter(forClass = Marca.class)
public class MarcaConverter implements Converter {

	private Marcas marcas;

	public MarcaConverter() {
		marcas = CDIServiceLocator.getBean(Marcas.class);
	}

	@Override
	public Object getAsObject(FacesContext context, UIComponent component,
			String value) {
		Marca retorno = null;

		if (value != null) {
			Long id = Long.parseLong(value);
			retorno = marcas.porId(id);
		}
		
		return retorno;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component,
			Object value) {
		if (value != null) {
			Marca marca = (Marca) value;
			return marca.getId() == null ? null : marca.getId().toString();
		}

		return "";
	}

}