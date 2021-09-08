package com.sousa.sigpro.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.sousa.sigpro.model.RemessaItem;
import com.sousa.sigpro.repository.Remessas;
import com.sousa.sigpro.util.cdi.CDIServiceLocator;

@FacesConverter(forClass = RemessaItem.class)
public class RemessaItemConverter implements Converter {

	private Remessas remessas;

	public RemessaItemConverter() {
		remessas = CDIServiceLocator.getBean(Remessas.class);
	}

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		RemessaItem retorno = null;

		if (value != null) {
			Long id = Long.parseLong(value);
			retorno = remessas.itemPorId(id);
		}

		return retorno;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value != null) {
			RemessaItem remessaItem = (RemessaItem) value;
			return remessaItem.getId() == null ? null : remessaItem.getId().toString();
		}

		return "";
	}

}