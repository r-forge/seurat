package Data;
import java.util.*;



public class MyStringTokenizer {

	public Vector<String> tokens;



	int i = 0; 

	int size;

	public MyStringTokenizer(String str, int size) {

        this.size = size;		
		
		tokens = new Vector();

		int i = 0;

		while (i < str.length()) {

			int j = i;

			while (j < str.length() && str.charAt(j) != '	') {

				j++;

			}

			String token = "" + str.substring(i, j);

			i = j + 1;

			tokens.add(token);

		}
		
		
		while (tokens.size() < size) tokens.add("NA");



	}

	
	
	
	
	
	public MyStringTokenizer(String str) {

        
		
		tokens = new Vector();

		int i = 0;

		while (i < str.length()) {

			int j = i;

			while (j < str.length() && str.charAt(j) != '	') {

				j++;

			}

			String token = "" + str.substring(i, j);
			//System.out.print(token + " * ");

			i = j + 1;

			tokens.add(token);

		}

		
	



	}
	

	

	public boolean hasMoreTokens() {

		if (i < tokens.size()) return true;

		else return false;

	}

	

	public String nextToken() {

		i++;

		return tokens.elementAt(i-1);

	}

	

}

