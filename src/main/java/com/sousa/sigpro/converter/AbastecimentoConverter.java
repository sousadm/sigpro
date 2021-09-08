package com.sousa.sigpro.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.sousa.sigpro.model.Abastecimento;
import com.sousa.sigpro.repository.Veiculos;
import com.sousa.sigpro.util.cdi.CDIServiceLocator;

@FacesConverter(forClass = Abastecimento.class)
public class AbastecimentoConverter implements Converter {

	private Veiculos abastecimentos;

	public AbastecimentoConverter() {
		abastecimentos = CDIServiceLocator.getBean(Veiculos.class);
	}

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		Abastecimento retorno = null;

		if (value != null) {
			Long id = Long.parseLong(value);
			retorno = abastecimentos.abastecimentoPorId(id);
		}

		return retorno;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value != null) {
			Abastecimento abastecimento = (Abastecimento) value;
			return abastecimento.getId() == null ? null : abastecimento.getId().toString();
		}

		return "";
	}

}