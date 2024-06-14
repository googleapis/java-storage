package com.google.cloud.storage;

import com.google.cloud.storage.it.runner.StorageITRunner;
import com.google.cloud.storage.it.runner.annotations.Backend;
import com.google.cloud.storage.it.runner.annotations.CrossRun;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.sdk.metrics.SdkMeterProvider;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(StorageITRunner.class)
@CrossRun(
        backends = {Backend.PROD},
        transports = {TransportCompatibility.Transport.GRPC})
public class ITGrpcMetricsTest {
    @Test
    public void testGrpcMetrics() {
        GlobalOpenTelemetry.resetForTest(); // avoids problems with "GlobalOpenTelemetry.set has already been called"
        Storage storage = StorageOptions.grpc().build().getService();
        Assert.assertEquals("monitoring.googleapis.com:443",
                ((GrpcStorageOptions) storage.getOptions()).getCloudMonitoringEndpoint("storage.googleapis.com:443"));
        SdkMeterProvider provider = ((GrpcStorageOptions)storage.getOptions())
                .createMeterProvider("monitoring.googleapis.com:443");


        /** SDKMeterProvider doesn't expose the relevant fields we want to test, but they are present
         * in the String representation, so we'll check that instead.
         * Most of the resources are auto-set, and will depend on environment, which could cause flakes to check.
         * We're only responsible for setting the project ID, endpoint, and Histogram boundaries,
         * so we'll just check those
         **/
        String result = provider.toString();

        Assert.assertTrue(result.contains(storage.getOptions().getProjectId()));

        // This is the check for the Seconds histogram boundary. We can't practically check for every boundary,
        // but if *any* are present, that means they're different from the results and we successfully set them
        Assert.assertTrue(result.contains("1.2"));

        // This is the check for the Size boundary
        Assert.assertTrue(result.contains("131072"));
    }

    @Test
    public void testGrpcMetrics_universeDomain() {
        GlobalOpenTelemetry.resetForTest();
        Storage storage = StorageOptions.grpc().setUniverseDomain("my-universe-domain.com").build().getService();
        Assert.assertEquals("monitoring.my-universe-domain.com:443",
                ((GrpcStorageOptions) storage.getOptions()).getCloudMonitoringEndpoint("storage.my-universe-domain.com:443"));
    }

    @Test
    public void testGrpcMetrics_private() {
        GlobalOpenTelemetry.resetForTest();
        Storage storage = StorageOptions.grpc().setHost("https://private.googleapis.com").build().getService();

        Assert.assertEquals("private.googleapis.com:443",
                ((GrpcStorageOptions) storage.getOptions()).getCloudMonitoringEndpoint("private.googleapis.com:443"));
    }

    @Test
    public void testGrpcMetrics_restricted() {
        GlobalOpenTelemetry.resetForTest();
        Storage storage = StorageOptions.grpc().setHost("https://restricted.googleapis.com").build().getService();

        Assert.assertEquals("restricted.googleapis.com:443",
                ((GrpcStorageOptions) storage.getOptions()).getCloudMonitoringEndpoint("restricted.googleapis.com:443"));
    }
}
