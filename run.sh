#!/bin/sh
export CLASSPATH=$CLASSPATH:/usr/share/java/postgresql.jar
javac DatabaseConnectionManager.java CarRentalService.java ClientService.java Validation.java Main.java
java Main