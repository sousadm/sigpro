package com.sousa.sigpro.controller.parametro;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import com.sousa.sigpro.model.Parametro;
import com.sousa.sigpro.model.tipo.TipoGraficoModelo;
import com.sousa.sigpro.model.tipo.TipoTheme;
import com.sousa.sigpro.repository.Parametros;
import com.sousa.sigpro.security.Seguranca;
import com.sousa.sigpro.util.Criptografia;
import com.sousa.sigpro.util.Suporte;

public class ParametroMail implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Parametros parametros;
	@Inject
	private Seguranca seguranca;

	public static final String TYPE_TEXT_PLAIN = "text/plain";
	public static final String TYPE_TEXT_HTML = "text/html";

	private String smtpHostMail = "";
	private String smtpPortMail = "";
	private boolean smtpAuth = false;
	private boolean smtpStartSsl = false;
	private boolean smtpStarttls = false;
	private boolean smtpSslTrust = false;
	private String userMail = "";
	private String userMailContabilidade = "";
	private String pasword = "";
	private String subjectMail = "";
	private String bodyMail = "";
	private Map<String, String> toMailsUsers;
	private List<String> fileMails;
	private String charsetMail = "";
	private String typeTextMail = "";
	private String smtpGateway = "";
	private String msgMailPedido = "";
	private String assinatura = "";

	private String certificado = "";
	private String certificadoSenha = "";
	private String logomarca = "";
	private boolean ambienteProducao = false;

	private TipoTheme theme = TipoTheme.sam;
	private String email = "";
	private TipoGraficoModelo modelo;

	public ParametroMail() {

	}

	public void ler() {
		List<Parametro> lst = null;
		charsetMail = "charset=UTF-8"; // "ISO-8859-1";
		typeTextMail = TYPE_TEXT_HTML;

		if (seguranca.getPessoaLogado().getUsuario().isPermiteEmailProprio())
			lst = parametros.listaPorGrupo("MAIL", seguranca.getPessoaLogado());

		if (lst == null || lst.size() == 0)
			lst = parametros.listaPorGrupo("MAIL");

		for (Parametro item : lst) {
			if (item.getCodigo().equals("userMail")) {
				userMail = item.getValor();
			} else if (item.getCodigo().equals("smtpPortMail")) {
				smtpPortMail = item.getValor();
			} else if (item.getCodigo().equals("userMailContabilidade")) {
				userMailContabilidade = item.getValor();
			} else if (item.getCodigo().equals("smtpHostMail")) {
				smtpHostMail = item.getValor();
			} else if (item.getCodigo().equals("smtpStartSsl")) {
				smtpStartSsl = Boolean.parseBoolean(item.getValor());
			} else if (item.getCodigo().equals("smtpSslTrust")) {
				smtpSslTrust = Boolean.parseBoolean(item.getValor());
			} else if (item.getCodigo().equals("smtpStarttls")) {
				smtpStarttls = Boolean.parseBoolean(item.getValor());
			} else if (item.getCodigo().equals("smtpAuth")) {
				smtpAuth = Boolean.parseBoolean(item.getValor());
			} else if (item.getCodigo().equals("smtpGateway")) {
				smtpGateway = item.getValor();
			} else if (item.getCodigo().equals("msgMailPedido")) {
				msgMailPedido = item.getValor();
			} else if (item.getCodigo().equals("pasword")) {
				pasword = Criptografia.decrypt(item.getValor());
			} else if (item.getCodigo().equals("certificado")) {
				certificado = item.getValor();
			} else if (item.getCodigo().equals("certificadoSenha")) {
				certificadoSenha = Criptografia.decrypt(item.getValor());
			} else if (item.getCodigo().equals("logomarca")) {
				logomarca = item.getValor();
			} else if (item.getCodigo().equals("ambienteProducao")) {
				ambienteProducao = Boolean.parseBoolean(item.getValor());
			} else if (item.getCodigo().equals("assinatura")) {
				assinatura = item.getValor();
			}

			lst = parametros.listaPorGrupo(Suporte.USUARIO_LOGADO);
			for (Parametro param : lst) {
				if (param.getCodigo().equals("theme"))
					theme = TipoTheme.valueOf(param != null ? param.getValor() : "sunny");
				else if (param.getCodigo().equals("userMail"))
					email = param.getValor();
				else if (param.getCodigo().equals("modelo")) {
					modelo = TipoGraficoModelo.valueOf(param.getValor());
				}
			}
		}

	}

	public void gravar() {
		List<Parametro> lista = new ArrayList<>();
		lista.add(new Parametro("smtpStartSsl", "MAIL", String.valueOf(smtpStartSsl), seguranca.getPessoaLogado()));
		lista.add(new Parametro("smtpAuth", "MAIL", String.valueOf(smtpAuth), seguranca.getPessoaLogado()));
		lista.add(new Parametro("smtpStarttls", "MAIL", String.valueOf(smtpStarttls), seguranca.getPessoaLogado()));
		lista.add(new Parametro("smtpSslTrust", "MAIL", String.valueOf(smtpSslTrust), seguranca.getPessoaLogado()));
		lista.add(new Parametro("ambienteProducao", "MAIL", String.valueOf(ambienteProducao),
				seguranca.getPessoaLogado()));
		lista.add(new Parametro("smtpPortMail", "MAIL", smtpPortMail, seguranca.getPessoaLogado()));
		lista.add(new Parametro("smtpGateway", "MAIL", smtpGateway, seguranca.getPessoaLogado()));
		lista.add(new Parametro("smtpHostMail", "MAIL", smtpHostMail, seguranca.getPessoaLogado()));
		lista.add(new Parametro("userMail", "MAIL", userMail, seguranca.getPessoaLogado()));
		lista.add(new Parametro("userMailContabilidade", "MAIL", userMailContabilidade, seguranca.getPessoaLogado()));

		if (pasword != null && pasword.length() > 0)
			lista.add(new Parametro("pasword", "MAIL", Criptografia.encrypt(pasword), seguranca.getPessoaLogado()));
		if (assinatura != null && assinatura.length() > 0)
			lista.add(new Parametro("assinatura", "MAIL", assinatura, seguranca.getPessoaLogado()));
		lista.add(new Parametro("certificado", "MAIL", certificado, seguranca.getPessoaLogado()));
		if (certificadoSenha != null && certificadoSenha.length() > 0)
			lista.add(new Parametro("certificadoSenha", "MAIL", Criptografia.encrypt(certificadoSenha),
					seguranca.getPessoaLogado()));
		lista.add(new Parametro("logomarca", "MAIL", logomarca, seguranca.getPessoaLogado()));
		lista.add(new Parametro("msgMailPedido", "MAIL", msgMailPedido, seguranca.getPessoaLogado()));
		lista.add(new Parametro("theme", Suporte.USUARIO_LOGADO, theme.name(), seguranca.getPessoaLogado()));
		lista.add(new Parametro("userMail", Suporte.USUARIO_LOGADO, email, seguranca.getPessoaLogado()));
		lista.add(new Parametro("modelo", Suporte.USUARIO_LOGADO, modelo.name(), seguranca.getPessoaLogado()));
		parametros.guardar(lista);
	}
	

	public String getSmtpHostMail() {
		return smtpHostMail;
	}

	public void setSmtpHostMail(String smtpHostMail) {
		this.smtpHostMail = smtpHostMail;
	}

	public String getSmtpPortMail() {
		return smtpPortMail;
	}

	public void setSmtpPortMail(String smtpPortMail) {
		this.smtpPortMail = smtpPortMail;
	}

	public boolean getSmtpAuth() {
		return smtpAuth;
	}

	public void setSmtpAuth(boolean smtpAuth) {
		this.smtpAuth = smtpAuth;
	}

	public boolean getSmtpStarttls() {
		return smtpStarttls;
	}

	public void setSmtpStarttls(boolean smtpStarttls) {
		this.smtpStarttls = smtpStarttls;
	}

	public String getUserMailContabilidade() {
		return userMailContabilidade;
	}

	public void setUserMailContabilidade(String userMailContabilidade) {
		this.userMailContabilidade = userMailContabilidade;
	}

	public String getUserMail() {
		return userMail;
	}

	public void setUserMail(String userMail) {
		this.userMail = userMail;
	}

	public String getPasword() {
		return pasword;
	}

	public void setPasword(String pasword) {
		this.pasword = pasword;
	}

	public String getSubjectMail() {
		return subjectMail;
	}

	public void setSubjectMail(String subjectMail) {
		this.subjectMail = subjectMail;
	}

	public String getBodyMail() {
		return bodyMail;
	}

	public void setBodyMail(String bodyMail) {
		this.bodyMail = bodyMail;
	}

	public Map<String, String> getToMailsUsers() {
		return toMailsUsers;
	}

	public void setToMailsUsers(Map<String, String> toMailsUsers) {
		this.toMailsUsers = toMailsUsers;
	}

	public List<String> getFileMails() {
		return fileMails;
	}

	public void setFileMails(List<String> fileMails) {
		this.fileMails = fileMails;
	}

	public String getCharsetMail() {
		return charsetMail;
	}

	public void setCharsetMail(String charsetMail) {
		this.charsetMail = charsetMail;
	}

	public String getTypeTextMail() {
		return typeTextMail;
	}

	public void setTypeTextMail(String typeTextMail) {
		this.typeTextMail = typeTextMail;
	}

	public static String getTypeTextPlain() {
		return TYPE_TEXT_PLAIN;
	}

	public String getTypeTextHtml() {
		return TYPE_TEXT_HTML;
	}

	public boolean getSmtpStartSsl() {
		return smtpStartSsl;
	}

	public void setSmtpStartSsl(boolean smtpStartSsl) {
		this.smtpStartSsl = smtpStartSsl;
	}

	public String getSmtpGateway() {
		return smtpGateway;
	}

	public void setSmtpGateway(String smtpGateway) {
		this.smtpGateway = smtpGateway;
	}

	public String getMsgMailPedido() {
		return msgMailPedido;
	}

	public void setMsgMailPedido(String msgMailPedido) {
		this.msgMailPedido = msgMailPedido;
	}

	public boolean getSmtpSslTrust() {
		return smtpSslTrust;
	}

	public void setSmtpSslTrust(boolean smtpSslTrust) {
		this.smtpSslTrust = smtpSslTrust;
	}

	public String getCertificado() {
		return certificado;
	}

	public void setCertificado(String certificado) {
		this.certificado = certificado;
	}

	public String getCertificadoSenha() {
		return certificadoSenha;
	}

	public void setCertificadoSenha(String certificadoSenha) {
		this.certificadoSenha = certificadoSenha;
	}

	public boolean isAmbienteProducao() {
		return ambienteProducao;
	}

	public void setAmbienteProducao(boolean ambienteProducao) {
		this.ambienteProducao = ambienteProducao;
	}

	public boolean isEstaConfigurado() {
		return certificado != null && !certificado.equals("") && certificadoSenha != null
				&& !certificadoSenha.equals("");
	}

	public boolean isEstaComCertificado() {
		return certificado != null && !certificado.equals("");
	}

	public boolean isEstaComLogomarca() {
		return logomarca != null && !logomarca.equals("");
	}

	public String getLogomarca() {
		return logomarca;
	}

	public void setLogomarca(String logomarca) {
		this.logomarca = logomarca;
	}

	public TipoTheme getTheme() {
		return theme;
	}

	public void setTheme(TipoTheme theme) {
		this.theme = theme;
	}

	public String getEmail() {
		return email;
	}

	public String getAssinatura() {
		return assinatura;
	}

	public void setAssinatura(String assinatura) {
		this.assinatura = assinatura;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public TipoGraficoModelo getModelo() {
		return modelo;
	}

	public void setModelo(TipoGraficoModelo modelo) {
		this.modelo = modelo;
	}
}