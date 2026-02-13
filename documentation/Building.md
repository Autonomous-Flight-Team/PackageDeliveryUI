# Installing
Run the installer (.pkg/.exe/.deb) on your computer, and follow the instructions.

# Install from Source / Building the Application Installer

### The installer must be build on its target OS.
**Mac** 
`mvn clean package &&  jpackage --type pkg --name DroneView --input target --main-jar droneview-0.1.0-jar-with-dependencies.jar --main-class com.ui.Launcher --dest dist --app-version 1.1.1 --vendor "Automated Flight Team" `

**Windows**
`mvn clean package && jpackage --type exe --name DroneView --input target --main-jar droneview-0.1.0-jar-with-dependencies.jar --main-class com.ui.Launcher --dest dist --win-menu --win-shortcut --app-version 1.1.0 --vendor "Autonmated Flight Team"`

**Linux (Debian/Ubuntu)**
`mvn clean package && jpackage --type deb --name droneview --input target --main-jar droneview-0.1.0-jar-with-dependencies.jar --main-class com.ui.Launcher --dest dist --app-version 0.1.0 --vendor "Automated Flight Team"`

# Uninstalling

**Mac** 
Delete the .app file from your applications.

**Windows**
Uninstall the program from the Windows Uninstall Programs settings menu, or through the control panel, or so on.

**Linux**
Run `sudo apt remove DroneView` or a similar command for whatever package manager you use.