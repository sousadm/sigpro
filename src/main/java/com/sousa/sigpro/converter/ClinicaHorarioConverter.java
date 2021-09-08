package com.sousa.sigpro.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.sousa.sigpro.model.clinica.ClinicaHorario;
import com.sousa.sigpro.repository.ClinicaHorarios;
import com.sousa.sigpro.util.cdi.CDIServiceLocator;

@FacesConverter(forClass = ClinicaHorario.class)
public class ClinicaHorarioConverter implements Converter {

	private ClinicaHorarios horarios;

	public ClinicaHorarioConverter() {
		horarios = CDIServiceLocator.getBean(ClinicaHorarios.class);
	}

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		ClinicaHorario retorno = null;

		if (value != null) {
			Long id = Long.parseLong(value);
			retorno = horarios.porId(id);
		}

		return retorno;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value != null) {
			ClinicaHorario cor = (ClinicaHorario) value;
			return cor.getId() == null ? null : cor.getId().toString();
		}

		return "";
	}

}
