package com.sousa.sigpro.controller;

import java.io.InputStream;
import java.io.Serializable;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.event.FileUploadEvent;
import org.springframework.ui.context.Theme;

import com.fincatto.documentofiscal.DFModelo;
import com.fincatto.documentofiscal.DFUnidadeFederativa;
import com.fincatto.documentofiscal.nfe400.classes.statusservico.consulta.NFStatusServicoConsultaRetorno;
import com.sousa.sigpro.controller.parametro.ParametroFinanceiro;
import com.sousa.sigpro.controller.parametro.ParametroMail;
import com.sousa.sigpro.model.Parametro;
import com.sousa.sigpro.nfe.SuporteNFe;
import com.sousa.sigpro.repository.Parametros;
import com.sousa.sigpro.security.Seguranca;
import com.sousa.sigpro.service.NegocioException;
import com.sousa.sigpro.util.Suporte;
import com.sousa.sigpro.util.jsf.FacesUtil;

@Named
@ViewScoped
public class ParametroBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Seguranca seguranca;
	@Inject
	private ParametroMail mail;
	@Inject
	private Parametros parametros;
	@Inject
	private ParametroFinanceiro custoFinanceiro;

	InputStream certificado;
	InputStream logomarca;
	private String senha;
	private boolean usuarioPrincipal;
	private List<Theme> themes;

	private double valor = 0;

	public void inicializar() {
		if (FacesUtil.isNotPostback()) {
			Suporte.removerAtributoDaSessao("imagem");
			mail.ler();
			custoFinanceiro.ler();
			usuarioPrincipal = seguranca.isUsuarioPrincipal()
					|| seguranca.getPessoaLogado().getUsuario().isPermiteEmailProprio();
		}
	}

	public void gerarCadeiaCertificadora() {
		try {

			if (mail.getCertificado() == null)
				throw new NegocioException("Defina o certificado digital");

			if (mail.getCertificadoSenha() == null)
				throw new NegocioException("Defina a senha do certificado digital");

			SuporteNFe suporte = new SuporteNFe(mail);
			suporte.montarCadeiaCertificado();
			FacesUtil.addInfoMessage("Operação concluída com sucesso");

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public ParametroMail getMail() {
		return mail;
	}

	public void setMail(ParametroMail mail) {
		this.mail = mail;
	}

	public ParametroFinanceiro getCustoFinanceiro() {
		return custoFinanceiro;
	}

	public void setCustoFinanceiro(ParametroFinanceiro custoFinanceiro) {
		this.custoFinanceiro = custoFinanceiro;
	}

	public void upload(FileUploadEvent event) {
		try {
			certificado = event.getFile().getInputstream();
			mail.setCertificado(event.getFile().getFileName());
		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void verificarServicoSefaz() {
		try {

			if (mail.getCertificado() == null)
				throw new NegocioException("Defina o certificado digital");

			if (mail.getCertificadoSenha() == null)
				throw new NegocioException("Defina a senha do certificado digital");

			SuporteNFe suporte = new SuporteNFe(mail);
			NFStatusServicoConsultaRetorno retorno = suporte.statusServico(DFUnidadeFederativa.CE, DFModelo.NFE);
			FacesUtil.addInfoMessage(retorno.getMotivo());

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void removerCertificado() {
		try {
			Suporte.removerArquivo(Suporte.uploadLocal + mail.getCertificado());
			Suporte.removerArquivo(Suporte.uploadLocal + seguranca.getEmpresa() + "_homologacao.cacerts");
			Suporte.removerArquivo(Suporte.uploadLocal + seguranca.getEmpresa() + "_producao.cacerts");
			mail.setCertificado(null);
			mail.setCertificadoSenha(null);
			mail.gravar();
			FacesUtil.addInfoMessage("Certificado removido com sucesso");

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public String getSenha() {
		return senha;
	}

	public boolean isUsuarioPrincipal() {
		return usuarioPrincipal;
	}

	public void gravarCusto() {
		try {
			custoFinanceiro.gravar();
			FacesUtil.addInfoMessage("Parâmetros financeiros gravados com sucesso!");
		} catch (Exception e) {
			FacesUtil.addInfoMessage(e.getCause().getMessage());
		}
	}

	public void preConfiguraEmail() {
		mail.setSmtpStartSsl(true);
		mail.setSmtpAuth(true);
		mail.setSmtpPortMail("587");
		if (mail.getUserMail().toLowerCase().contains("yahoo.com")) {
			mail.setSmtpHostMail("imap.mail.yahoo.com");
		} else if (mail.getUserMail().toLowerCase().contains("gmail.com")) {
			mail.setSmtpHostMail("smtp.gmail.com");
		} else if (mail.getUserMail().toLowerCase().contains("hotmail.com")
				|| mail.getUserMail().toLowerCase().contains("live.com")) {
			mail.setSmtpHostMail("smtp.live.com");
			mail.setSmtpStarttls(true);
		} else if (mail.getUserMail().toLowerCase().contains("uol.com.br")) {
			mail.setSmtpHostMail("smtps.uol.com.br");
		}
	}

	public boolean isEmpresaNormal() {
		return seguranca.isEmpresaNormal();
	}

	public void removerLogomarca() {
		try {
			Suporte.removerArquivo(Suporte.uploadLocal + mail.getLogomarca());
			mail.setLogomarca(null);
			mail.gravar();

			FacesUtil.addInfoMessage("Logomarca removida com sucesso");

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void uploadLogomarca(FileUploadEvent event) {
		try {
			logomarca = event.getFile().getInputstream();
			mail.setLogomarca(event.getFile().getFileName());
		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void gravarMail() {
		try {

			mail.gravar();
			if (mail.getLogomarca() != null && logomarca != null) {
				Suporte.copyFile(mail.getLogomarca(), Suporte.uploadLocal, logomarca);
				Suporte.logomarca = Suporte.uploadLocal + mail.getLogomarca();
			}

			mail.ler();
			Suporte.theme = mail.getTheme().name();

			FacesUtil.addInfoMessage("Gravado com sucesso!");

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		} finally {
			logomarca = null;
			certificado = null;
			senha = null;
		}
	}

	public List<Theme> getThemes() {
		return themes;
	}

	public double getValor() {
		return valor;
	}

	public void setValor(double valor) {
		this.valor = valor;
	}

	public void gravarThema() {
		Suporte.theme = mail.getTheme().name();
		Parametro param = parametros.porCodigoGrupo("theme", Suporte.USUARIO_LOGADO, seguranca.getPessoaLogado());
		if (param == null)
			param = new Parametro("theme", Suporte.USUARIO_LOGADO, mail.getTheme().name(), seguranca.getPessoaLogado());
		param.setValor(Suporte.theme);
		parametros.guardar(param);
	}

}