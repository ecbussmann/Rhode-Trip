import './App.css';
import './index.js'
import GooglePlacesAutocomplete from 'react-google-places-autocomplete';
import { DirectionsRenderer, DirectionsService, TravelMode, DirectionsStatus } from "react-google-maps";
import {useState, useEffect} from 'react'
import { AwesomeButton } from "react-awesome-button";
import TextBox from './TextBox.js';
import "react-awesome-button/dist/themes/theme-eric.css";
import axios from 'axios';

import { makeStyles } from '@material-ui/core/styles';
import Grid from '@material-ui/core/Grid';
import Typography from '@material-ui/core/Typography';
import Slider from '@material-ui/core/Slider';
//import { Link } from 'react-router-dom';

/*global google*/

let origin = null
let dest = null
let direction_service = null
let costPreference = 1
let steps = []
let shortestRouteDist = ""
//let shortestRouteTime = null
let distanceMessage = ["", ""]
let logo = "https://i.ibb.co/drqk6c8/logo.png";
let response_message = ""


//const google = window.google;

function App() {

  const [shortestRouteTime, setShortestRouteTime] = useState("");
  const [submitted, setSubmitted] = useState(0)
  const [attractions, setAttractions] = useState([])

  const [restaurant, setRestaurant] = useState(30);
  const [museum, setMuseum] = useState(30);
  const [park, setPark] = useState(30);
  const [shop, setShop] = useState(30);

  const handleChangeRestaurant = (event, newValue) => {
    setRestaurant(newValue);
  };

  const handleChangeMuseum = (event, newValue) => {
    setMuseum(newValue);
  };

  const handleChangePark = (event, newValue) => {
    setPark(newValue);
  };

  const handleChangeShop = (event, newValue) => {
    setShop(newValue);
  };

  const useStyles = makeStyles({
    root: {
      width: 100,
    },
  });

  const classes = useStyles();


  //const [value, setValue] = useState(null);

  //<script async defer src="https://maps.googleapis.com/maps/api/js?key=AIzaSyAbX-U5h4aaNk2TTyrhYfFBG5a1C3zGU-c"></script>

  const script = document.createElement("script");
  script.async = true;
  script.defer = true;
  script.src = "https://maps.googleapis.com/maps/api/js?key=AIzaSyAbX-U5h4aaNk2TTyrhYfFBG5a1C3zGU-c";

  function setOrigin(newOrigin){
    origin = newOrigin
    //direction_service = new DirectionsService
    //direction_service.origin = origin
    console.log(origin.value.place_id)

  }

  function setDollar1(){
    costPreference = 1
  }

  function setDollar2(){
    costPreference = 2
  }

  function setDollar3(){
    costPreference = 3
  }

  function setStops(){

  }

  function setDest(newDest){
    dest = newDest
    console.log(dest.value.place_id)
    //let directions = new google.maps.DirectionsService()
    console.log(dest.value)
    //let directionsRenderer = new google.maps.DirectionsRenderer()

    getRouteInfo();
  }
    //console.log(dest.value.formatted_address)
    //directions.origin = origin.location;
    //directions.destination = dest.location;

    function getRouteInfo(){

    let directions = new google.maps.DirectionsService()
    let directionsRenderer = new google.maps.DirectionsRenderer()


    directions.route({
        origin: origin.value.description,
        destination: dest.value.description,
        travelMode: google.maps.TravelMode.DRIVING,
      }, (result, status) => {
        if (status === google.maps.DirectionsStatus.OK) {
          console.log(result)

      directionsRenderer.setDirections(result);

      shortestRouteDist = result.routes[0].legs[0].distance.text
      setShortestRouteTime(result.routes[0].legs[0].duration.text)


      let directionsData = result.routes[0].legs[0];

      steps = [];

      for (var i = 0; i < directionsData.steps.length; i++) {
        let currStep = directionsData.steps[i]
        let startLat = currStep.start_location.lat()
        let startLon = currStep.start_location.lng()
        let endLat = currStep.end_location.lat()
        let endLon = currStep.end_location.lng()
        steps.push([startLat, startLon, endLat, endLon])

       }
       console.log(steps)
        } else {
          console.error(`error fetching directions ${result}`);
        }
      });

  }

    // make axios post request to the backend
    const requestTrip = () => {

      console.log(costPreference)
      console.log(steps)
      console.log(restaurant)
      console.log(museum)
      console.log(park)

      // the source and destination of our desired route
      const toSend = {
        costPref: costPreference,
        route: steps,
        restValue: restaurant,
        musValue: museum,
        parkValue: park
      };

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
          //console.log(response.data["stops"])
          // stops is a map of [business id, AttractionNode object]
          //const stops = response.data["route"]
          let stop1 = {
            id: "tnhfDv5Il8EaGSXZGiuQGg",
            name: "Garaje",
            location: ["475 3rd St", "San Francisco", "CA", "94107"],
            coordinates: [37.7817529521, -122.39612197],
            price: 1.0,
            rating: 4.5
          }

          let stop2 = {
            id: "tnhfDv5Il8EaGSXZGiuQGh",
            name: "Garaje",
            location: ["475 3rd St", "San Francisco", "CA", "94107"],
            coordinates: [37.7817529521, -122.39612197],
            price: 1.0,
            rating: 4.5
          }

          setAttractions([stop1, stop2])


          /*console.log(stops)
          for (const id in stops) {
            console.log(stops[id].id)
            console.log(stops[id].name)
            console.log(stops[id].location)
            console.log(stops[id].price)
            console.log(stops[id].rating)
          }*/

          setSubmitted(1);

        })

        .catch(function(error) {
          console.log(error);

        });

    }


    const requestTrip2 = () => {

      let stop1 = {
        id: "tnhfDv5Il8EaGSXZGiuQGg",
        name: "Garaje",
        location: ["475 3rd St", "San Francisco", "CA", "94107"],
        coordinates: [37.7817529521, -122.39612197],
        price: 1.0,
        rating: 4.5
      }

      let stop2 = {
        id: "tnhfDv5Il8EaGSXZGiuQGh",
        name: "Plant City",
        location: ["475 3rd St", "Providence", "RI", "94107"],
        coordinates: [41.8207975,-71.406356],
        price: 1.0,
        rating: 4.5
      }

      setAttractions([stop1, stop2])

    }

    useEffect(()=> {
      console.log("in use effect")
      console.log(shortestRouteDist)
      console.log(shortestRouteTime)
      distanceMessage[0] = "The quickest route for this origin and destination is "
      distanceMessage[1] = "and"
    }, [shortestRouteTime])

    useEffect(()=> {
      console.log("changing message")
      response_message = "Here's your route!"
    }, [submitted])

    useEffect(()=> {
      console.log("changing message")
      console.log(attractions)
    }, [attractions])

  return (

    <div>
    &nbsp;
      <div className={"center"}><img src={logo} alt={"RhodeTrip logo"} style={{width: '300px'}}></img></div>
    <div>
    &nbsp;

    Where do you want to start?
    <GooglePlacesAutocomplete id = "origin" apiKey="AIzaSyAbX-U5h4aaNk2TTyrhYfFBG5a1C3zGU-c"
    selectProps={{
          origin,
          onChange: setOrigin,
        }}
      style = {{width: '66%'}}/>

    <br></br>
    <br></br>
    &nbsp;
    Where do you want to go?
    <GooglePlacesAutocomplete id = "destination" apiKey="AIzaSyAbX-U5h4aaNk2TTyrhYfFBG5a1C3zGU-c"
    selectProps={{
          dest,
          onChange: setDest,
        }}/>

      <br></br>

      <div>
      <p>{distanceMessage[0]} {shortestRouteDist} {distanceMessage[1]} {shortestRouteTime}</p>
      </div>
      <br></br>
      Based on this, how much long would you like to spend on the road?
      &nbsp;
      <br></br>
      <TextBox label = {"Maximum time (hours): "} change = {setStops} />
      <TextBox label = {"Maximum distance (miles): "} change = {setStops} />

      <br></br>
      &nbsp;
      What's your budget like?
      <br></br>

      {/*&nbsp;*/}
      {/*<AwesomeButton type = "primary" onPress = {setDollar1} > $ < /AwesomeButton>*/}
      {/*&nbsp;*/}
      {/*<AwesomeButton type = "primary" onPress = {setDollar2} > $$ < /AwesomeButton>*/}
      {/*&nbsp;*/}
      {/*<AwesomeButton type = "primary" onPress = {setDollar3} > $$$ < /AwesomeButton>*/}
        <div class="item1">
          <input type="radio" value="$" name="budget" onChange={setDollar1} checked/> $
          <input type="radio" value="$$" name="budget" onChange={setDollar2}/> $$
          <input type="radio" value="$$$" name="budget" onChange={setDollar3}/> $$$
        </div>

      <br></br>
      <br></br>

      <TextBox label = {"Maximum # of stops: "}
      change = {setStops} />

      <br></br>
      Any stops you need to make on the way?
      <GooglePlacesAutocomplete id = "destination" apiKey="AIzaSyAbX-U5h4aaNk2TTyrhYfFBG5a1C3zGU-c"
      selectProps={{
            dest,
            onChange: setDest,
          }}/>

      <br></br>

      How much do you prefer the following types of stops?

          <div className={classes.root}>
            <Typography id="continuous-slider" gutterBottom>
              Restaurants
            </Typography>
            <Grid container spacing={2}>
              <Grid item xs>
                <Slider value={restaurant} onChange={handleChangeRestaurant} aria-labelledby="continuous-slider" />
              </Grid>
            </Grid>

            <Typography id="continuous-slider" gutterBottom>
              Museums
            </Typography>
            <Grid container spacing={2}>
              <Grid item xs>
                <Slider value={museum} onChange={handleChangeMuseum} aria-labelledby="continuous-slider" />
              </Grid>
            </Grid>

            <Typography id="continuous-slider" gutterBottom>
              Parks
            </Typography>
            <Grid container spacing={2}>
              <Grid item xs>
                <Slider value={park} onChange={handleChangePark} aria-labelledby="continuous-slider" />
              </Grid>
            </Grid>

            <Typography id="continuous-slider" gutterBottom>
              Shops
            </Typography>
            <Grid container spacing={2}>
              <Grid item xs>
                <Slider value={shop} onChange={handleChangeShop} aria-labelledby="continuous-slider" />
              </Grid>
            </Grid>

        </div>
        <AwesomeButton type = "secondary" onPress = {requestTrip2} > Get my trip! < /AwesomeButton>
        <div><p>{response_message}</p></div>

        Start:
        <div>
        <ul>
          {attractions.map((x,i, elements) => (<li> <a href={"https://www.yelp.com/biz/" + x.id} target="_blank">{x.name}</a>
          <br></br> stars: {x.rating}
          <br></br> location: {x.location[1]}, {x.location[2]}
          <br></br> directions to next stop: <a href={"https://www.google.com/maps/dir/" +
          x.coordinates[0]+ "," + x.coordinates[1] + "/"
           } target="_blank">directions {i}</a></li>))}
       </ul>
          </div>
        End:


    </div>
    </div>


  );

}

export default App;
