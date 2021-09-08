package com.sousa.sigpro.util;

import java.util.Base64;

public class SuporteSigproApi {

	public static String URL_SIGPRO_API = "http://contrateconsultoria.com.br/sigproapi";

	public static String CredencialSigproAPI() {
		String usuarioSigproAPI = "sigpro";
		String senhaSigproAPI = "s3nh4";
		String credencialAux = usuarioSigproAPI + ":" + senhaSigproAPI;
		return "Basic " + Base64.getEncoder().encodeToString(credencialAux.getBytes());
	}
	
}