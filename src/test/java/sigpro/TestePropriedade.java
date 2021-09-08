package sigpro;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import javax.inject.Inject;

import com.sousa.sigpro.security.Seguranca;

public class TestePropriedade {

	@Inject
	private static Seguranca seguranca;

	public static Properties getProp() throws IOException {
		Properties props = new Properties();
		FileInputStream file = new FileInputStream("C:\\Users\\sousa\\Documents\\JAVA\\sigpro\\src\\main\\webapp\\META-INF\\Parametros.properties");
		props.load(file);
		return props;
	}

	public static void main(String args[]) throws IOException {

//		System.out.println(seguranca.pathClass("META-INF") + "/Parametros.properties");

		 Properties prop = getProp();
		 System.out.println(prop.getProperty("URL_APLICACAO"));
	}

}