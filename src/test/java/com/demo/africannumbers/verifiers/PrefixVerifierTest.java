package com.demo.africannumbers.verifiers;

import com.demo.africannumbers.model.MobileNumber;
import com.demo.africannumbers.service.NumberProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class PrefixVerifierTest {

  PrefixVerifier verifier;
  NumberProcessor processor = mock(NumberProcessor.class);

  @BeforeEach
  void setup(){
    verifier = new PrefixVerifier(processor);
  }

  @Test
  void valid_whenNumberHasCorrectPrefix_returnTrue() {
    //GIVEN
    MobileNumber number = new MobileNumber("idFromFile", "2783-5678901");

    //WHEN
    boolean result = verifier.valid(number);

    //THEN
    assertTrue(result);
  }

  @Test
  void valid_whenNumberHasIncorrectPrefix_returnFalse() {
    //GIVEN
    MobileNumber number = new MobileNumber("idFromFile", "incorrect-5678901");

    //WHEN
    boolean result = verifier.valid(number);

    //THEN
    assertFalse(result);
  }

}