package org.acme.micrometer;

import java.util.concurrent.ConcurrentLinkedDeque;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.Tags;

@Path("/example")
@Produces("text/plain")
public class ExampleResource {

    static final int MAX_GAUGE_SIZE = 100;

    private final MeterRegistry registry;
    private final ConcurrentLinkedDeque<Long> list = new ConcurrentLinkedDeque<>();

    ExampleResource(MeterRegistry registry) {
        this.registry = registry;
        registry.gaugeCollectionSize("example.list.size", Tags.empty(), list);
    }

    @GET
    @Path("prime/{number}")
    public String checkIfPrime(@PathParam("number") long number) {
        if (number < 1) {
            registry.counter("example.prime.number", "type", "not-natural")
                    .increment();
            return "Only natural numbers can be prime numbers.";
        }
        if (number == 1) {
            registry.counter("example.prime.number", "type", "one")
                    .increment();
            return number + " is not prime.";
        }
        if (number == 2) {
            registry.counter("example.prime.number", "type", "prime")
                    .increment();
            return number + " is prime.";
        }
        if (number % 2 == 0) {
            registry.counter("example.prime.number", "type", "even")
                    .increment();
            return number + " is not prime.";
        }
        if (timedTestPrimeNumber(number)) {
            registry.counter("example.prime.number", "type", "prime")
                    .increment();
            return number + " is prime.";
        } else {
            registry.counter("example.prime.number", "type", "not-prime")
                    .increment();
            return number + " is not prime.";
        }
    }

    protected boolean testPrimeNumber(long number) {
        for (int i = 3; i < Math.floor(Math.sqrt(number)) + 1; i = i + 2) {
            if (number % i == 0) {
                return false;
            }
        }
        return true;
    }

    protected boolean timedTestPrimeNumber(long number) {
        Timer.Sample sample = Timer.start(registry);
        boolean result = testPrimeNumber(number);
        sample.stop(registry.timer("example.prime.number.test", "prime", result + ""));
        return result;
    }

    @GET
    @Path("gauge/{number}")
    public Long checkListSize(@PathParam("number") long number) {
        if (number < 0) {
            registry.counter("example.gauge.number", "type", "negative").increment();
            return 0L;
        }
        if (number == 2 || number % 2 == 0) {
            list.add(number);
            while (list.size() > MAX_GAUGE_SIZE) {
                list.pollFirst();
            }
            return number;
        }

        Long removed = list.pollFirst();
        return removed != null ? removed : 0L;
    }
}
