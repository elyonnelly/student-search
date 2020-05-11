set PATH_TO_FX="C:\javafx-sdk-11.0.2\lib"
java --module-path %PATH_TO_FX% --add-modules javafx.controls,javafx.fxml -jar client-jar-with-dependencies.jar
java -jar "client-jar-with-dependencies.jar"