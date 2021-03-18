package servidores;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.FileOutputStream;
import org.apache.commons.codec.binary.Base64;


//com.sun.org.apache.xerces.internal.impl.dv.util.Base64;






public class Sever {
	
	public static void main (String args[]) {
		try{
			int serverPort = 7896; // the server port
			ServerSocket listenSocket = new ServerSocket(serverPort);
			while(true) {
				Socket clientSocket = listenSocket.accept();
				Connection c = new Connection(clientSocket);
			}
		} catch(IOException e) {System.out.println("Listen socket:"+e.getMessage());}
	}
}
class Connection extends Thread {
	// entrada de dados para o servidor
	DataInputStream in;
	// saida de dados do servidor(o que o servidor devolve)
	DataOutputStream out;
	// canal de comuninicação
	Socket clientSocket;
	public Connection (Socket aClientSocket) {
		try {
			clientSocket = aClientSocket;
			in = new DataInputStream( clientSocket.getInputStream());
			out =new DataOutputStream( clientSocket.getOutputStream());
			this.start();
		} catch(IOException e) {System.out.println("Connection:"+e.getMessage());}
	}
	public void run(){
		//String pasta="8f69b";
		String pasta="não";
		try {
			pasta = in.readUTF();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
			//file=new FileInputStream("/home/ubuntu"+"/"+pasta+"/"+pasta+".png");
			
			File file = new File("C:/Users/elnte/OneDrive/Área de Trabalho/"+pasta+"/"+pasta+".png");
			
			// Reading a Image file from file system
            FileInputStream imageInFile = new FileInputStream(file);
            byte imageData[] = new byte[(int) file.length()];
            imageInFile.read(imageData);
            String imageDataString = new String(Base64.encodeBase64(imageData), "UTF-8");
            
            byte[] data=imageDataString.getBytes("UTF-8");
            out.writeInt(data.length);
            out.write(data);
			imageInFile.close();
			System.out.println("imagem " + pasta + " enviada sucesso");
			
		}catch (EOFException e){System.out.println("EOF:"+e.getMessage());
		} catch(IOException e) {System.out.println("readline:"+e.getMessage());
		} finally{ try {clientSocket.close();}catch (IOException e){/*close failed*/}}
		
	
	}
}
