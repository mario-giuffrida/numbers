package com.demo.africannumbers.correctors;

import com.demo.africannumbers.model.MobileNumber;

public interface Corrector {
  boolean correct(MobileNumber number);
  String correctionInfo();
}
