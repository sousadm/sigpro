package com.sousa.sigpro.nfe;

import com.sousa.sigpro.controller.parametro.ParametroMail;
import com.sousa.sigpro.util.Suporte;

public class SuporteDados {

	private String senha;
	private String certificado;
	private String producaoCacerts;
	private String homologacaoCacerts;
	private String certificadoCacerts;

	public SuporteDados(ParametroMail mail) {
		String empresa = (String) Suporte.getAtributoDaSessao("empresa");
		senha = mail.getCertificadoSenha();
		certificado = Suporte.uploadLocal + mail.getCertificado();
		producaoCacerts = Suporte.uploadLocal + empresa + "_producao.cacerts";
		homologacaoCacerts = Suporte.uploadLocal + empresa + "_homologacao.cacerts";
		if (mail.isAmbienteProducao()) {
			certificadoCacerts = producaoCacerts;
		} else {
			certificadoCacerts = homologacaoCacerts;
		}
	}

	public SuporteDados(String empresa, boolean producao) {
		String path = "empresa\\" + empresa + "\\";
		certificado = path + "vlc12395.pfx";
		producaoCacerts = path + empresa + "_producao.cacerts";
		homologacaoCacerts = path + empresa + "_homologacao.cacerts";
		senha = "vlc12395";
		if (producao) {
			certificadoCacerts = producaoCacerts;
		} else {
			certificadoCacerts = homologacaoCacerts;
		}
	}

	public String getSenha() {
		return senha;
	}

	public String getCertificado() {
		return certificado;
	}

	public String getProducaoCacerts() {
		return producaoCacerts;
	}

	public String getHomologacaoCacerts() {
		return homologacaoCacerts;
	}

	public String getCertificadoCacerts() {
		return certificadoCacerts;
	}
}