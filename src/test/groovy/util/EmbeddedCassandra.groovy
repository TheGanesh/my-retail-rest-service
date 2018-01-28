package util

import groovy.transform.CompileStatic
import org.apache.cassandra.config.DatabaseDescriptor
import org.apache.cassandra.db.commitlog.CommitLog
import org.apache.cassandra.io.util.FileUtils
import org.apache.cassandra.service.CassandraDaemon
import org.apache.cassandra.service.StorageServiceMBean
import org.apache.commons.io.IOUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.management.JMX
import javax.management.MBeanServer
import javax.management.ObjectName
import java.lang.management.ManagementFactory
import java.nio.charset.Charset

@CompileStatic
class EmbeddedCassandra {

    private static final Logger LOG = LoggerFactory.getLogger(EmbeddedCassandra)

    private static final int PORT = 9042

    private CassandraDaemon cassandraDaemon

    private static final String YAML_FILE = '/cassandra.yaml'

    private static final String CASSANDRA_DIR = 'build/cassandra'

    synchronized void start() {

        LOG.info('starting embedded cassandra')

        // Spring Boot is calling main twice, need to check port to insure we don't try to start cassandra when it is already running.
        if (isRunning()) {
            LOG.info('cassandra already started and configured.')
            return
        }

        setup()
        cassandraDaemon.start()
        ConfigureCassandra.makeCcdKeyspace()
    }
    void stop() {
        cassandraDaemon.stop()
    }

    boolean isRunning() {
        try {
            new Socket("localhost", PORT)
            LOG.info("isRunning() - cassandra already running (port " + PORT + " already in use).")
            return true
        } catch (final UnknownHostException e) {
            throw new RuntimeException(e)
        } catch (final IOException e) {
            LOG.info("isRunning() - cassandra not running (port " + PORT + " not in use).")
            return false
        }
    }

    void setup() throws IOException, InterruptedException {

        // only for 'tmp' do we start over, if it is something else what exists remains....
        if (CASSANDRA_DIR == 'tmp') {
            rmdir(CASSANDRA_DIR)
            mkdir(CASSANDRA_DIR)
        }

        System.setProperty('cassandra.config', 'file:' + CASSANDRA_DIR + '/' + YAML_FILE.substring(YAML_FILE.lastIndexOf('/') + 1))
        System.setProperty('cassandra-foreground', 'true')

        File cassandraDirectory = new File(CASSANDRA_DIR)

        if (!cassandraDirectory.exists() && cassandraDirectory.isDirectory()) {
            throw new RuntimeException("${CASSANDRA_DIR} exists and is not a directory.")
        }

        if (!cassandraDirectory.exists()) {
            mkdir(CASSANDRA_DIR)
        }

        File yamlFile = new File(CASSANDRA_DIR + YAML_FILE)

        if (!yamlFile.exists()) {
            copy(YAML_FILE, CASSANDRA_DIR)
            makeDirectories()
        }

        cassandraDaemon = new CassandraDaemon()
        cassandraDaemon.activate()

        Runtime.getRuntime().addShutdownHook(new Thread() {
            void run() {
                LOG.info('JVN shutting down, flushing column families and shutting down cassandra daemon.')
                flush()
                cassandraDaemon.stop()
            }
        })
    }

    private static void rmdir(String dir) throws IOException {
        final File dirFile = new File(dir)
        if (dirFile.exists()) {
            FileUtils.deleteRecursive(dirFile)
        }
    }

    private static void copy(String resource, String directory) throws IOException {

        final InputStream inputStream = EmbeddedCassandra.getResourceAsStream(resource)

        String yaml = IOUtils.toString(inputStream, Charset.defaultCharset())

        yaml = yaml.replaceAll('\\{CASSANDRA_DIR}', CASSANDRA_DIR)

        final String fileName = resource.substring(resource.lastIndexOf('/') + 1)
        final File file = new File(directory + System.getProperty('file.separator') + fileName)
        final FileOutputStream outputStream = new FileOutputStream(file)
        IOUtils.write(yaml, outputStream, Charset.defaultCharset())
    }

    private static void makeDirectories() throws IOException {
        DatabaseDescriptor.createAllDirectories()
        CommitLog.instance.resetUnsafe(true)
    }

    private static void mkdir(String dir) throws IOException {
        FileUtils.createDirectory(dir)
    }

    private void flush() {
        try {
            final MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer()
            final ObjectName storageServiceObjectName = new ObjectName('org.apache.cassandra.db:type=StorageService')
            final StorageServiceMBean storageServiceMBean = JMX.newMBeanProxy(mBeanServer, storageServiceObjectName, StorageServiceMBean)
            for (final String keyspace : storageServiceMBean.getKeyspaces()) {
                storageServiceMBean.forceKeyspaceFlush(keyspace, new String[0])
            }
        } catch (final Exception e) {
            println 'unable to flush cassandra on shutdown.'
        }
    }
}
