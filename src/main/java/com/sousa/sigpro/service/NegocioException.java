package com.sousa.sigpro.service;

public class NegocioException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public NegocioException(String msg) {
		super(msg);
	}

	public static void analisa(boolean condicao, String msg) {
		if (condicao)
			new NegocioException(msg);
	}

}