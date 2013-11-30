MavenBuild
==========

Maven Build is a plugin that builds all specified maven projects and moves the files into the plugin directory. It will execute a 'mvn clean install' command and wait for the project to build. It will then transfer the file and allow all the plugins to reload. It only builds plugins when the /reload command is executed.

Configuration
==========
Each directory should have a path with sub paths:
 * files: A list of files to copy (without extension)
 * path: the path to the directory

'*' represents wild values AKA file only needs to contain that part of the string. It is important to have apostrophes around the values so special characters can be read.

Example configurations:

    SomeProject:
      files:
        - 'SomeFileName*'
      path: 'C:\PathToFolder\'
    ProjectTwo:
      files
        - 'ExactFileName'
        - 'This*and*that'
      path: 'C:\MyDevEnv\Project\'
