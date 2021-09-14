# cs0320 Term Project 2021

**Team Members:** Sara (ssyed7), Abby (adicther), Erika (ebussman), Pradyut (psekhsar)

**Project Concept**
### The Road Trip Planner
Overview: The Road Trip planner would aim to solve the problem of planning a roadtrip that balances driving with sightseeing. A customizable planner would allow a user to choose where they would like to start and end their roadtrip, how much time they want to spend on the road, and what kind of attractions they want to see (natural scenery, national parks, restaurants, museums etc). The planner would generate a scenic and entertaining route specific to what each user is looking for in an ideal road trip.

<br />Requirements/Features:
<br />**Constraints: specify the user’s travel budget (restaurants, accomodations, tolls, etc), allowed time for the trip, maximum/minimum number of stops, stops for gas/ charging stations**
<br /> Why:
<br /> -People have many different objectives/limitations when planning a road trip, and for the
sake of usability/inclusivity it’s important to acknowledge these different criteria when planning a trip
<br />Challenges:
<br /> -Optimizing for multiple constraints and identifying user preferences when these
constraints conflict (eg. is time constraint more important than exactly meeting the budget?)
<br /> -Estimating how long certain stops (such as restaurant visits etc.) might take
<br /> -Estimating the time between stops for gas/charging
<br /> -Estimating how different stops will affect overall budget
<br />**Interests: specify the user’s preferences between categories (eg. restaurants, museums,
etc) and within categories (eg. Italian restaurants)**
<br />Why:
<br /> -How enjoyable a trip is depends a lot on whether the user is able to explore the things
they’re interested in, and allowing for preference specification improves the chances that the user and their travel party will be able to draw on past experiences while experiencing new things
<br />Challenges:
<br /> -Finding data about local attractions and scenic routes
<br /> -Deciding how much to weight/prioritize each category
<br />**Visualization/trip summary:allows the user to visualize the suggested trip plan in
multiple formats (eg. map form, list form with stops and details)**
<br />Why:
<br /> -when on a road trip, it’s helpful to visualize the overall trip from a bird’s eye view
(eg. to see if there’s other stops not on the list that user may be interested in visiting) as well as in a more detailed, informational form (eg. list of stops, times, addresses) so the user can follow the route in a step-by-step manner and avoid getting lost on the way
<br />Challenges:
<br /> -Choosing which details to present on a map vs. which to specify in the trip
overview/directions
<br /> -Semi-automating the process of visualization for the map (eg. icons for stops,
categorization of stop types, etc.)

**HTA Approval (crusch):** Accepted, just make sure you have a strong core algorithm as your main feature.

**Mentor TA:** Ben Silverman, benjamin_silverman1@brown.edu

## How to Build and Run
In the terminal, navigate to the root directory "term-project-adichter-ebussman-psekhsar-ssyed7" and
run "mvn clean package". Next, run "./run -gui". From another terminal, navigate to the root directory,
cd into "frontend", and run "npm start". If this doesn't happen automatically, open a browser
(Chrome works well) and navigate to "https://localhost:3000". Finally, fill out the form
on the web page and get your roadtrip!

## Errors/Bugs
While not an explicit error/bug, the user may achieve more/less satisfactory stops for their route
based on the kinds of preferences they indicated and how well these preferences align with the data
in the Yelp DB or the Yelp API. However, we have attempted to match these preferences as closely as
possible.

## Design decisions
On the frontend, we decided to limit both the number of user inputs (to reduce the amount of work
the user has to do) and the number of "free-text" inputs (eg. boxes where user can type anything).
This significantly reduced the amount of input sanitizing we had to do, and also allows for a
simpler, more precise user experience, because the user can begin typing a location and it will
likely show up (biased towards their current location). 


## Tests
We divided our Unit tests into three classes: AttractionNodeTests, BoundingBoxTests and DijkstraTests.
AttractionNodeTests consists of tests for the four different attraction classes, testing the different
getter and setter methods as well as the value heuristic method for each class. BoundingBoxTests covers
tests for functions involved in querying AttractionNodes from the Yelp Database within a BoundingBox
between a start and end position. Edge cases tested for include bounding boxes with the same start and end 
positions, querying for attractions of invalid categories, querying within an empty bounding box. Lastly, 
DijkstraTests covers tests for our implementation of Dijkstra's algorithm, testing if paths
include/exclude certain stops/categories of stops.


## Support sources utilized for Google Maps API implementation
https://tintef.github.io/react-google-places-autocomplete/docs/
https://developers.google.com/maps/documentation/places/web-service/details
https://developers.google.com/maps/documentation/directions/start#api-key
https://developers.google.com/maps/documentation/javascript/reference/places-service#PlaceDetailsRequest

## Other sources utilized
Animations: http://jsfiddle.net/MDzsR/1/
Bootstrap: https://react-bootstrap.github.io/layout/grid/
Flaticon: https://www.flaticon.com/
