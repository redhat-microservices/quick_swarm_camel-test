package io.obsidian.quickstart.swarm;

import io.fabric8.arquillian.kubernetes.Session;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.assertj.core.api.Condition;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
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
    public void testPodIsReady() throws Exception {
        // assert that a pod is ready from the RC... It allows to capture also the logs if they barf before trying to invoke services (which may not be ready yet)
        assertThat(client).replicas("swarm-camel").pods().isPodReadyForPeriod();
    }

    @Test
    @Ignore
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