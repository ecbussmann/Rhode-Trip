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

## Google Maps API Specs

## Support sources utilized for Google Maps API implementation
https://tintef.github.io/react-google-places-autocomplete/docs/
https://developers.google.com/maps/documentation/places/web-service/details
https://developers.google.com/maps/documentation/directions/start#api-key
