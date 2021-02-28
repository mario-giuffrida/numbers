package com.demo.africannumbers.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.demo.africannumbers.correctors.Corrector;
import com.demo.africannumbers.exceptions.InvalidStateFilterException;
import com.demo.africannumbers.model.MobileNumber;
import com.demo.africannumbers.model.MobileNumberState;
import com.demo.africannumbers.persistence.MobileNumberRepository;
import com.demo.africannumbers.verifiers.Verifier;
import com.opencsv.CSVReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NumberProcessor {

  private static final Logger LOGGER = LoggerFactory.getLogger(NumberProcessor.class);
  private static final String ID = "id";

  private final ExecutorService executor = Executors.newSingleThreadExecutor();
  private final List<Verifier> registeredVerifiers = new ArrayList<>();
  private final List<Corrector> registeredCorrectors = new ArrayList<>();

  private final MobileNumberRepository repository;

  @Autowired
  public NumberProcessor(MobileNumberRepository repository) {
    this.repository = repository;
  }

  public void loadNumbers(String path) throws URISyntaxException, IOException, InterruptedException {
    if(path == null){
      throw new FileNotFoundException();
    }
    Path url = Paths.get(path);
    Reader reader = Files.newBufferedReader(url);
    loadNumbers(reader);
  }

  void loadNumbersAndWaitForCompletion(String path) throws IOException, InterruptedException, URISyntaxException,
      ExecutionException {
    Path url = Paths.get(ClassLoader.getSystemResource(path).toURI());
    Reader reader = Files.newBufferedReader(url);
    loadNumbersAndWaitForCompletion(reader);
  }

  void loadNumbersAndWaitForCompletion(Reader reader) throws IOException, InterruptedException, ExecutionException {
    List<Future<MobileNumber>> submitted = loadNumbers(reader);
    for (Future<MobileNumber> mobileNumberFuture : submitted) {
      mobileNumberFuture.get();
    }
  }

  List<Future<MobileNumber>> loadNumbers(Reader reader) throws IOException {
    CSVReader csvReader = new CSVReader(reader);
    String[] line;
    List<Future<MobileNumber>> submitted = new ArrayList<>();

    while ((line = csvReader.readNext()) != null) {
      if (header(line)) {
        continue;
      }
      NumberProcessorCallable callable = new NumberProcessorCallable(new MobileNumber(line[0], line[1]));
      submitted.add(executor.submit(callable));
    }
    reader.close();
    csvReader.close();
    return submitted;
  }

  public MobileNumber processSingleNumber(MobileNumber mobile) {
    mobile.setState(MobileNumberState.INCORRECT);
    if (callRegisteredValidators(mobile)) {
      mobile.setState(MobileNumberState.ACCEPTABLE);
    } else if (callRegisteredCorrectors(mobile) && callRegisteredValidators(mobile)) {
      mobile.setState(MobileNumberState.CORRECTED);
    }
    LOGGER.debug("number {} is {}, corrected {}", mobile.getNumber(), mobile.getState(), mobile.getCorrections());
    return mobile;
  }

  public List<MobileNumber> retrieveNumbersByState(String stateFilterString) throws InvalidStateFilterException {
    MobileNumberState stateFilter;
    try {
      stateFilter = MobileNumberState.valueOf(stateFilterString);
      switch (stateFilter) {
        case CORRECTED:
        case INCORRECT:
        case ACCEPTABLE:
      }
    } catch (IllegalArgumentException e) {
      throw new InvalidStateFilterException("filter not valid: " + stateFilterString);
    }
    return repository.findByState(stateFilter);
  }

  public void registerVerifier(Verifier verifier) {
    this.registeredVerifiers.add(verifier);
  }

  public void registerCorrector(Corrector corrector) {
    this.registeredCorrectors.add(corrector);
  }

  private boolean header(String[] line) {
    return line[0].endsWith(ID) && line[1].equals("sms_phone");
  }

  private boolean callRegisteredCorrectors(MobileNumber mobileNumber) {
    boolean corrected = false;
    for (Corrector corrector : registeredCorrectors) {
      if (corrector.correct(mobileNumber)) {
        corrected = true;
      }
    }
    return corrected;
  }

  private boolean callRegisteredValidators(MobileNumber mobileNumber) {
    for (Verifier verifier : registeredVerifiers) {
      if (!verifier.valid(mobileNumber)) {
        return false;
      }
    }
    return true;
  }

  class NumberProcessorCallable implements Callable<MobileNumber> {
    private final MobileNumber mobileNumber;

    public NumberProcessorCallable(MobileNumber mobileNumber) {
      this.mobileNumber = mobileNumber;
    }

    @Override
    public MobileNumber call() {
      MobileNumber result = processSingleNumber(mobileNumber);
      repository.save(result);
      return result;
    }
  }
}
