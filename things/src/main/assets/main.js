//var textContainer = document.createElement("p");
//var nativeText = document.createTextNode("Android Text");
//textContainer.appendChild(nativeText);
//nativeText.nodeValue = "Test";

function setRedLed() {
    var checkBox = document.getElementById("redLed");
    Android.setRedLed(checkBox.checked)
}

function setGreenLed() {
    var checkBox = document.getElementById("greenLed");
    Android.setGreenLed(checkBox.checked)
}

function setBlueLed() {
    var checkBox = document.getElementById("blueLed");
    Android.setBlueLed(checkBox.checked)
}

function onButtonAPressed(pressed) {
    document.getElementById('buttonA').checked = pressed
}

function onButtonBPressed(pressed) {
    document.getElementById('buttonB').checked = pressed
}

function onButtonCPressed(pressed) {
    document.getElementById('buttonC').checked = pressed
}