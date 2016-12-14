package com.standalone;

import java.io.Console;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.codec.binary.Base64;



public class Encode {
	private static final String FILE_DOES_NOT_EXIST_OPTIONS = "\nThis file does not exist at the specified location. \nEnter another file name (\'f\') or quit (\'q\'): "; 
	private static final String ENTER_PATH = "Enter path of file to be encoded: ";
	
	public static void main(String[] args) throws IOException, InterruptedException {
		String option = "";
		Console c = System.console();
		
		if (c == null) {
			System.err.println("No console.");
			System.exit(1);
		}
		
		// Make filepath entered relative to cwd
		String filePath = Paths.get(".").toAbsolutePath().normalize().toString() + File.separator;
		
		// Check to see if Encoded.txt already exists in cwd
		String encodedPath = filePath + "Encoded.txt";
		 
		// Can be used to change name of filePath -- Which is more elegant? Allow them to choose file path name/whether they want to overwrite?
/*		String newName = "";
		while (new File(encodedPath).exists()) {
			option = c.readLine("File: " + encodedPath + " already exists. Is it okay to overwrite? ['y' to overwrite, 'n' to change file name]");
			if (option.equals("y")) {
				break;
			}
			if (option.equals("n")) {
				newName = c.readLine("Enter new file name: ");
				encodedPath = Paths.get(".").toAbsolutePath().normalize().toString() + File.separator + newName;
			}
		}*/
		
		Integer version = 1;
		if (new File(encodedPath).exists()) {
			String tempPath = Paths.get(".").toAbsolutePath().normalize().toString() + File.separator + "Encoded (1).txt";
			while (new File(tempPath).exists()) {
				tempPath = Paths.get(".").toAbsolutePath().normalize().toString() + File.separator + "Encoded (" + version.toString() + ").txt";;
				version += 1;
			}
			encodedPath = tempPath;
		}
					
		filePath += c.readLine(ENTER_PATH);
		
		File plainTextFile = new File(filePath);
		
		// Continue to loop until user quits or enters path to file that exists
		if (!plainTextFile.exists()) {
			option = c.readLine(FILE_DOES_NOT_EXIST_OPTIONS);
			while (!plainTextFile.exists()) {	
				if (option.equals("f")) {
					filePath = Paths.get(".").toAbsolutePath().normalize().toString() + File.separator;
					filePath += c.readLine("\n" + ENTER_PATH);
					plainTextFile = new File(filePath);
					if (!plainTextFile.exists()) {
						option = c.readLine(FILE_DOES_NOT_EXIST_OPTIONS);
					}
				}
				
				else if (option.equals("q")) {
					System.out.println("\nNo file will be encoded. Exiting application...");
					return;
				}
				
				else {
					option = c.readLine("\nInput: \'" + option + "\' invalid." + FILE_DOES_NOT_EXIST_OPTIONS);
				}
			}
		}
		
		File parent = plainTextFile.getParentFile();
		Path path = Paths.get(plainTextFile.getAbsolutePath());
		
		byte[] byteArray = (Files.readAllBytes(path));
		byte[] encoded = Base64.encodeBase64(byteArray);
		
		FileOutputStream out = null;
		if (parent.equals(null)) {
			out = new FileOutputStream("Encoded.txt");
		} else {
			System.out.println("File was created at: " + encodedPath);
			out = new FileOutputStream(encodedPath);
			
		}
		
		// Allow user to open the file upon encoding if they wish (eases copy/paste)
		option = c.readLine("\nType \'o\' to open the file upon encoding. [Return to skip]: ");
			
		out.write(encoded);
		out.close();
		
		if ((option.equals("o") || option.equals("O")) && new File(encodedPath).exists()) {
			Process p = Runtime.getRuntime().exec("rundll32 url.dll, FileProtocolHandler " + encodedPath);
			p.waitFor();
		}
		
		System.out.println("Application closed.");
	}
}
