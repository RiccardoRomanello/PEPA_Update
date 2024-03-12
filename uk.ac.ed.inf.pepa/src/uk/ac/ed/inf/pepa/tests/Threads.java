/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.tests;

import java.util.Random;

public class Threads {
	
	private static class WorkerThread extends Thread {
		
		private int computations;
		
		public WorkerThread(int computations) {
			
			this.computations = computations;
		}
		public void run() {
			Random r = new Random();
			double sum = 0;
			for (int i = 0; i < computations; i++)
				sum += r.nextDouble();
		}
	}
	
	public static void main(String[] args) {
		
		long tic = System.currentTimeMillis();
		
		int N = 4;
		
		int computations = (Integer.MAX_VALUE / 25 ) / N;
		
		Thread[] threads = new Thread[N];
		
		for (int i = 0; i < N; i++) {
			threads[i] = new WorkerThread(computations);
			threads[i].start();
		}
		
		for (int i = 0; i < N; i++) {
			try {
				threads[i].join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		long elapsed = System.currentTimeMillis() - tic;
		
		System.out.println("Elapsed " + N + " : "+ elapsed);
		
		
	}

}
