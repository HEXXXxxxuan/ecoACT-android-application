# [G04 - EcoACT] Report

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

| UID   |  Name  |                                                                 Role |
|:------|:------:|---------------------------------------------------------------------:|
| [u7327620] | [Ryan Foote] |                                 [Firebase, Login, GPS, Integrations] |
| [u7902000] | [Gea Linggar Galih]| [Data Fetching and Processing, Backend, Data Graphical, Code Review] |
| [u8003980] | [Cheng Leong Chan] |                    [UI design, Search, Profile Page, Interact-Micro] |
| [u8006862] | [Hexuan Wang] |           [UI design/layout, UI interface (UI Graphical), Logo, UML] |
| [u7635535] | [Zechuan Liu] |                  [Overall Functionality, Search Designate] |


## Summary of Individual Contributions

**u8003980, Chan Cheng Leong** I have 20% contribution, as follows:
- **Code Contribution in the final App**
    - Feature Change Profile Picture in Profile Page
        - [ProfileActivity](/App/app/src/main/java/com/go4/application/profile/ProfileActivity.java) class
    - Feature Display Pinned Suburb Cards in Profile Page
        - [ProfileActivity](/App/app/src/main/java/com/go4/application/profile/ProfileActivity.java)
        - [SuburbCardViewAdapter](/App/app/src/main/java/com/go4/application/profile/SuburbCardViewAdapter.java) class
        - [SuburbCard](/App/app/src/main/java/com/go4/application/profile/SuburbCard.java) class
    - Feature Parse Search Bar input in Suburb Historical Data Page:
        - SuburbHistoricalActivity class [parseSearchBarInput](/App/app/src/main/java/com/go4/application/historical/SuburbHistoricalActivity.java#L243-282) method, lines of code 243-282
        - [Parser](/App/app/src/main/java/com/go4/utils/tokenizer_parser/Parser.java) class
        - [Tokenizer](/App/app/src/main/java/com/go4/utils/tokenizer_parser/Tokenizer.java) class
        - [Token](/App/app/src/main/java/com/go4/application/profile/Token.java) class
        - [SearchRecord](/App/app/src/main/java/com/go4/application/historical/SearchRecord.java) class
    - Profile Page UI - [activity_profile.xml](/App/app/src/main/res/layout/activity_profile.xml)
    - Pinned Suburb Card UI - [activity_profile_card.xml](/App/app/src/main/res/layout/activity_profile_card.xml)
    - Bottom Navigation Bar UI - [suburb_live.xml, lines of code 390-449](/App/app/src/main/res/layout/suburb_live.xml#L390-449)

- **Code and App Design**
    - [UI Design](media/_examples/UI_Design.jpg): Used Figma to design UI for Profile Page and Suburb Live Data Page with Hexuan Wang.

- **Others**:
    - Recorded and Edited [features.mp4](features.mp4)
    - Constructed [1/4 Meeting Minutes](meeting-4.md)
    - [Meeting notes](meeting-3-document.jpg)

***

**u7635535, Zechuan Liu** I have 20% contribution, as follows:
- **Code Contribution in the final App**
    - Feature "selected nearest suburb" - class NearestSuburbStrategy: [NearestSuburbStrategy.java](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/blob/main/App/app/src/main/java/com/go4/application/live_data/NearestSuburbStrategy.java)
    - Feature Display pinned Search result on live page – class SuburbLiveActivity:[SuburbLiveActivity.java](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/blob/main/App/app/src/main/java/com/go4/application/live_data/SuburbLiveActivity.java)
    - Feature Search Designate by distances – class LoadMoreSearchResultAdapter: [LoadMoreSearchResultAdapter.java](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/blob/main/App/app/src/main/java/com/go4/application/live_data/adapter/LoadMoreSearchResultAdapter.java)
      class SearchResultEndlessRecyclerOnScrollListener: [SearchResultEndlessRecyclerOnScrollListener.java](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/blob/main/App/app/src/main/java/com/go4/application/live_data/listener/SearchResultEndlessRecyclerOnScrollListener.java),

- **Code and App Design**
    - Search: Designed how to meet the requirements for designate.
    - First version login: Designed the login page without firebase.

***

**u8006862, Hexuan Wang** I have contributed 20%:
- **Code Contribution in the final App**
    - Feature Data Graphical(air quality displays) in [SuburbHistoricalActivity](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/blob/main/App/app/src/main/java/com/go4/application/historical/SuburbHistoricalActivity.java) [SuburbLiveActivity](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/blob/main/App/app/src/main/java/com/go4/application/live_data/SuburbLiveActivity.java) and UI Layout in both activities
    - Feature Navigation Bar for the app in [Main Activity](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/blob/main/App/app/src/main/java/com/go4/application/MainActivity.java)and related XML.
    - Feature created original [SplashActivity](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/blob/main/App/app/src/main/java/com/go4/application/SplashActivity.java) and its UI.
    - Feature [UI Layout in landscape and portrait](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/tree/main/App/app/src/main/res/layout-land)
    - Feature testing covers 70% of main functions, associated with Gea

- **Code and App Design**
    - Design the logo and color/style used in app
    - Design the Figma with Chan
    - Design/modify/create the layout in each XML to make the app have a consistent layout style(color/button/cardview etc.)
    - Performance enhanced and bug fixed in UI Layout related, for example in - [ExecutorServiceSingleton](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/blob/84716283214c2a43ec69051045ee5e19963ed8e1/App/app/src/main/java/com/go4/utils/design_pattern/ExecutorServiceSingleton.java)

- **Others**:
    - Create the UML

***

**u7902000, Gea Linggar Galih** I have contributed 20%:
- **Code Contribution in the final App**
    - Created classes:
        - [DataFetcher](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/blob/84716283214c2a43ec69051045ee5e19963ed8e1/App/app/src/main/java/com/go4/utils/DataFetcher.java)
        - [CsvParser](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/blob/84716283214c2a43ec69051045ee5e19963ed8e1/App/app/src/main/java/com/go4/utils/CsvParser.java)
        - [SuburbsJsonUtils](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/blob/84716283214c2a43ec69051045ee5e19963ed8e1/App/app/src/main/java/com/go4/utils/SuburbJsonUtils.java)
        - [AVLTree](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/blob/84716283214c2a43ec69051045ee5e19963ed8e1/App/app/src/main/java/com/go4/utils/tree/AVLTree.java)
        - [DataAccessObject](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/blob/84716283214c2a43ec69051045ee5e19963ed8e1/App/app/src/main/java/com/go4/utils/design_pattern/DataAccessObject.java)
        - [ExecutorServiceSingleton](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/blob/84716283214c2a43ec69051045ee5e19963ed8e1/App/app/src/main/java/com/go4/utils/design_pattern/ExecutorServiceSingleton.java)
        - [LocationStrategy](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/blob/84716283214c2a43ec69051045ee5e19963ed8e1/App/app/src/main/java/com/go4/utils/design_pattern/LocationStrategy.java)
        - [AirQualityRecord](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/blob/84716283214c2a43ec69051045ee5e19963ed8e1/App/app/src/main/java/com/go4/application/model/AirQualityRecord.java)
        - [MockAirQualityRecord](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/blob/84716283214c2a43ec69051045ee5e19963ed8e1/App/app/src/main/java/com/go4/application/live_data/MockDataStream/MockAirQualityRecord.java)
        - [MockJSONResponse](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/blob/84716283214c2a43ec69051045ee5e19963ed8e1/App/app/src/main/java/com/go4/application/live_data/MockDataStream/MockJSONResponse.java)
        - [SuburbLiveActivity](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/blob/84716283214c2a43ec69051045ee5e19963ed8e1/App/app/src/main/java/com/go4/application/live_data/SuburbLiveActivity.java)
        - basic class for [SuburbHistoricalActivity](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/blob/84716283214c2a43ec69051045ee5e19963ed8e1/App/app/src/main/java/com/go4/application/historical/SuburbHistoricalActivity.java)


- Contributed in classes:
    - [SplashActivity](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/blob/main/App/app/src/main/java/com/go4/application/SplashActivity.java)
        - [Add data fetching mechanism](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/commit/f635ede3045ca1a17dc39feeb037187b6ce8e886)
        - [Add credits and loading status](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/commit/e86e25fb574c593d4dbb6143c112683f936f5c3f)

    - [ProfileActivity](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/blob/84716283214c2a43ec69051045ee5e19963ed8e1/App/app/src/main/java/com/go4/application/profile/ProfileActivity.java)
        - [Refractor json parser for suburb list](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/commit/659b318c35163ebb1fc2c577ac487a80cfcb68a5)


- **Code and App Design**
    - Basic layout for [SuburbHistoricalActivity](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/blob/84716283214c2a43ec69051045ee5e19963ed8e1/App/app/src/main/java/com/go4/application/historical/SuburbHistoricalActivity.java) including [suburb, date and time selection method](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/commit/eca48c42e951d86a2cd15acae43d21ed0ab0df03)
    - [General layout](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/commit/e27a7dd1fb6826c5fdcd2badc9d14b947202a90f) of [SuburbLiveActivity](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/blob/84716283214c2a43ec69051045ee5e19963ed8e1/App/app/src/main/java/com/go4/application/live_data/SuburbLiveActivity.java), using Hexuan and Leo design

- **Others**:
    - Acquiring air quality data from [OpenWeaterMap](https://openweathermap.org/api/air-pollution)
    - [merge request review](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/merge_requests/15)
    - Checking for bugs on real android device

***

**u7327620, Ryan Foote** I have contributed 20%:
- **Code Contribution in the final App**
    - [SplashActivity](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/blob/main/App/app/src/main/java/com/go4/application/SplashActivity.java) methods
        - Building on what Gea implemented: [checkPermissions flow](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/blob/main/App/app/src/main/java/com/go4/application/SplashActivity.java#L62).
        - A few refactors: [notably](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/merge_requests/49/diffs#fc57be931eb08ba1143607ff8ef48225b5bb9f6f).
    - [MainActivity](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/blob/main/App/app/src/main/java/com/go4/application/MainActivity.java)
        - Integrated [FirebaseLogin](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/blob/main/App/app/src/main/java/com/go4/application/FirebaseLoginActivity.java) and [GPSService](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/blob/main/App/app/src/main/java/com/go4/utils/GPSService.java)
        - ActivityResultLauncher Implementation
        - Push notifications
    - [FirebaseLogin](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/blob/main/App/app/src/main/java/com/go4/application/FirebaseLoginActivity.java)
        - [Further Refactors](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/commit/f681406e2257af7d728bd7ad77d11ff672e60c3d)
        - [Created](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/commit/d2d372e6bf16051af5fecc26bd4553db1d4e88d7), [it became deprecated](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/commit/6c7fd54462bd9ba938401ee06fa81d9f5bd4a80a), then was [revived](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/commit/edd9c3b7da70be004390b03db6e9e167d4dad676)
        - ActivityResult contract allowing safe-usage
        - Handled interactions with [firebaseAuth](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/commit/d2d372e6bf16051af5fecc26bd4553db1d4e88d7#7bfdaf85de87d0757f3c20334e5b4d93a01f83cf_0_51)
        - Set-up Firebase Project
    - [GPSService](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/blob/main/App/app/src/main/java/com/go4/utils/GPSService.java)
        - Adjusted from background service to bound service.
        - Requested locations through [Play Services FusedLocationClient](https://developer.android.com/develop/sensors-and-location/location/request-updates).
        - Setup required notification system.

    - **Code and App Design**
        - Reviewed *requested* merge requests
            - [Styling](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/merge_requests/56)
            - [Style bordering on Critique](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/merge_requests/58)
            - [Reverts](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/merge_requests/39/diffs)
        - Design choice to switch LocationService to a bound service instead of a background service.
        - Implementation of the ResultActivityApi throughout the app.

- **Others**:
    - The *working* [CI](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/blob/main/.gitlab-ci.yml) (see reflection)
    - The [firebase project](https://console.firebase.google.com/u/2/project/go4gp-s2/overview)
    - Constructed 3/4 Meeting Minutes

## Application Description


Our app is called "EcoACT", which aims to provide users with real-time air quality information for communities in the Canberra area. This app allows users to view the air quality index (AQI) of different areas on a map, so as to understand the current air conditions in the environment and make better life decisions.

The main features of the app include the following:
**Real-time air quality monitoring**: Users can view the real-time air quality of various areas in Canberra on a map.
**Regional search**: Users can quickly view the air quality of an area by entering the name of the area.
**Personalized collection**: Users can collect frequently visited areas so that they can check the air quality of these places at any time.
**Historical data analysis**: The app also provides historical data on air quality to help users understand the changing trends of air quality in a certain area.
**Suburb Comparison**: Users can compare the air quality between two suburbs, allowing them to make informed decisions about which area is safer or more suitable for activities.

For example, if you live in the Bruce area and want to know whether it is suitable for outdoor activities today, you can open the app and view the air quality index of the Bruce area. In addition, the app will mark the location of nearby public facilities, such as parks, hospitals, etc., to help users make more appropriate living arrangements.

Screenshots:

![Suburb Comparison Graph](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/raw/main/items/media/_examples/comparable%20suburb.png)

*Figure 1: Comparison of air quality between two suburbs.*


![Regional Search](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/raw/main/items/media/_examples/live.png)

*Figure 2: The app's regional search function allows users to quickly search for different suburbs.*


![Air Quality Monitoring](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/raw/main/items/media/_examples/historical.png)

*Figure 3: Real-time air quality monitoring with detailed pollutant data.*

![User Profile and Pinned Suburbs](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/raw/main/items/media/_examples/profile.png)

*Figure 4: User profile section and personalized pinned suburbs feature.*

The "EcoACT" app empowers Canberra residents to stay informed about the air quality in their environment and make decisions that positively impact their health and well-being.

### Problem Statement

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

![UML diagram](media/_examples/UML.png)
** This image is in .png format, please click on and zoom in to see the details, use Visual Paradigm to generate **


## Code Design and Decisions

This is an important section of your report and should include all technical decisions made. Well-written justifications will increase your marks for both the report as well as for the relevant parts (e.g., data structure). This includes, for example,

- Details about the parser (describe the formal grammar and language used)

- Decisions made (e.g., explain why you chose one or another data structure, why you used a specific data model, etc.)

-  ⁠Details about the design patterns used (where in the code, justification of the choice, etc) concise, crucial reasons of your design.

*Please give clear and concise descriptions for each subsections of this part. It would be better to list all the concrete items for each subsection and give no more than 5



<hr>

### Data Structures


Here is a partial (short) example for the subsection `Data Structures`:*


1. *ArrayList*
    * *Objective**: Used for storing and managing a list of suburb details and historical air quality records dynamically as new data is fetched and displayed in the UI.
    * *Code Locations**: Defined in [`SuburbCardViewAdapter`](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/blob/main/App/app/src/main/java/com/go4/application/profile/SuburbCardViewAdapter.java#L28) and [`SuburbHistoricalActivity`](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/blob/main/App/app/src/main/java/com/go4/application/historical/SuburbHistoricalActivity.java#L159); processed using methods like `onBindViewHolder()` and `updateList()` to display air quality data and suburb information dynamically.
    * *Reasons*:
        * *It is more efficient than `LinkedList` for frequent random access operations where elements are accessed by index with a time complexity of O(1).*
        * *We need to frequently update and access elements in the list for UI rendering, such as when new air quality data is loaded or the list of suburbs is displayed.*
        * *ArrayList supports dynamic resizing, which is essential as the number of suburb cards and air quality records may vary during real-time data updates.*

2. *HashMap*
    * *Objective**: Used for storing suburb names as keys and their geographical coordinates as values to determine proximity in nearest suburb calculations.
    * *Code Locations**: Defined in [`NearestSuburbStrategy`](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/blob/main/App/app/src/main/java/com/go4/application/live_data/NearestSuburbStrategy.java#L92); processed using `getNearestSuburb()` and `getNearestSuburbList()` methods.
    * *Reasons*:
        * *It is more efficient than using a list for key-value pair lookups with an average time complexity of O(1).*
        * *We don't need ordered data for this feature because the suburbs are queried by name.*
        * *HashMap allows fast retrieval of suburb coordinates during proximity calculations without performance degradation, ensuring a smooth user experience.*

3. *HashSet*
    * *Objective**: Used to store unique combinations of suburb names and timestamps to prevent duplicate air quality records from being written to the CSV file.
    * *Code Locations**: Defined in [`DataFetcher.java`](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/blob/main/App/app/src/main/java/com/go4/utils/DataFetcher.java#L138) in the `fetchHistoricalDataTOCSV()` method to ensure no duplicate records are stored in the CSV.
    * *Reasons*:
        * *O(1) average time complexity for both insertions and lookups, which is critical for checking the existence of records efficiently before writing to the CSV.*
        * *Prevents the need for additional loops or checks to ensure that duplicate suburb records are not written.*
        * *Helps maintain the integrity of the dataset by ensuring that only new, unique records are appended to the file, reducing redundant data.*
4. *AVL Tree*
    * *Objective: Used for storing suburb data in a balanced manner to ensure efficient lookups, insertions, and deletions.*
    * *Code Locations: Defined in [`AVLTree`](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/blob/main/App/app/src/main/java/com/go4/utils/tree/AVLTree.java#L11); processed using `insert()`, `delete()`, and `search()` methods.*
    * *Reasons:*
        * *It is more efficient than a standard binary search tree, with O(log n) time complexity for all operations.*
        * *We need to maintain balanced performance as the dataset grows to avoid worst-case O(n) time complexity.*
        * *AVL Tree ensures consistent performance for large datasets, which is crucial as the number of suburbs increases.*
<hr>

### Design Patterns

1. **DAO (Data Access Object) Pattern**
    * **Objective**: Used to abstract and encapsulate access to air quality data from APIs or files, separating data access logic from business logic.
    * **Code Locations**: Defined in [`DataAccessObject`](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/blob/main/App/app/src/main/java/com/go4/utils/design_pattern/DataAccessObject.java); used in [`DataFetcher`](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/blob/main/App/app/src/main/java/com/go4/utils/DataFetcher.java#L80-L169) for interacting with APIs and managing data storage.
    * **Reasons**:
        * *It separates data access logic from the core business logic, improving maintainability and scalability.*
        * *It makes it easier to switch between data sources  without affecting the rest of the application.*
        * *Improves testability by allowing mock data sources during testing, making it easier to isolate and test individual components.*

2. **Singleton Pattern**
    * **Objective**: Ensures that only one instance of the `ExecutorService` is created and shared across the entire application to manage asynchronous tasks.
    * **Code Locations**: Defined in [`ExecutorServiceSingleton`](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/blob/main/App/app/src/main/java/com/go4/utils/design_pattern/ExecutorServiceSingleton.java); used in [`DataFetcher`](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/blob/main/App/app/src/main/java/com/go4/utils/DataFetcher.java#L46-L48) to manage API calls and background tasks efficiently.
    * **Reasons**:
        * *It ensures that only one instance of the thread pool exists, preventing resource exhaustion by creating multiple thread pools.*
        * *The Singleton pattern allows easy and consistent access to the thread pool across different parts of the application.*
        * *It reduces resource consumption by sharing a single instance for background processing tasks, such as fetching air quality data.*

3. **Strategy Pattern**
    * **Objective**: Allows for different strategies to be implemented for determining the nearest suburb or calculating distances based on user location.
    * **Code Locations**: Defined in [`LocationStrategy`](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/blob/main/App/app/src/main/java/com/go4/utils/design_pattern/LocationStrategy.java); implemented in [`NearestSuburbStrategy`](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/blob/main/App/app/src/main/java/com/go4/application/live_data/NearestSuburbStrategy.java) for calculating the nearest suburb based on geographic coordinates.
    * **Reasons**:
        * *It enables flexibility in choosing or adding new location-based calculation methods without modifying existing code.*
        * *Adheres to the open-closed principle, allowing new strategies to be added without altering the core logic.*
        * *It decouples the algorithm from the context where it's used, improving code maintainability and making it easier to switch or extend strategies.*
<hr>

### Parser

### <u>Grammar(s)</u>
Our grammar is designed to parse various types of data input related to air quality and suburb information, ensuring flexibility and adaptability for different formats such as JSON and CSV. By allowing key components like &lt;Location&gt;, &lt;Date&gt;, and &lt;Time&gt; to be interchangeable, the grammar design provides robustness to handle diverse data orders.

Advantages:

Flexibility: The grammar allows the tokens &lt;Location&gt;, &lt;Date&gt;, and &lt;Time&gt; to be in any order, making it adaptable to different input styles, which improves user experience.

Error Handling: This design allows the parser to still function even if the input data is provided in different sequences or contains invalid tokens, as long as the necessary tokens are provided.

Production Rules:

    <Input> ::= <Input> ::= <Location> <Separator>? <Date> <Separator>? <Time>
          | <Location> <Separator>? <Time> <Separator>? <Date>
          | <Date> <Separator>? <Location> <Separator>? <Time>
          | <Date> <Separator>? <Time> <Separator>? <Location>
          | <Time> <Separator>? <Location> <Separator>? <Date>
          | <Time> <Separator>? <Date> <Separator>? <Location>
    
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

    <Separator> ::= "," | ";" | "_"

### <u>Tokenizers and Parsers</u>

The tokenizers and parsers are used primarily in the Suburb Historical Data Page, in the class  SuburbHistoricalActivity in [SuburbHistoricalActivity.java](/App/app/src/main/java/com/go4/application/historical/SuburbHistoricalActivity.java) lines 192-230.
Three classes are used, including  Parser ([Parser.java](/App/app/src/main/java/com/go4/utils/tokenizer_parser/Parser.java)), Tokenizer ([Tokenizer.java](/App/app/src/main/java/com/go4/utils/tokenizer_parser/Tokenizer.java)) and Token ([Token.java](/App/app/src/main/java/com/go4/application/profile/Token.java)).
Tokenizers: The Tokenizer class uses regular expressions to split the input data into discrete tokens. These tokens are then classified as &lt;Location&gt;, &lt;Date&gt;, &lt;Time&gt;, making the input easier to process.
Parsers: The Parser class takes these tokens and applies the grammar rules to construct the data model used in the application. The parser uses predefined grammar rules to validate and organize the tokens into meaningful structures.

Advantages of the Designs:
Modularity: The separation between tokenizing and parsing tasks makes the system modular, simplifying maintenance and allowing for easy updates.
Adaptability: By using a flexible, the system can handle different input sequences and formats, improving the user experience when working with varied data sources.
Efficiency: Breaking data into tokens before parsing reduces complexity, leading to more efficient data handling. This design ensures that each step of data processing is straightforward and optimized.

## Implemented Features
### Basic Features
1. [LogIn]. The app must support user log-ins. User sign-up is not required. (easy)
   Username: comp2100@anu.edu.au Password: comp2100
   Username: comp6442@anu.edu.au Password: comp6442
* **Code**: [`FirebaseLoginActivity.java, methods onCreate, signIn`](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/blob/main/App/app/src/main/java/com/go4/application/FirebaseLoginActivity.java), [`activity_firebase_login_ui.xml`](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/blob/main/App/app/src/main/res/layout/activity_firebase_login_ui.xml) <br>
* **Description of feature**: This feature provides users with a secure way to log into the app using their email and password. Firebase Authentication is used to verify the credentials. Once successfully logged in, the user is redirected to the app's main functionality. Error messages are displayed in case of invalid credentials or network issues. Two predefined accounts (`comp2100@anu.edu.au` and `comp6442@anu.edu.au`) are provided for the markers to access the app. <br>
* **Description of your implementation**: The login functionality is implemented in `FirebaseLoginActivity`. In the `onCreate` method, we retrieve the authorisation instance associated with the project(`mAuth = FirebaseAuth.getInstance()`), and checks for a cached user, before finally showing the result via a toast message. If no cached user exists, the layout instantiates and provides a form. The login form includes two `EditText` fields: `lg_username` for the email input and `lg_password` for the password, defined in the XML layout file [`activity_firebase_login_ui.xml`](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/blob/main/App/app/src/main/res/layout/activity_firebase_login_ui.xml). The `signIn()` method validates the input fields and uses Firebase's `signInWithEmailAndPassword` method to authenticate the user. If the login is successful, the resultant user data returns via an `Intent`. If the login fails, a failure message is displayed using a `Toast` (`addOnFailureListener`) to inform the user. This ensures a smooth and user-friendly login experience, including proper error handling and input validation. <br>

2. [DataFiles]. The app must use a data set (which you may create) where each entry represents a meaningful piece of information relevant to the app. The data set must be represented and stored in a structured format as taught in the course. It must contain at least 2,500 valid instances. (easy)in your report.
* **Description of feature**: There are three persistent data sets used in this app. The file [`canberra_suburbs.json`](tFpOJ4pms32UhUzrfL4XFSK6)stores all the locations (suburbs) used in the app, primarily for populating the spinner or retrieving information about the selected location. The file[`canberra_suburbs_coordinates.json`](SANICmkjDFxw6rcZdjZE65ur) stores the coordinates related to each suburb, primarily for coordinate parameters in API calls, distance calculations, and for the GPS feature to select the nearest location. Both of these files are stored in the assets folder. The [historical_data.csv](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/blob/main/items/data/historical_data.csv) file is stored in the cache folder on the Android device and contains all air quality historical data from the last 7 days, with each record having a one-hour interval. The historical_data file is automatically fetched before each application opens to ensure the user has the latest data.
* **Description of your implementation**: [`canberra_suburbs.json`](tFpOJ4pms32UhUzrfL4XFSK6) is parsed and implemented using [SuburbJsonUtils](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/blob/84716283214c2a43ec69051045ee5e19963ed8e1/App/app/src/main/java/com/go4/utils/SuburbJsonUtils.java), while [`canberra_suburbs_coordinates.json`](SANICmkjDFxw6rcZdjZE65ur) is implemented using [these lines](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/blob/84716283214c2a43ec69051045ee5e19963ed8e1/App/app/src/main/java/com/go4/utils/DataFetcher.java#L73-91). Each record in [historical_data.csv](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/blob/main/items/data/historical_data.csv) is parsed and converted to an AVL Tree of [AirQualityRecord](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/blob/84716283214c2a43ec69051045ee5e19963ed8e1/App/app/src/main/java/com/go4/application/model/AirQualityRecord.java) using [CsvParser](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/blob/84716283214c2a43ec69051045ee5e19963ed8e1/App/app/src/main/java/com/go4/utils/CsvParser.java) class.

3. [LoadShowData] The app must load and display data instances from the data set. Data must be retrieved from either a local file (e.g., JSON, XML) or Firebase. (easy)
   <br>
* **Code**: [`SuburbLiveActivity.java`](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/blob/main/App/app/src/main/java/com/go4/application/SuburbLiveActivity.java) in [these lines](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/blob/84716283214c2a43ec69051045ee5e19963ed8e1/App/app/src/main/java/com/go4/application/live_data/SuburbLiveActivity.java#L767-809), [SuburbHistoricalActivity](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/blob/84716283214c2a43ec69051045ee5e19963ed8e1/App/app/src/main/java/com/go4/application/historical/SuburbHistoricalActivity.java) in [these lines](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/blob/84716283214c2a43ec69051045ee5e19963ed8e1/App/app/src/main/java/com/go4/application/historical/SuburbHistoricalActivity.java#L320-354), [ProfileActivity](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/blob/84716283214c2a43ec69051045ee5e19963ed8e1/App/app/src/main/java/com/go4/application/profile/ProfileActivity.java)

    * **Description of feature**: This feature loads air quality data for various suburbs in Canberra and displays it to the user in real-time. The data can be retrieved from local JSON files such as `canberra_suburbs.json` and `canberra_suburbs_coordinates.json`, which store suburb names and their geographical coordinates. The app is designed to retrieve data from these files or Firebase if real-time data is available. The loaded data is presented to users in a well-organized and dynamic format (via `RecyclerView`), which shows the current air quality index (AQI) for each suburb, allowing users to make informed decisions about outdoor activities based on the air quality.

* **Description of your implementation**:
  - The data loading functionality is managed by the `DataFetcher.java` class, which retrieves suburb and air quality data from `OpenWeaterMap API`. The data is displayed in `SuburbLiveActivity.java`, `SuburbHistoricalActivity`, or `ProfileActivity`, showing suburb names and related air quality metrics. The app provides air quality metrics either in numerical values or represents them as graphical information (see custom feature - Data Graphical).

4. [DataStream] The app must simulate user interactions through data streams. These data streams must be used to feed the app so that when a user is logged in (or enters a specific activity), the data is loaded at regular time intervals and the app is updated automatically.  (medium)
* **Code**: [SuburbLiveActivity](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/blob/main/App/app/src/main/java/com/go4/application/SuburbLiveActivity.java) in [showDataAndRefresh()](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/blob/84716283214c2a43ec69051045ee5e19963ed8e1/App/app/src/main/java/com/go4/application/live_data/SuburbLiveActivity.java#L396-406) method.

* **Description of feature**: This feature simulates user interactions by loading data streams periodically, allowing the app to update automatically when the user logs in or enters specific activities. The data streams feed air quality data into the app, ensuring that users always have up-to-date information without manual refreshes.

* **Description of your implementation**: The `SuburbLiveActivity` class periodically updates the displayed data by calling `refreshData`, which triggers the data fetch. A `SwipeRefreshLayout` in [`suburb_live.xml`](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/blob/main/App/app/src/main/res/layout/suburb_live.xml) enables users to manually refresh the data stream, simulating real-time updates. The app also utilizes timers or handlers with default interval of two minutes to automatically trigger the `fetchData` method in the background. However, because the data interval from the OpenWeather API is 1 hour, to demonstrate the app's ability to stream data, we created a [MockJSONResponse](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/blob/84716283214c2a43ec69051045ee5e19963ed8e1/App/app/src/main/java/com/go4/application/live_data/MockDataStream/MockJSONResponse.java). This class mimics the API response with similar JSON structures and generates 30 mock responses that are used in a cyclic manner.
5. [Search] The app must allow users to search for information. Based on the user's input, adhering to pre-defined grammar(s), a query processor must interpret the input and retrieve relevant information matching the user's query. The implementation of this functionality should align with the app’s theme. The application must incorporate a tokenizer and parser utilizing a formal grammar created specifically for this purpose. (medium)
* **Code**: SuburbHistoricalActivity class [`parseSearchBarInput`](/App/app/src/main/java/com/go4/application/historical/SuburbHistoricalActivity.java#L243-282) method, lines of code 243-282,
  [`Tokenizer.java`](/App/app/src/main/java/com/go4/utils/tokenizer_parser/Tokenizer.java),
  [`Parser.java`](/App/app/src/main/java/com/go4/utils/tokenizer_parser/Parser.java),
  [`Token.java`](/App/app/src/main/java/com/go4/utils/tokenizer_parser/Token.java),
  and [`SearchRecord.java`](/App/app/src/main/java/com/go4/application/historical/SearchRecord.java) <br>
* **Description of feature**: This feature allows users to search for suburb-specific air quality information based on their input, which follows to a pre-defined grammar. This ensures that users can easily access relevant air quality data by entering queries that match the expected input format. <br>
* **Description of your implementation**: The `SuburbHistoricalActivity` class has a method `parseSearchBarInput`, and it is invoked when the user's input in the search bar has changed.
  When called, a new `Tokenizer` and `Parser` is initialized, and the method `parseInput` of the `Parser` class is called, parsing the input.
  The parsed details, including the suburb name, date and time, are stored in the `SearchRecord` class.
  These details are displayed on screen in the suburb, date and time selectors, so the user can clearly see if their input is interpreted correctly.
  When the user clicks the search button, the parsed details are used to search for the air quality information.

6. [UXUI] The app must maintain a consistent design language throughout, including colors, fonts, and UI element styles, to provide a cohesive user experience. The app must also handle orientation changes (portrait to landscape and vice versa) gracefully, ensuring that the layout adjusts appropriately. (easy)
* **Code**: XML layouts like [`activity_main.xml`](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/blob/main/App/app/src/main/res/layout/activity_main.xml), [`suburb_live.xml`](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/blob/main/App/app/src/main/res/layout/suburb_live.xml), [`activity_profile.xml`](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/blob/main/App/app/src/main/res/layout/activity_profile.xml), [`card_layout.xml`](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/blob/main/App/app/src/main/res/layout/card_layout.xml) <br>
* **Description of feature**: The application maintains a consistent design language, including colors, fonts, and UI styles throughout all activities and screens. It also handles orientation changes gracefully, ensuring that the layout adapts to both portrait and landscape modes without disrupting the user experience. <br>
* **Description of your implementation**: The UI is implemented using `ConstraintLayout` and `LinearLayout` for flexibility and responsive design. Consistent colors, fonts, and styles are applied across XML layouts, with elements like `RecyclerView`, `CardView`, and `SwipeRefreshLayout` providing a uniform user interface. The app handles orientation changes through flexible layouts defined in XML files such as [`suburb_live.xml`](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/blob/main/App/app/src/main/res/layout/suburb_live.xml) and [`suburb_historical.xml`](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/blob/main/App/app/src/main/res/layout/suburb_historical.xml). Colors are defined in `colors.xml` to maintain consistency across all screens, and UI elements like `Spinner` and `EditText` adjust appropriately to different screen sizes and orientations, providing a seamless user experience.
  <br>
7. [UIFeedback] The UI must provide clear and informative feedback for user actions, including error messages to guide users effectively. (easy)
* **Code**: [`SuburbLiveActivity`](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/blob/main/App/app/src/main/java/com/go4/application/SuburbLiveActivity.java), [`DataFetcher`](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/blob/main/App/app/src/main/java/com/go4/application/DataFetcher.java), methods `onDataLoadFailure`, `onDataLoadSuccess` <br>
* **Description of feature**: The app provides clear and informative feedback to users during data loading and error states. Progress indicators and error messages help users understand the current state of the app and what actions they can take if something goes wrong. <br>
* **Description of your implementation**: The app uses a `ProgressBar` within [`layout_refresh_footer.xml`](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/blob/main/App/app/src/main/res/layout/layout_refresh_footer.xml) to visually indicate loading progress. In the `SuburbLiveActivity`, `SwipeRefreshLayout` provides feedback during manual data refreshes, offering users visual cues that the content is being updated. When data loading is successful, `onDataLoadSuccess` handles the updates, removing the progress bar and refreshing the UI. If data loading fails, `onDataLoadFailure` is triggered, displaying a `Toast` message to inform users of the error. This ensures users are aware of the issue and can take appropriate actions, such as retrying the data load or checking their network connection. Additionally, error messages are concise and clear, guiding users through troubleshooting steps to ensure a smooth experience.
  <br>



### Custom Features
***
Feature Category: Search
1. [Search-Designate]. The app must rank search results based on the status of the users. For example, a user may have multiple roles within the app, which should result in different ranked lists of results. (medium)
    * Code: [`NearestSuburbStrategy.java`](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/blob/main/App/app/src/main/java/com/go4/application/live_data/NearestSuburbStrategy.java), method `getNearestSuburb` and [`LoadMoreSearchResultAdapter.java`](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/tree/main/App/app/src/main/java/com/go4/application/live_data/adapter), method `onBindViewHolder`

    * Description of feature: The app ranks search results by calculating the distance between the user's current location and various suburbs, displaying the nearest suburbs first. This ensures that users receive more relevant information based on their physical proximity to the suburbs.

    * Description of your implementation: The proximity-based ranking is implemented by first calculating the distance between the user's current GPS coordinates (latitude and longitude) and each suburb's coordinates, stored in a `HashMap<String, double[]>`. This calculation is done using the Haversine formula, which provides an accurate measure of the great-circle distance between two points on the Earth's surface. The`NearestSuburbStrategy.java` class manages this distance calculation within the `getNearestSuburb` method, iterating through each suburb to determine the shortest distance from the user.

   Once the distances are calculated, the search results are ranked based on proximity. The`LoadMoreSearchResultAdapter.java` class is responsible for binding this sorted list of suburbs to the `RecyclerView`. In the `onBindViewHolder` method, the suburb data—including the name, distance, and coordinates—is dynamically displayed. The UI layout [`adapter_search_result_recyclerview`](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/blob/main/App/app/src/main/res/layout/adapter_search_result_recyclerview.xml) ensures that the ranked suburbs are shown in ascending order of distance, making it easy for the user to see which suburbs are closest.

   Additionally, the app is designed to provide real-time updates as the user moves or interacts with the app. The `SuburbLiveActivity.java` class triggers data fetching and re-ranking of the search results as the user's location changes. This ensures that the displayed results always reflect the user’s current location and proximity to various suburbs. The data is continually updated in the `RecyclerView`, maintaining a smooth and interactive experience for the user.
***
Feature Category: Data
2. [Data-GPS] The app must utilize GPS information based on location data. Hint: see the demo presented by our tutors on ECHO360. (easy)
    *  Code:[`GPSService.java`](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/blob/main/App/app/src/main/java/com/go4/utils/GPSService.java), and [`MainActivity.java`](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/blob/main/App/app/src/main/java/com/go4/application/MainActivity.java).

    * Description of feature: The app tracks the user's real-time location using GPS and Wifi via FusedLocationClient to deliver location-specific content, such as air quality information for the nearest suburb. By continuously updating the user's location when bound, the app ensures that the displayed data is always relevant to the user's current surroundings.

    * Description of your implementation: The GPS tracking feature connects to the plays services location FusedLocation api via `GPSService.java`, where the `startLocationUpdates()` method requests location updates using `FusedLocationProviderClient` to acquire GPS coordinates. Once permissions are granted, `GPSService.java` processes the updates, accessible by components like `MainActivity.java`. There is a single api method `getRecentLocation()` exposed for communication with the service which is available when the service is bound to an activity. Instead of handling `onLocationChanged()` in `MainActivity.java`, proximity-based updates occur in `GPSService.java`, which provides updated location data for displaying suburb-specific information, such as air quality. Distance calculations between the user’s location and suburbs are managed using suburb coordinates stored in a `HashMap<String, double[]>`. `SuburbLiveActivity.java` reloads data based on GPS updates, and `RecyclerView` in `SuburbCardViewAdapter.java` updates the UI to reflect the nearest suburbs and their air quality data, ensuring a smooth, real-time experience.

3. [Data-Graphical] The app must include a graphical report viewer that displays a report with useful data from the app. No marks will be awarded if the report is not graphical. (hard)
    * Code: [SuburbHistoricalActivity] in [plotPrimaryData() and plotComparisonData()](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/blob/84716283214c2a43ec69051045ee5e19963ed8e1/App/app/src/main/java/com/go4/application/live_data/SuburbLiveActivity.java#L823-921)

    * Description of your implementation: The `SuburbHistoricalActivity` class includes a graphical report viewer that visualizes air   quality data for various suburbs over a selected time period. The `plotPrimaryData()` method plots the primary suburb’s air quality metrics using a line chart. Users can view trends for specific air quality components such as AQI, CO, NO2, PM2.5, PM10, O3, and SO2. The data points are plotted with corresponding time labels on the X-axis, which are dynamically generated based on the retrieved records. The `plotComparisonData()` method provides an additional feature that allows users to compare air quality trends between two suburbs. The method plots a second set of data on the same line chart, using different colors to visually differentiate between the primary and comparison suburb data. The implementation is integrated with the [`MPAndroidChart`](https://github.com/PhilJay/MPAndroidChart) library

4. [UI-Layout]  The app must incorporate appropriate layout adjustments for UI components to support both portrait and landscape orientations, as well as various screen sizes. This requirement is in addition to the [UXUI] basic feature and necessitates the implementation of new layouts for each orientation and screen size. (easy)
    * Code: [layout-land, entire folder](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/tree/main/App/app/src/main/res/layout-land?ref_type=heads)
    * [activity_firebase_login_ui.xml](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/blob/main/App/app/src/main/res/layout/activity_firebase_login_ui.xml?ref_type=heads) and [activity_profile.xml](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/blob/main/App/app/src/main/res/layout/activity_profile.xml?ref_type=heads) and [suburb_historical.xml](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/blob/main/App/app/src/main/res/layout/suburb_historical.xml?ref_type=heads) and [suburb_live.xml](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/blob/main/App/app/src/main/res/layout/suburb_live.xml?ref_type=heads)

    * Description of your implementation: Our app utilized CardView for a unified visual style, along with flexible layout managers, enhances both the aesthetic appeal and performance of the app. The modular layout structure further facilitates easy maintenance and scalability, allowing the app to adapt seamlessly to future requirements.
    * The use of match_parent and wrap_content in combination with ConstraintLayout and GridLayout allows UI components to automatically adjust their size and position based on the device's screen size and resolution. For instance, in SuburbHistoricalActivity, ConstraintLayout and GridLayout are employed to dynamically adapt to different screen arrangements seamlessly.
    * ScrollView is used to wrap the main content, ensuring that on smaller screens or with extensive content, users can scroll to access all information. This is implemented in both suburb_historical.xml and suburb_live.xml to handle content overflow gracefully.
    * All CardViews utilize a consistent color theme (@color/primary_ColorW) and corner radius (app:cardCornerRadius="12dp") to maintain a cohesive visual appearance across different layouts and orientations.
    *


Feature Cateory: User Interactivity <br>
5. [Interact-Micro] The app must provide the ability to micro-interact with items or users (e.g., like, block, connect to another user) with interactions stored in-memory. (easy)
    * Code: [Class ProfileActivity, entire file](/App/app/src/main/java/com/go4/application/historical/SuburbHistoricalActivity.java),
      [Class SuburbCardViewAdapter, entire file](/App/app/src/main/java/com/go4/application/profile/SuburbCardViewAdapter.java),
      [Class SuburbCard, entire file](/App/app/src/main/java/com/go4/application/profile/SuburbCard.java),
      [activity_profile.xml, lines of code 101-153](/App/app/src/main/res/layout/activity_profile.xml#L101-153),
      and [activity_profile_card.xml, entire file](/App/app/src/main/res/layout/activity_profile_card.xml)
    * [Class ProfileActivity, lines of code 243-462](/App/app/src/main/java/com/go4/application/profile/ProfileActivity.java#L243-462):
      methods displayPinnedSuburbCards, searchForQualityAndPm10Number, readPinnedSuburbs, writePinnedSuburbs,
      updatePinnedSuburbs, addButtonOnClick, addSuburbCard
    * Description of your implementation:
        - The Profile Page displays a list of the user's pinned suburbs as cards, which contains an editable label, the suburb name and the PM10 Number.
        - It indicates whether the air quality in that suburb is "Good", "Moderate" or "Bad", based on the air quality index.
            - If it is "Good", the card will have a green background colour, and the image next to the text "Good" is a smiley face.
            - If it is "Moderate", the card will have a yellow background colour, and the image next to the text "Moderate" is a meh face.
            - If it is "Bad", the card will have an orange background colour, and the image next to the text "Bad" is a meh face.
        - Whenever a suburb card is added with the ADD button or deleted by swiping right, or when an label is updated, the method [writePinnedSuburbs](/App/app/src/main/java/com/go4/application/profile/ProfileActivity.java#L243-279) is called to store the details of the pinned suburbs in internal storage, in a file titled "pinned_suburbs.txt".
        - The pinned suburbs are displayed using the class SuburbCardViewAdapter and RecyclerView, and the class SuburbCard is used to store and return the data of each card.
        - The data of the pinned suburbs are updated every 15 minutes using the [updatePinnedSuburbs](/App/app/src/main/java/com/go4/application/profile/ProfileActivity.java#L374-390) method.

## Surprise Feature

#### *TLDR:*
- In the previous layout design [see here](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/commit/cd1dc8bb273f4cf903a6e802262c7c785ec3099a), we did not give credit to `OpenWeatherMap` for using their data in our app. For ethical considerations, we decided to update the layout and include a reference to `OpenWeatherMap` in our recent [commit](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/commit/e86e25fb574c593d4dbb6143c112683f936f5c3f)
- In the previous code design [see here](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/commit/7a69ac685e38f9b99e04c33d556d6592255f4a26), we are using several `ExecutorService` instances, in `SuburbLiveActivity` and `SplashActivity`, We felt that this design would not be efficient or scalable in the long term. Therefore, we created the[ExecutorServiceSingleton](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/blob/84716283214c2a43ec69051045ee5e19963ed8e1/App/app/src/main/java/com/go4/utils/SuburbJsonUtils.java) class to make sure that only one `ExecutorService` instance is used across the app, optimizing resource management and preventing the creation of multiple thread pools, [see here](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/commit/a14c0cd1478043e6c66ee060237e0c80655a2f19).

#### *In full*

*"Your design choices have resulted in an app that, while it may be functional, is not fully modular, reusable, scalable or readable. We are also concerned that you haven’t carefully thought through possible perceived or actual ethical violations, as is standard in industry. And you never consulted us on the software license!"*

Hello customer,

We here at EcoACT wish to extend our sincerest apologies for the mismanagement of your product. We aim to hold ourselves to the highest standards and accept no less than perfection when shipping our customers final products. I'm deeply upset to hear that the team have let you down. To show you that we're adamant on our claims, we've taken your advice to heart and implemented the following changes to the project.

([Issue/merge documentation stem](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/issues/4))

To ensure scalability and reusability, we've identified multiple usages of the ExecutorService class. This causes multiple instances of the class that consume system resources without optimal usage. To rectify the mistake, we've implemented a Singleton design pattern and migrated our Activity classes to the new version. This means that the code can be re-used elsewhere under different executor handling conditions and the code within the project will optimally use the system resources assigned to it.
- [Associated Issue](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/issues/5)
- [Merge Request 1](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/merge_requests/48) creates the singleton rendition of the class.
    - [Relevant file](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/blob/7b172896d7d871f479bc3ba7de8d2ac85abc0d09/App/app/src/main/java/com/go4/utils/design_pattern/ExecutorServiceSingleton.java)
- [Merge Request 2](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/merge_requests/59) updates activity usages of the ExecutorService class to our new singleton version.
  These upgrades to the system rectifies the improper system resource management and aligns with our project vision for reusability through robust design patterns.

The Eco Team™ are vehemently apologetic to the lack care taken to avoid ethical violations, and as such, violating the industry standards. We've conducted an explosive internal review spearheaded by our scrum masters to identify decadent developers and conducted ethical training modules where needed. The Eco Team™ have identified that the usage of the OpenWeather Api has gone without accreditation within the project. Since the availability of data is the foundation to the project, we've rectified the mistake and added accreditation into the app to maintain amicable business agreements with the company.
- [Associated Issue](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/issues/4?work_item_iid=6)
- [Merge Request Solution](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/merge_requests/56)
  We hope our rapid response exemplifies The Eco Team™ strict moral code and adherance to perpetuating positive ethics through our work.

The Eco Team™ have selected the MIT license for the project as (despite being a very real and epic company) we're a group of students who's code is going to be hidden in a sea of student submissions. If a user reviewed our code and wished to use it, I'm sure we would be honoured (if not a little confused). The license protects us against any potential legal issues regarding warranties whilst allowing anyone to utilise and adapt the software to their needs.


## Testing Summary

1.  Tests for **Data Fetching**
- **Code**: [`DataFetcherTest.java`](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/blob/main/App/app/src/androidTest/java/com/go4/application/DataFetcherTest.java)
- **Number of Test Cases**: 1
- **Code Coverage**: This test ensures that the historical data fetching method (`fetchHistoricalDataTOCSV`) creates a CSV file in the cache directory using actual API calls. It verifies the file generation and includes a delay to account for network response times.
- **Types of Tests and Descriptions**:
    - Integration Test: Validates data fetching and storage logic for air quality data, using a real API endpoint to create and verify the generated CSV file.

2. Tests for **AVL Tree Operations**
- **Code**: [`AVLTreeTest.java`](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/blob/main/App/app/src/test/java/com/go4/application/AVLTreeTest.java)
- **Number of Test Cases**: 3
- **Code Coverage**: The test verifies AVL tree operations, including insertions, balancing, and height calculations.
- **Types of Tests and Descriptions**:
    - Functional Tests: Verifies AVL tree insertion, balancing operations, and checks tree height. The in-order traversal is also tested to confirm proper data arrangement.

3. Tests for **CSV Parsing**
- **Code**: [`CSVParsingTest.java`](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/blob/main/App/app/src/test/java/com/go4/application/CSVParsingTest.java)
- **Number of Test Cases**: 6
- **Code Coverage**: Tests cover CSV parsing logic for air quality data, including multiple scenarios such as empty records, correctly formatted records, invalid data, and safe parsing operations.
- **Types of Tests and Descriptions**:
    - Unit Tests: Covers CSV parsing functionality for various input scenarios, including valid data, empty files, and invalid records. Ensures that the parser handles errors gracefully and returns expected outputs without crashing.

4. Instrumented Tests
- **Code**: [`ExampleInstrumentedTest.java`](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/blob/main/App/app/src/androidTest/java/com/go4/application/ExampleInstrumentedTest.java)
- **Number of Test Cases**: 1
- **Code Coverage**: This test verifies the application context in an Android device environment.
- **Types of Tests and Descriptions**:
    - Context Validation: Ensures that the app runs correctly on an Android device, verifying the basic functionality of the application's environment.

5. General Unit Tests
- **Code**: [`ExampleUnitTest.java`](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/blob/main/App/app/src/test/java/com/go4/application/ExampleUnitTest.java)
- **Number of Test Cases**: 1
- **Code Coverage**: A basic arithmetic test to verify the correctness of the unit test setup.
  -**Types of Tests and Descriptions**:
    - Basic Unit Test: Validates arithmetic functionality, primarily used to verify the setup of the testing framework.

6. Tests for **Tokenizer**
- **Code**: [`TokenizerTest.java`](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/blob/main/App/app/src/test/java/com/go4/application/TokenizerTest.java)
- **Number of Test Cases**: 11
- **Code Coverage**: Test covers all Token Types, including different formats of the same token.
- **Types of Tests and Descriptions**:
    - Unit Tests: Ensure the tokenizer correctly identifies and categorizes all token types, including handling different formats and variations of tokens.

7. Tests for **Parser**
- **Code**: [`ParserTest.java`](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/blob/main/App/app/src/test/java/com/go4/application/ParserTest.java)
- **Number of Test Cases**: 14
- **Code Coverage**: Test covers inputs with different orders, inputs with separators and inputs with invalid tokens.
- **Types of Tests and Descriptions**:
    - Unit Tests: Validate the parser's ability to process and interpret sequences of tokens, handling various input orders, separators, and invalid tokens.

<br> <hr>


***

## Summary of Known Errors and Bugs

### Active
1. *Permissions Denial*
    - When a user denies most permissions, the app can simply re-request until the permissions are granted. There is a specific issue with the background location permission where we can't request it multiple times. Presently, if the user denies the "Access at all times" option for location, the app will continually chew up resources trying to request the permission until it crasheds.
    - The simple fix is: settings > apps > Application > permissions > location > allow access all the time

### Fixed
1. *Caching:*
    - persistent bug that show up only in real APK, however the error is not shown in android emulator, [see here](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/blob/main/items/media/_examples/462563872_554352077247235_7338037996929308810_n.png)
    - After running the debug using the Android setup, we found that `firebaseAuth` was returning a `null` user's email. We didn't realize this earlier due to differences in how the cache is stored between the emulator and the real device. We modified the code in the login activity class, and the bug was resolved.

## Team Management
### Meeting Minutes
- [Team Meeting 1](meeting-1.md)
- [Team Meeting 2](meeting-2.md)
- [Team Meeting 3](meeting-3.md)
- [Team Meeting 4](meeting-4.md)

### Conflict Resolution Protocol

*If your group has issues, how will your group reach consensus or solve the problem?*

Our team is committed to open communication, collaboration, and mutual support. This protocol outlines how we handle different challenges that may come up during our 6-to-8-week project, ensuring everyone feels valued and supported. Here’s how we plan to deal with potential issues:

1. If a Team Member Gets Sick in the Final Week
    - **Notify the Team**: If someone gets sick, they should let the group know as soon as possible using our regular communication channel(insgram or discord) .
    - **Reassign Tasks**: We'll hold an emergency meeting to redistribute the workload of the sick member to make sure we stay on track. Tasks with immediate deadlines will be prioritized.
    - **Offer Support**: The member who is sick will still be updated on the project progress but won’t be expected to work until they’re better. Other team members will help to fill in any gaps to keep things moving smoothly.

2. If a Team Member Misses a Task Deadline Before a Checkpoint
    - **Check Progress Early**: We'll do a quick check-in about two days before each checkpoint to make sure everyone is on track. If anyone thinks they won’t meet a deadline, they’re encouraged to let the team know so we can help.
    - **Help Finish Tasks**: If a task isn’t done in time and the checkpoint is approaching, other members will step in to help complete it. We’ll work together to avoid missing critical deadlines.
    - **Reflect and Adjust**: After the checkpoint, we’ll discuss what caused the delay and figure out how we can avoid similar issues in the future. This could involve adjusting timelines or offering more support where needed.

3. If a Team Member Is Unreachable for Two Days
    - **Multiple Ways to Contact**: If a member hasn’t responded to messages for two days without prior notice, the team lead will try to contact them through different means, like calling or emailing.
    - **Temporary Task Reassignment**: If they still can’t be reached after 48 hours, we’ll temporarily reassign their tasks to other team members to keep the project on track.
    - **Follow-up Conversation**: When the member returns, we’ll have a conversation to understand what happened and how to avoid similar situations in the future. The goal is to keep communication as open as possible.

4. If There Are Different Understandings of Assignment Requirements
    - **Team Discussion**: If there are different interpretations of the assignment, we’ll hold a team meeting to openly discuss it until we reach a shared understanding.
    - **Ask for Clarification**: If we can’t reach a consensus internally, we’ll collectively put together questions and ask the course instructor or tutor for clarification.
    - **Document Decisions**: Once we agree on the requirements, we’ll document them and share with everyone to make sure we all stay aligned moving forward.

5. General Conflict Resolution
    - **Open and Respectful Discussion**: If any conflict arises, the members involved will discuss the issue openly during a team meeting. Respectful communication is key, and everyone will get a chance to share their point of view.
    - **Mediation**: If direct discussion doesn’t resolve the conflict, a neutral team member will act as a mediator to help find a solution.
    - **Escalation**: If mediation fails, we’ll bring the issue to the course instructor for further guidance.

6. Handling Unexpected Incidents
    - **Risk Assessment**: At the start of the project, we’ll identify potential risks (like illness or technical problems) and create contingency plans for each.
    - **Task Backups**: Every critical task will have a backup person familiar with it, so the work can continue if the main person is unavailable.
    - **Regular Check-ins**: We’ll have weekly meetings to review our progress and spot any potential issues early. This will help us take action before they become bigger problems.



      - [SplashActivity](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/blob/main/App/app/src/main/java/com/go4/application/SplashActivity.java)
         - [Add data fetching mechanism](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/commit/f635ede3045ca1a17dc39feeb037187b6ce8e886)
         - [Add credits and loading status](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/commit/e86e25fb574c593d4dbb6143c112683f936f5c3f)
   
      - [ProfileActivity](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/blob/84716283214c2a43ec69051045ee5e19963ed8e1/App/app/src/main/java/com/go4/application/profile/ProfileActivity.java)
         - [Refractor json parser for suburb list](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/commit/659b318c35163ebb1fc2c577ac487a80cfcb68a5)


   - **Code and App Design** 
      - Basic layout for [SuburbHistoricalActivity](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/blob/84716283214c2a43ec69051045ee5e19963ed8e1/App/app/src/main/java/com/go4/application/historical/SuburbHistoricalActivity.java) including [suburb, date and time selection method](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/commit/eca48c42e951d86a2cd15acae43d21ed0ab0df03)
      - [General layout](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/commit/e27a7dd1fb6826c5fdcd2badc9d14b947202a90f) of [SuburbLiveActivity](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/blob/84716283214c2a43ec69051045ee5e19963ed8e1/App/app/src/main/java/com/go4/application/live_data/SuburbLiveActivity.java), using Hexuan and Leo design 

- **Others**:
   - Acquiring air quality data from [OpenWeaterMap](https://openweathermap.org/api/air-pollution)
   - [merge request review](https://gitlab.cecs.anu.edu.au/u7327620/gp-24s2/-/merge_requests/15)
   - Checking for bugs on real android device