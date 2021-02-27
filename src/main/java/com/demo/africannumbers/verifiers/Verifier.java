package com.demo.africannumbers.verifiers;

import com.demo.africannumbers.model.MobileNumber;

public interface Verifier {
  boolean valid(MobileNumber number);
}
