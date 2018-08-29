from pybleno import *
import sys
import signal
import threading
import time
import RPi.GPIO as GPIO


def alarm(delay, count):
    def onStateChange(state):
        print('on -> stateChange: ' + state);
        if (state == 'poweredOn'):
            bleno.startAdvertisingIBeacon('e2c56db5dffb48d2b060d0f5a71096e0',1,2,-60)
        else:
            bleno.stopAdvertising()
    
    
    GPIO.setmode(GPIO.BCM) # Set GPIO Pin As Numbering
    GPIO.setwarnings(False)
    GPIO.setup(17, GPIO.OUT) # for Buzzer
    GPIO.setwarnings(False)
    GPIO.setup(22,GPIO.OUT)# for LED
    
    if delay ==1 and count ==3:
        bleno = Bleno()
        bleno.on('stateChange', onStateChange)
        
        
    for i in range(0,count):
        beep(delay)
        blink(delay)
        
    if delay ==1 and count ==3:
        bleno.stopAdvertising()
        bleno.disconnect()
        print('disconnected')
       
     
def beep(x):
    GPIO.output(17, GPIO.HIGH)
    time.sleep(x)
    GPIO.output(17, GPIO.LOW)
    time.sleep(0.2)
	
def destroy():
    GPIO.cleanup()
	
def blink(x):
    GPIO.output(22,GPIO.HIGH)
    time.sleep(x)
    GPIO.output(22,GPIO.LOW)
    time.sleep(x)
    
    
#t1 = threading.Thread(target=alarm, args=(0.2,2,))
#t1.start()


