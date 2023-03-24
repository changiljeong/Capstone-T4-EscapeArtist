import static org.junit.jupiter.api.Assertions.*;

import com.escapeartist.controllers.GameController;
import com.escapeartist.models.GameDialogue;
import com.escapeartist.models.Trivia;
import com.escapeartist.util.GsonDeserializer;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GameControllerTest {

  private GameDialogue gameDialogue;
  private GsonDeserializer deserializer;
  private JsonObject gameData;
  private List<Trivia> trivias;

  @BeforeEach
  void setUp(){
    deserializer = new GsonDeserializer();
    gameDialogue = deserializer.deserializeGameDialogue();
    trivias = deserializer.deserializeTrivia();
    gameData = new JsonObject();
    gameData.add("trivia", new Gson().toJsonTree(trivias));
  }

  @Test
  public void finalIncorrectTriviaAnswerTest() {
    String thirdIncorrectAnswer = "You're all out of guesses!. Better luck next time!";
    assertEquals(thirdIncorrectAnswer, gameDialogue.getFinalIncorrectAnswer());
  }

  @Test
  public void playerGaveIncorrectAnswerTest(){
    String playerGaveIncorrectAnswer = "Incorrect answer. Please try again";
    assertEquals(playerGaveIncorrectAnswer, gameDialogue.getPlayerGaveIncorrectAnswer());
  }

  @Test
  public void triviaJsonArrayTest(){
    JsonArray triviaJsonArray = gameData.getAsJsonArray("trivia");
    assertNotNull(triviaJsonArray);
    assertTrue(triviaJsonArray.isJsonArray());
    assertFalse(triviaJsonArray.isJsonObject());
    assertFalse(triviaJsonArray.toString().isEmpty());
  }

}