package sigpro;

import java.io.File;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.xml.JRXmlWriter;

public class JaperReverse {

	public static void main(String[] args) {

		try {

			File jasper = new File(
					"C:\\Users\\sousa\\Documents\\java\\sigpro\\src\\main\\webapp\\resources\\relatorios\\titulos.jasper");
			String jrxml = "C:\\Users\\sousa\\Documents\\java\\sigpro\\src\\main\\webapp\\resources\\relatorios\\titulos.jrxml";
			JasperReport jr = (JasperReport) JRLoader.loadObject(jasper);
			JRXmlWriter.writeReport(jr, jrxml, "UTF-8");

			System.out.println("Ok");

		} catch (JRException e) {
			System.out.println(e.getMessage());
		}

	}

}