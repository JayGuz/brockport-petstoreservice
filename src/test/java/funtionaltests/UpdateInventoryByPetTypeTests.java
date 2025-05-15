package funtionaltests;

import com.petstore.AnimalType;
import com.petstore.PetEntity;
import com.petstore.animals.DogEntity;
import com.petstore.animals.attributes.*;
import io.restassured.RestAssured;
import io.restassured.http.*;
import io.restassured.parsing.Parser;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.util.List;

import static io.restassured.RestAssured.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Functional test for updating an existing pet entity.
 */
public class UpdateInventoryByPetTypeTests {

    private static Headers headers;

    @BeforeEach
    public void setup() {
        RestAssured.baseURI = "http://localhost:8080/";
        headers = new Headers(
                new Header("Content-Type", ContentType.JSON.toString()),
                new Header("Accept", ContentType.JSON.toString()));
    }

    @Test
    @DisplayName("Update Dog PetEntity with ID 1")
    public void updateDogEntityTest() {
        RestAssured.registerParser("application/json", Parser.JSON);

        // Arrange – build the update request body
        DogEntity updatedDog = new DogEntity(
                AnimalType.DOMESTIC,
                Skin.FUR,
                Gender.FEMALE,
                Breed.GERMAN_SHEPHERD,
                new BigDecimal("123.45"),
                1 // petId to update
        );

        // Act – send PUT request to update the entity
        PetEntity responseEntity =
                given()
                        .headers(headers)
                        .body(updatedDog)
                        .when()
                        .put("inventory/petType/DOG/petId/1")
                        .then()
                        .log().all()
                        .statusCode(200)
                        .extract().as(PetEntity.class);

        // Assert – validate that the returned entity matches what we updated
        assertNotNull(responseEntity);
        assertEquals(1, responseEntity.getPetId(), "Pet ID should match the updated value");
        assertEquals(PetType.DOG, responseEntity.getPetType(), "PetType should be DOG");
        assertEquals(Gender.FEMALE, responseEntity.getGender(), "Gender should be updated to FEMALE");
        assertEquals(Breed.GERMAN_SHEPHERD, responseEntity.getBreed(), "Breed should be GERMAN_SHEPHERD");
        assertEquals(0, responseEntity.getCost().compareTo(new BigDecimal("123.45")), "Cost should be updated");
    }

    @Test
    @DisplayName("Update with Invalid PetType should return 400")
    public void updateInvalidPetTypeTest() {
        RestAssured.registerParser("application/json", Parser.JSON);

        DogEntity updatedDog = new DogEntity(
                AnimalType.DOMESTIC,
                Skin.FUR,
                Gender.MALE,
                Breed.POODLE,
                new BigDecimal("100.00"),
                99
        );

        BadRequestResponseBody body =
                given()
                        .headers(headers)
                        .body(updatedDog)
                        .when()
                        .put("inventory/petType/FROGGER/petId/99")
                        .then()
                        .log().all()
                        .statusCode(400)
                        .extract()
                        .as(BadRequestResponseBody.class);

        assertEquals("Bad Request", body.getError());
        assertTrue(body.getMessage().contains("Failed to convert"));
    }
}
