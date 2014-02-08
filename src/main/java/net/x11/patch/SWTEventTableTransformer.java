package net.x11.patch;

import javassist.*;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.ProtectionDomain;
import java.util.Properties;

/**
 * Created by mikl on 08.02.14.
 */
public class SWTEventTableTransformer implements ClassFileTransformer {
    public static final String EVENT_TABLE_CLASS = "org/eclipse/swt/widgets/EventTable";
    public static final String PATCH_KEY_MAPPING_PROPERTIES = "swt.key.mapping.properties";
    public static final String PRINT = "print";
    public static final String SWT = "swt";
    private String agentArgument;

    public SWTEventTableTransformer(String agentArgument) {
        this.agentArgument = agentArgument==null||agentArgument.equalsIgnoreCase(SWT) ? null : agentArgument.substring(4);
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        try {
            return className.equals(EVENT_TABLE_CLASS) ? doClass(loader, className, classBeingRedefined, classfileBuffer) : classfileBuffer;
        } catch (Throwable err) {
            err.printStackTrace();
            return classfileBuffer;
        }
    }

    private byte[] doClass(ClassLoader classLoader, String name, Class clazz, byte[] b) throws ClassNotFoundException, NotFoundException {
        CtClass cl = null;
        ClassPool pool = ClassPool.getDefault();
        pool.appendClassPath(new LoaderClassPath(classLoader));
        try {
            cl = pool.makeClass(new java.io.ByteArrayInputStream(b));
            CtMethod method = cl.getDeclaredMethod("sendEvent");
            if(agentArgument!=null && agentArgument.equals(PRINT)) {
                method.insertBefore("if($1.type==1) {System.out.println(\"keyCode=\"+$1.keyCode);};" );
            } else {
                Properties props = LinuxJavaPatchAgent.getProperties(PATCH_KEY_MAPPING_PROPERTIES, agentArgument);
                for(Object key: props.keySet()) {
                    String value = props.getProperty((String) key);
                    method.insertBefore("if(($1.type==1) && $1.keyCode==" + key + " && ((event.stateMask & org.eclipse.swt.SWT.MOD1) !=0 || (event.stateMask & org.eclipse.swt.SWT.MOD2) !=0 )) { $1.keyCode=" + value + ";}");
                }
            }
            b = cl.toBytecode();
        } catch (Exception e) {
            e.printStackTrace(System.out);
            System.out.println("LinuxJavaPatchAgent.Could not instrument  " + name + ",  exception : " + e.getMessage());
        } finally {
            if (cl != null) {
                cl.detach();
            }
        }
        return b;
    }

}
