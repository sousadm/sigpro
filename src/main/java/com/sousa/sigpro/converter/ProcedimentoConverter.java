package com.sousa.sigpro.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.sousa.sigpro.model.Procedimento;
import com.sousa.sigpro.repository.Procedimentos;
import com.sousa.sigpro.util.cdi.CDIServiceLocator;

@FacesConverter(forClass = Procedimento.class)
public class ProcedimentoConverter implements Converter {

	private Procedimentos procedimentos;

	public ProcedimentoConverter() {
		procedimentos = CDIServiceLocator.getBean(Procedimentos.class);
	}

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		return procedimentos.porId(value);
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value != null) {
			Procedimento procedimento = (Procedimento) value;
			return procedimento.getCodigo() == null ? null : procedimento.getCodigo();
		}

		return "";
	}

}