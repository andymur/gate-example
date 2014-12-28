package com.andymur.gate;
import gate.Gate;
import gate.Document;
import gate.util.GateException;
import gate.Factory;
import gate.creole.SerialAnalyserController;

import java.util.Iterator;
import java.io.File;

class GateRunner {
	public static void main(String [] args) throws GateException {
		System.out.println("trying to init gate...");
		init();
		System.out.println("initialization completed");
	}
	
	private static void init() throws GateException {
		Gate.init();
	}
}
