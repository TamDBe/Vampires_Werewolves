# Vampires_Werewolves
A short tactics-style RPG game implemented in which you, the player, control a team of vampires in battle against a team of evil werewolves!
Implemented in Java SE15, using JavaFX 15.

How to run:
1. Install the latest version of Eclipse from https://www.eclipse.org/downloads/.
2. Download the latest JavaFX from https://gluonhq.com/products/javafx/.
   Note: Users of MacOS with the M1 chip need to download the x64 version, not the aarch64.
3. Open Eclipse.
4. Create a new Java Project in Eclipse by going to File -> New -> Java Project.
5. Uncheck 'Use default location' and change the location to Vampires_Werewolves folder.
6. Click Finish.
7. Right click on the Vampire_Werewolves project in the Package Explorer and select Run As -> Run Configurations.
8. In the Arguments tab, insert the following line into 'VM arguments':
--module-path PATH_TO_JAVAFX_LIB --add-modules javafx.controls,javafx.fxml
  
where PATH_TO_JAVAFX_LIB is the location of the 'lib' folder located in the JavaFX folder that was downloaded in step 2.
9. Uncheck 'Use the -XstartOnFirstThread argument when launching with SWT'
10. Click Run.
