package com.demo.africannumbers.correctors;

import com.demo.africannumbers.model.MobileNumber;
import com.demo.africannumbers.service.NumberProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DeleteRemoverCorrector implements Corrector {

  private static final Logger LOGGER = LoggerFactory.getLogger(DeleteRemoverCorrector.class);
  private static final String DELETED = "_DELETED_";
  private static final String CORRECTION_INFO = "removed deleted postfix";

  @Autowired
  public DeleteRemoverCorrector(NumberProcessor processor) {
    processor.registerCorrector(this);
  }

  @Override
  public boolean correct(MobileNumber number) {
    if (number.getNumber().contains(DELETED)) {
      int indexDeleted = number.getNumber().indexOf(DELETED);
      number.setNumber(number.getNumber().substring(0, indexDeleted));
      number.getCorrections().add(correctionInfo());
      LOGGER.debug("removed {} postfix from number {}: {}", DELETED, number.getNumber(), correctionInfo());
      return true;
    }
    return false;
  }

  @Override
  public String correctionInfo() {
    return CORRECTION_INFO;
  }
}
