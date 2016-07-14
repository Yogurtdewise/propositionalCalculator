package propositionalCalculatorV1_0;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A data card class. The iterator of the ExpressionCard's binary tree iterates over an
 *  entire expression. Each individual ExpressionCard is used to maintain the sub-expressions
 *  in a propositional logic expression.
 *  A sub-expression is a section of the whole expression that is separated by parentheses, and
 *  is therefore separated by order of operations. Each sub-expression can be negated by the
 *  "not"(!) operator. The use of the not operator is also stored by the ExpressionCard, and
 *  assumed to be false (not negated).
 * @author Kenneth Chin
 *
 */
public class ExpressionCard implements Iterable<ExpressionCard>{
	
	private ExpressionCard root; 	        //The expression's root.
	private ExpressionCard leftExpression;  //The sub-expression's left sub-expression.
	private ExpressionCard rightExpression; //The sub-expression's right sub-expression.
	private String expression;              //The whole sub-expression.
	private boolean not;                    //Is the whole sub-expression to be negated?
	
	/**
	 * Default constructor. All Strings set to null. All booleans set to false.
	 */
	public ExpressionCard(){
		this.leftExpression  = null;
		this.rightExpression = null;
		this.expression      = null;
		this.not             = false;
	}
	
	/**
	 * Constructor offered for easy initialization of values.
	 * @param root The root ExpressionCard node of this ExpressionCard node.
	 * @param leftExpression The sub-expression's left sub-expression.
	 * @param expression The whole sub-expression for this card.
	 * @param rightExpression The sub-expression's right sub-expression.
	 * @param notVal Is the whole sub-expression to be negated?
	 */
	public ExpressionCard(ExpressionCard root, ExpressionCard leftExpression, String expression,
			ExpressionCard rightExpression, boolean notVal){
		this.root            = root;
		this.leftExpression  = leftExpression;
		this.rightExpression = rightExpression;
		this.expression      = expression;
		this.not             = notVal;
	}
	
	//Getter operations.
	
	/**
	 * Returns this ExpressionCard's parent ExpressionCard.
	 * @return This ExpressionCard's parent ExpressionCard.
	 */
	public ExpressionCard getRoot(){
		return this.root;
	}
	
	/**
	 * Returns this ExpressionCard's left child.
	 * @return This ExpressionCard's left child.
	 */
	public ExpressionCard getLeftEx(){
		return leftExpression;
	}

	/**
	 * Returns this ExpressionCard's right child.
	 * @return This ExpressionCard's right child.
	 */
	public ExpressionCard getRightEx(){
		return rightExpression;
	}
	
	/**
	 * Returns the sub-expression stored by this ExpressionCard.
	 * @return The sub-expression stored by this ExpressionCard.
	 */
	public String getEx(){
		return expression;
	}
	
	/**
	 * Returns a boolean indicating if this ExpressionCard's sub-expression
	 *  should be negated.
	 * @return A boolean. True if this ExpressionCard's sub-expression should be negated;
	 *  false otherwise.
	 */
	public boolean getNotVal(){
		return not;
	}
	
	
	//Setter operations.
	
	/**
	 * Sets this ExpressionCard's parent ExpressionCard.
	 * @param card This ExpressionCard's parent ExpressionCard.
	 */
	public void setRoot(ExpressionCard card){
		this.root = card;
	}
	
	/**
	 * Sets this ExpressionCard's left child.
	 * @param expression This ExpressionCard's left child.
	 */
	public void setLeftEx(ExpressionCard expression){
		this.leftExpression = expression;
	}
	
	/**
	 * Sets this ExpressionCard's right child.
	 * @param expression This ExpressionCard's right child.
	 */
	public void setRightEx(ExpressionCard expression){
		this.rightExpression = expression;
	}
	
	/**
	 * Sets this ExpressionCard's sub-expression.
	 * @param expression This ExpressionCard's sub-expression.
	 */
	public void setEx(String expression){
		this.expression = expression;
	}
	
	/**
	 * Sets this ExpressionCard's "not" value. True indicates this ExpressionCard's
	 *  sub-expression should be negated. False indicates the sub-expression should
	 *  not be negated.
	 * @param value A boolean. True indicates this ExpressionCard's sub-expression
	 *  should be negated. False indicates the sub-expression should not be negated.
	 */
	public void setNot(boolean value){
		this.not = value;
	}
	
	
	/**
	 * This returns the iterator for this ExpressionCard. It allows use of for:each loops, or allows
	 *  a user to step through the expression tree manually. It complies with the Iterator interface,
	 *  but does not allow for the removal of elements (.remove() throws an UnsupportedOperationException).
	 *  NOTE: Iteration is returned in pre-order format.
	 * @return An Iterator<ExpressionCard> that allows for expression tree traversal.
	 */
	public Iterator<ExpressionCard> iterator() {
		return new CardIterator(this);
	}
	
	/**
	 * This the iterator for the ExpressionCard Class. It allows use of for:each loops, or allows
	 *  a user to step through the expression tree manually. It complies with the Iterator interface,
	 *  but does not allow for the removal of elements (.remove() throws an UnsupportedOperationException).
	 *  NOTE: Iteration is returned in pre-order format.
	 * @author Kenneth Chin
	 *
	 */
	private class CardIterator implements Iterator<ExpressionCard> {

		ExpressionCard nextNode        = null;  //Track the next node.
		ExpressionCard rightNodeOfRoot = null;  //Store the right node of the root.
		boolean        inRightBranch   = false; //Determine if we are traversing the right branch of the root.
		
		/**
		 * This private constructor allows the current instance of ExpressionCard to act as the root of
		 *  the expression tree to be traversed.
		 * @param root The ExpressionCard that is to be used as a root of the tree.
		 */
		private CardIterator(ExpressionCard root){
			nextNode        = root;
			rightNodeOfRoot = root.getRightEx();
			inRightBranch   = false;
		}
		
		/**
		 * Looks for the next node in pre-order format. This is a helper function for .next() and .hasNext().
		 * @return The next ExpressionCard in pre-order format, or null if no unexplored elements exist.
		 */
		private ExpressionCard getNextCard(){
			if(nextNode==null)
				return null;
			ExpressionCard card = nextNode;
			//This node is a root. Next node is left node.
			if(!(card.getLeftEx()==null))
				return card.getLeftEx();
			//This node is a root. Next node is right node.
			else if(!(card.getRightEx()==null)){
				return card.getRightEx();
			}
			//If this node is the root, with no children, return null.
			else if(card.getRoot()==null)
				return null;
			//If this node is an end node. Next node is the right node of next parent that has a right node.
			else if(card.getLeftEx()==null && card.getRightEx()==null){
				//Traverse the tree upward until there is a right node to give as next.
				while(card.getRoot().getRightEx()==null || card.getRoot().getRightEx() == card){
					card = card.getRoot();
					//If were traversing up the root's right branch & there are no new right nodes, return null.
					if((card.getRoot()==null) || (card.getRoot().getRoot()==null && inRightBranch))
						return null;
					//If were traversing the root's left branch & there are no right nodes, return null.
					if(card.getRoot()==null && card.getRoot().getRightEx()==null)
						return null;
					//If we reach the root and there is a right node, it is the next node.
					if(card.getRoot()==null)
						return card.getRightEx();
				}
				//If we found a right node(or already had one before the loop), it is the next node.
				if(!(card.getRoot().getRightEx()==null)){
					//If we are moving from the root's left tree to its right tree, flag inRightBranch.
					if(card.getRoot().getRightEx()==rightNodeOfRoot && card.getRoot().getRoot()==null)
						inRightBranch = true;
					return card.getRoot().getRightEx();
				}
			}
			//We have checked left & right node of the given node. We have explored all right nodes of all roots.
			// The whole tree has been traversed. Return null.
			return null;
		}
		
		/**
		 * Returns the next ExpressionCard in pre-order format. If none exists, a NoSuchElementException is thrown.
		 *  NOTE: Users who manually use this method should ensure .hasNext() == true.
		 * @return The next ExpressionCard in pre-order format.
		 */
		public ExpressionCard next(){
			if (!hasNext())
				throw new NoSuchElementException("Tree exhausted. Please use .hasNext() to ensure there are elements remaining.");
			ExpressionCard temp = nextNode;
			nextNode = getNextCard();
			return temp;
		}		
		
		/**
		 * Use to ensure there are more elements in the expression tree, before calling .next().
		 * @return A boolean. True if there are more elements in the expression tree. False otherwise.
		 */
		public boolean hasNext() {
			//The whole tree has been returned.
			if(nextNode == null)
				return false;
			//There must be a nextNode queued, or this is the root with a left and/or right child.
			return true;
		}

		/**
		 * This method is unsupported & will throw an UnsupportedOperationException if called.
		 * It exists solely to comply with the Iterator interface.
		 */
		public void remove() {
			throw new UnsupportedOperationException("Removing tree elements is not allowed.");
		}
		
	}

}
