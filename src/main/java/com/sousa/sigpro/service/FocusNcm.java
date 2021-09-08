package com.sousa.sigpro.service;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;
import com.sousa.sigpro.model.NcmFocus;
import com.sousa.sigpro.model.PessoaJuridica;
import com.sousa.sigpro.util.Suporte;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

public class FocusNcm implements Serializable {

	private static final long serialVersionUID = 1L;

	String server;
	String login;
	private int total;

	public FocusNcm(PessoaJuridica empresa) {
		login = empresa.getTokenProducao() == null ? Suporte.TOKEN_PROVISORIO : empresa.getTokenProducao();
		server = "https://api.focusnfe.com.br/";
	}

	public List<NcmFocus> pesquisa(String descricao, String codigo, int first) {

		if (descricao == null && codigo == null)
			throw new NegocioException("critério indefinido");

		String uri = "";
		if (codigo != null && codigo.length() > 0)
			uri = "codigo=" + Suporte.onlyNumbers(codigo);

		if (descricao != null && descricao.length() > 0)
			uri = uri + "descricao=" + descricao.toLowerCase();

		String url = server.concat("v2/ncms?" + uri.replace(" ", "+"));

		if (first > 50)
			url = url + "&offset=" + first;

		/* Configuração para realizar o HTTP BasicAuth. */
		try {
			Object config = new DefaultClientConfig();
			Client client = Client.create((ClientConfig) config);
			client.addFilter(new HTTPBasicAuthFilter(login, ""));

			WebResource request = client.resource(url);
			ClientResponse resposta = request.get(ClientResponse.class);
			String body = resposta.getEntity(String.class);
			total = Integer.parseInt(resposta.getHeaders().get("X-Total-Count").get(0));

			Gson gson = new Gson();
			NcmFocus[] lst = gson.fromJson(body.toString(), NcmFocus[].class);
			return Arrays.asList(lst);
		} catch (Exception e) {
			return null;
		}

	}

	public int getTotal() {
		return total;
	}
}
