package com.sousa.sigpro.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.sousa.sigpro.model.ModuloPagamento;
import com.sousa.sigpro.repository.ModuloPagamentos;
import com.sousa.sigpro.util.cdi.CDIServiceLocator;

@FacesConverter(forClass = ModuloPagamento.class)
public class ModuloFiscalConverter implements Converter {

	private ModuloPagamentos ModulosFiscais;

	public ModuloFiscalConverter() {
		ModulosFiscais = CDIServiceLocator.getBean(ModuloPagamentos.class);
	}

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		ModuloPagamento retorno = null;

		if (value != null) {
			Long id = Long.parseLong(value);
			retorno = ModulosFiscais.porId(id);
		}

		return retorno;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value != null) {
			ModuloPagamento ModuloFiscal = (ModuloPagamento) value;
			return ModuloFiscal.getId() == null ? null : ModuloFiscal.getId().toString();
		}

		return "";
	}
}
