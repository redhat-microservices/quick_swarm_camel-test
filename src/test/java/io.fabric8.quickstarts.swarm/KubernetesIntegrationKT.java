package io.fabric8.quickstarts.swarm;

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
public class KubernetesIntegrationKT {

    private static final Logger LOG = LoggerFactory.getLogger(KubernetesIntegrationKT.class);

    @ArquillianResource
    KubernetesClient client;

    OkHttpClient httpClient = new OkHttpClient();

    @Test
    public void testAppProvisionsRunningPods() throws Exception {
        assertThat(client).replicationController("swarm-camel");
    }

    @Test
    public void testHttpEndpoint() throws Exception {

        String serviceURL = KubernetesHelper.getServiceURL(client,"swarm-camel",KubernetesHelper.DEFAULT_NAMESPACE,"http",true);
        String req = serviceURL + "/service/say/charles";
        LOG.info("### HTTP Request : " + req);

        Request request = new Request.Builder()
                .url(req)
                .build();

        Response response = httpClient.newCall(request).execute();
        Assert.assertEquals("Hello from REST endpoint to charles",response.body().string());
    }
}