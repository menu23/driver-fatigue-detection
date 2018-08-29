# driver-fatigue-detection
A fatigue detection and alert system that can be easily installed in vehicles to prevent road accidents. The system runs on a Raspberry Pi module and works by analyzing the eye closure duration and yawn frequency and alerting the driver by activating LEDs, buzzers and sending warning message to his emergency contacts.

# Hardware Setup
1)	Connect the Raspberry Pi minicomputer to peripherals like monitor, keyboard and mouse
2)	Connect the NoIR camera in the on-board camera slot
3)	Use the GPIO pins to connect the LED (GPIO 22) and Buzzer (GPIO 17) modules

# Source Code Setup
## Raspberry Pi
1)	Copy the source code in Raspberry Pi folder onto a directory
## Android
1)	Open the app folder to find the Android app code
2)	Run the Android Studio Project through your PC on your connected Android device
3)	Now the Driver Assist app has been installed on your Android smartphone

# Running the System
## Raspberry Pi
1)	Turn on Bluetooth
2)	Open a Terminal window and move into the directory where the files are saved
3)	Run the detect_both_camera.py file
4)	Make sure face is getting captured
## Android
1)	Set your emergency contact from your contact list through the option in the main menu.
2)	Make sure your phone's Bluetooth and GPS are turned on.
3)	Pair your phone to raspberrypi through Bluetooth. You can use Device Status option in the main menu to check the pairing.
4)	If you are detected to be slightly tired, your device will alert you by blinking LED lights and beeping.
5)	If you seem extremely sleepy, your phone will send three SMSs to your emergency contact, spaced by 5 minutes each, containing your current location.
