package servidores;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;

//com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

public class Sever {

	public static void main(String args[]) {
		try {
			int serverPort = 7896; // the server port
			ServerSocket listenSocket = new ServerSocket(serverPort);
			while (true) {
				Socket clientSocket = listenSocket.accept();
				Connection c = new Connection(clientSocket);
			}
		} catch (IOException e) {
			System.out.println("Listen socket:" + e.getMessage());
		}
	}
}

class Connection extends Thread {
	// entrada de dados para o servidor
	DataInputStream in;
	// saida de dados do servidor(o que o servidor devolve)
	DataOutputStream out;
	// canal de comuninica��o
	Socket clientSocket;

	public Connection(Socket aClientSocket) {
		try {
			clientSocket = aClientSocket;
			in = new DataInputStream(clientSocket.getInputStream());
			out = new DataOutputStream(clientSocket.getOutputStream());
			this.start();
		} catch (IOException e) {
			System.out.println("Connection:" + e.getMessage());
		}
	}

	public void run() {
		// String pasta="8f69b";
		String pasta = "n�o";
		try {
			pasta = in.readUTF();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// nova parte

		BufferedImage img = null;

		// File fileResizing = new File("C:/Users/elnte/OneDrive/�rea de
		// Trabalho/"+pasta+"/"+pasta+".png");
		String s1="/home/ubuntu" + "/" + pasta + "/" + pasta + ".png";
		System.out.println("ler o aqruivo "+ s1);
		File fileResizing = new File(s1);

		try {
			img = ImageIO.read(fileResizing);

			img = scale(img);
			//inicio da redundancia que tem que ser ermovida
			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			// ImageIO.write(img, "jpg", baos);
			ImageIO.write(img, "jpg", baos);

			byte[] encodedArray = baos.toByteArray();
			baos.flush();

			ByteArrayInputStream bis = new ByteArrayInputStream(encodedArray);
			BufferedImage bImage2 = ImageIO.read(bis);
			// fim da redundancia 
			System.out.println("imagem que retornou do m�todo "+img );
			System.out.println("imagem do passos extras "+bImage2 );
			// ImageIO.write(bImage2, "jpg", new File("C:\\Users\\elnte\\OneDrive\\�rea de
			// Trabalho\\8f69b\\saida.jpg") );
			String s2="/home/ubuntu" + "/" + pasta + "/saida.jpg";
			System.out.println("salvar o aqruivo "+s2);
			//ImageIO.write(bImage2, "jpg", new File(s2));
			ImageIO.write(img, "jpg", new File(s2));

		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			// file=new FileInputStream("/home/ubuntu"+"/"+pasta+"/"+pasta+".png");

			// File file = new File("C:/Users/elnte/OneDrive/�rea de
			// Trabalho/"+pasta+"/"+pasta+".png");
			String s2="/home/ubuntu" + "/" + pasta + "/saida.jpg";
			System.out.println("ler o aqruivo recem salvo " + s2);
			File file = new File(s2);

			// Reading a Image file from file system
			FileInputStream imageInFile = new FileInputStream(file);
			byte imageData[] = new byte[(int) file.length()];
			imageInFile.read(imageData);
			String imageDataString = new String(Base64.encodeBase64(imageData), "UTF-8");

			byte[] data = imageDataString.getBytes("UTF-8");
			out.writeInt(data.length);
			out.write(data);
			imageInFile.close();
			System.out.println("imagem " + pasta + " enviada sucesso");
			boolean result = Files.deleteIfExists(file.toPath());
			System.out.println("arquivo s2 deletado: " + result);

		} catch (EOFException e) {
			System.out.println("EOF:" + e.getMessage());
		} catch (IOException e) {
			System.out.println("readline:" + e.getMessage());
		} finally {
			try {
				clientSocket.close();
			} catch (IOException e) {
				/* close failed */}
		}

	}

	public static BufferedImage scale(BufferedImage img) {
		
		try {
			if (!(img.equals(null))) {
				System.out.println("A imagem n�o chegou nula no metodo de redu��o de imagem" );
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("A imagem chegou nula no metodo de redu��o de imagem" );
		}
		
		int height = img.getHeight();
		int width = img.getWidth();
		int targetWidth = (int) (width * 0.5);
		int targetHeight = (int) (height * 0.5);
		int type = (img.getTransparency() == Transparency.OPAQUE) ? BufferedImage.TYPE_INT_RGB
				: BufferedImage.TYPE_INT_ARGB;
		BufferedImage ret = img;
		BufferedImage scratchImage = null;
		Graphics2D g2 = null;

		int w = img.getWidth();
		int h = img.getHeight();

		int prevW = w;
		int prevH = h;

		do {
			if (w > targetWidth) {
				w /= 2;
				w = (w < targetWidth) ? targetWidth : w;
			}

			if (h > targetHeight) {
				h /= 2;
				h = (h < targetHeight) ? targetHeight : h;
			}

			if (scratchImage == null) {
				scratchImage = new BufferedImage(w, h, type);
				g2 = scratchImage.createGraphics();
			}

			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g2.drawImage(ret, 0, 0, w, h, 0, 0, prevW, prevH, null);

			prevW = w;
			prevH = h;
			ret = scratchImage;
		} while (w != targetWidth || h != targetHeight);

		if (g2 != null) {
			g2.dispose();
		}

		if (targetWidth != ret.getWidth() || targetHeight != ret.getHeight()) {
			scratchImage = new BufferedImage(targetWidth, targetHeight, type);
			g2 = scratchImage.createGraphics();
			g2.drawImage(ret, 0, 0, null);
			g2.dispose();
			ret = scratchImage;
		}
		
		try {
			if (!(ret.equals(null))) {
				System.out.println("A imagem n�o est� retornando nulna o metodo de redu��o de imagem" );
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("A imagem est� retornando nulna o metodo de redu��o de imagem" );
		}

		return ret;

	}
}
