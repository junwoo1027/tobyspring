package springbook.reflection;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import static org.junit.jupiter.api.Assertions.*;

class ReflectionTest {

    @Test
    void invokeMethod() throws Exception {
        String name = "Spring";

        assertEquals(name.length(), 6);

        Method lengthMethod = String.class.getMethod("length");
        assertEquals((Integer)lengthMethod.invoke(name), 6);

        assertEquals(name.charAt(0), 'S');

        Method charAtMethod = String.class.getMethod("charAt", int.class);
        assertEquals((Character)charAtMethod.invoke(name, 0), 'S');
    }

    @Test
    void simpleProxy() {
        Hello proxyHello = (Hello) Proxy.newProxyInstance(
                getClass().getClassLoader(),
                new Class[] { Hello.class },
                new UpperCaseHandler(new HelloTarget()));

        assertEquals(proxyHello.sayHello("jun"), "HELLO JUN");
        assertEquals(proxyHello.sayHi("jun"), "HI JUN");
        assertEquals(proxyHello.sayThankYou("jun"), "THANK YOU JUN");
    }
}