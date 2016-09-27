/**
 *  Copyright 2005-2015 Red Hat, Inc.
 *
 *  Red Hat licenses this file to you under the Apache License, version
 *  2.0 (the "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *  implied.  See the License for the specific language governing
 *  permissions and limitations under the License.
 */
package io.obsidian.quickstart.swarm;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.wildfly.swarm.Swarm;
import org.wildfly.swarm.config.logging.FileHandler;
import org.wildfly.swarm.config.logging.Level;
import org.wildfly.swarm.config.logging.LogFile;
import org.wildfly.swarm.jolokia.JolokiaFraction;
import org.wildfly.swarm.logging.LoggingFraction;
import org.wildfly.swarm.undertow.WARArchive;

import java.util.HashMap;
import java.util.Map;

public class MainApp {

	public static void main(String[] args) throws Exception {

		Map<String, String> fileSpec = new HashMap<>();
		fileSpec.put("path","/Users/chmoulli/Temp/log/swarm.log");
		fileSpec.put("level",Level.INFO.toString());
		fileSpec.put("formatter","%d{HH:mm:ss,SSS} %-5p [%c] (%t) %s%e%n");

		FileHandler logFile = new FileHandler("swarm-camel");
		logFile.file(fileSpec);

		Swarm container = new Swarm();
		container.fraction(new JolokiaFraction("/jmx"));
        container.fraction(new LoggingFraction().fileHandler(logFile));
		container.start();

		WARArchive deployment = ShrinkWrap.create(WARArchive.class);
		deployment.addPackage("io.obsidian.quickstart.swarm.route");
		deployment.staticContent();

		container.deploy(deployment);
	}
}
