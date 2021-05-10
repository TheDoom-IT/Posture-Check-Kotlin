package com.example.posturecheck

import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.posturecheck.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    enum class TimerState {
        Stopped, Running
    }

    private var currentState: TimerState = TimerState.Stopped
    private var minutesSet: Long = 1

    //Time left to play a sound and start countdown again
    private var secondsLeft: Long = 0
    private var minutesLeft: Long = 0
    //Play the sound after the countdown
    private lateinit var sound: MediaPlayer

    //Attributes of the window. Used to change the brightness of the screen
    private lateinit var winAttr: WindowManager.LayoutParams

    private lateinit var timer: CountDownTimer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        winAttr = this.window.attributes

        //Keeps the screen on
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        sound = MediaPlayer.create(this, R.raw.sound)

        updateWidgets()

        binding.buttonPlus?.setOnClickListener {
            addMinute()
            updateTimerText()
        }

        binding.buttonMinus?.setOnClickListener {
            subtractMinute()
            updateTimerText()
        }

        binding.buttonStartStop?.setOnClickListener {
            if (currentState == TimerState.Stopped) {
                //Change the current state
                currentState = TimerState.Running

                //Update the time to count down
                minutesLeft = minutesSet
                secondsLeft = 0

                //initialize the counter
                initializeTimer()

                //Change brightness of the screen to save an energy
                darkenScreen(true)
            } else {
                currentState = TimerState.Stopped
                timer.cancel()
                darkenScreen(false)
            }

            updateWidgets()
        }
    }

    //Adds 1 to minutesSet. Keeps its value below 60.
    private fun addMinute() {
        minutesSet++
        if (minutesSet >= 60) {
            minutesSet = 1
        }
    }

    //Subtract 1 from minutesSet. Keeps its value above 0.
    private fun subtractMinute() {
        minutesSet--
        if (minutesSet <= 0) {
            minutesSet = 59
        }
    }

    //Create CountDownTimer object and start countdown
    private fun initializeTimer() {
        timer = object : CountDownTimer(minutesLeft * 60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                secondsLeft--
                if (secondsLeft < 0) {
                    minutesLeft--
                    secondsLeft = 59
                }
                updateTimerText()
            }

            //Play sound and start countdown from the beginning
            override fun onFinish() {
                sound.start()
                minutesLeft = minutesSet
                secondsLeft = 0
                this.start()
            }
        }
        timer.start()
    }

    //Change the brightness of the screen to save energy.
    private fun darkenScreen(darken: Boolean) {
        if(darken){
            if(binding.darkenScreen?.isChecked == true){
                winAttr.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_OFF
                this.window.attributes = winAttr
            }
        }else{
            winAttr.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
            this.window.attributes = winAttr
        }

    }

    //Update text on the timer
    private fun updateTimerText() {
        if (currentState == TimerState.Running) {
            binding.clock?.text = getString(
                R.string.clock,
                if (minutesLeft < 10) "0$minutesLeft" else minutesLeft.toString(),
                if (secondsLeft < 10) "0$secondsLeft" else secondsLeft.toString()
            )
        } else {
            binding.clock?.text = minutesSet.toString()
        }
    }

    //Updates widgets so they suit to the current state of the timer
    private fun updateWidgets() {
        if (currentState == TimerState.Running) {
            //change the text on the clock
            updateTimerText()

            //change text of start/stop button
            binding.buttonStartStop?.text = getString(R.string.stop)

            //hide the buttons
            binding.buttonPlus?.visibility = View.INVISIBLE
            binding.buttonMinus?.visibility = View.INVISIBLE
        }
        //currentState == ClockState.Stopped
        else {
            //change the text on the timer
            updateTimerText()

            //change text of start/stop button
            binding.buttonStartStop?.text = getString(R.string.start)

            //show the buttons
            binding.buttonPlus?.visibility = View.VISIBLE
            binding.buttonMinus?.visibility = View.VISIBLE
        }
    }
}