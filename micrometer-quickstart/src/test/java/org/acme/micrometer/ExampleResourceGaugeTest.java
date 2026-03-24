package org.acme.micrometer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;

class ExampleResourceGaugeTest {

    @Test
    void shouldCapGaugeBacklogToAvoidUnboundedGrowth() {
        ExampleResource resource = new ExampleResource(new SimpleMeterRegistry());

        for (int number = 2; number <= 202; number += 2) {
            resource.checkListSize(number);
        }

        for (int index = 0; index < ExampleResource.MAX_GAUGE_SIZE; index++) {
            long expected = 4L + (index * 2L);
            assertEquals(expected, resource.checkListSize(3));
        }

        assertEquals(0L, resource.checkListSize(3));
    }

    @Test
    void shouldIgnoreNegativeGaugeInputs() {
        ExampleResource resource = new ExampleResource(new SimpleMeterRegistry());

        assertEquals(0L, resource.checkListSize(-2));
        assertEquals(0L, resource.checkListSize(3));
    }

}
