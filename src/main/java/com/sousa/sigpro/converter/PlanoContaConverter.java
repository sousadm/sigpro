package com.sousa.sigpro.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.sousa.sigpro.model.PlanoConta;
import com.sousa.sigpro.repository.PlanoContas;
import com.sousa.sigpro.util.cdi.CDIServiceLocator;

@FacesConverter(forClass = PlanoConta.class)
public class PlanoContaConverter implements Converter {

	private PlanoContas PlanoContas;

	public PlanoContaConverter() {
		PlanoContas = CDIServiceLocator.getBean(PlanoContas.class);
	}

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		PlanoConta retorno = null;
		
		if (value != null) {
			Long id = Long.parseLong(value);
			retorno = PlanoContas.porId(id);			
		}
		
		return retorno;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value != null) {
			PlanoConta PlanoConta = (PlanoConta) value;
			return PlanoConta.getId() == null ? null : PlanoConta.getId().toString();
		}
		
		return "";		
	}

}