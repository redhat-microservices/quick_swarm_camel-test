package io.obsidian.quickstart.swarm;

import io.fabric8.kubernetes.api.KubernetesHelper;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.client.KubernetesClient;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.arquillian.cube.kubernetes.impl.requirement.RequiresKubernetes;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Named;

import java.util.List;

import static io.fabric8.kubernetes.assertions.Assertions.assertThat;

@RunWith(Arquillian.class)
@RequiresKubernetes
public class OpenshiftIntegrationKT {

    private static final Logger LOG = LoggerFactory.getLogger(OpenshiftIntegrationKT.class);

    @ArquillianResource
    KubernetesClient client;

    @Named("swarm-camel")
    @ArquillianResource
    Service sayHello;

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

        List<String> IPs = sayHello.getSpec().getExternalIPs();
        for(String ip : IPs) {
            LOG.info("### External : IP : " + ip);
        }

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