/*
 * Copyright 2024-2025 Kereval.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.ihe.gazelle.maestro.quarkus;


import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import net.ihe.gazelle.maestro.quarkus.annotation.InjectConnection;
import org.testcontainers.Testcontainers;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.MountableFile;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DatahouseTestResource implements QuarkusTestResourceLifecycleManager {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(DatahouseTestResource.class);

    private static Network network;

    private static MongoDBContainer mongoDBContainer;

    private static MongoDatabase mongoDatabase;


    private static MongoClient mongoClient;

    private static GenericContainer<?> datahouseContainer;

    @Override
    public Map<String, String> start() {
        Map<String, String> envVariables = getEnvVariables("/.env");
        String mongoUser = envVariables.get("MONGODB_INITDB_ROOT_USERNAME");
        String mongoPassword = envVariables.get("MONGODB_INITDB_ROOT_PASSWORD");
        network = Network.newNetwork();
        mongoDBContainer = new MongoDBContainer(envVariables.get("MONGO_IMAGE"))
                .withNetwork(network)
                .withExposedPorts(27017)
                .withEnv("MONGODB_INITDB_ROOT_USERNAME", mongoUser)
                .withEnv("MONGODB_INITDB_ROOT_PASSWORD", mongoPassword)
                .withNetworkAliases("mongoDBTest")
                .withAccessToHost(true)
        ;
        String host = mongoDBContainer.getHost();
        mongoDBContainer.start();

        //KC
        WireMockSingleton.mockKeycloak();

        String clientUri = "mongodb://"+host+":"+mongoDBContainer.getMappedPort(27017)+"/test";

        mongoClient = MongoClients.create(clientUri);
        mongoDatabase = mongoClient.getDatabase("datahouse");
        mongoDatabase.createCollection("mongockLock");
        mongoDatabase.createCollection("mongockChangeLog");

        String mongoUri = "mongodb://mongoDBTest:27017/test";
        logger.info("Mongo URI: {}", mongoUri);
        Testcontainers.exposeHostPorts(28080);
        datahouseContainer = new GenericContainer<>(envVariables.get("DATAHOUSE_CONTAINER_IMAGE"))
                .withExposedPorts(8080)
                .withCreateContainerCmdModifier(cmd -> cmd.withName("gazelle-datahouse-test"))
                .withNetwork(network)
                .withAccessToHost(true)
                .withCopyFileToContainer(MountableFile.forClasspathResource("datahouse/index_configs.json"), "/opt/datahouse/index_configs.json")
                .withEnv("MONGODB_CONNECTION_STRING", mongoUri)
                .withEnv("DATAHOUSE_INDEX_VERIFICATION", "false")
                .withEnv("CONFIG_FILE_PATH", "/opt/datahouse/index_configs.json")
                .withEnv(getEnvVariables("/datahouse/.env"))
                .waitingFor(Wait.forHttp("/datahouse/rest/v1/indexes").withReadTimeout(Duration.ofMinutes(2)).forPort(8080))
                .withLogConsumer(outputFrame -> logger.info("gazelle-datahouse: {}",outputFrame.getUtf8String()));
        datahouseContainer.start();

        int datahousePort = datahouseContainer.getMappedPort(8080);
        return Map.of(
                "gzl.datahouse.url", "http://"+host+":" + datahousePort+"/datahouse/rest/v1",
                "gzl.root.test.bed.url", "http://"+host+":" + datahousePort+"/"
        );
    }

    @Override
    public void inject(TestInjector testInjector) {
        // inject connection
        testInjector.injectIntoFields(mongoDatabase, new TestInjector.AnnotatedAndMatchesType(InjectConnection.class, MongoDatabase.class));
    }


    @Override
    public void stop() {
        mongoClient.close();
        mongoDBContainer.stop();
        datahouseContainer.stop();
        network.close();
    }

    static Map<String, String> getEnvVariables(String envPath) {
        Map<String, String> envMap = new HashMap<>();
        String fullPath = Objects.requireNonNull(DatahouseTestResource.class.getClassLoader().getResource(envPath)).getPath();
        try (BufferedReader reader = new BufferedReader(new FileReader(fullPath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Assuming each line in the .env file has the format "KEY=VALUE"
                int equalsIndex = line.indexOf('=');
                if (equalsIndex != -1) {
                    String key = line.substring(0, equalsIndex);
                    String value = line.substring(equalsIndex + 1)
                            .replace("\"", "");
                    envMap.put(key, value);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return envMap;
    }


}
