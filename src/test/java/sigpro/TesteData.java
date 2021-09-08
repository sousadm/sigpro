package sigpro;
import java.util.Date;

import com.sousa.sigpro.util.SuporteData;

public class TesteData {

	public static void main(String[] args) throws Exception {

		Date agora = SuporteData.somenteData(new Date());
		Date hoje = SuporteData.somenteData("08/01/2020");
		System.out.println(agora.compareTo(hoje));

	}

}
