# Minecraft Plugin Setup
# 1. Configuration
To use the plugin, create a configuration file in src/resources named config.yml with the following content:

      database:
        host: "Enter the IP of your database"
        port: 3306 # Normally 3306
        database: "Enter the name of the database"
        username: "Enter the database user"
        password: "Enter the password of the user"


Replace each placeholder with your actual database details.

# 2. Building the Plugin

To build the Minecraft plugin, make sure to include the following files in your build artifacts:

paper-plugin.yml

config.yml

These files must be present in the final JAR so that Paper can load your plugin and its configuration correctly.
