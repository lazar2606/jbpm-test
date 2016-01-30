package demo;

import org.drools.core.spi.ProcessContext;
import org.kie.api.runtime.KieRuntime;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.KieInternalServices;
import org.kie.internal.process.CorrelationAwareProcessRuntime;
import org.kie.internal.process.CorrelationKey;
import org.kie.internal.process.CorrelationKeyFactory;

import java.util.Collection;
import java.util.HashMap;

/**
 * Create processes for each user
 */
public class CreateProcessor {
    ProcessContext kcontext;
    String newProcessName;

    public CreateProcessor(final Object kcontext, final String processName) {
        this.kcontext = (org.drools.core.spi.ProcessContext) kcontext;
        this.newProcessName = processName;
    }

    public void run() {
        final Message o = (Message) kcontext.getVariable("message");
        if (o != null) {
            System.out.println("Read message: " + o.toString());
            CorrelationKeyFactory correlationKeyFactory = KieInternalServices.Factory.get().newCorrelationKeyFactory();
            CorrelationKey key = correlationKeyFactory.newCorrelationKey("user_" + o.getIdUser());
            KieRuntime runtime = kcontext.getKnowledgeRuntime();
            CorrelationAwareProcessRuntime correlationRuntime = (CorrelationAwareProcessRuntime) runtime;
            ProcessInstance process = correlationRuntime.getProcessInstance(key);
            if (process == null) {
                System.out.println("Can't find process by key: " + key + "; Deleting facts");
                deleteFacts(o.getIdUser());
                final HashMap<String, Object> params = new HashMap<String, Object>();
                params.put("idUser", o.getIdUser());
                process = correlationRuntime.startProcess(newProcessName, key, params);
                System.out.println("Start new process: " + newProcessName + " for user: " + o.getIdUser()
                        + "; process: "  + process);

            }
            if (process != null) {
                runtime.signalEvent("Message-code_" + o.getCode(), process.getId());
                FactHandle fact = runtime.insert(process);
                runtime.insert(o);
                runtime.delete(fact);
            }

            Collection<FactHandle> facts = kcontext.getKnowledgeRuntime().getFactHandles();
            if (facts != null) for (final FactHandle fact : facts) {
                System.out.println("fact: " + fact);
            }
        }
    }

    private void deleteFacts(final Integer key) {
        Collection<FactHandle> facts = kcontext.getKnowledgeRuntime().getFactHandles();
        for (final FactHandle fact : facts) {
            Object object = kcontext.getKnowledgeRuntime().getObject(fact);
            if (object instanceof Message) {
                final Integer id = ((Message) object).getIdUser();
                if (id != null && id.equals(key)) {
                    kcontext.getKnowledgeRuntime().delete(fact);
                    System.out.println("Delete fact from WorkingMemory: " + fact);
                }
            } else if (object instanceof ProcessInstance
                    && ((ProcessInstance) object).getState() != ProcessInstance.STATE_ACTIVE) {
                kcontext.getKnowledgeRuntime().delete(fact);
                System.out.println("Delete process from WorkingMemory: " + fact);
            }
        }
    }

}
