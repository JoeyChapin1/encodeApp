package com.standalone;

import java.io.Console;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FilenameUtils;




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
		String parentPath = Paths.get(".").toAbsolutePath().normalize().toString() + File.separator;
		String filePath = parentPath;
		
		filePath += c.readLine(ENTER_PATH);
		
		File plainTextFile = new File(filePath);
		
		// If file does not exist, continue to loop until user quits or enters path to file that exists
		if (!plainTextFile.exists()) {
			option = c.readLine(FILE_DOES_NOT_EXIST_OPTIONS);
			while (!plainTextFile.exists()) {	
				if (option.equals("f")) {
					filePath = parentPath + c.readLine("\n" + ENTER_PATH);
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
		
		// Check to see if Encoded.txt already exists in cwd
		String encodedPathBase = parentPath + FilenameUtils.removeExtension(plainTextFile.getName());
		String encodedPath = encodedPathBase + "_encoded.txt";
		
		Integer version = 1;
		if (new File(encodedPath).exists()) {
			encodedPath = encodedPathBase + "_encoded (1).txt";
			while (new File(encodedPath).exists()) {
				encodedPath = encodedPathBase + "_encoded (" + version.toString() + ").txt";;
				version += 1;
			}
		}
		
		File parent = plainTextFile.getParentFile();
		Path path = Paths.get(plainTextFile.getAbsolutePath());
		
		// Read in bytes and encode
		byte[] byteArray = (Files.readAllBytes(path));
		byte[] encoded = Base64.encodeBase64(byteArray);
		
		// Stream output to new file in cwd, named original filename + "_encoded.txt"
		FileOutputStream out = null;
		if (parent.equals(null)) {
			out = new FileOutputStream(encodedPath);
		} else {
			System.out.println("File was created at: " + encodedPath);
			out = new FileOutputStream(encodedPath);
			
		}
		
		// Allow user to open the file upon encoding if on Windows OS if they wish (eases copy/paste)
		if(isWindows(System.getProperty("os.name"))) {
			option = c.readLine("\nType \'o\' to open the file upon encoding. [Return to skip]: ");
		}
			
		out.write(encoded);
		out.close();
		
		// If user is on Windows OS, allow them to open the file upon encoding if so choose
		if ((option.equals("o") || option.equals("O")) && new File(encodedPath).exists()) {
			Process p = Runtime.getRuntime().exec("rundll32 url.dll, FileProtocolHandler " + encodedPath);
			p.waitFor();
		}
		
		System.out.println("Application closed.");
	}
	
	public static boolean isWindows(String os) {
        return (os.toLowerCase().contains("windows"));
    }
}
