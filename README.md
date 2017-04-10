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
    create-item      Create a item
      Usage: create-item [options]
        Options:
        * --description
            Description for the item being created
        * --name
            Name of the item being created

    add-party      Add a character to a party
      Usage: add-party [options]
        Options:
        * --name
            Name of character to add to party
        * --party
            Name of party
        * --username


    create-party      Create a party
      Usage: create-party [options]
        Options:
        * --gm
            Name of user acting as the game master for the party
        * --name
            Name of new party

    list-characters      List created characters
      Usage: list-characters [options]
        Options:
          --username, --user
            Username of the user whose characters to list
            Default: <empty string>

    list-items      List all items
      Usage: list-items [options]

    delete-quest      Delete quest
      Usage: delete-quest [options]
        Options:
        * --name
            Name of the quest to delete

    create-user      Create a user
      Usage: create-user [options]
        Options:
        * --password
            Password of the user to create
        * --username
            Username of the user to create

    create-tables      Create database tables
      Usage: create-tables [options]

    create-character      Create a character
      Usage: create-character [options]
        Options:
        * --archetype
            Name of the character's archetype
        * --name
            Name of the character being created
        * --perception
            The character's perception stat
            Default: 0
        * --personality
            The character's personality stat
            Default: 0
        * --power
            The character's power stat
            Default: 0
        * --proficiency
            The character's proficiency stat
            Default: 0
        * --race
            Name of the character's race
          --story
            The character's story
            Default: NULL
        * --username
            Username of the character's owner

    create-quest      Create a quest
      Usage: create-quest [options]
        Options:
        * --description, --desc
            Description for the quest being created
        * --experience, --exp, --xp
            Ammount of experience gained by completing the quest
            Default: 0
        * --item-name, --item
            Name of the reward item
        * --name
            Name of the quest being created

    login      User login
      Usage: login [options]
        Options:
        * --password
            Password of the user logging in
        * --username
            Username of the user logging in

    delete-item      Delete a item
      Usage: delete-item [options]
        Options:
        * --name
            Name of the item being created

    list-party      List all parties
      Usage: list-party [options]

    remove-from-party      Delete a character from a party
      Usage: remove-from-party [options]
        Options:
        * --name
            Name of character to delete from party
        * --party
            Name of party
        * --username
            Owner of character

    list-quests      List all quests
      Usage: list-quests [options]
```

This project uses [JCommander](http://jcommander.org/) to parse command line arguments.
