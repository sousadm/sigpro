package com.sousa.sigpro.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.sousa.sigpro.model.Montagem;
import com.sousa.sigpro.repository.Montagens;
import com.sousa.sigpro.util.cdi.CDIServiceLocator;

@FacesConverter(forClass = Montagem.class)
public class MontagemConverter implements Converter {

	private Montagens montagens;

	public MontagemConverter() {
		montagens = CDIServiceLocator.getBean(Montagens.class);
	}

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		Montagem retorno = null;

		if (value != null) {
			Long id = Long.parseLong(value);
			retorno = montagens.porId(id);
		}

		return retorno;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value != null) {
			Montagem montagem = (Montagem) value;
			return montagem.getId() == null ? null : montagem.getId().toString();
		}

		return "";
	}

}