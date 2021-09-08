package com.sousa.sigpro.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.sousa.sigpro.model.BoletoDigital;
import com.sousa.sigpro.repository.Titulos;
import com.sousa.sigpro.util.cdi.CDIServiceLocator;

@FacesConverter(forClass = BoletoDigital.class)
public class BoletoDigitalConverter implements Converter {

	private Titulos titulos;

	public BoletoDigitalConverter() {
		titulos = CDIServiceLocator.getBean(Titulos.class);
	}

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		BoletoDigital retorno = null;

		if (value != null) {
			Long id = Long.parseLong(value);
			retorno = titulos.boletoDigitalporId(id);
		}

		return retorno;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value != null) {
			BoletoDigital boleto = (BoletoDigital) value;
			return boleto.getId() == null ? null : boleto.getId().toString();
		}

		return "";
	}

}