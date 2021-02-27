package com.demo.africannumbers.service;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.demo.africannumbers.correctors.Corrector;
import com.demo.africannumbers.correctors.DeleteRemoverCorrector;
import com.demo.africannumbers.exceptions.InvalidStateFilterException;
import com.demo.africannumbers.model.MobileNumber;
import com.demo.africannumbers.model.MobileNumberState;
import com.demo.africannumbers.persistence.MobileNumberRepository;
import com.demo.africannumbers.verifiers.LenghtVerifier;
import com.demo.africannumbers.verifiers.PrefixVerifier;
import com.demo.africannumbers.verifiers.Verifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class NumberProcessorTest {

  MobileNumberRepository repository;
  NumberProcessor processor;
  Verifier verifier;
  Corrector corrector;

  @BeforeEach
  void setup(){
    repository = mock(MobileNumberRepository.class);
    processor = new NumberProcessor(repository);
    verifier = mock(Verifier.class);
    processor.registerVerifier(verifier);
    corrector = mock(Corrector.class);
    processor.registerCorrector(corrector);
  }

  @Test
  void loadNumbers_whenFromFile_countIsCorrect() throws IOException, URISyntaxException, InterruptedException,
      ExecutionException {
    //GIVEN
    when(verifier.valid(any())).thenReturn(true);

    //WHEN
    processor.loadNumbersAndWaitForCompletion("South_African_Mobile_Numbers.csv");

    //THEN
    verify(verifier, times(1000)).valid(any());
    verify(repository, times(1000)).save(any());
  }

  @Test
  void loadNumbers_whenReader_countIsCorrect() throws IOException, URISyntaxException, InterruptedException, ExecutionException {
    //GIVEN
    when(verifier.valid(any())).thenReturn(true);
    String numbers = "id,sms_phone\n" +
        "103343262,6478342944\n" +
        "103327417,260955751013";
    Reader targetReader = new StringReader(numbers);

    //WHEN
    processor.loadNumbersAndWaitForCompletion(targetReader);

    //THEN
    verify(verifier, times(2)).valid(any());
    verify(repository, times(2)).save(any());
  }

  @Test
  void processSingleNumber_whenNumberValid_returnNumber() {
    //GIVEN
    MobileNumber number = new MobileNumber("importedId", "number");
    when(verifier.valid(any())).thenReturn(true);

    //WHEN
    MobileNumber result = processor.processSingleNumber(number);

    //THEN
    verify(verifier, times(1)).valid(any());
    verify(corrector, times(0)).correct(any());
    assertEquals(MobileNumberState.ACCEPTABLE, result.getState());
  }

  @Test
  void processSingleNumber_whenCorrectable_returnCorrectedNumber() {
    //GIVEN
    MobileNumber correctableNumber = new MobileNumber("correctable", "number");
    when(verifier.valid(correctableNumber)).thenReturn(false, true);
    when(corrector.correct(correctableNumber)).thenReturn(true);

    //WHEN
    MobileNumber result = processor.processSingleNumber(correctableNumber);

    //THEN
    verify(verifier, times(2)).valid(correctableNumber);
    verify(corrector, times(1)).correct(correctableNumber);
    assertEquals(MobileNumberState.CORRECTED, result.getState());
  }

  @Test
  void processSingleNumber_whenNotCorrectableCorrectorSucceeds_returnIncorrectNumber() {
    //GIVEN
    MobileNumber correctableNumber = new MobileNumber("incorrect", "number");
    when(verifier.valid(correctableNumber)).thenReturn(false, false);
    when(corrector.correct(correctableNumber)).thenReturn(true);

    //WHEN
    MobileNumber result = processor.processSingleNumber(correctableNumber);

    //THEN
    verify(verifier, times(2)).valid(correctableNumber);
    verify(corrector, times(1)).correct(correctableNumber);
    assertEquals(MobileNumberState.INCORRECT, result.getState());
  }

  @Test
  void processSingleNumber_whenNotCorrectableCorrectorFails_returnIncorrectNumber() {
    //GIVEN
    MobileNumber correctableNumber = new MobileNumber("incorrect", "number");
    when(verifier.valid(correctableNumber)).thenReturn(false, false);
    when(corrector.correct(correctableNumber)).thenReturn(false);

    //WHEN
    MobileNumber result = processor.processSingleNumber(correctableNumber);

    //THEN
    verify(verifier, times(1)).valid(correctableNumber);
    verify(corrector, times(1)).correct(correctableNumber);
    assertEquals(MobileNumberState.INCORRECT, result.getState());
  }

  @Test
  void retrieveNumbersByState_whenStateIsCorrect_listIsReturned() throws IOException, URISyntaxException, InterruptedException, ExecutionException, InvalidStateFilterException {
    //WHEN
    processor.retrieveNumbersByState("INCORRECT");

    //THEN
    verify(repository, times(1)).findByState(MobileNumberState.INCORRECT);
  }


  @Test
  void retrieveNumbersByState_whenStateIsIncorrect_exceptionIsThrown() throws IOException, URISyntaxException, InterruptedException, ExecutionException, InvalidStateFilterException {
    //THEN
    assertThrows(InvalidStateFilterException.class, () -> processor.retrieveNumbersByState("it-does-not-exist"));
  }
}