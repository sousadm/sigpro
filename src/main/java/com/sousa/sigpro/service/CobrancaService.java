package com.sousa.sigpro.service;

import java.util.Date;
import java.util.List;

import com.sousa.sigpro.model.BoletoDigital;
import com.sousa.sigpro.model.ClienteDigital;
import com.sousa.sigpro.model.Pessoa;
import com.sousa.sigpro.model.Titulo;
import com.sousa.sigpro.repository.filter.CondicaoFilter;

public abstract class CobrancaService {

	public abstract ClienteDigital adicionaClienteNaPlataformaAPI(Pessoa pessoa);

	public abstract ClienteDigital atualizaClienteNaPlataformaAPI(Pessoa pessoa, String id);
	
	public abstract Pessoa pesquisaClienteNaPlataformaAPI(String id);

	public abstract BoletoDigital adicionaBoletoNaPlataformaAPI(String clienteId_api, Titulo titulo);

	public abstract BoletoDigital adicionaBoletoNaPlataformaAPI(String clienteId_api, Titulo titulo, String id);

	public abstract List<BoletoDigital> listaBoletoDaPlataformaAPI(CondicaoFilter filtro);

	public abstract BoletoDigital atualizaBoletoNaPlataforma(BoletoDigital boletoDigital);
	
	public abstract BoletoDigital restauraBoletoNaPlataforma(BoletoDigital boletoDigital);

	public abstract BoletoDigital confirmaRecebimentoNaPlataforma(BoletoDigital boletoDigital, Date data, double valor);
	
	public abstract boolean removerBoletoNaPlataforma(BoletoDigital boletoDigital);

}