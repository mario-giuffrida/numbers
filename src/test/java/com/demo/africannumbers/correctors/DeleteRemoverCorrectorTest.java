package com.demo.africannumbers.correctors;

import com.demo.africannumbers.model.MobileNumber;
import com.demo.africannumbers.service.NumberProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class DeleteRemoverCorrectorTest {

  DeleteRemoverCorrector corrector;
  NumberProcessor processor = mock(NumberProcessor.class);

  @BeforeEach
  void setup(){
    corrector = new DeleteRemoverCorrector(processor);
  }

  @Test
  void correct_whenNumberEndsWithDeleted_removeDelete() {
    //GIVEN
    MobileNumber number = new MobileNumber("idFromFile", "1234_DELETED_4oieutpoiwoslfkjsi");

    //WHEN
    boolean result = corrector.correct(number);

    //THEN
    assertTrue(result);
    assertEquals("1234", number.getNumber());
  }

  @Test
  void correct_whenNumberDoesNotEndWithDeleted_doNothing() {
    //GIVEN
    MobileNumber number = new MobileNumber("idFromFile", "1234");

    //WHEN
    boolean result = corrector.correct(number);

    //THEN
    assertFalse(result);
    assertEquals("1234", number.getNumber());
  }
}