package entities;

import exceptions.PlayerNotFoundException;
import org.junit.jupiter.api.Assertions;
import usecases.Response;

public class ResponseTest {

    @org.junit.Test
    public void testResultCodeIsCorrectForGivenException(){
        String m = "My super message";
        Response response = Response.fromException(new PlayerNotFoundException(m), m);
        Assertions.assertEquals(response.getCode(), Response.ResCode.PLAYER_NOT_FOUND);
        Assertions.assertEquals(response.getMessage(), m);
    }

}
