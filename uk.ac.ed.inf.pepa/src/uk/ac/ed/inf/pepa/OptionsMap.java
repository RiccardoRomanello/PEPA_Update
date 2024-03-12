/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class OptionsMap {
	
	public enum SolverType {
		CTMC, Stochastic, ODE;
	}
	
	public enum Solver {
		Direct(SolverType.CTMC, "direct", "MTJ Direct solver"){public Parameter[] getRequiredParameters() {return new Parameter[] {};}},
		CG(SolverType.CTMC, "cg", "MTJ Conjungate Gradient solver"){public Parameter[] getRequiredParameters() {return new Parameter[] {};}},
		CGS(SolverType.CTMC, "cgs", "MTJ Conjungate Gradient Stablized solver"){public Parameter[] getRequiredParameters() {return new Parameter[] {};}},
		BiCG(SolverType.CTMC, "bicg", "MTJ BiConjungate Gradient solver"){public Parameter[] getRequiredParameters() {return new Parameter[] {};}},
		BiCGS(SolverType.CTMC, "bicgs", "BiConjungate Gradient Stablized solver"){public Parameter[] getRequiredParameters() {return new Parameter[] {};}},
		GMRES(SolverType.CTMC, "gmres", "MTJ GMRES solver"){public Parameter[] getRequiredParameters() {return new Parameter[] {};}},
		IR(SolverType.CTMC, "ir", "MTJ Iterative Refinement solver"){public Parameter[] getRequiredParameters() {return new Parameter[] {};}},
		QMR(SolverType.CTMC, "qmr", "MTJ Quasi-Minimal Residual solver"){public Parameter[] getRequiredParameters() {return new Parameter[] {};}},
		// Chebyshev(SolverType.CTMC, "chebyshev", "", new Parameter[] {}),
		Gillespie(SolverType.Stochastic, "gillespie", "Gillespies Stochastic Algorithm"){public Parameter[] getRequiredParameters() {return new Parameter[] {Parameter.Start_Time, Parameter.Stop_Time, Parameter.Components, Parameter.Data_Points, Parameter.Independent_Replications, Parameter.Confidence_Interval};}},
		// TauLeap(SolverType.Stochastic, "tau-leap", "Tauleap Algorithm"){public Parameter[] getRequiredParameters() {return new Parameter[] {Parameter.Start_Time, Parameter.Stop_Time, Parameter.Components, Parameter.Data_Points, Parameter.Independent_Replications, Parameter.Step_Size, Parameter.Relative_Error, Parameter.Confidence_Interval};}},
		Gibson_Bruck(SolverType.Stochastic, "gibson-bruck", "Gibson-Bruck Stochastic Algorithm"){public Parameter[] getRequiredParameters() {return new Parameter[] {Parameter.Start_Time, Parameter.Stop_Time, Parameter.Components, Parameter.Data_Points, Parameter.Independent_Replications, Parameter.Confidence_Interval};}},
		// RK5_Adaptive(SolverType.ODE, "rk5-adaptive", "Adaptive step-size 5th-order Runge Kutta ODE Solver"){public Parameter[] getRequiredParameters() {return new Parameter[] {Parameter.Start_Time, Parameter.Stop_Time, Parameter.Components, Parameter.Data_Points, Parameter.Step_Size, Parameter.Relative_Error, Parameter.Absolute_Error};}},
		// RK5_Fixed(SolverType.ODE, "rk5-fixed", "Fixed step-size 5th-order Runge Kutta ODE Solver"){public Parameter[] getRequiredParameters() {return new Parameter[] {Parameter.Start_Time, Parameter.Stop_Time, Parameter.Components, Parameter.Data_Points, Parameter.Step_Size, Parameter.Relative_Error, Parameter.Absolute_Error};}},
		DOPR(SolverType.ODE, "dopr-adaptive", "Adaptive step-size 5th-order Dormand Prince ODE Solver"){public Parameter[] getRequiredParameters() {return new Parameter[] {Parameter.Start_Time, Parameter.Stop_Time, Parameter.Components, Parameter.Data_Points, Parameter.Step_Size, Parameter.Relative_Error, Parameter.Absolute_Error};}},
		IMEX(SolverType.ODE, "imex-stiff", "Implicit-Explicit Runge Kutta ODE Solver"){public Parameter[] getRequiredParameters() {return new Parameter[] {Parameter.Start_Time, Parameter.Stop_Time, Parameter.Components, Parameter.Data_Points, Parameter.Step_Size, Parameter.Relative_Error, Parameter.Absolute_Error};}};
		
		SolverType type;
		String argument, name;
				
		Solver(SolverType type, String argument, String name) {
			this.type = type;
			this.argument = argument;
			this.name = name;
		}
		
		public String getDescriptiveName() {
			return name;
		}
		
		public String getArgumentName() {
			return argument;
		}
		
		public SolverType getType() {
			return type;
		}
		
		public String toString() {
			return argument;
		}
		
		public abstract Parameter[] getRequiredParameters();
	}
	
	public enum Parameter {
		Solver("Solver", "solver", Solver.class, OptionsMap.Solver.Direct),
		// CTMC parameters here
		PreConditioner("Preconditioner", "preconditioner", PreConditioner.class, OptionsMap.PreConditioner.None),
		// SBA parameters here
		Start_Time("Start time","start-time", Double.class, 0.00),
		Stop_Time("Stop time","stop-time", Double.class, 100.0),
		Step_Size("Step size", "step-size", Double.class, 0.001),
		Data_Points("Number of data points", "data-points", Integer.class, 100),
		Components("Components", "components", (new String[] {}).getClass(), new String[] {}),
		Independent_Replications("Number of independent replications", "replications", Integer.class, 1),
		Relative_Error("Relative error", "relative-error", Double.class, 0.0001),
		Absolute_Error("Absolute error", "absolute-error", Double.class, 0.0001),
		Confidence_Interval("Confidence interval", "confidence-interval", Double.class, 0.05);
		
		Class<?> parameterClass;
		String argument, descriptiveName;
		Object defaultValue;
		
		Parameter(String name, String argument, Class<?> parameterClass, Object defaultValue) {
			this.descriptiveName = name;
			this.argument = argument;
			this.parameterClass = parameterClass;
			this.defaultValue = defaultValue;
		}
		
		public Object getDefault() {
			return defaultValue;
		}
		
		public String toString() {
			return descriptiveName;
		}
		
		public String getKey() {
			return "s9552712-" + argument; // matric temporary addition
		}
		
		public Class<? extends Object> getType() {
			return parameterClass;
		}
	}
	
	public enum PreConditioner {
		None, AMG, AMGnoSSOR, ICC, SSOR, Diagonal, ILU, ILUT; 
	}
	
	private HashMap<Parameter, Object> options;
	
	public OptionsMap() {
		options = new HashMap<Parameter, Object>();
		options.put(Parameter.Solver, Parameter.Solver.defaultValue);
		// CTMC options
		options.put(Parameter.PreConditioner, Parameter.PreConditioner.defaultValue);
		// SBA options
		options.put(Parameter.Start_Time, Parameter.Start_Time.defaultValue);
		options.put(Parameter.Stop_Time, Parameter.Stop_Time.defaultValue);
		options.put(Parameter.Step_Size, Parameter.Step_Size.defaultValue);
		options.put(Parameter.Independent_Replications, Parameter.Independent_Replications.defaultValue);
		options.put(Parameter.Relative_Error, Parameter.Relative_Error.defaultValue);
		options.put(Parameter.Absolute_Error, Parameter.Absolute_Error.defaultValue);
		options.put(Parameter.Data_Points, Parameter.Data_Points.defaultValue);
		options.put(Parameter.Components, Parameter.Components.defaultValue);
		options.put(Parameter.Confidence_Interval, Parameter.Confidence_Interval.defaultValue);
	}
	
	public Object getValue(Parameter parameter) {
		return options.get(parameter);
	}
	
	public String serialise(Parameter parameter) {
		if(parameter.equals(Parameter.Components)) {
			String[] sa = (String[]) options.get(parameter);
			StringBuilder sb = new StringBuilder();
			for(String s : sa)
				sb.append(s.length()).append(":").append(s);
			return sb.toString(); // bencoded
		}
		return options.get(parameter).toString();
	}
	
	public void setValue(Parameter parameter, Object value) {
		if(parameter.parameterClass.isInstance(value))
			options.put(parameter, value);
		else if((parameter.equals(Parameter.Solver) || parameter.equals(Parameter.PreConditioner)) && value instanceof String) {
			Object[] o;
			String s = (String) value;
			if(parameter.equals(Parameter.Solver))
				o = Solver.values();
			else
				o = PreConditioner.values();
			for(Object oo : o) {
				if(oo.toString().equals(s)) {
					options.put(parameter, oo);
					return;
				}
			}
			throw new IllegalArgumentException("Value is not of type " + parameter.parameterClass.getName() + " and cannot be constructed using a String as a parameter.");
		}else if(parameter.equals(Parameter.Components) && value instanceof String) {
			StringBuilder sb = new StringBuilder((String) value);
			ArrayList<String> al = new ArrayList<String>();
			int index, length;
			try {
				while(sb.length() > 0) {
					index = sb.indexOf(":");
					length = Integer.parseInt(sb.substring(0, index));
					sb.delete(0, index+1);
					al.add(sb.substring(0, length));
					sb.delete(0, length);
				}
			} catch(NumberFormatException e) {
				throw new IllegalArgumentException(parameter.descriptiveName + " requires a legal bencoded string.");
			}
			options.put(parameter, al.toArray(new String[] {}));
		}else if(value instanceof String) { // try and cast...
			try {
				Constructor<?> constructor = parameter.parameterClass.getConstructor(String.class);
				Object o = constructor.newInstance(value);
				options.put(parameter, o);
			} catch(Exception e) {
				throw new IllegalArgumentException("Value is not of type " + parameter.parameterClass.getName() + " and cannot be constructed using a String as a parameter.");
			}
		}
		else
			throw new IllegalArgumentException("Value is not of type " + parameter.parameterClass.getName());
	}
	
	public Set<Parameter> keySet() {
		return options.keySet();
	}
}
