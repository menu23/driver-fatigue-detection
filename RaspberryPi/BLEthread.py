from pybleno import *
import sys
import signal
import time
from threading import Thread



def startIBeacon():
    def onStateChange(state):
        print('on -> stateChange: ' + state);
        if (state == 'poweredOn'):
            bleno.startAdvertisingIBeacon('e2c56db5dffb48d2b060d0f5a71096e0',1,2,-60)
        else:
            bleno.stopAdvertising()

    bleno = Bleno()
    bleno.on('stateChange', onStateChange)
    time.sleep(20)
    bleno.stopAdvertising()
    bleno.disconnect()
    print('disconnected')
    
    
