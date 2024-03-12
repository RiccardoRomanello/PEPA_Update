/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.experimentation;

import java.util.ArrayList;

public class ExperimentationTest {

	public static void main(String[] args) {
		double[] array1 = new double[] { 1  };
		double[] array2 = new double[] { 1  };
		double[] array3 = new double[] { 1  };
		ArrayList<double[]> list = new ArrayList<double[]>();
		list.add(array1);
		list.add(array2);
		list.add(array3);
		int totalExperiments = getFrequency(list, 0)
		* array1.length;
		System.out.println("Experiments: " + totalExperiments);
		for (int i = 0; i < totalExperiments; i++) {
			System.out.print("]\nExp. " + i + ": [");
			for (int j=0; j < list.size(); j++) {
				System.out.print(" " + list.get(j)[getElement(list, j, i)]);
			}
		}
	}

	private static int getElement(ArrayList<double[]> arrays, int arrayNumber,
			int experimentNumber) {
		int frequency = getFrequency(arrays, arrayNumber);
		return (experimentNumber / frequency) % arrays.get(arrayNumber).length;

	}

	private static int getFrequency(ArrayList<double[]> arrays, int arrayNumber) {
		int frequency = 1;
		for (int i = arrayNumber + 1; i < arrays.size(); i++) {
			frequency *= arrays.get(i).length;
		}
		return frequency;
	}
}
