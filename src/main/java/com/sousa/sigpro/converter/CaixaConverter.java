package com.sousa.sigpro.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.sousa.sigpro.model.Caixa;
import com.sousa.sigpro.repository.Caixas;
import com.sousa.sigpro.util.cdi.CDIServiceLocator;

@FacesConverter(forClass = Caixa.class)
public class CaixaConverter implements Converter {

	private Caixas cores;

	public CaixaConverter() {
		cores = CDIServiceLocator.getBean(Caixas.class);
	}

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		Caixa retorno = null;
		
		if (value != null) {
			Long id = Long.parseLong(value);
			retorno = cores.porId(id);			
		}
		
		return retorno;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value != null) {
			Caixa cor = (Caixa) value;
			return cor.getId() == null ? null : cor.getId().toString();
		}
		
		return "";		
	}

}