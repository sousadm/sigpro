package com.sousa.sigpro.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.sousa.sigpro.model.Veiculo;
import com.sousa.sigpro.repository.Veiculos;
import com.sousa.sigpro.util.cdi.CDIServiceLocator;

@FacesConverter(forClass = Veiculo.class)
public class EquipamentoConverter implements Converter {

	private Veiculos veiculos;

	public EquipamentoConverter() {
		veiculos = CDIServiceLocator.getBean(Veiculos.class);
	}

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		Veiculo retorno = null;
		
		if (value != null) {
			Long id = Long.parseLong(value);
			retorno = veiculos.porId(id);			
		}
		
		return retorno;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value != null) {
			Veiculo veiculo = (Veiculo) value;
			return veiculo.getId() == null ? null : veiculo.getId().toString();
		}
		
		return "";		
	}

}