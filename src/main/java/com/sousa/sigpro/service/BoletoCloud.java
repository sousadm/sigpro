package com.sousa.sigpro.service;

import static javax.ws.rs.core.MediaType.WILDCARD;
import static javax.ws.rs.core.Response.Status.CREATED;

import java.io.IOException;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;

import com.sousa.sigpro.model.ContaCorrente;
import com.sousa.sigpro.model.Pessoa;
import com.sousa.sigpro.model.Titulo;
import com.sousa.sigpro.util.SuporteData;

public class BoletoCloud {

	/*
	 * https://sandbox.boletocloud.com/usuario/dados/
	 */
	public static String token = "api-key_9JNMcQNlLSSbFpHINlT13Sl2qZ8r2DtTetckVbi89yM=";
	public static String server = "https://sandbox.boletocloud.com/api/v1";

	private static Pessoa empresa;

	public BoletoCloud(Pessoa empresa) {
		this.empresa = empresa;
	}

	public static String ler(Long numero) throws IOException {

		String boleto = "bhx8boVVwKLXpsgWrPlSqgzLXL19gzFECO7NBWV4TkQ=";

		// server = "https://sandbox.boletocloud.com/boleto/2via/";
		// String url = server
		// .concat(boleto);
		// Object config = new DefaultClientConfig();
		// Client client = Client.create((ClientConfig) config);
		// client.addFilter(new HTTPBasicAuthFilter(token, "token"));
		//
		// WebResource request = client.resource(url);
		// ClientResponse resposta = request.get(ClientResponse.class);
		//
		// Files.copy(resposta.getEntity(InputStream.class),
		// Paths.get("d:/arquivo-api-boleto-get-teste.pdf"), REPLACE_EXISTING);
		//
		// return resposta.toString();

		Response response = ClientBuilder.newClient().target("https://sandbox.boletocloud.com/api/v1/boletos/")
				.path(boleto) // Token do boleto
				.register(HttpAuthenticationFeature.basic(token, "token")).request(WILDCARD)// Aceita qualquer resposta
				.get();

		System.out.println("HTTP Status Code: " + response.getStatus());

		return "teste";
	}

	public static void gerar(Titulo titulo) {

		ContaCorrente conta = titulo.getContaBancaria();

		Form formData = new Form();
//		formData.param("boleto.conta.banco", conta.getBanco().getCodigo());
//		formData.param("boleto.conta.agencia", conta.getAgenciaToStr());
//		formData.param("boleto.conta.numero", conta.getNumeroToStr());
//		formData.param("boleto.conta.carteira", String.valueOf(conta.getInstrucao().getCarteiraCodigo()));
//		formData.param("boleto.beneficiario.nome", empresa.getNome());
//		formData.param("boleto.beneficiario.cprf", empresa.getCpfCnpjToString());
//		formData.param("boleto.beneficiario.endereco.cep", empresa.getEndereco().getCep());
//		formData.param("boleto.beneficiario.endereco.uf", empresa.getEndereco().getMunicipio().getUf().getCodigo());
//		formData.param("boleto.beneficiario.endereco.localidade", empresa.getEndereco().getMunicipio().getDescricao());
//		formData.param("boleto.beneficiario.endereco.bairro", empresa.getEndereco().getBairro());
//		formData.param("boleto.beneficiario.endereco.logradouro", empresa.getEndereco().getLogradouro());
//		formData.param("boleto.beneficiario.endereco.numero",
//				empresa.getEndereco().getNumero() == 0 ? "s/n" : String.valueOf(empresa.getEndereco().getNumero()));
//		formData.param("boleto.beneficiario.endereco.complemento", empresa.getEndereco().getComplemento());
//		formData.param("boleto.emissao", SuporteData.formataDataToStr(titulo.getEmissao(), "yyyy-MM-dd"));
//		formData.param("boleto.vencimento", SuporteData.formataDataToStr(titulo.getVencimento(), "yyyy-MM-dd"));
//		formData.param("boleto.documento", String.valueOf(titulo.getId()));
//		formData.param("boleto.numero", String.valueOf(titulo.getId()));
//		formData.param("boleto.titulo", titulo.getTipoDocto().getSigla());
//		formData.param("boleto.valor", String.valueOf(titulo.getSaldo()));
//		formData.param("boleto.pagador.nome", titulo.getResponsavel().getNome());
//		formData.param("boleto.pagador.cprf", titulo.getResponsavel().getCpfCnpjToString());
//		formData.param("boleto.pagador.endereco.cep", titulo.getResponsavel().getEndereco().getCep());
//		formData.param("boleto.pagador.endereco.uf",
//				titulo.getResponsavel().getEndereco().getMunicipio().getUf().getCodigo());
//		formData.param("boleto.pagador.endereco.localidade",
//				titulo.getResponsavel().getEndereco().getMunicipio().getDescricao());
//		formData.param("boleto.pagador.endereco.bairro", titulo.getResponsavel().getEndereco().getBairro());
//		formData.param("boleto.pagador.endereco.logradouro", titulo.getResponsavel().getEndereco().getLogradouro());
//		formData.param("boleto.pagador.endereco.numero", titulo.getResponsavel().getEndereco().getNumero() == 0 ? "s/n"
//				: String.valueOf(titulo.getResponsavel().getEndereco().getNumero()));
//		formData.param("boleto.pagador.endereco.complemento", titulo.getResponsavel().getEndereco().getComplemento());
//		if (conta.getInstrucao().getInstrucao1() != null)
//			formData.param("boleto.instrucao", conta.getInstrucao().getInstrucao1());
//		if (conta.getInstrucao().getInstrucao2() != null)
//			formData.param("boleto.instrucao", conta.getInstrucao().getInstrucao2());
		// formData.param("boleto.instrucao", "Mais info em
		// http://www.boletocloud.com/app/dev/api");

		Response response = ClientBuilder.newClient().target(server).path("/boletos")
				.register(HttpAuthenticationFeature.basic(token, "token")).request(WILDCARD)// Aceita qualquer resposta
				.post(Entity.form(formData));

		System.out.println("HTTP Status Code: " + response.getStatus());
		// System.out.println("Boleto Cloud
		// Version:"+response.getHeaderString("X-BoletoCloud-Version"));
		// System.out.println("Boleto Cloud Token:
		// "+response.getHeaderString("X-BoletoCloud-Token"));

		if (response.getStatus() == CREATED.getStatusCode()) {
			String resposta = response.toString();
			System.out.println(resposta);
		} else {
			System.out.println("erro");
			// System.err.println("Erro retornado em json:
			// "+response.readEntity(String.class));
		}

	}

}