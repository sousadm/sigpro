package com.sousa.sigpro.service;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;
import com.sousa.sigpro.model.EnderecoFocus;
import com.sousa.sigpro.model.PessoaJuridica;
import com.sousa.sigpro.util.Suporte;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

public class FocusEndereco implements Serializable {

	private static final long serialVersionUID = 1L;

	String server;
	String login;

	public FocusEndereco(PessoaJuridica empresa) {
		login = Suporte.TOKEN_PROVISORIO; //!= null ? Suporte.TOKEN_PROVISORIO : empresa.getTokenProducao();
		server = "https://api.focusnfe.com.br/";
	}

	public EnderecoFocus pesquisa(String cep) {

		EnderecoFocus retorno = null;
		String url = server.concat("v2/ceps/" + Suporte.onlyNumbers(cep));

		/* Configuração para realizar o HTTP BasicAuth. */
		Object config = new DefaultClientConfig();
		Client client = Client.create((ClientConfig) config);
		client.addFilter(new HTTPBasicAuthFilter(login, ""));

		WebResource request = client.resource(url);
		ClientResponse resposta = request.get(ClientResponse.class);
		String body = resposta.getEntity(String.class);

		Gson g = new Gson();
		retorno = g.fromJson(body.toString(), EnderecoFocus.class);

		return retorno;

	}

	public List<EnderecoFocus> pesquisa(String uf, String localidade, String endereco) {

		if (endereco == null)
			throw new NegocioException("defina um endereço para pesquisa");

		String uri = "logradouro=" + endereco.toLowerCase();
		if (localidade != null)
			uri = uri + "&localidade=" + localidade;

		if (uf != null)
			uri = "uf=" + uf.toUpperCase() + "&" + uri;

		String url = server.concat("v2/ceps?" + uri.replace(" ", "+"));

		/* Configuração para realizar o HTTP BasicAuth. */
		try {
			Object config = new DefaultClientConfig();
			Client client = Client.create((ClientConfig) config);
			client.addFilter(new HTTPBasicAuthFilter(login, ""));

			WebResource request = client.resource(url);
			ClientResponse resposta = request.get(ClientResponse.class);
			String body = resposta.getEntity(String.class);

			Gson gson = new Gson();
			EnderecoFocus[] lst = gson.fromJson(body.toString(), EnderecoFocus[].class);
			return Arrays.asList(lst);
		} catch (Exception e) {
			return null;
		}

	}

}
