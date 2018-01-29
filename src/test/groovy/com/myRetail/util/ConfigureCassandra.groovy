package com.myRetail.util

import com.datastax.driver.core.Cluster
import com.datastax.driver.core.Session
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.apache.commons.io.IOUtils

@CompileStatic
@Slf4j
class ConfigureCassandra {

    private static final String[] CONTACT_POINTS = ["localhost"]

    static void makeCcdKeyspace() throws IOException {

        final Cluster.Builder builder = Cluster.builder().addContactPoints(CONTACT_POINTS)
        final Cluster cluster = builder.build()
        final Session session = cluster.newSession()

        createKeyspace(session)

        session.close()
        cluster.close()
    }

    private static void createKeyspace(final Session session) {
        try {
            InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("setup.cql")
            String cql = IOUtils.toString(inputStream, "UTF8")
            for (String command : getCommands(cql)) {
                log.info("executing CQL=${command}")
                session.execute(command)
            }

        } catch (IOException e) {
            throw new RuntimeException(e)
        }
    }

    private static List<String> getCommands(String cql) {
        String[] split = cql.split(';')

        List<String> list = new ArrayList<>(split.length)

        for (String string : split) {
            String command = string.replaceAll('\\s+', ' ').trim()
            if (command.length() > 0) {

                if (isCreateKeyspace(command)) {
                    command = alterCreateKeyspace(command)
                }

                list.add(command)
            }
        }

        return list
    }

    private static boolean isCreateKeyspace(String command) {
        return command.toUpperCase().startsWith('CREATE KEYSPACE')
    }

    private static String alterCreateKeyspace(String command) {
        return command.replace('\'class\'\\s+:\\s+\'NetworkTopologyStrategy\'', '\'class\' : \'SimpleStrategy\'').replaceFirst('\'replication_factor\'\\s+:\\s+[0-9]+', '\'replication_factor\' : 1')
    }
}
