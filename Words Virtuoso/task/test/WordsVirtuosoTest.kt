import org.hyperskill.hstest.dynamic.DynamicTest
import org.hyperskill.hstest.stage.StageTest
import org.hyperskill.hstest.testcase.CheckResult
import org.hyperskill.hstest.testing.TestedProgram
import java.io.File

class WordsVirtuosoTest : StageTest<Any>() {
    private val wordFiles = mapOf(
        "somewords.txt" to "stove\nKILOS\nalong\nkites\nartis\nthorn\nflags\njonty\nglift\ninvar" +
                "\nsteal\nburnt\nrains\nvelar\nafter\ncives\ntrove\ndebag\npaiks\nyoung\nshaft" +
                "\nbinks\nplows\nchamp\nsixth\nsynod\nroids\nanigh\nforts\natopy\ntired\nskite",
        "wrongwords.txt" to "stove\nkilos\nalong\nkites\nartis\nthorn\nflags\njonty\nglift\ninvar" +
                "\nand\ncontain\nhello\nΔrash\nedt#r\n1nums\nwo rd",
        "somecandidates.txt" to "STOVE\nkilos\nalong\nkites\nthorn\nflags\nsteal\nburnt\nrains\nafter" +
                "\nyoung\nshaft\nsixth\ntired",
        "wrongcandidates.txt" to "stove\nkilos\nalong\nkites\nthorn\nflags\nsteal\nburnt\nrains\nafter" +
                "\nand\ncontain\nhello\nΔrash\nedt#r\n1nums\nwo rd\n12345\nPizza",
        "addcandidates.txt" to "STOVE\nkilos\nalong\nkites\nthorn\nflags\nsteal\nburnt\nrains\nafter" +
                "\nyoung\nshaft\nsixth\ntired\neight\ncharm\nmetro",
        "oneword1.txt" to "azure",
        "oneword2.txt" to "could",
        "oneword3.txt" to "music",
        "threewords1.txt" to "cover\nguild\ncould",
        "fourwords1.txt" to "tulip\npoker\nmouse\nmusic"
    )

    @DynamicTest(order = 1)
    fun wrongArgumentsTest(): CheckResult {
        val argsList = listOf(
            arrayOf(""),
            arrayOf("words.txt"),
            arrayOf("words.txt", "candidates.txt", "other.txt")
        )
        for (args in argsList) {
            val co = CheckOutput()
            co.setArguments(*args)
            if (!co.start("Error: Wrong number of arguments."))
                return CheckResult(false, "Your output should contain \"Error: Wrong number of arguments.\"")
            if (!co.programIsFinished())
                return CheckResult(false, "The application didn't exit.")
        }

        return CheckResult.correct()
    }

    @DynamicTest(order = 2, files = "wordFiles")
    fun noExistFilesTest(): CheckResult {

        var co = CheckOutput()
        co.setArguments("noexist.txt", "candidates.txt")
        if (!co.start("Error: The words file noexist.txt doesn't exist."))
            return CheckResult(false,
                "Your output should contain \"The words file noexist.txt doesn't exist.\"")
        if (!co.programIsFinished())
            return CheckResult(false, "The application didn't exit.")
        co = CheckOutput()
        co.setArguments("somewords.txt", "noexist.txt")
        if (!co.start("Error: The candidate words file noexist.txt doesn't exist."))
            return CheckResult(false,
                "Your output should contain \"Error: The candidate words file noexist.txt doesn't exist.\"")
        if (!co.programIsFinished())
            return CheckResult(false, "The application didn't exit.")

        return CheckResult.correct()
    }

    @DynamicTest(order = 3, files = "wordFiles")
    fun invalidWordsTest(): CheckResult {
        val co = CheckOutput()
        co.setArguments("wrongwords.txt", "somecandidates.txt")
        if (!co.start("Error: 7 invalid words were found in the wrongWords.txt file."))
            return CheckResult(false,
                "Wrong error message after input of a words file with invalid words.")
        if (!co.programIsFinished())
            return CheckResult(false, "The application didn't exit.")

        return CheckResult.correct()
    }

    @DynamicTest(order = 4, files = "wordFiles")
    fun invalidCandidatesTest(): CheckResult {
        val co = CheckOutput()
        co.setArguments("somewords.txt", "wrongcandidates.txt")
        if (!co.start("Error: 9 invalid words were found in the wrongcandidates.txt file."))
            return CheckResult(false,
                "Wrong error message after input of a candidate words file with invalid words.")
        if (!co.programIsFinished())
            return CheckResult(false, "The application didn't exit.")

        return CheckResult.correct()
    }

    @DynamicTest(order = 5, files = "wordFiles")
    fun additionalCandidatesTest(): CheckResult {
        val co = CheckOutput()
        co.setArguments("somewords.txt", "addcandidates.txt")
        if (!co.start("Error: 3 candidate words are not included in the somewords.txt file."))
            return CheckResult(false,
                "Wrong error message after input of a candidate words file with additional words.")
        if (!co.programIsFinished())
            return CheckResult(false, "The application didn't exit.")

        return CheckResult.correct()
    }

    @DynamicTest(order = 6, files = "wordFiles")
    fun normalRunTest2(): CheckResult {
        val co = CheckOutput()
        co.setArguments("somewords.txt", "somecandidates.txt")
        if (!co.start("Words Virtuoso"))
            return CheckResult(false, "Your output should contain \"Words Virtuoso\"")
        co.stop()

        return CheckResult.correct()
    }

    @DynamicTest(order = 7, files = "wordFiles")
    fun wrongInputWordsTest(): CheckResult {
        val co = CheckOutput()
        co.setArguments("somewords.txt", "somecandidates.txt")
        if (!co.start("Words Virtuoso", "Input a 5-letter word:"))
            return CheckResult(false, "Your output should contain \"Words Virtuoso\"")

        val noFiveLetters = listOf("trains", "One", "Four", "Two Words", "trouvée")
        noFiveLetters.forEach { input ->
            if (!co.input(input, "The input isn't a 5-letter word.", "Input a 5-letter word:"))
                return CheckResult(false,
                    "Your output should contain \"The input isn't a 5-letter word." +
                            "\nInput a 5-letter word:\"")
        }

        val invalidLetters = listOf("ΗΛΙΟΣ", "étage", "word1", "12345")
        invalidLetters.forEach { input ->
            if (!co.input(input, "One or more letters of the input aren't valid.", "Input a 5-letter word:"))
                return CheckResult(false,
                    "Your output should contain \"One or more letters of the input aren't valid." +
                            "\nInput a 5-letter word:\"")
        }

        val duplicateLetters = listOf("walls", "hello", "pizza")
        duplicateLetters.forEach { input ->
            if (!co.input(input, "The input has duplicate letters.", "Input a 5-letter word:"))
                return CheckResult(false,
                    "Your output should contain \"The input has duplicate letters." +
                            "\nInput a 5-letter word:\"")
        }

        if (!co.input("exit", "The game is over."))
            return CheckResult(false,
                "Your output should contain \"The game is over.\"")

        if (!co.programIsFinished())
            return CheckResult(false, "The application didn't exit.")

        return CheckResult.correct()
    }

    @DynamicTest(order = 8, files = "wordFiles")
    fun wordNotInFile(): CheckResult {
        val co = CheckOutput()
        co.setArguments("somewords.txt", "somecandidates.txt")
        if (!co.start("Words Virtuoso", "Input a 5-letter word:"))
            return CheckResult(false, "Your output should contain \"Words Virtuoso\"")

        val notInFile = listOf("abcde", "KLMNO", "qwert", "AsDfG")
        notInFile.forEach { input ->
            if (!co.input(input, "The input word isn't included in my words list.", "Input a 5-letter word:"))
                return CheckResult(false,
                    "Your output should contain \"The input word isn't included in my words list." +
                            "\nInput a 5-letter word:\"")
        }

        if (!co.input("exit", "The game is over."))
            return CheckResult(false,
                "Your output should contain \"The game is over.\"")

        if (!co.programIsFinished())
            return CheckResult(false, "The application didn't exit.")

        return CheckResult.correct()
    }

    @DynamicTest(order = 9, files = "wordFiles")
    fun normalRun7File(): CheckResult {
        val co = CheckOutput()
        co.setArguments("oneword1.txt", "oneword1.txt")
        if (!co.start("Words Virtuoso", "Input a 5-letter word:"))
            return CheckResult(false, "Your output should contain \"Words Virtuoso\"")

        if (!co.input("azure",
                "\u001B[48:5:10mA\u001B[0m" +
                        "\u001B[48:5:10mZ\u001B[0m" +
                        "\u001B[48:5:10mU\u001B[0m" +
                        "\u001B[48:5:10mR\u001B[0m" +
                        "\u001B[48:5:10mE\u001B[0m",
                "Correct!", "Amazing luck! The solution was found at once."))
            return CheckResult(false,
                "Your output should contain \"AZURE\nCorrect!\nAmazing luck! The solution was found at once.\"" +
                        " with correct coloring.")

        if (!co.programIsFinished())
            return CheckResult(false, "The application didn't exit.")

        return CheckResult.correct()
    }

    @DynamicTest(order = 10, files = "wordFiles")
    fun normalRun8File(): CheckResult {
        val strCover = "\u001B[48:5:10mC\u001B[0m" +
                "\u001B[48:5:10mO\u001B[0m" +
                "\u001B[48:5:7mV\u001B[0m" +
                "\u001B[48:5:7mE\u001B[0m" +
                "\u001B[48:5:7mR\u001B[0m"
        val strGuild = "\u001B[48:5:7mG\u001B[0m" +
                "\u001B[48:5:11mU\u001B[0m" +
                "\u001B[48:5:7mI\u001B[0m" +
                "\u001B[48:5:10mL\u001B[0m" +
                "\u001B[48:5:10mD\u001B[0m"
        val strCould = "\u001B[48:5:10mC\u001B[0m" +
                "\u001B[48:5:10mO\u001B[0m" +
                "\u001B[48:5:10mU\u001B[0m" +
                "\u001B[48:5:10mL\u001B[0m" +
                "\u001B[48:5:10mD\u001B[0m"

        val co = CheckOutput()
        co.setArguments("threewords1.txt", "oneword2.txt")
        if (!co.start("Words Virtuoso", "Input a 5-letter word:"))
            return CheckResult(false, "Your output should contain \"Words Virtuoso\"")

        if (!co.input("cover",
                strCover,
                "\u001B[48:5:14mERV\u001B[0m",
                "Input a 5-letter word:"))
            return CheckResult(false,
                "Your output should contain \"COVER\nERV\nInput a 5-letter word:\" " +
                        "with correct coloring.")

        if (!co.input("guild",
                strCover,
                strGuild,
                "\u001B[48:5:14mEGIRV\u001B[0m", "Input a 5-letter word:"))
            return CheckResult(false,
                "Your output should contain \"COVER\nGUILD\nEGIRV\nInput a 5-letter word:\" " +
                        "with correct coloring.")

        if (!co.input("could",
                strCover,
                strGuild,
                strCould,
                "Correct!"))
            return CheckResult(false,
                "Your output should contain \"COVER\nGUILD\nCOULD\nCorrect!\" " +
                        "with correct coloring.")

        val reportStr = co.getLastOutput().substring(co.position + 1)
        if ("The solution was found after 3 tries in \\d+ seconds.".toRegex().find(reportStr) == null)
            return CheckResult(false,
                "Wrong message on number of tries and lapsed time.")

        if (!co.programIsFinished())
            return CheckResult(false, "The application didn't exit.")

        return CheckResult.correct()
    }

    @DynamicTest(order = 11, files = "wordFiles")
    fun normalRun9File(): CheckResult {
        val strMouse = "\u001B[48:5:10mM\u001B[0m" +
                "\u001B[48:5:7mO\u001B[0m" +
                "\u001B[48:5:11mU\u001B[0m" +
                "\u001B[48:5:11ms\u001B[0m" +
                "\u001B[48:5:7mE\u001B[0m"
        val strPoker = "\u001B[48:5:7mP\u001B[0m" +
                "\u001B[48:5:7mO\u001B[0m" +
                "\u001B[48:5:7mK\u001B[0m" +
                "\u001B[48:5:7mE\u001B[0m" +
                "\u001B[48:5:7mR\u001B[0m"
        val strTulip = "\u001B[48:5:7mT\u001B[0m" +
                "\u001B[48:5:10mU\u001B[0m" +
                "\u001B[48:5:7mL\u001B[0m" +
                "\u001B[48:5:10mI\u001B[0m" +
                "\u001B[48:5:7mP\u001B[0m"
        val strMusic = "\u001B[48:5:10mM\u001B[0m" +
                "\u001B[48:5:10mU\u001B[0m" +
                "\u001B[48:5:10mS\u001B[0m" +
                "\u001B[48:5:10mI\u001B[0m" +
                "\u001B[48:5:10mC\u001B[0m"

        val co = CheckOutput()
        co.setArguments("fourwords1.txt", "oneword3.txt")
        if (!co.start("Words Virtuoso", "Input a 5-letter word:"))
            return CheckResult(false, "Your output should contain \"Words Virtuoso\"")

        if (!co.input("mouse",
                strMouse,
                "\u001B[48:5:14mEO\u001B[0m",
                "Input a 5-letter word:"))
            return CheckResult(false,
                "Your output should contain \"MOUSE\nEO\nInput a 5-letter word:\" " +
                        "with correct coloring.")

        if (!co.input("poker",
                strMouse,
                strPoker,
                "\u001B[48:5:14mEKOPR\u001B[0m",
                "Input a 5-letter word:"))
            return CheckResult(false,
                "Your output should contain \"MOUSE\nPOKER\nEKOPR\nInput a 5-letter word:\" " +
                        "with correct coloring.")

        if (!co.input("tulip",
                strMouse,
                strPoker,
                strTulip,
                "\u001B[48:5:14mEKLOPRT\u001B[0m", "Input a 5-letter word:"))
            return CheckResult(false,
                "Your output should contain \"MOUSE\nPOKER\nTULIP\nEKLOPRT\nInput a 5-letter word:\" " +
                        "with correct coloring.")

        if (!co.input("music",
                strMouse,
                strPoker,
                strTulip,
                strMusic,
                "Correct!"))
            return CheckResult(false,
                "Your output should contain \"MOUSE\nPOKER\nTULIP\nMUSIC\nCorrect!\" " +
                        "with correct coloring.")

        val reportStr = co.getLastOutput().substring(co.position + 1)
        if ("The solution was found after 4 tries in \\d+ seconds.".toRegex().find(reportStr) == null)
            return CheckResult(false,
                "Wrong message on number of tries and lapsed time.")

        if (!co.programIsFinished())
            return CheckResult(false, "The application didn't exit.")

        return CheckResult.correct()
    }

    @DynamicTest(order = 12, files = "wordFiles")
    fun checkIfWordsRandomFile2(): CheckResult {
        val words = listOf("cover", "guild", "could")
        val counts = IntArray(3)

        repeat(15) {
            val co = CheckOutput()
            co.setArguments("threewords1.txt", "threewords1.txt")
            co.start("Words Virtuoso", "Input a 5-letter word:")

            for ((index, word) in words.withIndex()) {
                if (co.getNextOutput(word).contains("Correct!")) {
                    counts[index]++
                    break
                }
            }
        }

        if (counts[0] * counts[1] * counts[2] == 0)
            return CheckResult(false, "Words aren't random chosen.")

        return CheckResult.correct()
    }
}

class CheckOutput {
    private var main: TestedProgram = TestedProgram()
    var position = 0
    private var caseInsensitive = true
    private var trimOutput = true
    private val arguments= mutableListOf<String>()
    private var isStarted = false
    private var lastOutput = ""

    private fun checkOutput(outputString: String, vararg checkStr: String): Boolean {
        var searchPosition = position
        for (cStr in checkStr) {
            val str = if (caseInsensitive) cStr.lowercase() else cStr
            val findPosition = outputString.indexOf(str, searchPosition)
            if (findPosition == -1) return false
            if ( outputString.substring(searchPosition until findPosition).isNotBlank() ) return false
            searchPosition = findPosition + str.length
        }
        position = searchPosition
        return true
    }

    fun start(vararg checkStr: String): Boolean {
        return if (!isStarted) {
            var outputString = main.start(*arguments.toTypedArray())
            lastOutput = outputString
            if (trimOutput) outputString = outputString.trim()
            if (caseInsensitive) outputString = outputString.lowercase()
            isStarted = true
            checkOutput(outputString, *checkStr)
        } else false
    }

    fun stop() {
        main.stop()
    }

    fun input(input: String, vararg checkStr: String): Boolean {
        if (main.isFinished) return false
        var outputString = main.execute(input)
        lastOutput = outputString
        if (trimOutput) outputString = outputString.trim()
        if (caseInsensitive) outputString = outputString.lowercase()
        position = 0
        return checkOutput(outputString, *checkStr)
    }

    fun inputNext(vararg checkStr: String): Boolean {
        var outputString = lastOutput
        if (trimOutput) outputString = outputString.trim()
        if (caseInsensitive) outputString = outputString.lowercase()
        return checkOutput(outputString, *checkStr)
    }

    fun getNextOutput(input: String): String {
        if (main.isFinished) return ""
        val outputString = main.execute(input)
        lastOutput = outputString
        position = 0
        return  outputString
    }

    fun getLastOutput(): String { return lastOutput }
    fun programIsFinished(): Boolean  = main.isFinished
    fun setArguments(vararg args: String) { arguments.addAll(args.toMutableList()) }
    fun setCaseSensitivity(caseInsensitive: Boolean) { this.caseInsensitive = caseInsensitive }
    fun setOutputTrim(trimOutput: Boolean) { this.trimOutput = trimOutput}
}
