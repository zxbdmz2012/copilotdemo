package com.github.copilotdemo.common;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RPCResponseTest {

    @Test
    void testRPCResponse() {
        RPCResponse response = RPCResponse.success("TestSuccess");
        assertEquals("TestSuccess", response.getResult());
        assertTrue(response.isSuccess());

        response = RPCResponse.fail();
        assertNull(response.getResult());
        assertFalse(response.isSuccess());
    }
}