package com.scalarr.siniaieva;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

public class HangmanGuesser {

	private int secretWordLength;
	private List<String> possibleWords = new ArrayList<>();
	private Set<Character> correct = new HashSet<Character>();
	private Set<Character> incorrect = new HashSet<Character>();

	public HangmanGuesser(int secretWordLength) {

		/*try (Stream<Path> paths = Files.walk(Paths.get("src/main/resources/word_list"))) {
			wordListWithSpesifiedLenght = paths
					.filter(Files::isRegularFile)
					.flatMap(this::getWords)
					.filter(word -> word.length() == wordLenght)
					.collect(Collectors.toList());
		} catch (IOException e) {
			System.err.println(e);
		}*/
		this.secretWordLength = secretWordLength;
		try {

			for (File f : new File("src/main/resources/word_list/").listFiles()) {
				BufferedReader reader = new BufferedReader(new FileReader(f));
				for (String str; (str = reader.readLine()) != null; ) {
					str = str.trim();
					if (str.length() == secretWordLength) {
						possibleWords.add(str);
					}
				}
			}
		} catch (IOException ex) {
			// ignore
		}
	}


	public char makeGuess(String state) {

		StringBuilder excluding = new StringBuilder();

		for (Iterator<Character> ex = incorrect.iterator(); ex.hasNext();) {
			excluding.append(ex.next());
		}

		//if the length is 1, then most likely letters are 'a' and 'i'
		if(state.length() == 1){
			return (!correct.contains('a') && !incorrect.contains('a')) ? 'a' : 'I';
		}
		//guessing the one with least number of '_'
		String word = state.toLowerCase();
		Pattern regex = Pattern.compile(word.replace(
				"_",
				(excluding.length() > 0) ? String.format("[a-z&&[^%s]]",
						excluding) : "[a-z]"));

		possibleWords = possibleWords.stream()
				.filter(possibleWord -> regex.matcher(possibleWord).find())
				.collect(Collectors.toList());

		Map<Character, Long> frequency;

		if(almostGuessed(state)) {
			frequency = contFrequencyOfLetterByPosition().get(state.indexOf('_'));
		} else {
			frequency = contGeneralFrequencyOfLetter();
		}
		//find  and return the letter with highest frequency
		return getMostFrequentLetter(frequency);
	}

	// put the guessed letters into appropriate group
	public void update(char guess, boolean success) {
		if (success) {
			correct.add(guess);
		} else {
			incorrect.add(guess);
		}
	}

	//count the frequency of each letter within the possible words depending of letter position

	private List<Map<Character, Long>> contFrequencyOfLetterByPosition() {
		return IntStream.range(0, secretWordLength - 1)
				.mapToObj(index -> possibleWords.stream()
						.map(possibleWord -> possibleWord.charAt(index))
						.filter(letter -> !correct.contains(letter) && !incorrect.contains(letter))
						.collect(Collectors.groupingBy(Function.identity(), Collectors.counting())))
				.collect(Collectors.toList());
	}

	private Map<Character, Long> contGeneralFrequencyOfLetter() {
		return possibleWords.stream()
				.flatMap(word -> word.chars().mapToObj(c -> (char)c))
				.filter(letter -> !correct.contains(letter) && !incorrect.contains(letter))
						.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
	}

	private boolean almostGuessed(String state) {
		long notGuessedNumber = state.chars().mapToObj(c -> (char) c).filter(symbol -> symbol.equals('_')).count();
		return ((float)notGuessedNumber % secretWordLength) > 0.8;
	}

	private char getMostFrequentLetter(Map<Character, Long> frequency) {
		Long maxFrequency = frequency.values().stream().max(Long::compareTo).get();
		for (Map.Entry<Character, Long> entry : frequency.entrySet()) {
			if(entry.getValue() == maxFrequency) {
				return entry.getKey();
			}
		}
		for (char c = 'a'; c <= 'z'; ++c) {
			if (!(correct.contains(c) || incorrect.contains(c))) {
				return c;
			}
		}
		return 0;
	}
	
}
