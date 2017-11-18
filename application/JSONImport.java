package application;

import java.io.*;
import application.model.*;

public class JSONImport {
	
	public static Flughafen createFlughafen(String jsonPath) {
		InputStream is = ClassLoader.getSystemResourceAsStream(jsonPath);
		// TODO
		
		return new Flughafen();
	}
}
