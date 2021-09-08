package com.sousa.sigpro.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.sousa.sigpro.model.ModuloFiscal;
import com.sousa.sigpro.repository.ModuloFiscais;
import com.sousa.sigpro.util.cdi.CDIServiceLocator;

@FacesConverter(forClass = ModuloFiscal.class)
public class ModuloPagamentoConverter implements Converter {

	private ModuloFiscais modulosFiscais;

	public ModuloPagamentoConverter() {
		modulosFiscais = CDIServiceLocator.getBean(ModuloFiscais.class);
	}

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		ModuloFiscal retorno = null;

		if (value != null) {
			Long id = Long.parseLong(value);
			retorno = modulosFiscais.porId(id);
		}

		return retorno;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value != null) {
			ModuloFiscal ModuloFiscal = (ModuloFiscal) value;
			return ModuloFiscal.getId() == null ? null : ModuloFiscal.getId().toString();
		}
		return "";
	}
}
