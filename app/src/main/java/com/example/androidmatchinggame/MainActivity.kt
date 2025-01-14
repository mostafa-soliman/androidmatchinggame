package com.example.androidmatchinggame

import android.graphics.Color
import android.graphics.PointF
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidmatchinggame.Adapter.MeaningsAdapter
import com.example.androidmatchinggame.Adapter.WordsAdapter
import com.example.androidmatchinggame.ViewModel.GameViewModel
import com.example.androidmatchinggame.data.Connection
import com.example.androidmatchinggame.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var wordsAdapter: WordsAdapter
    private lateinit var meaningsAdapter: MeaningsAdapter
    private var selectedWordView: Pair<View, String>? = null

    private var isConnecting = false
    private var startWordView: Pair<View, String>? = null

    private val gameViewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupRecyclerViews()
        observeGameState()

//        observeGameState()

        binding.verifyButton.setOnClickListener {
            checkAnswers()
        }
    }

    private fun setupRecyclerViews() {
        with(binding) {
            wordsColumn.layoutManager = LinearLayoutManager(this@MainActivity)
            meaningsColumn.layoutManager = LinearLayoutManager(this@MainActivity)

            wordsAdapter = WordsAdapter(emptyList()) { view, word, event ->
                handleWordTouch(view, word, event)
            }

            meaningsAdapter = MeaningsAdapter(emptyList()) { view, meaning, event ->
                handleMeaningTouch(view, meaning, event)
            }
            wordsColumn.adapter = wordsAdapter
            meaningsColumn.adapter = meaningsAdapter
        }
    }

    private fun observeGameState() {
        lifecycleScope.launchWhenStarted {
            gameViewModel.gameState.collect { gameState ->
                wordsAdapter.updateWords(gameState.words)
                meaningsAdapter.updateMeanings(gameState.meanings)
            }
        }
    }
    private fun handleWordTouch(view: View, word: String, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                // Only start connection if no connection is in progress
                if (!isConnecting) {
                    isConnecting = true
                    startWordView = Pair(view, word)
                    view.isSelected = true
                    val coords = getViewCoordinatesInCanvas(view)
                    binding.connectionView.startDrawing(
                        Connection(
                            coords.first,
                            coords.second,
                            coords.first,
                            coords.second
                        )

                    )
                }
            }

        }
        return true
    }

    private fun handleMeaningTouch(view: View, meaning: String, event: MotionEvent): Boolean {

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                // لا نريد بدء التوصيل من meaning
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                // تحديث الخط فقط إذا كان هناك توصيل جاري
                if (isConnecting && startWordView != null) {
                    val startCoords = getViewCoordinatesInCanvas(startWordView!!.first)
                    val currentPoint = PointF(event.rawX, event.rawY)
                    val canvasLocation = IntArray(2)
                    binding.connectionView.getLocationInWindow(canvasLocation)

                    // تحويل الإحداثيات إلى نظام إحداثيات الـ canvas
                    val canvasX = currentPoint.x - canvasLocation[0]
                    val canvasY = currentPoint.y - canvasLocation[1]

                    val connection = Connection(
                        startCoords.first,
                        startCoords.second,
                        canvasX,
                        canvasY
                    )
                    binding.connectionView.updateCurrentLine(connection)
                }
            }

            MotionEvent.ACTION_UP -> {
                if (isConnecting && startWordView != null) {
                    val (wordView, word) = startWordView!!

                    // التحقق من صحة التوصيل
                    if (gameViewModel.isValidConnection(word, meaning)) {
                        // إضافة التوصيل الصحيح
                        val startCoords = getViewCoordinatesInCanvas(wordView)
                        val endCoords = getViewCoordinatesInCanvas(view)

                        val connection = Connection(
                            startCoords.first,
                            startCoords.second,
                            endCoords.first,
                            endCoords.second
                        )

                        binding.connectionView.addLine(
                            startCoords.first,
                            startCoords.second,
                            endCoords.first,
                            endCoords.second
                        )

                        gameViewModel.addConnection(word, meaning, connection)

              //          playSuccessSound()
                    } else {
                        // تأثير بصري للتوصيل الخاطئ
                        val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
                        } else {
                            @Suppress("DEPRECATION")
                            vibrator.vibrate(200)
                        }

                        wordView.animate()
                            .translationX(10f)
                            .setDuration(100)
                            .withEndAction {
                                wordView.animate()
                                    .translationX(-10f)
                                    .setDuration(100)
                                    .withEndAction {
                                        wordView.translationX = 0f
                                    }
                            }

                    }

                    // إعادة تعيين حالة التوصيل
                    isConnecting = false
                    startWordView?.first?.isSelected = false
                    startWordView = null
                    binding.connectionView.clearCurrentLine()
                }
            }
        }

        return true
}

    private fun handleWordSelection(view: View, word: String) {
        if (!gameViewModel.gameState.value.connections.any {
                getWordByCoordinates(
                    it.startX,
                    it.startY
                ) == word
            }) {
            selectedWordView = Pair(view, word)
        }
    }

    private fun handleMeaningSelection(meaningView: View, meaning: String) {
        selectedWordView?.let { (wordView, word) ->
            if (gameViewModel.canAddConnection(word, meaning)) {
                val startCoords = getViewCoordinatesInCanvas(wordView)
                val endCoords = getViewCoordinatesInCanvas(meaningView)

                binding.connectionView.addLine(
                    startCoords.first,
                    startCoords.second,
                    endCoords.first,
                    endCoords.second
                )

                // Add connection to game state
                gameViewModel.addConnection(
                    word,
                    meaning,
                    Connection(
                        startCoords.first,
                        startCoords.second,
                        endCoords.first,
                        endCoords.second
                    )
                )

                selectedWordView = null
                wordsAdapter.resetSelection()
                meaningsAdapter.resetSelection()
            }
        }
    }

    private fun checkAnswers() {

        val isCorrect = gameViewModel.checkAnswers()

        if (isCorrect) {
            Toast.makeText(this, "الإجابات صحيحة", Toast.LENGTH_SHORT).show()
        } else {
            val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
            vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
            Toast.makeText(this, "الإجابات خاطئة", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getWordByCoordinates(x: Float, y: Float): String {
        val layoutManager = binding.wordsColumn.layoutManager as LinearLayoutManager
        val firstVisiblePosition = layoutManager.findFirstVisibleItemPosition()
        val lastVisiblePosition = layoutManager.findLastVisibleItemPosition()

        for (position in firstVisiblePosition..lastVisiblePosition) {
            val view = layoutManager.findViewByPosition(position)
            if (view != null) {
                val location = IntArray(2)
                view.getLocationOnScreen(location)
                val viewX = location[0].toFloat()
                val viewY = location[1].toFloat()

                val viewWidth = view.width.toFloat()
                val viewHeight = view.height.toFloat()

                if (x in viewX..(viewX + viewWidth) && y in viewY..(viewY + viewHeight)) {
                    return wordsAdapter.getWordAtPosition(position)
                }
            }
        }
        return ""
    }

    private fun getMeaningByCoordinates(x: Float, y: Float): String {
        val layoutManager = binding.meaningsColumn.layoutManager as LinearLayoutManager
        val firstVisiblePosition = layoutManager.findFirstVisibleItemPosition()
        val lastVisiblePosition = layoutManager.findLastVisibleItemPosition()

        for (position in firstVisiblePosition..lastVisiblePosition) {
            val view = layoutManager.findViewByPosition(position)
            if (view != null) {
                val location = IntArray(2)
                view.getLocationOnScreen(location)
                val viewX = location[0].toFloat()
                val viewY = location[1].toFloat()

                val viewWidth = view.width.toFloat()
                val viewHeight = view.height.toFloat()

                if (x in viewX..(viewX + viewWidth) && y in viewY..(viewY + viewHeight)) {
                    return meaningsAdapter.getMeaningAtPosition(position)
                }
            }
        }
        return ""
    }

    private fun getViewCoordinatesInCanvas(view: View): Pair<Float, Float> {
        val viewLocation = IntArray(2)
        val canvasLocation = IntArray(2)

        binding.connectionView.getLocationInWindow(canvasLocation)
        view.getLocationInWindow(viewLocation)

        return Pair(
            (viewLocation[0] - canvasLocation[0] + view.width / 2).toFloat(),
            (viewLocation[1] - canvasLocation[1] + view.height / 2).toFloat()
        )
    }
}

