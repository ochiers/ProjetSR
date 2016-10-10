package jvn;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import jvn.Mode.ModeType;

public class MyProxy implements InvocationHandler {

    private JvnObject obj;

    public static Object newInstance(JvnLocalServer js, String name, Class<?> c) {
	JvnObject jo = null;
	try {
	    jo = js.jvnLookupObject(name);

	    if (jo == null) {
		jo = js.jvnCreateObject((Serializable) c.newInstance());
		// after creation, I have a write lock on the object

		jo.jvnUnLock();
		js.jvnRegisterObject(name, jo);
	    }

	} catch (JvnException e) {
	    e.printStackTrace();
	} catch (InstantiationException e) {
	    e.printStackTrace();
	} catch (IllegalAccessException e) {
	    e.printStackTrace();
	}

	return java.lang.reflect.Proxy.newProxyInstance(c.getClassLoader(), c.getInterfaces(), new MyProxy(jo));
    }

    private MyProxy(JvnObject obj) {
	this.obj = obj;
    }

    public Object invoke(Object proxy, Method m, Object[] args) throws Throwable {
	Object result;
	try {
	    if (m.isAnnotationPresent(Mode.class)) {
		if (m.getAnnotation(Mode.class).mode() == ModeType.Read) {
		    // lockRead()
		    obj.jvnLockRead();
		} else if (m.getAnnotation(Mode.class).mode() == ModeType.Write) {
		    // lockWrite
		    obj.jvnLockWrite();
		}
	    } else {
		throw new PasDAnnotation();
	    }

	    result = m.invoke(obj.getTheObject(), args);

	    // unlock
	    obj.jvnUnLock();
	} catch (InvocationTargetException e) {
	    throw e.getTargetException();
	} catch (Exception e) {
	    throw new RuntimeException("unexpected invocation exception: " + e.getMessage());
	}
	return result;
    }

}
