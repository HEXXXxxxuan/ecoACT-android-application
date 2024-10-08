# [G0 - Team Name] Report

*We give instructions enclosed in square brackets [...] and examples for each sections to demonstrate what are expected for your project report. Note that they only provide part of the skeleton and your description should be more content-rich. Quick references about markdown by [CommonMark](https://commonmark.org/help/)*

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

*Instruction: please place the CORRECT link to your firebase repository here (with comp21006442@gmail.com added as an Editor)*

- Firebase Repository Link: <"https://console.firebase.google.com/u/1/project/go4gp-s2/overview"> // TODO
   - Confirm: [ ] I have already added comp21006442@gmail.com as a Editor to the Firebase project prior to due date.
- Two user accounts for markers' access are usable on the app's APK (do not change the username and password unless there are exceptional circumstances. Note that they are not real e-mail addresses in use):
   - Username: comp2100@anu.edu.au	Password: comp2100 [X] // TODO: check if done
   - Username: comp6442@anu.edu.au	Password: comp6442 [ ] // TODO: check if done

## Team Members and Roles
The key area(s) of responsibilities for each member

| UID   |  Name  |   Role |
|:------|:------:|-------:|
| [u7327620] | [Ryan Foote] | [Worked on the login and using Firebase,GPS function and code review] |
| [u7902000] | [Gea Linggar Galih]| [Worked on the back-end for data-related funtion and code review] |
| [u8003980] | [Cheng Leong Chan] | [worked on the UI figma design and search-fliter function]] |
| [u8006862] | [Hexuan Wang] | [worked on the UI design and Implementing the UI interface and Lego design] |
| [u7635535] | [Zechuan Liu] | [worked on the basic function and search designate] |


## Summary of Individual Contributions

Specific details of individual contribution of each member to the project.

Each team member is responsible for writing **their own subsection**.

A generic summary will not be acceptable and may result in a significant lose of marks.

*[Summarise the contributions made by each member to the project, e.g. code implementation, code design, UI design, report writing, etc.]*

*[Code Implementation. Which features did you implement? Which classes or methods was each member involved in? Provide an approximate proportion in pecentage of the contribution of each member to the whole code implementation, e.g. 30%.]*

*you should ALSO provide links to the specified classes and/or functions*
Note that the core criteria of contribution is based on `code contribution` (the technical developing of the App).

*Here is an example: (Note that you should remove the entire section (e.g. "others") if it is not applicable)*

1. **UID1, Name1**  I have 30% contribution, as follows: <br>
  - **Code Contribution in the final App**
    - Feature A1, A2, A3 - class Dummy: [Dummy.java](https://gitlab.cecs.anu.edu.au/comp2100/group-project/ga-23s2/-/blob/main/items/media/_examples/Dummy.java)
    - XYZ Design Pattern -  class AnotherClass: [functionOne()](https://gitlab.cecs.anu.edu.au/comp2100/group-project/ga-23s2/-/blob/main/items/media/_examples/Dummy.java#L22-43), [function2()](the-URL)
    - ... (any other contribution in the code, including UI and data files) ... [Student class](../src/path/to/class/Student.java), ..., etc.*, [LanguageTranslator class](../src/path/to/class/LanguageTranslator.java): function1(), function2(), ... <br><br>

  - **Code and App Design** 
    - [What design patterns, data structures, did the involved member propose?]*
    - [UI Design. Specify what design did the involved member propose? What tools were used for the design?]* <br><br>

  - **Others**: (only if significant and significantly different from an "average contribution") 
    - [Report Writing?] [Slides preparation?]*
    - [You are welcome to provide anything that you consider as a contribution to the project or team.] e.g., APK, setups, firebase* <br><br>

2. **UID2, Name2**  I have xx% contribution, as follows: <br>
  - ...



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

- Details about the design patterns used (where in the code, justification of the choice, etc)

*Please give clear and concise descriptions for each subsections of this part. It would be better to list all the concrete items for each subsection and give no more than `5` concise, crucial reasons of your design.

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

      * Data Access Object (DAO) Pattern

Objective: Used for abstracting and encapsulating all access to the application's data source, providing a simple API for querying and updating data.

Code Locations: Defined in DataAccessObject.java class, methods getData, insertData, updateData, lines 20-85; processed using getConnection and executeQuery.

Reasons:
We need to separate the low-level data accessing operations from the higher-level business logic, ensuring clear and maintainable code.
DAO Pattern provides a clear interface for interacting with the data source, minimizing code duplication and ensuring all data-related operations are performed consistently.
It improves code readability and makes it easier to change or replace the data source without affecting the rest of the application, promoting a modular architecture.

 Singleton Pattern

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
*If there are several grammars, list them all under this section and what they relate to.*

Production Rules:
```
    <Non-Terminal> ::= <some output>
    <Non-Terminal> ::= <some output>
```

### <u>Tokenizers and Parsers</u>

*[Where do you use tokenisers and parsers? How are they built? What are the advantages of the designs?]*

<hr>

### Others

*[What other design decisions have you made which you feel are relevant? Feel free to separate these into their own subheadings.]*

<br>
<hr>

## Implemented Features
*[What features have you implemented? where, how, and why?]* <br>
*List all features you have completed in their separate categories with their featureId. THe features must be one of the basic/custom features, or an approved feature from Voice Four Feature.*

### Basic Features
1. [LogIn]. Description of the feature ... (easy)
   * Code: [Class X, methods Z, Y](https://gitlab.cecs.anu.edu.au/comp2100/group-project/ga-23s2/-/blob/main/items/media/_examples/Dummy.java#L22-43) and Class Y, ...
   * Description of feature: ... <br>
   * Description of your implementation: ... <br>

2. [DataFiles]. Description  ... ... (...)
   * Code to the Data File [users_interaction.json](link-to-file), [search-queries.xml](link-to-file), ...
   * Link to the Firebase repo: ...

3. ...
   <br>

### Custom Features
Feature Category: Privacy <br>
1. [Privacy-Request]. Description of the feature  (easy)
   * Code: [Class X, methods Z, Y](https://gitlab.cecs.anu.edu.au/comp2100/group-project/ga-23s2/-/blob/main/items/media/_examples/Dummy.java#L22-43) and Class Y, ...
   * Description of your implementation: ... <br>
     <br>

2. [Privacy-Block]. Description ... ... (medium)
   ... ...
   <br><br>

Feature Category: Firebase Integration <br>
3. [FB-Auth] Description of the feature (easy)
   * Code: [Class X, entire file](https://gitlab.cecs.anu.edu.au/comp2100/group-project/ga-23s2/-/blob/main/items/media/_examples/Dummy.java#L22-43) and Class Y, ...
   * [Class B](../src/path/to/class/file.java#L30-85): methods A, B, C, lines of code: 30 to 85
   * Description of your implementation: ... <br>

<hr>

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


- *[Team Meeting 1](link_to_md_file.md)*
- ...
- ...
- [Team Meeting 4](link_to_md_file.md)
- ... (Add any descriptions if needed) ...

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


