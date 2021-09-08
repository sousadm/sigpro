package com.sousa.sigpro.nfe;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import com.fincatto.documentofiscal.DFUnidadeFederativa;
import com.fincatto.documentofiscal.nfe.NFeConfig;

public class ConfigNFe extends NFeConfig {

	// @Inject
	// private ParametroMail mail;

	private KeyStore keyStoreCertificado = null;
	private KeyStore keyStoreCadeia = null;
	private SuporteDados dados;

	public ConfigNFe() {
		// dados = new SuporteDados(mail);
	}

	public ConfigNFe(SuporteDados dados) {
		this.dados = dados;
	}

	@Override
	public DFUnidadeFederativa getCUF() {
		return DFUnidadeFederativa.CE;
	}

	@Override
	public String getCertificadoSenha() {
		return dados.getSenha();
	}

	@Override
	public String getCadeiaCertificadosSenha() {
		return dados.getSenha();
	}

	@Override
	public KeyStore getCertificadoKeyStore() throws KeyStoreException {
		if (this.keyStoreCertificado == null) {
			this.keyStoreCertificado = KeyStore.getInstance("PKCS12");
			try (InputStream certificadoStream = new FileInputStream(dados.getCertificado())) {
				this.keyStoreCertificado.load(certificadoStream, this.getCertificadoSenha().toCharArray());
			} catch (CertificateException | NoSuchAlgorithmException | IOException e) {
				this.keyStoreCadeia = null;
				throw new KeyStoreException("Nao foi possibel montar o KeyStore com a cadeia de certificados", e);
			}
		}
		return this.keyStoreCertificado;
	}

	@Override
	public KeyStore getCadeiaCertificadosKeyStore() throws KeyStoreException {
		if (this.keyStoreCadeia == null) {
			this.keyStoreCadeia = KeyStore.getInstance("JKS");
			try (InputStream cadeia = new FileInputStream(dados.getCertificadoCacerts())) {
				this.keyStoreCadeia.load(cadeia, this.getCadeiaCertificadosSenha().toCharArray());
			} catch (CertificateException | NoSuchAlgorithmException | IOException e) {
				this.keyStoreCadeia = null;
				throw new KeyStoreException("Nao foi possibel montar o KeyStore com o certificado", e);
			}
		}
		return this.keyStoreCadeia;
	}
}