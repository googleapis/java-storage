package com.google.cloud.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

public class Main {
  static {
    SLF4JBridgeHandler.removeHandlersForRootLogger();
    SLF4JBridgeHandler.install();
  }

  private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

  public static void main(String[] args) throws Exception {
    LOGGER.debug(">>> main(args : {})", args);
    StorageOptions so = StorageOptions.grpc().setAttemptDirectPath(true).build();
    LOGGER.info("so.getProjectId() = {}", so.getProjectId());
    try (Storage s = so.getService()) {
      LOGGER.info("Storage created");
      for (int i = 0; i < 20; i++) {
        LOGGER.info("i = {}", i);
        s.list();
        Thread.sleep(1000);
      }
    }
    LOGGER.info("Done. Exiting...");
  }
}
