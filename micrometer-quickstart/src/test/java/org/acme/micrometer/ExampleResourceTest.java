package org.acme.micrometer;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

@QuarkusTest
class ExampleResourceTest {

    @Test
    void shouldTreatTwoAsPrime() {
        given()
                .when().get("/example/prime/2")
                .then()
                .statusCode(200)
                .body(is("2 is prime."));
    }

    @Test
    void shouldReturnZeroWhenGaugeListIsEmpty() {
        given()
                .when().get("/example/gauge/3")
                .then()
                .statusCode(200)
                .body(is("0"));
    }
}
