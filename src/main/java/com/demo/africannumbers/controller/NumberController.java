package com.demo.africannumbers.controller;

import javax.validation.Valid;
import java.io.IOException;
import java.net.URISyntaxException;

import com.demo.africannumbers.model.FilePath;
import com.demo.africannumbers.model.MobileNumber;
import com.demo.africannumbers.service.NumberProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NumberController {

  private final NumberProcessor numberProcessor;

  @Autowired
  public NumberController(NumberProcessor numberProcessor) {
    this.numberProcessor = numberProcessor;
  }

  @GetMapping("/number/{number}")
  public ResponseEntity<?> checkNumber(@PathVariable("number") String number) {
    try {
      MobileNumber mobile = new MobileNumber(number);
      numberProcessor.processSingleNumber(mobile);
      return ResponseEntity.ok(
          String.format("Number %s, status %s, corrections %s",
              mobile.getNumber(),
              mobile.getState(),
              mobile.getCorrections()));
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  @GetMapping("/numbers/{parameter}")
  public ResponseEntity<?> getNumbers(@PathVariable("parameter") String parameter) {
    try {
      return ResponseEntity.ok(numberProcessor.retrieveNumbersByState(parameter));
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }


  @PostMapping("/numbers")
  public ResponseEntity<?> loadNumbers(@Valid @RequestBody FilePath filePath) {
    try {
      numberProcessor.loadNumbers(filePath.getPath());
      return ResponseEntity.ok("numbers loading started");
    } catch (URISyntaxException | IOException | InterruptedException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }
}
