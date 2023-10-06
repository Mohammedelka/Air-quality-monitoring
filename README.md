# A-QM

The Air Quality Monitoring app, developed using Java on Android Studio, focuses on collecting real-time data from an Arduino prototype and transmitting it through Firebase Realtime Database. The primary objective of the app is to monitor air quality. Additionally, a room management feature has been implemented for practice purposes, although technically there is only a single room associated with the prototype.

Upon launching the app, users are prompted to authenticate or sign up to create a new account. Once logged in, they are presented with a dashboard displaying four options:

- Room List: Users can view the list of added rooms and have the ability to add or modify them by performing a long press.
- Air Quality Indicators: This section provides a visual representation of general air quality indicators, including temperature (in Celsius), humidity (in percentage), CO2 levels (in ppm), and light intensity (in lux). An Air Quality Index (AQI) is calculated based on a threshold of 1000, with the maximum value obtained being 1200. The app assigns a corresponding comment on the air quality, such as good, bad, or moderate.
- Fan Control: Users can control the real fan by toggling it on or off directly from the app.
- Occupancy Prediction: The app consumes a custom API developed to predict room occupancy based on the previously mentioned air quality indicators.

Although there are plans to add more functionalities, due to time constraints, the existing features provide a comprehensive air quality monitoring solution. Thank you for taking the time to read this description.

NOTE : the app is not scalable yet to all devices, The Emulator used is : Pixel 6 Pro API 28
