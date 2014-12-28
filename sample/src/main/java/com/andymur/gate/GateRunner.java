package com.andymur.gate;
import gate.Gate;
import gate.Document;
import gate.util.GateException;
import gate.Factory;

import java.util.Iterator;
import java.io.File;

class GateRunner {
	public static void main(String [] args) {
		System.out.println("trying to init gate...");
		try {
			init();
		} catch (Exception e) {
			System.out.println("initialization error occured!");
		}
		System.out.println("initialization completed");
	}
	
	private static void init() throws GateException {
		Gate.init();
	}
}
