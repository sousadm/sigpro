package com.sousa.sigpro.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.sousa.sigpro.model.Questionario;
import com.sousa.sigpro.repository.Questionarios;
import com.sousa.sigpro.util.cdi.CDIServiceLocator;

@FacesConverter(forClass = Questionario.class)
public class QuestionarioConverter implements Converter {

	private Questionarios cores;

	public QuestionarioConverter() {
		cores = CDIServiceLocator.getBean(Questionarios.class);
	}

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		Questionario retorno = null;

		if (value != null) {
			Long id = Long.parseLong(value);
			retorno = cores.porId(id);
		}

		return retorno;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value != null) {
			Questionario cor = (Questionario) value;
			return cor.getId() == null ? null : cor.getId().toString();
		}

		return "";
	}

}