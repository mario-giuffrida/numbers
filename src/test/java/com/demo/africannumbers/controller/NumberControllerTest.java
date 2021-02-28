package com.demo.africannumbers.controller;

import java.io.IOException;
import java.net.URISyntaxException;

import com.demo.africannumbers.exceptions.InvalidStateFilterException;
import com.demo.africannumbers.model.FilePath;
import com.demo.africannumbers.model.MobileNumber;
import com.demo.africannumbers.service.NumberProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class NumberControllerTest {

  NumberProcessor processor;
  NumberController controller;

  @BeforeEach
  void setup(){
    processor = mock(NumberProcessor.class);
    controller = new NumberController(processor);
  }

  @Test
  void checkNumber_processSingleNumber() {
    //WHEN
    controller.checkNumber("number");

    //THEN
    verify(processor, times(1)).processSingleNumber(any(MobileNumber.class));
  }

  @Test
  void getNumbers_retrieveNumbersByState() throws InvalidStateFilterException {
    //WHEN
    controller.getNumbers("state");

    //THEN
    verify(processor, times(1)).retrieveNumbersByState("state");
  }

  @Test
  void loadNumbers_loadNumbers() throws InterruptedException, IOException, URISyntaxException {
    //GIVEN
    FilePath filePath = new FilePath("path");

    //WHEN
    controller.loadNumbers(filePath);

    //THEN
    verify(processor, times(1)).loadNumbers("path");
  }
}