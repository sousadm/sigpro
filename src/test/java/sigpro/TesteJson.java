package sigpro;

import java.util.Date;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.sousa.sigpro.util.SuporteData;

public class TesteJson {

	public static void main(String[] args) throws JSONException {

		JSONObject json = new JSONObject();
		json.put("natureza_operacao", "teste");
		json.put("data_emissao", SuporteData.formataDataToStr(new Date(), "yyyy-MM-dd HH:mm:ss"));

		JSONArray items = new JSONArray();
		for (int i = 0; i < 2; i++) {
			JSONObject nfeItem = new JSONObject();
			nfeItem.put("numero_item", String.valueOf(i));
			items.put(nfeItem);
		}
		json.put("items", items);
		System.out.println(json.toString());
	}
}