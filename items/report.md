# [G04 - Dogs] Report

## Table of Contents

1. [Team Members and Roles](#team-members-and-roles)
2. [Summary of Individual Contributions](#summary-of-individual-contributions)
3. [Application Description](#application-description)
4. [Application UML](#application-uml)
5. [Application Design and Decisions](#application-design-and-decisions)
6. [Summary of Known Errors and Bugs](#summary-of-known-errors-and-bugs)
7. [Testing Summary](#testing-summary)
8. [Implemented Features](#implemented-features)
9. [Team Meetings](#team-meetings)
10. [Conflict Resolution Protocol](#conflict-resolution-protocol)

## Administrative

- Firebase Repository Link: <"https://console.firebase.google.com/u/1/project/go4gp-s2/overview">
   - Confirm: [x] I have already added comp21006442@gmail.com as a Editor to the Firebase project prior to due date.
- Two user accounts for markers' access are usable on the app's APK:
   - Username: comp2100@anu.edu.au	Password: comp2100 [X] 
   - Username: comp6442@anu.edu.au	Password: comp6442 [x]

## Team Members and Roles
The key area(s) of responsibilities for each member

| UID   |  Name  |                                                    Role |
|:------|:------:|--------------------------------------------------------:|
| [u7327620] | [Ryan Foote] |          [Firebase, Login, GPS, Code Refactors/Reviews] |
| [u7902000] | [Gea Linggar Galih]| [AirQualityApi, Data Storage, Data Structures, Backend] |
| [u8003980] | [Cheng Leong Chan] |              [UI design in Figma, Search, Profile Page] |
| [u8006862] | [Hexuan Wang] |   [UI design/layout, UI interface (UI Graphical), Logo] |
| [u7635535] | [Zechuan Liu] |               [Overall Functionality, Search Designate] |


## Summary of Individual Contributions

### Template for Team Members
1. **UID, NAME** I have contributed 20%: <br>
   - **Outcome Impact (i.e. Location Services, Search-Designate)**
      - Feature "Request Permissions" - class x: [gps.java](/App/app/src/main/java/com/go4/utils/GPSService.java)
      - Design pattern - class y: [design](https://google.com)<br><br>

  - **Code and App Design** 
    - [What design patterns, data structures, did the involved member propose?]*
    - [UI Design. Specify what design did the involved member propose? What tools were used for the design?]* <br><br>

  - **Others**: (only if significant and significantly different from an "average contribution") 
    - [Report Writing?] [Slides preparation?]*
    - [You are welcome to provide anything that you consider as a contribution to the project or team.] e.g., APK, setups, firebase* <br><br>

2. **u8003980, Chan Cheng Leong** I have 20% contribution, as follows: <br>
- **Code Contribution in the final App**
    - Feature Change Profile Picture in Profile Page - class ProfileActivity: [profileActivity.java](../App/app/src/main/java/com/go4/application/profile/ProfileActivity.java)
    - Feature Display Pinned Suburbs in Profile Page – class SuburbCardViewAdapter: [SuburbCardViewAdapter.java](../App/app/src/main/java/com/go4/application/profile/SuburbCardViewAdapter.java),
    class SuburbCard: [SuburbCard.java](../App/app/src/main/java/com/go4/application/profile/SuburbCard.java)
    - Feature Parse Search Bar input in Historical Data Page – method parseSearchBarInput(): [SuburbHistoricalActivity.java](../App/app/src/main/java/com/go4/application/historical/SuburbHistoricalActivity.java),
    class Parser: [Parser.java](../App/app/src/main/java/com/go4/utils/tokenizer_parser/Parser.java),
    class Tokenizer: [Tokenizer.java](../App/app/src/main/java/com/go4/utils/tokenizer_parser/Tokenizer.java),
    class Token: [Token.java](../App/app/src/main/java/com/go4/application/profile/Token.java)
    - UI Profile Page - [activity_profile.xml](../App/app/src/main/res/layout/activity_profile.xml)
    - UI Pinned Suburb Card - [activity_profile_card.xml](../App/app/src/main/res/layout/activity_profile_card.xml)

- **Code and App Design**
    - UI Design: designed UI for Profile Page and Suburb Live Data Page with Figma

- **Others**: (only if significant and significantly different from an "average contribution")
    - [Report Writing?] [Slides preparation?]*
    - [You are welcome to provide anything that you consider as a contribution to the project or team.] e.g., APK, setups, firebase* <br><br>




## Application Description

*[What is your application, what does it do? Include photos or diagrams if necessary]*

Our app is called "EcoACT", which aims to provide users with real-time air quality information for communities in the Canberra area. This app allows users to view the air quality index (AQI) of different areas on a map, so as to understand the current air conditions in the environment and make better life decisions.

The main features of the app include the following:

Real-time air quality monitoring: Users can view the real-time air quality of various areas in Canberra on a map.

Regional search: Users can quickly view the air quality of an area by entering the name of the area.

Personalized collection: Users can collect frequently visited areas so that they can check the air quality of these places at any time.

Historical data analysis: The app also provides historical data on air quality to help users understand the changing trends of air quality in a certain area.

For example, if you live in the Bruce area and want to know whether it is suitable for outdoor activities today, you can open the app and view the air quality index of the Bruce area. In addition, the app will mark the location of nearby public facilities, such as parks, hospitals, etc., to help users make more appropriate living arrangements.

### Problem Statement

*[Problem statement that defines the purpose of your App]*
Air pollution is a growing concern for urban residents, especially for people with sensitive respiratory systems, such as the elderly and those with diseases such as asthma. Changes in air quality have a direct impact on the health of these people, and meteorological conditions, vehicle exhaust emissions, construction, etc. can affect the fluctuation of air quality.

However, many people lack sufficient understanding of the changes in the air quality of the environment they live in, especially in special circumstances such as fire season and sandstorms, when air quality can change dramatically. At this time, users need to understand the air conditions around them in order to decide whether they should engage in outdoor activities or take necessary precautions.

Therefore, the goal of the EcoACT application is to provide users with accurate and real-time air quality information, allowing them to make informed decisions in their lives and better protect the health of themselves and their families. This is especially important for commuters, runners, the elderly and parents of children.


### Application Use Cases and/or Examples

Our application is mainly aimed at Canberra residents, especially those who are sensitive to air quality and need to know the air conditions. Here are some specific application scenarios:
Mr. Zhang is a running enthusiast. He likes to exercise along the running route near his home every morning. However, the air quality in Canberra has fluctuated recently, which makes Mr. Zhang a little worried about the impact of going out for a run on his health.

1. Mr. Zhang noticed that the sky was a little gloomy in the morning
2. So he opened the "EcoACT" app to check the air quality index in his Bruce area. He found that the air quality index (AQI) showed moderate pollution, which means that the concentration of particulate matter in the air is high and not very friendly to sensitive people.

3. In order to confirm the specific situation, Mr. Zhang searched for the surrounding air quality in the app and checked the historical data of the past few days. He found that the air quality has declined in recent days due to construction of nearby roads.

4. Mr. Zhang decided not to run along the outdoor running route today, but chose to go to the gym to complete the day's exercise on the treadmill.

5. Mr. Zhang also collected Bruce and several running places he often goes to, so that he can check the air quality conditions in these areas at any time, so as to make the best running plan in the next few days.

*List all the use cases in text descriptions or create use case diagrams. Please refer to https://www.visual-paradigm.com/guide/uml-unified-modeling-language/what-is-use-case-diagram/ for use case diagram.*

<hr> 

### Application UML

![ClassDiagramExample](media/_examples/ClassDiagramExample.png) <br>
*[Replace the above with a class diagram. You can look at how we have linked an image here as an example of how you can do it too.]*

<hr>

## Code Design and Decisions

This is an important section of your report and should include all technical decisions made. Well-written justifications will increase your marks for both the report as well as for the relevant parts (e.g., data structure). This includes, for example,

- Details about the parser (describe the formal grammar and language used)

- Decisions made (e.g., explain why you chose one or another data structure, why you used a specific data model, etc.)

-  ⁠Details about the design patterns used (where in the code, justification of the choice, etc) concise, crucial reasons of your design.

*Please give clear and concise descriptions for each subsections of this part. It would be better to list all the concrete items for each subsection and give no more than 5



<hr>

### Data Structures

*[What data structures did your team utilise? Where and why?]*

Here is a partial (short) example for the subsection `Data Structures`:*

*I used the following data structures in my project:*

1. LinkedList

Objective: Used for storing historical air quality data for the historical view feature.

Code Locations: Defined in DataFetcher class, methods fetchHistoricalData, storeData and SuburbHistoricalActivity class, lines 45-78; processed using addData and getHistory.

Reasons:
It is more efficient than ArrayList for insertion with a time complexity of O(1).
We don't need to access the item by index for the historical feature because data is processed sequentially for each time period.
For historical air quality data, LinkedList is well-suited due to the sequential nature of the data flow and frequent additions.

2. HashMap

Objective: Used for storing the mapping between suburb names and corresponding air quality data for the live data feature.

Code Locations: Defined in SuburbLiveActivity class, methods fetchLiveData, getSuburbData, lines 60-120; processed using put and get methods.

Reasons:
HashMap provides O(1) average-time complexity for lookups, making it ideal for fetching data quickly based on suburb names.
We need to frequently access the air quality data for each suburb without iterating over all entries.
The data structure ensures fast retrieval of data, which is crucial for displaying live air quality information to users in real time.

3.  ArrayList

Objective: Used for storing the list of favorite suburbs for the user's personalized view feature.

Code Locations: Defined in MainActivity class, methods addFavorite, removeFavorite, lines 110-140; processed using add, remove, and get.

Reasons:
ArrayList provides O(1) access time for indexed data, allowing quick retrieval of favorite suburbs.
We don't need to perform frequent insertions/deletions other than at the end, making ArrayList a suitable choice.
For storing a small list of favorite suburbs that users can update, ArrayList provides simple and efficient management.

4.AVL Tree
Objective: Used for storing real-time air quality data to maintain balanced and efficient lookup and insertion operations.

Code Locations: Defined in AVLTree.java class, methods insert, delete, balance, lines 10-150; processed using insertNode and rebalance.

Reasons:
AVL Tree maintains balance through rotations, ensuring O(log n) time complexity for insertions, deletions, and lookups.
We need to maintain a sorted structure with fast access to the data without compromising efficiency during updates.
For real-time air quality monitoring, AVL Tree helps in ensuring that newly incoming data is organized, allowing efficient retrieval and display of data.

5.TreeMap

Objective: Used for storing air quality data sorted by timestamp for the historical data viewing feature.

Code Locations: Defined in SuburbHistoricalActivity class, methods storeHistoricalData, queryDateRange, lines 130-200; processed using put and subMap.

Reasons:
TreeMap maintains sorted keys, which makes it easy to handle date-based data for historical queries.
We need to frequently access air quality data in a sorted order to facilitate date-range queries.
It ensures that the data is always sorted by timestamp, making it easy to visualize historical trends in air quality.

<hr>

### Design Patterns
*[What design patterns did your team utilise? Where and why?]*

1. Factory Pattern

Objective: Used for creating instances of different data parsers (e.g., JSON parser, CSV parser) based on the input data type.

Code Locations: Defined in ParserFactory.java class, methods createParser, getParserType, lines 10-70; processed using createParser in DataFetcher.

Reasons:
We need to create specific parser objects depending on the data format.
Factory Pattern allows encapsulating the logic of object creation, promoting loose coupling by delegating instantiation responsibility.
It provides flexibility for easily extending the system to support new data formats by adding new parser classes without modifying the existing codebase.

2. Data Access Object (DAO) Pattern

Objective: Used for abstracting and encapsulating all access to the application's data source, providing a simple API for querying and updating data.

Code Locations: Defined in DataAccessObject.java class, methods getData, insertData, updateData, lines 20-85; processed using getConnection and executeQuery.

Reasons:
We need to separate the low-level data accessing operations from the higher-level business logic, ensuring clear and maintainable code.
DAO Pattern provides a clear interface for interacting with the data source, minimizing code duplication and ensuring all data-related operations are performed consistently.
It improves code readability and makes it easier to change or replace the data source without affecting the rest of the application, promoting a modular architecture.

3. Singleton Pattern

Objective: Used for managing a single instance of the data access object (DAO) to ensure efficient resource management and centralized database access.

Code Locations: Defined in DataAccessObject.java class, methods getInstance, getConnection, lines 15-55; processed using getInstance to ensure a single object is created.

Reasons:
We need to manage a single instance of the DAO for accessing the database throughout the application, avoiding multiple connections and potential conflicts.
Singleton Pattern ensures there is only one instance of the DAO, minimizing overhead and ensuring consistency in data access.
It provides a global point of access to the database connection, making it easier to manage and reuse the same instance.

<hr>

### Parser

### <u>Grammar(s)</u>
*[How do you design the grammar? What are the advantages of your designs?]*
Our grammar is designed to parse various types of data input related to air quality and suburb information, ensuring flexibility and adaptability for different formats such as JSON and CSV. By allowing key components like &lt;Location&gt;, &lt;Date&gt;, and &lt;Time&gt; to be interchangeable, the grammar design provides robustness to handle diverse data orders.

Advantages:

Flexibility: The grammar allows the tokens &lt;Location&gt;, &lt;Date&gt;, and &lt;Time&gt; to be in any order, making it adaptable to different input styles, which improves user experience.

Error Handling: This design allows the parser to still function even if the input data is provided in different sequences or contains invalid tokens, as long as the necessary tokens are provided.

Production Rules:

    <Input> ::= <Location> <Date> <Time> | <Location> <Time> <Date> | <Date> <Location> <Time> | ...

    <Location> ::= “Acton” | “Ainslie” | … | "Yarralumla"

    <Date> ::= <Year> <Month> <Day>

    <Year> ::= [0-9]{4}

    <Month> ::= "Jan" | "January" | "Feb" | "February" | ... | "Dec" | "December"

    <Day> ::= [0-3][0-9]

    <Time> ::= <Hour24>:<Minute> | <Hour12><Ampm>

    <Hour24> ::= "00" | "01" | ... | "23"

    <Minute> ::= "00" | "01" | ... | "59"

    <Hour12> ::= "1" | "2" | ... | "12"

    <Ampm> ::= "am" | "pm"

### <u>Tokenizers and Parsers</u>

*[Where do you use tokenisers and parsers? How are they built? What are the advantages of the designs?]*

The tokenizers and parsers are used primarily in the Suburb Historical Data Page, in the class  SuburbHistoricalActivity in [SuburbHistoricalActivity.java](../App/app/src/main/java/com/go4/application/historical/SuburbHistoricalActivity.java) lines 192-230.
They are built by taking inspiration from Lab 06, and three classes are used, including  Parser ([Parser.java](../App/app/src/main/java/com/go4/utils/tokenizer_parser/Parser.java)), Tokenizer ([Tokenizer.java](../App/app/src/main/java/com/go4/utils/tokenizer_parser/Tokenizer.java)) and Token ([Token.java](../App/app/src/main/java/com/go4/application/profile/Token.java)).
Tokenizers: The Tokenizer class uses regular expressions to split the input data into discrete tokens. These tokens are then classified as &lt;Location&gt;, &lt;Date&gt;, &lt;Time&gt;, making the input easier to process.
Parsers: The Parser class takes these tokens and applies the grammar rules to construct the data model used in the application. The parser uses predefined grammar rules to validate and organize the tokens into meaningful structures.

Advantages of the Designs:

Modularity: The separation between tokenizing and parsing tasks makes the system modular, simplifying maintenance and allowing for easy updates.

Adaptability: By using a flexible, the system can handle different input sequences and formats, improving the user experience when working with varied data sources.

Efficiency: Breaking data into tokens before parsing reduces complexity, leading to more efficient data handling. This design ensures that each step of data processing is straightforward and optimized.

<hr>

### Others

*[What other design decisions have you made which you feel are relevant? Feel free to separate these into their own subheadings.]*


<br>
<hr>

## Implemented Features
*[What features have you implemented? where, how, and why?]* <br>
*List all features you have completed in their separate categories with their featureId. THe features must be one of the basic/custom features, or an approved feature from Voice Four Feature.*

### Basic Features
1. [LogIn]. Description of the feature he app must support user login functionality. User sign-up is not required. (easy)
Important: You must include the following two accounts for markers' access to your App:

Username: comp2100@anu.edu.au Password: comp2100

Username: comp6442@anu.edu.au Password: comp6442
   * Code:[`FirebaseLoginActivity`](../App/app/src/main/java/com/go4/application/FirebaseLoginActivity.java?ref_type=heads), methods `onCreate`, `loginUser` and `LoginActivity`, methods `validateUserInput`.
   * Description of feature: The login feature provides a secure way for users to access the app by entering their username and password. It validates user credentials through Firebase.<br>
   * Description of your implementation:Implemented using Firebase Authentication in `FirebaseLoginActivity`. The `onCreate` method sets up listeners for UI elements, and `loginUser` handles the Firebase authentication process. UI components such as `EditText` for entering credentials and a `Button` for submitting are defined in the XML file [`activity_firebase_login_ui.xml`](133†source), enabling interaction with these methods to ensure a smooth login experience. Proper error messages are provided when login fails to guide users effectively. <br>

2. [DataFiles]. Description he app must use a data set (which you may create) where each entry represents a meaningful piece of information relevant to the app. The data set must be represented and stored in a structured format as taught in the course. It must contain at least 2,500 valid instances. (easy)

Important: You must include in your report the link to the data file(s) on your project's GitLab repository and/or the link to your Firebase repository. In the latter case, you must add comp21006442@gmail.com as an Editor in your Firebase repository and mention this in your report.
   * Code to the Data File [`canberra_suburbs.json`](tFpOJ4pms32UhUzrfL4XFSK6), [`canberra_suburbs_coordinates.json`](SANICmkjDFxw6rcZdjZE65ur).
   * Link to the Firebase repo: ...

3. [LoadShowData] The app must load and display data instances from the data set. Data must be retrieved from either a local file (e.g., JSON, XML) or Firebase. (easy)
   <br>
* Description of feature:  The application loads and displays air quality data instances from the data set. Data must be retrieved from local files like JSON or via Firebase if available.
 * Description of your implementation: Data loading is performed by the `DataFetcher.java` class, which reads from `canberra_suburbs.json` and `canberra_suburbs_coordinates.json`. Data is displayed using a RecyclerView, managed in the `SuburbLiveActivity` activity with UI elements like `RecyclerView` and progress bars for real-time air quality data.
4. [[DataStream] The app must simulate user interactions through data streams. These data streams must be used to feed the app so that when a user is logged in (or enters a specific activity), the data is loaded at regular time intervals and the app is updated automatically.  (medium)
   * Code: [`SuburbLiveActivity`](../App/app/src/main/java/com/go4/application/SuburbLiveActivity.java?ref_type=heads), methods `onCreate`, `refreshData`, and [`DataFetcher`](../App/app/src/main/java/com/go4/application/DataFetcher.java), methods `fetchData`....
   * Description of feature: This feature simulates user interactions by loading data streams periodically, allowing the app to update automatically when the user logs in or enters specific activities. <br>
   * Description of your implementation:Implemented using `DataFetcher.java` to retrieve data at regular time intervals. The data is displayed in the `SuburbLiveActivity` class, with UI elements like `SwipeRefreshLayout` in [`suburb_live.xml`](132†source) to ensure the app content is automatically refreshed, simulating live updates. <br>
5. [Search] The app must allow users to search for information. Based on the user's input, adhering to pre-defined grammar(s), a query processor must interpret the input and retrieve relevant information matching the user's query. The implementation of this functionality should align with the app’s theme. The application must incorporate a tokenizer and parser utilizing a formal grammar created specifically for this purpose. (medium)
   * Code: [`Tokenizer`](../App/app/src/main/java/com/go4/application/Tokenizer.java?ref_type=heads), methods `tokenizeInput`, [`Parser`](../App/app/src/main/java/com/go4/application/Parser.java), methods `parseQuery`, and `SearchRecord`.
   * Description of feature: Allows users to search for suburb-specific information based on their input, adhering to a pre-defined grammar.<br>
   * Description of your implementation:The search functionality is implemented using a tokenizer (`Tokenizer.java`) to break down the user's input and a parser (`Parser.java`) to interpret it according to the grammar. The results are then displayed using a RecyclerView, making the search results interactive and easy to browse.
 <br>
6. [UIUX] The app must maintain a consistent design language throughout, including colors, fonts, and UI element styles, to provide a cohesive user experience. The app must also handle orientation changes (portrait to landscape and vice versa) gracefully, ensuring that the layout adjusts appropriately. (easy)
 * Code: XML layouts like [`activity_main.xml`](../App/app/src/main/res/layout/activity_main.xml?ref_type=heads), [`suburb_live.xml`](../App/app/src/main/res/layout/suburb_live.xml), [`activity_profile.xml`](../App/app/src/main/res/layout/activity_profile.xml).
   * Description of feature: The application maintains a consistent design language, including colors, fonts, and UI styles throughout all activities and screens. It also handles orientation changes gracefully.<br>
   * Description of your implementation:The UI is implemented using `ConstraintLayout` and `LinearLayout` for flexibility. Consistent colors and styles are used across XML files, and UI elements like `RecyclerView` and `Spinner` adapt to different screen orientations. The application supports responsive layouts that adjust automatically between portrait and landscape modes.

 <br>
7. [UIFeedback] The UI must provide clear and informative feedback for user actions, including error messages to guide users effectively. (easy)
 * Code: [`SuburbLiveActivity`](../App/app/src/main/java/com/go4/application/SuburbLiveActivity.java?ref_type=heads), [`DataFetcher`](../App/app/src/main/java/com/go4/application/DataFetcher.java?ref_type=heads), methods `onDataLoadFailure`, `onDataLoadSuccess`.
   * Description of feature:  Provides informative feedback for user actions, including data loading progress and error messages. <br>
   * Description of your implementation:A `ProgressBar` in [`layout_refresh_footer.xml`](../App/app/src/main/res/layout/layout_refresh_footer.xml?ref_type=heads) is used to indicate loading states, and error messages are displayed using `Toast` notifications. When a data fetch fails, users are informed with a clear error message to guide them on the next steps. The `SwipeRefreshLayout` is used for visual feedback when users refresh the page manually. <br>



### Custom Features
Feature Category: Search <br>
1. [Search-Filter]. The app must provide functionality to sort and filter a list of items returned from searches using appropriate UI components. (easy)
   * Code: [Class X, methods Z, Y](https://gitlab.cecs.anu.edu.au/comp2100/group-project/ga-23s2/-/blob/main/items/media/_examples/Dummy.java#L22-43) and Class Y, ...
   * Description of your implementation: ... <br>
     <br>

2. [Search-Designate]. The app must rank search results based on the status of the users. For example, a user may have multiple roles within the app, which should result in different ranked lists of results. (medium)
* Code: [Class X, methods Z, Y](https://gitlab.cecs.anu.edu.au/comp2100/group-project/ga-23s2/-/blob/main/items/media/_examples/Dummy.java#L22-43) and Class Y, ...
   * Description of your implementation: ... <br>
     <br>

Feature Category: Data <br>
3. [Data-GPS] The app must utilize GPS information based on location data. Hint: see the demo presented by our tutors on ECHO360. (easy)
   * Code: [Class X, entire file](https://gitlab.cecs.anu.edu.au/comp2100/group-project/ga-23s2/-/blob/main/items/media/_examples/Dummy.java#L22-43) and Class Y, ...
   * [Class B](../src/path/to/class/file.java#L30-85): methods A, B, C, lines of code: 30 to 85
   * Description of your implementation: ... <br>
4. [Data-Graphical] The app must include a graphical report viewer that displays a report with useful data from the app. No marks will be awarded if the report is not graphical. (hard)
   * Code: [Class X, entire file](https://gitlab.cecs.anu.edu.au/comp2100/group-project/ga-23s2/-/blob/main/items/media/_examples/Dummy.java#L22-43) and Class Y, ...
   * [Class B](../src/path/to/class/file.java#L30-85): methods A, B, C, lines of code: 30 to 85
   * Description of your implementation: ... <br>
5. [Data-Formats] The app must read data from local files in at least two different formats, such as JSON, XML, etc. (easy)
   * Code: [Class X, entire file](https://gitlab.cecs.anu.edu.au/comp2100/group-project/ga-23s2/-/blob/main/items/media/_examples/Dummy.java#L22-43) and Class Y, ...
   * [Class B](../src/path/to/class/file.java#L30-85): methods A, B, C, lines of code: 30 to 85
   * Description of your implementation: ... <br>
6. [Data-Profile] The app must include a Profile Page for users (or any relevant entity within your app’s theme) that displays a media file, such as an image, animation (e.g., GIF), or video. (easy)
   * Code: [Class ProfileActivity, entire file](../App/app/src/main/java/com/go4/application/historical/SuburbHistoricalActivity.java) and [activity_profile.xml](../App/app/src/main/res/layout/activity_login.xml)
   * [Class ProfileActivity](../App/app/src/main/java/com/go4/application/historical/SuburbHistoricalActivity.java#L162-217): methods editableProfilePicture, getFilePath, readProfilePicture, writeProfilePicture, lines of code: 162-217
   * Description of your implementation: Our app contains a profile page, which is shown when the user successfully logs in our app. 
     The Profile Page displays user email, a profile picture that can be changed by clicking and selecting a picture from your phone. 
     The profile page is stored in internal storage, and read whenever the activity is created. A default profile picture is shown if the user has not uploaded a profile picture. 
     The page also contains a LOGOUT button.

Feature Category: UI Design and Testing <br>
7. [UI-Layout]  The app must incorporate appropriate layout adjustments for UI components to support both portrait and landscape orientations, as well as various screen sizes. This requirement is in addition to the [UXUI] basic feature and necessitates the implementation of new layouts for each orientation and screen size. (easy)
   * Code: [Class X, entire file](https://gitlab.cecs.anu.edu.au/comp2100/group-project/ga-23s2/-/blob/main/items/media/_examples/Dummy.java#L22-43) and Class Y, ...
   * [Class B](../src/path/to/class/file.java#L30-85): methods A, B, C, lines of code: 30 to 85

Feature Cateory: User Interactivity
8. [Interact-Micro] The app must provide the ability to micro-interact with items or users (e.g., like, block, connect to another user) with interactions stored in-memory. (easy)
   * Code: [Class ProfileActivity, entire file](../App/app/src/main/java/com/go4/application/historical/SuburbHistoricalActivity.java), 
   [Class SuburbCardViewAdapter, entire file](../App/app/src/main/java/com/go4/application/profile/SuburbCardViewAdapter.java),
   [Class SuburbCard, entire file](../App/app/src/main/java/com/go4/application/profile/SuburbCard.java)
   [activity_profile.xml](../App/app/src/main/res/layout/activity_profile.xml) 
   and [activity_profile_card.xml](../App/app/src/main/res/layout/activity_profile_card.xml)
   * [Class ProfileActivity](../App/app/src/main/java/com/go4/application/historical/SuburbHistoricalActivity.java#L224-365): 
   methods displayPinnedSuburbCards, searchForQualityAndPm10Number, readPinnedSuburbs, writePinnedSuburbs, 
   updatePinnedSuburbs, addButtonOnClick, addSuburbCard,  lines of code: 224-365
   * Description of your implementation: The Profile Page displays a list of the user's pinned suburbs as cards, which contains an editable label, the suburb name and the PM10 Number.
   It also indicates whether the air quality in that suburb is "Good", "Moderate" or "Bad", based on the air quality index. 
   If it is "Good", the card will have a green background colour, and the image next to the text "Good" is a smiley face.
   If it is "Moderate", the card will have a yellow background colour, and the image next to the text "Moderate" is a meh face.
   If it is "Bad", the card will have an orange background colour, and the image next to the text "Bad" is a meh face.
   Whenever a suburb is added with the ADD button or deleted by swiping right, or when an label is updated, the method writePinnedSuburbs is called to store the details of the pinned suburbs in internal storage, in a file titled "pinned_suburbs.txt".
   The pinned suburbs are displayed using the class SuburbCardViewAdapter and RecyclerView, and the class SuburbCard is used to store and return the data of each card.
   The data of the pinned suburbs are updated every 15 minutes using the updatePinnedSuburbs function.

### Surprise Feature

*Instructions:*
- If implemented, explain how your solution addresses the task (any detail requirements will be released with the surprise feature specifications).
- State that "Surprise feature is not implemented" otherwise.

<br> <hr>


## Testing Summary

*[What features have you tested? What is your testing coverage?]*
*Please provide details (see rubrics) including some screenshots of your testing summary, showing the achieved testing coverage. Feel free to provide further details on your tests.*

*Here is an example:*

1. Tests for Search
   - Code: [TokenizerTest Class, entire file](https://gitlab.cecs.anu.edu.au/comp2100/group-project/ga-23s2/-/blob/main/items/media/_examples/Dummy.java) for the [Tokenizer Class, entire file](https://gitlab.cecs.anu.edu.au/comp2100/group-project/ga-23s2/-/blob/main/items/media/_examples/Dummy.java#L22-43)
   - *Number of test cases: ...*
   - *Code coverage: ...*
   - *Types of tests created and descriptions: ...*

2. xxx

...

<br> <hr>



## Summary of Known Errors and Bugs

*[Where are the known errors and bugs? What consequences might they lead to?]*
*List all the known errors and bugs here. If we find bugs/errors that your team does not know of, it shows that your testing is not thorough.*

*Here is an example:*

1. *Bug 1:*
    - *A space bar (' ') in the sign in email will crash the application.*
    - ...

2. *Bug 2:*
3. ...

<br> <hr>


## Team Management

### Meeting Minutes
* Link to the minutes of your meetings like above. There must be at least 4 team meetings.
  (each committed within 2 days after the meeting)
* Your meetings should also have a reasonable date spanning across Week 6 to 11.*


- [Team Meeting 1](meeting-1.md)
- [Team Meeting 2](meeting-2.md)
- [Team Meeting 3](meeting-3.md)
- [Team Meeting 4](meeting-4.md)


<hr>

### Conflict Resolution Protocol
*[Write a well defined protocol your team can use to handle conflicts. That is, if your group has problems, what is the procedure for reaching consensus or solving a problem?
(If you choose to make this an external document, link to it here)]*

*If your group has issues, how will your group reach consensus or solve the problem?*
*- e.g., if a member gets sick, what is the solution? Alternatively, what is your plan to mitigate the impact of unforeseen incidents for this 6-to-8-week project?*

This shall include an agreed procedure for situations including (but not limited to):
- A member is sick in the final week of the project.
- A member didn't complete the assigned task which should've been completed before the checkpoint, and the checkpoint is approaching.
- A member is unreachable (didn't respond messages in your agreed communication channels and emails in two days).
- The team have different understandings toward the requirement of the assignment.


