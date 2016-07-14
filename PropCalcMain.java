package propositionalCalculatorV1_0;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * The driver class for the PropositionalCalculator program. Continually asks
 *  and responds to user user for input until they indicate program termination.
 * The PropositionalCalculator determines the truth value for a given
 *  propositional expression, with user-given variable values. The program then
 *  offers to determine the expression's satisfiability. If the expression is
 *  satisfiable, the program will print all variable value combination that
 *  make the expression true.
 * @author Kenneth Chin
 *
 */
public class PropCalcMain {

	public static void main(String[] args) {
		ConsoleUI      ui   = new ConsoleUI();
		ExpressionCard root;
		
		String expression;
		HashMap<String, Boolean>            valueMap;
		ArrayList<String>                   variables;
		ArrayList<HashMap<String, Boolean>> satMap;
		boolean exValue;
		boolean runAgain = true;

		
		ui.printWelcome();
		while(runAgain){
			expression = ui.exPrompt();
			if(expression != null){
				variables  = ui.findOperands(expression);
				valueMap   = ui.valPrompt(variables);
				
				ui.printWait();
				
				root       = ExpressionDisAssembler.getCards(expression);
				exValue    = ExpressionEvaluator.evaluateExpressionCards(valueMap, root);
		
				ui.printResult(expression, exValue);
		
				if(ui.promptForSatisfiability()){
					ui.printWait();
					satMap = SatisfiablilityCalculator.findSatisfiablility(valueMap, root);
					ui.printSatisfiability(satMap, expression);
				}
			}
			else
				runAgain = false;
		}
		ui.printGoodbye();
	}

}
