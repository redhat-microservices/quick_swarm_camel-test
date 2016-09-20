# Swarm Camel REST Quickstart

## Introduction

This quickstart uses WildFly Swarm as Java container and the Apache Camel Integration Framework to expose a RESTfull endpoint registered within the Undertow server.
This example uses the REST fluent DSL to define a service which provides one operation

- GET api/say/{id}       - Say Hello to the user name

The Camel modules deployed within the WildFly Swarm container are defined within the pom.xml definition file. To expose the JMX MBeans using a HTTP endpoint, we have also
packaged to this quickstart project, the [jolokia](https://jolokia.org/reference/html/protocol.html) technology which allows to query your MBeans using JSon over HTTP.

To configure jolokia, we must declare a Wildfly [fraction](https://wildfly-swarm.gitbooks.io/wildfly-swarm-users-guide/content/v/6a00bb344527303f784f541ee2fb93abec4a1ef4/fraction_authoring.html) which is the composable piece of the platform
in order to deploy and customize the module (example: to define the URL path to access the Jolokia JMX resource).

The static resources (index.html file containing the link to the swagger.json doc file) like also the package containing the Camel route are defined within the WAR Archive which is created using ShrinkWrap and deployed after the Container has been started. 

The Camel context is created using the CDI Weld Container which is reponsible to scan the classes to discover the @ApplicationScope annotation like also the the @CamelContext annotation which is managed as a cdi extension.

The MainApp class is bootstrapped by the WildFly Swarm container when we launch it.

```
public static void main(String[] args) throws Exception {
	Container container = new Container();
    container.fraction(new JolokiaFraction("/jmx"));
    container.start();

    WARArchive deployment = ShrinkWrap.create(WARArchive.class);
    deployment.addPackage("io.fabric8.quickstarts.swarm.route");
    deployment.staticContent();

    container.deploy(deployment);
```

## Build

You will need to compile this example first:

    mvn install

## Run

To run the example type

    mvn wildfly-swarm:run

The rest service can be accessed from the following url

    curl http://localhost:8080/service/say/{name}
<http://localhost:8080/service/say/{name}>

For example to say Hello for the name `charles`

    curl http://localhost:8080/service/say/charles
<http://localhost:8080/service/say/charles>

The rest services provides Swagger API which can be accessed from the following url

    curl http://localhost:8080/swagger.json
<http://localhost:8080/swagger.json>

To stop the example hit <kbd>ctrl</kbd>+<kbd>c</kbd>

## Jolokia & JMX

We have registered the Jolokia fraction in order to access the JMX operations or attributes using the JSon HTTP Servlet Bridge offered by the
[jolokia](https://jolokia.org/reference/html/protocol.html) project.

Here are some curl request that we can use to grab JVM data

```
curl -X GET http://localhost:8080/jmx
curl -d "{\"type\":\"read\",\"mbean\":\"java.lang:type=Memory\",\"attribute\":\"HeapMemoryUsage\",\"path\":\"used\"}" http://localhost:8080/jmx/ && echo ""
```

## Running the example on OpenShift

It is assumed that an OpenShift platform is already running. If not, you can find details how to setup the infrastructure hereafter and more information here 
[get started](https://github.com/jimmidyson/minishift).

* Launch minishift

```
minishift delete
minishift start --deploy-registry=true --deploy-router=true --memory=4048 --vm-driver="xhyve"
minishift docker-env
eval $(minishift docker-env)
```

Remark : Don't forget to be authenticated with the OpenShift Server using the command `oc login`

The example can be built and deployed using a single goal:

    mvn -Popenshift-local-deploy

When the example runs in fabric8, you can use the OpenShift client tool to inspect the status

To list all the running pods:

    oc get pods

Then find the name of the pod that runs this quickstart, and output the logs from the running pods with:

    oc logs <name of pod>

You can also use the OpenShift web console to manage the running pods, and view logs and much more.

## Access services using a web browser

You can use any browser to perform a HTTP GET. This allows you to very easily test a few of the RESTful services we defined:

Notice: As it depends on your OpenShift setup, the hostname (route) might vary. Verify with oc get routes which hostname is valid for you.

Use this URL to display the response message from the REST service:

    export serviceURL=$(minishift service swarm-camel --url=true)
    curl $serviceURL/service/say/charles

## Testing

To test the service and also the pod deployed, we have created an integration test using the [Arquillian Testing framework](http://arquillian.org/) and the [Kubernetes Client
Api](https://github.com/fabric8io/fabric8/tree/master/components/fabric8-arquillian) responsible to talk with the OpenShift platform in order to retrieve the pods, services, replication controllers, ... and to perform some assertions.

To check that a replication controller exists, you will design such JUnit Test

```java

@ArquillianResource
KubernetesClient client;
    
@Test
public void testAppProvisionsRunningPods() throws Exception {
    // assert that a Replication Controller exists
    assertThat(client).replicationController("swarm-camel");
}
```

and to test a HTTP endpoint/service deployed

```java
@Test
public void testHttpEndpoint() throws Exception {
    // assert that a pod is ready from the RC... It allows to capture also the logs if they barf before trying to invoke services (which may not be ready yet)
    assertThat(client).replicas("swarm-camel").pods().isPodReadyForPeriod();

    // Fech the External Address of the Service
    String serviceURL = KubernetesHelper.getServiceURL(client,"swarm-camel",KubernetesHelper.DEFAULT_NAMESPACE,"http",true);
```

To run the Junit tests, simply exexute this maven command

    mvn test -Dtest=KubernetesIntegrationKT
