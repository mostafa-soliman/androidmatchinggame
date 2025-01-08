package com.example.androidmatchinggame

import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidmatchinggame.Adapter.MeaningsAdapter
import com.example.androidmatchinggame.Adapter.WordsAdapter
import com.example.androidmatchinggame.ViewModel.GameViewModel
import com.example.androidmatchinggame.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var wordsAdapter: WordsAdapter
    private lateinit var meaningsAdapter: MeaningsAdapter
    private var selectedStartView: View? = null

    private val gameViewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            wordsColumn.layoutManager = LinearLayoutManager(this@MainActivity)
            meaningsColumn.layoutManager = LinearLayoutManager(this@MainActivity)

            wordsAdapter = WordsAdapter(emptyList()) { wordView ->
                handleStartSelection(wordView)
            }
            meaningsAdapter = MeaningsAdapter(emptyList()) { meaningView ->
                handleEndSelection(meaningView)
            }

            wordsColumn.adapter = wordsAdapter
            meaningsColumn.adapter = meaningsAdapter
        }

        observeGameState()  // مراقبة تغييرات الـ gameState
    }

    private fun observeGameState() {
        lifecycleScope.launchWhenStarted {
            gameViewModel.gameState.collect { gameState ->
                // تحديث البيانات في الـ Adapter
                wordsAdapter.updateWords(gameState.words)
                meaningsAdapter.updateMeanings(gameState.meanings)
            }
        }
    }

    private fun handleStartSelection(view: View) {
        selectedStartView = view
        wordsAdapter.resetOtherRadioButtons(view)
    }

    private fun handleEndSelection(endView: View) {
        selectedStartView?.let { startView ->
            val startCoords = getViewCoordinatesInCanvas(startView)
            val endCoords = getViewCoordinatesInCanvas(endView)

            binding.connectionView.addLine(
                startCoords.first,
                startCoords.second,
                endCoords.first,
                endCoords.second
            )

            selectedStartView = null
            wordsAdapter.resetAllRadioButtons()
            meaningsAdapter.resetAllRadioButtons()
        }
    }

    private fun getViewCoordinatesInCanvas(view: View): Pair<Float, Float> {
        val viewLocation = IntArray(2)
        val canvasLocation = IntArray(2)

        binding.connectionView.getLocationInWindow(canvasLocation)

        val radioButton = view.findViewById<RadioButton>(R.id.startLine)
            ?: view.findViewById<RadioButton>(R.id.endLine)

        radioButton?.let {
            val radioLocation = IntArray(2)
            it.getLocationInWindow(radioLocation)

            return Pair(
                (radioLocation[0] - canvasLocation[0] + it.width / 2).toFloat(),
                (radioLocation[1] - canvasLocation[1] + it.height / 2).toFloat()
            )
        }

        throw NullPointerException("RadioButton not found in the provided view")
    }
}

//class MainActivity : AppCompatActivity() {
//    private lateinit var binding: ActivityMainBinding
//    private lateinit var wordsAdapter: WordsAdapter
//    private lateinit var meaningsAdapter: MeaningsAdapter
//    private var selectedStartView: View? = null
//
//    // استدعاء ViewModel باستخدام viewModels
//    private val gameViewModel: GameViewModel by viewModels()
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        setupRecyclerViews()
//        observeGameState()
//    }
//
//    private fun setupRecyclerViews() {
//        with(binding) {
//            wordsColumn.layoutManager = LinearLayoutManager(this@MainActivity)
//            meaningsColumn.layoutManager = LinearLayoutManager(this@MainActivity)
//
//            wordsAdapter = WordsAdapter(emptyList()) { wordView ->
//                handleStartSelection(wordView)
//            }
//            meaningsAdapter = MeaningsAdapter(emptyList()) { meaningView ->
//                handleEndSelection(meaningView)
//            }
//
//            wordsColumn.adapter = wordsAdapter
//            meaningsColumn.adapter = meaningsAdapter
//        }
//    }
//
//    private fun observeGameState() {
//        // مراقبة البيانات من gameState
//        gameViewModel.gameState.observe(this) { gameState ->
//            wordsAdapter.submitList(gameState.words)
//            meaningsAdapter.submitList(gameState.meanings)
//        }
//    }
//
//    private fun handleStartSelection(view: View) {
//        selectedStartView = view
//        wordsAdapter.resetOtherRadioButtons(view)
//    }
//
//    private fun handleEndSelection(endView: View) {
//        selectedStartView?.let { startView ->
//            val startCoords = getViewCoordinatesInCanvas(startView)
//            val endCoords = getViewCoordinatesInCanvas(endView)
//
//            binding.connectionView.addLine(
//                startCoords.first,
//                startCoords.second,
//                endCoords.first,
//                endCoords.second
//            )
//
//            gameViewModel.addConnection(
//                com.example.androidmatchinggame.data.Connection(
//                    startX = startCoords.first,
//                    startY = startCoords.second,
//                    endX = endCoords.first,
//                    endY = endCoords.second
//                )
//            )
//
//            selectedStartView = null
//            wordsAdapter.resetAllRadioButtons()
//            meaningsAdapter.resetAllRadioButtons()
//        }
//    }
//
//    private fun getViewCoordinatesInCanvas(view: View): Pair<Float, Float> {
//        val viewLocation = IntArray(2)
//        val canvasLocation = IntArray(2)
//
//        binding.connectionView.getLocationInWindow(canvasLocation)
//
//        val radioButton = try {
//            when (view) {
//                is ViewGroup -> {
//                    if (view.findViewById<RadioButton>(R.id.startLine) != null) {
//                        ItemWordBinding.bind(view).startLine
//                    } else {
//                        ItemMeaningBinding.bind(view).endLine
//                    }
//                }
//
//                else -> throw IllegalArgumentException("Unsupported view type: $view")
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//            null
//        }
//
//        radioButton?.let {
//            val radioLocation = IntArray(2)
//            it.getLocationInWindow(radioLocation)
//
//            return Pair(
//                (radioLocation[0] - canvasLocation[0] + it.width / 2).toFloat(),
//                (radioLocation[1] - canvasLocation[1] + it.height / 2).toFloat()
//            )
//        } ?: throw NullPointerException("RadioButton not found in the provided view")
//    }
//}

//
//import android.os.Bundle
//import android.view.View
//import android.view.ViewGroup
//import android.widget.RadioButton
//import androidx.appcompat.app.AppCompatActivity
//import androidx.recyclerview.widget.LinearLayoutManager
//import com.example.androidmatchinggame.Adapter.MeaningsAdapter
//import com.example.androidmatchinggame.Adapter.WordsAdapter
//import com.example.androidmatchinggame.data.WordMeaningPair
//import com.example.androidmatchinggame.databinding.ActivityMainBinding
//import com.example.androidmatchinggame.databinding.ItemMeaningBinding
//import com.example.androidmatchinggame.databinding.ItemWordBinding
//
//class MainActivity : AppCompatActivity() {
//    private lateinit var binding: ActivityMainBinding
//    private lateinit var wordsAdapter: WordsAdapter
//    private lateinit var meaningsAdapter: MeaningsAdapter
//    private var selectedStartView: View? = null
//
//    private val wordMeaningPairs = listOf(
//        WordMeaningPair("الصلاة", "العبادة اليومية"),
//        WordMeaningPair("الزكاة", "التصدق"),
//        WordMeaningPair("الصيام", "الامتناع عن الأكل والشرب"),
//        WordMeaningPair("الحج", "زيارة الكعبة"),
//        WordMeaningPair("القرآن", "كتاب الإسلام المقدس")
//    )
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        with(binding) {
//            wordsColumn.layoutManager = LinearLayoutManager(this@MainActivity)
//            meaningsColumn.layoutManager = LinearLayoutManager(this@MainActivity)
//
//            val shuffledPairs = wordMeaningPairs.shuffled()
//            val shuffledWords = shuffledPairs.map { it.word }
//            val shuffledMeanings = shuffledPairs.map { it.meaning }
//
//            wordsAdapter = WordsAdapter(shuffledWords) { wordView ->
//                handleStartSelection(wordView)
//            }
//            meaningsAdapter = MeaningsAdapter(shuffledMeanings) { meaningView ->
//                handleEndSelection(meaningView)
//            }
//
//            wordsColumn.adapter = wordsAdapter
//            meaningsColumn.adapter = meaningsAdapter
//        }
//    }
//
//    private fun handleStartSelection(view: View) {
//        selectedStartView = view
//        wordsAdapter.resetOtherRadioButtons(view)
//    }
//
//    private fun handleEndSelection(endView: View) {
//        selectedStartView?.let { startView ->
//            val startCoords = getViewCoordinatesInCanvas(startView)
//            val endCoords = getViewCoordinatesInCanvas(endView)
//
//            binding.connectionView.addLine(
//                startCoords.first,
//                startCoords.second,
//                endCoords.first,
//                endCoords.second
//            )
//
//            selectedStartView = null
//            wordsAdapter.resetAllRadioButtons()
//            meaningsAdapter.resetAllRadioButtons()
//        }
//    }
//
//    private fun getViewCoordinatesInCanvas(view: View): Pair<Float, Float> {
//        val viewLocation = IntArray(2)
//        val canvasLocation = IntArray(2)
//
//        binding.connectionView.getLocationInWindow(canvasLocation)
//
//        val radioButton = try {
//            when (view) {
//                is ViewGroup -> {
//                    if (view.findViewById<RadioButton>(R.id.startLine) != null) {
//                        ItemWordBinding.bind(view).startLine
//                    } else {
//                        ItemMeaningBinding.bind(view).endLine
//                    }
//                }
//
//                else -> throw IllegalArgumentException("Unsupported view type: $view")
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//            null
//        }
//
//        radioButton?.let {
//            val radioLocation = IntArray(2)
//            it.getLocationInWindow(radioLocation)
//
//            return Pair(
//                (radioLocation[0] - canvasLocation[0] + it.width / 2).toFloat(),
//                (radioLocation[1] - canvasLocation[1] + it.height / 2).toFloat()
//            )
//        } ?: throw NullPointerException("RadioButton not found in the provided view")
//    }
//}
