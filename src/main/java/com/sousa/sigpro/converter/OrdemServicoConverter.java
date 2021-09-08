package com.sousa.sigpro.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.sousa.sigpro.model.OrdemServico;
import com.sousa.sigpro.repository.OrdemServicos;
import com.sousa.sigpro.util.cdi.CDIServiceLocator;

@FacesConverter(forClass = OrdemServico.class)
public class OrdemServicoConverter implements Converter {

	private OrdemServicos OrdemServicos;

	public OrdemServicoConverter() {
		OrdemServicos = CDIServiceLocator.getBean(OrdemServicos.class);
	}

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		OrdemServico retorno = null;
		
		if (value != null) {
			Long id = Long.parseLong(value);
			retorno = OrdemServicos.porId(id);			
		}
		
		return retorno;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value != null) {
			OrdemServico OrdemServico = (OrdemServico) value;
			return OrdemServico.getId() == null ? null : OrdemServico.getId().toString();
		}
		
		return "";		
	}

}