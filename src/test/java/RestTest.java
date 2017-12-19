import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static io.restassured.config.SSLConfig.sslConfig;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.hamcrest.core.IsEqual.equalTo;

public class RestTest {
    private final static String BASE_URL = "http://swapi.co/api";

    @Before
    public void setUp() {
        given().config(RestAssured.config().sslConfig(sslConfig().relaxedHTTPSValidation("TLS"))).then().statusCode(200);
    }

    //https://swapi.co/api/people проверить, что существует персонаж “Luke Skywalker” c home planet Tatooine
    @Test
    public void canGetLukeFromPlanet() {
        RequestSpecification httpRequest = RestAssured.given();
        Response response = httpRequest.get(BASE_URL + "/people/1/");
        response.then()
                .assertThat()
                .body("name", equalTo("Luke Skywalker"));
        JsonPath jsonPathEvaluator = response.jsonPath();
        String linkPlanet = jsonPathEvaluator.get("homeworld");
        // System.out.println("HisPlanet " + jsonPathEvaluator.get("homeworld"));
        when().get(linkPlanet)
                .then()
                .assertThat()
                .body("name", equalTo("Tatooine"));
    }


    //Проверить что количество персонажей соответствует 87
    @Test
    public void canGetTotalPeople() {
        when().get(BASE_URL + "/people/")
                .then()
                .assertThat()
                .body("count", equalTo(87));
    }

    //Проверить что первые 3 персонажа это Luke Skywalker, "C-3PO и R2-D2"
    @Test
    public void canGetFirstThreePerson() {
        when().get("http://swapi.co/api/people")
                .then()
                .assertThat()
                .body("results.name", hasItems("Luke Skywalker", "C-3PO", "R2-D2"));
    }

}

