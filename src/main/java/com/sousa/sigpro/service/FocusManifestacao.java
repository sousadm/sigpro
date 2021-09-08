package com.sousa.sigpro.service;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;
import com.sousa.sigpro.model.Manifestacao;
import com.sousa.sigpro.model.PessoaJuridica;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

public class FocusManifestacao implements Serializable {

	private static final long serialVersionUID = 1L;

	String cnpj;
	String server;
	String login;
	private int total;

	public FocusManifestacao(PessoaJuridica empresa) {
		cnpj = empresa.getCnpj();
		login = empresa.getTokenProducao();
		server = "https://api.focusnfe.com.br/";
	}

	public List<Manifestacao> pesquisa() {

		String url = "https://api.focusnfe.com.br/v2/nfes_recebidas?cnpj=" + cnpj;

		try {
			Object config = new DefaultClientConfig();
			Client client = Client.create((ClientConfig) config);
			client.addFilter(new HTTPBasicAuthFilter(login, ""));

			WebResource request = client.resource(url);
			ClientResponse resposta = request.get(ClientResponse.class);
			String body = resposta.getEntity(String.class);
			total = Integer.parseInt(resposta.getHeaders().get("X-Total-Count").get(0));

			Gson gson = new Gson();
			Manifestacao[] lst = gson.fromJson(body.toString(), Manifestacao[].class);
			return Arrays.asList(lst);
		} catch (Exception e) {
			return null;
		}

	}

	public String nota(String chave) {

		String url = "https://api.focusnfe.com.br/v2/nfes_recebidas/" + chave + ".xml";

		/* Configuração para realizar o HTTP BasicAuth. */
		Object config = new DefaultClientConfig();
		Client client = Client.create((ClientConfig) config);
		client.addFilter(new HTTPBasicAuthFilter(login, ""));

		WebResource request = client.resource(url);
		ClientResponse resposta = request.get(ClientResponse.class);
		// int httpCode = resposta.getStatus();
		String body = resposta.getEntity(String.class);

		// System.out.print("HTTP Code: ");
		// System.out.print(httpCode);
		// System.out.printf(body);

		return body;

	}

	public int getTotal() {
		return total;
	}
}
