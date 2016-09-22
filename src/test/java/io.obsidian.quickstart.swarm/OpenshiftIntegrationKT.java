package io.obsidian.quickstart.swarm;

import io.fabric8.arquillian.kubernetes.Session;
import io.fabric8.kubernetes.api.KubernetesHelper;
import io.fabric8.kubernetes.client.KubernetesClient;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.fabric8.kubernetes.assertions.Assertions.assertThat;

@RunWith(Arquillian.class)
public class OpenshiftIntegrationKT {

    private static final Logger LOG = LoggerFactory.getLogger(OpenshiftIntegrationKT.class);

    @ArquillianResource
    KubernetesClient client;

    @ArquillianResource
    Session session;

    OkHttpClient httpClient = new OkHttpClient();

    @Test
    public void testAppProvisionsRunningPods() throws Exception {
        // assert that a Replication Controller exists
        assertThat(client).replicationController("swarm-camel");
    }

    @Test
    public void testHttpEndpoint() throws Exception {

        // assert that a pod is ready from the RC... It allows to capture also the logs if they barf before trying to invoke services (which may not be ready yet)
        assertThat(client).replicas("swarm-camel").pods().isPodReadyForPeriod();

        String serviceURL = KubernetesHelper.getServiceURL(client, "swarm-camel", session.getNamespace(), "http", true);
        String req = serviceURL + "/service/say/charles";
        LOG.info("### HTTP Request : " + req);

        Request request = new Request.Builder()
                .url(req)
                .build();

        Response response = httpClient.newCall(request).execute();
        Assert.assertEquals("Hello from REST endpoint to charles",response.body().string());
    }
}