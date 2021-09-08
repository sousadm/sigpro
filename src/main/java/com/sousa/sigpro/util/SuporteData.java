package com.sousa.sigpro.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import com.sousa.sigpro.model.tipo.TipoDiaSemana;

public class SuporteData {

	public static String dataHoraToStr(Date data) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
		return dateFormat.format(data);

	}

	public static int idade(Date data) {
		LocalDate aniversario = data.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		final LocalDate dataAtual = LocalDate.now();
		final Period periodo = Period.between(aniversario, dataAtual);
		return periodo.getYears();
	}

	public static int idade(Date inicio, Date termino) {
		LocalDate aniversario = inicio.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate dataAtual = termino.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		final Period periodo = Period.between(aniversario, dataAtual);
		return periodo.getYears();
	}

	public static int tempo(Date data) {
		Calendar minuto = Calendar.getInstance();
		minuto.setTime(data);
		return minuto.get(Calendar.HOUR) * 60 + minuto.get(Calendar.MINUTE);
	}

	public static int primeiroDiaDoMes(Date data) {
		Calendar atual = Calendar.getInstance();
		atual.setTime(data);
		return atual.getActualMinimum(Calendar.DAY_OF_MONTH);
	}

	public static int ultimoDiaDoMes(Date data) {
		Calendar atual = Calendar.getInstance();
		atual.setTime(data);
		return atual.getActualMaximum(Calendar.DAY_OF_MONTH);
	}

	public static Date ultimaDataDoMes(Date data) {
		Calendar atual = Calendar.getInstance();
		atual.setTime(data);
		atual.add(Calendar.MONTH, 1);
		atual.set(Calendar.DAY_OF_MONTH, 1);
		atual.add(Calendar.DATE, -1);
		return atual.getTime();
	}

	public static Date primeiraDataDoMes(Date data) {
		Calendar atual = Calendar.getInstance();
		atual.setTime(data);
		atual.set(Calendar.DAY_OF_MONTH, 1);
		return atual.getTime();
	}

	public static int diaDoMes(Date data) {
		Calendar atual = Calendar.getInstance();
		atual.setTime(data);
		return atual.get(Calendar.DAY_OF_MONTH);
	}

	public static String nomeMes(int valor) {
		String[] meses = { "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro",
				"Outubro", "Novembro", "Dezembro" };
		return meses[valor].toString();

	}

	public static String convertTimeWithTimeZome(long time) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeZone(TimeZone.getTimeZone("UTC"));
		cal.setTimeInMillis(time);
		String curTime = String.format("%02d:%02d", cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
		return curTime;
	}

	public static String formataDataToStr(Date data) {
		return formataDataToStr(data, "");
	}

	public static String formataDataToStr(Date data, String formato) {
		if (formato.isEmpty() || formato.equals("")) {
			formato = "dd/MM/yyyy HH:mm:ss";
		}
		SimpleDateFormat dateFormat = new SimpleDateFormat(formato);
		return dateFormat.format(data);
	}

	public static String formataHoraToStr(Date data) {
		String formato = "HH:mm:ss";
		SimpleDateFormat dateFormat = new SimpleDateFormat(formato);
		return dateFormat.format(data);
	}

	public static int idade(String dtNasc) {
		Date dt = null;
		SimpleDateFormat temp = new SimpleDateFormat("ddMMyyyy");
		try {
			dt = temp.parse(dtNasc);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Calendar dtNiver = new GregorianCalendar();
		dtNiver.setTime(dt);
		Calendar dtHoje = Calendar.getInstance();
		int idade = dtHoje.get(Calendar.YEAR) - dtNiver.get(Calendar.YEAR);
		dtNiver.add(Calendar.YEAR, idade);
		if (dtHoje.before(dtNiver)) {
			idade--;
		}
		return idade;
	}

	public static int quantDomingos(int mes, int ano) {
		int quantDomingos = 0;
		Calendar c = new GregorianCalendar(ano, mes, 1);
		do {
			if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
				quantDomingos++;
			}
			c.roll(Calendar.DAY_OF_MONTH, true);
		} while (c.get(Calendar.DAY_OF_MONTH) != 1);
		return quantDomingos;
	}

	public static Date incrementaDiaNaData(Date data, int incremento) {
		Calendar c = Calendar.getInstance();
		c.setTime(data);
		c.add(Calendar.DAY_OF_MONTH, incremento);
		return c.getTime();
	}

	public static Date incrementaMesNaData(Date data, int incremento) {
		Calendar c = Calendar.getInstance();
		c.setTime(data);
		c.add(Calendar.MONTH, incremento);
		return c.getTime();
	}

	public static Date somenteData(String data) throws Exception {
		if (data == null || data.equals(""))
			return null;
		Date date = null;
		try {
			DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			date = (java.util.Date) formatter.parse(data);
		} catch (ParseException e) {
			throw e;
		}
		return date;
	}

	public static int diasEntreDatas(Date inicio, Date termino) {
		try {
			Long dif = (termino.getTime() - inicio.getTime()) / (1000 * 60 * 60 * 24);
			if (dif < 0)
				dif = 0L;
			return dif.intValue();
		} catch (Exception e) {
			return 0;
		}
	}

	public static Date horaInicial(Date data) throws Exception {
		Calendar c = Calendar.getInstance();
		c.setTime(data);
		c.set(Calendar.HOUR, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		return c.getTime();
	}

	public static Date horaFinal(Date data) throws Exception {
		Calendar c = Calendar.getInstance();
		c.setTime(data);
		c.set(Calendar.HOUR, 23);
		c.set(Calendar.MINUTE, 59);
		c.set(Calendar.SECOND, 59);
		return c.getTime();
	}

	public static Date horaAddMinuto(Date data, int tempo) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.DATE, 0);
		c.setTime(data);
		c.add(Calendar.MINUTE, tempo);
		return c.getTime();
	}

	public static TipoDiaSemana diaDaSemana(Date data) {
		Calendar c = Calendar.getInstance();
		return TipoDiaSemana.valueOfOrdem(c.get(Calendar.DAY_OF_WEEK) - 1);
	}

	public static Date somenteHora(Date data) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(data);
		cal.set(Calendar.DATE, 0);
		return cal.getTime();
	}

	public static Date somenteData(Date data) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(data);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	public static Date concatenaDataHora(Date data, Date hora) {

		Calendar horaCalendario = Calendar.getInstance();
		horaCalendario.setTime(hora);

		Calendar dataCalendario = Calendar.getInstance();
		dataCalendario.setTime(data);
		dataCalendario.set(Calendar.HOUR_OF_DAY, horaCalendario.get(Calendar.HOUR_OF_DAY));
		dataCalendario.set(Calendar.MINUTE, horaCalendario.get(Calendar.MINUTE));
		dataCalendario.set(Calendar.SECOND, horaCalendario.get(Calendar.SECOND));
		dataCalendario.set(Calendar.MILLISECOND, horaCalendario.get(Calendar.MILLISECOND));

		return dataCalendario.getTime();

	}

	public static Date horaPrevistaPadrao(Date data) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(data);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

}
