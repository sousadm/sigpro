package com.sousa.sigpro.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.sousa.sigpro.model.VeiculoDespesa;
import com.sousa.sigpro.repository.Veiculos;
import com.sousa.sigpro.util.cdi.CDIServiceLocator;

@FacesConverter(forClass = VeiculoDespesa.class)
public class VeiculoDespesaConverter implements Converter {

	private Veiculos veiculos;

	public VeiculoDespesaConverter() {
		veiculos = CDIServiceLocator.getBean(Veiculos.class);
	}

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		VeiculoDespesa retorno = null;

		if (value != null) {
			Long id = Long.parseLong(value);
			retorno = veiculos.despesaPorId(id);
		}

		return retorno;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value != null) {
			VeiculoDespesa despesa = (VeiculoDespesa) value;
			return despesa.getId() == null ? null : despesa.getId().toString();
		}

		return "";
	}

}