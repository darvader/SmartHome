# Smart home project
This android application is used to run several smart home devices powered by ESP chips (ESP8266 and ESP32)

# WIFI
Your phone must be in the same WIFI as your ESP devices

# UDP
This app uses UDP for the communication. To find the devices, it does a broadcast with the string 'detect'. 
All ESP devices will then return its answer containing the function of it (like 'LedMatrix' or 'LedStrip').
With that you can now select the device for your commands.
The commands are done with simple strings or binary arrays.

# Led Matrix
To install this on your ESP see [LedMatrix](https://github.com/darvader/LedMatrix)
Choose LedMatrix in the app.
There are different function you can choose like time, score board.
## Score Board
The score board is used to display the scores for a volleyball game.
### Live Score
It is basically the same, but it reads the live ticker from either TVV or DVV and shows it live on the score board.