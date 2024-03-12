
//Based on http://www.atlaseditorials.com/2008/02/01/java-infix-to-postfix-evaluator/
//By Jonathan Carr

package uk.ac.ed.inf.pepa.jhydra.evaluator;

//import java.io.*;
import java.util.*;


public class ExpressionEvaluator {


	private enum Opr{MULT,DIV,MOD,PLUS,MINUS,AND,OR,EQUALS,NEQUALS,LEQ,GEQ,LESS,GREATER,NOT}

//	private String[] operators = {"*", "/", "%", "+", "-", "&&", "||", "==", "!=", "<=", ">=", "<", ">","!" };

	private String[] separators = {"*", "/", "%", "+", "-", "&&", "||", "==", "!=", "<=", ">=", "<", ">", "!", "(", ")", ",", "?", ":"};


	private Hashtable<String,Opr> operatorTable = new Hashtable<String,Opr>(); 

//	private Hashtable<String,Double> constantTable;

	// initialize two stacks: operator and operand
	private Stack<String> operatorStack = new Stack<String>();
	private Stack<String> operandStack = new Stack<String>();

	
	// main method
//	public ExpressionEvaluator(Hashtable<String,Double> cT){
	public ExpressionEvaluator(){

	
//		constantTable = cT;
		
		operatorTable.put("*",Opr.MULT);
		operatorTable.put("/",Opr.DIV);
		operatorTable.put("%",Opr.MOD);
		operatorTable.put("+",Opr.PLUS);
		operatorTable.put("-",Opr.MINUS);
		operatorTable.put("&&",Opr.AND);
		operatorTable.put("||",Opr.OR);
		//	operatorTable.put("=",Opr.ASSIGN);
		operatorTable.put("==",Opr.EQUALS);
		operatorTable.put("!=",Opr.NEQUALS);
		operatorTable.put("<=",Opr.LEQ);
		operatorTable.put(">=",Opr.GEQ);
		operatorTable.put("<",Opr.LESS);	
		operatorTable.put(">",Opr.GREATER);
		operatorTable.put("!",Opr.NOT);

	}

	public void addSpace(String input){

		for(int i=0;i<separators.length;i++){
			String str = separators[i];
			//	    System.out.println("Replacing [" + str + "] with [" + " " + str + " " + "]");
			input = input.replace(str, " " + str + " ");
		}

		input = input.replace("=  =", "==");

		input = input.replace("< =", "<=");
		input = input.replace("> =", ">=");
		input = input.replace("! =", "!=");
		
	}
	
	public String evaluate(String infix){
	
/*		
		for(int i=0;i<separators.length;i++){
			String str = separators[i];
			//		    System.out.println("Replacing [" + str + "] with [" + " " + str + " " + "]");
			infix = infix.replace(str, " " + str + " ");
		}

		infix = infix.replace("=  =", "==");

		infix = infix.replace("< =", "<=");
		infix = infix.replace("> =", ">=");
		infix = infix.replace("! =", "!=");
*/
		
//		System.out.println("Expression in infix: " + infix);

		String postfixString = toPostfix(infix);
		
		// displays postfix notation
//		System.out.println("Expression in postfix:" + postfixString);

		String resultString = evaluatePostfix(postfixString);
		
		// displays evaluated expression
//		System.out.println("Evaluated expression: " + resultString);
		
		return resultString;
	}
	
	

	// method converts infix expression to postfix notation
	private String toPostfix(String infix){

		StringTokenizer s = new StringTokenizer(infix);
		// divides the input into tokens for input
		String symbol, postfix = "";

		// while there is input to be read
		while (s.hasMoreTokens()){
			symbol = s.nextToken();

			// if it's a number, add it to the string
			if (!isSeparator(symbol)){
				postfix = postfix + " " + symbol;
			}else if (symbol.equals("(")){
				// push "("
				String operator = new String("(");
				operatorStack.push(operator);
			}else if (symbol.equals(")")){
				// push everything back to "("

				while (!(operatorStack.peek().equals("("))){
					postfix = postfix + " " + operatorStack.pop();
				}
				operatorStack.pop();
			}else{
				// print operatorStack occurring before it that have greater precedence
				while (!operatorStack.empty() && !(operatorStack.peek()).equals("(") && prec(symbol) <= prec((operatorStack.peek())))
					postfix = postfix + " " + operatorStack.pop();

				String operator = new String(symbol);
				operatorStack.push(operator);
			}
		}

		while (!operatorStack.empty())
			postfix = postfix + " " + operatorStack.pop();
		
		return postfix;
	}

	// method evaulates postfix expression
	private String evaluatePostfix(String postfix){

		StringTokenizer s = new StringTokenizer(postfix);
		// divides the input into tokens for input
		String value;
		String symbol;

		while (s.hasMoreTokens()){
			symbol = s.nextToken();

			if (!isSeparator(symbol)){
				// if it's an operand, push it onto stack
				operandStack.push(symbol);
			}else{
				// if it's an operator, operate on the previous two popped operandStack items
				String result="";

				if(symbol.equals("!")){
					String op = operandStack.pop();
					if(op.toLowerCase().equals("true"))
						result = "false";
					else
						result = "true";
				}else{

					String op2 = operandStack.pop();
					String op1 = operandStack.pop();

					Opr op = operatorTable.get(symbol);
					Double res;

					switch(op){
					case MULT:
						res = Double.parseDouble(op1) * Double.parseDouble(op2);
						result = res.toString();
						break;

					case PLUS:
						res = Double.parseDouble(op1) + Double.parseDouble(op2);
						result = res.toString();
						break;

					case MINUS:
						res = Double.parseDouble(op1) - Double.parseDouble(op2);
						result = res.toString();
						break;

					case DIV:
						res = Double.parseDouble(op1) / Double.parseDouble(op2);
						result = res.toString();
						break;

					case MOD:
						res = Double.parseDouble(op1) % Double.parseDouble(op2);
						result = res.toString();
						break;

					case AND:
						if (op1.toLowerCase().equals("true") && op2.toLowerCase().equals("true"))
							result = "true";
						else
							result = "false";
						break;

					case OR:
						if (op1.toLowerCase().equals("true") || op2.toLowerCase().equals("true"))
							result = "true";
						else
							result = "false";
						break;
						/*
			  case ASSIGN:
			  op1 = op2;
			  result = op1.toString();
			  break;
						 */
					case EQUALS:
						if (op1.equals(op2))
							result = "true";
						else
							result = "false";
						break;

					case NEQUALS:
						if (!(op1.equals(op2)))
							result = "true";
						else
							result = "false";
						break;

					case LESS:
						if (Double.parseDouble(op1) < Double.parseDouble(op2))
							result = "true";
						else
							result = "false";
						break;

					case LEQ:
						if (Double.parseDouble(op1) <= Double.parseDouble(op2))
							result = "true";
						else
							result = "false";
						break;

					case GREATER:
						if (Double.parseDouble(op1) > Double.parseDouble(op2))
							result = "true";
						else
							result = "false";
						break;

					case GEQ:
						if (Double.parseDouble(op1) >= Double.parseDouble(op2))
							result = "true";
						else
							result = "false";
						break;

					}
				}
				String operand = new String(result);
				operandStack.push(operand);
			}
		}
		value = operandStack.pop();
		return value;
	}

	// method compares operators to establish precedence
	private int prec(String x){

		if(x.equals("="))
			return 1;
		if(x.equals("?") || x.equals(":"))
			return 2;
		if(x.equals("||"))
			return 3;
		if(x.equals("&&"))
			return 4;
		if (x.equals("==") || x.equals("!="))
			return 8;
		if (x.equals("<") || x.equals("<=") || x.equals(">") || x.equals(">="))
			return 9;
		if (x.equals("+") || x.equals("-"))
			return 11;
		if (x.equals("*") || x.equals("/") || x.equals("%"))
			return 12;
		if (x.equals("!"))
			return 13;
		return 0;
	}

	// Check if the token is a part of operators
	private boolean isSeparator(String s){
		String str;

		for(int i=0;i<separators.length;i++){
			str = separators[i];
			if (str.equals(s)){
				return true;
			}
		}
		return false;
	}





}
