package com.plattysoft.blocklyrainbowhat

import android.os.Bundle
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import com.google.android.things.contrib.driver.button.Button
import com.google.android.things.contrib.driver.ht16k33.AlphanumericDisplay
import com.google.android.things.contrib.driver.rainbowhat.RainbowHat
import com.google.android.things.pio.Gpio
import com.google.blockly.android.AbstractBlocklyActivity
import com.google.blockly.android.codegen.CodeGenerationRequest
import com.google.blockly.model.DefaultBlocks
import java.util.*

private val TAG = MainActivity::class.java.simpleName

class MainActivity : AbstractBlocklyActivity() {

    lateinit var redLed: Gpio
    lateinit var greenLed: Gpio
    lateinit var blueLed: Gpio

    lateinit var buttonA: Button
    lateinit var buttonB: Button
    lateinit var buttonC: Button

    var stateButtonA = false
    var stateButtonB = false
    var stateButtonC = false

    lateinit var alphanumericDisplay: AlphanumericDisplay

    lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initRainbowHat()

        webView = WebView(this)
//        setContentView(webView)

        webView.settings.javaScriptEnabled = true
        webView.addJavascriptInterface(WebAppInterface(this), "Android")
        webView.addJavascriptInterface(AlphanumericDisplayWebInterface(alphanumericDisplay), "AlphanumericDisplay")

        /*
         * The communication from the app towards the WebApp includes the information based on events
         * - Button A, B or C pressed
         * - Temperature / pressure changed
         */
        buttonA.setOnButtonEventListener { button: Button, b: Boolean ->
            stateButtonA = b
            webView.evaluateJavascript("javascript: " +
                    "onButtonAPressed($b);",
                    null)
        }
        buttonB.setOnButtonEventListener { button: Button, b: Boolean ->
            stateButtonB = b
            webView.evaluateJavascript("javascript: " +
                    "onButtonBPressed($b);",
                    null)
        }
        buttonC.setOnButtonEventListener { button: Button, b: Boolean ->
            stateButtonC = b
            webView.evaluateJavascript("javascript: " +
                    "onButtonCPressed($b);",
                    null)
        }

//        loadSampleProgram(webView)
        webView.loadUrl("file:///android_asset/index.html");
    }

    private fun loadSampleProgram(webView: WebView) {
       loadProgram(
                    "function onButtonAPressed(pressed) {\n" +
                    "    Android.setRedLed(pressed)\n" +
                    "}\n" +
                    "\n" +
                    "function onButtonBPressed(pressed) {\n" +
                    "    Android.setGreenLed(pressed)\n" +
                    "}\n" +
                    "\n" +
                    "function onButtonCPressed(pressed) {\n" +
                    "    Android.setBlueLed(pressed)\n" +
                    "}\n")

    }

    private fun loadProgram(program: String) {
        Log.d("Program", program)
        val tagProgram = "<script language=\"JavaScript\">\n $program </script>"
        runOnUiThread{
            webView.loadData(tagProgram, "text/html", "UTF-8")
        }
    }

    private fun initRainbowHat() {
        redLed = RainbowHat.openLedRed()
        greenLed = RainbowHat.openLedGreen()
        blueLed = RainbowHat.openLedBlue()

        buttonA = RainbowHat.openButtonA()
        buttonB = RainbowHat.openButtonB()
        buttonC = RainbowHat.openButtonC()

        alphanumericDisplay = RainbowHat.openDisplay()
        alphanumericDisplay.setEnabled(true)
        alphanumericDisplay.setBrightness(AlphanumericDisplay.HT16K33_BRIGHTNESS_MAX)
    }

    override fun onDestroy() {
        super.onDestroy()

        redLed.close()
        greenLed.close()
        blueLed.close()

        buttonA.close()
        buttonB.close()
        buttonC.close()

        alphanumericDisplay.close()
    }

    override fun getToolboxContentsXmlPath(): String {
//        return "default/toolbox.xml"
        return "toolbox.xml"
    }

    override fun getBlockDefinitionsJsonPaths(): MutableList<String> {
        val assetPaths = ArrayList(DefaultBlocks.getAllBlockDefinitions())
        assetPaths.add("rainbowHat_blocks.json")
        return assetPaths
    }

    override fun getGeneratorsJsPaths(): MutableList<String> {
        return Arrays.asList("generators.js")
    }

    override fun getCodeGenerationCallback(): CodeGenerationRequest.CodeGeneratorCallback {
        return CodeGenerationRequest.CodeGeneratorCallback {
            loadProgram(it)
        }
    }

    override fun onInitBlankWorkspace() {
        // Initialize available variable names.
        controller.addVariable("item")
    }
}

/**
 * Class that defines all the communication from the WebApp to the Rainbow HAT
 * It gives access to:
 * - Red, Green and Blue LEDs
 * - Read button A, B and C states
 * - Read temperature value
 * - Alphanumeric display
 * - RGB LED Strip
 * - Piezo buzzer
 */
class WebAppInterface(val mainActivity: MainActivity) {
    @JavascriptInterface
    fun setRedLed(state: Boolean) {
        mainActivity.redLed.value = state
    }

    @JavascriptInterface
    fun getStateButtonA(): Boolean {
        return mainActivity.stateButtonA
    }

    @JavascriptInterface
    fun getStateButtonB(): Boolean {
        return mainActivity.stateButtonB
    }

    @JavascriptInterface
    fun getStateButtonC(): Boolean {
        return mainActivity.stateButtonC
    }

    @JavascriptInterface
    fun setGreenLed(state: Boolean) {
        mainActivity.greenLed.value = state
    }

    @JavascriptInterface
    fun setBlueLed(state: Boolean) {
        mainActivity.blueLed.value = state
    }

}
