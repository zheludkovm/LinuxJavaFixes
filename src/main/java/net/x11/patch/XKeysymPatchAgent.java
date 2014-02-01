package net.x11.patch;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.Properties;


/**
 * Created with IntelliJ IDEA.
 * User: mzheludkov
 * -javaagent:target/LinuxJavaFixes-1.0.0-SNAPSHOT.jar
 */
public class XKeysymPatchAgent implements ClassFileTransformer {

    public static final String XNET_PROTOCOL = "sun/awt/X11/XKeysym";
    public static final String PATCH_KEY_MAPPING_PROPERTIES = "patch.key.mapping.properties";
    public static final String PRINT = "print";
    private static String agentArgument;


    public static void premain(String agentArgument, Instrumentation instrumentation) {
        XKeysymPatchAgent.agentArgument = agentArgument;
        instrumentation.addTransformer(new XKeysymPatchAgent());
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        try {
            return className.equals(XNET_PROTOCOL) ? doClass(className, classBeingRedefined, classfileBuffer) : classfileBuffer;
        } catch (Throwable err) {
            err.printStackTrace();
            return classfileBuffer;
        }
    }

    private byte[] doClass(String name, Class clazz, byte[] b) {
        CtClass cl = null;
        ClassPool pool = ClassPool.getDefault();
        try {
            cl = pool.makeClass(new java.io.ByteArrayInputStream(b));
            if(agentArgument!=null && agentArgument.equals(PRINT)) {
                CtMethod method = cl.getDeclaredMethod("getJavaKeycode");
                method.insertBefore("System.out.println(\"XKeysymPatchAgent.keysym=\"+Long.toHexString($1));");
            } else {
                Properties props = getProperties();
                for(Object key: props.keySet()) {
                    String value = props.getProperty((String) key);
                    cl.getClassInitializer().insertAfter("keysym2JavaKeycodeHash.put( Long.valueOf(0x"+key+"l), new sun.awt.X11.XKeysym$Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_"+value+", java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));");
                }
            }
            b = cl.toBytecode();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("XKeysymPatchAgent.Could not instrument  " + name + ",  exception : " + e.getMessage());
        } finally {
            if (cl != null) {
                cl.detach();
            }
        }
        return b;
    }

    private Properties getProperties() throws IOException {
        Properties props = null;
        if(agentArgument!=null && !agentArgument.equals("")) {
            File mapping = new File(agentArgument);
            if(mapping.exists()) {
                props = new Properties();
                System.out.println("XKeysymPatchAgent.loaded properties from "+mapping);
                props.load(new FileInputStream(mapping));

            }
        }
        if(props==null) {
            props = new Properties();
            System.out.println("XKeysymPatchAgent.loaded properties from classpath");
            props.load(XKeysymPatchAgent.class.getClassLoader().getResourceAsStream(PATCH_KEY_MAPPING_PROPERTIES));
        }
        return props;
    }
}
