package com.sousa.sigpro.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.sousa.sigpro.model.ProjetoLicaoAprendida;
import com.sousa.sigpro.repository.ProjetoLicaoAprendidas;
import com.sousa.sigpro.util.cdi.CDIServiceLocator;

@FacesConverter(forClass = ProjetoLicaoAprendida.class)
public class ProjetoLicaoAprendidaConverter implements Converter {

	private ProjetoLicaoAprendidas projetoLicaoAprendidas;

	public ProjetoLicaoAprendidaConverter() {
		projetoLicaoAprendidas = CDIServiceLocator.getBean(ProjetoLicaoAprendidas.class);
	}

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		ProjetoLicaoAprendida retorno = null;
		
		if (value != null) {
			Long id = Long.parseLong(value);
			retorno = projetoLicaoAprendidas.porId(id);			
		}
		
		return retorno;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value != null) {
			ProjetoLicaoAprendida genero = (ProjetoLicaoAprendida) value;
			return genero.getId() == null ? null : genero.getId().toString();
		}
		
		return "";		
	}

}