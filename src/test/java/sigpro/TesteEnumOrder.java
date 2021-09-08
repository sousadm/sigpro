package sigpro;

import java.util.Arrays;
import java.util.List;

import com.sousa.sigpro.model.tipo.TipoTheme;

public class TesteEnumOrder {

	public static void main(String[] args) {

		List<TipoTheme> list = Arrays.asList(TipoTheme.values());
		list.sort((s1, s2) -> s1.compareTo(s2));
		TipoTheme[] myArray = new TipoTheme[list.size()];
		list.toArray(myArray);

		for (TipoTheme t : myArray) {
			System.out.println(t);
		}

	}

}
