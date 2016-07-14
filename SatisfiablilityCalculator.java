package propositionalCalculatorV1_0;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;

/**
 * This class tests the truth table of a given expression and returns an ArrayList
 *  of variable values for which the expression is evaluated to be true.
 * The class is singleton, and it's only public method is
 *  findSatisfiablility(HashMap<String, Boolean>, ExpressionCard).
 * @author Kenneth Chin
 *
 */
public final class SatisfiablilityCalculator {

	private static HashMap<String, Boolean> originalMap;
	private static ArrayList<HashMap<String, Boolean>> mapList;
	private static ArrayList<String> variableList;
	private static int listSize;
	private static ExpressionCard root;
	
	/**
	 * The private constructor which forces this class to be singleton.
	 */
	private SatisfiablilityCalculator(HashMap<String, Boolean> aMap, ExpressionCard aRoot){}	
	
	/**
	 * Finds all combinations of truth values for each variable given by aMap for which aRoot's
	 *  expression can be evaluated to be true. Each combination that is evaluated as true is
	 *  added to the returned ArrayList<HashMap<String, Boolean>>. A single combination is the
	 *  key/value mapping of all keys in a single map. If the returned ArrayList.isEmpty, then
	 *  the given expression is unsatisfiable.
	 * @param aMap A HashMap<String, Boolean> who's keys are the unique String variables of the
	 *  given expression, and who's mapped values are the truth value of the given key.
	 * @param aRoot The ExpressionCard at the root of an ExpressionCard's binary tree; who's
	 *  iterator returns the entire expression to be evaluated.
	 * @return An ArrayList<HashMap<String, Boolean>> who's contents are HashMap<String, Boolean>
	 *  for each variable(key) value combination that evaluates as true for the expression given
	 *  by aRoot. If there are no combinations that evaluate as true, then the returned ArrayList
	 *  will be empty.
	 */
	public static ArrayList<HashMap<String, Boolean>> findSatisfiablility(HashMap<String, Boolean> aMap, ExpressionCard aRoot){
		originalMap  = aMap;
		root         = aRoot;
		mapList      = new ArrayList<HashMap<String, Boolean>>();
		variableList = getVariableList(originalMap);
		listSize = variableList.size();
		HashMap<String, Boolean> newMap = getNewTempMap();
		evaluateMap(newMap, listSize);
		return mapList;
	}
	
	/**
	 * A helper method creates an ArrayList<String> of all unique keys given by aMap.
	 * @param aMap A HashMap<String, Boolean> who's keys are the unique variables of the
	 *  given expression.
	 * @return An ArrayList<String> of all unique keys given by aMap.
	 */
	private static ArrayList<String> getVariableList(HashMap<String, Boolean> aMap){
		Set<Entry<String, Boolean>> set = aMap.entrySet();
		Iterator<Entry<String, Boolean>> iter = set.iterator();
		ArrayList<String> keyList = new ArrayList<String>();
		while(iter.hasNext())
			keyList.add(iter.next().getKey());
		return keyList;
	}
	
	/**
	 * A helper method that provides the method, evaluateMap(HashMap<String, Boolean>, int),
	 *  with a "dummy" map for its initial call. This dummy map gives all keys an initial
	 *  value of true.
	 * @return A HashMap<String, Boolean> who's keys are the unique variables of the given
	 *  expression. Each key is given an initial value of true.
	 */
	private static HashMap<String, Boolean> getNewTempMap(){
		HashMap<String, Boolean> map = new HashMap<String, Boolean>(listSize);
		for(int i = 0; i<listSize; i++)
			map.put(variableList.get(i), true);
		return map;
	}
	
	/**
	 * A helper method that produces a deep clone of a given HashMap<String, Boolean>. This is
	 *  used to populate the mapList array with maps. Without cloning the HashMaps, the method
	 *  "evaluateMap(HashMap<String, Boolean>, int)" would change the given map's values, but
	 *  the mapList array would contain several references to the same map.
	 * @param map The HashMap<String, Boolean> to be cloned.
	 * @return A deep clone of the given HashMap<String, Boolean>.
	 */
	private static HashMap<String, Boolean> cloneMap(HashMap<String, Boolean> map){
		HashMap<String, Boolean> clonedMap = new HashMap<String, Boolean>();
		Set<Entry<String, Boolean>> set = map.entrySet();
		Iterator<Entry<String, Boolean>> iter = set.iterator();
		while(iter.hasNext()){
			Entry<String, Boolean> aMapping = iter.next();
			clonedMap.put(aMapping.getKey(), aMapping.getValue());
		}
		return clonedMap;	
	}
	
	/**
	 * Recursively evaluates all combinations of variable values. Populates the mapList array with
	 *  all value combinations which evaluate to be true given the expression provided by root.
	 * NOTE: This method evaluates all combinations of a map of any given size. Each combination is
	 *  evaluated at the last instance in a recursive thread. Since the first instance requires a map,
	 *  a "dummy" map is given to initiate the recursion. The dummy map is never evaluated.
	 * @param map A HashMap<String, Boolean> who's keys are all unique variables in the expression
	 *  being evaluated. See method note for recursion details.
	 * @param numVars An int indicating the number of remaining variables who's combinations are
	 *  to be tested.
	 */
	private static void evaluateMap(HashMap<String, Boolean> map, int numVars){
		//Base case. This map is already queued to be evaluated.
		if(numVars < 1)
			return;
		
		String key = variableList.get(numVars-1);
		HashMap<String, Boolean> tempMap;
		boolean isSatisfiable;
		
		tempMap = cloneMap(map);
		tempMap.put(key, false);
		evaluateMap(tempMap, numVars-1);
		//After all variables in this recursive thread have been toggled evaluate (False, ..., ..., ...).
		if(numVars == 1){
			isSatisfiable =  ExpressionEvaluator.evaluateExpressionCards(tempMap, root);
			if(isSatisfiable)
				mapList.add(tempMap);
		}
		
		tempMap = cloneMap(map);
		tempMap.put(key, true);
		evaluateMap(tempMap, numVars-1);
		//After all variables in this recursive thread have been toggled evaluate (True, ..., ..., ...).
		if(numVars == 1){
			isSatisfiable =  ExpressionEvaluator.evaluateExpressionCards(tempMap, root);
			if(isSatisfiable)
				mapList.add(tempMap);
		}
	}
}
