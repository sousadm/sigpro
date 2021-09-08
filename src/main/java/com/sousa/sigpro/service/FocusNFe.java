package com.sousa.sigpro.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.fincatto.documentofiscal.DFAmbiente;
import com.fincatto.documentofiscal.DFUnidadeFederativa;
import com.fincatto.documentofiscal.nfe400.classes.NFRegimeTributario;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFMeioPagamento;
import com.sousa.sigpro.model.Aquisicao;
import com.sousa.sigpro.model.Endereco;
import com.sousa.sigpro.model.NotaFiscal;
import com.sousa.sigpro.model.NotaFiscalCorrecao;
import com.sousa.sigpro.model.NotaFiscalItem;
import com.sousa.sigpro.model.NotaFiscalPgto;
import com.sousa.sigpro.model.NotaFiscalVolume;
import com.sousa.sigpro.model.Pessoa;
import com.sousa.sigpro.model.PessoaJuridica;
import com.sousa.sigpro.model.tipo.TipoFocusStatus;
import com.sousa.sigpro.model.tipo.TipoPessoa;
import com.sousa.sigpro.util.Suporte;
import com.sousa.sigpro.util.SuporteData;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

public class FocusNFe implements Serializable {

	private static final long serialVersionUID = 1L;

	NotaFiscal nota;
	String ambiente_envio;
	String login;

	public FocusNFe(NotaFiscal nota, PessoaJuridica empresa) {
		try {
			login = nota.getFiscal().getAmbiente() == DFAmbiente.PRODUCAO ? empresa.getTokenProducao()
					: empresa.getTokenHomologacao();
			ambiente_envio = nota.getFiscal().getAmbiente() == DFAmbiente.PRODUCAO ? "https://api.focusnfe.com.br"
					: "http://homologacao.acrasnfe.acras.com.br";
		} catch (Exception e) {
			throw new NegocioException("Verifique definições de acesso à plataforma de emissão fiscal");
		}

		this.nota = nota;
	}

	public String getArquivoPdf() {
		return ambiente_envio + nota.getFiscal().getPdfFile();
	}

	public String getUrl() {
		return nota.getFiscal().getPdfFile();
	}

	public String getArquivoCorrecaoPdf() {
		return ambiente_envio + nota.getFiscal().getCaminhoPdfCarta();
	}

	public String getArquivoXml() {
		return ambiente_envio + nota.getFiscal().getXmlFile();
	}

	public String getArquivoXmlCancelamento() {
		return ambiente_envio + nota.getFiscal().getCaminhoXmlCancelamento();
	}

	public String getArquivoCorrecaoXml() {
		return ambiente_envio + nota.getFiscal().getCaminhoXmlCarta();
	}

	public String getUrlAmbiente() {
		return ambiente_envio;
	}

	public NotaFiscalCorrecao corrigirNFe(NotaFiscalCorrecao correcao) throws JSONException {
		return correcao;
	}

	public NotaFiscal enviarEmail(List<String> lista) throws JSONException {

		String server = ambiente_envio;
		String url = server
				.concat(nota.getFiscal().getModelo().getUrl() + "/" + String.valueOf(nota.getId()) + "/email");

		JSONObject json = new JSONObject();
		JSONArray listaEmails = new JSONArray();
		for (String email : lista) {
			listaEmails.put(email);
		}
		json.put("emails", listaEmails);

		Object config = new DefaultClientConfig();
		Client client = Client.create((ClientConfig) config);
		client.addFilter(new HTTPBasicAuthFilter(login, ""));

		WebResource request = client.resource(url);
		ClientResponse resposta = request.post(ClientResponse.class, json);

		Suporte.analisaRetornoHTTP(resposta);
		defineDadosRetorno(resposta);

		return nota;
	}

	public NotaFiscalCorrecao corrigirNFe(String justificativa) throws JSONException {

		justificativa = StringEscapeUtils.escapeJava(justificativa);
		String server = ambiente_envio;
		String url = server
				.concat(nota.getFiscal().getModelo().getUrl() + "/" + String.valueOf(nota.getId()) + "/carta_correcao");

		HashMap<String, String> correcao = new HashMap<String, String>();
		correcao.put("correcao", justificativa);
		JSONObject json = new JSONObject(correcao);

		Object config = new DefaultClientConfig();
		Client client = Client.create((ClientConfig) config);
		client.addFilter(new HTTPBasicAuthFilter(login, ""));

		WebResource request = client.resource(url);
		ClientResponse resposta = request.post(ClientResponse.class, json);

		Suporte.analisaRetornoHTTP(resposta);
		
		String body = resposta.getEntity(String.class);
		JSONObject retorno = new JSONObject(body);

		NotaFiscalCorrecao notaCorrecao = new NotaFiscalCorrecao();
		notaCorrecao.setJustificativa(justificativa);
		notaCorrecao.setDataEvento(new Date());

		if (!retorno.isNull("numero_carta_correcao"))
			notaCorrecao.setNumero(retorno.getLong("numero_carta_correcao"));
		if (!retorno.isNull("status_sefaz"))
			notaCorrecao.setStatusSefaz(retorno.getLong("status_sefaz"));
		if (!retorno.isNull("mensagem_sefaz"))
			notaCorrecao.setMensagemSefaz(retorno.getString("mensagem_sefaz"));
		if (!retorno.isNull("caminho_xml_carta_correcao"))
			notaCorrecao.setCaminhoXmlCarta(retorno.getString("caminho_xml_carta_correcao"));
		if (!retorno.isNull("caminho_pdf_carta_correcao"))
			notaCorrecao.setCaminhoPdfCarta(retorno.getString("caminho_pdf_carta_correcao"));

		return notaCorrecao;

	}

	public NotaFiscal cancelar(String valor) throws JSONException {

		String server = ambiente_envio;
		String url = server.concat(nota.getFiscal().getModelo().getUrl() + "/" + String.valueOf(nota.getId()));

		HashMap<String, String> justificativa = new HashMap<String, String>();
		justificativa.put("justificativa", valor);
		JSONObject json = new JSONObject(justificativa);

		Object config = new DefaultClientConfig();
		Client client = Client.create((ClientConfig) config);
		client.addFilter(new HTTPBasicAuthFilter(login, ""));

		WebResource request = client.resource(url);
		ClientResponse resposta = request.delete(ClientResponse.class, json);

		Suporte.analisaRetornoHTTP(resposta);
		defineDadosRetorno(resposta);

		return nota;

	}

	public NotaFiscal consultar() throws JSONException {

		String server = ambiente_envio;
		String url = server
				.concat(nota.getFiscal().getModelo().getUrl() + "/" + String.valueOf(nota.getId()) + "?completa=1");
		Object config = new DefaultClientConfig();
		Client client = Client.create((ClientConfig) config);
		client.addFilter(new HTTPBasicAuthFilter(login, ""));

		WebResource request = client.resource(url);
		ClientResponse resposta = request.get(ClientResponse.class);
		Suporte.analisaRetornoHTTP(resposta);
		defineDadosRetorno(resposta);

		return nota;
	}

	public void defineDadosRetorno(ClientResponse resposta) throws JSONException {

		String body = resposta.getEntity(String.class);
		JSONObject retorno = new JSONObject(body);

		if (!retorno.isNull("status")) {
			if (retorno.getString("status").equals("autorizado")) {
				nota.getFiscal().setStatus(TipoFocusStatus.AUTORIZADO);
				if (nota.getFiscal().getDataEmissao() == null)
					nota.getFiscal().setDataEmissao(new Date());
			} else if (retorno.getString("status").equals("cancelado")) {
				nota.getFiscal().setStatus(TipoFocusStatus.CANCELADO);
				if (nota.getFiscal().getDataCancelamento() == null)
					nota.getFiscal().setDataCancelamento(new Date());
			} else if (retorno.getString("status").equals("denegado"))
				nota.getFiscal().setStatus(TipoFocusStatus.DENEGADO);
			else if (retorno.getString("status").equals("erro_autorizacao")) {
				String msg = "";
				nota.getFiscal().setStatus(TipoFocusStatus.ERRO_SEFAZ);
				if (retorno.toString().contains("erros")) {
					JSONArray erros = new JSONArray(retorno.getString("erros"));
					for (int i = 0; i < erros.length(); i++) {
						JSONObject jsonObj = erros.getJSONObject(i);
						if (!jsonObj.isNull("codigo"))
							msg = "Código: " + jsonObj.getString("codigo");
						if (!jsonObj.isNull("mensagem"))
							msg = msg + " " + jsonObj.getString("mensagem");
						if (!jsonObj.isNull("correcao"))
							msg = msg + " " + jsonObj.getString("correcao");
					}
				}

				if (!retorno.isNull("mensagem_sefaz"))
					msg = msg + " " + retorno.getString("mensagem_sefaz");

				throw new NegocioException(msg);

			} else if (retorno.getString("status").equals("processando_autorizacao")) {
				throw new NegocioException("Processando autorização");
			}
		}

		if (!retorno.isNull("codigo") && (retorno.getInt("codigo") == 400 || retorno.getInt("codigo") == 403
				|| retorno.getInt("codigo") == 404 || retorno.getInt("codigo") == 422))
			throw new NegocioException("Erro: " + retorno.getString("mensagem"));

		if (!retorno.isNull("caminho_xml_nota_fiscal"))
			nota.getFiscal().setXmlFile(retorno.getString("caminho_xml_nota_fiscal"));

		if (!retorno.isNull("caminho_xml_carta_correcao"))
			nota.getFiscal().setCaminhoXmlCarta(retorno.getString("caminho_xml_carta_correcao"));

		if (!retorno.isNull("caminho_xml_cancelamento"))
			nota.getFiscal().setCaminhoXmlCancelamento(retorno.getString("caminho_xml_cancelamento"));

		if (!retorno.isNull("caminho_pdf_carta_correcao"))
			nota.getFiscal().setCaminhoPdfCarta(retorno.getString("caminho_pdf_carta_correcao"));

		if (!retorno.isNull("codigo_unico"))
			nota.getFiscal().setCodigoUnico(retorno.getLong("codigo_unico"));

		if (!retorno.isNull("status_sefaz"))
			nota.getFiscal().setStatusSefaz(retorno.getLong("status_sefaz"));

		if (!retorno.isNull("mensagem_sefaz"))
			nota.getFiscal().setMensagemSefaz(retorno.getString("mensagem_sefaz"));

		if (!retorno.isNull("numero"))
			nota.getFiscal().setNumero(retorno.getLong("numero"));

		if (!retorno.isNull("chave_nfe"))
			nota.getFiscal().setChave(Suporte.onlyNumbers(retorno.getString("chave_nfe")));

		if (!retorno.isNull("serie"))
			nota.getFiscal().setSerie(retorno.getInt("serie"));

		if (!retorno.isNull("caminho_danfe"))
			nota.getFiscal().setPdfFile(retorno.getString("caminho_danfe"));

		if (!retorno.isNull("url"))
			nota.getFiscal().setPdfFile(retorno.getString("url"));

		if (!retorno.isNull("codigo_verificacao"))
			nota.getFiscal().setCodigoVerificacao(retorno.getLong("codigo_verificacao"));

		if (!retorno.isNull("data_emissao"))
			nota.getFiscal().setDataEmissao(Suporte.stringFocusToDataHora(retorno.getString("data_emissao")));

	}

	private void addProdutoInformacaoAdicional(JSONArray informacoes, String tagName, String valor)
			throws JSONException {
		if (informacoes == null)
			informacoes = new JSONArray();
		JSONObject item = new JSONObject();
		item.put(tagName, valor);
		informacoes.put(item);
	}

	public NotaFiscal autorizar(Pessoa cliente, Endereco enderecoDoCliente, Endereco enderecoEmpresa)
			throws JSONException {

		if (enderecoEmpresa.getMunicipio() == null || enderecoEmpresa.getMunicipio().getId() == null)
			throw new NegocioException("verifique se o endereço da empresa está completo");

		if (enderecoDoCliente.getMunicipio() == null || enderecoDoCliente.getMunicipio().getId() == null)
			throw new NegocioException("verifique o município do cliente");

		JSONObject volume;
		JSONObject parcela;
		JSONObject json = new JSONObject();

		json.put("presenca_comprador", nota.getFiscal().getPresencaComprador().getCodigo());
		json.put("consumidor_final", nota.getFiscal().getConsumidorFinal().getCodigo());
		json.put("local_destino", nota.getFiscal().getLocalDestinoOperacao().getCodigo());

		if (Suporte.stringComValor(nota.getFiscal().getObservacao()))
			json.put("informacoes_adicionais_contribuinte",
					Suporte.removeCaracteresEspeciaisXML(nota.getFiscal().getObservacao()));
		json.put("natureza_operacao",
				Suporte.removeCaracteresEspeciaisXML(nota.getFiscal().getOperacao().getDescricao()));
		json.put("data_emissao", SuporteData.formataDataToStr(new Date(), "yyyy-MM-dd HH:mm:ss"));
		json.put("tipo_documento", nota.getFiscal().getTipo().getCodigo());

		json.put("finalidade_emissao", nota.getFiscal().getFinalidade().getCodigo());
		json.put("cnpj_emitente", nota.getEmpresa().getPJ().getCnpj());
		json.put("logradouro_emitente", Suporte.removeCaracteresEspeciaisXML(enderecoEmpresa.getLogradouro()));
		json.put("numero_emitente", String.valueOf(enderecoEmpresa.getNumero()));
		json.put("bairro_emitente", Suporte.removeCaracteresEspeciaisXML(enderecoEmpresa.getBairro()));
		json.put("municipio_emitente", enderecoEmpresa.getMunicipio().getDescricao());
		json.put("uf_emitente", enderecoEmpresa.getMunicipio().getUf().getCodigo());

		String ie = Suporte.onlyNumbers(nota.getEmpresa().getPJ().getIE());
		if (ie.length() > 0)
			json.put("inscricao_estadual_emitente", ie);

		json.put("regime_tributario_emitente", nota.getEmpresa().getPJ().getRegime().getCodigo());
		if (cliente.getTipo() == TipoPessoa.PJ) {

			if (cliente.getPJ().getTipoIE() == null)
				throw new NegocioException("Indicador da Inscrição Estadual do destinatário indefinido");
			// nota.getFiscal().getConsumidorFinal().getCodigo()

			json.put("indicador_inscricao_estadual_destinatario", cliente.getPJ().getTipoIE().getCodigo());
			if (Suporte.stringComValor(cliente.getPJ().getIE()))
				json.put("inscricao_estadual_destinatario", Suporte.onlyNumbers(cliente.getPJ().getIE()));

			if (!Suporte.stringComValor(cliente.getPJ().getCnpj()))
				throw new NegocioException("Obrigatório informar CNPJ ou CPF do destinatário");

			json.put("cnpj_destinatario", cliente.getPJ().getCnpj());

		} else {

			if (!Suporte.stringComValor(cliente.getPF().getCpf()))
				throw new NegocioException("Obrigatório informar CNPJ ou CPF do destinatário");

			json.put("cpf_destinatario", cliente.getPF().getCpf());
		}
		if (nota.getFiscal().getAmbiente() == DFAmbiente.PRODUCAO)
			json.put("nome_destinatario", Suporte.removeCaracteresEspeciaisXML(cliente.getNome()));
		else
			json.put("nome_destinatario", "NF-E EMITIDA EM AMBIENTE DE HOMOLOGACAO - SEM VALOR FISCAL");
		json.put("logradouro_destinatario", Suporte.removeCaracteresEspeciaisXML(enderecoDoCliente.getLogradouro()));
		json.put("numero_destinatario", String.valueOf(enderecoDoCliente.getNumero()));
		json.put("bairro_destinatario", Suporte.removeCaracteresEspeciaisXML(enderecoDoCliente.getBairro()));
		json.put("municipio_destinatario",
				Suporte.removeCaracteresEspeciaisXML(enderecoDoCliente.getMunicipio().getDescricao()));
		json.put("uf_destinatario", enderecoDoCliente.getMunicipio().getUf().getCodigo());

		json.put("data_entrada_saida", SuporteData.formataDataToStr(new Date(), "yyyy-MM-dd HH:mm:ss"));
		json.put("nome_emitente", Suporte.removeCaracteresEspeciaisXML(nota.getEmpresa().getNome()));
		json.put("nome_fantasia_emitente",
				Suporte.removeCaracteresEspeciaisXML(nota.getEmpresa().getPJ().getFantasia()));
		json.put("cep_emitente", enderecoEmpresa.getCep());
		if (cliente.getCelular().length() >= 8)
			json.put("telefone_destinatario", Suporte.onlyNumbers(cliente.getCelular()));
		else if (cliente.getFone().length() >= 8)
			json.put("telefone_destinatario", Suporte.onlyNumbers(cliente.getFone()));
		json.put("pais_destinatario", "Brasil");

		if (Suporte.stringComValor(Suporte.onlyNumbers(enderecoDoCliente.getCep())))
			json.put("cep_destinatario", Suporte.onlyNumbers(enderecoDoCliente.getCep()));

		json.put("valor_desconto", String.valueOf(nota.getValorDesconto()));
		json.put("valor_frete", String.valueOf(nota.getValorFrete()));
		json.put("valor_seguro", String.valueOf(nota.getValorSeguro()));
		json.put("valor_total", String.valueOf(nota.getValorTotal()));
		json.put("valor_produtos", String.valueOf(nota.getValorProduto()));
		json.put("modalidade_frete", nota.getFiscal().getModalidadeFrete().getCodigo());

		if (nota.getItems().size() > 0) {

			JSONArray items = new JSONArray();
			JSONArray informacoes = null;

			int indice = 0;
			for (NotaFiscalItem item : nota.getItems()) {

				JSONObject nfeItem = new JSONObject();

				if (item.getProd().getFiscal().getOrigem() == null)
					throw new NegocioException("origem do produto indefinida: " + item.getProd().getNome());

				if (item.getProd().getCodigoNCM() == null)
					throw new NegocioException("NCM do produto indefinido: " + item.getProd().getNome());
				indice++;
				nfeItem.put("numero_item", String.valueOf(indice));
				if (Suporte.stringComValor(item.getProd().getSku()))
					nfeItem.put("codigo_produto", String.valueOf(item.getProd().getSku()));
				else
					nfeItem.put("codigo_produto", String.valueOf(item.getProd().getId()));

				nfeItem.put("descricao", Suporte.removeCaracteresEspeciaisXML(item.getProd().getNome()));
				nfeItem.put("cfop", String.valueOf(item.getCfop().getId()));
				nfeItem.put("unidade_comercial", item.getUnidade().getCodigo());
				nfeItem.put("quantidade_comercial", String.valueOf(item.getQuantidade()));
				nfeItem.put("valor_unitario_comercial", String.valueOf(item.getValorUnitario()));
				nfeItem.put("valor_unitario_tributavel", String.valueOf(item.getValorUnitario()));
				nfeItem.put("valor_desconto", String.valueOf(item.getValorDescontoTotal()));
				nfeItem.put("unidade_tributavel", item.getUnidade().getCodigo());
				nfeItem.put("codigo_ncm", String.valueOf(item.getProd().getCodigoNCM()));
				nfeItem.put("quantidade_tributavel", String.valueOf(item.getQuantidade()));
				nfeItem.put("valor_bruto", String.valueOf(item.getValorProduto()));
				if (item.getIcms().getCst() != null) {
					nfeItem.put("icms_situacao_tributaria", item.getIcms().getCst().getCodigo());
				} else {
					nfeItem.put("icms_situacao_tributaria", item.getIcms().getCstSN().getCodigo());
				}
				nfeItem.put("icms_origem", item.getProd().getFiscal().getOrigem().getCodigo());

				if (item.getPis() != null && item.getPis().getCstPIS() != null) {
					nfeItem.put("pis_situacao_tributaria", item.getPis().getCstPIS().getCodigo());
					nfeItem.put("pis_base_calculo", String.valueOf(item.getPis().getBasePIS()));
					nfeItem.put("pis_aliquota_porcentual", String.valueOf(item.getPis().getAliquotaPIS()));
					nfeItem.put("pis_valor", String.valueOf(item.getPis().getValorPIS()));
				}

				if (item.getCofins() != null && item.getCofins().getCstCofins() != null) {
					nfeItem.put("cofins_situacao_tributaria", item.getCofins().getCstCofins().getCodigo());
					nfeItem.put("cofins_base_calculo", String.valueOf(item.getCofins().getBaseCofins()));
					nfeItem.put("cofins_aliquota_porcentual", String.valueOf(item.getCofins().getAliquotaCofins()));
					nfeItem.put("cofins_valor", String.valueOf(item.getCofins().getValorCofins()));
				}

				if (Suporte.stringComValor(item.getProd().getCodigoEAN()))
					nfeItem.put("codigo_barras_comercial", item.getProd().getCodigoEAN());
				if (Suporte.stringComValor(item.getProd().getCodigoEanTrib()))
					nfeItem.put("codigo_barras_tributavel", item.getProd().getCodigoEanTrib());
				if (Suporte.stringComValor(item.getProd().getCodigoCEST()))
					nfeItem.put("cest", String.valueOf(item.getProd().getCodigoCEST()));

				if (item.getIcms() != null && item.getIcms().getAliquotaICMS() > 0) {
					nfeItem.put("icms_modalidade_base_calculo", item.getIcms().getModalidade().getCodigo());
					nfeItem.put("icms_aliquota", String.valueOf(item.getIcms().getAliquotaICMS()));
					nfeItem.put("icms_base_calculo", String.valueOf(item.getValorProduto()));
					nfeItem.put("icms_valor", String.valueOf(item.getIcms().getValorICMS()));
				}

				if (item.getPedidoCompra() != null)
					nfeItem.put("pedido_compra", String.valueOf(item.getPedidoCompra()));

				if (item.getPedidoCompraItem() != null)
					nfeItem.put("numero_item_pedido_compra", String.valueOf(item.getPedidoCompraItem()));

				if (Suporte.stringComValor(item.getPedidoCompraMaterial())
						&& Suporte.stringComValor(cliente.getCliente().getTagFiscalProduto())) {
					addProdutoInformacaoAdicional(informacoes, cliente.getCliente().getTagFiscalProduto(),
							item.getProd().getCodigoEAN());
				}

				if (informacoes != null && informacoes.length() > 0)
					nfeItem.put("informacoes_adicionais_item", informacoes);

				items.put(nfeItem);

			}

			json.put("items", items);

		}

		/* Nota Fiscal Referenciada */
		List<Aquisicao> lstref = new ArrayList<>();
		for (NotaFiscalItem item : nota.getItems()) {

			JSONArray referenciadas = new JSONArray();

			if (nota.getFiscal().getOperacao().isDevolucao() && item.getAquisicaoItem() != null) {
				Aquisicao aqui = item.getAquisicaoItem().getAquisicao();
				if (lstref.indexOf(aqui) < 0) {
					lstref.add(aqui);
				}
			}

			if (lstref.size() > 0) {
				for (Aquisicao compra : lstref) {
					JSONObject notaref = new JSONObject();
					notaref.put("chave_nfe", compra.getNota().getChave());
					referenciadas.put(notaref);
				}
				json.put("notas_referenciadas", referenciadas);
			}
		}

		/* parcelas de pagamento */
		JSONArray pagamentos = new JSONArray();
		if (nota.getFiscal().getOperacao().isDevolucao()) {
			parcela = new JSONObject();
			parcela.put("forma_pagamento", NFMeioPagamento.SEM_PAGAMENTO.getCodigo());
			pagamentos.put(parcela);
		} else if (nota.getPgtos().size() > 0) {

			for (NotaFiscalPgto pgto : nota.getPgtos()) {
				parcela = new JSONObject();
				parcela.put("forma_pagamento", pgto.getMeioPgto().getCodigo());
				parcela.put("valor_pagamento", String.valueOf(pgto.getValor()));
				if (pgto.isMeioDePagamentoCartao()) {
					parcela.put("tipo_integracao", "2");
					// parcela.put("cnpj_credenciadora", "CNPJ");
					parcela.put("bandeira_operadora", pgto.getOperadora().getCodigo());
				}
				pagamentos.put(parcela);
			}
		}
		json.put("formas_pagamento", pagamentos);

		/* volumes */
		if (nota.getVolumes().size() > 0) {

			JSONArray volumes = new JSONArray();
			volume = new JSONObject();

			for (NotaFiscalVolume item : nota.getVolumes()) {
				volume.put("quantidade", String.valueOf(item.getQuantidade()));
				volume.put("peso_liquido", String.valueOf(item.getPesoLiquido()));
				volume.put("peso_bruto", String.valueOf(item.getPesoBruto()));
				if (item.getEspecie() != null && item.getEspecie().length() > 0)
					volume.put("especie", item.getEspecie());
				if (item.getMarca() != null && item.getMarca().length() > 0)
					volume.put("marca", item.getMarca());
				if (item.getNumero() != null && item.getNumero().length() > 0)
					volume.put("numero", item.getNumero());
				volumes.put(volume);
			}
			json.put("volumes", volumes);
		}

		json.put("icms_base_calculo", String.valueOf(nota.getBaseICMS()));
		json.put("icms_valor_total", String.valueOf(nota.getValorICMS()));

		String server = ambiente_envio;
		String url = server.concat(nota.getFiscal().getModelo().getUrl() + "/?ref=" + String.valueOf(nota.getId()));

		Object config = new DefaultClientConfig();
		Client client = Client.create((ClientConfig) config);
		client.addFilter(new HTTPBasicAuthFilter(login, ""));

		WebResource request = client.resource(url);
		ClientResponse resposta = request.post(ClientResponse.class, json);

		Suporte.analisaRetornoHTTP(resposta);

		if (resposta.toString().contains("returned a response status of 402 Payment Required"))
			throw new NegocioException("Erro no servidor de envio: contactar suporte");

		defineDadosRetorno(resposta);

		// nota.getFiscal().setDataEmissao(new Date());

		return nota;

	}

	public NotaFiscal autorizarNFSe(Pessoa cliente, Endereco enderecoCliente, Endereco enderecoEmpresa)
			throws JSONException, NullPointerException {

		if (enderecoEmpresa.getMunicipio() == null || enderecoEmpresa.getMunicipio().getId() == null)
			throw new NegocioException("verifique se o endereço da empresa está completo");

		if (enderecoCliente.getMunicipio() == null || enderecoCliente.getMunicipio().getId() == null)
			throw new NegocioException("verifique o município do cliente");

		String server = ambiente_envio;
		String url = server.concat(nota.getFiscal().getModelo().getUrl() + "/?ref=" + String.valueOf(nota.getId()));

		Object config = new DefaultClientConfig();
		Client client = Client.create((ClientConfig) config);
		client.addFilter(new HTTPBasicAuthFilter(login, ""));

		/* Aqui são criados as hash's que receberão os dados da nota. */
		HashMap<String, Object> nfse = new HashMap<String, Object>();
		HashMap<String, Object> prestador = new HashMap<String, Object>();
		HashMap<String, Object> tomador = new HashMap<String, Object>();
		HashMap<String, Object> TomadorEndereco = new HashMap<String, Object>();
		HashMap<String, Object> servico = new HashMap<String, Object>();

		nfse.put("data_emissao", SuporteData.formataDataToStr(new Date(), "yyyy-MM-dd"));
		nfse.put("natureza_operacao", nota.getIssqn().getNaturezaOperacaoNFSe().getCodigo());
		prestador.put("cnpj", nota.getEmpresa().getPJ().getCnpj());
		prestador.put("inscricao_municipal", Suporte.onlyNumbers(nota.getEmpresa().getPJ().getIM()));
		prestador.put("codigo_municipio", enderecoEmpresa.getMunicipio().getId());
		if (nota.getIssqn().getCodigoMunicipio().getUf() == DFUnidadeFederativa.SP) {
			prestador.put("tributacao_rps", Suporte.onlyNumbers(nota.getIssqn().getTributacaoRPS().getCodigo()));
		} else {
			prestador.put("incentivador_cultural", nota.getIssqn().isIncentivadorCultural());
			prestador.put("regime_especial_tributacao", nota.getIssqn().getRegimeEspecial().getCodigo());
			prestador.put("optante_simples_nacional",
					nota.getEmpresa().getPJ().getRegime() != NFRegimeTributario.NORMAL);
		}

		// Tomador
		tomador.put("email", Suporte.substring(cliente.getEmail(), 0, 80));
		if (cliente.getTipo() == TipoPessoa.PJ) {
			if (cliente.getPJ().getCnpj() == null)
				throw new NegocioException("Cliente sem CNPJ cadastrado");
			tomador.put("cnpj", cliente.getPJ().getCnpj());
			tomador.put("razao_social", Suporte.substring(cliente.getNome(), 0, 115));
			if (cliente.getPJ().getIM() != null)
				tomador.put("inscricao_municipal", cliente.getPJ().getIM());
		} else {
			if (cliente.getPF().getCpf() == null)
				throw new NegocioException("Cliente sem CNPJ cadastrado");
			tomador.put("cpf", cliente.getPF().getCpf());
			tomador.put("razao_social", cliente.getNome());
		}
		if (nota.getIssqn().getCodigoMunicipio().getUf() != DFUnidadeFederativa.SP) {
			if (cliente.getFone() != null)
				tomador.put("telefone", Suporte.substring(cliente.getFone(), 0, 11));
		}

		// Tomador Endereço
		TomadorEndereco.put("logradouro", Suporte.substring(enderecoCliente.getLogradouro(), 0, 125));
		TomadorEndereco.put("bairro", Suporte.substring(enderecoCliente.getBairro(), 0, 60));
		TomadorEndereco.put("cep", enderecoCliente.getCep());
		TomadorEndereco.put("codigo_municipio", enderecoCliente.getMunicipio().getId());
		TomadorEndereco.put("numero", enderecoCliente.getNumero());
		TomadorEndereco.put("uf", enderecoCliente.getMunicipio().getUf().getCodigo());
		if (nota.getIssqn().getCodigoMunicipio().getUf() == DFUnidadeFederativa.SP)
			TomadorEndereco.put("tipo_logradouro", "RUA");

		servico.put("codigo_municipio", nota.getIssqn().getCodigoMunicipio().getId());
		servico.put("discriminacao",
				Suporte.removeCaracteresEspeciaisXML(Suporte.substring(nota.getIssqn().getDiscriminacao(), 0, 2000)));
		servico.put("aliquota", nota.getIssqn().getAliquotaISSQN());
		servico.put("iss_retido", nota.getIssqn().isRetencaoIss());
		servico.put("valor_servicos", nota.getIssqn().getValorServicos());
		if (nota.getIssqn().getItemListaServicos().length() > 0)
			servico.put("item_lista_servico",
					Long.valueOf(Suporte.onlyNumbers(nota.getIssqn().getItemListaServicos())));
		if (nota.getIssqn().getCodigoCnae().length() > 0)
			servico.put("codigo_cnae", Long.valueOf(Suporte.onlyNumbers(nota.getIssqn().getCodigoCnae())));

		if (nota.getIssqn().getCodigoTributarioMunicipio().length() > 0)
			servico.put("codigo_tributario_municipio",
					Long.valueOf(Suporte.onlyNumbers(nota.getIssqn().getCodigoTributarioMunicipio())));

		/*
		 * Depois de fazer o input dos dados, são criados os objetos JSON já com os
		 * valores das hash's.
		 */
		JSONObject json = new JSONObject(nfse);
		JSONObject JsonPrestador = new JSONObject(prestador);
		JSONObject JsonTomador = new JSONObject(tomador);
		JSONObject JsonTomadorEndereco = new JSONObject(TomadorEndereco);
		JSONObject JsonServico = new JSONObject(servico);

		/*
		 * Aqui adicionamos os objetos JSON nos campos da API como array no JSON
		 * principal.
		 */
		json.accumulate("prestador", JsonPrestador);
		json.accumulate("tomador", JsonTomador);
		JsonTomador.accumulate("endereco", JsonTomadorEndereco);
		json.accumulate("servico", JsonServico);

		WebResource request = client.resource(url);
		ClientResponse resposta = request.post(ClientResponse.class, json);
		Suporte.analisaRetornoHTTP(resposta);

//		if (resposta.toString().contains("returned a response status of 402 Payment Required"))
//			throw new NegocioException("Erro no servidor de envio: contactar suporte");

		defineDadosRetorno(resposta);

		return nota;
	}


}