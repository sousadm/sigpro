package sigpro;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellValue;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.sousa.sigpro.util.Suporte;

public class TesteXLS_2 {

	String letras = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

	static HSSFWorkbook workbook;

	public static void main(String[] args) throws IOException {

//		String SAMPLE_XLSX_FILE_PATH = "/home/sousa/Documentos/sigpro/src/test/java/sigpro/teste.xls";
		String SAMPLE_XLSX_FILE_PATH = "D:/sousa/Java/sigpro/src/test/java/sigpro/teste.xls";
		String SAMPLE_JSON_FILE = "D:/sousa/Java/sigpro/src/test/java/sigpro/testeXLS.json";

		FileInputStream file = new FileInputStream(new File(SAMPLE_XLSX_FILE_PATH));
		workbook = new HSSFWorkbook(file);

//		List<ExcelParam> lista = new ArrayList<>();
//		lista.add(new ExcelParam("dados", "tempo previsto em dias", "B1", 200));
//		lista.add(new ExcelParam("dados", "quantidade de operarios", "B2", 100));
//		lista.add(new ExcelParam("dados", "custo de hora/homem", "B3", 11.0));
//		lista.add(new ExcelParam("dados", "outras despesas", "B4", 1000.0));
//		lista.add(new ExcelParam("dados", "custo de material por unidade", "B5", 70000.00));
//		lista.add(new ExcelParam("dados", "quantidade de unidades construidas", "B6", 40));
//		lista.add(new ExcelParam("dados", "margem de segurança", "B7", 0.15));
//
//		Gson gson = new Gson();
//		JsonElement element = gson.toJsonTree(lista, new TypeToken<List<Customer>>() {
//		}.getType());
//		System.out.println(element.toString());

		String conteudo = Suporte.lerArquivo(SAMPLE_JSON_FILE, StandardCharsets.UTF_8);

//		String jsonRetorno = "{\"sheet\":\"dados\",\"titulo\":\"valor final\",\"adressCell\":\"C10\",\"valor\":null}";
//		String jsonLista = "[{\"sheet\":\"dados\",\"titulo\":\"tempo previsto em dias\",\"adressCell\":\"B1\",\"valor\":250},{\"sheet\":\"dados\",\"titulo\":\"quantidade de operarios\",\"adressCell\":\"B2\",\"valor\":100},{\"sheet\":\"dados\",\"titulo\":\"custo de hora/homem\",\"adressCell\":\"B3\",\"valor\":11.0},{\"sheet\":\"dados\",\"titulo\":\"outras despesas\",\"adressCell\":\"B4\",\"valor\":1000.0},{\"sheet\":\"dados\",\"titulo\":\"custo de material por unidade\",\"adressCell\":\"B5\",\"valor\":70000.0},{\"sheet\":\"dados\",\"titulo\":\"quantidade de unidades construidas\",\"adressCell\":\"B6\",\"valor\":40},{\"sheet\":\"dados\",\"titulo\":\"margem de segurança\",\"adressCell\":\"B7\",\"valor\":0.15}]\n";

		double valor = getValorEstruturado(conteudo);
		System.out.println(valor);

//		HSSFSheet sheet = null;
//		HSSFRow row;
//		HSSFCell cell;
//
//		for (ExcelParam param : lista) {
//
//			int coluna = getIndiceColuna(param.adressCell);
//			int linha = getIndiceLinha(param.adressCell);
//
//			sheet = workbook.getSheet(param.sheet);
//			row = sheet.getRow(linha);
//			cell = row.getCell(coluna);
//
//			try {
//
//				if (param.getValor().getClass().getName() == "java.lang.Integer") {
//					cell.setCellValue((Integer) param.getValor());
//				} else if (param.getValor().getClass().getName() == "java.lang.Double") {
//					cell.setCellValue((Double) param.getValor());
//				} else if (param.getValor().getClass().getName() == "java.lang.String") {
//					cell.setCellValue((String) param.getValor());
//				} else if (param.getValor().getClass().getName() == "java.lang.Boolean") {
//					cell.setCellValue((Boolean) param.getValor());
//				} else if (param.getValor().getClass().getName() == "java.lang.Date") {
//					cell.setCellValue((Date) param.getValor());
//				}
//
//			} catch (Exception e) {
//				// TODO: handle exception
//			}
//
//		}
//
//		HSSFCell cellTotal = sheet.getRow(9).getCell(2);
//		HSSFFormulaEvaluator fe = new HSSFFormulaEvaluator(workbook);
//		CellValue cv = fe.evaluate(cellTotal);
//
//		System.out.println(cv.getNumberValue());

	}

	public static class ExcelParam {

		public String sheet;
		public String titulo;
		public String adressCell;
		public String adressLista;
		public Object valor;

		public ExcelParam() {

		}

		public ExcelParam(String sheet, String titulo, String adressCell, Object valor) {
			this.sheet = sheet;
			this.titulo = titulo;
			this.adressCell = adressCell;
			this.valor = valor;
		}

		public String getTitulo() {
			return titulo;
		}

		public void setTitulo(String titulo) {
			this.titulo = titulo;
		}

		public String getAdressCell() {
			return adressCell;
		}

		public void setAdressCell(String adressCell) {
			this.adressCell = adressCell;
		}

		public String getAdressLista() {
			return adressLista;
		}

		public void setAdressLista(String adressLista) {
			this.adressLista = adressLista;
		}

		public Object getValor() {
			return valor;
		}

		public void setValor(Object valor) {
			this.valor = valor;
		}

		public String getSheet() {
			return sheet;
		}

		public void setSheet(String sheet) {
			this.sheet = sheet;
		}

	}

	public static int getIndexColuna(String adress) {

		String res = "";
		int index = -1;
		int resposta = -1;
		char[] lst = "abcdefghijklmnopqrstuvwxyz".toUpperCase().toCharArray();

		/************* PRIMEIRO CICLO *************/
		for (int i = 0; i < lst.length; i++) {
			index++;
			res = String.valueOf(lst[i]);
			// System.out.println(res);
			if (res.equals(adress.toUpperCase())) {
				resposta = index;
				break;
			}
		}

		/************* SEGUNDO CICLO *************/
		if (resposta < 0) {
			for (int i = 0; i < lst.length; i++) {
				for (int x = 0; x < lst.length; x++) {
					index++;
					res = String.valueOf(lst[i]) + String.valueOf(lst[x]);
					// System.out.println(res);
					if (res.equals(adress.toUpperCase())) {
						resposta = index;
						break;
					}
				}
			}
		}

		/************* TERCEIRO CICLO *************/
		if (resposta < 0) {
			for (int i = 0; i < lst.length; i++) {
				for (int x = 0; x < lst.length; x++) {
					for (int y = 0; y < lst.length; y++) {
						index++;
						res = String.valueOf(lst[i]) + String.valueOf(lst[x]) + String.valueOf(lst[y]);
						// System.out.println(res);
						if (res.equals(adress.toUpperCase())) {
							resposta = index;
							break;
						}
					}
				}
			}
		}

		return resposta;
	}

	public static int getIndiceColuna(String valor) {
		String textoColuna = valor;
		textoColuna = textoColuna.replaceAll("[0-9]", "");
		return getIndexColuna(textoColuna);
	}

	public static int getIndiceLinha(String valor) {
		String textoLinha = valor;
		textoLinha = textoLinha.replaceAll("[^0-9]", "");
		return Integer.valueOf(textoLinha) - 1;
	}

	public static double getValorEstruturado(String json) {
		try {

			HSSFSheet sheet = null;
			HSSFRow row;
			HSSFCell cell;

			JSONObject jsonObject;
			JSONArray jsonLista;
			JSONParser parser = new JSONParser();

			jsonObject = (JSONObject) parser.parse(json);
			jsonLista = (JSONArray) jsonObject.get("params");

			for (int i = 0; i < jsonLista.size(); i++) {
				JSONObject obj = (JSONObject) jsonLista.get(i);

				int coluna = getIndiceColuna((String) obj.get("adressCell"));
				int linha = getIndiceLinha((String) obj.get("adressCell"));

				sheet = workbook.getSheet((String) obj.get("sheet"));
				row = sheet.getRow(linha);
				cell = row.getCell(coluna);

				try {

					Object valor = (Object) obj.get("valor");

					if (valor.getClass().getName() == "java.lang.Integer") {
						cell.setCellValue((Integer) valor);
					} else if (valor.getClass().getName() == "java.lang.Long") {
						cell.setCellValue((Long) valor);
					} else if (valor.getClass().getName() == "java.lang.Double") {
						cell.setCellValue((Double) valor);
					} else if (valor.getClass().getName() == "java.lang.String") {
						cell.setCellValue((String) valor);
					} else if (valor.getClass().getName() == "java.lang.Boolean") {
						cell.setCellValue((Boolean) valor);
					} else if (valor.getClass().getName() == "java.lang.Date") {
						cell.setCellValue((Date) valor);
					}

				} catch (Exception e) {
					// TODO: handle exception
				}

			}

			int coluna = getIndiceColuna((String) jsonObject.get("retorno"));
			int linha = getIndiceLinha((String) jsonObject.get("retorno"));
			
			sheet = workbook.getSheet((String) jsonObject.get("sheet"));
			row = sheet.getRow(linha);
			cell = row.getCell(coluna);
			
			HSSFFormulaEvaluator fe = new HSSFFormulaEvaluator(workbook);
			CellValue cv = fe.evaluate(cell);

			return cv.getNumberValue();

		} catch (Exception e) {
			return 0;
		}
	}

}