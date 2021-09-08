package com.sousa.sigpro.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.sousa.sigpro.model.Motorista;
import com.sousa.sigpro.repository.Pessoas;
import com.sousa.sigpro.util.cdi.CDIServiceLocator;

@FacesConverter(forClass = Motorista.class)
public class MotoristaConverter implements Converter {

	private Pessoas pessoas;
	
	public MotoristaConverter() {
		pessoas = CDIServiceLocator.getBean(Pessoas.class);
	}
	
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		Motorista retorno = null;
		
		if (value != null) {
			Long id = Long.parseLong(value);
			retorno = pessoas.motoristaPorId(id);
		}
		
		return retorno;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value != null) {
			Motorista cliente = (Motorista) value;
			return cliente.getId() == null ? null : cliente.getId().toString();
		}
		
		return "";
	}

}