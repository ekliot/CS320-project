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
    create-character   Creates a character
      Usage: create-character [character options]
  Options:
    Character options:
      --name           The name of the character to create
      --username       The username of the character's owner
      --race           The character's race
      --archetype      The character's archetype
      --story          <optional> The characters story
      --power          The power stat of the character
      --proficiency    The proficiency stat of the character
      --personality    The personality stat of the character
      --perception     The perception stat of the character
```

This project uses [JCommander](http://jcommander.org/) to parse command line arguments.
