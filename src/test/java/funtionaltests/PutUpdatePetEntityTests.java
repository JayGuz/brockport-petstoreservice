package funtionaltests;


import com.petstore.PetEntity;
import com.petstore.PetStoreReader;
import com.petstoreservices.exceptions.PetDataStoreException;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

    public class PutUpdatePetEntityTests {

        private static Headers headers;
        private List<PetEntity> expectedResults;

        @BeforeEach
        public void setUp() throws PetDataStoreException {
            RestAssured.baseURI = "http://localhost:8080/";
            Header contentType = new Header("Content-Type", ContentType.JSON.toString());
            Header accept = new Header("Accept", ContentType.JSON.toString());
            headers = new Headers(contentType, accept);

            PetStoreReader reader = new PetStoreReader();
            expectedResults = reader.readJsonFromFile();
        }

        @Test
        @DisplayName("Update Existing Pet Price")
        public void updatePetPriceTest() throws Exception {
            PetEntity petToUpdate = expectedResults.get(0);
            BigDecimal newCost = petToUpdate.getCost().add(new BigDecimal("50.00"));
            petToUpdate.setCost(newCost);

            PetEntity updatedPet =
                    given()
                            .headers(headers)
                            .body(petToUpdate)
                            .when()
                            .put("inventory/update")
                            .then()
                            .log().all()
                            .assertThat().statusCode(200)
                            .assertThat().contentType("application/json")
                            .extract()
                            .jsonPath().getObject(".", PetEntity.class);

            assertEquals(petToUpdate.getPetId(), updatedPet.getPetId(), "Pet ID should remain the same after update");
            assertEquals(newCost, updatedPet.getCost(), "Updated price should be reflected");
        }
    }


