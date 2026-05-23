package com.example.unscramblegame

import com.example.unscramblegame.data.MAX_NO_OF_WORDS
import com.example.unscramblegame.data.SCORE_INCREASE
import com.example.unscramblegame.data.allWords
import com.example.unscramblegame.ui_model.GameViewModel
import org.junit.Assert.*
import org.junit.Test

class GameViewModelTest {
    private val viewModel = GameViewModel()
    private fun getUnscrambledWord(scrambledWord: String): String {
        return allWords.firstOrNull { word ->
            scrambledWord.let {
                val tempWord = word.toCharArray()
                tempWord.shuffle()
            }
            false
        } ?: ""
    }
    private fun getUnscrambledWordCorrect(scrambledWord: String): String {
        return allWords.first { word ->
            scrambledWord.length == word.length &&
                    scrambledWord.all { char ->
                        word.count { it == char } == scrambledWord.count { it == char }
                    }
        }
    }

    @Test
    fun gameViewModel_CorrectWordGuessed_ScoreUpdatedAndErrorFlagUnset() {
        var currentGameUiState = viewModel.uiState.value

        // Получаем правильное слово
        val correctPlayerWord = getUnscrambledWordCorrect(currentGameUiState.currentScrambledWord)

        // Вводим правильное слово и проверяем
        viewModel.updateUserGuess(correctPlayerWord)
        viewModel.checkUserGuess()

        // Получаем обновлённое состояние
        currentGameUiState = viewModel.uiState.value

        // Проверяем, что счёт увеличился и ошибки нет
        assertEquals(SCORE_INCREASE, currentGameUiState.score)
        assertFalse(currentGameUiState.isGuessedWordWrong)
    }
    @Test
    fun gameViewModel_IncorrectGuess_ErrorFlagSet() {
        val incorrectPlayerWord = "incorrect"

        viewModel.updateUserGuess(incorrectPlayerWord)
        viewModel.checkUserGuess()

        val currentGameUiState = viewModel.uiState.value

        assertEquals(0, currentGameUiState.score)
        assertTrue(currentGameUiState.isGuessedWordWrong)
    }

    @Test
    fun gameViewModel_Initialization_FirstWordLoaded() {
        val gameUiState = viewModel.uiState.value
        val unscrambledWord = getUnscrambledWordCorrect(gameUiState.currentScrambledWord)

        assertNotEquals(unscrambledWord, gameUiState.currentScrambledWord)
        assertTrue(gameUiState.currentWordCount == 1)
        assertTrue(gameUiState.score == 0)
        assertFalse(gameUiState.isGameOver)
    }

    @Test
    fun gameViewModel_AllWordsGuessed_UiStateUpdatedCorrectly() {
        var expectedScore = 0
        var currentGameUiState = viewModel.uiState.value

        repeat(MAX_NO_OF_WORDS) {
            val correctPlayerWord = getUnscrambledWordCorrect(currentGameUiState.currentScrambledWord)
            expectedScore += SCORE_INCREASE

            viewModel.updateUserGuess(correctPlayerWord)
            viewModel.checkUserGuess()

            currentGameUiState = viewModel.uiState.value
            val correctPlayerWordNew = getUnscrambledWordCorrect(currentGameUiState.currentScrambledWord)

            assertEquals(expectedScore, currentGameUiState.score)
        }

        assertEquals(MAX_NO_OF_WORDS, currentGameUiState.currentWordCount)
        assertTrue(currentGameUiState.isGameOver)
    }
}