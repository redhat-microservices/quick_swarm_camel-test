package io.fabric8.quickstarts.swarm;

import io.fabric8.arquillian.kubernetes.Session;
import io.fabric8.kubernetes.api.KubernetesHelper;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.KubernetesClient;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.assertj.core.api.Condition;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Assert;
import org.junit.Ignore;
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

    @Test
    public void testReplicationControllerAndServiceExists() throws Exception {
        LOG.info("## Testing if Replication Controller exists");

        String serviceName = "swarm-camel";
        assertThat(client).replicationController(serviceName).isNotNull();
        assertThat(client).hasServicePort(serviceName, 8080);
    }

    @Test
    public void testPod() throws Exception {
        LOG.info("## Testing Swarm Pod");
        LOG.info("## Namespace : " + session.getNamespace());

        assertThat(client).pods()
                .runningStatus()
                .filterNamespace(session.getNamespace())
                .haveAtLeast(1, new Condition<Pod>() {
                    @Override
                    public boolean matches(Pod podSchema) {
                        return true;
                    }
                });
    }
}