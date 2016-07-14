package propositionalCalculatorV1_0;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;

/**
 * This class is used to read and evaluate the expression expressed by the iterator
 *  of the root ExpressionCard in an ExpressionCard binary tree. The expression is
 *  evaluated at the values given by a HashMap of variable(key)/boolean(value) pairs.
 * This class is singleton, and it's only public method is
 *  evaluateExpressionCards(HashMap<String, Boolean>, ExpressionCard).
 * @author Kenneth Chin
 *
 */
public final class ExpressionEvaluator {

	private static HashMap<String, Boolean> map;
	private static Iterator<ExpressionCard> iter;
	
	private static Stack<Boolean> valueStack;

	//A list of all conditionals. Note: NOT (!) is not a conditional.
	private static final char[] conditionals = {'&', '|', '^', '>', '='};
	
	/**
	 * The private constructor which forces this class to be singleton.
	 */
	private ExpressionEvaluator(){}
	
	/**
	 * Evaluates the entire expression expressed by root's iterator. The expression is evaluated
	 *  with the variable values given by aMap. The evaluation of the expression is returned as
	 *  a boolean indicating the entire expression's truth value at the given variable values.
	 * @param aMap A HashMap<String, Boolean> who's keys are the unique String variables of the
	 *  given expression. The Boolean values are the truth value of each key.
	 * @param root The ExpressionCard at the root of the ExpressionCard binary tree. The iterator
	 *  of root should return the entire expression to be evaluated.
	 * @return A boolean indicating the entire expression's truth value given the  variable values of aMap.
	 */
	public static boolean evaluateExpressionCards(HashMap<String, Boolean> aMap, ExpressionCard root){
		map        = aMap;
		iter       = root.iterator();
		valueStack = new Stack<Boolean>();
		
		boolean currentVal;
		boolean returnVal;
		
		while(iter.hasNext()){
			ExpressionCard card  = iter.next();
			String expression    = card.getEx();
			expression           = ensureFormat(expression);
			//Skip any terminating children of the ExpressionCard tree.
			if(!expression.equals("") && !(expression == null) && expression.length() > 0){
				char firstChar       = expression.charAt(0);
				//If the expression contains ONLY a conditional, then var1 and var2 are on valueStack in reverse order.
				if(expression.length() == 1 && isConditional(firstChar)){
					boolean val2 = valueStack.pop();
					boolean val1 = valueStack.pop();
					currentVal   = applyConditional(val1, firstChar, val2);
					valueStack.push(currentVal);
				}
				//If the expression contains ONLY a negation, then there is a double negation (ie. (!(!(a&b))),
				// and the value at the top of the stack should be negated.
				else if(expression.length() == 1 && isNegation(firstChar)){
					currentVal = valueStack.pop();
					valueStack.push(!currentVal);
				}
				
				//If the expression has variables to be evaluated, do so.
				else if(!(expression == null) && !(expression.equals(""))){
					if(isConditional(firstChar)){
						currentVal = combinePrevValue(expression);
						valueStack.push(currentVal);
					}
					else{
						currentVal = evaluateSubExpression(expression);
						valueStack.push(Boolean.valueOf(currentVal));
					}
				}
				//If the expression is negated, negate the expression's truth value.
				if(card.getNotVal()){
					currentVal = valueStack.pop();
					valueStack.push(!currentVal);
				}
			}
			if(expression.equals("") && card.getNotVal()){
				currentVal = valueStack.pop();
				valueStack.push(!currentVal);
			}
		}
		returnVal = valueStack.pop();
		if(!valueStack.isEmpty())
			throw new IllegalStateException("valueStack in ExpressionEvaluator.evaluateExpressionCards was not emptied!");
		return returnVal;
	}
	
	/**
	 * An error-prevention method that ensures there are no spaces or parentheses
	 *  in the given expression.
	 * @param expression A String of the given expression to be checked.
	 * @return A String of the given expression without white spaces or parentheses.
	 */
	private static String ensureFormat(String expression){
		String newString = expression;
		newString = newString.replaceAll("\\s+","");
		newString = newString.replaceAll("\\(", "");
		newString = newString.replaceAll("\\)", "");
		return newString;
	}
	
	/**
	 * Combines the truth value of this expression with the truth value of the previous
	 *  expression (found in valueStack). Returns the truth value of the combination's
	 *  evaluation.
	 * @param expression A String. A part of the propositional expression that begins with
	 *  a conditional. The "part" to be combined with a previously evaluated "part".
	 * @return A boolean indicating the truth value of the evaluation of the given expression
	 *  segment with previously evaluated parts.
	 */
	private static boolean combinePrevValue(String expression){
		char conditional  = expression.charAt(0);
		boolean subValue  = evaluateSubExpression(expression.substring(1));
		boolean prevValue = valueStack.pop();
		return  applyConditional(prevValue, conditional, subValue);
	}
	
	/**
	 * Finds the truth value of a given sub-expression. A "sub-expression" is a propositional
	 *  expression that is either complete, or ends but does note begin with a conditional.
	 * NOTE: If the given sub-expression ends with a conditional and does note begin with a conditional,
	 *  the value to the right of the expression should already be on in valueStack.
	 * @param expression A String of the propositional expression to be evaluated. The expression
	 *  should not begin with a conditional.
	 * @return A boolean indicating the truth value of the given sub-expression.
	 */
	private static boolean evaluateSubExpression(String expression){
		boolean currentValue;
		boolean hasMoreVariables = true;
		int     readIndex = 0;
		char    firstChar = expression.charAt(0);
		boolean not1      = isNegation(firstChar);
		String  var1;
		boolean var1Value;
		char    conditional;
		int     conditionalIndex;
		
		if(isNegation(firstChar))
			var1 = readVariable(expression.substring(readIndex + 1));
		else
			var1 = readVariable(expression);
		
		var1Value = map.get(var1);
		if(not1)
			var1Value = !var1Value;
		
		conditionalIndex = findConditional(expression);
		//If no conditional exists, then this is an expression with a single variable.
		if(conditionalIndex == -1)
			return var1Value;
		conditional = expression.charAt(conditionalIndex);
		
		//If conditional is at the end of the expression (ie. a&), then var1 is the last variable and valueStack is required to evaluate the expression.
		if(conditionalIndex == expression.length() - 1)
			return applyConditional(var1Value, conditional, valueStack.pop());

		//If conditional is not the end of the expression, then there are more variables.
		while(hasMoreVariables){
			readIndex    = conditionalIndex + 1;
			firstChar    = expression.charAt(readIndex);
			boolean not2 = isNegation(firstChar);
			String var2;
			boolean var2Value;
		
			if(isNegation(firstChar))
				var2 = readVariable(expression.substring(readIndex + 1));
			else
				var2 = readVariable(expression.substring(readIndex));
		
			var2Value = map.get(var2);
			if(not2)
				var2Value = !var2Value;
			
			currentValue = applyConditional(var1Value, conditional, var2Value);
		
			//If there's no other conditionals, return the currentValue;
			conditionalIndex = findConditional(expression.substring(readIndex));
			if(conditionalIndex == -1)
				return currentValue;
			
			//There's another conditional, continue evaluating.
			conditionalIndex = conditionalIndex + readIndex;
			conditional      = expression.charAt(conditionalIndex);
			
			//If conditional is at the end of the expression (ie. a&b&), then currentValue must be evaluated against valueStack.
			if(conditionalIndex == expression.length() - 1)
				return applyConditional(currentValue, conditional, valueStack.pop());
			
			//There are more variables to be evaluated.
			var1Value = currentValue;
		}
		throw new IllegalArgumentException("The expression, \"" + expression + "\", was not properly parsed by ExpressionEvaluator.evaluateSubExpression.");
	}
	
	
	/**
	 * Returns the first full variable String of expression. Returns the empty String ("") if no variable was
	 *  found. For simplicity, expression should start with a variable, However expressions that do not start
	 *  with a variable are handled.
	 * @param expression The String where a variable is to be found. For simplicity, expression should start
	 *  with a variable.
	 * @return A String representing the first variable found in expression.
	 */
	private static String readVariable(String expression){
		if(expression.equals(""))
			return "";
		char currentChar = expression.charAt(0);
		int  startIndex  = 0;
		if(isConditional(currentChar))
			startIndex++;
		if(isNegation(currentChar))
			startIndex++;
		for(int i = startIndex; i<expression.length(); i++){
			currentChar = expression.charAt(i);
			if(!Character.isLetter(currentChar) || isConditional(currentChar))
				return expression.substring(startIndex, i);
		}
		//expression begins and ends with a variable (shouldn't happen).
		if(Character.isLetter(expression.charAt(startIndex)))
			return expression.substring(startIndex, expression.length());
		//No variable found.
		return "";
	}
	
	/**
	 * Finds the index of the first(hopefully, only) conditional in a given expression.
	 * @param expression A String. An expression with a single conditional
	 * @return The index of the conditional in the given String. -1 if no conditional exists.
	 */
	private static int findConditional(String expression){
		for(int i=0; i<expression.length(); i++){
			for(char conditional:conditionals){
				if(expression.charAt(i) == conditional)
					return i;
			}
		}
		return -1;
	}

	
	/**
	 * Determines if a character is a conditional.
	 * @param character The char to be evaluated.
	 * @return A boolean. True if the char is a conditional; false otherwise.
	 */
	private static boolean isConditional(char character){
		for(int i=0; i<conditionals.length; i++){
			if(conditionals[i] == character)
				return true;
		}
		return false;
	}
	
	/**
	 * Determines if a character is the negation character.
	 * @param character The char to be evaluated.
	 * @return A boolean. True if the char is the negation character; false otherwise.
	 */
	private static boolean isNegation(char character){
		if(character == '!')
			return true;
		return false;
	}
	
	
	/**
	 * Return the boolean value of the operation indicated on two variables.
	 * @param var1 A boolean value.
	 * @param conditional A char indicating the boolean operation to be preformed.
	 * @param var2 A boolean value.
	 * @return A boolean equal to the evaluation of the two given variables using the operation indicated by the conditional.
	 */
	private static boolean applyConditional(boolean var1, char conditional, boolean var2){
		switch(conditional){
			case '&' : return applyAnd(var1, var2);
			case '|' : return applyOr(var1, var2);
			case '^' : return applyXOr(var1, var2); 
			case '>' : return applyImplication(var1, var2);
			case '=' : return applyEquivalence(var1, var2);
			default  : throw new UnsupportedOperationException("The conditional \"" + conditional + "\" is not supported.");
		}
	}

	/**
	 * Determine the boolean value of an 'And' operation on two variables.
	 * @param var1 A boolean value.
	 * @param var2 A boolean value.
	 * @return The evaluation of var1 And var2.
	 */
	private static boolean applyAnd(boolean var1, boolean var2){
		return (var1 && var2);
	}
	
	/**
	 * Determine the boolean value of an 'or' operation on two variables.
	 * @param var1 A boolean value.
	 * @param var2 A boolean value.
	 * @return The evaluation of var1 OR var2.
	 */
	private static boolean applyOr(boolean var1, boolean var2){
		return (var1 || var2);
	}
	
	/**
	 * Determine the boolean value of an 'XOr' operation on two variables.
	 * @param var1 A boolean value.
	 * @param var2 A boolean value.
	 * @return The evaluation of var1 XOr var2.
	 */
	private static boolean applyXOr(boolean var1, boolean var2){
		return (var1 ^ var2);
	}
	
	/**
	 * Determine the boolean value of an "implies" operation on two variables.
	 * NOTE: "x implies y" is true for all values EXCEPT x=true and y=false.
	 * @param var1 A boolean value.
	 * @param var2 A boolean value.
	 * @return The evaluation of var1 implies var2.
	 */
	private static boolean applyImplication(boolean var1, boolean var2){
		if(!var1)
			return true;
		if(var2)
			return true;
		return false;
	}
	
	/**
	 * Determine the boolean value of an "equivalence" operation on two variables.
	 * NOTE: "x is equivalent to y" is true if x=y, and is therefore the negation of
	 *  the xOr operation on x^y.
	 * @param var1 A boolean value.
	 * @param var2 A boolean value.
	 * @return The evaluation of var1 is equivalent to var2.
	 */
	private static boolean applyEquivalence(boolean var1, boolean var2){
		return !(var1 ^ var2); 
	}
}
