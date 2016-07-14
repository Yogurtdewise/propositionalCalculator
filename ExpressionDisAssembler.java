package propositionalCalculatorV1_0;

/**
 * This class is used to disassemble a propositional logic String into a form that
 *  can be read by the ExpressionEvaluator class. The binary tree created by
 *  ExpressionDisAssembler honors the order of operations rules of a propositional
 *  logic expression by prioritizing order in a depth-first, pre-order, ordering.
 *  The lower the depth, the lower the priority. Left nodes are created before right
 *  nodes. Negation rules are honored through use of ExpressionCard's "not" class
 *  variable. See the getCards(String) method description for further details on
 *  the behavior of the ExpressionCard binary tree.
 * @author Kenneth Chin
 *
 */
public final class ExpressionDisAssembler {

	private static String expression; //The whole expression to be evaluated.
	
	/**
	 * The private constructor which forces this class to be singleton.
	 */
	private ExpressionDisAssembler(){
		expression = "";
	}

	/**
	 * Creates a binary tree of ExpressionCard objects. The root of the binary tree
	 *  is the inner-most sub-expression in the given expression. A sub-expression
	 *  is a part of the expression that is surrounded by parentheses. Left children
	 *  of a parent node are obtained from the left of the parent's sub-expression. Right
	 *  children of a parent note are obtained from the right of the parent's sub-expression.
	 *  Empty components of an expression, such as those appearing between index 7 & 8
	 *  of (a&(b&c)), are given a node who's expression is the empty String ("").
	 *  Nodes with empty Strings can be negated. This allows for nested negation,
	 *  as in (!(!(a&b))). A parent node's binary tree can be iterated through via
	 *  ExpressionCard's iterator. This method returns the binary tree's absolute
	 *  root, & its iterator returns the entire binary tree.
	 * @param expression A propositional logic expression String. Since the details
	 *  of an expression's format are complex, please see ConsoleUI's explanation of
	 *  expression rules (See the promptInstructions() method).
	 * @return An ExpressionCard that represents the absolute root of a binary tree.
	 *  The iterator of the returned ExpressionCard will express the complete expression
	 *  String passed into this method as ExpressionCard tree nodes.
	 */
	public static ExpressionCard getCards(String expression){
		if(expression==null)
			return null;
		ExpressionDisAssembler.expression = expression;
		return findRoot();
	}
	
	/**
	 * Used to build a binary tree of ExpressionCards. See getCards(String) for
	 *  details regarding the binary tree. This method returns the absolute root
	 *  of the binary tree. It is separated from makeTree(ExpressionCard, String)
	 *  for conceptual convenience.
	 * @return The ExpressionCard at the absolute root of an expression's binary
	 *  tree.
	 */
	private static ExpressionCard findRoot(){
		String    expression  = ExpressionDisAssembler.expression;
		ExpressionCard root = new ExpressionCard();
		root.setRoot(null);
		int closeIndex = findClosedParentheses(expression);
		int openIndex  = findMatchingOpenParentheses(expression, closeIndex);
		if(closeIndex == -1)
			closeIndex = expression.length();
		String subExpression = expression.substring(openIndex+1, closeIndex);
		//Determine if this subExpression is negated.
		if(openIndex>0 && expression.substring(openIndex-1, openIndex).equals("!")){
			root.setNot(true);
			openIndex--;
		}
		root.setEx(subExpression);
		//If there's more expressions to the left, get the root of the left tree.
		if((openIndex - 1) > -1){
			root.setLeftEx(makeTree(root, expression.substring(0, openIndex)));
		}
		//If there's more expressions to the right, get the root of the right tree.
		if((closeIndex+1) < (expression.length()-1) && hasMoreVars(expression.substring(closeIndex+1, expression.length()))){
			root.setRightEx(makeTree(root, expression.substring(closeIndex + 1, expression.length())));
		}
		return root;
	}
	
	/**
	 * Creates and assembles the sub-trees of expression's binary tree.
	 * NOTE: This method is a recursive method. The ExpressionCard passed to this
	 *  method is not necessarily the binary tree's absolute root.
	 * @param root A parent ExpressionCard of an expression's binary tree.
	 * @param expression The expression String to be expressed by a binary tree of
	 *  ExpressionCards objects.
	 * @return An ExpressionCard that is a left or right child of a parent ExpressionCard node.
	 */
	private static ExpressionCard makeTree(ExpressionCard root, String expression){
		ExpressionCard thisCard = new ExpressionCard();
		thisCard.setRoot(root);
		String subExpression;
		int closeIndex = findClosedParentheses(expression);
		int openIndex;
		//If there's no closing parentheses, find the first open parentheses (right to left).
		// Otherwise get the index of the open parentheses that matches the innermost closed parentheses.
		if(closeIndex == -1)
			openIndex = findMatchingOpenParentheses(expression, expression.length()-1);
		else
			openIndex = findMatchingOpenParentheses(expression, closeIndex);
		if(openIndex == -1){
			//Check if a set of parentheses encased another set. IE ((xyz)), previous expression would = ().
			if(expression==null)
				return null;
			//If expression is contains ONLY "!", then there is a double negation (ie. (!(!(a&b)))).
			else if(expression.equals("!")){
				thisCard.setEx("");
				thisCard.setLeftEx(null);
				thisCard.setRightEx(null);
				thisCard.setNot(true);
			}
			else{
				String newString = stripParentheses(expression);
				thisCard.setEx(newString);
				thisCard.setLeftEx(null);
				thisCard.setRightEx(null);
				thisCard.setNot(false);
				return thisCard;
			}
		}
		//There are more expressions within this expression. Handle the current subExpression.
		if(closeIndex == -1)
			closeIndex = expression.length();
		subExpression = expression.substring(openIndex+1, closeIndex);
		//If the subExpression ends with "!" or contains only "!", strip it. The negation should be
		// caught on a previous iteration.
		if(subExpression.endsWith("!"))
			subExpression = subExpression.substring(0, closeIndex-1);
		String newString = stripParentheses(subExpression);
		thisCard.setEx(newString);
		//Determine if this subExpression is negated.
		if(openIndex>0 && expression.substring(openIndex-1, openIndex).equals("!")){
			thisCard.setNot(true);
			openIndex--;
		}
		//If there's more expressions to the left, get the root of the left tree.
		if((openIndex - 1) > -1){
			thisCard.setLeftEx(makeTree(thisCard, expression.substring(0, openIndex)));
		}
		//If there's more expressions to the right, get the root of the right tree.
		if((closeIndex+1) < (expression.length()-1) && hasMoreVars(expression.substring(closeIndex+1, expression.length()))){
			thisCard.setRightEx(makeTree(thisCard, expression.substring(closeIndex + 1, expression.length())));
		}
		return thisCard;
	}

	
	/**
	 * Finds the index of the innermost close parentheses.
	 * @param expression A String to search.
	 * @return An int. The index of the innermost close parentheses. -1 if none found.
	 */
	private static int findClosedParentheses(String expression){
		for(int i=0; i<expression.length(); i++){
			String currChar = expression.substring(i, i+1);
			if(currChar.equals(")"))
				return i;
		}
		return -1;
	}
	
	/**
	 * Finds the index of the open parentheses that's paired with the specified closing parentheses.
	 * @param expression A String to find the matching open parentheses.
	 * @param indexOfClosed An int. The index of a closing parentheses that is to be matched.
	 * @return An int. The index of the open parentheses that matches the specified closing parentheses,
	 * 	or -1 if none found.
	 */
	private static int findMatchingOpenParentheses(String expression, int indexOfClosed){
		if(indexOfClosed<1)
			return -1;
		String leftOfClosed = expression.substring(0, indexOfClosed);
		for(int i=leftOfClosed.length()-1; i>=0; i--){
			if(leftOfClosed.substring(i, i+1).equals("("))
				return i;
		}
		return -1;
	}
	
	/**
	 * Removes any parentheses from the given String.
	 * @param expression The String to have parentheses removed from.
	 * @return The expression String, minus any parentheses.
	 */
	private static String stripParentheses(String expression){
		String newString = expression;
		newString = newString.replaceAll("\\)","");
		newString = newString.replaceAll("\\(", "");
		return newString;
	}
	
	/**
	 * Determines if the sub-expression has more variables in it.
	 * @param subExpression The section of the expression String to be checked
	 *  for more variables.
	 * @return A boolean indicating true if there are more variables in the
	 *  sub-expression; false otherwise.
	 */
	private static boolean hasMoreVars(String subExpression){
		for(int i=0; i<subExpression.length(); i++){
			char currentChar = subExpression.charAt(i);
			if(Character.isLetter(currentChar))
				return true;
		}
		return false;
	}
}
