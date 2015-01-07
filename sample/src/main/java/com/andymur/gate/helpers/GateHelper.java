package com.andymur.gate.helpers;

import gate.Controller;
import gate.Gate;
import gate.creole.ANNIEConstants;
import gate.creole.ConditionalSerialAnalyserController;
import gate.util.GateException;
import gate.util.persistence.PersistenceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * Created by andymur on 1/8/15.
 */
public class GateHelper {
    private static Controller controller;

    public static Controller getController(String gateHome, String pluginDir,
                                           String userConfig) throws GateException, IOException {

        if (controller == null) {
            Gate.setGateHome(new File(Thread.currentThread().getContextClassLoader().getResource(gateHome).getFile()));
            Gate.setSiteConfigFile(new File(Thread.currentThread().getContextClassLoader().getResource(
                    userConfig).getFile()));
            Gate.setPluginsHome(new File(Thread.currentThread().getContextClassLoader().getResource(
                    pluginDir).getFile()));
            Gate.init();
            
            controller = (ConditionalSerialAnalyserController)
                    PersistenceManager.loadObjectFromFile(new File(new File(
                            Gate.getPluginsHome(), ANNIEConstants.PLUGIN_DIR),
                            ANNIEConstants.DEFAULT_FILE
                    ));
        }

        return controller;
    }

}
