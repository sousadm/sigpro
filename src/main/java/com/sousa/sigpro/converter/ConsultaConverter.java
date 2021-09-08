package com.sousa.sigpro.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.sousa.sigpro.model.clinica.ClinicaConsulta;
import com.sousa.sigpro.repository.Consultas;
import com.sousa.sigpro.util.cdi.CDIServiceLocator;

@FacesConverter(forClass = ClinicaConsulta.class)
public class ConsultaConverter implements Converter {

	private Consultas consultas;

	public ConsultaConverter() {
		consultas = CDIServiceLocator.getBean(Consultas.class);
	}

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		ClinicaConsulta retorno = null;
		
		if (value != null) {
			Long id = Long.parseLong(value);
			retorno = consultas.porId(id);			
		}
		
		return retorno;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value != null) {
			ClinicaConsulta consulta = (ClinicaConsulta) value;
			return consulta.getId() == null ? null : consulta.getId().toString();
		}
		
		return "";		
	}

}