package com.github.copilotdemo.common;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RPCRequestTest {

    @Test
    void testRPCRequest() {
        RPCRequest request = new RPCRequest();
        request.setInterfaceName("TestInterface");
        request.setMethodName("TestMethod");
        request.setParams(new Object[]{1, "test"});
        request.setParamsTypes(new Class[]{int.class, String.class});

        assertEquals("TestInterface", request.getInterfaceName());
        assertEquals("TestMethod", request.getMethodName());
        assertArrayEquals(new Object[]{1, "test"}, request.getParams());
        assertArrayEquals(new Class[]{int.class, String.class}, request.getParamsTypes());
    }
}