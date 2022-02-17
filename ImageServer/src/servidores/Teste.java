package servidores;

import java.util.ArrayList;
import java.util.List;

public class Teste {

	public static void main(String[] args) {

		List <Integer> l = new ArrayList<>();
		
		for (int i =0; i<5; i++){
			l.add(i);
		}
		
	System.out.println(l);
	System.out.println(l.get(l.size()-1));

	}

}
