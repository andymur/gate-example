package com.andymur.gate;
import gate.Gate;
import gate.Document;
import gate.util.GateException;
import gate.Factory;
import gate.creole.*;
import gate.util.persistence.*;
import gate.creole.ANNIEConstants;
import gate.util.persistence.PersistenceManager;

import java.util.Iterator;
import java.io.File;
import java.io.*;

class GateRunner {
	public static void main(String [] args) {
		System.out.println("trying to init gate...");
		try {
			init();
		} catch (Exception e) {
			System.out.println("initialization error occured!");
			System.out.println(e);
			e.printStackTrace();
		}
		System.out.println("initialization completed");
	}
	
	private static void init() throws GateException, IOException {
		Gate.init();
		PersistenceManager p;
		File f = new File(new File(Gate.getPluginsHome(), ANNIEConstants.PLUGIN_DIR), ANNIEConstants.DEFAULT_FILE);
		ConditionalSerialAnalyserController controller = (ConditionalSerialAnalyserController) PersistenceManager.loadObjectFromFile(f);
	}
}
