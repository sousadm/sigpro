package com.sousa.sigpro.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.sousa.sigpro.model.ContratoAdesao;
import com.sousa.sigpro.repository.ContratoAdesaos;
import com.sousa.sigpro.util.cdi.CDIServiceLocator;

@FacesConverter(forClass = ContratoAdesao.class)
public class ContratoAdesaoConverter implements Converter {

	private ContratoAdesaos cores;

	public ContratoAdesaoConverter() {
		cores = CDIServiceLocator.getBean(ContratoAdesaos.class);
	}

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		ContratoAdesao retorno = null;

		if (value != null) {
			Long id = Long.parseLong(value);
			retorno = cores.porId(id);
		}

		return retorno;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value != null) {
			ContratoAdesao cor = (ContratoAdesao) value;
			return cor.getId() == null ? null : cor.getId().toString();
		}

		return "";
	}
}