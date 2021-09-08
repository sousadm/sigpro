package com.sousa.sigpro.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.Normalizer;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.Locale;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.SAXException;

import com.fincatto.documentofiscal.nfe400.classes.nota.NFNota;
import com.sousa.sigpro.service.NegocioException;
import com.sousa.sigpro.util.jsf.FacesUtil;
import com.sun.jersey.api.client.ClientResponse;

import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;

public class Suporte {

	boolean lido;
	public static String EMPRESA_PADRAO;
	public static String theme;
	public static String logomarca;
	public static String uploadLocal;
	public static String USUARIO_LOGADO;
	public static String TOKEN_PROVISORIO;
	public static String CNPJ_PROVISORIO;
	public static Date DATA_LICENCA_VALIDA;

	public static String USUARIO_BANCO_DADOS;
	public static String URL_DATABASE;
	public static String SENHA_BANCO_DADOS;

	public static int TEMPO_PARA_ENCERRAMENTO_CAIXA; // = 2;
	public static double PERCENTUAL_MARGEM_TRIBUTACAO; // = 8.00;
	public static double PERCENTUAL_MARGEM_CONTRIBUICAO; // = 5.00;
	public static double PERCENTUAL_MARGEM_LUCRO; // = 10.00;
	public static double PERCENTUAL_MARGEM_RESIDUAL; // = 5.00;
	public static int PRAZO_VENCIMENTO_GERENCIAVEL; // = 30;

	public static int SERIE_NFE; // = 4;
	public static String VERSAO_EMISSOR; // = "1.00";

	public Suporte() {
	}

	public static Properties getPropriedade(String arquivo) throws IOException {
		Properties props = new Properties();
		FileInputStream file = new FileInputStream(arquivo);
		props.load(file);
		return props;
	}

	public static String getUrlDoSchema(String schema) {
		return URL_DATABASE + "?currentSchema=" + schema;
	}

	public static String zerosAEsquerda(int numero, int quantidade) {
		return zerosAEsquerda(new Long(numero), quantidade);
	}

	public static String zerosAEsquerda(Long numero, int quantidade) {
		return String.format("%0" + String.valueOf(quantidade) + "d", numero);
	}

	public static void removerArquivo(String arquivo) {
		(new File(arquivo)).delete();
	}

	public static String getQuotedStr(String valor) {
		return "'" + valor + "'";
	}

	public static String getQuotedStr(double valor) {
		return "'" + valor + "'";
	}

	public static String getChaveNFe(NFNota nota) {

		SimpleDateFormat sm = new SimpleDateFormat("yyMM");

		String valor = substring(nota.getInfo().getIdentificacao().getCodigoMunicipio(), 0, 2)
				+ sm.format(nota.getInfo().getIdentificacao().getDataHoraEmissao().toLocalDate())
				+ nota.getInfo().getEmitente().getCnpj() + nota.getInfo().getIdentificacao().getModelo().getCodigo()
				+ String.format("%03d", Integer.parseInt(nota.getInfo().getIdentificacao().getSerie()))
				+ String.format("%09d", nota.getInfo().getIdentificacao().getNumeroNota())
				+ nota.getInfo().getIdentificacao().getTipoEmissao().getCodigo();
		valor = valor + nota.getInfo().getIdentificacao().getCodigoRandomico() + modulo11(valor);

		return valor.toString();
	}

	public static int getChaveNFeDigito(String chave) {
		return Integer.parseInt(chave.substring(chave.length() - 1));
	}

	public static int modulo11(String chave) {
		int total = 0;
		int peso = 2;

		for (int i = 0; i < chave.length(); i++) {
			total += (chave.charAt((chave.length() - 1) - i) - '0') * peso;
			peso++;
			if (peso == 10)
				peso = 2;
		}
		int resto = total % 11;
		return (resto == 0 || resto == 1) ? 0 : (11 - resto);
	}

	public static void setAtributoNaSessao(String name, Object value) {
		FacesContext fc = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) fc.getExternalContext().getSession(false);
		session.setAttribute(name, value);
	}

	public static boolean existeAtributoNaSessao(String name) {
		FacesContext fc = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) fc.getExternalContext().getSession(false);
		return session.getAttribute(name) != null;
	}

	public static Object getAtributoDaSessao(String name) {
		FacesContext fc = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) fc.getExternalContext().getSession(false);
		return session.getAttribute(name);
	}

	public static void removerAtributoDaSessao(String name) {
		FacesContext fc = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) fc.getExternalContext().getSession(false);
		session.removeAttribute(name);
	}

	public static String substring(String valor, int inicio, int tamanho) {
		if (valor != null) {
			if (valor.length() < tamanho)
				tamanho = valor.length();
			if (inicio > tamanho)
				inicio = tamanho;
			return valor.substring(inicio, tamanho);
		} else {
			return valor;
		}
	}

	public static String valorPorExtenso(double valor) {
		Extenso extenso = new Extenso();
		extenso.setNumber(new BigDecimal(valor));
		return extenso.toString().trim();
	}

	public static boolean isEmailValido(String email) {
		if ((email == null) || (email.trim().length() == 0))
			return false;

		String emailPattern = "\\b(^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@([A-Za-z0-9-])+(\\.[A-Za-z0-9-]+)*((\\.[A-Za-z0-9]{2,})|(\\.[A-Za-z0-9]{2,}\\.[A-Za-z0-9]{2,}))$)\\b";
		Pattern pattern = Pattern.compile(emailPattern, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(email);
		return matcher.matches();
	}

	public static String onlyNumbers(String str) {
		if (str != null) {
			return str.replaceAll("[^0123456789]", "");
		} else {
			return "";
		}
	}

	public static void validaPeriodo(Date inicio, Date termino) {
		if (inicio != null && termino != null && inicio.after(termino))
			throw new NegocioException("Período inválido!");
	}

	public static Long MillisEntreDatas(Date inicio, Date termino) {
		if (inicio != null && termino != null)
			return Duration.between(inicio.toInstant(), termino.toInstant()).toMillis();
		else
			return Long.valueOf(0);
	}

	@SuppressWarnings("deprecation")
	public static double arredondaValor(double valor, int nrCasasDecimais) {
		BigDecimal bd = new BigDecimal(valor);
		bd = bd.setScale(nrCasasDecimais, BigDecimal.ROUND_HALF_UP);
		return bd.doubleValue();
	}

	public static double arredondaValor(double valor) {
		return arredondaValor(valor, 2);
	}

	public static String formataCurrency(double valor) {
		Locale locale = new Locale("pt", "BR");
		NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);
		return currencyFormatter.format(valor);
	}

	public static boolean dataIsValida(Date data) {
		boolean valor = false;
		if (data != null) {
			DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
			df.setLenient(false); // aqui o pulo do gato
			try {
				valor = true;
				df.parse(data.toString());
			} catch (ParseException ex) {
			}
		}
		return valor;
	}

	public static String formataDataSQL(Date data) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		return dateFormat.format(data);
	}

	public static String formataDataSQL_Quoted(Date data) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		return " '" + dateFormat.format(data) + "' ";

	}

	public static String xmlNFe(String arquivo, String tagName, int posicao)
			throws ParserConfigurationException, SAXException, IOException {

		/* fonte: http://www.javac.com.br/jc/posts/list/2557.page */

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(false);
		DocumentBuilder docBuilder = dbf.newDocumentBuilder();
		Document doc;
		doc = docBuilder.parse(new File(arquivo));
		doc.getDocumentElement().normalize();

		NodeList noLista = doc.getElementsByTagName(tagName);
		Node no = noLista.item(posicao);

		Document document = no.getOwnerDocument();
		DOMImplementationLS domImplLS = (DOMImplementationLS) document.getImplementation();
		LSSerializer serializer = domImplLS.createLSSerializer();

		String xmlNfeProc = serializer.writeToString(no);
		return xmlNfeProc;
	}

	public static void copyFile(String fileName, String destination, InputStream in) throws IOException {
		OutputStream out = new FileOutputStream(new File(destination + fileName));

		int read = 0;
		byte[] bytes = new byte[1024];

		while ((read = in.read(bytes)) != -1) {
			out.write(bytes, 0, read);
		}

		in.close();
		out.flush();
		out.close();

	}

	public static Long getRandomLong(double min, double max) {
		return (new Double(getRandomDouble(min, max))).longValue();
	}

	public static double getRandomDouble(double min, double max) {
		min = Math.ceil(min);
		max = Math.floor(max);
		return Math.floor(Math.random() * (max - min + 1)) + min;
	}

	public static void ExportarArquivo(JasperPrint printer, String arquivo) throws Exception {
		OutputStream saida = new FileOutputStream(arquivo);
		JRPdfExporter exporter = new JRPdfExporter();
		exporter.setExporterInput(new SimpleExporterInput(printer));
		exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(saida));
		exporter.exportReport();
	}

	public static void ImprimirArquivo(HttpServletResponse response, JasperPrint printer) throws Exception {

		SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
		OutputStream saida = response.getOutputStream();
		JRPdfExporter exporter = new JRPdfExporter();

		configuration.setMetadataAuthor("Sousa"); // why not set some config as we like
		exporter.setExporterInput(new SimpleExporterInput(printer));
		exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(saida));
		exporter.setConfiguration(configuration);
		exporter.exportReport();

	}

	public static void ImprimirArquivo(HttpServletResponse response, JasperPrint printer, String arquivo)
			throws Exception {

		if (arquivo == null) {
			String path_pdf = (String) System.getProperties().get("os.name");
			path_pdf = path_pdf.toLowerCase().contains("windows") ? "c:/" : "/";
			arquivo = path_pdf + "tmp/print_" + SuporteData.dataHoraToStr(new Date()) + ".pdf";
		}

		Suporte.ExportarArquivo(printer, arquivo);

		File file = new File(arquivo);
		byte[] bytes = Files.readAllBytes(file.toPath());

		response.reset();
		response.setContentType("application/pdf");
		response.setContentLength(bytes.length);
		response.setHeader("Content-disposition", "inline; filename=" + file.getName());
		try {
			response.getOutputStream().write(bytes);
			response.getOutputStream().flush();
			response.getOutputStream().close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static Date diaMesAnoToDate(String valor) throws ParseException {
		Date data = new Date();
		DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
		data = df.parse(valor);
		return data;
	}

	public static Date stringFocusToDataHora(String valor) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
		Date data;
		try {
			data = formatter.parse(valor);
		} catch (ParseException e) {
			data = new Date();
		}
		return data;
	}

	public static double valorAbsoluto(double valor) {
		return Math.abs(valor);
	}

	public static String extensaoDeArquivo(String nome) {
		return nome.substring(nome.lastIndexOf("."), nome.length());
	}

	public static StreamedContent imagemStreamedOutContent(String arquivo) throws FileNotFoundException {
		if (arquivo != null) {
			File file = new File(arquivo);
			if (file.exists()) {
				InputStream inputStream = new FileInputStream(file);
				StreamedContent content = new DefaultStreamedContent(inputStream, "image/jpeg", arquivo);
				return content;
			} else
				return null;
		} else
			return null;
	}

	public static boolean isCPF(String CPF) {
		// considera-se erro CPF's formados por uma sequencia de numeros iguais
		if (CPF.equals("00000000000") || CPF.equals("11111111111") || CPF.equals("22222222222")
				|| CPF.equals("33333333333") || CPF.equals("44444444444") || CPF.equals("55555555555")
				|| CPF.equals("66666666666") || CPF.equals("77777777777") || CPF.equals("88888888888")
				|| CPF.equals("99999999999") || (CPF.length() != 11))
			return (false);

		char dig10, dig11;
		int sm, i, r, num, peso;

		// "try" - protege o codigo para eventuais erros de conversao de tipo (int)
		try {
			// Calculo do 1o. Digito Verificador
			sm = 0;
			peso = 10;
			for (i = 0; i < 9; i++) {
				// converte o i-esimo caractere do CPF em um numero:
				// por exemplo, transforma o caractere '0' no inteiro 0
				// (48 eh a posicao de '0' na tabela ASCII)
				num = (int) (CPF.charAt(i) - 48);
				sm = sm + (num * peso);
				peso = peso - 1;
			}

			r = 11 - (sm % 11);
			if ((r == 10) || (r == 11))
				dig10 = '0';
			else
				dig10 = (char) (r + 48); // converte no respectivo caractere numerico

			// Calculo do 2o. Digito Verificador
			sm = 0;
			peso = 11;
			for (i = 0; i < 10; i++) {
				num = (int) (CPF.charAt(i) - 48);
				sm = sm + (num * peso);
				peso = peso - 1;
			}

			r = 11 - (sm % 11);
			if ((r == 10) || (r == 11))
				dig11 = '0';
			else
				dig11 = (char) (r + 48);

			// Verifica se os digitos calculados conferem com os digitos informados.
			if ((dig10 == CPF.charAt(9)) && (dig11 == CPF.charAt(10)))
				return (true);
			else
				return (false);
		} catch (InputMismatchException erro) {
			return (false);
		}
	}

	public static String imprimeCPF(String CPF) {
		return (CPF.substring(0, 3) + "." + CPF.substring(3, 6) + "." + CPF.substring(6, 9) + "-"
				+ CPF.substring(9, 11));
	}

	/*
	 * https://www.devmedia.com.br/validando-o-cnpj-em-uma-aplicacao-java/22374
	 */
	public static boolean isCNPJ(String CNPJ) {
		// considera-se erro CNPJ's formados por uma sequencia de numeros iguais
		if (CNPJ.equals("00000000000000") || CNPJ.equals("11111111111111") || CNPJ.equals("22222222222222")
				|| CNPJ.equals("33333333333333") || CNPJ.equals("44444444444444") || CNPJ.equals("55555555555555")
				|| CNPJ.equals("66666666666666") || CNPJ.equals("77777777777777") || CNPJ.equals("88888888888888")
				|| CNPJ.equals("99999999999999") || (CNPJ.length() != 14))
			return (false);

		char dig13, dig14;
		int sm, i, r, num, peso;

		// "try" - protege o código para eventuais erros de conversao de tipo (int)
		try {
			// Calculo do 1o. Digito Verificador
			sm = 0;
			peso = 2;
			for (i = 11; i >= 0; i--) {
				// converte o i-ésimo caractere do CNPJ em um número:
				// por exemplo, transforma o caractere '0' no inteiro 0
				// (48 eh a posição de '0' na tabela ASCII)
				num = (int) (CNPJ.charAt(i) - 48);
				sm = sm + (num * peso);
				peso = peso + 1;
				if (peso == 10)
					peso = 2;
			}

			r = sm % 11;
			if ((r == 0) || (r == 1))
				dig13 = '0';
			else
				dig13 = (char) ((11 - r) + 48);

			// Calculo do 2o. Digito Verificador
			sm = 0;
			peso = 2;
			for (i = 12; i >= 0; i--) {
				num = (int) (CNPJ.charAt(i) - 48);
				sm = sm + (num * peso);
				peso = peso + 1;
				if (peso == 10)
					peso = 2;
			}

			r = sm % 11;
			if ((r == 0) || (r == 1))
				dig14 = '0';
			else
				dig14 = (char) ((11 - r) + 48);

			// Verifica se os dígitos calculados conferem com os dígitos informados.
			if ((dig13 == CNPJ.charAt(12)) && (dig14 == CNPJ.charAt(13)))
				return (true);
			else
				return (false);
		} catch (InputMismatchException erro) {
			return (false);
		}
	}

	public static String imprimeCNPJ(String CNPJ) {
		// máscara do CNPJ: 99.999.999.9999-99
		return (CNPJ.substring(0, 2) + "." + CNPJ.substring(2, 5) + "." + CNPJ.substring(5, 8) + "."
				+ CNPJ.substring(8, 12) + "-" + CNPJ.substring(12, 14));
	}

	public static boolean numeroEInteiro(double num) {
		int aux = (int) num;
		return (((double) aux) == num);
	}

	public static boolean numeroEInteiro(String num) {
		try {
			Integer.parseInt(num);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static boolean stringComValor(String valor) {
		return valor != null && valor.length() > 0;
	}

	public static String removeCaracteresEspeciaisXML(String valorAcentuado) {
		return Normalizer.normalize(valorAcentuado, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
	}

	public static String iniciaisDoNome(String name) {
		name = name.toLowerCase();
		// normalizando a string para ficar sem acentos
		name = Normalizer.normalize(name, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
		// retirando as preposições. Adicione outras se desejar.
		name = name.replaceAll("da|das|do|dos|e|de", "");
		// retirando todas as palvras exceto a primeira letra e transformando e
		// maiusculo.
		return name.replaceAll("\\B\\w\\s*", "").toUpperCase();
	}

	public static byte[] pdfToArrayByte(String arquivo) {

		byte[] conteudoPdf = null;
		File file = new File(arquivo);

		if (!file.exists())
			throw new NegocioException("arquivo não localizado");

		try {

			conteudoPdf = new byte[(int) file.length()];
			InputStream is = new FileInputStream(file);
			is.read(conteudoPdf);
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return conteudoPdf;
	}

	public static void visualizarPdf(String arquivo) {
		try {
			// http://java-bytecode.blogspot.com/2012/08/como-exibir-um-pdf-numa-pagina-jsf-fora-container.html
			byte[] conteudoPdf = Suporte.pdfToArrayByte(arquivo);

			FacesContext fc = FacesContext.getCurrentInstance();
			HttpServletResponse response = (HttpServletResponse) fc.getExternalContext().getResponse();
			response.reset();
			response.setContentType("application/pdf");
			response.setContentLength(conteudoPdf.length);
			// Mude para "attachment" para indicar que deve ser feito um download
			response.setHeader("Content-disposition", "inline; filename=" + arquivo);
			// response.setHeader("Content-disposition", "attachment; filename=" + arquivo);
			try {
				response.getOutputStream().write(conteudoPdf);
				response.getOutputStream().flush();
				response.getOutputStream().close();
				fc.responseComplete();
			} catch (IOException e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			FacesUtil.addInfoMessage(e.getMessage());
		}
	}

	public static double truncate(Double valor, int precisao) {
		BigDecimal bd = BigDecimal.valueOf(valor);
		bd = bd.setScale(precisao, BigDecimal.ROUND_DOWN);

		return bd.doubleValue();
	}

	public static boolean arquivoExiste(String arquivo) {
		return new File(arquivo).exists();
	}

	public static void analisaRetornoHTTP(ClientResponse resposta) {
		switch (resposta.getStatus()) {
		case 400:
			throw new NegocioException("Algum parâmetro obrigatório não foi enviado ou é inválido: "
					+ resposta.getStatusInfo().toString());
		case 401:
			throw new NegocioException("Não autorizado: " + resposta.getStatusInfo().toString());
		case 402:
			throw new NegocioException(
					"Erro no servidor de envio: contactar suporte: " + resposta.getStatusInfo().toString());
		case 403:
			throw new NegocioException("Permissão negada: " + resposta.getStatusInfo().toString());
		case 404:
			throw new NegocioException(
					"O endpoint ou o objeto solicitado não existe: " + resposta.getStatusInfo().toString());
		case 415:
			throw new NegocioException("Mídia inválida: " + resposta.getStatusInfo().toString());
		case 422:
			throw new NegocioException("Entidade improcessável: " + resposta.getStatusInfo().toString());
		case 429:
			throw new NegocioException(
					"Você ultrapassou o limite de requisições por minuto: " + resposta.getStatusInfo().toString());
		case 500:
			throw new NegocioException(
					"Ocorreu algum erro inesperado. Contate o suporte técnico: " + resposta.getStatusInfo().toString());
		}
	}

	public static String lerArquivo(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return encoding.decode(ByteBuffer.wrap(encoded)).toString();
	}
}