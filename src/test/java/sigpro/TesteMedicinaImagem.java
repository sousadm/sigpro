package sigpro;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

public class TesteMedicinaImagem {

	static String nome = "C:/Users/sousa/OneDrive/mrp21sys/teste+medicina.bmp";

	public static void main(String[] args) {

		try {

			byte[] bytes = Files.readAllBytes(Paths.get(nome));
			for (byte bt : bytes) {
				System.out.print(bt);
			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

	public static byte[] writeImage(BufferedImage imagem, String formato) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ImageIO.write(imagem, formato, bos);
		bos.flush();
		return bos.toByteArray();
	}

}
