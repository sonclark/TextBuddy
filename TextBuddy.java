
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Assumptions: 
 * - When user input command that required additional information (such as add or delete),there will be handler 
 * 	 to ensure that the information is not empty.
 * - Empty command will be access as invalid command.
 * - When user wants to delete line number that is out of bound, the program will not allow.
 * - The program will automatically create new file if the file that user wants to access is non existent
 * - The program will not write empty line into the file.
 */


public class TextBuddy {
	// Possible messages that can be showed to the user
	private static final String MESSAGE_ADDED = "added to %1$s: "+'"'+"%2$s"+'"';
	private static final String MESSAGE_DELETED = "deleted from %1$s: "+'"'+"%2$s"+'"';
	private static final String MESSAGE_CLEARED = "all content deleted from %1$s";
	private static final String MESSAGE_SORTED = "file has been sorted";
	private static final String MESSAGE_RETURN_SEARCH = "total %1$s line(s) found";
	private static final String MESSAGE_EMPTY_FILE = "%1$s is empty";
	private static final String MESSAGE_WELCOME = "Welcome to TextBuddy.";
	private static final String MESSAGE_READY = "%1$s is ready for use.";

	
	//Another set of messages, mostly used when invalid situations take place.
	private static final String INVALID_COMMAND = "invalid command.";
	private static final String INVALID_LINE_NUMBER = "line %1$s does not exist or out of bound.";
	private static final String INVALID_ADD_FORMAT = "invalid add format. Correct format : add <line_to_add>";
	private static final String INVALID_DELETE_FORMAT = "invalid delete format. Correct format : delete <line_number>";
	
	
	//Indexes and first line number defined for later usage.
	private static final int FIRST_COMMAND_INDEX = 0;
	private static final int SECOND_COMMAND_INDEX = 1;
	private static final int FIRST_LINE_NUMBER = 1;
	

	//All possible command type
	enum COMMAND_TYPE {
		ADD , DELETE ,DISPLAY , CLEAR, INVALID, SORT , SEARCH ,EXIT ; 
	}
	
	
	public TextBuddy(String fileName) throws IOException {
		processInput(fileName);
	}


	//contentList is used to store all content of the file into an ArrayList
	private static ArrayList<String> contentList = new ArrayList<String>();
	//Global Scanner object to scan for input
	private static Scanner sc = new Scanner(System.in);
	//Reader and writer defined for file handling
	private static BufferedReader  reader = null;
	private static BufferedWriter  writer = null;
	//Define an empty String for fileName. Will be use globally for convenience.
	static String fileName = "";


	public static void main(String[] args) throws IOException{
		showWelcomeMessage(args);
		runProgram();	
	}


	private static void runProgram() throws IOException {
		while (true) {
			askForCommand();
			String command = readUserInput();
			showUserCommand(command);
			String feedback = executeCommand(command);
			showMessage(feedback);

		}
	}


	private static void showUserCommand(String command) {
		System.out.println(command+"\r\n");
	}

	
	private static void askForCommand() {
		System.out.print("command:");
	}


	private static void showWelcomeMessage(String[] args) throws IOException {
		fileName = args[0];
		showMessage(MESSAGE_WELCOME + processInput(fileName));
	}

	
	/**
	 * This operation is used to process the file ( including load content and prepare reader and writer)
	 * Once finish, it will return message to notify that the file is ready
	 * 
	 * @param fileName
	 * 			 The name of the file we will be working with
	 * @return a ready message with fileName
	 */
	static String processInput (String fileName) throws IOException{
		File file = new File(fileName);
		if(!file.exists()){
			file.createNewFile();
			setUpReaderAndWriter(fileName);
		}
		else{
			setUpReaderAndWriter(fileName);
		}
		return String.format(MESSAGE_READY,fileName);
	}
	

	private static String readUserInput() {
		String command = sc.nextLine();
		return command;
	}


	private static String executeCommand(String command) throws IOException{
		if (command.trim().equals("")){	
			return String.format(INVALID_COMMAND);
		}

		String commandTypeString = getCommandType(command);

		COMMAND_TYPE commandType = determineCommandType(commandTypeString);

		switch (commandType) {
		case ADD:
			return addLine(command);
		case DELETE:
			return deleteLine(command);
		case CLEAR:
			return clear();
		case DISPLAY:
			return displayContent(fileName);
		case INVALID:
			return String.format(INVALID_COMMAND);
		case EXIT:
			System.exit(0);
		default:
			//throw an error if the command is not recognized
			throw new Error("Command type does not exist");
		}
	}
	
	
	/**
	 * This operation is used to determine the command type that the user wishes to execute
	 * 
	 * @param commandTypeString
	 * 			 the string that contain the command type
	 * @return COMMAN_TYPE object
	 * 		     corresponding command type
	 */
	private static COMMAND_TYPE determineCommandType(String commandTypeString){
		if (commandTypeString == null){
			throw new Error("command type string cannot be null!");
		}

		if (commandTypeString.equalsIgnoreCase("add")) {
			return COMMAND_TYPE.ADD;
		} else if (commandTypeString.equalsIgnoreCase("display")) {
			return COMMAND_TYPE.DISPLAY;
		} else if (commandTypeString.equalsIgnoreCase("delete")) {
			return COMMAND_TYPE.DELETE;
		} else if (commandTypeString.equalsIgnoreCase("clear")) {
			return COMMAND_TYPE.CLEAR;
		} else if (commandTypeString.equalsIgnoreCase("sort")) {
			return COMMAND_TYPE.SORT;
		} else if (commandTypeString.equalsIgnoreCase("search")) {
			return COMMAND_TYPE.SEARCH;
		} else if (commandTypeString.equalsIgnoreCase("exit")) {
			return COMMAND_TYPE.EXIT;
		} else {
			return COMMAND_TYPE.INVALID;
		}
	}
	

	/**
	 * This operation is used to set up reader and writer on the file
	 * 
	 * @param fileName
	 * 			 The name of the file we will be working with
	 */
	private static void setUpReaderAndWriter(String fileName)
			throws FileNotFoundException, IOException {
		readFromFile(fileName);
		//writeToFile();
	}


	private static void showMessage(String message){
		System.out.println(message+"\r\n");
	}
	
	
	/**
	 * This operation is used to display all content of the file
	 * 
	 * @param fileName
	 * 			 The name of the file we will be working with
	 * @return finalString
	 *  		 A string contains all content of the file
	 */
	static String displayContent(String fileName) throws IOException{
		readFromFile(fileName);
		String finalString ="";
		if(contentList.isEmpty()){
			return String.format(MESSAGE_EMPTY_FILE,fileName);
		}
		else{
			int lineNumber = FIRST_LINE_NUMBER;
			for(String s : contentList){
				String outputLine = constructOutput(s,lineNumber);
				finalString += outputLine;
				lineNumber++;
			}
		}
		finalString = removeEmptyLine(finalString);
		return finalString;
	}
	

	/**
	 * This operation is used to remove and empty line at the end of the display message
	 */
	private static String removeEmptyLine(String finalString) {
		finalString = finalString.substring(0, finalString.length()-2);
		return finalString;
	}
	

	/**
	 * This operation is used to construct a proper output for displaying
	 */
	static String constructOutput(String currentLine, int lineNumber) {
		String outputLine = lineNumber+". "+currentLine+"\r\n";
		return outputLine;
	}
	

	/**
	 * This operation is used to add a new line specified by the user to the end of the file
	 * @param userInput
	 * 			 The line that user wants to be added into the file
	 * @return status of the addition (Invalid or successful)
	 */
	static String addLine(String userInput) throws IOException{
		String inputLine = getUserInput(userInput);
		if(inputLine.equalsIgnoreCase("")){
			return String.format(INVALID_ADD_FORMAT);
		}
		contentList.add(inputLine);	
		writeToFile();
		return String.format(MESSAGE_ADDED,fileName,inputLine);
	}

	
	/**
	 * This operation is used to write all content from an ArrayList contentList to the designated file.
	 * Mainly used to save file to the most updated version
	 */
	private static void writeToFile() throws IOException {
		writer = new BufferedWriter(new FileWriter(fileName,false));
		if(contentList.isEmpty()){
			writer.write("");
		}
		else{
			for (String s : contentList){
				writer.write(s);
				writer.newLine();
			}
		}
		closeWriter();
	}
	
	
	/**
	 * This operation is used to read all content from the designated file to contentList.
	 * Mainly used to get most updated content in order for the program to work with
	 */
	private static void readFromFile(String fileName)
			throws FileNotFoundException, IOException {
		contentList.clear();
		reader = new BufferedReader(new FileReader(fileName));
		String currentLine;
		while ((currentLine = reader.readLine()) != null) {
			contentList.add(currentLine);
		}
		reader.close();
	}
	

	/**
	 * This operation is used to delete a line from the file, specified by the line number entered by the user
	 * 
	 * @param userInput
	 * 			 The line number that user wants to delete
	 * @return status of deletion (invalid or success)
	 */
	static String deleteLine(String userInput) throws IOException{
		String input = getUserInput(userInput);
		if(input.equalsIgnoreCase("")){
			return String.format(INVALID_DELETE_FORMAT);
		}
		
		int lineNumber = Integer.parseInt(getUserInput(userInput));
		if(contentList.isEmpty()){
			return String.format(MESSAGE_EMPTY_FILE,fileName);

		}
		
		if(lineNumber > 0 && lineNumber <= contentList.size() ){
			int actualIndex = lineNumber - 1;
			String deletedLine = contentList.get(actualIndex);
			contentList.remove(actualIndex);
			writeToFile();
			return String.format(MESSAGE_DELETED,fileName,deletedLine);
		}
		else{
			return String.format(INVALID_LINE_NUMBER,lineNumber);
		}
	}

	
	/**
	 * This operation is used to clear all content in the file
	 */
	static String clear() throws IOException{
		contentList.clear();
		writeToFile();
		return String.format(MESSAGE_CLEARED,fileName);
	}
	

	/**
	 * This operation is used to close the writer used on the file
	 */
	private static void closeWriter() throws IOException{
		writer.flush();
		writer.close();
	}
	

	/**
	 * This operation is used to extract the command type that the user used
	 * 
	 * @param command
	 *  		The full command string that the user has entered
	 * @return commandType
	 * 			The command type that the user entered		
	 */
	private static String getCommandType(String command){
		String commandType= splitInputCommand(command)[FIRST_COMMAND_INDEX];
		return commandType;
	}

	
	/**
	 * This operation is used to determine that information that the user has input
	 * 
	 * @param command
	 *  		The full command string that the user has entered
	 * @return userInput
	 * 			The input that the user has entered
	 */
	private static String getUserInput(String command){
		String[] commandArray = splitInputCommand(command);
		
		if(commandArray.length == 1){
			return "";
		}
		
		String userInput = splitInputCommand(command)[SECOND_COMMAND_INDEX];
		return userInput;
	}

	
	/**
	 * This operation is used to separate command type and user input from the full command string that
	 * the user has entered.These information will then be stored in an Array.
	 * 
	 * @param command
	 * 			 The full command string that the user has entered
	 * @return commandArray
	 * 			 An Array that contains both command type and user input.
	 */
	private static String[] splitInputCommand(String command){
		String[] commandArray;
		if(command.trim().contains(" ")){
			commandArray = command.trim().split(" ",2);
		}
		else{
			commandArray = new String[1];
			commandArray[FIRST_COMMAND_INDEX] = command.trim();
		}
		return commandArray;
	}

}
