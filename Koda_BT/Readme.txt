BT Simple App
This is an Android application that allows you to connect to a Bluetooth server, receive and display text data from a specified file on the server(your computer).

Features
- Connect to a Bluetooth server with a specified UUID
- Display a list of paired Bluetooth devices
- Receive and display text data from the server
- Automatically update the displayed text when new data is received

Usage
- Run the app on your device or emulator.
- Grant Bluetooth permissions when prompted.
- Previously you need to pair your device to the computer. Then, a list of paired Bluetooth devices will be displayed. Tap on the device running the Bluetooth server to initiate a connection.
- Once connected, the app will receive and display text data from the server. The displayed text will be updated automatically when new data is received.
   To test that the new data is being displayed, you can manually add new line of data and press Save.

Troubleshooting
- If the app is unable to connect to the server, ensure that the server is running, advertising the service, and using the correct UUID.
- If no paired devices are found, check your device's Bluetooth settings and ensure that the server device is paired with your Android device.
----------------------------------------------------------------------------------------------------------------------------------------------

Bluetooth Server
This Python script allows you to create a simple Bluetooth server that sends the content of a text file to a connected Bluetooth client when the file is modified.

Features
- Create a Bluetooth server with a specified UUID
- Monitor a text file for changes and send the new content to the connected client
- Handle multiple lines of text
- Gracefully exit the program when Ctrl+C is pressed

Prerequisites
- Python 3.x
- PyBluez library (install with pip install pybluez)

To run the SERVER code:
- CD into directory  +  python "file-name"

Troubleshooting
- If the server is unable to start, ensure that your device's Bluetooth is enabled and that the PyBluez library is installed correctly.
- If the server is not sending data when the file is modified, double-check the file_path variable and ensure it points to the correct file.
- If the client is unable to connect, make sure the UUID in the server script matches the one used in the Android client app.






