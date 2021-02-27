package com.demo.africannumbers.verifiers;

import com.demo.africannumbers.model.MobileNumber;
import com.demo.africannumbers.service.NumberProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LenghtVerifier implements Verifier {

  private static final Logger LOGGER = LoggerFactory.getLogger(LenghtVerifier.class);
  private static final int SIZE = 11;

  @Autowired
  public LenghtVerifier(NumberProcessor processor) {
    processor.registerVerifier(this);
  }

  @Override
  public boolean valid(MobileNumber number) {
    boolean result = (number.getNumber().length() == SIZE);
    LOGGER.debug("number {}: {}", number.getNumber(), result);
    return result;
  }
}
