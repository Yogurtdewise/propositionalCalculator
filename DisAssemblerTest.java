package propositionalCalculatorV1_0;

import java.util.Iterator;

public class DisAssemblerTest {

	public static void main(String[] args) {
		
		String expression = "(!(a&b))";
		//String expression = "(!(!(a&b)))";
		//String expression = "(!(!(!(a&b))))";
		//String expression = "(!(!(!(!(a&b)))))";
		//String expression = "(a&b|!(c^d)&e|(f^g))";
		//String expression = "(a&b&(c&d)&e&(f&g)&h)";
		//String expression = "(a&b|!(c^d)&e|(f^g)&h)";
		//String expression = "(c^d)";
		
		
		ExpressionCard root = ExpressionDisAssembler.getCards(expression);
		boolean left;
		boolean right;
		
		if(root==null)
			System.out.println("Root is null!");
		else{
			//Print the root.
			if(root.getLeftEx()==null)
				left = false;
			else
				left = true;
			if(root.getRightEx()==null)
				right = false;
			else
				right = true;
			System.out.println("**** Root ****");
			System.out.println("Expression: " + root.getEx());
			System.out.println("Negated? " + root.getNotVal());
			System.out.println("Have left node? " + left);
			System.out.println("Have right node? " + right);
			System.out.println();
		}
		if(!(root==null)){
			int nodeCount = 0;
			Iterator<ExpressionCard> iter = root.iterator();
			ExpressionCard card;
			while(iter.hasNext()){
				card = iter.next();
				nodeCount++;
				if(card.getLeftEx()==null)
					left = false;
				else
					left = true;
				if(card.getRightEx()==null)
					right = false;
				else
					right = true;
				System.out.println("**** Node# " + nodeCount + " ****");
				System.out.println("Expression: " + card.getEx());
				System.out.println("Negated? " + card.getNotVal());
				System.out.println("Have left node? " + left);
				System.out.println("Have right node? " + right);
				System.out.println();
			}
		}
		System.out.println("Test Done!");
	}

}
