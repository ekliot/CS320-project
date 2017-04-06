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
3. Run `java -jar target/CS320-project-0.0.1-SNAPSHOT-jar-with-dependencies.jar [options] [command] [command options]`
```
  Options:
    --help, -h
      Show this message
      Default: false
  Commands:
    create-tables      Create database tables
      Usage: create-tables [options]
    list-characters    List characters in the database
      Usage: list-characters [list-characters options]
  Options:
    list-characters
      --username, --user  <optional> specify which user's characters to list
```

This project uses [JCommander](http://jcommander.org/) to parse command line arguments.
