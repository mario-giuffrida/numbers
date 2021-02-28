package com.demo.africannumbers.verifiers;

import com.demo.africannumbers.model.MobileNumber;
import com.demo.africannumbers.service.NumberProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class LengthVerifierTest {

  LengthVerifier verifier;
  NumberProcessor processor = mock(NumberProcessor.class);

  @BeforeEach
  void setup(){
    verifier = new LengthVerifier(processor);
  }

  @Test
  void valid_whenNumberHasCorrectLength_returnTrue() {
    //GIVEN
    MobileNumber number = new MobileNumber("idFromFile", "12345678901");

    //WHEN
    boolean result = verifier.valid(number);

    //THEN
    assertTrue(result);
  }

  @Test
  void valid_whenNumberHasIncorrectLength_returnFalse() {
    //GIVEN
    MobileNumber number = new MobileNumber("idFromFile", "12345678901abcd");

    //WHEN
    boolean result = verifier.valid(number);

    //THEN
    assertFalse(result);
  }

}