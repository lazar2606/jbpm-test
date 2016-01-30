package demo;

import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.internal.command.CommandFactory;
import org.kie.remote.client.api.RemoteRuntimeEngineFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class Sender {
    private static final Logger logger = LoggerFactory.getLogger(Sender.class);

    public static void main(String[] args) {
        try {
            final int timeout = 10000;
            final URL url = new URL("http://localhost:8080/jbpm-console/");
            final URL url2 = new URL("http://localhost:8230/jbpm-console/");
            final String deploymentId = "demo:unique:1.0";
            final String createProcessName = "unique.CreateUserProcess";
            final String userName = "krisv";
            final String password = "krisv";

            KieSession ksession1 = getKieSession(timeout, url, deploymentId, userName, password);
            KieSession ksession2 = getKieSession(timeout, url2, deploymentId, userName, password);

            Message mess1 = buildMessage(3, 1);
            sendMessage(createProcessName, ksession1, mess1);
            logger.debug("Send: " + mess1.toString());
            Thread.sleep(5000);
            Message mess2 = buildMessage(3, 2);
            sendMessage(createProcessName, ksession2, mess2);
            logger.debug("Send: " + mess2.toString());
            //sendMessage(createProcessName, ksession1, buildMessage(1, 2));
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    private static void sendMessage(String createProcessName, KieSession ksession, Message message) {
        final Map<String, Object> data = new HashMap<String, Object>();
        data.put("message", message);
        ksession.startProcess(createProcessName, data);
    }

    private static Message buildMessage(final int idUser, final int code) {
        final Message mess = new Message();
        mess.setIdUser(idUser);
        mess.setCode(code);
        return mess;
    }

    private static KieSession getKieSession(int timeout, URL url, String deploymentId, String userName, String password) {
        RuntimeEngine engine = RemoteRuntimeEngineFactory.newRestBuilder()
                .addUrl(url)
                .addTimeout(timeout)
                .addDeploymentId(deploymentId)
                .addUserName(userName)
                .addPassword(password)
                .build();
        return engine.getKieSession();
    }
}
