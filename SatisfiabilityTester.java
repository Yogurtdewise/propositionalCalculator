package propositionalCalculatorV1_0;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;

public class SatisfiabilityTester {

	public static void main(String[] args) {
		
		String expression = "(a|b)";
		//String expression = "(a&b|!(c^d)&e|(f^g))"; //FTFTTTTT
		//String expression = "(a&b&(c&d)&e&(f&g)&h)";
		//Passed! String expression = "(a&b|!(c^d)&e|(f^g)&h)";
		//Passed! String expression = "(c^d)";
		
		int numOfVars = 8;
		
		HashMap<String, Boolean> map  = new HashMap<String, Boolean>(numOfVars);
		map.put("a", Boolean.valueOf(false));
		map.put("b", Boolean.valueOf(true));
		//map.put("c", Boolean.valueOf(false));
		//map.put("d", Boolean.valueOf(true));
		//map.put("e", Boolean.valueOf(true));
		//map.put("f", Boolean.valueOf(true));
		//map.put("g", Boolean.valueOf(true));
		//map.put("h", Boolean.valueOf(true));
		
		ExpressionCard root = ExpressionDisAssembler.getCards(expression);
		
		System.out.println("Testing Satisfiablility...");
		
		ArrayList<HashMap<String, Boolean>> mapList = SatisfiablilityCalculator.findSatisfiablility(map, root);
		
		if(mapList.isEmpty())
			System.out.println("There are no values which satisfy the expression \"" + expression + "\".");
		else{
			for(int i=0; i<mapList.size(); i++){
				HashMap<String, Boolean> aMap = mapList.get(i);
				Set<Entry<String, Boolean>> set = aMap.entrySet();
				Iterator<Entry<String, Boolean>> iter = set.iterator();
				System.out.println();
				System.out.println("**** Map #" + (i+1) + " ****");
				while(iter.hasNext()){
					Entry<String, Boolean> mapping = iter.next();
					String  key   = mapping.getKey();
					boolean value = mapping.getValue();
					System.out.println("(" + key + ", " + value + ")");
				}
		}
		System.out.println();
		System.out.println("Test Complete!");
	}

	}

}
