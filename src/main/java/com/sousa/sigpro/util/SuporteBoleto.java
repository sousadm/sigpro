package com.sousa.sigpro.util;

import org.jrimum.bopepo.BancosSuportados;
import org.jrimum.domkee.financeiro.banco.febraban.ContaBancaria;

import com.sousa.sigpro.service.NegocioException;

public class SuporteBoleto {

	public static BancosSuportados getBopepoBancoSuportado(String codigo) {
		if ("001".equals(codigo))
			return BancosSuportados.BANCO_DO_BRASIL;
		else if ("004".equals(codigo))
			return BancosSuportados.BANCO_DO_NORDESTE_DO_BRASIL;
		else if ("021".equals(codigo))
			return BancosSuportados.BANCO_DO_ESTADO_DO_ESPIRITO_SANTO;
		else if ("033".equals(codigo))
			return BancosSuportados.BANCO_SANTANDER;
		else if ("041".equals(codigo))
			return BancosSuportados.BANCO_DO_ESTADO_DO_RIO_GRANDE_DO_SUL;
		else if ("077".equals(codigo))
			return BancosSuportados.BANCO_INTEMEDIUM;
		else if ("104".equals(codigo))
			return BancosSuportados.CAIXA_ECONOMICA_FEDERAL;
		else if ("151".equals(codigo))
			return BancosSuportados.NOSSA_CAIXA;
		else if ("237".equals(codigo))
			return BancosSuportados.BANCO_BRADESCO;
		else if (codigo.equals("341"))
			return BancosSuportados.BANCO_ITAU;
		else if (codigo.equals("356"))
			return BancosSuportados.BANCO_ABN_AMRO_REAL;
		else if (codigo.equals("389"))
			return BancosSuportados.MERCANTIL_DO_BRASIL;
		else if (codigo.equals("399"))
			return BancosSuportados.HSBC;
		else if (codigo.equals("409"))
			return BancosSuportados.UNIBANCO;
		else if ("422".equals(codigo))
			return BancosSuportados.BANCO_SAFRA;
		else if (codigo.equals("453"))
			return BancosSuportados.BANCO_RURAL;
		else if (codigo.equals("748"))
			return BancosSuportados.BANCO_SICREDI;
		else if (codigo.equals("756"))
			return BancosSuportados.BANCOOB;
		return null;
	}

	public static String getNossoNumeroDV(BancosSuportados banco, Integer numero, Integer carteira) {
		switch (banco) {
		case BANCO_BRADESCO:
			return getNossoNumeroDVBradesco(numero, carteira);
		default:
			return getNossoNumeroDVBase11(numero);
		}
	}

	public static String getNossoNumeroDVBradesco(Integer numero, Integer carteira) {

		if (numero == null)
			throw new NegocioException("nosso número inválido");

		if (carteira == null)
			throw new NegocioException("carteira inválida");

		String digitosParaCalculo = Suporte.zerosAEsquerda(carteira.longValue(), 2)
				+ Suporte.zerosAEsquerda(numero.longValue(), 11);

		int fator = 2;
		int somatorio = 0;
		for (int i = digitosParaCalculo.length() - 1; i >= 0; i--) {
			Integer digito = Integer.parseInt(String.valueOf(digitosParaCalculo.charAt(i)));
			somatorio += fator * digito;
			fator++;
			if (fator == 8) {
				fator = 2;
			}
		}
		fator = 11 - (somatorio % 11);
		if (fator == 10)
			return "P";
		else
			return String.valueOf(fator);

	}

	public static String getNossoNumeroDVBase11(Integer numero) {

		if (numero == null)
			throw new NegocioException("nosso número inválido");

		String digitosParaCalculo = String.valueOf(numero);

		int fator = 2;
		int somatorio = 0;
		for (int i = digitosParaCalculo.length() - 1; i >= 0; i--) {
			Integer digito = Integer.parseInt(String.valueOf(digitosParaCalculo.charAt(i)));
			somatorio += fator * digito;
			fator++;
		}

		int resto = somatorio % 11;

		switch (resto) {
		case 0:
			return String.valueOf(1);
		case 1:
			return String.valueOf(0);
		default:
			return String.valueOf(11 - resto);
		}

	}

	public static String getNossoNumero(Long numero, BancosSuportados banco) {
		switch (banco) {
		case BANCO_SANTANDER:
			return Suporte.zerosAEsquerda(numero, 12);
		case BANCO_SAFRA:
			return Suporte.zerosAEsquerda(numero, 9);
		default:
			return Suporte.zerosAEsquerda(numero, 7);
		}
	}
	
	public static ContaBancaria getBopepoContaBancaria(String codigo) {
		BancosSuportados suportado = SuporteBoleto.getBopepoBancoSuportado(codigo);
		ContaBancaria conta = new ContaBancaria(suportado.create());
		return conta;
	}
	
	
}
