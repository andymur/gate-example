package com.andymur.gate;

import gate.*;
import gate.creole.ANNIEConstants;
import gate.creole.ConditionalSerialAnalyserController;
import gate.creole.ResourceInstantiationException;
import gate.util.GateException;
import gate.util.persistence.PersistenceManager;

import java.io.File;
import java.io.IOException;

class GateRunner {
	public static void main(String [] args) {
		System.out.println("trying to init gate...");
		try {
			init();
		} catch (Exception e) {
			System.out.println("initialization error occured!");
            e.printStackTrace();
		}
		System.out.println("initialization completed");
	}
	
	private static void init() throws GateException, IOException {
		Gate.init();
        ConditionalSerialAnalyserController controller = (ConditionalSerialAnalyserController)
                PersistenceManager.loadObjectFromFile(new File(new File(
                        Gate.getPluginsHome(), ANNIEConstants.PLUGIN_DIR),
                        ANNIEConstants.DEFAULT_FILE
                ));
	}

    private static Corpus createCorpusFromDocument(String name, String documentUrl) throws ResourceInstantiationException {
        Corpus corpus = Factory.newCorpus(name);
        Document gateDocument = createDocumentByUrl(documentUrl);
        corpus.add(gateDocument);
        return corpus;
    }

    private static Document createDocumentByUrl(String url) throws ResourceInstantiationException {
        FeatureMap params = Factory.newFeatureMap();
        params.put("sourceUrl", url);
        params.put("preserveOriginalContent", new Boolean(true));
        params.put("collectRepositioningInfo", new Boolean(true));
        return (Document) Factory.createResource(gate.corpora.DocumentContentImpl.class.getCanonicalName(),
                params);
    }
}
