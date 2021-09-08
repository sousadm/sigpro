package com.sousa.sigpro.sigproapi.repository;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.sousa.sigpro.sigproapi.domain.Demanda;
import com.sousa.sigpro.util.Suporte;
import com.sousa.sigpro.util.SuporteSigproApi;
import com.sousa.sigpro.util.jsf.FacesUtil;

public class Demandas {

	private RestTemplate restTemplate;
	private String URI_BASE;

	public Demandas() {
		restTemplate = new RestTemplate();
		String URN_BASE = "/demandas/" + Suporte.getAtributoDaSessao("empresa");
		URI_BASE = SuporteSigproApi.URL_SIGPRO_API.concat(URN_BASE);
	}

	public List<Demanda> lista() {
		List<Demanda> lst = null;
		try {
			String senha = SuporteSigproApi.CredencialSigproAPI();
			RequestEntity<Void> request = RequestEntity.get(URI.create(URI_BASE)).header("Authorization", senha)
					.build();
			ResponseEntity<Demanda[]> response = restTemplate.exchange(request, Demanda[].class);

			lst = Arrays.asList(response.getBody());
		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
		return lst;

	}

	public Demanda salvar(Demanda demanda) {
		RequestEntity<Demanda> request = RequestEntity.post(URI.create(URI_BASE))
				.header("Authorization", SuporteSigproApi.CredencialSigproAPI()).body(demanda);
		ResponseEntity<Void> response = restTemplate.exchange(request, Void.class);
		return buscar(response.getHeaders().getLocation().toString());
	}

	public Demanda buscar(String uri) {
		RequestEntity<Void> request = RequestEntity.get(URI.create(uri))
				.header("Authorization", SuporteSigproApi.CredencialSigproAPI()).build();
		ResponseEntity<Demanda> response = restTemplate.exchange(request, Demanda.class);

		return response.getBody();
	}

	public Demanda porId(Long id) {
		return buscar(URI_BASE + "/" + id);
	}

}