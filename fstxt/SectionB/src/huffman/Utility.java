package huffman;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Utility {

  public static List<String> getWords(String filePath) {
    List<String> words = null;
    try (Stream<String> linesStream = Files.lines(Paths.get(filePath))) {
      words = linesStream.flatMap(line -> Arrays.stream(line.split(" "))).map(word -> word.trim())
          .collect(Collectors.toList());
    } catch (IOException e) {
      e.printStackTrace();
    }
    return words;
  }

  public static String sequenceOfBitsAsNumber(String binaryEncoding) {
    final String binaryEncodingWithHeading1 =
        "1" + binaryEncoding; // Prepending 1 not to lose heading zeroes
    BigInteger result = new BigInteger(binaryEncodingWithHeading1, 2);
    return result.toString();
  }

  public static String numberAsSequenceOfBits(String numberRepresentation) {
    BigInteger number = new BigInteger(numberRepresentation);
    String binaryRepresentation = number.toString(2);
    return binaryRepresentation.substring(1); // Removing previously prepended 1
  }

  public static long totalLength(List<String> words) {
    long length = words.size() - 1; // White spaces
    length += words.stream().mapToLong(w -> w.length()).sum();
    return length;
  }

  public static Map<String, Integer> countWords(List<String> words) {
    int THREAD_NUM = 8;

    Counter[] counters = new Counter[THREAD_NUM];
    int lengthPerThread = words.size() / THREAD_NUM;
    for (int i = 0; i < THREAD_NUM - 1; i++) {
      counters[i] = new Counter(words, i * lengthPerThread, (i + 1) * lengthPerThread);
    }
    counters[THREAD_NUM - 1] = new Counter(words, (THREAD_NUM - 1) * lengthPerThread, words.size());
    Arrays.stream(counters).forEach(Thread::start);

    for (Counter counter : counters) {
      try {
        counter.join();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    Map<String, Integer> result = new HashMap<>();
    for (String word : words) {
      Integer totalCount =
          Arrays.stream(counters)
              .map(Counter::getResult)
              .filter((mapResult) -> mapResult.containsKey(word))
              .map((mapResult) -> mapResult.get(word))
              .reduce(Integer::sum)
              .get();
      result.put(word, totalCount);
    }

    return result;
  }
}
