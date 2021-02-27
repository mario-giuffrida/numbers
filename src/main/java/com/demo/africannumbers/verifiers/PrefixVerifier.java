package com.demo.africannumbers.verifiers;

import com.demo.africannumbers.model.MobileNumber;
import com.demo.africannumbers.service.NumberProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PrefixVerifier implements Verifier {

  private static final Logger LOGGER = LoggerFactory.getLogger(PrefixVerifier.class);
  private static final String PREFIX = "2783";

  @Autowired
  public PrefixVerifier(NumberProcessor processor) {
    processor.registerVerifier(this);
  }

  @Override
  public boolean valid(MobileNumber number) {
    boolean result = number.getNumber().startsWith(PREFIX);
    LOGGER.debug("number {}: {}", number.getNumber(), result);
    return result;
  }
}
