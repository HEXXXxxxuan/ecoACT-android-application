## Motivation
The world is changing, and with it, the air we all share. Irregardless of our smart-city citizens' wealth, status, or power, their human body requires air without pollutants. This causes a great deal of anxiety for our citizens. Whilst our smart-city teams are working to eradicate damage caused by those that came before us, we need to find a way to minimise impact to our citizens in our city. 

So what's the solution?

## Solution
By providing a user-friendly application that tracks live and historical air-quality data indexed via suburb, our citizens move forth with the knowledge to make impactful and informed decisions for their safety in regards to the air they breathe. By looking up a suburb they are thinking of visiting before making the journey, they can see trends in the levels of different molecules that make up our air as well as a general "Good or Bad" indicator. To further aid the user, we individualized it to each citizen remember the suburbs they frequent so that they can access the most relevant information to them the quickest. 

Let's break down the back-end of the app.
## Design Patterns
  - GPS Service-Layer
    - To engage with Location services, we separated the logic into a separate service for the application. This abstraction enforced a consistent interface through a `service.getRecentLocation()` function. Using a service pattern also allowed swift change from being a standalone background service to a bound service.
  - Execution Singleton
    - To ensure safety when multi-threading, we implemented a singleton instance of the Executor class. This meant ... {Gea fill out please}.
  - CSVParser DAO
    - Also Gea Pls
    
## Data Structures
  - AVLTree()


