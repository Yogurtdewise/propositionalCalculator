package propositionalCalculatorV1_0;

import java.util.HashMap;

public class EvaluatorTest {

	public static void main(String[] args) {
		boolean answer;
		
		String expression = "((a&b))";
		//String expression = "(!(!(a&b)))";
		//String expression = "(!(!(!(a&b))))";
		//String expression = "(!(!(!(!(a&b)))))";
		//String expression = "(!(a&b))";
		//String expression = "((!(!a))&b)";
		//String expression = "((a&b)&(c&d))";
		//String expression = "(a&b&c&(d&f)&g&h)";
		//String expression = "(a&b|!(c^d)&e|(f^g))";
		//String expression = "(a&b&(c&d)&e&(f&g)&h)";
		//String expression = "(a&b|!(c^d)&e|(f^g)&h)";
		//String expression = "(c^d)";
		
		int numOfVars = 8;
		
		HashMap<String, Boolean> map  = new HashMap<String, Boolean>(numOfVars);
		map.put("a", Boolean.valueOf(true));
		map.put("b", Boolean.valueOf(true));
		map.put("c", Boolean.valueOf(true));
		map.put("d", Boolean.valueOf(true));
		map.put("e", Boolean.valueOf(true));
		map.put("f", Boolean.valueOf(true));
		map.put("g", Boolean.valueOf(true));
		map.put("h", Boolean.valueOf(true));
		
		ExpressionCard root = ExpressionDisAssembler.getCards(expression);
		
		answer = ExpressionEvaluator.evaluateExpressionCards(map, root);
		
		System.out.println("The expression, \"" + expression + "\", was evaluated to be: " + answer);
	}

}
