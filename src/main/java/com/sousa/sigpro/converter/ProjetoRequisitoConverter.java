package com.sousa.sigpro.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.sousa.sigpro.model.ProjetoRequisito;
import com.sousa.sigpro.repository.ProjetoRequisitos;
import com.sousa.sigpro.util.cdi.CDIServiceLocator;

@FacesConverter(forClass = ProjetoRequisito.class)
public class ProjetoRequisitoConverter implements Converter {

	private ProjetoRequisitos ProjetoRequisitos;

	public ProjetoRequisitoConverter() {
		ProjetoRequisitos = CDIServiceLocator.getBean(ProjetoRequisitos.class);
	}

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		ProjetoRequisito retorno = null;

		if (value != null) {
			Long id = Long.parseLong(value);
			retorno = ProjetoRequisitos.porId(id);
		}

		return retorno;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value != null) {
			ProjetoRequisito ProjetoRequisito = (ProjetoRequisito) value;
			return ProjetoRequisito.getId() == null ? null : ProjetoRequisito.getId().toString();
		}

		return "";
	}

}