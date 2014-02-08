package net.x11.patch;

import java.io.*;
import java.lang.instrument.Instrumentation;
import java.util.Properties;


/**
 * Created with IntelliJ IDEA.
 * User: mzheludkov
 * -javaagent:target/LinuxJavaFixes-1.0.0-SNAPSHOT.jar
 */
public class LinuxJavaPatchAgent {

    public static void premain(String agentArgument, Instrumentation instrumentation) {
        if(agentArgument==null || !agentArgument.startsWith(SWTEventTableTransformer.SWT)) {
            instrumentation.addTransformer(new XKeysymTransformer(agentArgument));
        } else if(agentArgument.startsWith(SWTEventTableTransformer.SWT)){
            instrumentation.addTransformer(new SWTEventTableTransformer(agentArgument));
        }
    }

    public static Properties getProperties(String defaultProperties, String propertiesFile) throws IOException {
        Properties props = null;
        if(propertiesFile !=null && !propertiesFile.equals("")) {
            File mapping = new File(propertiesFile);
            if(mapping.exists()) {
                props = new Properties();
                System.out.println("LinuxJavaPatchAgent.loaded properties from "+mapping);
                props.load(new FileInputStream(mapping));

            }
        }
        if(props==null) {
            props = new Properties();
            System.out.println("LinuxJavaPatchAgent.loaded properties from classpath! "+defaultProperties);
            props.load(LinuxJavaPatchAgent.class.getClassLoader().getResourceAsStream(defaultProperties));
        }
        return props;
    }


}
