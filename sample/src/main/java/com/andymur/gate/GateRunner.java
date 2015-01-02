package com.andymur.gate;

import com.google.common.collect.Sets;
import gate.*;
import gate.corpora.DocumentImpl;
import gate.creole.ANNIEConstants;
import gate.creole.ConditionalSerialAnalyserController;
import gate.creole.ExecutionException;
import gate.creole.ResourceInstantiationException;
import gate.util.GateException;
import gate.util.persistence.PersistenceManager;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

class GateRunner {

    public static final String DEFAULT_FILE_NAME = "file:////home//andymur//workspace//gate-playground//sample//address.txt";

    public static void main(String [] args) throws MalformedURLException, ResourceInstantiationException, ExecutionException {
		System.out.println("trying to init gate...");
        ConditionalSerialAnalyserController controller = null;
        try {
			controller = init();
		} catch (Exception e) {
			System.out.println("initialization error occured!");
			System.out.println(e);
			e.printStackTrace();
		}
		System.out.println("initialization completed");
        Corpus corpus
                = createCorpusFromDocument("my corpus", DEFAULT_FILE_NAME);
        if (controller != null) {
            controller.setCorpus(corpus);

            System.out.println("corpus loaded");
            controller.execute();
            Document document = corpus.get(0);

            for (Annotation annotation: document.getAnnotations().get(Sets.newHashSet("Address"))) {
                System.out.println("get annotation of type: " + annotation.getType());
                FeatureMap featureMap = annotation.getFeatures();
                for (Map.Entry<Object, Object> entry: featureMap.entrySet()) {
                    System.out.println("feature of annotation k->v: " + entry.getKey() + "->" + entry.getValue());
                }
            }
        }
    }

	
	private static ConditionalSerialAnalyserController init() throws GateException, IOException {
		Gate.init();
        return (ConditionalSerialAnalyserController)
                PersistenceManager.loadObjectFromFile(new File(new File(
                        Gate.getPluginsHome(), ANNIEConstants.PLUGIN_DIR),
                        ANNIEConstants.DEFAULT_FILE
                ));
	}

    private static Corpus createCorpusFromDocument(String name, String documentUrl)
            throws ResourceInstantiationException, MalformedURLException {
        Corpus corpus = Factory.newCorpus(name);
        Document gateDocument = createDocumentByUrl(documentUrl);
        corpus.add(gateDocument);
        return corpus;
    }

    private static Document createDocumentByUrl(String url) throws ResourceInstantiationException,
            MalformedURLException {
        FeatureMap params = Factory.newFeatureMap();
        params.put("sourceUrl", new URL(url));
        params.put("preserveOriginalContent", true);
        params.put("collectRepositioningInfo", true);
        String documentClassName = DocumentImpl.class.getCanonicalName();
        return (Document)
                Factory.createResource(documentClassName, params);
    }
}
