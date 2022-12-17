package wordsvirtuoso

import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.random.Random
import kotlin.system.exitProcess

const val GREEN_COLOR = "\u001B[48:5:10m%c\u001B[0m"
const val YELLOW_COLOR = "\u001B[48:5:11m%c\u001B[0m"
const val GREY_COLOR = "\u001B[48:5:7m%c\u001B[0m"
const val AZURE_COLOR = "\u001B[48:5:14m%s\u001B[0m"

fun main(args: Array<String>) {
    if (args.size != 2) {
        exitProgramWithMessage("Error: Wrong number of arguments.")
    }

    val wordsFileName = args[0]
    val candidatesFileName = args[1]

    val wordsFile = File(wordsFileName)
    if (!wordsFile.exists()) {
        exitProgramWithMessage("Error: The words file $wordsFileName doesn't exist.")
    }

    val candidateFile = File(candidatesFileName)
    if (!candidateFile.exists()) {
        exitProgramWithMessage("Error: The candidate words file $candidatesFileName doesn't exist.")
    }

    getFilesAndCheckForMistakes(wordsFile, candidateFile)
    val candidates = candidateFile.readLines().map { it.lowercase() }
    val words = wordsFile.readLines().map { it.lowercase() }
    val randomIndex = Random.nextInt(0, candidates.size)
    val secretWord = candidates[randomIndex].lowercase()

    val startTime = System.currentTimeMillis()
    printMessage("Words Virtuoso")
    printMessage()

    val history = mutableListOf<String>()
    play(secretWord, words, history = history)
    val numberOfTries = history.size + 1
    val secretWordCorrect = secretWord.map { GREEN_COLOR.format(it.uppercaseChar()) }.joinToString("")

    if (numberOfTries == 1) exitProgramWithMessage("$secretWordCorrect\nCorrect!\nAmazing luck! The solution was found at once.")
    val endTime = System.currentTimeMillis()
    val duration = endTime - startTime

    for (clue in history) {
        printMessage(clue)
    }

    printMessage(secretWordCorrect)
    printMessage("Correct!")
    printMessage("The solution was found after $numberOfTries tries in ${TimeUnit.MILLISECONDS.toSeconds(duration)} seconds.")
}

fun play(
    secretWord: String,
    words: List<String>,
    wrongLetters: CharArray = CharArray(26),
    history: MutableList<String>
) {
    printMessage("Input a 5-letter word: ")
    val guess = getUserInput()
    checkForExit(guess)
    if (checkForCorrectness(guess, secretWord)) return

    if (!checkForLength(guess)) {
        printMessage("The input isn't a 5-letter word.")
        return play(secretWord, words, wrongLetters, history)
    }

    if (!checkForEnglishCharacters(guess)) {
        printMessage("One or more letters of the input aren't valid.")
        return play(secretWord, words, wrongLetters, history)
    }

    if (!checkForDuplicates(guess)) {
        printMessage("The input has duplicate letters.")
        return play(secretWord, words, wrongLetters, history)
    }

    if (!isWordIncludedInFile(guess, words)) {
        printMessage("The input word isn't included in my words list.")
        return play(secretWord, words, wrongLetters, history)
    }

    var outputMessage = ""
    for (i in guess.indices) {
        if (guess[i] == secretWord[i]) {
            outputMessage += GREEN_COLOR.format(guess[i].uppercaseChar())
        } else if (secretWord.contains(guess[i])) {
            outputMessage += YELLOW_COLOR.format(guess[i].uppercaseChar())
        } else {
            val wrongLetter = guess[i].uppercaseChar()
            wrongLetters[guess[i] - 'a'] = wrongLetter
            outputMessage += GREY_COLOR.format(wrongLetter)
        }
    }

    history.add(outputMessage)
    for (his in history) {
        printMessage(his)
    }

    val wrongLettersString = wrongLetters.filter { it.isLetter() }.joinToString("")
    printMessage(AZURE_COLOR.format(wrongLettersString))
    printMessage()

    play(secretWord, words, wrongLetters, history)
}

fun checkForCorrectness(guess: String, secretWord: String) = guess.equals(secretWord, true)

fun checkForExit(guess: String) {
    if (guess.equals("exit", false)) {
        exitProgramWithMessage("The game is over.")
    }
}

fun getFilesAndCheckForMistakes(wordsFile: File, candidateFile: File) {

    val words = wordsFile.readLines()

    var invalidWordsCount = countInvalidWords(words)

    if (invalidWordsCount != 0) {
        exitProgramWithMessage("Error: $invalidWordsCount invalid words were found in the ${wordsFile.name} file.")
    }

    val candidates = candidateFile.readLines()

    invalidWordsCount = countInvalidWords(candidates)

    if (invalidWordsCount != 0) {
        exitProgramWithMessage("Error: $invalidWordsCount invalid words were found in the ${candidateFile.name} file.")
    }

    val candidatesSet = candidates.map { it.lowercase() }.toSet()
    val wordsSet = words.map { it.lowercase() }.toSet()
    val wordsInCandidates = candidatesSet subtract wordsSet

    if (wordsInCandidates.isNotEmpty()) {
        exitProgramWithMessage("Error: ${wordsInCandidates.size} candidate words are not included in the ${wordsFile.name} file.")
    }
}

fun isWordIncludedInFile(word: String, words: List<String>): Boolean {
    return words.contains(word.lowercase())
}

fun printMessage(message: Any = "", newLine: Boolean = true) {
    if (newLine) {
        return println(message)
    }
    print(message)
}

fun exitProgramWithMessage(message: String) {
    printMessage(message)
    exitProcess(1)
}

fun getUserInput(): String {
    return readln()
}

fun checkForLength(input: String) = input.length == 5

fun checkForDuplicates(input: String) = input.length == input.toSet().size

fun checkForEnglishCharacters(input: String) = "[a-zA-Z]+".toRegex().matches(input)

fun makeCheck(input: String): Boolean {
    return checkForLength(input)
            && checkForEnglishCharacters(input)
            && checkForDuplicates(input)
}

fun countInvalidWords(potentialWords: List<String>): Int {
    var invalidWordsCount = 0
    for (potentialWord in potentialWords) {
        if (!makeCheck(potentialWord)) {
            invalidWordsCount++
        }
    }
    return invalidWordsCount
}