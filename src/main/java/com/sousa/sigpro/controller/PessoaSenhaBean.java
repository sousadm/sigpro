package com.sousa.sigpro.controller;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.hibernate.Session;
import org.primefaces.PrimeFaces;

import com.sousa.sigpro.security.Seguranca;
import com.sousa.sigpro.util.Criptografia;
import com.sousa.sigpro.util.jsf.FacesUtil;

@Named
@ViewScoped
public class PessoaSenhaBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Seguranca seguranca;
	@Inject
	private EntityManager manager;

	private String atual;
	private String nova;
	private String prova;
	Map<String, Object> options;

	public PessoaSenhaBean() {
		nova = "";
		prova = "";
		options = new HashMap<>();
		options.put("closeOnEscape", true);
		options.put("modal", true);
		options.put("width", 400);
		// options.put("height", 300);
	}

	public void mostrarCaixaDialogo() {
		PrimeFaces.current().dialog().openDynamic("/convidado/PessoaSenha", options, null);
	}

	public void confirmar() {

		try {
 
			String md5key = Criptografia.convertPasswordToMD5(atual);
			String senha = seguranca.getPessoaLogado().getUsuario().getSenha();

			if (!md5key.equals(senha))
				throw new IllegalArgumentException("Senha atual incorreta!");

			if (!nova.equals(prova))
				throw new IllegalArgumentException("Senha incorreta!");

			md5key = Criptografia.convertPasswordToMD5(nova);
			String sql = "update Usuario set senha = '" + md5key + "' where id = "
					+ seguranca.getPessoaLogado().getUsuario().getId();

			Session session = manager.unwrap(Session.class);
			session.beginTransaction();
			Query query = manager.createQuery(sql);
			query.executeUpdate();
			session.getTransaction().commit();

			// Pessoa pessoa = pessoas.porId(seguranca.getPessoaLogado().getId());
			// pessoa.getUsuario().setSenha(nova);
			// pessoas.enviarEmailSenha(pessoa, nova);
			// FacesUtil.addInfoMessage("Nova senha enviada para seu email.");

			PrimeFaces.current().dialog().closeDynamic(null);

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}

	}

	public String getAtual() {
		return atual;
	}

	public void setAtual(String atual) {
		this.atual = atual;
	}

	public String getNova() {
		return nova;
	}

	public void setNova(String nova) {
		this.nova = nova;
	}

	public String getProva() {
		return prova;
	}

	public void setProva(String prova) {
		this.prova = prova;
	}

}
