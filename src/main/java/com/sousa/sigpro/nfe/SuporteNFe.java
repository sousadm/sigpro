package com.sousa.sigpro.nfe;

import com.fincatto.documentofiscal.DFModelo;
import com.fincatto.documentofiscal.DFUnidadeFederativa;
import com.fincatto.documentofiscal.nfe400.classes.lote.envio.NFLoteEnvio;
import com.fincatto.documentofiscal.nfe400.classes.lote.envio.NFLoteEnvioRetornoDados;
import com.fincatto.documentofiscal.nfe400.classes.statusservico.consulta.NFStatusServicoConsultaRetorno;
import com.fincatto.documentofiscal.nfe400.webservices.WSFacade;
import com.sousa.sigpro.controller.parametro.ParametroMail;

public class SuporteNFe {

	private SuporteDados dados;
	private ConfigNFe config;

	public SuporteNFe(ParametroMail mail) {
		dados = new SuporteDados(mail);
		config = new ConfigNFe(dados);
	}

	public SuporteNFe(String empresa, boolean producao) throws Exception {
		dados = new SuporteDados(empresa, producao);
		config = new ConfigNFe(dados);
	}

	public String xmlNotaRecuperadaAssinada(String xmlNotaRecuperada) throws Exception {
		return null;
//		return new AssinaturaDigital(config).assinarDocumento(xmlNotaRecuperada);
	}

	public NFStatusServicoConsultaRetorno statusServico(DFUnidadeFederativa UF, DFModelo modelo) throws Exception {
		return new WSFacade(config).consultaStatus(UF, modelo);
	}

	public NFLoteEnvioRetornoDados envioLoteRetorno(NFLoteEnvio lote) throws Exception {
		return new WSFacade(config).enviaLote(lote);
	}

	public void montarCadeiaCertificado() throws Exception {

		// File file;

		// file = new File(dados.getProducaoCacerts());
		// if (!file.exists()) {
		// FileUtils.writeByteArrayToFile(new File(dados.getProducaoCacerts()),
		// NFGeraCadeiaCertificados.geraCadeiaCertificados(DFAmbiente.PRODUCAO,
		// dados.getSenha()));
		// }

		// file = new File(dados.getHomologacaoCacerts());
		// if (!file.exists()) {
		// FileUtils.writeByteArrayToFile(new File(dados.getHomologacaoCacerts()),
		// FGeraCadeiaCertificados.geraCadeiaCertificados(DFAmbiente.HOMOLOGACAO,
		// dados.getSenha()));
		// }

	}

	public ConfigNFe getConfig() {
		return config;
	}
}