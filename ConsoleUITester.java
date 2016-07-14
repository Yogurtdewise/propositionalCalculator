package propositionalCalculatorV1_0;

import java.util.ArrayList;
import java.util.HashMap;

public class ConsoleUITester {

	public static void main(String[] args) {
		ConsoleUI prompt = new ConsoleUI();
		prompt.printWelcome();
		String expression = prompt.exPrompt();
		if(expression != null){
			ArrayList<String> opList = prompt.findOperands(expression);
			HashMap<String,Boolean> map = prompt.valPrompt(opList);
			
			System.out.println();
			System.out.println("**** Hashmap Values ****");
			for(String operand:opList){
				System.out.println(operand + " = " + map.get(operand));
			}
		}
		else{
			System.out.println();
			System.out.println("Quitting...");
		}
	}

}
