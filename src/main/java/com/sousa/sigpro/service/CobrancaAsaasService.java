package com.sousa.sigpro.service;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.inject.Named;
import javax.ws.rs.core.MediaType;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

import com.fincatto.documentofiscal.DFAmbiente;
import com.google.gson.Gson;
import com.sousa.sigpro.model.Agente;
import com.sousa.sigpro.model.BoletoDigital;
import com.sousa.sigpro.model.ClienteDigital;
import com.sousa.sigpro.model.Endereco;
import com.sousa.sigpro.model.Pessoa;
import com.sousa.sigpro.model.PessoaFisica;
import com.sousa.sigpro.model.PessoaJuridica;
import com.sousa.sigpro.model.Titulo;
import com.sousa.sigpro.model.tipo.TipoEndereco;
import com.sousa.sigpro.model.tipo.TipoPessoa;
import com.sousa.sigpro.model.tipo.TipoTituloOrigem;
import com.sousa.sigpro.repository.Pessoas;
import com.sousa.sigpro.repository.Titulos;
import com.sousa.sigpro.repository.filter.CondicaoFilter;
import com.sousa.sigpro.util.Suporte;
import com.sousa.sigpro.util.SuporteData;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

@Named
public class CobrancaAsaasService extends CobrancaService implements Serializable {

	private static final long serialVersionUID = 1L;

	static String URL_PRODUCAO = "https://www.asaas.com/api/v3";
	static String URL_HOMOLOGACAO = "https://sandbox.asaas.com/api/v3";

	private Titulos titulos;
	private Pessoas pessoas;

	Agente agente;

	public CobrancaAsaasService(Agente agente) {
		this.agente = agente;
	}

	public CobrancaAsaasService(Agente agente, Pessoas pessoas) {
		this.pessoas = pessoas;
		this.agente = agente;
	}

	public CobrancaAsaasService(Agente agente, Titulos titulos) {
		this.titulos = titulos;
		this.agente = agente;
	}

	public CobrancaAsaasService(Agente agente, Pessoas pessoas, Titulos titulos) {
		this.pessoas = pessoas;
		this.titulos = titulos;
		this.agente = agente;
	}

	public class Customer {
		public String id;
		public Date dateCreated;
		public String name;
		public String cpfCnpj;
		public String email;
		public String phone;
		public String mobilePhone;
		public String address;
		public String addressNumber;
		public String complement;
		public String province;
		public String postalCode;
		public String externalReference;
		public String[] additionalEmails;
		public String municipalInscription;
		public String stateInscription;
		public Long city;
		public Boolean notificationDisabled;
		public Boolean deleted;
		public Boolean foreignCustomer;
		public String state;
		public String country;
	}

	public enum TipoBillingType {
		BOLETO, CREDIT_CARD, UNDEFINED;
	}

	public enum TipoDedesconto {
		FIXED, PERCENTAGE;
	}

	public class Discount {
		public double value;
		public int dueDateLimitDays;
		public TipoDedesconto type;
	}

	public class Interest {
		public double value;
	}

	public class Fine {
		public double value;
	}

	public Customer pessoaToCobraCliente(Pessoa pessoa, Endereco endereco) {

		if (endereco == null)
			throw new NegocioException("endereço não localizado");

		if (endereco.getMunicipio() == null)
			throw new NegocioException("município indefinido para o cliente");

		Customer cobra = new Customer();
		cobra.externalReference = Suporte.zerosAEsquerda(pessoa.getId(), 6);
		cobra.name = pessoa.getNome();
		cobra.cpfCnpj = Suporte.onlyNumbers(pessoa.getCpfCnpjToString());
		// próximos não são obrigatórios
		cobra.email = pessoa.getEmail();
		cobra.phone = Suporte.onlyNumbers(pessoa.getFone());
		cobra.mobilePhone = Suporte.onlyNumbers(pessoa.getCelular());
		cobra.addressNumber = String.valueOf(endereco.getNumero());
		cobra.complement = endereco.getComplemento();
		cobra.postalCode = Suporte.onlyNumbers(endereco.getCep());
		cobra.city = endereco.getMunicipio().getId();
		cobra.province = endereco.getBairro();
		cobra.address = endereco.getLogradouro();

		cobra.notificationDisabled = false;
		if (pessoa.getPJ() != null) {
			cobra.municipalInscription = Suporte.onlyNumbers(pessoa.getPJ().getIM());
			cobra.stateInscription = Suporte.onlyNumbers(pessoa.getPJ().getIE());
		}

		return cobra;

	}

	public List<Customer> pesquisaPorCpfCnpj(String valor) {

		String uri = (agente.getAmbiente() == DFAmbiente.PRODUCAO ? URL_PRODUCAO : URL_HOMOLOGACAO)
				.concat("/customers?cpfCnpj=".concat(Suporte.onlyNumbers(valor)));

		Client client = Client.create();
		WebResource webResource = client.resource(uri);
		ClientResponse resposta = webResource.accept(MediaType.TEXT_PLAIN_TYPE)
				.header("access_token", agente.getTokenDeCobranca()).get(ClientResponse.class);

		Suporte.analisaRetornoHTTP(resposta);

		String body = resposta.getEntity(String.class);
		Gson gson = new Gson();
		CobraClienteLista cobraClienteLista = gson.fromJson(body, CobraClienteLista.class);

		return Arrays.asList(cobraClienteLista.data);

	}

	public class CobraClienteLista {
		public boolean hasMore;
		public int totalCount;
		public int limit;
		public int offset;
		public Customer[] data;
	}

	public Customer adicionaClienteNaPlataformaAPI(Customer cobra)
			throws JsonGenerationException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(cobra);
		json = adicionaClienteNaPlataformaAPI(json);

		Gson gson = new Gson();
		return gson.fromJson(json, Customer.class);
	}

	public String adicionaClienteNaPlataformaAPI(String json) {

		String body;
		String uri = (agente.getAmbiente() == DFAmbiente.PRODUCAO ? URL_PRODUCAO : URL_HOMOLOGACAO)
				.concat("/customers");

		try {

			Client client = Client.create();
			WebResource webResource = client.resource(uri);
			ClientResponse resposta = webResource.accept(MediaType.APPLICATION_JSON_TYPE)
					.header("access_token", agente.getTokenDeCobranca()).post(ClientResponse.class, json);

			Suporte.analisaRetornoHTTP(resposta);

			body = resposta.getEntity(String.class);

		} catch (Exception e) {
			throw new NegocioException(e.getMessage());
		}

		return body;

	}

	public Customer defineClienteDigital(Pessoa pessoa)
			throws JsonGenerationException, JsonMappingException, IOException {

		Endereco endereco;
		if (pessoa.isPessoaJuridica())
			endereco = pessoas.endereco(pessoa, TipoEndereco.COMERCIAL);
		else
			endereco = pessoas.endereco(pessoa, TipoEndereco.RESIDENCIAL);

		Customer cobraCliente = pessoaToCobraCliente(pessoa, endereco);

		List<Customer> lst = pesquisaPorCpfCnpj(pessoa.getCpfCnpjToString());
		if (lst != null && lst.size() > 0)
			cobraCliente.id = lst.get(0).id;
		else
			cobraCliente = adicionaClienteNaPlataformaAPI(cobraCliente);

		pessoas.guardar(pessoa, agente, cobraCliente);

		return cobraCliente;

	}

	public Customer atualizaClienteNaPlataformaAPI(Customer cobra, String id) {

		Customer cobraret = null;
		String uri = (agente.getAmbiente() == DFAmbiente.PRODUCAO ? URL_PRODUCAO : URL_HOMOLOGACAO)
				.concat("/customers/" + id);

		try {

			ObjectMapper mapper = new ObjectMapper();
			String json = mapper.writeValueAsString(cobra);

			Client client = Client.create();
			WebResource webResource = client.resource(uri);
			ClientResponse resposta = webResource.accept(MediaType.APPLICATION_JSON_TYPE)
					.header("access_token", agente.getTokenDeCobranca()).post(ClientResponse.class, json);

			Suporte.analisaRetornoHTTP(resposta);

			String body = resposta.getEntity(String.class);
			Gson gson = new Gson();
			cobraret = gson.fromJson(body, Customer.class);

		} catch (Exception e) {
			throw new NegocioException(e.getMessage());
		}

		return cobraret;

	}

	public ClienteDigital adicionaClienteNaPlataformaAPI(Pessoa pessoa) {
		return atualizaClienteNaPlataformaAPI(pessoa, "");
	}

	private CobraBoleto tituloToCobraBoleto(String clienteId_api, Titulo titulo) {

		CobraBoleto cobraBoleto = new CobraBoleto();
		cobraBoleto.customer = clienteId_api;
		cobraBoleto.billingType = TipoBillingType.BOLETO;
		cobraBoleto.value = titulo.getValor();
		cobraBoleto.dueDate = titulo.getVencimento();
		cobraBoleto.description = titulo.getDescricao();
		cobraBoleto.externalReference = Suporte.zerosAEsquerda(titulo.getId(), 6);
		cobraBoleto.postalService = titulo.getAgente().isCobrancaViaCorreio();
		// boleto.installmentCount = Valor de cada parcela (somente no caso de cobrança
		// parcelada)

		Interest interest = new Interest();
		interest.value = titulo.getAgente().getJuro();
		cobraBoleto.interest = interest;

		Fine fine = new Fine();
		fine.value = titulo.getAgente().getMulta();
		cobraBoleto.fine = fine;

		Discount discount = new Discount();
		if (titulo.getAbatimento() > 0) {
			discount.type = TipoDedesconto.FIXED;
			discount.value = titulo.getAbatimento();
		} else if (titulo.getAgente().getDesconto() > 0) {
			discount.type = TipoDedesconto.PERCENTAGE;
			discount.value = titulo.getAgente().getDesconto();
		}
		if (discount.value > 0)
			discount.dueDateLimitDays = titulo.getAgente().getTempoParaDesconto();
		cobraBoleto.discount = discount;

		return cobraBoleto;

	}

	@Override
	public ClienteDigital atualizaClienteNaPlataformaAPI(Pessoa pessoa, String id) {

		Customer cobraret = null;
		String uri = (agente.getAmbiente() == DFAmbiente.PRODUCAO ? URL_PRODUCAO : URL_HOMOLOGACAO)
				.concat("/customers");

		if (id != "")
			uri.concat("/customers/" + id);

		try {

			Endereco endereco = pessoas.endereco(pessoa, TipoEndereco.RESIDENCIAL);
			Customer cobraCliente = pessoaToCobraCliente(pessoa, endereco);

			ObjectMapper mapper = new ObjectMapper();
			String json = mapper.writeValueAsString(cobraCliente);

			Client client = Client.create();
			WebResource webResource = client.resource(uri);
			ClientResponse resposta = webResource.accept(MediaType.APPLICATION_JSON_TYPE)
					.header("access_token", agente.getTokenDeCobranca()).post(ClientResponse.class, json);

			Suporte.analisaRetornoHTTP(resposta);

			String body = resposta.getEntity(String.class);
			Gson gson = new Gson();
			cobraret = gson.fromJson(body, Customer.class);

			return pessoas.guardar(pessoa, agente, cobraret);

		} catch (Exception e) {
			throw new NegocioException(e.getMessage());
		}

	}

	private void criticaDadosDoTitulo(Titulo titulo) {

		int numero = SuporteData.diasEntreDatas(new Date(), titulo.getVencimento());

		if (numero < 0)
			throw new NegocioException("vencimento com data inferior à emissão");

		if (titulo.getValor() < agente.getValorMinimo())
			throw new NegocioException(
					"valor mínimo para título é de R$ " + Suporte.formataCurrency(agente.getValorMinimo()));
	}

	@Override
	public BoletoDigital adicionaBoletoNaPlataformaAPI(String clienteId_api, Titulo titulo) {
		return adicionaBoletoNaPlataformaAPI(clienteId_api, titulo, "");
	}

	@Override
	public BoletoDigital adicionaBoletoNaPlataformaAPI(String clienteId_api, Titulo titulo, String id) {

		String uri = (agente.getAmbiente() == DFAmbiente.PRODUCAO ? URL_PRODUCAO : URL_HOMOLOGACAO).concat("/payments");

		try {

			if (id == "")
				criticaDadosDoTitulo(titulo);
			else
				uri.concat("/customers/" + id);

			CobraBoleto cobraBoleto = tituloToCobraBoleto(clienteId_api, titulo);
			ObjectMapper mapper = new ObjectMapper();
			String json = mapper.writeValueAsString(cobraBoleto);

			Client client = Client.create();
			WebResource webResource = client.resource(uri);
			ClientResponse resposta = webResource.accept(MediaType.APPLICATION_JSON_TYPE)
					.header("access_token", agente.getTokenDeCobranca()).post(ClientResponse.class, json);

			Suporte.analisaRetornoHTTP(resposta);

			String body = resposta.getEntity(String.class);
			Gson gson = new Gson();
			cobraBoleto = gson.fromJson(body, CobraBoleto.class);

			return titulos.guardar(titulo, agente, cobraBoleto);

		} catch (Exception e) {
			throw new NegocioException(e.getMessage());
		}

	}

	public class CobraBoleto {

		public String id; // Identificador único da cobrança no Asaas
		public String customer; // Identificador único do cliente ao qual a cobrança pertence
		public TipoBillingType billingType;
		public Date dateCreated; // Data de criação da cobrança
		public String subscription; // Identificador único da assinatura (quando cobrança recorrente)
		public String installment; // Identificador único do parcelamento (quando cobrança parcelada)
		public Date dueDate; // Data de vencimento da cobrança
		public double value;// Valor da cobrança
		public double netValue; // Valor líquido da cobrança após desconto da tarifa do Asaas
		public String description; // Descrição da cobrança
		public String externalReference;// Campo livre para busca
		public String originalDueDate; // Vencimento original no ato da criação da cobrança
		public double originalValue; // Valor original da cobrança (preenchido quando paga com juros e multa)
		public double interestValue; // Valor calculado de juros e multa que deve ser pago após o vencimento
		public String confirmedDate; // Data de confirmação da cobrança (Somente para cartão de crédito)
		public Date paymentDate; // Data de liquidação da cobrança no Asaas
		public Date clientPaymentDate; // Data em que o cliente efetuou o pagamento do boleto
		public boolean deleted; // true quando a cobrança estiver excluída
		public boolean postalService; // Define se a cobrança será enviada via Correios
		public boolean anticipated; // Define se a cobrança foi antecipada ou está em processo de antecipação
		public Discount discount;
		public Interest interest;
		public Fine fine;

		public String bankSlipUrl; // URL para download do boleto
		public String invoiceUrl; // URL da fatura
		public String status; // Status da cobrança
		public String invoiceNumber; // Número da fatura

	}

	public class CobraBoletoUpdate {
		public TipoBillingType billingType;
		public String dueDate; // Data de vencimento da cobrança
		public double value;// Valor da cobrança
		public String description; // Descrição da cobrança
		public String externalReference;// Campo livre para busca
		public Discount discount;
		public Fine fine;
		public Interest interest;
		public boolean postalService; // Define se a cobrança será enviada via Correios
	}

	@Override
	public BoletoDigital restauraBoletoNaPlataforma(BoletoDigital boletoDigital) {

		try {

			if (!(boletoDigital.getStatus().equals("PENDING") || boletoDigital.getStatus().equals("OVERDUE")))
				throw new NegocioException("O status do boleto não permite ser atualizado");

			String uri = (agente.getAmbiente() == DFAmbiente.PRODUCAO ? URL_PRODUCAO : URL_HOMOLOGACAO)
					.concat("/payments");

			ClienteDigital clienteDigital = pessoas.clienteDigital(boletoDigital.getTitulo().getResponsavel(),
					boletoDigital.getAgente());
			CobraBoleto cobraBoleto = tituloToCobraBoleto(clienteDigital.getIdentificador(), boletoDigital.getTitulo());
			uri = uri + "/" + boletoDigital.getIdentificador() + "/restore";

			CobraBoletoUpdate cobraBoletoUpdate = new CobraBoletoUpdate();
			cobraBoletoUpdate.billingType = cobraBoleto.billingType;
			cobraBoletoUpdate.dueDate = SuporteData.formataDataToStr(boletoDigital.getTitulo().getVencimento(),
					"yyyy-MM-dd");
			cobraBoletoUpdate.value = Suporte.arredondaValor(cobraBoleto.value, 2);
			cobraBoletoUpdate.description = cobraBoleto.description;
			cobraBoletoUpdate.externalReference = cobraBoleto.externalReference;

			cobraBoletoUpdate.discount = new Discount();
			cobraBoletoUpdate.discount.dueDateLimitDays = 0;
			cobraBoletoUpdate.discount.value = Suporte.arredondaValor(boletoDigital.getTitulo().getDesconto(), 2);
			cobraBoletoUpdate.discount.type = TipoDedesconto.PERCENTAGE;

			cobraBoletoUpdate.fine = cobraBoleto.fine;
			cobraBoletoUpdate.interest = cobraBoleto.interest;
			cobraBoletoUpdate.postalService = cobraBoleto.postalService;

			ObjectMapper mapper = new ObjectMapper();
			String json = mapper.writeValueAsString(cobraBoletoUpdate);

			Client client = Client.create();
			WebResource webResource = client.resource(uri);
			ClientResponse resposta = webResource.accept(MediaType.APPLICATION_JSON_TYPE)
					.header("access_token", agente.getTokenDeCobranca()).post(ClientResponse.class, json.toString());

			Suporte.analisaRetornoHTTP(resposta);

			String body = resposta.getEntity(String.class);
			Gson gson = new Gson();
			CobraBoleto boleto = gson.fromJson(body, CobraBoleto.class);

			double desconto = Suporte.arredondaValor(boleto.value - boleto.netValue, 2);

			boletoDigital.setDataAtualizacao(new Date());
			boletoDigital.setUrlDaFatura(boleto.invoiceUrl);
			boletoDigital.setUrlDoArquivo(boleto.bankSlipUrl);

			boletoDigital.getTitulo().setValor(Suporte.arredondaValor(boleto.value, 2));
			boletoDigital.getTitulo().setSaldo(boletoDigital.getTitulo().getValor() - desconto);
			boletoDigital.getTitulo().setDesconto(desconto);
			boletoDigital.getTitulo().setDocumento(boleto.id);

			return titulos.guardar(boletoDigital);

		} catch (Exception e) {
			throw new NegocioException(e.getMessage());
		}

	}

	@Override
	public Pessoa pesquisaClienteNaPlataformaAPI(String id) {
		try {

			String uri = (agente.getAmbiente() == DFAmbiente.PRODUCAO ? URL_PRODUCAO : URL_HOMOLOGACAO)
					.concat("/customers/" + id);

			Client client = Client.create();
			WebResource webResource = client.resource(uri);
			ClientResponse resposta = webResource.accept(MediaType.APPLICATION_JSON_TYPE)
					.header("access_token", agente.getTokenDeCobranca()).get(ClientResponse.class);

			Suporte.analisaRetornoHTTP(resposta);

			String body = resposta.getEntity(String.class);
			Gson gson = new Gson();
			Customer cliente = gson.fromJson(body, Customer.class);

			Pessoa pessoa = new Pessoa();
			pessoa.setNome(cliente.name);
			pessoa.setDataCadastro(cliente.dateCreated);
			pessoa.setCelular(cliente.addressNumber);
			pessoa.setEmail(cliente.email);
			pessoa.setCelular(cliente.mobilePhone);
			if (cliente.cpfCnpj.length() > 11) {
				pessoa.setTipo(TipoPessoa.PJ);
				pessoa.setPJ(new PessoaJuridica());
				pessoa.getPJ().setCnpj(cliente.cpfCnpj);
			} else {
				pessoa.setTipo(TipoPessoa.PF);
				pessoa.setPF(new PessoaFisica());
				pessoa.getPF().setCpf(cliente.cpfCnpj);
			}

//			Endereco endereco = new Endereco();
//			endereco.setBairro(cliente.province);
//			endereco.setCep(cliente.postalCode);
//			endereco.setComplemento(cliente.complement);
//			endereco.setLogradouro(cliente.address);
//			endereco.setMunicipio(seguranca.);			
//			pessoa.setEnderecos(new ArrayList<>());

			return pessoa;

		} catch (Exception e) {
			throw new NegocioException(e.getMessage());
		}

	}

	@Override
	public List<BoletoDigital> listaBoletoDaPlataformaAPI(CondicaoFilter filtro) {

		Gson gson = null;
		BoletoDigital boleto;
		List<BoletoDigital> lista = null;
		Pessoa destino = pessoas.agente(agente);

		String uri = (agente.getAmbiente() == DFAmbiente.PRODUCAO ? URL_PRODUCAO : URL_HOMOLOGACAO).concat("/payments");

		try {

			if (filtro.getVencimentoInicial() != null) {
				uri = uri + "?dueDate%5Bge%5D="
						+ SuporteData.formataDataToStr(filtro.getVencimentoInicial(), "yyyy-MM-dd");
				if (filtro.getVencimentoFinal() != null)
					uri = uri + "&dueDate%5Ble%5D="
							+ SuporteData.formataDataToStr(filtro.getVencimentoFinal(), "yyyy-MM-dd");
			}

			if (filtro.getPagamentoInicial() != null) {
				uri = uri + "?paymentDate%5Bge%5D="
						+ SuporteData.formataDataToStr(filtro.getPagamentoInicial(), "yyyy-MM-dd");
				if (filtro.getPagamentoFinal() != null)
					uri = uri + "&paymentDate%5Ble%5D="
							+ SuporteData.formataDataToStr(filtro.getPagamentoFinal(), "yyyy-MM-dd");
			}

			Client client = Client.create();
			WebResource webResource = client.resource(uri);
			ClientResponse resposta = webResource.accept(MediaType.TEXT_PLAIN)
					.header("access_token", agente.getTokenDeCobranca()).get(ClientResponse.class);

			Suporte.analisaRetornoHTTP(resposta);

			String body = resposta.getEntity(String.class);
			JSONObject obj = new JSONObject(body);
			JSONArray jArray = obj.getJSONArray("data");

			lista = new ArrayList<>();
			for (int i = 0; i < jArray.length(); i++) {

				obj = jArray.getJSONObject(i);
				gson = new Gson();

				CobraBoleto cobra = gson.fromJson(obj.toString(), CobraBoleto.class);
				boleto = titulos.boletoDigitalporId(cobra.id, agente, agente.getAmbiente());

				if (boleto != null) {

					boleto.setStatus(cobra.status);
					Titulo titulo = boleto.getTitulo();
					if (titulo.getDataBaixa() == null
							&& (cobra.deleted || cobra.paymentDate != null || cobra.clientPaymentDate != null)) {

						if (cobra.deleted) {
							boleto.getTitulo().setDataCancelamento(new Date());
							boleto.getTitulo().setSaldo(0);
							boleto = titulos.guardar(boleto);
						} else {
							if (cobra.clientPaymentDate != null)
								boleto.getTitulo().setDataBaixa(cobra.clientPaymentDate);
							else if (cobra.paymentDate != null)
								boleto.getTitulo().setDataBaixa(cobra.paymentDate);

							boleto.getTitulo().setValor(cobra.value);
							boleto.getTitulo().setDesconto(cobra.value - cobra.netValue);
							boleto.getTitulo().setSaldo(0);
							boleto = titulos.compensar(boleto, destino);
						}
					}

					lista.add(boleto);

				} else if (cobra.status.equals("PENDING") || cobra.status.equals("OVERDUE")) {

					ClienteDigital cliente = pessoas.clienteDigital(cobra.customer, agente);
					if (cliente == null) {
						Pessoa pessoa = pesquisaClienteNaPlataformaAPI(cobra.customer);
						cliente = new ClienteDigital(pessoa, agente, cobra.customer);
						cliente.setPessoa(pessoa);
					}

					boleto = new BoletoDigital();
					boleto.setTitulo(new Titulo());
					boleto.setAgente(agente);
					boleto.setNumeroDaFatura(cobra.invoiceNumber);
					boleto.setDataCadastro(cobra.dateCreated);
					boleto.setIdentificador(cobra.id);
					boleto.setStatus(cobra.status);
					boleto.setUrlDaFatura(cobra.invoiceUrl);
					boleto.setUrlDoArquivo(cobra.bankSlipUrl);

					boleto.getTitulo().setResponsavel(cliente.getPessoa());
					boleto.getTitulo().setDocumento(cobra.id);
					boleto.getTitulo().setValor(cobra.value);
					boleto.getTitulo().setSaldo(cobra.netValue);
					boleto.getTitulo().setDesconto(cobra.value - cobra.netValue);
					boleto.getTitulo().setDescricao(cobra.description);
					boleto.getTitulo().setTipoDC(TipoTituloOrigem.RECEBER);
					boleto.getTitulo().setAgente(agente);
					boleto.getTitulo().setDataBoleto(cobra.dateCreated);
					boleto.getTitulo().setVencimento(cobra.dueDate);

					boleto = titulos.guardar(boleto);

					lista.add(boleto);

				}

			}

		} catch (Exception e) {
			throw new NegocioException(e.getMessage());
		}

		return lista;

	}

	@Override
	public boolean removerBoletoNaPlataforma(BoletoDigital boletoDigital) {

		try {

			if (!(boletoDigital.getStatus().equals("PENDING") || boletoDigital.getStatus().equals("OVERDUE")))
				throw new NegocioException("O status do boleto não permite ser removido");

			String uri = (agente.getAmbiente() == DFAmbiente.PRODUCAO ? URL_PRODUCAO : URL_HOMOLOGACAO)
					.concat("/payments/" + boletoDigital.getIdentificador());

			Client client = Client.create();
			WebResource webResource = client.resource(uri);
			ClientResponse resposta = webResource.accept(MediaType.APPLICATION_JSON_TYPE)
					.header("access_token", agente.getTokenDeCobranca()).delete(ClientResponse.class);

			Suporte.analisaRetornoHTTP(resposta);

			return resposta.getStatus() == 200;

		} catch (Exception e) {
			throw new NegocioException(e.getMessage());
		}

	}

	@Override
	public BoletoDigital confirmaRecebimentoNaPlataforma(BoletoDigital boletoDigital, Date data, double valor) {
		try {

			if (!(boletoDigital.getStatus().equals("PENDING") || boletoDigital.getStatus().equals("OVERDUE")))
				throw new NegocioException("O status do boleto não permite seu recebimento");

			String uri = (agente.getAmbiente() == DFAmbiente.PRODUCAO ? URL_PRODUCAO : URL_HOMOLOGACAO)
					.concat("/payments/" + boletoDigital.getIdentificador() + "/receiveInCash");

			HashMap<String, Object> pagamento = new HashMap<String, Object>();
			pagamento.put("paymentDate", SuporteData.formataDataToStr(data, "yyyy-MM-dd"));
			pagamento.put("value", valor);
			JSONObject json = new JSONObject(pagamento);

			Client client = Client.create();
			WebResource webResource = client.resource(uri);
			ClientResponse resposta = webResource.accept(MediaType.APPLICATION_JSON_TYPE)
					.header("access_token", agente.getTokenDeCobranca()).post(ClientResponse.class, json);

			Suporte.analisaRetornoHTTP(resposta);

			return null;

		} catch (Exception e) {
			throw new NegocioException(e.getMessage());
		}
	}

	@Override
	public BoletoDigital atualizaBoletoNaPlataforma(BoletoDigital boletoDigital) {

		try {

			if (!(boletoDigital.getStatus().equals("PENDING") || boletoDigital.getStatus().equals("OVERDUE")))
				throw new NegocioException("O status do boleto não permite ser atualizado");

			String uri = (agente.getAmbiente() == DFAmbiente.PRODUCAO ? URL_PRODUCAO : URL_HOMOLOGACAO)
					.concat("/payments");

			ClienteDigital clienteDigital = pessoas.clienteDigital(boletoDigital.getTitulo().getResponsavel(),
					boletoDigital.getAgente());

			if (clienteDigital == null)
				throw new NegocioException("Cliente digital não foi localizado no sistema");

			CobraBoleto cobraBoleto = tituloToCobraBoleto(clienteDigital.getIdentificador(), boletoDigital.getTitulo());
			uri = uri + "/" + boletoDigital.getIdentificador();

			CobraBoletoUpdate cobraBoletoUpdate = new CobraBoletoUpdate();
			cobraBoletoUpdate.billingType = cobraBoleto.billingType;
			cobraBoletoUpdate.dueDate = SuporteData.formataDataToStr(boletoDigital.getTitulo().getVencimento(),
					"yyyy-MM-dd");
			cobraBoletoUpdate.value = Suporte.arredondaValor(cobraBoleto.value, 2);
			cobraBoletoUpdate.description = cobraBoleto.description;
			cobraBoletoUpdate.externalReference = cobraBoleto.externalReference;
			cobraBoletoUpdate.discount = new Discount();
			cobraBoletoUpdate.discount.dueDateLimitDays = 0;
			cobraBoletoUpdate.discount.value = Suporte.arredondaValor(boletoDigital.getTitulo().getDesconto(), 2);
			cobraBoletoUpdate.discount.type = TipoDedesconto.PERCENTAGE;
			cobraBoletoUpdate.fine = cobraBoleto.fine;
			cobraBoletoUpdate.interest = cobraBoleto.interest;
			cobraBoletoUpdate.postalService = cobraBoleto.postalService;
			ObjectMapper mapper = new ObjectMapper();
			String json = mapper.writeValueAsString(cobraBoletoUpdate);

			Client client = Client.create();
			WebResource webResource = client.resource(uri);
			ClientResponse resposta = webResource.accept(MediaType.APPLICATION_JSON_TYPE)
					.header("access_token", agente.getTokenDeCobranca()).post(ClientResponse.class, json);

			Suporte.analisaRetornoHTTP(resposta);

			String body = resposta.getEntity(String.class);
			Gson gson = new Gson();
			CobraBoleto boleto = gson.fromJson(body, CobraBoleto.class);

			double desconto = Suporte.arredondaValor(boleto.value - boleto.netValue, 2);

			boletoDigital.setDataAtualizacao(new Date());
			boletoDigital.setUrlDaFatura(boleto.invoiceUrl);
			boletoDigital.setUrlDoArquivo(boleto.bankSlipUrl);

			boletoDigital.getTitulo().setValor(Suporte.arredondaValor(boleto.value, 2));
			boletoDigital.getTitulo().setSaldo(boletoDigital.getTitulo().getValor() - desconto);
			boletoDigital.getTitulo().setDesconto(desconto);
			boletoDigital.getTitulo().setDocumento(boleto.id);

			return titulos.guardar(boletoDigital);

		} catch (Exception e) {
			throw new NegocioException(e.getMessage());
		}

	}

}