package propositionalCalculatorV1_0;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;
import java.util.Map.Entry;

/**
 * This class provides the interface between the user and the program. It provides methods for
 *  prompting the user to make choices and enter expressions/values via the console.
 * NOTE: My apologies, this class is a mess. I should have separated the print statements into
 *  another class... Maybe later.
 * @author Kenneth Chin
 *
 */
public class ConsoleUI {

	private Scanner inStream;
	
	//A list of all reserved characters. Contains(starts with) all conditionals. Note: "NOT" (!) is not a conditional.
	char[] validChars = {'&', '|', '^', '>', '=', '(', ')', '!', ' '};
	//A list of all conditionals. Note: NOT (!) is not a conditional.
	char[] conditionals = {'&', '|', '^', '>', '='};
	
	public ConsoleUI(){
		inStream = new Scanner( System.in );
	}
	
	public void printWelcome(){
		System.out.println("********************************************");
		System.out.println("*                                          *");
		System.out.println("*  Welcome to the PropositionalCalculator  *");
		System.out.println("*                                          *");
		System.out.println("********************************************");
		System.out.println();
	}
	/**
	 * Prompt the user to enter a propositional logic expression. Go to the instruction prompt
	 *  if "1" is entered. Return null if "2" is entered. Prompt again if nothing was entered.
	 *  Otherwise return the expression entered, minus any whitespace.
	 * @return The String expression entered by the user, minus any whitespace characters, or
	 *  null if the user entered "2".
	 */
	public String exPrompt(){
		System.out.println("Type \"1\" for instructions or \"2\" to quit. Otherwise;");
		System.out.print("Please enter a propositional expression:\n");
		String expression = inStream.nextLine();
		if(expression == null || expression.equals(""))
			expression = exPrompt();
		char firstChar = expression.charAt(0);
		if(firstChar == '1'){
			promptInstructions();
			expression = exPrompt();
			if(expression == null || expression.charAt(0) == '2')
				return null;
		}
		else if(firstChar == '2')
			return null;
		//Validate the expression. Re-prompt if invalid.
		if(!isValidExpression(expression)){
			System.out.println();
			System.out.println("\"" + expression + "\" is not a valid expression!");
			expression = exPrompt();
			if(expression == null || expression.charAt(0) == '2')
				return null;
		}
		//Remove all whitespace characters and return.
		return expression.replaceAll("\\s+","");
	}
	
	/**
	 * Prompts the user to enter a truth value for each String variable in operandList.
	 *  After each prompt, the boolean value is mapped to the variable's unique key value.
	 *  If the user enters an invalid response, the user is re-prompted until a valid
	 *  response is entered. After all values are mapped, a HashMap<String, Boolean> is returned
	 *  containing the variable(key)/boolean(value) pairs.
	 * NOTE: See the findOperands(String) method to easily create the ArrayList.
	 * @param operandList An ArrayList<String> containing all the unique variables in an expression.
	 * @return A HashMap<String, Boolean> containing pairs of variable(keys) from the given ArrayList,
	 *  and boolean(values) as given from the user prompts.
	 */
	public HashMap<String, Boolean> valPrompt(ArrayList<String> operandList){
		HashMap<String, Boolean> map = new HashMap<String, Boolean>(operandList.size() - 1);
		System.out.println();
		System.out.println("Please enter a True/False(T/F) value for each operand:");
		for(String key:operandList){
			boolean value = false;
			boolean valid = false;
			while(!valid){
				System.out.print(key + " is True or False(T/F)? ");
				String answer = inStream.nextLine();
				if(answer.equalsIgnoreCase("true") || answer.equalsIgnoreCase("t")){
					value = true;
					valid = true;
				}
				else if(answer.equalsIgnoreCase("false") || answer.equalsIgnoreCase("f")){
					value = false;
					valid = true;
				}
				else
					System.out.println("*** \"" + answer + "\" is not a valid response! ***");
			}
			map.put(key, new Boolean(value));
		}
		return map;
	}
	
	
	/**
	 * Determines if the user wants to view the expression's satisfiability. Repeats until
	 *  a valid answer is given.
	 * @return A boolean. True if the user indicates that they want to view the expression's
	 *  satisfiability; false if they indicate they do not.
	 */
	public boolean promptForSatisfiability(){
		System.out.println();
		System.out.println("Would you like to find all variable value combinations for which");
		System.out.print  ("  this expression is true (its satisfiability)? Enter \"y\" or \"n\": \n");
		String answer = inStream.nextLine();
		if(answer.equalsIgnoreCase("y") || answer.equalsIgnoreCase("yes"))
			return true;
		if(answer.equalsIgnoreCase("n") || answer.equalsIgnoreCase("no"))
			return false;
		System.out.println();
		System.out.println("\"" + answer + "\" is not a valid answer. Please enter \"y\" for yes or \"n\" for no.");
		return promptForSatisfiability();
	}
	
	/**
	 * Prints all values for each variable in the given expression which make the expression true.
	 *  The user is prompted to press enter after each set of values are given.
	 *  If the expression is unsatisfiable, a statement prints indicating such.
	 * @param maps An ArrayList<HashMap<String, Boolean>> containing all combinations of variable values
	 *  that satisfy the given expression. If the expression is unsatisfiable, maps.isEmpty().
	 * @param expression The expression String that was tested for satisfiability.
	 */
	public void printSatisfiability(ArrayList<HashMap<String, Boolean>> maps, String expression){
		int size = maps.size();
		if(size<1){
			System.out.println();
			System.out.println("The expression \"" + expression + "\"");
			System.out.println("  has no variable combinations for which it true(Unsatisfiable).");
		}
		else{
			System.out.println();
			System.out.println("There are " + size + " result(s). Printing all variable combinations that satisfy the expression...");
			for(int i=0; i<size;i++){
				System.out.println();
				System.out.println("*** Result #" + (i+1) + " ***");
				System.out.println("Expression: " + expression);
				
				HashMap<String, Boolean>         aMap = maps.get(i);
				Set<Entry<String, Boolean>>      set = aMap.entrySet();
				Iterator<Entry<String, Boolean>> iter = set.iterator();
				while(iter.hasNext()){
					Entry<String, Boolean> mapping = iter.next();
					String  key   = mapping.getKey();
					boolean value = mapping.getValue();
					System.out.println("(" + key + ", " + value + ")");
				}
				promptContinue();
			}
			System.out.println();
			System.out.println("All variable combinations have been printed!");
			System.out.println();
		}
	}
	
	
	/**
	 * Creates an ArrayList<String> containing all unique variables found in expression.
	 *  This method is primarily an helper method for valPrompt(ArrayList<String>(),
	 *   but is left public since it could prove useful elsewhere.
	 * @param expression The expression for which variables are to be found.
	 * @return An ArrayList<String> containing all unique variables found in expression.
	 */
	public ArrayList<String> findOperands(String expression){
		ArrayList<String> operands = new ArrayList<String>();
		char[]  expressionChars = expression.toCharArray();
		String  operand = "";
		char lastLetter = 0;
		for(int i=0; i<expressionChars.length; i++){
			char currentChar = expressionChars[i];
			if(!Character.isLetter(lastLetter))
				operand = "";
			if(Character.isLetter(currentChar))
				operand = operand + Character.toString(currentChar);
			else if(!operand.equals("") && !operands.contains(operand)){
				operands.add(operand);
				operand = "";
			}
			//If the last char is a letter, then it is the last operand.
			if(i==expressionChars.length - 1 && Character.isLetter(currentChar)
					&& !operands.contains(operand)){
				operands.add(operand);
			}
			lastLetter = currentChar;
		}
		return operands;
	}
	
	/**
	 * Validates the expression String (albeit loosely). Ensures EQUAL number of
	 *  parentheses, no numbers, duplicate negation, or invalid characters.
	 * @param expression The expression String to be validated.
	 * @return A boolean indicating true, if the expression is valid; False otherwise.
	 */
	private boolean isValidExpression(String expression){
		if(expression == null || expression.equals(""))
			return false;
		char currentChar;
		char lastChar        = '\0';
		int  numOpenParens   = 0;
		int  numClosedParens = 0;
		boolean isValidChar  = false;
		for(int i=0; i<expression.length(); i++){
			currentChar = expression.charAt(i);
			isValidChar = false;
			if(currentChar == ')'){
				numOpenParens++;
				isValidChar = true;
			}
			else if(currentChar == '('){
				numClosedParens++;
				isValidChar = true;
			}
			else if(Character.isLetter(currentChar))
				isValidChar = true;
			else{
				for(int j=0; j<validChars.length;j++){
					if(currentChar == validChars[j]){
						isValidChar = true;
						break;
					}
				}
			}
			if(i!=0 && lastChar=='!' && currentChar=='!')
				return false;
			lastChar = currentChar;
			if(!isValidChar)
				return false;
		}
		if(numOpenParens != numClosedParens)
			return false;
		return true;
	}
	
	
	/**
	 * Prints a statement that the result of expression's evaluation was result.
	 * DOES NOT evaluate an expression.
	 * @param expression The expression String that was evaluated.
	 * @param result A boolean of the expression's truth value after it has been evaluated.
	 */
	public void printResult(String expression, boolean result){
		System.out.println();
		System.out.println("The expression \"" + expression + "\" was evaluated to be: " + result);
	}
	
	/**
	 * Prints a statement asking the user to wait for calculations to complete.
	 */
	public void printWait(){
		System.out.println();
		System.out.println("Calculating, please wait...");
	}
	
	/**
	 * Prints a thank you and quit statement.
	 */
	public void printGoodbye(){
		System.out.println();
		System.out.println("Thanks for using PropositionalCalculator!");
		System.out.println("Exitting program...");
	}
	
	
	/**
	 * Prompts the user to choose a subject to learn more about. Instructions topics
	 *  detail all known usage restrictions.
	 */
	private void promptInstructions(){
		int indentNumber = 5;
		String indent = indent(indentNumber);
		
		System.out.println();
		System.out.println("***************  Instructions  ***************");
		System.out.println();
		System.out.println(indent + "1) Variables");
		System.out.println(indent + "2) Conditionals");
		System.out.println(indent + "3) Negation");
		System.out.println(indent + "4) Order of Operation");
		System.out.println(indent + "5) Credits");
		System.out.println();
		System.out.println( indent +"quick example: (a&b|!(c^d)>(e&f)=!c&d)");
		System.out.println();
		System.out.println("**********************************************");
		System.out.println();
		System.out.println("Enter the number of the instruction that you want more information about, or");
		System.out.print  ("  enter any other value to return to the expression prompt...\n");
		
		String request = inStream.nextLine();
		serveInstructionRequest(request, indent);
	}
	
	/**
	 * Identifies the choice entered by promptInstructions() and executes the coresponding command.
	 * @param request The String value entered by the user.
	 * @param indent A String of whitespaces used to indent instructions.
	 */
	private void serveInstructionRequest(String request, String indent){
		if(request.equals("") || request==null)
			System.out.println();
		else{
			char firstChar = request.charAt(0);
			switch(firstChar){
				case '1' : printVariableInstructions(indent);         break;
				case '2' : printConditionalInstructions(indent);      break;
				case '3' : printNegationInstructions(indent);         break;
				case '4' : printOrderOfOperationInstructions(indent); break;
				case '5' : printCredits();                            break;
				default  : System.out.println();
			}
		}
	}
	
	/**
	 * Used to pause the program until the user has read the the display, and presses enter.
	 */
	private void promptContinue(){
		System.out.println();
		System.out.print("**** Press Enter to continue ****\n");
		inStream.nextLine();
	}
	
	/**
	 * Used to create a String of whitespaces to indent instructions.
	 * @param spaces An int indicating the number of whitespaces to indent.
	 * @return A String of whitespaces who's lengh() is equal to spaces.
	 */
	private String indent(int spaces){
		String indent = "";
		for(int i=0; i<=spaces; i++){
			indent = indent + " ";
		}
		return indent;
	}
	
	
	//Instruction text...
	
	/**
	 * Prints instructions on variable usage, then waits until the user presses enter.
	 * @param indent A String of whitespaces used to indent the instructions.
	 */
	private void printVariableInstructions(String indent){
		System.out.println();
		System.out.println("**** Variables ****");
		System.out.println();
		System.out.println("Variables are operands in a propositional expression that can have a truth value.");
		System.out.println(indent + "All expressons must have at least one variable.");
		System.out.println(indent + "A variable is a letter or group of letters. NO NUMBERS are allowed.");
		System.out.println(indent + "Any number of letters that are not separated by a conditional or parentheses is considered");
		System.out.println(indent + "  a single variable. (ie. a&ab&aac are 3 separate variables)");
		System.out.println(indent + "All whitespace is removed from an expresssion. Therefore (a a&b) and (aa&b) have the same 2 variables.");
		System.out.println(indent + "Variables are case-sensitive. Therefore \"A\" and \"a\" are 2 different variables.");
		promptContinue();
		promptInstructions();
	}
	
	/**
	 * Prints instructions on conditional usage, then waits until the user presses enter.
	 * @param indent A String of whitespaces used to indent the instructions.
	 */
	private void printConditionalInstructions(String indent){
		System.out.println();
		System.out.println("**** Conditionals ****");
		System.out.println();
		System.out.println("Conditionals are operators in a propositional expression.");
		System.out.println(indent + "Each variable may be separated by ONE conditional.");
		System.out.println(indent + "Each set of parentheses may be separated by ONE conditional.");
		System.out.println(indent + "Supported conditionals are: and, or, xOr, implication, and equivalence.");
		System.out.println(indent + "Representaion(SYMBOL) of conditional operators are as follows:");
		System.out.println(indent + indent + "CONDITIONAL : SYMBOL : SYMBOL NAME");
		System.out.println(indent + indent + "and         :   &    : ampersand");
		System.out.println(indent + indent + "or          :   |    : pipe");
		System.out.println(indent + indent + "xOr         :   ^    : carrot");
		System.out.println(indent + indent + "implication :   >    : greater than");
		System.out.println(indent + indent + "equivalence :   =    : equals");
		promptContinue();
		promptInstructions();
	}
	
	/**
	 * Prints instructions on negation usage, then waits until the user presses enter.
	 * @param indent A String of whitespaces used to indent the instructions.
	 */
	private void printNegationInstructions(String indent){
		System.out.println();
		System.out.println("**** Negation ****");
		System.out.println();
		System.out.println("Negation changes the truth value of a single variable or sub-expression to have the opposite value.");
		System.out.println(indent + "A sub-expression is any part of a propositional expression that is surrounded by parentheses.");
		System.out.println(indent + "Negation is represeted by the exclamation point character (\"!\")");
		System.out.println(indent + "Negation can occur ONLY ONCE before a variable or sub-expression. This means:");
		System.out.println(indent + "     !!(a&b) is WRONG! Instead use (!(!(a&b)))");
		System.out.println(indent + "     !!a&b   is WRONG! Instead use ((!(!a))&b)");
		promptContinue();
		promptInstructions();
	}
	
	/**
	 * Prints instructions on order of operations, then waits until the user presses enter.
	 * @param indent A String of whitespaces used to indent the instructions.
	 */
	private void printOrderOfOperationInstructions(String indent){
		System.out.println();
		System.out.println("**** Order of Operation ****");
		System.out.println();
		System.out.println("Order of operation is the order in which sub-expressions and variables are read.");
		System.out.println(indent + "A sub-expression is any part of a propositional expression that is surrounded by parentheses.");
		System.out.println(indent + "First  : The inner-most sub-expression is read first.");
		System.out.println(indent + "Second : Sub-expressions at the same depth are read left-to-right.");
		System.out.println(indent + "           ie. ((a&b)&(c&d)), a&b is evaluated first, then c&d.");
		System.out.println(indent + "Third  : Sub-expressions at higher depths are read before lower-depth sub-expressions.");
		System.out.println(indent + "           ie. ((a&b)&((c&d)&(e&f))), c&d are evaluated first, then e&f, then (c&d)&(e&f),");
		System.out.println(indent + "               then a&b, and lastly ((a&b)&((c&d)&(e&f)))");
		System.out.println(indent + "Lastly : Variables at the same depth are read left-to-right.");
		promptContinue();
		promptInstructions();
	}
	
	/**
	 * Prints program credits, contact info, version number, usage rights, and program guarantees,
	 *  then waits until the user presses enter.
	 * NOTE: The e-mail given is a personal e-mail address. Please keep this e-mail PRIVATE. It's public use
	 *  is the primary reason I do not want this program published.
	 * NOTE: I make no guarantees that I will actually keep this program up-to-date.
	 */
	private void printCredits(){
		System.out.println();
		System.out.println("**** Credits ****");
		System.out.println();
		System.out.println("Author : Kenneth Chin");
		System.out.println("Contact: chin_ken@hotmail.com - Please do not publish this address!");
		System.out.println("Version: 1.0");
		System.out.println("Licence: For academic demonstration at Framingham State University. NOT for publication!");
		System.out.println("Guarantees: NONE! Version 1.0 or lower needs more testing! Please contact the author with any bugs!");
		promptContinue();
		promptInstructions();
	}
}
