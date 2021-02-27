package com.demo.africannumbers.persistence;

import java.util.List;

import com.demo.africannumbers.model.MobileNumber;
import com.demo.africannumbers.model.MobileNumberState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MobileNumberRepository extends JpaRepository<MobileNumber, Long> {
  List<MobileNumber> findByState(MobileNumberState state);
}
