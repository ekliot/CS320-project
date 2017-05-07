The Foreign Keys
================

**Team Lead**: Sam Kilgus

**UI Engineer**: Matt Witte

**Test Engineer**: Alberto Serrano

**Design Engineer**: Elijah Kliot

**Domain**: RPG Characters

## Running the project
1. Download [Maven](https://maven.apache.org/download.cgi)
2. Run `mvn package` to get an executable jar file
3. Run any of the following commands:
   - `./erase_db.sh`
      * Removes a previously created database instance. 
   - `./create_db.sh`
      * Runs the initilizaion script for the database (runs the `erase_db.sh` script before execution). 
      * Equivalent to `java -jar target/CS320-project-0.0.1-SNAPSHOT-jar-with-dependencies.jar create-tables`
   - `./start.sh`
      * Starts the program (database must be created before use).
      * Equivalent to `java -jar target/CS320-project-0.0.1-SNAPSHOT-jar-with-dependencies.jar`

This project uses [JCommander](http://jcommander.org/) to parse command line arguments.
