package com.google.cloud.storage;

public class Main {
    public static void main(String [] args) throws Exception {
        StorageOptions so = StorageOptions.grpc().setAttemptDirectPath(true).build();
        System.out.println(so.getProjectId());
        Storage s = so.getService();
        System.out.println("storage created");
        for(int i = 0; i < 20; i++) {
            System.out.println("run" + i);
            s.list();
            Thread.sleep(1000);
        }
    }
}
