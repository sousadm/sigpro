package com.sousa.sigpro.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.sousa.sigpro.model.Veiculo;
import com.sousa.sigpro.repository.Veiculos;
import com.sousa.sigpro.util.cdi.CDIServiceLocator;

@FacesConverter(forClass = Veiculo.class)
public class VeiculoConverter implements Converter {

	private Veiculos Veiculos;

	public VeiculoConverter() {
		Veiculos = CDIServiceLocator.getBean(Veiculos.class);
	}

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		Veiculo retorno = null;
		
		if (value != null) {
			Long id = Long.parseLong(value);
			retorno = Veiculos.porId(id);			
		}
		
		return retorno;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value != null) {
			Veiculo Veiculo = (Veiculo) value;
			return Veiculo.getId() == null ? null : Veiculo.getId().toString();
		}
		
		return "";		
	}

}