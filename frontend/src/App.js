import './App.css';
import './index.js'
import GooglePlacesAutocomplete from 'react-google-places-autocomplete';
import { Map, Marker } from "google-maps-react"

import {useState, useEffect} from 'react'
import { AwesomeButton } from "react-awesome-button";
import TextBox from './TextBox.js';
import "react-awesome-button/dist/themes/theme-eric.css";
import axios from 'axios';

import { makeStyles } from '@material-ui/core/styles';
import Grid from '@material-ui/core/Grid';
import Typography from '@material-ui/core/Typography';
import Slider from '@material-ui/core/Slider';
import fork from './fork.png'
import museum1 from './museum1.png'
import store from './store.png'
import start from './start.png'
import finish from './finish.png'
import road from './road.png'
import park2 from './park2.png'
import flags from './flags.png'

import {Container, Row, Col} from "react-bootstrap"
import 'bootstrap/dist/css/bootstrap.min.css';
import { Icon } from 'semantic-ui-react';

import ThumbUpAltIcon from '@material-ui/icons/ThumbUpAlt';
import ThumbDownAltIcon from '@material-ui/icons/ThumbDownAlt';

/*global google*/

let origin = null
let originPlace = null
let originName = ""
let originWebsite = ""
let originMapURL = ""
let originPhone = ""
let originIcon = ""
let error_message = ""
let driving_message = ""
let dest = null
let destPlace = null
let middle = null
let destText = ""
let direction_service = null
let costPreference = 1
let steps = []
let shortestRouteDist = ""
let distanceMessage = ["", ""]
let logo = "https://i.ibb.co/drqk6c8/logo.png";
let restaurantLogo = "fork.png"
let response_message = ""
let trip_message = ""
let route_message = ""
let summary_text = ""
let loading_message = ""
let loading_class = ""
let originCoords = []
let destCoords = []
let middleCoords = []
let stops = 0
let dist = 0
let directions = null
let directionsRenderer = null
let map = null
let markerList = []
let middlePhotoURL = ""
let tripResourceLink = ""
let resource_name = ""
let destName = ""
let destMapURL = ""
let destWebsite = ""
let destPhone = ""
let destIcon = ""
let originMessage = ""
let destMessage = ""
let contactOrigin = ""
let contactDest = ""
let viewInGoogle = ""
let destSet = 0
let intermedText = ""

function App() {

  // keeps track of the shortest route time
  const [shortestRouteTime, setShortestRouteTime] = useState("");

  // keeps track of whether used has submitted their preferences
  const [submitted, setSubmitted] = useState(0)

  // keeps track of whether route is actively being calculated
  const [loading, setLoading] = useState(0)

  // keeps track of the list of attractions received from the server
  const [attractions, setAttractions] = useState([])

  // keeps track of origin and destination messages
  const [originText, setOriginText] = useState("")
  const [destText, setDestText] = useState("")
  const [middleText, setMiddleText] = useState("")

  // keeps track of user's relative preference for restaurants
  const [restaurant, setRestaurant] = useState(50);

  // keeps track of user's relative preference for museums
  const [museum, setMuseum] = useState(50);

  // keeps track of user's relative preference for parks
  const [park, setPark] = useState(50);

  // keeps track of user's relative preference for shops
  const [shop, setShop] = useState(50);

  // keeps track of whether there was an error in the route from the server
  const [error, setError] = useState(0)

  // update user's restaurant preference
  const handleChangeRestaurant = (event, newValue) => {
    setRestaurant(newValue);
  };

  // update user's museum preference
  const handleChangeMuseum = (event, newValue) => {
    setMuseum(newValue);
  };

  // update user's park preference
  const handleChangePark = (event, newValue) => {
    setPark(newValue);
  };

  // update user's shopping preference
  const handleChangeShop = (event, newValue) => {
    setShop(newValue);
  };


  // set the place of the
  function setOrigin(newOrigin){
    // place return by Google Autocomplete search
    origin = newOrigin

    let service = new google.maps.places.PlacesService(document.getElementById('map'));

    // get the Google Place associated w/ the new origin
    service.getDetails({
        placeId: origin.value.place_id
      }, (result, status) => {
        if (status === google.maps.places.PlacesServiceStatus.OK) {
          originPlace = result
        }
      })

      // update the time/distance display for shortest route,
      // but only if the destination has already been set
      if (destSet == 1){
        getRouteInfo();
      }

    }


  // sets an intermediate stop indicated by the user
  function setMiddle(newMiddle){
    middle = newMiddle
    let service = new google.maps.places.PlacesService(document.getElementById('map'));

    service.getDetails({
        placeId: middle.value.place_id
      }, (result, status) => {
        if (status === google.maps.places.PlacesServiceStatus.OK) {

          middleCoords = [result.geometry.location.lat(), result.geometry.location.lng()]
          middlePhotoURL = result.photos[0].getUrl()
        }
      })
    }

  // sets the user's budget preference to $ (lowest price)
  function setDollar1(){
    costPreference = 1
  }

  // sets the user's budget preference to $$ (second-lowest)
  function setDollar2(){
    costPreference = 2
  }

  // sets the user's budget preference to $$$ (second-highest)
  function setDollar3(){
    costPreference = 3
  }

  // sets the user's budget preference to $$$$ (highest price)
  function setDollar4(){
    costPreference = 4
  }

  // sets the number of preferred stops (user preference)
  function setStops(value){
    stops = value
    console.log(stops)
  }

  // sets the maximum desired trip distance (user preference)
  function setDist(value){
    dist = value
  }

  // sets the Google Place associated with user-inputted destination
  function setDest(newDest){
    dest = newDest
    console.log(dest.value.place_id)
    console.log(dest.value)

    // so we can get more information about the destination (website, phone #, etc.)
    // for the destination later on (in trip summary)
    let service = new google.maps.places.PlacesService(document.getElementById('map'));

    service.getDetails({
        placeId: dest.value.place_id
      }, (result, status) => {
        if (status === google.maps.places.PlacesServiceStatus.OK) {
          destPlace = result
          console.log(originPlace)
        }
      })

      destSet = 1

    getRouteInfo();
  }

    // initializes the Google Map that will be displayed with the route
    function initMap(){
      let initialView = new google.maps.LatLng(41.850033, -87.6500523);
      var mapOptions = {
        zoom:7,
        center: initialView
      }
      map = new google.maps.Map(document.getElementById('map'), mapOptions);
      directionsRenderer.setMap(map);
      directionsRenderer.setPanel(document.getElementById('directionsPanel'));
    }

  // gets preliminary trip data (time, distance) for the inputted origin and
  // destination, using the Google Directions API
  function getRouteInfo(){

    directions = new google.maps.DirectionsService()
    directionsRenderer = new google.maps.DirectionsRenderer()


    directions.route({
        origin: origin.value.description,
        destination: dest.value.description,
        travelMode: google.maps.TravelMode.DRIVING,
      }, (result, status) => {
        if (status === google.maps.DirectionsStatus.OK) {

      directionsRenderer.setDirections(result);

      shortestRouteDist = result.routes[0].legs[0].distance.text
      setShortestRouteTime(result.routes[0].legs[0].duration.text)

      let directionsData = result.routes[0].legs[0];

      steps = [];
      let tripLength = directionsData.steps.length

      // get the legs of the Google Maps-recommended shortest route
      // between the origin and destination (used in backend to determine stops)
      for (let i = 0; i < tripLength; i++) {
        let currStep = directionsData.steps[i]
        let startLat = currStep.start_location.lat()
        let startLon = currStep.start_location.lng()
        let endLat = currStep.end_location.lat()
        let endLon = currStep.end_location.lng()
        steps.push([startLat, startLon, endLat, endLon])
       }

       originCoords = [steps[0][0], steps[0][1]]
       destCoords = [steps[tripLength-1][0], steps[tripLength-1][1]]

        } else {
          console.error(`error fetching directions ${result}`);
        }
      });

  }


  // use Google API to display the route along the stops selected by the server (ways)
  function getRouteWithStops(ways){

    directions = new google.maps.DirectionsService()
    // don't show markers for each stop now, add flags and start/stop markers later
    directionsRenderer = new google.maps.DirectionsRenderer({suppressMarkers: true})

    let waypts = [];

    for (let i = 0; i < ways.length; i++) {
        waypts.push({
          location: new google.maps.LatLng(ways[i].coordinates[0], ways[i].coordinates[1]),
          stopover: true,
        });
      }

    // use API to get and display the route through the stops
    directions.route({
        origin: origin.value.description,
        destination: dest.value.description,
        waypoints: waypts,
        travelMode: google.maps.TravelMode.DRIVING,
      }, (result, status) => {
        if (status === google.maps.DirectionsStatus.OK) {

          directionsRenderer.setDirections(result);
        }
      })
    }

    // make axios post request to the backend-- get stop information with possible error message
    const requestTrip = () => {
      error_message = ""
      resetDisplay()
      setSubmitted(0)

      // do preliminary error-checking on front-end user inputs
      // no origin input
      if(origin == null){
        error_message = "Please enter a starting location"
        loading_class = ""
        setError(1);
      // no destination input
      } else if (dest == null){
        error_message = "Please enter a destination"
        loading_class = ""
        setError(1);
      // no max distance entered
      } else if (dist == 0){
        error_message = "Please enter a maximum distance"
        loading_class = ""
        setError(1);
      }
      // no errors, proceed with regular display
      else {
      loading_message = "Calculating the perfect trip"
      loading_class = "loading"
      setLoading(1)

      // user preferences and locations to send to backend
      const toSend = {
        costPref: costPreference,
        route: steps,
        restValue: restaurant,
        musValue: museum,
        parkValue: park,
        shopValue: shop,
        stopPref: stops,
        originLat: originCoords[0],
        originLon:originCoords[1],
        destLat: destCoords[0],
        destLon: destCoords[1],
        maxDist: dist,
        middleLat: middleCoords[0],
        middleLon: middleCoords[1]
      }

      let config = {
        headers: {
          "Content-Type": "application/json",
          'Access-Control-Allow-Origin': '*',
        }
      }

      axios.post(
          "http://localhost:4567/route",
          toSend,
          config
        )
        .then(response => {
          console.log(response.data);
          // remove message that we're still calculating the route
          loading_message = ""

          // received an error message from the backend, display to user
          if (response.data["error_message"] != "") {
            error_message = response.data["error_message"] + ": please adjust your preferences"
            loading_class = ""
            setSubmitted(1);

            // server-generated route was empty, display message to user
          } else if (response.data["route"].length == 0){
            error_message = "Oops! This route returned no results. Please adjust your preferences "
            loading_class = ""
            setSubmitted(1);
          }
            else {

           // add the start and the end to the list of attractions (for display purposes)
           let originNode = {
             id: "",
             name: originText,
             location:  "",
             coordinates: [originCoords[0], originCoords[1]]
           }

           let destNode = {
             id: "",
             name: destText,
             location:  "",
             coordinates: [destCoords[0], destCoords[1]]
           }


          markerList = []
          let newAttractions = response.data["route"]
          // sets the map with attraction nodes as waypoints
          getRouteWithStops(newAttractions)
          // add start/end points to the attraction list
          newAttractions.unshift(originNode)
          newAttractions.push(destNode)

          // initialize the route map so we can add stop markers
          initMap()

          setAttractions(newAttractions)
          for (let i = 1; i < newAttractions.length - 1; i++){

              let marker = new google.maps.Marker({
                position: {lat: newAttractions[i].coordinates[0], lng:newAttractions[i].coordinates[1] },
                map: map,
                icon: 'https://developers.google.com/maps/documentation/javascript/examples/full/images/beachflag.png'
              })

          // check if it's the intermediate node and handle separately
          let infoWindow = null
          if (newAttractions[i].name == "Intermediate Stop") {
              infoWindow = new google.maps.InfoWindow({
                  content: '<div> <h3>' + middle.value.structured_formatting.main_text + '</h3>' + middle.value.structured_formatting.secondary_text + '</div>'
                })

          } else{
            infoWindow = new google.maps.InfoWindow({
              content: '<div> <h3>' + newAttractions[i].name + '</h3>' + newAttractions[i].location[1] + ", " + newAttractions[i].location[2] + '</div>'
            })
          }

          marker.addListener('click', function(){
            infoWindow.open(map, marker)
          })
          markerList.push(marker)

          }

          let originMarker = new google.maps.Marker({
            position: {lat: originCoords[0], lng:originCoords[1] },
            map: map,
            label: "A"
          })

          console.log(originMarker)
          console.log(originCoords)
          console.log(destCoords)

          // change name
          let infoWindowOrigin = new google.maps.InfoWindow({
            content: '<div> <h3>' + origin.value.structured_formatting.main_text + '</h3>' + origin.value.structured_formatting.secondary_text + '</div>'
          })

          console.log(infoWindowOrigin)

          originMarker.addListener('click', function(){
            infoWindowOrigin.open(map, originMarker)
          })

          let destMarker = new google.maps.Marker({
            position: {lat: destCoords[0], lng:destCoords[1] },
            map: map,
            label: "B"
          })

          // change name
          let infoWindowDest = new google.maps.InfoWindow({
            content: '<div> <h3>' + dest.value.structured_formatting.main_text + '</h3>' + dest.value.structured_formatting.secondary_text + '</div>'
          })

          destMarker.addListener('click', function(){
            infoWindowDest.open(map, destMarker)
          })

          console.log(response.data["route"])
          response_message = "Trip Itinerary"
          trip_message = "Trip Details"
          route_message = "Route Map"
          intermedText =  "(You added this stop!)"

          setOriginText(origin.value.structured_formatting)
          setDestText(dest.value.structured_formatting)
          setMiddleText(middle.value.structured_formatting)

          tripResourceLink = "https://www.tripsavvy.com/planning-a-road-trip-complete-guide-4845956"
          resource_name = "Planning a Road Trip: The Complete Guide"
          originName = originPlace.name
          originMapURL = originPlace.url
          originWebsite = originPlace.website
          originPhone = originPlace.formatted_phone_number
          originIcon = originPlace.icon

          destName = destPlace.name
          destMapURL = destPlace.url
          destWebsite = destPlace.website
          destPhone = destPlace.formatted_phone_number
          destIcon = destPlace.icon
          originMessage = "Origin"
          destMessage = "Destination"
          driving_message = "(Click on the road icons for directions!)"
          if(destPhone === undefined){
            contactDest = "Contact: None Available"
          }
          if (originPhone === undefined){
            contactOrigin = "Contact: None Available"
          }
          viewInGoogle = "View in Google Maps"
          loading_class = ""
          // now that we're done setting information for the results page,
          // re-render the page
          setSubmitted(1);

        }
        })

        .catch(function(error) {
          console.log(error);
        });

    }
  }

    function setDisplay(){
      tripResourceLink = "https://www.tripsavvy.com/planning-a-road-trip-complete-guide-4845956"
      originName = originPlace.name
      originMapURL = originPlace.url
      originWebsite = originPlace.website
      originPhone = originPlace.formatted_phone_number
      originIcon = originPlace.icon

      destName = destPlace.name
      destMapURL = destPlace.url
      destWebsite = destPlace.website
      destPhone = destPlace.formatted_phone_number
      destIcon = destPlace.icon

    }

    function resetDisplay(){
      tripResourceLink = ""
      resource_name = ""
      originName = ""
      originMapURL = ""
      originWebsite = ""
      originPhone = ""
      originIcon = ""

      destName = ""
      destMapURL = ""
      destWebsite = ""
      destPhone = ""
      destIcon = ""
      originMessage = ""
      destMessage = ""
      contactDest = ""
      contactOrigin = ""

    }


    useEffect(()=> {
      distanceMessage[0] = "The quickest route for this origin and destination is "
      distanceMessage[1] = "and"
    }, [shortestRouteTime])

    useEffect(()=> {

    }, [submitted])

    useEffect(()=> {

    }, [attractions])

    useEffect(()=> {
      console.log("adding origin")
      console.log(originText)

    }, [originText])

    useEffect(()=> {
      setError(0)
    }, [error])


  return (
  <div className = "App">
    <Container >
    <div >
    <Row >
    <Col>
    </Col>
    <Col sm = {8}>

    &nbsp;
      <div ><img src={logo} alt={"RhodeTrip logo"} style={{width: '500px'}}></img></div>

    &nbsp;

    <p class = "question">Where do you want to start?</p>
    <GooglePlacesAutocomplete id = "origin" apiKey="AIzaSyAbX-U5h4aaNk2TTyrhYfFBG5a1C3zGU-c"
    selectProps={{
          origin,
          onChange: setOrigin,
        }}
      style = {{width: '66%'}}/>



    &nbsp;
    <p class = "question">Where do you want to go?</p>
    <GooglePlacesAutocomplete id = "destination" apiKey="AIzaSyAbX-U5h4aaNk2TTyrhYfFBG5a1C3zGU-c"
    selectProps={{
          dest,
          onChange: setDest,
        }}/>

      <br></br>


      <p>{distanceMessage[0]} {shortestRouteDist} {distanceMessage[1]} {shortestRouteTime}</p>


      Based on this, how much long would you like to spend on the road?
      &nbsp;
      <br></br>
      <TextBox label = {"Maximum distance (miles): "} change = {setDist} />


      &nbsp;
      <p class = "question">What's your budget like?</p>

        <div class="item1">
          <input type="radio" value="$" name="budget" onChange={setDollar1} checked/> $
          <input type="radio" value="$$" name="budget" onChange={setDollar2}/> $$
          <input type="radio" value="$$$" name="budget" onChange={setDollar3}/> $$$
          <input type="radio" value="$$$$" name="budget" onChange={setDollar4}/> $$$$
        </div>

      <br></br>
      <br></br>

      <TextBox label = {"Preferred # of stops: "}
      change = {setStops} class = "question"/>

      <br></br>
      <p class = "question"> Any stops you need to make on the way? </p>
      <GooglePlacesAutocomplete id = "destination" apiKey="AIzaSyAbX-U5h4aaNk2TTyrhYfFBG5a1C3zGU-c"
      selectProps={{
            middle,
            onChange: setMiddle,
          }}/>

      <br></br>

      <p class = "question"> How much do you prefer the following types of stops? </p>

      <div class = "here">
            <Typography id="continuous-slider" gutterBottom>
              Restaurants
            </Typography>
            <Grid container spacing={1}>
              <Grid item>
                <ThumbDownAltIcon />
              </Grid>
              <Grid item xs>
                <Slider value={restaurant} onChange={handleChangeRestaurant} aria-labelledby="continuous-slider" />
              </Grid>
              <Grid item>
                <ThumbUpAltIcon />
              </Grid>
            </Grid>

            <Typography id="continuous-slider" gutterBottom>
              Museums
            </Typography>
            <Grid container spacing={1}>
              <Grid item>
                <ThumbDownAltIcon />
              </Grid>
              <Grid item xs>
                <Slider value={museum} onChange={handleChangeMuseum} aria-labelledby="continuous-slider" />
              </Grid>
              <Grid item>
                <ThumbUpAltIcon />
              </Grid>
            </Grid>

            <Typography id="continuous-slider" gutterBottom >
              Parks
            </Typography>
            <Grid container spacing={1}>
              <Grid item>
                <ThumbDownAltIcon />
              </Grid>
              <Grid item xs>
                <Slider value={park} onChange={handleChangePark} aria-labelledby="continuous-slider" />
              </Grid>
              <Grid item>
                <ThumbUpAltIcon />
              </Grid>
            </Grid>

            <Typography id="continuous-slider" gutterBottom>
              Shops
            </Typography>
            <Grid container spacing={1}>
              <Grid item>
                <ThumbDownAltIcon />
              </Grid>
              <Grid item xs>
                <Slider value={shop} onChange={handleChangeShop} aria-labelledby="continuous-slider" />
              </Grid>
              <Grid item>
                <ThumbUpAltIcon />
              </Grid>
            </Grid>

        </div>


        <br></br>
        <AwesomeButton type = "secondary" onPress = {requestTrip} > Get my trip! </AwesomeButton>
        <div><br></br>
        <div> <h1 class = {loading_class}>{loading_message}</h1></div>
        <h2>{error_message}</h2>
        </div>
        </Col>
        <Col></Col>

        </Row>
        </div>

        <Row>
        <Col>
        <h1>{response_message}</h1>
        &nbsp;
        {driving_message}
        <br></br>

          {attractions.map(function (x,i, elements){

          //displaying the start
          if (i == 0){
            return (<div><img class = "left" src={start} alt={"start icon"} style={{width: '100px'}}/>
          <h2 class = "text" class = "pad"> {originText.main_text} </h2> <h4> {originText.secondary_text}</h4>
          &nbsp;
          <a href={"https://www.google.com/maps/dir/" +
          x.coordinates[0]+ "," + x.coordinates[1] + "/" + elements[i+1].coordinates[0] + "," + elements[i+1].coordinates[1]} target="_blank">
          <img class = "center" src={road} alt={"road icon"} style={{width: '100px'}}/></a>

        </div>)
          }
          //displaying the destination
          else if (i == elements.length - 1){
            return (<div><br></br><img class = "left" src={finish} alt={"finish icon"} style={{width: '100px'}}/>
            <h2 class = "text"> {destText.main_text} </h2> <h4> {destText.secondary_text}</h4>
            </div>)
          }
          else if (elements[i].name == "Intermediate Stop"){
            return (<div><div class = "rectangle"><br></br><img class = "left" src={flags} alt={"flag icon"} style={{width: '100px'}}/>
            <br></br>
          <p class = "pad"><h2> {middleText.main_text}</h2>
          <p class = "description"> {middleText.secondary_text}
          <br></br> {intermedText}
          </p></p>
          <br></br>
        </div>
        <a href={"https://www.google.com/maps/dir/" +
        x.coordinates[0]+ "," + x.coordinates[1] + "/" + elements[i+1].coordinates[0] + "," + elements[i+1].coordinates[1]} target="_blank">
        <img class = "center" src={road} alt={"road icon"} style={{width: '100px'}} /></a>
        </div>)
          }
          else if (x.nodeType == "restaurant"){
            return (<div><div class = "rectangle"><img class = "left" src={fork} alt={"restaurant"} style={{width: '100px'}}/>
            <br></br>
          <p class = "pad"><h2> {x.name}</h2>
          <p class = "description"> {x.location[1]}, {x.location[2]}
          <br></br> {x.rating} stars
          <br></br>
          <a href={"https://www.yelp.com/biz/" + x.id} target="_blank">Learn more</a></p></p>
        </div>
        <a href={"https://www.google.com/maps/dir/" +
        x.coordinates[0]+ "," + x.coordinates[1] + "/" + elements[i+1].coordinates[0] + "," + elements[i+1].coordinates[1]} target="_blank">
        <img class = "center" src={road} alt={"road icon"} style={{width: '100px'}} /></a>
        </div>)
          } else if (x.nodeType == "museum"){
            return (<div><div class = "rectangle"><img class = "left" src={museum1} alt={"museum icon"} style={{width: '100px'}}/>
            <br></br>
          <p class = "pad"><h2> {x.name}</h2>
          <p class = "description"> {x.location[1]}, {x.location[2]}
          <br></br> {x.rating} stars
          <br></br>
          <a href={"https://www.yelp.com/biz/" + x.id} target="_blank">Learn more</a></p></p>
        </div>
        <a href={"https://www.google.com/maps/dir/" +
        x.coordinates[0]+ "," + x.coordinates[1] + "/" + elements[i+1].coordinates[0] + "," + elements[i+1].coordinates[1]} target="_blank">
        <img class = "center" src={road} alt={"road icon"} style={{width: '100px'}} /></a>
        </div>)
          } else if(x.nodeType == "shop"){
            return (<div><div class = "rectangle"><img class = "left" src={store} alt={"shop"} style={{width: '100px'}}/>
            <br></br>
          <p class = "pad"><h2> {x.name}</h2>
          <p class = "description"> {x.location[1]}, {x.location[2]}
          <br></br> {x.rating} stars
          <br></br>
          <a href={"https://www.yelp.com/biz/" + x.id} target="_blank">Learn more</a></p></p>
        </div>
        <a href={"https://www.google.com/maps/dir/" +
        x.coordinates[0]+ "," + x.coordinates[1] + "/" + elements[i+1].coordinates[0] + "," + elements[i+1].coordinates[1]} target="_blank">
        <img class = "center" src={road} alt={"road icon"} style={{width: '100px'}} /></a>
        </div>)
          } else {
            return (<div><div class = "rectangle"><img class = "left" src={park2} alt={"park"} style={{width: '100px'}}/>
            <br></br>
          <p class = "pad"><h2> {x.name}</h2>
          <div class = "description"> {x.location[1]}, {x.location[2]}
          <br></br> {x.rating} stars
          <br></br>
          <a href={"https://www.yelp.com/biz/" + x.id} target="_blank">Learn more</a></div></p>
        </div>
        <a href={"https://www.google.com/maps/dir/" +
        x.coordinates[0]+ "," + x.coordinates[1] + "/" + elements[i+1].coordinates[0] + "," + elements[i+1].coordinates[1]} target="_blank">
        <img class = "center" src={road} alt={"road icon"} style={{width: '100px'}} /></a>
        </div>)
          }})}


        </Col>

        <Col>

        <h1>{route_message}</h1>
        <div id="map" class = "rounded" style={{float: "center", width: 600, height: 400}}></div>
        <h1>{trip_message}</h1>
        <p>
        {summary_text}
        <a href={tripResourceLink} target="_blank">{resource_name}</a>
        <br></br>
        <h3>{originMessage}</h3>
        <img src = {originIcon} style={{width: '100px'}}/>
        <p>
        <a href={originWebsite} target="_blank">{originName}</a>
        <br></br>
        {contactOrigin}{originPhone}
        <br></br>
        <a href={originMapURL} target="_blank">{viewInGoogle}</a>
        </p>


        <h3>{destMessage}</h3>
        <img src = {destIcon} style={{width: '100px'}}/>
        <p>
        <a href={destWebsite} target="_blank">{destName}</a>
        <br></br>
        {contactDest}{destPhone}
        <br></br>
        <a href={destMapURL} target="_blank">{viewInGoogle}</a>
        </p>

        </p>

        </Col>
        </Row>
        </Container>
      </div>


  );

}

export default App;
