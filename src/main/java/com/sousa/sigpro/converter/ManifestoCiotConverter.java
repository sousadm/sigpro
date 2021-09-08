package com.sousa.sigpro.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.sousa.sigpro.model.ManifestoCiot;
import com.sousa.sigpro.repository.ManifestoCiots;
import com.sousa.sigpro.util.cdi.CDIServiceLocator;

@FacesConverter(forClass = ManifestoCiot.class)
public class ManifestoCiotConverter implements Converter {

	private ManifestoCiots cores;

	public ManifestoCiotConverter() {
		cores = CDIServiceLocator.getBean(ManifestoCiots.class);
	}

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		ManifestoCiot retorno = null;

		if (value != null) {
			Long id = Long.parseLong(value);
			retorno = cores.porId(id);
		}

		return retorno;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value != null) {
			ManifestoCiot cor = (ManifestoCiot) value;
			return cor.getId() == null ? null : cor.getId().toString();
		}

		return "";
	}

}