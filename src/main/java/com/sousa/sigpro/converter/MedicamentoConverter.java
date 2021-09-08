package com.sousa.sigpro.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.sousa.sigpro.model.clinica.Medicamento;
import com.sousa.sigpro.repository.Medicamentos;
import com.sousa.sigpro.util.cdi.CDIServiceLocator;

@FacesConverter(forClass = Medicamento.class)
public class MedicamentoConverter implements Converter {

	private Medicamentos medicamentos;

	public MedicamentoConverter() {
		medicamentos = CDIServiceLocator.getBean(Medicamentos.class);
	}

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		Medicamento retorno = null;

		if (value != null)
			retorno = medicamentos.porCodigo(value);

		return retorno;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value != null) {
			Medicamento medicamento = (Medicamento) value;
			return medicamento.getCodigo() == null ? null : medicamento.getCodigo();
		}

		return "";
	}

}