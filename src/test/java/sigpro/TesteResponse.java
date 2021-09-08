package sigpro;

import com.google.gson.Gson;
import com.sousa.sigpro.service.CobrancaAsaasService.Customer;

public class TesteResponse {

	static String valor = "{'object':'customer','id':'cus_000014743212','dateCreated':'2020-07-18','name':'alex car','email':'alexcarcosta3@gmail.com','company':null,'phone':'98997549161','mobilePhone':'99999999999','address':'Padre Manuel Primo','addressNumber':'254','complement':null,'province':'Olavo Oliveira','postalCode':'60351120','cpfCnpj':'08280924000109','personType':null,'deleted':false,'additionalEmails':null,'externalReference':'000039','notificationDisabled':false,'observations':null,'city':7072,'state':'CE','country':'Brasil','foreignCustomer':false}";

	public static void main(String[] args) {

		try {

			valor = valor.replaceAll("\'", "\"");
			Gson g = new Gson();
			Customer cobraClienteResp = g.fromJson(valor, Customer.class);


//			Read more: https://www.java67.com/2016/10/3-ways-to-convert-string-to-json-object-in-java.html#ixzz6SZpHeoNw
			
			System.out.println(cobraClienteResp);

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

}
