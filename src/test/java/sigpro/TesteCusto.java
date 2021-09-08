package sigpro;

import com.sousa.sigpro.model.Produto;

public class TesteCusto {

	public static void main(String[] args) {

		Produto produto = new Produto();
		produto.setPrecoCompra(10);

		produto.getCusto().setCredito(50);
		produto.getCusto().setImposto(0);
		produto.getCusto().setDespesa(0);
		produto.getCusto().setFrete(5);

		produto.getCusto().setMargem(20);
		produto.getCusto().setLucro(20);
		produto.getCusto().setResidual(5);
		produto.getCusto().setTributo(0);

		System.out.println("Custo aquisição: " + produto.getPrecoCusto());
		System.out.println("Preço de venda: " + produto.getPrecoVendaSugerido());

	}

}
