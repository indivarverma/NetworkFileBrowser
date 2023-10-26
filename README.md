# Network File Browser

## File Browser application to display directory structure
https://drive.google.com/file/d/1XwPG9XHu60DPG_pUmPVoWLqifjFI3tn3/view


### Acceptance Criteria

1. Connect to Server
2. Provide File Hierarchy navigation
   3 Display Full Screen Image

### Additional Requirements
- Create Folder (Achieved)
- Delete Folder (Achieved)
- Login Page (Achieved)
- Upload Local File (Could not  reach)
- Also implemented: Logout

### Feature Design Considerations
  **Flash Screen**

  At the time of launch, the app can not decide whether to launch the login activity or the  File listing activity before reading the saved user profile data. As this read is a quick call and I wanted the Login Screen or Directory Listings Screen to open in their own Koin context at launch, I introduced a Flash Screen to make this decision and launch the correct screen.

  **Login Screen**
- In login screen, hide proceed button till user enters minimum characters for userid & password.
- Keep password hidden (*** format)as user types, but provide reveal button to see password if user wants.
- Save user profile(username, user id, encrypted token & NOT password, top level directory info) to local preferences after login
- show error messages and retry option on failed login
- take user to Directory listings screen on successful login (getme call)

  **Directory Listings screen**
- contains list of directories and files in a folder.
- shows user profile on top action bar, along with relevant action buttons
- as the user can go multi level deep, should not create several activities. stay in same activity but update content.
- pressing back on navigation bar takes user back to parent directory / close app if user already on top level directory
- as it is hard for user to know the current directory, provide path view on top of the screen that displays file heirarchy to the current directory.
- If network fetch for directory content fails, show error messages and retry option on file heirarchy fetch
- Should not be able to delete top level folder (so no delete icon in action bar for top level directory)
- ask confirmation dialog if user tries to delete a folder.
- take user to parent folder on delete folder
- show error message if delete folder fails
- on Create folder click, display alert message with Edit textfield to enter new folder name
- display new folder in list after create folder
- show error message if create folder fails (with error sent from server)
- Provide logout option which clears user profile and takes user to login page.
  
  **Image Screen**
- User should be able to zoom on the image.
- User should be able to pan the image but the image should stay within the bounds of the device screen

### App Design Considerations

As the data of the screen changes quite frequenty as the user navigates the file heirarchy, I have used a version of MVVM that maintains the state of the app/screen at any given time.
In case of a state update  (reduce) call, the code has access to the previous state object and can modify that object to get to the next possible state.
I have used Koin dependency inject in this project as I am quite familiar with it. Dagger Hilt would also be a good choice.
I have also used RxKotlin here, as it can contain different user actions quite neatly and translate them well to new screen state (using the mvvm framework)
All the business logic has been obfuscated behind the ViewModelDelegates (one for each screen) that keeps it totally separate from the UI screens and makes it more testable.
As the ViewModelDelegates contain the core of the functionality , I have focussed my testing effots on them. I have used Mockk library to mock the objects used in the delegates for mocking behaviour as well as "spying" on the method calls to these objects.
Note  that I have used Coordinators/Navigators for navigation from one activity to next (one coordinator/navigator per activity) . If you check Navigators, they contain activity reference and are injected via factories so that each navigator has access to the latest instance of the activity (after possible configuration changes. Additionally, I have shared common navigation functionality between the navigators.

### Debugging tools used

Please note that i have integrated Facebook's flipper plugin here for easier debugging and dev work.

### Libraries used: 
Jetpack Compose, RxKotlin, Mockk, Flipper, Moshi, Retrofit, OkHttp3, Gson, Coil, Joda DateTime
