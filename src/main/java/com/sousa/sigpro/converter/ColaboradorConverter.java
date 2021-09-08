package com.sousa.sigpro.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.sousa.sigpro.model.Colaborador;
import com.sousa.sigpro.repository.Pessoas;
import com.sousa.sigpro.util.cdi.CDIServiceLocator;

@FacesConverter(forClass = Colaborador.class)
public class ColaboradorConverter implements Converter {

	private Pessoas pessoas;

	public ColaboradorConverter() {
		pessoas = CDIServiceLocator.getBean(Pessoas.class);
	}

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		Colaborador retorno = null;
		
		if (value != null) {
			Long id = Long.parseLong(value);
			retorno = pessoas.colaboradorPorId(id);			
		}
		
		return retorno;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value != null) {
			Colaborador colaborador = (Colaborador) value;
			return colaborador.getId() == null ? null : colaborador.getId().toString();
		}
		
		return "";		
	}

}