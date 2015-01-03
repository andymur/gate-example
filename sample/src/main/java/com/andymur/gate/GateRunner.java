package com.andymur.gate;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import gate.*;
import gate.corpora.DocumentImpl;
import gate.creole.ANNIEConstants;
import gate.creole.ConditionalSerialAnalyserController;
import gate.creole.ExecutionException;
import gate.creole.ResourceInstantiationException;
import gate.util.GateException;
import gate.util.InvalidOffsetException;
import gate.util.persistence.PersistenceManager;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;
import java.util.Set;

class GateRunner {

    public static final String DEFAULT_FILE_NAME = "address.txt";

    public static void main(String [] args) throws MalformedURLException, ResourceInstantiationException,
            ExecutionException, InvalidOffsetException {

		System.out.println("trying to init gate...");
        ConditionalSerialAnalyserController controller = null;
        try {
			controller = init();

            System.out.println("initialization completed");

            Corpus corpus
                    = createCorpusFromDocument("my corpus", DEFAULT_FILE_NAME);

            if (controller != null) {
                controller.setCorpus(corpus);

                System.out.println("corpus loaded");
                controller.execute();

                Document document = corpus.get(0);

                Map<String, String> nameToAnnotations = Maps.newHashMap(ImmutableMap.<String, String>of(
                        "position", "JobTitle", "person", "Person", "address", "Address", "organization", "Organization"));

                for (String name: nameToAnnotations.keySet()) {
                    Set<String> content = getContentAnnotatedBy(document, nameToAnnotations.get(name));
                    System.out.println("category: " + name + " values: " + content);
                }
            }

		} catch (Exception e) {
			System.out.println("initialization error occured!");
			System.out.println(e);
			e.printStackTrace();
		}

    }

    private static Set<String> getContentAnnotatedBy(Document document, String annotationName)
            throws InvalidOffsetException {

        Set<String> contentList = Sets.newHashSet();
        DocumentContent content = document.getContent();

        for (Annotation annotation: document.getAnnotations().get(Sets.newHashSet(annotationName))) {

            long startOffset = annotation.getStartNode().getOffset().longValue();
            long endOffset = annotation.getEndNode().getOffset().longValue();

            String annotationContent = content.getContent(startOffset, endOffset).toString();
            contentList.add(annotationContent);
        }

        return contentList;
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
        params.put("sourceUrl", new File(DEFAULT_FILE_NAME).toURI().toURL());
        params.put("preserveOriginalContent", true);
        params.put("collectRepositioningInfo", true);
        String documentClassName = DocumentImpl.class.getCanonicalName();
        return (Document)
                Factory.createResource(documentClassName, params);
    }
}
